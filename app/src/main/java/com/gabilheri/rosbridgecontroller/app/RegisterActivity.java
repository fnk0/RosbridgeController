package com.gabilheri.rosbridgecontroller.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gabilheri.rosbridgecontroller.app.JsonClasses.JSONParser;
import com.gabilheri.rosbridgecontroller.app.helperClasses.HandyStaticFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  @author Marcus Gabilheri
 *  @since April, 2014
 *  @version 1.0
 */

public class RegisterActivity extends Activity {

    // Declaring variables and constants
    private EditText mUsername, passwordField, repeatPasswordField, answer1, answer2, answer3;
    private Spinner securityQuestion1, securityQuestion2, securityQuestion3;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static final String REGISTER_URL = "http://www.gabilheri.com/rosbridge_controller/new_user.php";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mUsername = (EditText) findViewById(R.id.username);

        passwordField = (EditText) findViewById(R.id.password);
        repeatPasswordField = (EditText) findViewById(R.id.repeatPassword);

        securityQuestion1 = (Spinner) findViewById(R.id.question1);
        securityQuestion2 = (Spinner) findViewById(R.id.question2);
        securityQuestion3 = (Spinner) findViewById(R.id.question3);
        answer1 = (EditText) findViewById(R.id.securityQuestion1);
        answer2 = (EditText) findViewById(R.id.securityQuestion2);
        answer3 = (EditText) findViewById(R.id.securityQuestion3);
    }

    /**
     *
     * @param view
     */
    public void createUser(View view) {
        if(HandyStaticFunctions.getInternetState(this)) {
            String password = passwordField.getText().toString();
            String repeatPassword = repeatPasswordField.getText().toString();
            String email = mUsername.getText().toString();
            if(!password.equals(repeatPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords don't match!", Toast.LENGTH_LONG).show();
            } else if(password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters long!", Toast.LENGTH_LONG).show();
            }  else {
                new CreateUser().execute();
            }
        }
    }

    class CreateUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            int success;
            String userName = mUsername.getText().toString();

            String password = passwordField.getText().toString();

            String question1= securityQuestion1.getSelectedItem().toString();
            String question2 = securityQuestion2.getSelectedItem().toString();
            String question3 = securityQuestion3.getSelectedItem().toString();

            String sAnswer1 = answer1.getText().toString();
            String sAnswer2 = answer2.getText().toString();
            String sAnswer3 = answer3.getText().toString();

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date currentDate = new Date();

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", userName));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("date", dateFormat.format(currentDate)));
                params.add(new BasicNameValuePair("security_question1", question1));
                params.add(new BasicNameValuePair("security_question2", question2));
                params.add(new BasicNameValuePair("security_question3", question3));
                params.add(new BasicNameValuePair("answer1", sAnswer1));
                params.add(new BasicNameValuePair("answer2", sAnswer2));
                params.add(new BasicNameValuePair("answer3", sAnswer3));

                Log.d("request!", "starting");

                // Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST", params);

                // Full Json response
                Log.d("Register attempt", json.toString());

                // Json success element
                success = json.getInt(TAG_SUCCESS);

                if(success == 1) {
                    Log.d("User created!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Register failed!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            if(file_url != null) {
                Toast.makeText(RegisterActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }
    }

}
