package com.example.gmithighracks.ecommerce.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by nikos on 01-Sep-15.
 */
public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "freeLancerLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_EMPLOYEE = "employee";
    private static final String KEY_EMPLOYER = "employer";
    private String KEY_USERNAME;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String userType) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        if(userType.equals("employee"))
            editor.putBoolean(KEY_EMPLOYEE, true);
        else
            editor.putBoolean(KEY_EMPLOYER,true);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setUsername(String username){
        KEY_USERNAME = username;
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public boolean isEmployee(){
        return pref.getBoolean(KEY_EMPLOYEE, false);
    }
}
