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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetUserTasksActivity extends AppCompatActivity {

    private SessionManager session;
    public ArrayList<String> tasks;
    public ArrayList<Integer> tasksIds;

  //  private ProgressDialog pDialog;

    private static final String TAG = GetUserTasksActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String username;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_user_tasks);
        // Session manager
        session = new SessionManager(getApplicationContext());


           tasks = new ArrayList<String>();
           tasksIds = new ArrayList<Integer>();
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();

            // name
            String usertype = user.get(SessionManager.KEY_USERTYPE);
            // User is already logged in. Take him to main activity
            if(usertype.equals("employer"))
            {
                username = user.get("username");
                String tag_string_req = "req_getTasks";

              //  pDialog.setMessage("Finding Tasks ...");
               // showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_GET_TASKS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "GetTasks Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("error");

                            // Check for error node in json
                            if (error != 1) {
                                // user successfully logged in
                                // Create login session
                               // String tasks = jObj.getString("tasks");

                                //jObj = new JSONObject(response);
                                JSONArray jAbilities = jObj.getJSONArray("tasks");
                                for(int i=0;i<jAbilities.length();i++)
                                {
                                    String ab =(jAbilities.getJSONObject(i).getString("name"));
                                    Integer ii = (jAbilities.getJSONObject(i).getInt("id"));
                                    tasks.add(ab);
                                    tasksIds.add(ii);
                                }


                             //   String surname = jObj.getString("surname");


                                // Launch main activity

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
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        //hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "login");
                        params.put("username", username);



                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_user_tasks, menu);
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



}
