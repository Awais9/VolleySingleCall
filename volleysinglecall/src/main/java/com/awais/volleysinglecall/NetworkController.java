package com.awais.volleysinglecall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.volleysinglecall.network.VolleyResponse;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkController {

    /************* Network Controller Class Instances **************/
    private Context context;

    public NetworkController(Context ctx) {
        context = ctx;
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
                                final String tag, final Class<T> objectClass,
                                final VolleyResponse calls) {
        final VolleyQueue volleyQueue = VolleyQueue.getInstance(context);
        StringRequest request = new StringRequest(requestType,
                serviceName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                currentCount = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (objectClass.getName().equals(JSONObject.class.getName())) {
                            calls.onSuccess(jsonObject, tag);
                        } else if (objectClass.getName().equals(String.class.getName())) {
                            calls.onSuccess(response, tag);
                        } else {
                            Gson gson = new Gson();
                            Object typeClass = gson.fromJson(response, objectClass);
                            calls.onSuccess(typeClass, tag);
                        }
                    } else {
                        calls.onFailure(response);
                        if (isShowDialog())
                            showSingleDialog(jsonObject.optString("message"));
                    }
                } catch (Exception e) {
                    if (isShowDialog())
                        showSingleDialog(e.getMessage());
                    calls.onFailure(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isShouldLogout()) {
                    if (currentCount < getLogoutCount()) {
                        currentCount++;
                        if (isShowDialog())
                            showSingleDialog(error.getMessage());
                        calls.onFailure(error.getMessage());
                    } else {
                        logoutDialog("Do you want to logout?");
                    }
                } else {
                    if (isShowDialog())
                        showSingleDialog(error.getMessage());
                    calls.onFailure(error.getMessage());
                }
                volleyQueue.cancelPendingRequests(tag);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return volleyQueue.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                return hashMap;
            }
        };
        volleyQueue.addToRequestQueue(request, tag);
    }

    private void showSingleDialog(String message) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle(context.getString(R.string.app_name));
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (isFinishActivity()) {
                        Activity activity = (Activity) context;
                        activity.finish();
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
}
