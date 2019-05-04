package com.notio.bismillahydig.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerYDIG {
    private static final String TAG = "ServerYDIG";
    private Context context;
    private String aktifitas;

    public ServerYDIG(Context context, String aktifitas){
        this.context = context;
        this.aktifitas = aktifitas;
    }

    public void sendArrayList(final List<String> list) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, alamatServer(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String pesan = jsonObject.getString("pesan");
                            if (jsonObject.optString("error").equals("true")){
                                Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "onResponse: " + pesan);
                                Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String> params = new HashMap<>();
                params.put("params", String.valueOf(list));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private String alamatServer() {
        String url = "";
        if ("LoginData".equals(aktifitas)) {
            url = alamatURL.POST_LOGIN;
        }
        return url;
    }
}
