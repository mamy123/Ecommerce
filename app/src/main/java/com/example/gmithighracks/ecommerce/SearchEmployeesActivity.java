package com.example.gmithighracks.ecommerce;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchEmployeesActivity extends AppCompatActivity {

    private static final String TAG = SearchEmployeesActivity.class.getSimpleName();
    public ArrayList<String> usernames, Employees;
    public ArrayList<Integer> num;
    private ListView lv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_employees);

        usernames = new ArrayList<String>();
        Employees = new ArrayList<String>();
        num = new ArrayList<Integer>();
        String tag_string_req = "req_SerachEmployees";
        lv1 = (ListView)findViewById(R.id.listView2);
        Intent intent = getIntent();
        final String taskName = intent.getStringExtra("selectedTask");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEARCH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Employees Response: " + response.toString());
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
                        JSONArray jAbilities = jObj.getJSONArray("results");
                        for (int i = 0; i < jAbilities.length(); i++) {

                            String ab = (jAbilities.getJSONObject(i).getString("username"));
                            Integer ii = (jAbilities.getJSONObject(i).getInt("num"));
                            System.out.println(ab +" "+ii);
                            usernames.add(ab);

                            num.add(ii);

                            String emp = "Employee: "+ab+" has "+ii+" common abilities";
                            Employees.add(emp);
                        }

                        lv1.setAdapter(new ArrayAdapter<String>(SearchEmployeesActivity.
                                this,android.R.layout.simple_list_item_1, Employees));
                        lv1.setOnItemClickListener(new ListClickHandler());

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
                params.put("name", taskName);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_employees, menu);
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


    public class ListClickHandler implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            System.out.println("mikaaaaaaaaa");
            String text = ((TextView) view).getText().toString();
            //String text = listText.getText().toString();

            System.out.println(usernames.get(position));
            // create intent to start another activity
            Intent intent = new Intent(SearchEmployeesActivity.this, SearchHistoryActivity.class);
            // add the selected text item to our intent.
            intent.putExtra("selectedEmployee", usernames.get(position));
            startActivity(intent);
            finish();
        }


    }



}
