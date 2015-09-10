package com.example.gmithighracks.ecommerce;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.helper.SQLiteHelper;
import com.example.gmithighracks.ecommerce.helper.SessionManager;


public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText username;
    private EditText firstName;
    private EditText surName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText confirmPassword;
    private EditText areaInput;
    private EditText cityInput;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHelper db;
    private TextView error;
    private String userType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        error = (TextView) findViewById(R.id.accPwd);
        username = (EditText) findViewById(R.id.username);
        firstName = (EditText) findViewById(R.id.firstName);
        surName =(EditText) findViewById(R.id.surName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        areaInput = (EditText) findViewById(R.id.area);
        cityInput = (EditText) findViewById(R.id.city);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHelper(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    EmployeeHomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String user = username.getText().toString();
                String name = firstName.getText().toString();
                String lastName = surName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String confirm = confirmPassword.getText().toString();
                String area = areaInput.getText().toString();
                String city = cityInput.getText().toString();


                if (!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirm.isEmpty() && !userType.isEmpty()) {
                    registerUser(user, name, lastName, email, password, area, city, userType);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String strPass1 = inputPassword.getText().toString();
                String strPass2 = confirmPassword.getText().toString();
                if (strPass1.equals(strPass2)) {
                    error.setText(R.string.pwd_equal);
                    error.setTextColor(0xff00ff00);
                } else {
                    error.setText(R.string.pwd_not_equal);
                    error.setTextColor(0xffff0000);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_emploee:
                if (checked)
                    userType = "employee";
                    break;
            case R.id.radio_employer:
                if (checked)
                    userType = "employer";
                    break;
        }
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String username, final String name, final String surName, final String email,
                              final String password, final String area, final String city, final String usertype) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String userName = user.getString("username");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(userName, email, uid, created_at);

                        // Launch login activity
                        if (usertype.equals("employee")) {
                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    EmployeeHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    EmployerHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("username",username);
                params.put("name", name);
                params.put("surname",surName);
                params.put("email", email);
                params.put("password", password);
                params.put("area",area);
                params.put("city",city);
                params.put("usertype",usertype);

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
