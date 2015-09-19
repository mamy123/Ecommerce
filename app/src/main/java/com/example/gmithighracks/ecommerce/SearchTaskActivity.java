package com.example.gmithighracks.ecommerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_task);
        // session manager
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        tasks = new ArrayList<Tasks>();
        getAllAbilities();
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

    private void getAllAbilities() {
        //data source for drop-down list


        String tag_string_req = "req_abilities";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ABILITIES, new Response.Listener<String>() {

            //Log.d("DEsdUG", "Somthng request sender response: ");
            @Override
            public void onResponse(String response) {
                Log.i("DEBUG", "Request sender response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jTasks = jObj.getJSONArray("abilities");
                    for(int i=0;i<jTasks.length();i++)
                    {
//                        Ability task =new Tasks(jTasks.getJSONObject(i).getInt("id"),jTasks.getJSONObject(i).getString("name"),jTasks.getJSONObject(i).getString("description"));
//                        tasks.add(task);
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

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void logout(View view){
        session.deleteSession();
        Intent intent = new Intent(SearchTaskActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
