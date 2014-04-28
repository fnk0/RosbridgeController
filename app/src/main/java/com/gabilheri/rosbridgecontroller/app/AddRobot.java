package com.gabilheri.rosbridgecontroller.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import com.gabilheri.rosbridgecontroller.app.JsonClasses.JSONParser;
import com.gabilheri.rosbridgecontroller.app.helperClasses.HandyStaticFunctions;
import com.gabilheri.rosbridgecontroller.app.helperClasses.Robot;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AddRobot extends ActionBarActivity {

    private EditText robotNameField, ipBox1Field, ipBox2Field, ipBox3Field, ipBox4Field, portField, urlAddressField, port2Field;
    private EditText velMaxField, velDefaultField, angMaxField, angDefaultField;
    private RadioGroup ipTypeGroup;
    private LinearLayout layoutIP, layoutURL;
    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private static final String NEW_ROBOT_URL = "http://www.gabilheri.com/rosbridge_controller/new_robot.php";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_robot);

        robotNameField = (EditText) findViewById(R.id.robotName);
        ipBox1Field = (EditText) findViewById(R.id.ipBox1);
        ipBox2Field = (EditText) findViewById(R.id.ipBox2);
        ipBox3Field = (EditText) findViewById(R.id.ipBox3);
        ipBox4Field = (EditText) findViewById(R.id.ipBox4);
        portField = (EditText) findViewById(R.id.port);
        velDefaultField = (EditText) findViewById(R.id.velDefault);
        velMaxField = (EditText) findViewById(R.id.velMax);
        angDefaultField = (EditText) findViewById(R.id.angDefault);
        angMaxField = (EditText) findViewById(R.id.angMax);
        ipTypeGroup = (RadioGroup) findViewById(R.id.ipUrlGroup);

        urlAddressField = (EditText) findViewById(R.id.urlAddressField);
        port2Field = (EditText) findViewById(R.id.port2);
        layoutURL = (LinearLayout) findViewById(R.id.layoutUrlAddress);
        layoutIP = (LinearLayout) findViewById(R.id.layoutIpAddress);
        layoutURL.setVisibility(LinearLayout.GONE);
    }

    public void createRobot(View view) {
        if(HandyStaticFunctions.getInternetState(this)) {
            new AddNewRobot().execute();
        }
    }

    public void changeLayouts(View view) {
        int id = view.getId();
        if(id == R.id.ipAddress) {
            layoutIP.setVisibility(LinearLayout.VISIBLE);
            layoutURL.setVisibility(LinearLayout.GONE);
        } else if(id == R.id.urlAddress) {
            layoutIP.setVisibility(LinearLayout.GONE);
            layoutURL.setVisibility(LinearLayout.VISIBLE);
        }
    }

    class AddNewRobot extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddRobot.this);
            pDialog.setMessage("Adding Robot...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String getRobotName = robotNameField.getText().toString();
            String getVelDefault = velDefaultField.getText().toString();
            String getVelMax = velMaxField.getText().toString();
            String getAngularDefault = angDefaultField.getText().toString();
            String getAngularMax = angMaxField.getText().toString();
            SharedPreferences loginPref = getSharedPreferences("loginPref", MODE_PRIVATE);
            String username = loginPref.getString("username", "");
            String getAddress;
            String getPort;

            if(ipTypeGroup.getCheckedRadioButtonId() == R.id.ipAddress) {
                getAddress = ipBox1Field.getText().toString() + "." + ipBox2Field.getText().toString() + "."
                        + ipBox3Field.getText().toString() + "." + ipBox4Field.getText().toString();

                getPort = portField.getText().toString();
            } else {
                getAddress = urlAddressField.getText().toString();
                getPort = port2Field.getText().toString();
            }
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(Robot.TAG_USERNAME, username));
                params.add(new BasicNameValuePair(Robot.TAG_ROBOTNAME, getRobotName));
                params.add(new BasicNameValuePair(Robot.TAG_URL, getAddress));
                params.add(new BasicNameValuePair(Robot.TAG_PORT, getPort));
                params.add(new BasicNameValuePair(Robot.TAG_DEFAULT_VELOCITY, getVelDefault));
                params.add(new BasicNameValuePair(Robot.TAG_MAX_VELOCITY, getVelMax));
                params.add(new BasicNameValuePair(Robot.TAG_DEFAULT_ANGULAR, getAngularDefault));
                params.add(new BasicNameValuePair(Robot.TAG_MAX_ANGULAR, getAngularMax));

                JSONObject json = jsonParser.makeHttpRequest(NEW_ROBOT_URL, "POST", params);
                Log.d("New Robot Attempt", json.toString());
                int success = json.getInt(TAG_SUCCESS);
                if(success == 1) {
                    Intent backToRobots = new Intent(AddRobot.this, RobotsActivity.class);
                    startActivity(backToRobots);
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch(JSONException e) {
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
