package com.example.gmithighracks.ecommerce;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.app.Ability;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.app.DropDownListAdapter;
import com.example.gmithighracks.ecommerce.helper.SQLiteHelper;
import com.example.gmithighracks.ecommerce.helper.SessionManager;
import com.example.gmithighracks.ecommerce.service.AddTaskService;
import com.example.gmithighracks.ecommerce.service.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {



    private static final String TAG = AddTaskActivity.class.getSimpleName();
    private Button btnAddTask;
    private ProgressDialog pDialog;
    private EditText title;
    private EditText shortDescription;
    private EditText fullDescription;
    private EditText startDate;
    private EditText endDate;
    private EditText salary;

    private SessionManager session;
    private SQLiteHelper db;
    private TextView error;
    private String userType;
    private Location location;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private DataUpdateReceiver dataUpdateReceiver;
    private static final int REFRESH_DATA_INTENT = 896;

    public static  boolean[] checkSelected;
    private boolean expanded;
    private ArrayList<Ability> abilities;
    private TextView tv;
    private Button addAbilities, showAbilities;
    private PopupWindow pw;
    private HashMap<String, String> user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        title = (EditText) findViewById(R.id.title);
        shortDescription = (EditText) findViewById(R.id.shortdescription);
        fullDescription =(EditText) findViewById(R.id.fulldescription);
        startDate = (EditText) findViewById(R.id.startdate);
        endDate = (EditText) findViewById(R.id.end);
        salary = (EditText) findViewById(R.id.Salary);
        addAbilities = (Button) findViewById(R.id.btnAddSkillsFinal);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);

        // Session manager
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        btnAddTask.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String tit = title.getText().toString();
                String sDescription = shortDescription.getText().toString();

                String fDescription = fullDescription.getText().toString();
                String sDate = startDate.getText().toString();
                String eDate = endDate.getText().toString();
                String sal = salary.getText().toString();



                if (!tit.isEmpty() && !sDescription.isEmpty() && !fDescription.isEmpty() && !sDate.isEmpty() && !eDate.isEmpty() && !sal.isEmpty()) {
                    addTask(tit, sDescription, fDescription, sDate, eDate, sal);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

//

        dataUpdateReceiver =new DataUpdateReceiver();
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter("add_task");
        //intentFilter.addAction(RECEIVE_JSON);
        registerReceiver(dataUpdateReceiver, intentFilter);

        tv = (TextView) findViewById(R.id.skillsTv);

//
        getAllAbilities();
        Button createButton = (Button)findViewById(R.id.btnAddAbilities);
        createButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                initiatePopUp(abilities, tv);
            }
        });

    }


    public void sendSelectedAbilities(View view){
        pw.dismiss();
    }


    private void addTask(final String title, final String shortDescription, final String fullDescription, final String startDate,
                              final String endDate, final String salary) {
//        final Double longitude = location.getLongitude();
        //      final Double latitude = location.getLatitude();
        // Tag used to cancel the request
        pDialog.setMessage("Adding Task ...");
        showDialog();


//
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, AddTaskService.class);
            intent.putExtra("title", title);
            intent.putExtra("shortDescription", shortDescription);
            intent.putExtra("fullDescription",fullDescription);
            intent.putExtra("startDate",startDate);
            intent.putExtra("endDate",endDate);
            intent.putExtra("salary",salary);
            session = new SessionManager(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            intent.putExtra("username", user.get("username"));
            System.out.println(title + "  , " + shortDescription + "  , " + fullDescription + "  , " + startDate + "  , " + endDate + "  , " + salary + "  , " + user.get("username"));

            startService(intent);
        }

    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
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


    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideDialog();
            Log.i(TAG, "pira to broadcast.");
           // String msg = intent.getStringExtra("msg");
            String id = intent.getStringExtra("taskId");
//            if(msg.equals("OK")){
                sendTaskAbilities(id);
                hideDialog();
                    Intent i = new Intent(
                            AddTaskActivity.this, EmployerHomeActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();



//            }
//            else{
//                hideDialog();
//                Toast.makeText(context,
//                        msg, Toast.LENGTH_LONG).show();
//            }
        }
    }

    private void getAllAbilities() {
        //data source for drop-down list
        abilities = new ArrayList<Ability>();

        String tag_string_req = "req_abilities";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ABILITIES, new Response.Listener<String>() {

            //Log.d("DEsdUG", "Somthng request sender response: ");
            @Override
            public void onResponse(String response) {
                Log.i("DEBUG", "Request sender response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jAbilities = jObj.getJSONArray("abilities");
                    for(int i=0;i<jAbilities.length();i++)
                    {
                        Ability ab =new Ability(jAbilities.getJSONObject(i).getInt("id"),jAbilities.getJSONObject(i).getString("name"),jAbilities.getJSONObject(i).getString("description"));
                        abilities.add(ab);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                checkSelected = new boolean[abilities.size()];
                //initialize all values of list to 'unselected' initially
                for (int i = 0; i < checkSelected.length; i++) {
                    checkSelected[i] = false;
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

    private void initiatePopUp(ArrayList<Ability> abilities, TextView tv){
        LayoutInflater inflater = (LayoutInflater)AddTaskActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i("POPUP","Mpika popup");
        //get the pop-up window i.e.  drop-down layout
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));

        //get the view to which drop-down layout is to be anchored
        RelativeLayout layout1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
        pw = new PopupWindow(layout, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        //provide the source layout for drop-down
        pw.setContentView(layout);

        //anchor the drop-down to bottom-left corner of 'layout1'
        pw.showAtLocation(layout, Gravity.NO_GRAVITY, 10, 10);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.DropDownListFinal);
        DropDownListAdapter adapter = new DropDownListAdapter(this, abilities, tv,"addTask");
        list.setAdapter(adapter);


    }

    public void sendTaskAbilities(final String taskId) {
        String tag_string_req = "req_login";



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_TASK_ABILITIES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("SEND", "Send skills Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error");

                    // Check for error node in json
                    if (error != 1) {
                        // user successfully logged i
                        Toast.makeText(getApplicationContext(),
                                "Skills successfully added", Toast.LENGTH_LONG).show();
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
                Log.e("SEnd", "Send skill Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "insertTaskAbilities");
                params.put("taskId",taskId);
                params.put("username",user.get(SessionManager.KEY_USERNAME));
                params.put("num", String.valueOf(abilities.size()));
                int j =0;
                for (int i = 0; i < abilities.size(); i++) {

                    if (checkSelected[i] == true) {
                        j++;
                        params.put("ability" + j, String.valueOf(abilities.get(i).getId()));
                    }
                }
                params.put("num", String.valueOf(j));
                return params;
            }

        };
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
