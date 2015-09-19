package com.example.gmithighracks.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.helper.SessionManager;
import com.google.android.gms.gcm.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployerTaskActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;
    private static final String TAG = EmployerTaskActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_task);
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        // name
        final  String username = user.get("username");
        Tasks task = findTask(username);
    }

    private Tasks findTask(final String  username )
    {

        String tag_string_req = "req_getTask";

        pDialog.setMessage("Getting Task ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_TASK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Task Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error");

                    // Check for error node in json
                    if (error != 1) {
                        // user successfully logged in
                        // Create login session
                        String firstName = jObj.getString("firstname");
                        String surname = jObj.getString("surname");
                        Tasks t;
                        JSONArray jTask = jObj.getJSONArray("task");
                        //Integer ii = (jAbilities.getJSONObject(i).getInt("id"));
                        for(int i=0;i<jTask.length();i++) {
                        //    t = new Tasks(jTask.getJSONObject(i).getString("name"), jTask.getJSONObject(i).getString("shortDescription"), jTask.getJSONObject(i).getString("fullDescription"), jTask.getJSONObject(i).getString("startTime"), jTask.getJSONObject(i).getString("endTime"), jTask.getJSONObject(i).getInt("salary"), jTask.getJSONObject(i).getInt("closed"), jTask.getJSONObject(i).getString("created_by"));
                        }
                       // session.setLogin(true,userType,username,firstName, surname);


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get tasks Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "tasks");
                params.put("username", username);



                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employer_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
