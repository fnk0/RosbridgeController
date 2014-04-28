package com.gabilheri.rosbridgecontroller.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.gabilheri.rosbridgecontroller.app.JsonClasses.JSONParser;
import com.gabilheri.rosbridgecontroller.app.adapters.ListRowGroup;
import com.gabilheri.rosbridgecontroller.app.adapters.RobotsExpandableAdapter;
import com.gabilheri.rosbridgecontroller.app.helperClasses.HandyStaticFunctions;
import com.gabilheri.rosbridgecontroller.app.helperClasses.Robot;
import com.gabilheri.rosbridgecontroller.app.helperClasses.RobotController;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class RobotsActivity extends Activity {

    private EditText ipBox1Field, ipBox2Field, ipBox3Field, ipBox4Field, portField, urlAddressField, port2Field;
    private EditText velMaxField, velDefaultField, angMaxField, angDefaultField;
    private RadioGroup ipTypeGroup;
    private LinearLayout layoutIP, layoutURL;
    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private String username;
    private LinearLayout loggedInLayout, notLoggedInLayout;
    // Create an Array to hold the robots
    public JSONArray robots = null;
    // GroupList Stuff
    private SparseArray<ListRowGroup> robotsGroup = new SparseArray<>();
    private ArrayList<Robot> robotsList;
    // PHP script address
    private static final String ROBOTS_URL = "http://www.gabilheri.com/rosbridge_controller/view_robots.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robots);

        SharedPreferences loginPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        username = loginPref.getString("username", "");

        loggedInLayout = (LinearLayout) findViewById(R.id.loggedInLayout);
        notLoggedInLayout = (LinearLayout) findViewById(R.id.notLoggedInLayout);

        if(username.equals("")) {
            loggedInLayout.setVisibility(LinearLayout.GONE);
            notLoggedInLayout.setVisibility(LinearLayout.VISIBLE);
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        if(HandyStaticFunctions.getInternetState(this)) {
            new LoadRobots().execute();
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

    public void updateList() {
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        RobotsExpandableAdapter adapter = new RobotsExpandableAdapter(this, robotsGroup);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(childPosition == 4) {
                    startActivityWithExtra(robotsList.get(groupPosition));
                }
                return false;
            }
        });
    }

    /**
     *
     * @param view
     */
    public void connectWithoutUser(View view) {
        float getVelDefault = Float.parseFloat(velDefaultField.getText().toString());
        float getVelMax = Float.parseFloat(velMaxField.getText().toString());
        float getAngularDefault = Float.parseFloat(angDefaultField.getText().toString());
        float getAngularMax = Float.parseFloat(angMaxField.getText().toString());
        String getAddress;
        int getPort;

        if(ipTypeGroup.getCheckedRadioButtonId() == R.id.ipAddress) {
            getAddress = ipBox1Field.getText().toString() + "." + ipBox2Field.getText().toString() + "."
                    + ipBox3Field.getText().toString() + "." + ipBox4Field.getText().toString();

            getPort = Integer.parseInt(portField.getText().toString());
        } else {
            getAddress = urlAddressField.getText().toString();
            getPort = Integer.parseInt(port2Field.getText().toString());
        }

        Robot robot = new Robot(getAddress, getPort, getVelDefault, getVelMax, getAngularDefault, getAngularMax);
        startActivityWithExtra(robot);
    }

    /**
     *
     * @param robot
     */
    public void startActivityWithExtra(Robot robot) {
        RobotController robotController = new RobotController(robot);
        Intent i = new Intent(RobotsActivity.this, MainActivity.class);
        i.putExtra("robotController", robotController);
        startActivity(i);
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.robots, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences loginPref = getSharedPreferences("loginPref", 0);
            SharedPreferences.Editor loginEditor = loginPref.edit();
            loginEditor.clear();
            loginEditor.commit();
            startActivity(new Intent(RobotsActivity.this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param view
     */
    public void addRobot(View view) {
        startActivity(new Intent(RobotsActivity.this, AddRobot.class));
    }

    /**
     *
     */
    public void updateJSONdata() {
        // Initiate the ArrayList to hold the Json data
        robotsList = new ArrayList<>();
        int success;
        // Building parameters for the request

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            SharedPreferences loginPref = getSharedPreferences("loginPref", MODE_PRIVATE);
            username = loginPref.getString(Robot.TAG_USERNAME, "");
            params.add(new BasicNameValuePair(Robot.TAG_USERNAME, username));
            JSONObject jObject = jsonParser.makeHttpRequest(ROBOTS_URL, "POST", params);
            success = jObject.getInt(TAG_SUCCESS);
            robots = jObject.getJSONArray("robots");

            if(success == 1) {
                // loop through all the vehicles
                for(int i = 0; i < robots.length(); i++) {
                    JSONObject obj = robots.getJSONObject(i);

                    // Get each element based on it's tag
                    String robotName = obj.getString(Robot.TAG_ROBOTNAME);
                    String robotUrl = obj.getString(Robot.TAG_URL);
                    int robotPort = obj.getInt(Robot.TAG_PORT);
                    float defaultAngular = (float) obj.getDouble(Robot.TAG_DEFAULT_ANGULAR);
                    float maxAngular = (float) obj.getDouble(Robot.TAG_MAX_ANGULAR);
                    float defaultSpeed = (float) obj.getDouble(Robot.TAG_DEFAULT_VELOCITY);
                    float maxSpeed = (float) obj.getDouble(Robot.TAG_MAX_VELOCITY);

                    Robot robot = new Robot(robotName, robotUrl, robotPort, defaultSpeed, maxSpeed, defaultAngular, maxAngular);
                    robotsList.add(robot);
                    ListRowGroup group = new ListRowGroup(robotName);
                    group.children.add(robotUrl);
                    group.children.add("" + robotPort);
                    group.children.add("Default: " + defaultSpeed + "   |   Max: " + maxSpeed);
                    group.children.add("Default: " + defaultAngular + "    |   Max: " + maxAngular);
                    group.children.add("");
                    robotsGroup.append(i, group);
                }
            } else {
                String noRobots = "There's no Robots yet!! Click the Add Robot Button to add some of your awesome Robots!!";
                Toast.makeText(RobotsActivity.this, noRobots, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     *
     */
    class LoadRobots extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RobotsActivity.this);
            pDialog.setMessage("Loading Robots...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            updateJSONdata();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            updateList();
        }
    }
}
