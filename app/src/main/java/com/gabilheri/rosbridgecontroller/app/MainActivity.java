package com.gabilheri.rosbridgecontroller.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.gabilheri.rosbridgecontroller.app.VideoStreamClasses.MjpegInputStream;
import com.gabilheri.rosbridgecontroller.app.VideoStreamClasses.MjpegView;
import com.gabilheri.rosbridgecontroller.app.helperClasses.Joystick;
import com.gabilheri.rosbridgecontroller.app.helperClasses.JoystickTouchListener;
import com.gabilheri.rosbridgecontroller.app.helperClasses.RobotController;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.net.URI;

/**
 *  @author Marcus Gabilheri
 *  @version 1.0
 *  @since April, 2014
 */
public class MainActivity extends Activity {

    /**
     *
     */
    private static final String TAG = "MainActivity";
    private MjpegView videoView;
    private String cameraUrl;
    private RobotController robotController = null;
    private RelativeLayout joystickLayoutLinear, joystickLayoutAngular;
    private SeekBar controlVel, controlAngular;
    private Joystick joystickLinear, joystickAngular;
    private JoystickTouchListener joystickTouchListenerLinear, joystickTouchListenerAngular;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //sample public cam
        setContentView(R.layout.activity_main);

        robotController = getIntent().getExtras().getParcelable("robotController");
        //cameraUrl = "http://139.78.141.250:8080/stream?topic=/camera/rgb/image_color";
        Log.d("Camera URL: ", robotController.getStreamUrl());
        cameraUrl = robotController.getStreamUrl();

        joystickLayoutLinear = (RelativeLayout) findViewById(R.id.joystickLayoutLinear);
        joystickLinear = new Joystick (getApplicationContext(), joystickLayoutLinear, R.drawable.joystick);
        joystickLinear.setJoystickSize(150, 150);
        joystickLinear.setLayoutSize(500, 500);
        joystickLinear.setLayoutAlpha(150);
        joystickLinear.setJoystickAlpha(200);
        joystickLinear.setOffset(90);
        joystickLinear.setMinDistance(50);
        joystickTouchListenerLinear = new JoystickTouchListener(joystickLinear, robotController, Joystick.JOYSTICK_LINER);
        joystickLayoutLinear.setOnTouchListener(joystickTouchListenerLinear);

        joystickLayoutAngular = (RelativeLayout) findViewById(R.id.joystickLayoutAngular);
        joystickAngular = new Joystick (getApplicationContext(), joystickLayoutAngular, R.drawable.joystick);
        joystickAngular.setJoystickSize(150, 150);
        joystickAngular.setLayoutSize(500, 500);
        joystickAngular.setLayoutAlpha(150);
        joystickAngular.setJoystickAlpha(200);
        joystickAngular.setOffset(90);
        joystickAngular.setMinDistance(50);
        joystickTouchListenerAngular = new JoystickTouchListener(joystickAngular, robotController, Joystick.JOYSTICK_ANGULAR);
        joystickLayoutAngular.setOnTouchListener(joystickTouchListenerAngular);

        controlVel = (SeekBar) findViewById(R.id.seekBarVel);
        float maxVel = robotController.getMaxVel() * 100;
        float currentVel = robotController.getCurrentVelocity() * 100;
        controlVel.setMax((int) maxVel);
        controlVel.setProgress((int) currentVel);

        controlAngular = (SeekBar) findViewById(R.id.seekBarAngular);
        float maxAng = robotController.getMaxAng() * 100;
        float currentAng = robotController.getCurrentAngular() * 100;
        controlAngular.setMax((int)maxAng);
        controlAngular.setProgress((int)currentAng);

        controlVel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Progress Vel: ", "" + progress);
                float speed = (float) progress / 100;
                robotController.setCurrentVelocity(speed);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        controlAngular.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float angular =  (float) progress / 100;
                Log.d("Progress Ang: ", "" + progress);
                robotController.setCurrentAngular(angular);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        establishConnection();
        videoView = (MjpegView) findViewById(R.id.mv);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        new DoRead().execute(cameraUrl);
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    /**
     *
     * @param view
     */
    public void doCommand(View view) {
        if(robotController != null) {
            robotController.getCommand(view);
        }
    }

    /**
     *
     */
    public void establishConnection()  {

        if(robotController.getRobotUrl() != null && robotController.getPort() != 0) {
            Log.d("Attempt to Connect", "Start!");
            Log.d("URL: ", robotController.getRobotUrl());
            Log.d("Port: ", "" + robotController.getPort());
            robotController.connect();

            joystickTouchListenerLinear.setRobotController(robotController);
            joystickTouchListenerAngular.setRobotController(robotController);
        }
    }

    /**
     *
     */
    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

        /**
         *
         * @param url
         * @return
         */
        protected MjpegInputStream doInBackground(String... url) {
            HttpResponse res;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }
            return null;
        }

        /**
         *
         * @param result
         */
        protected void onPostExecute(MjpegInputStream result) {
            videoView.setSource(result);
            videoView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            videoView.showFps(true);
        }
    }

}
