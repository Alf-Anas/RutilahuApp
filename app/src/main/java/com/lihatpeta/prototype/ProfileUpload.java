package com.lihatpeta.prototype;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ProfileUpload {
    private static ProfileUpload mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    private ProfileUpload(Context context)
    {
        mCtx=context;
        requestQueue=getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if(requestQueue==null)
            requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        return requestQueue;
    }

    public static synchronized ProfileUpload getInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance=new ProfileUpload(context);
        }
        return mInstance;
    }

    public<T> void addToRequestQueue(Request<T> request)
    {
        getRequestQueue().add(request);
    }
}
