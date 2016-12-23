package com.example.evgeniy.scanner;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class VolleyHandler {
    // Ok because we are sure this is singleton class
    @SuppressLint("StaticFieldLeak")
    private static VolleyHandler instance;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private RequestQueue requestQueue;

    private VolleyHandler(Context context) {
        VolleyHandler.context = context;
        this.requestQueue = getRequestQueue();
    }

    static synchronized VolleyHandler getInstance(Context context) {
        if (instance == null)
            instance = new VolleyHandler(context);

        return instance;
    }

    RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
