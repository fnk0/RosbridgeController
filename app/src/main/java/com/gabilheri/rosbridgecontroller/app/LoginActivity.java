package com.gabilheri.rosbridgecontroller.app;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gabilheri.rosbridgecontroller.app.JsonClasses.JSONParser;
import com.gabilheri.rosbridgecontroller.app.helperClasses.FieldsRequiredDialog;
import com.gabilheri.rosbridgecontroller.app.helperClasses.HandyStaticFunctions;
import com.gabilheri.rosbridgecontroller.app.helperClasses.NoInternetDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Gabilheri
 * @version 1.0
 * @since April, 2014
 */
public class LoginActivity extends ActionBarActivity {

    public EditText mUsername, mPassword;
    public Button mSubmit, mContinue, mForgot;

    // Progress Dialog
    public ProgressDialog pDialog;

    // JSON parser class
    public JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = "http://www.gabilheri.com/rosbridge_controller/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";
    public boolean userAlreadyLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences("loginPref", MODE_PRIVATE);
        if(HandyStaticFunctions.getInternetState(this)) {
            if(sp.contains(TAG_USERNAME) && sp.contains(TAG_PASSWORD
            )) {
                userAlreadyLoggedIn = true;
                new AttemptLogin().execute();
            }
        }
        // Setup input fields
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);

        // Setup buttons
        mSubmit = (Button) findViewById(R.id.signIn);
        mContinue = (Button) findViewById(R.id.continueWithoutLogin);
        mForgot = (Button) findViewById(R.id.forgotUsernamePassword);
    }

    /**
     *
     * @param view
     */
    public void signInRegister (View view) {
        if(!mUsername.getText().toString().trim().equals("") && !mPassword.getText().toString().trim().equals("")) {
            if (HandyStaticFunctions.getInternetState(this)) {
                new AttemptLogin().execute();
            }
        } else if(!mUsername.getText().toString().trim().equals("")) {
            DialogFragment fields = new FieldsRequiredDialog();
            fields.show(getFragmentManager(), "fields");
        } else {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    /**
     *
     * @param view
     */
    public void continueWithoutLogin (View view) {
        startActivity(new Intent(LoginActivity.this, RobotsActivity.class));
    }

    /**
     *
     * @param view
     */
    public void forgotUsernamePassword (View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    /**
     *
     */
    class AttemptLogin extends AsyncTask<String, String, String> {

        // Three methods gets called, first preExecute, then doInBackground
        // Once doInBackground is completed, the onPost execute method will be called

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;
            String username;
            String password;

            if(userAlreadyLoggedIn) {
                SharedPreferences loginPref = getSharedPreferences("loginPref", MODE_PRIVATE);
                username = loginPref.getString(TAG_USERNAME, "");
                password = loginPref.getString(TAG_PASSWORD, "");
            } else {
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
            }

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_USERNAME, username));
                params.add(new BasicNameValuePair(TAG_PASSWORD, password));

                Log.d("request!", "starting");

                // Getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                // Check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                if(success == 1) {
                    Log.d("Login successful!", json.toString());

                    // Save user data
                    SharedPreferences loginPref = getSharedPreferences("loginPref", MODE_PRIVATE);
                    SharedPreferences.Editor loginEditor = loginPref.edit();
                    loginEditor.putString(TAG_USERNAME, username);
                    loginEditor.putString(TAG_PASSWORD, password);
                    loginEditor.commit();
                    startActivity(new Intent(LoginActivity.this, RobotsActivity.class));
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // After completing background task dismiss the progress dialog
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
