package com.example.gmithighracks.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class EmployeeViewTaskActivity extends AppCompatActivity {

    Tasks task;
    TextView taskTitle,sDescription,fDescription,startTime,stopTime,created,salary;
    Button submit;
    private ProgressDialog pDialog;
    private SessionManager session;
    private HashMap<String, String> user;
    EditText experience, offer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_view_task);
        task = new Tasks();
        Intent intent = getIntent();
        task.setName(intent.getStringExtra("name"));
        task.setId(Integer.parseInt(intent.getStringExtra("id")));
        task.setsDescription(intent.getStringExtra("sDescription"));
        task.setfDescription(intent.getStringExtra("fDescription"));
        task.setStime(intent.getStringExtra("startTime"));
        task.setEtime(intent.getStringExtra("stopTime"));
        task.setCreated_by(intent.getStringExtra("created_by"));
        task.setSalary(Integer.parseInt(intent.getStringExtra("salary")));

        taskTitle = (TextView) findViewById(R.id.taskTitle);
        taskTitle.setText(task.getName());
        sDescription = (TextView) findViewById(R.id.shortDescription);
        sDescription.setText(task.getsDescription());
        fDescription = (TextView) findViewById(R.id.fullDescription);
        fDescription.setText(task.getfDescription());
        startTime = (TextView) findViewById(R.id.startDate);
        startTime.setText(task.getName());
        stopTime = (TextView) findViewById(R.id.stopDate);
        stopTime.setText(task.getName());
        created = (TextView) findViewById(R.id.createdBy);
        created.setText(task.getName());
        salary = (TextView) findViewById(R.id.salary);
        salary.setText(task.getName());
        experience = (EditText) findViewById(R.id.experience);
        offer = (EditText) findViewById(R.id.offer);

        // session manager
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        submit = (Button) findViewById(R.id.sendOffer);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String exp = experience.getText().toString();
                String of =offer.getText().toString();
                if (!exp.isEmpty() && !of.isEmpty() )
                    sendOffer(exp,of);
                else{
                    Toast.makeText(getApplicationContext(),
                            "Please enter your offer and your experience!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    private void sendOffer(final String xp, final String price){
        pDialog.setMessage("Sending offer...");
        showDialog();
        String tag_string_req = "req_abilities";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEND_OFFER, new Response.Listener<String>() {

            //Log.d("DEsdUG", "Somthng request sender response: ");
            @Override
            public void onResponse(String response) {
                Log.i("DEBUG", "Request sender response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error");

                    // Check for error node in json
                    if (error != 1) {
                        hideDialog();
                        // user successfully logged i
                        Toast.makeText(getApplicationContext(),
                                "Offer successfully added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EmployeeViewTaskActivity.this, EmployeeHomeActivity.class);
                        startActivity(intent);
                        finish();
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
                Log.e("SEnd", "Login Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "getAll");
                params.put("username",user.get(SessionManager.KEY_USERNAME));
                params.put("experience",xp);
                params.put("price",price);
                params.put("endTime",task.getEtime());
                params.put("taskId",String.valueOf(task.getId()));

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_view_task, menu);
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
