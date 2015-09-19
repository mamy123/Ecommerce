package com.example.gmithighracks.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import android.view.ViewGroup.LayoutParams;

import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gmithighracks.ecommerce.app.AppConfig;
import com.example.gmithighracks.ecommerce.app.AppController;
import com.example.gmithighracks.ecommerce.app.DropDownListAdapter;
import com.example.gmithighracks.ecommerce.app.RequestSender;
import com.example.gmithighracks.ecommerce.helper.SQLiteHelper;
import com.example.gmithighracks.ecommerce.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EmployeeHomeActivity extends ActionBarActivity {

    private SQLiteHelper db;
    private SessionManager session;
    private Button searchComp, addAbilities;
    private HashMap<String, String> user;
    private PopupWindow pw;
    public static boolean[] checkSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);
        Resources res = getResources();
       // db = new SQLiteHelper(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
      //  String text = String.format(res.getString(R.string.employeeName), user.get("firstName"));
        TextView lastMsg = (TextView)findViewById(R.id.textView2);
        lastMsg.setText(user.get(SessionManager.KEY_FNAME) + " "+ user.get(SessionManager.KEY_SURNAME));
        searchComp = (Button) findViewById(R.id.btnSearchComp);
        addAbilities = (Button) findViewById(R.id.btnAddSkills);
        addAbilities.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getAbilities();
            }
        });
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
        final ArrayList<String> items = new ArrayList<String>();
        HashMap<String, String> abilitiesReq = new HashMap<String, String>();
        abilitiesReq.put("tag", "getAll");
       // abilitiesReq.put("username", user.get(SessionManager.KEY_USERNAME));
//        RequestSender rs = new RequestSender();
//        JSONObject jObj = rs.sendMessage(abilitiesReq, AppConfig.URL_ABILITIES);
//        try {
//            Log.i("Abilities","My abilities"+jObj.getString("abilities"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        items.add("Item 4");
        items.add("Item 5");

        String tag_string_req = "req_abilities";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ABILITIES, new Response.Listener<String>() {

            //Log.d("DEsdUG", "Somthng request sender response: ");
            @Override
            public void onResponse(String response) {
                Log.i("DEBUG", "Request sender response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

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


/*
        checkSelected = new boolean[items.size()];
        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }

	/*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
    	 * When this selectBox is clicked it will display all the selected values
    	 * and when clicked again it will display in shortened representation as before.

        final TextView tv = (TextView) findViewById(R.DropDownList.SelectBox);
        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!expanded) {
                    //display all selected values
                    String selected = "";
                    int flag = 0;
                    for (int i = 0; i < items.size(); i++) {
                        if (checkSelected[i] == true) {
                            selected += items.get(i);
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
        });*/
    }

    private void initiatePopUp(ArrayList<String> items, TextView tv){
        LayoutInflater inflater = (LayoutInflater)EmployeeHomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        pw.showAsDropDown(layout1);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.DropDownListFinal);
        DropDownListAdapter adapter = new DropDownListAdapter(this, items, tv);
        list.setAdapter(adapter);
    }
}
