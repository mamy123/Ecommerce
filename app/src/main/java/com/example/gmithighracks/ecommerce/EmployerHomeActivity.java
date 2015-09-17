package com.example.gmithighracks.ecommerce;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gmithighracks.ecommerce.helper.SessionManager;

import java.util.HashMap;


public class EmployerHomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);
        Resources res = getResources();
        // db = new SQLiteHelper(getApplicationContext());

        // session manager
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        //  String text = String.format(res.getString(R.string.employeeName), user.get("firstName"));
      //  TextView lastMsg = (TextView)findViewById(R.id.textView2);
        TextView lastMsg = (TextView)findViewById(R.id.textView2);
        lastMsg.setText(user.get(SessionManager.KEY_FNAME) +"  " + user.get(SessionManager.KEY_SURNAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employer_home, menu);
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
