package com.example.gmithighracks.ecommerce;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gmithighracks.ecommerce.helper.SessionManager;

import java.util.HashMap;


public class EmployerHomeActivity extends ActionBarActivity {

    private Button btnNewCompetition, btnCompetitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);
        Resources res = getResources();
        // db = new SQLiteHelper(getApplicationContext());
        btnNewCompetition = (Button) findViewById(R.id.btnNewCompetition);
        btnCompetitions = (Button) findViewById(R.id.btnCompetitions);
        // session manager
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        //  String text = String.format(res.getString(R.string.employeeName), user.get("firstName"));
      //  TextView lastMsg = (TextView)findViewById(R.id.textView2);
        TextView lastMsg = (TextView)findViewById(R.id.textView2);
        lastMsg.setText(user.get(SessionManager.KEY_FNAME) +"  " + user.get(SessionManager.KEY_SURNAME));


        btnNewCompetition.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent;
                intent = new Intent(EmployerHomeActivity.this,
                        AddTaskActivity.class);
                startActivity(intent);
                finish();

            }

        });

        btnCompetitions.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent;
                intent = new Intent(EmployerHomeActivity.this,
                        GetUserTasksActivity.class);
                startActivity(intent);
                finish();

            }

        });


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
