package com.example.gmithighracks.ecommerce.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.EmployeeHomeActivity;
import com.example.gmithighracks.ecommerce.EmployerHomeActivity;
import com.example.gmithighracks.ecommerce.R;
import com.example.gmithighracks.ecommerce.RegisterActivity;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.helper.SQLiteHelper;
import com.example.gmithighracks.ecommerce.helper.SessionManager;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationIntentService extends IntentService {

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHelper db;

    public class QuickstartPreferences {

        public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
        public static final String REGISTRATION_COMPLETE = "registrationComplete";

    }

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Session manager
        session = new SessionManager(getApplicationContext());
//        pDialog = new ProgressDialog(getApplicationContext());
//        pDialog.setCancelable(false);
        // SQLite database handler
        db = new SQLiteHelper(getApplicationContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String email = intent.getStringExtra("email");
        String firstName =  intent.getStringExtra("fname");
        String surname =  intent.getStringExtra("surname");
        String area =  intent.getStringExtra("area");
        String city = intent.getStringExtra("city");
        String usertype = intent.getStringExtra("userType");
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(username, firstName, surname, email, password, area, city, usertype, token);

            // Subscribe to topic channels
            subscribeTopics( token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param username
     * @param firstName
     * @param surname
     * @param email
     * @param password
     * @param area
     * @param city
     * @param usertype
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String username, final String firstName, final String surname, final String email, final String password, final String area, final String city, final String usertype, final String token) {

        // Add custom implementation, as needed.


        String tag_string_req = "req_register";

//        pDialog.setMessage("Registering ...");
//        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        //JSONObject user = jObj.getJSONObject("user");
                        String userName = jObj.getString("username");
                        Log.i(TAG, "mpiak edw" );


                        // Inserting row in users table
                        db.addUser(userName, firstName,surname, email, usertype);



                        session.setLogin(true,usertype);
                        session.setUsername(userName);
//                        hideDialog();
                        // Launch login activity
                        sendBroadcast(new Intent("loggedin"));
//                        if(session.isLoggedIn()){
//                            if(usertype.equals("employee")){
//                                Intent intent = new Intent(
//                                        getBaseContext(), EmployeeHomeActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//
//                            }
//                            else {
//
//                                Intent intent = new Intent(
//                                        getBaseContext(),
//                                        EmployerHomeActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//
//                            }
//
//                        }
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        sendBroadcast(new Intent(errorMsg));
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("tag", "register");
                params.put("username",username);
                params.put("firstname", firstName);
                params.put("surname",surname);
                params.put("email", email);
                params.put("password", password);
                params.put("area",area);
                params.put("city",city);
                params.put("usertype",usertype);
                params.put("gcm_id",token);
                //   params.put("latitude",latitude.toString());
                //   params.put("longitude",longitude.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}