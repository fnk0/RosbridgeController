package com.gabilheri.rosbridgecontroller.app.helperClasses;

/**
 * If you got this code on my GitHub account I'm not responsible for any damage that you have done for your robot
 * This project was created as a School Project and is NOT ERROR PROOF!
 * If you use this code, please use at your own risk. I will not be responsible.
 * Created by marcus on 3/31/14.
 * @author Marcus Gabilheri
 * @version 1.0
 * @since March 2014
 */
public class Robot {

    private String robotName, robotURL;
    private int robotPort;
    private float defaultVel, maxVel, defaultAng, maxAng;
    private int id;
    public static final String TAG_USERNAME = "username";
    public static final String TAG_ROBOTNAME = "robot_name";
    public static final String TAG_URL = "robot_url";
    public static final String TAG_PORT = "robot_port";
    public static final String TAG_DEFAULT_ANGULAR = "default_angular";
    public static final String TAG_MAX_ANGULAR = "max_angular";
    public static final String TAG_DEFAULT_VELOCITY = "default_velocity";
    public static final String TAG_MAX_VELOCITY = "max_velocity";

    public Robot() {
    }

    public Robot(String robotName, String robotURL, int robotPort, float defaultVel, float maxVel, float defaultAng, float maxAng) {
        this.robotName = robotName;
        this.robotURL = robotURL;
        this.robotPort = robotPort;
        this.defaultVel = defaultVel;
        this.maxVel = maxVel;
        this.defaultAng = defaultAng;
        this.maxAng = maxAng;
    }

    public Robot(String robotURL, int robotPort, float defaultVel, float maxVel, float defaultAng, float maxAng) {
        this.robotName = "Default Robot";
        this.robotURL = robotURL;
        this.robotPort = robotPort;
        this.defaultVel = defaultVel;
        this.maxVel = maxVel;
        this.defaultAng = defaultAng;
        this.maxAng = maxAng;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public String getRobotURL() {
        return robotURL;
    }

    public void setRobotURL(String robotURL) {
        this.robotURL = robotURL;
    }

    public float getDefaultVel() {
        return defaultVel;
    }

    public void setDefaultVel(float defaultVel) {
        this.defaultVel = defaultVel;
    }

    public float getMaxVel() {
        return maxVel;
    }

    public void setMaxVel(float maxVel) {
        this.maxVel = maxVel;
    }

    public float getDefaultAng() {
        return defaultAng;
    }

    public void setDefaultAng(float defaultAng) {
        this.defaultAng = defaultAng;
    }

    public float getMaxAng() {
        return maxAng;
    }

    public void setMaxAng(float maxAng) {
        this.maxAng = maxAng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRobotPort() {
        return robotPort;
    }

    public void setRobotPort(int robotPort) {
        this.robotPort = robotPort;
    }

    @Override
    public String toString() {
        return "Robot Name: " + robotName;
    }
}
