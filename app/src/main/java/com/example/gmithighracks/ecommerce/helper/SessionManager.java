package com.example.gmithighracks.ecommerce.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

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
    private static final String KEY_USERTYPE = "usertype";
    private static final String KEY_EMPLOYER = "employer";

    public static final String KEY_USERNAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_FNAME = "firstName";
    public static final String KEY_SURNAME = "surname";



    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String userType, String username, String fname, String surname) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_USERTYPE, userType);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FNAME, fname);
        editor.putString(KEY_SURNAME, surname);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_FNAME, pref.getString(KEY_FNAME, null));
        user.put(KEY_SURNAME, pref.getString(KEY_SURNAME, null));
        user.put(KEY_USERTYPE,pref.getString(KEY_USERTYPE,null));

        // return user
        return user;
    }

    public void setUsername(String username){
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }


    public void deleteSession(){
        editor.clear();
        editor.commit();
    }
}
