package com.example.gmithighracks.ecommerce;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class SearchHistoryActivity extends ListActivity {

    private ArrayAdapter<String> adapter;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<Tasks> tasks;
    private Button btnSendInvites;

    String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        Intent intent = getIntent();
        username = intent.getStringExtra("selectedEmployee");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        getEmployeeDetails();

        btnSendInvites = (Button) findViewById(R.id.button2);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        setListAdapter(adapter);

        // Login button Click Event
        btnSendInvites.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                System.out.println("Gcm try....");

            }

        });

    }




    private void getEmployeeDetails() {
        tasks = new ArrayList<Tasks>();

        String tag_string_req = "req_abilities";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EMPLOYEE_HISTORY, new Response.Listener<String>() {

            //Log.d("DEsdUG", "Somthng request sender response: ");
            @Override
            public void onResponse(String response) {
                Log.i("DEBUG", "Request sender response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jTasks = jObj.getJSONArray("tasks");
                    for(int i=0;i<jTasks.length();i++)
                    {
                        Tasks task =new Tasks(jTasks.getJSONObject(i).getInt("id"),jTasks.getJSONObject(i).getString("name"),jTasks.getJSONObject(i).getString("shortDescription"),jTasks.getJSONObject(i).getString("fullDescription"),jTasks.getJSONObject(i).getString("startTime"),jTasks.getJSONObject(i).getString("endTime"),jTasks.getJSONObject(i).getInt("salary"),jTasks.getJSONObject(i).getInt("closed"),jTasks.getJSONObject(i).getString("created_by"));
                        tasks.add(task);
                        listItems.add("Task Title: "+task.getName()+ "| Task Description: " + task.getsDescription());
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEnd", "history Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "getAll");
                params.put("username", username);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_history, menu);
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
