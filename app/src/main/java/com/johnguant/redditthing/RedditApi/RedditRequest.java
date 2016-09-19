package com.johnguant.redditthing.RedditApi;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.johnguant.redditthing.Auth.RedditAuthManager;
import com.johnguant.redditthing.VolleyQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RedditRequest extends Request<JSONObject>{
    private Map<String, String> headers;
    private Map<String, String> mParams;
    private final Response.Listener<JSONObject> mListener;
    Context ctx;
    boolean authError = false;

    public RedditRequest(int method, String url, Map<String, String> params,
                         Response.Listener<JSONObject> listener,
                         Response.ErrorListener errorListener){
        super(method, url, errorListener);
        mListener = listener;
        mParams = params;
        headers = new HashMap<>();
        headers.put("User-Agent", "android:com.johnguant.redditthing:v0.0.1 (by /u/john_guant)");
    }

    public RedditRequest(int method, String url,
                         Response.Listener<JSONObject> listener,
                         Response.ErrorListener errorListener, Context context) {
        this(method, url, null, listener, errorListener);
        ctx = context;
        headers.put("Authorization", "bearer " + RedditAuthManager.getInstance(context).getAccessToken());
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public void deliverError(VolleyError error){
        if(error.networkResponse.statusCode == 401 && !authError){
            authError = true;
            new AsyncTask<RedditRequest, Void, Void>() {
                @Override
                protected Void doInBackground(RedditRequest... thisRequest) {
                    AccountManager.get(ctx).invalidateAuthToken("com.johnguant.redditthing", RedditAuthManager.getInstance(ctx).getAccessToken());
                    headers.put("Authorization", "bearer " + RedditAuthManager.getInstance(ctx).getAccessToken());
                    VolleyQueue.getInstance(ctx).addToRequestQueue(thisRequest[0]);
                    return null;
                }
            }.execute(this);

            return;
        }
        if (getErrorListener() != null) {
            getErrorListener().onErrorResponse(error);
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getParams(){
        return mParams;
    }

    public void addHeader(String k, String v){
        headers.put(k, v);
    }
}
