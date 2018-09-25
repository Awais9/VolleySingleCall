package com.awais.volleysinglecall;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class VolleyQueue {

    @SuppressLint("StaticFieldLeak")
    private static VolleyQueue volleyQueue;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private HashMap<String, String> headers = new HashMap<>();

    public static VolleyQueue getInstance(Context ctx) {
        if (volleyQueue == null) {
            volleyQueue = new VolleyQueue();
        }
        context = ctx;
        return volleyQueue;
    }

    private RequestQueue mRequestQueue;

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }


}
