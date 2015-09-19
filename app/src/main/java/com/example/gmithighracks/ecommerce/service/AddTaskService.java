package com.example.gmithighracks.ecommerce.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.R;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.helper.SQLiteHelper;
import com.example.gmithighracks.ecommerce.helper.SessionManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddTaskService extends IntentService {



    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHelper db;

    public class QuickstartPreferences {

        public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
        public static final String ADD_TASK_COMPLETE = "addTaskComplete";

    }

    private static final String TAG = "AddTaskIntentService";
    private static final String[] TOPICS = {"global"};


    public AddTaskService() {
        super(TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        // Session manager
        session = new SessionManager(getApplicationContext());
//        pDialog = new ProgressDialog(getApplicationContext());
//        pDialog.setCancelable(false);
        // SQLite database handler

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String title = intent.getStringExtra("title");
        String shortDescription = intent.getStringExtra("shortDescription");
        String fullDescription = intent.getStringExtra("fullDescription");
        String startDate =  intent.getStringExtra("startDate");
        String endDate =  intent.getStringExtra("endDate");
        String salary =  intent.getStringExtra("salary");

        String emp =  intent.getStringExtra("username");

        try {

            sendNewTaskToServer(title, shortDescription, fullDescription, startDate, endDate, salary,emp);


        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
//            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent addTaskComplete = new Intent(QuickstartPreferences.ADD_TASK_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(addTaskComplete);

    }



    private void sendNewTaskToServer(final String title, final String sDescription, final String fDescription, final String sDate, final String eDate, final String salary,final String emp) {

        // Add custom implementation, as needed.

        System.out.println("Trying to send to server......");

        String tag_string_req = "req_addTask";

//        pDialog.setMessage("Registering ...");
//        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_TASK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Task Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {

                        Log.i(TAG, "mpiak edw" );
                        int id = jObj.getInt("id");

                        Intent i = new Intent("add_task");
                      //  i.putExtra("msg","OK");
                        i.putExtra("taskId", String.valueOf(id));

                        sendBroadcast(i);

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        sendBroadcast(new Intent("add_task").putExtra("msg", errorMsg));
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                    Intent new_intent = new Intent();
                    new_intent.setAction("add_task");
                    sendBroadcast(new_intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Add task Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("tag", "register");
                params.put("title",title);
                params.put("shortDescription", sDescription);
                params.put("fullDescription",fDescription);
                params.put("startTime", sDate);
                params.put("endTime", eDate);
                params.put("salary",salary);
                params.put("employers",emp);

                //   params.put("latitude",latitude.toString());
                //   params.put("longitude",longitude.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
