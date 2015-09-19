package com.example.gmithighracks.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import android.view.ViewGroup.LayoutParams;

import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EmployeeHomeActivity extends ActionBarActivity {

    private SQLiteHelper db;
    private SessionManager session;
    private Button searchComp, addAbilities, showAbilities;
    private HashMap<String, String> user;
    private PopupWindow pw;
    public static  boolean[] checkSelected;
    private boolean expanded;
    private ArrayList<Ability> abilities;
    private TextView tv;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);
        Resources res = getResources();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // session manager
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
      //  String text = String.format(res.getString(R.string.employeeName), user.get("firstName"));
        TextView lastMsg = (TextView)findViewById(R.id.textView2);
        lastMsg.setText(user.get(SessionManager.KEY_FNAME) + " "+ user.get(SessionManager.KEY_SURNAME));
        searchComp = (Button) findViewById(R.id.btnSearchComp);
        showAbilities = (Button) findViewById(R.id.btndropDownListSkills);
        addAbilities = (Button) findViewById(R.id.btnAddSkillsFinal);

        getAbilities();
//        Log.d("WAHT2", "checkseledasdsadadsa:" + abilities.size() + " and " + abilities.get(0).getName());
        Button createButton = (Button)findViewById(R.id.btnAddSkills);
        createButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                initiatePopUp(abilities, tv);
            }
        });
//        addAbilities.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                sendSelectedAbilities();
//            }
//        });
    }

    public void sendSelectedAbilities(View view) {
        String tag_string_req = "req_login";

        pDialog.setMessage("Adding skills ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ABILITIES, new Response.Listener<String>() {

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
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "insertAbilities");
                params.put("username", user.get(SessionManager.KEY_USERNAME));
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
    public void logout(View view){
        session.deleteSession();
        Intent intent = new Intent(EmployeeHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void getAbilities() {
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
                        Log.d("WAHT3", "checkseledasdsadadsa:" + abilities.size() + " and " + abilities.get(i).getName());
                    }
                    Log.d("WAHT4", "checkseledasdsadadsa:" + abilities.size());
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                checkSelected = new boolean[abilities.size()];
                Log.d("WAHT2", "checkseledasdsadadsa:" + abilities.size()+" and " +checkSelected.length);
                //initialize all values of list to 'unselected' initially
                for (int i = 0; i < checkSelected.length; i++) {
                    checkSelected[i] = false;
                }


                tv = (TextView) findViewById(R.id.DropDownListSelectBox);
                tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (!expanded) {
                            //display all selected values
                            String selected = "";
                            int flag = 0;
                            for (int i = 0; i < abilities.size(); i++) {
                                if (checkSelected[i] == true) {
                                    selected += abilities.get(i).getName();
                                    selected += ", ";
                                    flag = 1;
                                }
                            }
                            if (flag == 1)
                                tv.setText(selected);
                            expanded = true;
                        } else {
                            //display shortened representation of selected values
                            tv.setText(DropDownListAdapter.getSelected());
                            expanded = false;
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEnd", "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();

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





	/*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
    	 * When this selectBox is clicked it will display all the selected values
    	 * and when clicked again it will display in shortened representation as before.
    */



    }

    private void initiatePopUp(ArrayList<Ability> abilities, TextView tv){
        LayoutInflater inflater = (LayoutInflater)EmployeeHomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i("POPUP","Mpika popup");
        //get the pop-up window i.e.  drop-down layout
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));

        //get the view to which drop-down layout is to be anchored
        RelativeLayout layout1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
        pw = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new OnTouchListener() {

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
        DropDownListAdapter adapter = new DropDownListAdapter(this, abilities, tv);
        list.setAdapter(adapter);
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
