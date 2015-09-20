package com.example.gmithighracks.ecommerce;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.app.Ability;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchTaskActivity extends AppCompatActivity {

    private SessionManager session;
    private HashMap<String, String> user;
    private  ArrayList<Tasks> tasks;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems=new ArrayList<String>();
    private ListView lv1;
    private int commonSkills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_task);
        // session manager
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        tasks = new ArrayList<Tasks>();
        getAllTasks();
        lv1 = (ListView)findViewById(R.id.listView);
//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                listItems);

       // lv1.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_task, menu);
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

    private void getAllTasks() {
        //data source for drop-down list


        String tag_string_req = "req_abilities";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_TASKS_SORTED, new Response.Listener<String>() {

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
                        commonSkills = jTasks.getJSONObject(i).getInt("counter");
                        tasks.add(task);
                        listItems.add("Task Title: "+task.getName()+ "| Task Description: " + task.getsDescription());
                    }
                    lv1.setAdapter(new ArrayAdapter<String>(SearchTaskActivity.
                            this, android.R.layout.simple_list_item_1, listItems));
                    lv1.setOnItemClickListener(new ListClickHandler());

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

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public class ListClickHandler implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            System.out.println("position is:" + position);
            String text = ((TextView) view).getText().toString();
            System.out.println(text);
            // create intent to start another activity
            Intent intent = new Intent(SearchTaskActivity.this, EmployeeViewTaskActivity.class);
            // add the selected text item to our intent.
            intent.putExtra("name", tasks.get(position).getName());
            intent.putExtra("id",String.valueOf(tasks.get(position).getId()));
            intent.putExtra("sDescription", tasks.get(position).getsDescription());
            intent.putExtra("fDescription",tasks.get(position).getfDescription());
            intent.putExtra("startTime",tasks.get(position).getStime());
            intent.putExtra("stopTime", tasks.get(position).getEtime());
            intent.putExtra("created_by",tasks.get(position).getCreated_by());
            intent.putExtra("salary",String.valueOf(tasks.get(position).getSalary()));
            startActivity(intent);
            finish();
        }
    }

    public void logout(View view){
        session.deleteSession();
        Intent intent = new Intent(SearchTaskActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(SearchTaskActivity.this, EmployeeHomeActivity.class);
        startActivity(intent);
        finish();
    }
}
