package com.example.gmithighracks.ecommerce;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.gmithighracks.ecommerce.helper.SQLiteHelper;
import com.example.gmithighracks.ecommerce.helper.SessionManager;
import com.example.gmithighracks.ecommerce.service.AddTaskService;
import com.example.gmithighracks.ecommerce.service.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.HashMap;

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

        btnAddTask = (Button) findViewById(R.id.btnAddTask);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHelper(getApplicationContext());


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



        dataUpdateReceiver =new DataUpdateReceiver();
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter("Send_login");
        //intentFilter.addAction(RECEIVE_JSON);
        registerReceiver(dataUpdateReceiver, intentFilter);


    }



    private void addTask(final String title, final String shortDescription, final String fullDescription, final String startDate,
                              final String endDate, final String salary) {
//        final Double longitude = location.getLongitude();
        //      final Double latitude = location.getLatitude();
        // Tag used to cancel the request
        pDialog.setMessage("Registering ...");
        showDialog();


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(RegistrationIntentService.QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
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
            intent.putExtra("username",user.get("username"));
            System.out.println(title+"  , "+shortDescription+"  , "+fullDescription+"  , "+startDate+"  , "+endDate+"  , "+salary+"  , "+user.get("username"));
            startService(intent);
        }
        //  hideDialog();

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
            String msg = intent.getStringExtra("msg");
            if(session.isLoggedIn()){

                    Intent i = new Intent(
                            AddTaskActivity.this, EmployerHomeActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();



            }
            else{

                Toast.makeText(context,
                        msg, Toast.LENGTH_LONG).show();
            }
        }
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
