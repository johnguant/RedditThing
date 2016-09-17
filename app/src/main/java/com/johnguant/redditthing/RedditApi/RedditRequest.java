package com.johnguant.redditthing.RedditApi;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RedditRequest extends Request<JSONObject>{
    private Map<String, String> headers;
    private Map<String, String> mParams;
    private final Response.Listener<JSONObject> mListener;

    public RedditRequest(int method, String url, Map<String, String> params,
                         Response.Listener<JSONObject> listener,
                         Response.ErrorListener errorListener){
        super(method, url, errorListener);
        mListener = listener;
        mParams = params;
        headers = new HashMap<>();
        headers.put("User-Agent", "android:com.johnguant.redditthing:v0.0.1 (by /u/john_guant)");
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
