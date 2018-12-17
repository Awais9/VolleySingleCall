package com.awais.volleysinglecall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.company.volleysinglecall.network.VolleyResponse;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class NetworkController {

    /************* Network Controller Class Instances **************/
    private Context context;
    private VolleyQueue volleyQueue;

    public NetworkController(Context ctx) {
        context = ctx;
        volleyQueue = VolleyQueue.getInstance(context);
    }

    /***************************************************************/
    private int logoutCount = 2;
    private int currentCount = 0;
    private boolean finishActivity = false;
    private boolean shouldLogout = false;
    private boolean showDialog = true;

    public boolean isFinishActivity() {
        return finishActivity;
    }

    public void setFinishActivity(boolean finishActivity) {
        this.finishActivity = finishActivity;
    }

    public boolean isShouldLogout() {
        return shouldLogout;
    }

    public void setShouldLogout(boolean shouldLogout) {
        this.shouldLogout = shouldLogout;
    }

    public int getLogoutCount() {
        return logoutCount;
    }

    public void setLogoutCount(int logoutCount) {
        this.logoutCount = logoutCount;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    /***************************************************************/

    public <T> void callService(int requestType, String serviceName, final HashMap<String, String> hashMap,
                                final String tag, final Class<T> objectClass, final VolleyResponse calls,
                                JSONObject jsonObject, boolean isJsonReq) {
        if (isNetworkAvailable(context)) {
            if (isJsonReq) {
                JsonObjectRequest objectRequest = new JsonObjectRequest(requestType, serviceName,
                        jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        requestSuccess("", response, tag, calls, objectClass);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseFailed(error, tag, calls);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        return volleyQueue.getHeaders();
                    }
                };
                volleyQueue.addToRequestQueue(objectRequest, tag);
            } else {
                StringRequest request = new StringRequest(requestType,
                        serviceName, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestSuccess(response, null, tag, calls, objectClass);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseFailed(error, tag, calls);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        return volleyQueue.getHeaders();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        if (hashMap != null)
                            return hashMap;
                        else
                            return new HashMap<>();
                    }
                };
                volleyQueue.addToRequestQueue(request, tag);
            }
        } else {
            showSingleDialog("Please check your internet connection", true);
        }
    }

    private <T> void requestSuccess(String response, JSONObject responseObj, String tag,
                                    VolleyResponse calls, Class<T> objectClass) {
        currentCount = 0;
        try {
            JSONObject jsonObject;
            if (response.isEmpty()) {
                jsonObject = responseObj;
            } else {
                jsonObject = new JSONObject(response);
            }
            if (jsonObject.getInt("status") == 1) {
                if (objectClass.getName().equals(JSONObject.class.getName())) {
                    calls.onSuccess(jsonObject, tag);
                } else if (objectClass.getName().equals(String.class.getName())) {
                    calls.onSuccess(jsonObject.toString(), tag);
                } else {
                    Gson gson = new Gson();
                    Object typeClass = gson.fromJson(jsonObject.toString(), objectClass);
                    calls.onSuccess(typeClass, tag);
                }
            } else {
                calls.onFailure(jsonObject.toString());
                if (isShowDialog())
                    showSingleDialog(jsonObject.optString("message"), false);
            }
        } catch (Exception e) {
            if (isShowDialog())
                showSingleDialog(e.getMessage(), false);
            calls.onFailure(e.getMessage());
        }
    }

    private void responseFailed(VolleyError error, String tag, VolleyResponse calls) {
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null) {
            try {
                String res = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                // Now you can use any deserializer to make sense of data
                JSONObject obj = new JSONObject(res);
                Log.e("VolleyError", obj.toString());
            } catch (UnsupportedEncodingException e1) {
                // Couldn't properly decode data to string
                e1.printStackTrace();
            } catch (JSONException e2) {
                // returned data is not JSONObject?
                e2.printStackTrace();
            }
        }
        if (isShouldLogout()) {
            if (currentCount < getLogoutCount()) {
                currentCount++;
                if (isShowDialog())
                    showSingleDialog(error.toString(), false);
                calls.onFailure(error.toString());
            } else {
                logoutDialog("Do you want to logout?");
            }
        } else {
            if (isShowDialog())
                showSingleDialog(error.toString(), false);
            calls.onFailure(error.toString());
        }
        volleyQueue.cancelPendingRequests(tag);
    }

    public void imageRequest(final ImageResponse imageResponse, String imageURL) {
        ImageRequest imageRequest = new ImageRequest(imageURL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageResponse.onSuccess(response);
            }
        }, 0, 0, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageResponse.onFailure(error.toString());
            }
        });
        volleyQueue.addToRequestQueue(imageRequest, "imageRequest");
    }

    public <T> void uploadImage(String serviceURL, JSONObject params, final String tag, final Class<T> objectClass,
                                final VolleyResponse response) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(200 * 10000);
        HashMap<String, String> headers = volleyQueue.getHeaders();
        Iterator<String> keys = headers.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = headers.get(key);
            client.addHeader(key, value);
        }
        RequestParams requestParams = new RequestParams();
        try {
            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = params.getString(key);
                if (key.contains("image") || key.contains("picture") || key.contains("icon")) {
                    requestParams.put(key, new File(value));
                } else {
                    requestParams.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.post(serviceURL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject objResponse) {
                try {
                    requestSuccess("", objResponse, tag, response, objectClass);
                } catch (Exception e) {
                    response.onFailure(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                response.onFailure(responseString);
            }

            @Override
            public void setRequestHeaders(Header[] requestHeaders) {
                super.setRequestHeaders(requestHeaders);
            }
        });
    }

    private void showSingleDialog(String message, final boolean fromNet) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle(context.getString(R.string.app_name));
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (!fromNet) {
                        if (isFinishActivity()) {
                            Activity activity = (Activity) context;
                            activity.finish();
                        }
                    }
                }
            }).show();
        } else {
            Log.e("NetworkController", "Current context is null or expired.");
        }
    }

    private void logoutDialog(String message) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle(context.getString(R.string.app_name));
            builder.setMessage(message);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    currentCount = 0;
                    EventBus.getDefault().post(new LogoutModel());
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } else {
            Log.e("NetworkController", "Current context is null or expired.");
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
