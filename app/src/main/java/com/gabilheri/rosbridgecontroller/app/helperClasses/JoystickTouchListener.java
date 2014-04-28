package com.gabilheri.rosbridgecontroller.app.helperClasses;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * If you got this code on my GitHub account I'm not responsible for any damage that you have done for your robot
 * This project was created as a School Project and is NOT ERROR PROOF!
 * If you use this code, please use at your own risk. I will not be responsible.
 * Created by marcus on 3/31/14.
 * @author Marcus Gabilheri
 * @version 1.0
 * @since March 2014
 */
public class JoystickTouchListener implements View.OnTouchListener {

    private Joystick joystick;
    private RobotController robotController;
    private int joystickType;
    private boolean debug = false;

    public JoystickTouchListener(Joystick joystick, RobotController robotController, int joystickType) {
        this.joystick = joystick;
        this.robotController = robotController;
        this.joystickType = joystickType;
    }

    public void setRobotController(RobotController robotController) {
        this.robotController = robotController;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(debug) {
          // Log.d("Position: ", "x -> " + motionEvent.getX() + " // y -> " + motionEvent.getY());
        }

        joystick.drawJoystick(motionEvent);

        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (joystickType == Joystick.JOYSTICK_LINER) {
                robotController.commandRobotLinear(Joystick.JOYSTICK_STOPPED);
            } else if (joystickType == Joystick.JOYSTICK_ANGULAR) {
                robotController.commandAngularAndZ(Joystick.JOYSTICK_STOPPED);
            }
        }

        if(robotController != null) {
            if(debug) {
                Log.d("Sending Movement: ", "Movement");
            }
            if (joystick.getJoystickState()) {
                if (joystickType == Joystick.JOYSTICK_LINER) {
                    robotController.commandRobotLinear(joystick.get8axisDirections());
                } else if (joystickType == Joystick.JOYSTICK_ANGULAR) {
                    robotController.commandAngularAndZ(joystick.get8axisDirections());
                }

            }  else {
                if (joystickType == Joystick.JOYSTICK_LINER) {
                    robotController.commandRobotLinear(Joystick.JOYSTICK_STOPPED);
                } else if (joystickType == Joystick.JOYSTICK_ANGULAR) {
                    robotController.commandAngularAndZ(Joystick.JOYSTICK_STOPPED);
                }
            }

        }

        return true;
    }


}
