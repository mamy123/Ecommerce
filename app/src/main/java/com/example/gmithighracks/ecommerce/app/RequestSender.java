package com.example.gmithighracks.ecommerce.app;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.EmployeeHomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikos on 17-Sep-15.
 */
public class RequestSender {

    public JSONObject jObj;
    private String tag_string_req = "req_abilities";

    public JSONObject sendMessage(final Map<String, String> map, String url) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            //Log.d("DEsdUG", "Somthng request sender response: ");
            @Override
            public void onResponse(String response) {
                Log.i("DEBUG", "Request sender response: " + response.toString());


                try {
                    jObj = new JSONObject(response);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEnd", "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = map;

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        return jObj;
    }


}
