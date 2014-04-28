package com.gabilheri.rosbridgecontroller.app.helperClasses;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import com.gabilheri.rosbridgecontroller.app.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * If you got this code on my GitHub account I'm not responsible for any damage that you have done for your robot
 * This project was created as a School Project and is NOT ERROR PROOF!
 * If you use this code, please use at your own risk. I will not be responsible.
 * Created by marcus on 3/31/14.
 * @author Marcus Gabilheri
 * @version 1.0
 * @since March 2014
 */
public class RobotController implements Parcelable {

    private String robotUrl;
    private Socket socket;
    private int port;
    private PrintWriter out;
    private boolean connected = false;
    private float currentVelocity, currentAngular, intervalVel, maxVel, intervalAng, maxAng;
    private ConnectionThread linearThread = null, angularThread = null, commandThread = null;
    private double xL, yL, zL, aL;
    private boolean debug = true, stopComandLinear = false, stopCommandAngular = false;
    private Robot robot;

    // String Constants
    private String takeOff = "{\"op\":\"publish\",\"topic\":\"/ardrone/takeoff\",\"msg\":{}}";
    private String stop = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":0," +
            "\"y\":0,\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":0}}}";
    private String land = "{\"op\":\"publish\",\"topic\":\"/ardrone/land\",\"msg\":{}}";

    public RobotController() {
    }

    public RobotController(Robot robot) {
        this.robot = robot;
        this.robotUrl = robot.getRobotURL();
        this.port = robot.getRobotPort();
        this.currentVelocity = robot.getDefaultVel();
        this.currentAngular = robot.getDefaultAng();
        this.maxVel = robot.getMaxVel();
        this.maxAng = robot.getMaxAng();
    }

    public RobotController(String robotUrl, int port) {
        this.robotUrl = robotUrl;
        this.port = port;
        this.currentVelocity = (float) 0.25;
        this.currentAngular = (float) 1.0;
    }

    public static final Parcelable.Creator<RobotController> CREATOR
            = new Parcelable.Creator<RobotController>() {
        public RobotController createFromParcel(Parcel in) {
            return new RobotController(in);
        }
        public RobotController[] newArray(int size) {
            return new RobotController[size];
        }
    };

    public RobotController(Parcel in) {
        this.robotUrl = in.readString();
        this.port = in.readInt();
        this.currentVelocity = in.readFloat();
        this.currentAngular = in.readFloat();
        this.intervalVel = in.readFloat();
        this.maxVel = in.readFloat();
        this.intervalAng = in.readFloat();
        this.maxAng = in.readFloat();
    }

    public void connect() {
        if(!connected) {
            if(robotUrl != null) {
                Log.d("Connecting!", "Start Connection!");
                Thread cThread = new Thread(new ConnectionThread("hello robot"));
                cThread.start();
            }
        }
    }

    public void getCommand(View view) {
        int id = view.getId();
        if(id == R.id.takeOff) {
            Log.d("Executing Command: ", "Take Off");
            sendCommandToRobot(takeOff);
        } else if(id == R.id.stop) {
            Log.d("Executing Command: ", "Stop");
            sendCommandToRobot(stop);
        } else if(id == R.id.land) {
            Log.d("Executing Command: ", "Land");
            sendCommandToRobot(land);
        }
    }

    /**
     *
     * @param direction the direction on which the robot should move based on the Joystick input
     */
    public void commandRobotLinear(int direction) {
        switch (direction) {
            case Joystick.JOYSTICK_UP:
                setxL(currentVelocity);
                Log.d("Command: ", "UP");
                break;
            case Joystick.JOYSTICK_UPLEFT:
                setxL(currentVelocity);
                setyL(currentVelocity);
                Log.d("Command: ", "UPLEFT");
                break;
            case Joystick.JOYSTICK_UPRIGHT:
                setxL(currentVelocity);
                setyL(-currentVelocity);
                Log.d("Command: ", "UPRIGHT");
                break;
            case Joystick.JOYSTICK_DOWN:
                setxL(-currentVelocity);
                Log.d("Command: ", "DOWN");
                break;
            case Joystick.JOYSTICK_DOWNLEFT:
                setxL(-currentVelocity);
                setyL(currentVelocity);
                Log.d("Command: ", "DOWNLEFT");
                break;
            case Joystick.JOYSTICK_DOWNRIGHT:
                setxL(-currentVelocity);
                setyL(-currentVelocity);
                Log.d("Command: ", "DOWNRIGHT");
                break;
            case Joystick.JOYSTICK_LEFT:
                setyL(currentVelocity);
                Log.d("Command: ", "LEFT");
                break;
            case Joystick.JOYSTICK_RIGHT:
                setyL(-currentVelocity);
                Log.d("Command: ", "RIGHT");
                break;
            case Joystick.JOYSTICK_STOPPED:
                stopComandLinear = true;
                setxL(0);
                setyL(0);
                Log.d("Command: ", "STOP LINEAR");
                break;
        }
        moveRobot();
    }

    public void commandAngularAndZ(int direction) {

        switch (direction) {
            case Joystick.JOYSTICK_UP:
                setzL(currentVelocity);
                break;
            case Joystick.JOYSTICK_DOWN:
                setzL(-currentVelocity);
                break;
            case Joystick.JOYSTICK_LEFT:
               setaL(currentAngular);
                break;
            case Joystick.JOYSTICK_RIGHT:
                setaL(-currentAngular);
                break;
            case Joystick.JOYSTICK_STOPPED:
                Log.d("Command: ", "STOP ANGULAR");
                stopCommandAngular = true;
                setzL(0);
                setaL(0);
                break;
        }
        moveRobot();
    }

    public void moveRobot() {

        String movement = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + getxL() + "," +
                "\"y\":" + getyL() + ",\"z\":" + getzL() +"},\"angular\":{\"x\":0,\"y\":0,\"z\":" + getaL() +"}}}";
        if(getxL() != 0 || getyL() != 0) {
            if(debug) {
                Log.d("Direction: ", "Sending Linear Command!!");
            }
            sendLinearToRobot(movement);
        } else if(getzL() != 0 || getaL() != 0) {
            if(debug) {
                Log.d("Direction: ", "Sending Angular Command!!");
            }
            sendAngularToRobot(movement);
        } else {
            if(debug) {
                Log.d("Direction: ", "Sending Command!!");
            }
            sendLinearToRobot(movement);
        }
    }

    public void sendLinearToRobot(String command) {

        if(linearThread == null) {
            if(debug) {
                Log.d("Thread: ", "Starting a new Linear thread...");
            }
            linearThread  = new ConnectionThread(command);
            linearThread.start();
        } else {
            if(debug) {
                Log.d("Thread: ", "Running an existing Linear thread...");
            }
            linearThread.setCommand(command);
            linearThread.run();
        }
    }

    public void sendAngularToRobot(String command) {

        if(angularThread == null) {
            if(debug) {
                Log.d("Thread: ", "Starting a new Angular thread...");
            }
            angularThread = new ConnectionThread(command);
            angularThread.start();
        } else {
            if(debug) {
                Log.d("Thread: ", "Running an existing Angular thread...");
            }
            angularThread.setCommand(command);
            angularThread.run();
        }
    }

    public void sendCommandToRobot(String command) {
        if(commandThread == null) {
            if(debug) {
                Log.d("Thread: ", "Starting a new Command thread...");
            }
            commandThread = new ConnectionThread(command);
            commandThread.start();
        } else {
            if(debug) {
                Log.d("Thread: ", "Running an existing Command thread...");
            }
            commandThread.setCommand(command);
            commandThread.run();
        }
    }

    public double getxL() {
        return xL;
    }

    public void setxL(double xL) {
        this.xL = xL;
    }

    public double getyL() {
        return yL;
    }

    public void setyL(double yL) {
        this.yL = yL;
    }

    public double getzL() {
        return zL;
    }

    public void setzL(double zL) {
        this.zL = zL;
    }

    public double getaL() {
        return aL;
    }

    public void setaL(double aL) {
        this.aL = aL;
    }

    public class ConnectionThread extends Thread implements Runnable {
        String command;

        public ConnectionThread() {

        }
        public ConnectionThread(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            try {
                if(!connected) {
                    InetAddress robotAddress = InetAddress.getByName(robotUrl);
                    Log.d("Connection Type:", " Connecting....");
                    socket = new Socket(robotAddress, port);
                    out = new PrintWriter(socket.getOutputStream());
                    connected = true;
                }
                out.print(command);
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }

    public String getRobotUrl() {
        return robotUrl;
    }

    public void setRobotUrl(String robotUrl) {
        this.robotUrl = robotUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public float getCurrentVelocity() {
        return currentVelocity;
    }

    public void setCurrentVelocity(float currentVelocity) {
        this.currentVelocity = currentVelocity;
        Log.d("Current Velocity: ", "" + currentVelocity);
    }

    public float getCurrentAngular() {
        return currentAngular;
    }

    public void setCurrentAngular(float currentAngular) {
        this.currentAngular = currentAngular;
        Log.d("Current Angular: ", "" + currentAngular);
    }

    public float getIntervalVel() {
        return intervalVel;
    }

    public float getMaxVel() {
        return maxVel;
    }

    public void setMaxVel(float maxVel) {
        this.maxVel = maxVel;
    }

    public float getIntervalAng() {
        return intervalAng;
    }


    public float getMaxAng() {
        return maxAng;
    }

    public void setMaxAng(float maxAng) {
        this.maxAng = maxAng;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public String getStreamUrl() {
        return "http://" + getRobotUrl() + ":" + getPort() + "/stream?topic=/camera/rgb/image_color";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getRobotUrl());
        parcel.writeInt(getPort());
        parcel.writeFloat(getCurrentVelocity());
        parcel.writeFloat(getCurrentAngular());
        parcel.writeFloat(getIntervalVel());
        parcel.writeFloat(getMaxVel());
        parcel.writeFloat(getIntervalAng());
        parcel.writeFloat(getMaxAng());
    }
}
