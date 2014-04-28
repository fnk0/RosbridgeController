package com.gabilheri.rosbridgecontroller.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gabilheri.rosbridgecontroller.app.JsonClasses.JSONParser;
import com.gabilheri.rosbridgecontroller.app.helperClasses.HandyStaticFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ForgotPasswordActivity extends Activity {

    private LinearLayout questionsLayout, getPasswordStuff;
    private EditText securityAnswer1, securityAnswer2, usernameField, passwordField, repeatPasswordField;
    private TextView securityQuestion1, securityQuestion2;
    private String question1, question2, answer1, answer2;

    private ProgressDialog pDialog;
    private ArrayList<String> questionsArrayList, answersArrayList;
    public JSONParser jsonParser = new JSONParser();
    public JSONArray questions = null;

    private static final String QUESTIONS_URL = "http://www.gabilheri.com/rosbridge_controller/view_questions.php";
    private static final String UPDATE_PASSWORD_URL = "http://www.gabilheri.com/rosbridge_controller/update_password";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        questionsLayout = (LinearLayout) findViewById(R.id.getElements);
        getPasswordStuff = (LinearLayout) findViewById(R.id.getPasswordStuff);
        securityQuestion1 = (TextView) findViewById(R.id.securityQuestion1);
        securityQuestion2 = (TextView) findViewById(R.id.securityQuestion2);
        securityAnswer1 = (EditText) findViewById(R.id.securityAnswer1);
        securityAnswer2 = (EditText) findViewById(R.id.securityAnswer2);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.newPassword);
        repeatPasswordField = (EditText) findViewById(R.id.confirmPassword);
    }

    public void getAnswers(View view) {
        if(HandyStaticFunctions.getInternetState(this)) {
            new LoadQuestions().execute();
        }
    }

    public void answerQuestions(View v) {
        String answeredQuestion1 = securityAnswer1.getText().toString();
        String answeredQuestion2 = securityAnswer2.getText().toString();
        Log.d("Answer 1", answersArrayList.get(0));
        Log.d("Answer 2", answersArrayList.get(1));
        Log.d("User Answer 1", answeredQuestion1);
        Log.d("User Answer 1", answeredQuestion2);
        if(answeredQuestion1.equals(answersArrayList.get(0)) && answeredQuestion2.equals(answersArrayList.get(1))) {
            questionsLayout.removeAllViews();
            getPasswordStuff.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void resetPassword(View v) {
        if(HandyStaticFunctions.getInternetState(this)) {
            String password = passwordField.getText().toString();
            String repeatPassword = repeatPasswordField.getText().toString();
            if(!password.equals(repeatPassword)) {
                Toast.makeText(ForgotPasswordActivity.this, "Passwords don't match!", Toast.LENGTH_LONG).show();
            } else if(password.length() < 6) {
                Toast.makeText(ForgotPasswordActivity.this, "Password should be at least 6 characters long!", Toast.LENGTH_LONG).show();
            }  else {
                new UpdatePassword().execute();
            }
        }
    }

    class LoadQuestions extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setMessage("Loading Questions...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String username = usernameField.getText().toString();
            // Initiate the ArrayList to hold the Json data
            questionsArrayList = new ArrayList<String>();
            answersArrayList = new ArrayList<String>();
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                Log.d("Making Question Request for user: ", username);
                JSONObject jObject = jsonParser.makeHttpRequest(QUESTIONS_URL, "POST", params);
                success = jObject.getInt(TAG_SUCCESS);
                Log.d("Success: " , "" + success);
                if(success == 1) {
                    questions = jObject.getJSONArray("questions");
                    for(int i = 0; i < questions.length(); i++) {
                        JSONObject obj = questions.getJSONObject(i);
                        // Get each element based on it's tag
                        int num1 = obj.getInt("number1");
                        int num2 = obj.getInt("number2");
                        question1 = obj.getString("security_question" + num1);
                        question2 = obj.getString("security_question" + num2);
                        answer1 = obj.getString("answer" + num1);
                        answer2 = obj.getString("answer" + num2);
                        Log.d("Question One: ", question1);
                        Log.d("Question Two: ", question2);
                        questionsArrayList.add(question1);
                        questionsArrayList.add(question2);
                        answersArrayList.add(answer1);
                        answersArrayList.add(answer2);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            securityQuestion1.setText(questionsArrayList.get(0));
            securityQuestion2.setText(questionsArrayList.get(1));
            questionsLayout.setVisibility(View.VISIBLE);
            pDialog.dismiss();
        }
    }

    class UpdatePassword extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setMessage("Updating Password...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            questionsArrayList = new ArrayList<String>();
            answersArrayList = new ArrayList<String>();
            // Initiate the ArrayList to hold the Json data

            int success;
            // Building parameters for the request

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                JSONObject jObject = jsonParser.makeHttpRequest(UPDATE_PASSWORD_URL, "POST", params);

                Log.d("Querying: ", UPDATE_PASSWORD_URL);
                Log.d("With username: ", username);
                Log.d("And password: ", password);

                success = jObject.getInt(TAG_SUCCESS);

                Log.d("My Result is...", jObject.getString(TAG_MESSAGE));

                if(success == 1) {

                    Intent loginActivity = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(loginActivity);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

        }
    }

}
