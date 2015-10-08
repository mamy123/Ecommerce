package com.example.gmithighracks.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    //  private ProgressDialog pDialog;
    private SessionManager session;
    TextView lastMsg;
    private Button btnFind,btnOffers;
    private static final String TAG = EmployerTaskActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employer_task);
        lastMsg = (TextView)findViewById(R.id.textView4);

        btnFind = (Button) findViewById(R.id.button);
        btnOffers = (Button) findViewById(R.id.button3);
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        Intent intent = getIntent();

        // fetch value from key-value pair and make it visible on TextView.
        final String taskName = intent.getStringExtra("selectedTask");

        // name
        //final  String username = user.get("username");
        Tasks task = findTask(taskName);


        btnFind.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                findEmployees(taskName);
            }


        });

        btnOffers.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Offers(taskName);
            }


        });


    }

    public void findEmployees(String taskName)
    {
        Intent intent = new Intent(EmployerTaskActivity.this, SearchEmployeesActivity.class);
        // add the selected text item to our intent.
        intent.putExtra("selectedTask", taskName);
        startActivity(intent);
        finish();
    }

    public void Offers(String taskName)
    {
        Intent intent = new Intent(EmployerTaskActivity.this, OffersActivity.class);
        // add the selected text item to our intent.
        intent.putExtra("selectedTask", taskName);
        startActivity(intent);
        finish();
    }

    private Tasks findTask(final String  taskName )
    {

        String tag_string_req = "req_getTask";

        //   pDialog.setMessage("Getting Task ...");
        //  showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_TASK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Task Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error");

                    // Check for error node in json
                    if (error != 1) {
                        // user successfully logged in
                        // Create login session
                        //   String firstName = jObj.getString("firstname");
                        //  String surname = jObj.getString("surname");
                        Tasks t;
                        JSONArray jTask = jObj.getJSONArray("task");
                        //Integer ii = (jAbilities.getJSONObject(i).getInt("id"));
                        //for(int i=0;i<jTask.length();i++) {
                        t = new Tasks(jTask.getJSONObject(0).getInt("id"),jTask.getJSONObject(0).getString("name"), jTask.getJSONObject(0).getString("shortDescription"), jTask.getJSONObject(0).getString("fullDescription"), jTask.getJSONObject(0).getString("startTime"), jTask.getJSONObject(0).getString("endTime"), jTask.getJSONObject(0).getInt("salary"), jTask.getJSONObject(0).getInt("closed"), jTask.getJSONObject(0).getString("created_by"));
                        //}
                        System.out.println(t.getName()+' '+t.getsDescription());
                        String s = "Title: " + t.getName()+" Short Description: "+ t.getsDescription()+" Full Description: "+ t.getfDescription()+ " Start Date"+ t.getStime()+" End time: "+ t.getEtime()+" Salary: "+ t.getSalary()+" Created by: "+ t.getCreated_by();
                        lastMsg.setText(s);
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

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "tasks");
                params.put("taskName", taskName);



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


}