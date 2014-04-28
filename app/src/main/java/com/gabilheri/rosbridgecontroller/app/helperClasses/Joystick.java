package com.gabilheri.rosbridgecontroller.app.helperClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * If you got this code on my GitHub account I'm not responsible for any damage that you have done for your robot
 * This project was created as a School Project and is NOT ERROR PROOF!
 * If you use this code, please use at your own risk. I will not be responsible.
 * Created by marcus on 3/31/14.
 * @author Marcus Gabilheri
 * @version 1.0
 * @since March 2014
 */
public class Joystick {

    private boolean debug = false;

    // Position Constants
    public static final int JOYSTICK_STOPPED = 0;
    public static final int JOYSTICK_UP = 1;
    public static final int JOYSTICK_DOWN = 2;
    public static final int JOYSTICK_RIGHT = 3;
    public static final int JOYSTICK_LEFT = 4;
    public static final int JOYSTICK_UPRIGHT = 5;
    public static final int JOYSTICK_UPLEFT = 6;
    public static final int JOYSTICK_DOWNLEFT = 7;
    public static final int JOYSTICK_DOWNRIGHT = 8;
    public static final int JOYSTICK_LINER = 9;
    public static final int JOYSTICK_ANGULAR = 10;
    public static final int JOYSTICK_LANDROBOT = 11;

    private int JOYSTICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;

    // Joystick between the Edges of the picture and the joystick.
    private int OFFSET = 0;

    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams layoutParams;
    private int joystickWidth, joystickHeight;
    private int positionX = 0, positionY = 0, minDistance = 0;
    private float distance = 0, angle = 0;

    private boolean isJoystickTouched = false;

    private DrawCanvas drawCanvas;
    private Paint paint;
    private Bitmap joystickImage;

    public Joystick(Context context, ViewGroup mLayout, int joystickImageId) {

        mContext = context;

        joystickImage = BitmapFactory.decodeResource(mContext.getResources(), joystickImageId);
        joystickWidth = joystickImage.getWidth();
        joystickHeight = joystickImage.getHeight();

        drawCanvas = new DrawCanvas(mContext);
        paint = new Paint();

        this.mLayout = mLayout;
        layoutParams = this.mLayout.getLayoutParams();
    }

    public void setOffset(int offset) {
        OFFSET = offset;
    }

    public int getOffset() {
        return OFFSET;
    }

    public boolean getJoystickState() {

        return isJoystickTouched;
    }

    public void setJoystickSize(int width, int height) {
        joystickImage = Bitmap.createScaledBitmap(joystickImage, width, height, false);
        joystickWidth = joystickImage.getWidth();
        joystickHeight = joystickImage.getHeight();
    }

    public void setJoystickWidth(int width) {
        joystickImage = Bitmap.createScaledBitmap(joystickImage, width, joystickHeight, false);
        joystickWidth = joystickImage.getWidth();
    }

    public void setJoystickHeight(int height) {
        joystickImage = Bitmap.createScaledBitmap(joystickImage, joystickWidth, height, false);
        joystickHeight = joystickImage.getHeight();
    }

    public int[] getPosition() {
        if(distance > minDistance && isJoystickTouched) {
            return new int[] {positionX, positionY};
        }

        return new int[] {0, 0};
    }

    public int getPositionX() {
        if(distance > minDistance && isJoystickTouched) {
            return positionX;
        }
        return 0;
    }

    public int getPositionY() {
        if(distance > minDistance && isJoystickTouched) {
            return positionY;
        }
        return 0;
    }

    public float getAngle() {
        if(distance > minDistance && isJoystickTouched) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        if(distance > minDistance && isJoystickTouched) {
            return distance;
        }
        return 0;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    /**
     * Method to draw the joystick on the screen.
     * Uses the X and Y position of the touchEvent.
     * @param motionEvent the onTouch motionEvent
     */
    public void drawJoystick(MotionEvent motionEvent) {
        positionX = (int) (motionEvent.getX() - (layoutParams.width / 2));
        positionY = (int) (motionEvent.getY() - (layoutParams.height / 2));
        distance = (float) Math.sqrt(Math.pow(positionX, 2) + Math.pow(positionY, 2));
        angle = (float) calibrationAngle(positionX, positionY);

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            if(debug) {
                Log.d("Distance: ", "" + distance);
                Log.d("Layout Params: ", "" + ((layoutParams.width / 2) - OFFSET));
                Log.d("Angle", "" + angle);
                Log.d("Position X: ", "" + positionX);
                Log.d("Position Y: ", "" + positionY);
            }
            // Always check if the position is inside the joystick
            if(distance <= (layoutParams.width /2) - OFFSET) {
                Log.d("Where: " , "Inside drawing!");
                drawCanvas.setPosition(motionEvent.getX(), motionEvent.getY());
                draw();
                isJoystickTouched = true;
            }
        } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE && isJoystickTouched) {
            if(debug) {
                Log.d("Distance: ", "" + distance);
                Log.d("Layout Params: ", "" + ((layoutParams.width / 2) - OFFSET));
                Log.d("Angle", "" + angle);
                Log.d("Position X: ", "" + positionX);
                Log.d("Position Y: ", "" + positionY);
            }
            if(distance <= (layoutParams.width /2) - OFFSET) {
                drawCanvas.setPosition(motionEvent.getX(), motionEvent.getY());
                draw();
            } else if(distance > (layoutParams.width / 2) - OFFSET) {
                float x = (float) (Math.cos(Math.toRadians(calibrationAngle(positionX, positionY))) * ((layoutParams.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(calibrationAngle(positionX, positionY))) * ((layoutParams.width / 2) - OFFSET));

                x += (layoutParams.width / 2);
                y += (layoutParams.height / 2);
                drawCanvas.setPosition(x, y);
                draw();
            } else {
                mLayout.removeView(drawCanvas);
            }
        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            mLayout.removeView(drawCanvas);
            isJoystickTouched = false;
        }
    }

    public int get8axisDirections() {
        if(distance > minDistance && isJoystickTouched) {
            if(angle >= 247.5 && angle < 292.5) {
                Log.d("Joystick Position: ", "UP");
                return JOYSTICK_UP;
            } else if(angle >= 292.5 && angle < 337.5) {
                return JOYSTICK_UPRIGHT;
            } else if(angle >= 337.5 || angle < 22.5) {
                Log.d("Joystick Position: ", "Right");
                return JOYSTICK_RIGHT;
            } else if(angle >= 22.5 && angle < 67.5) {
                return JOYSTICK_DOWNRIGHT;
            } else if(angle >= 67.5 && angle < 112.5) {
                Log.d("Joystick Position: ", "Down");
                return JOYSTICK_DOWN;
            } else if(angle >= 112.5 && angle < 157.5) {
                return JOYSTICK_DOWNLEFT;
            } else if(angle >= 157.5 && angle < 202.5) {
                Log.d("Joystick Position: ", "Left");
                return JOYSTICK_LEFT;
            } else if(angle >= 202.5 && angle < 247.5) {
                return JOYSTICK_UPLEFT;
            }
        } else if(distance <= minDistance && isJoystickTouched) {
            return JOYSTICK_STOPPED;
        }
        return 0;
    }

    public int get4axisDirection() {
        if(distance > minDistance && isJoystickTouched) {
            if(angle >= 225 && angle < 315) {
                Log.d("Joystick Position: ", "UP");
                return JOYSTICK_UP;
            } else if(angle >= 315 || angle < 45) {
                Log.d("Joystick Position: ", "Right");
                return JOYSTICK_RIGHT;
            } else if(angle >= 45 && angle < 315) {
                Log.d("Joystick Position: ", "Down");
                return JOYSTICK_DOWN;
            } else if(angle >= 135 && angle < 225) {
                Log.d("Joystick Position: ", "Left");
                return JOYSTICK_LEFT;
            }
        } else if(distance <= minDistance && isJoystickTouched) {
            return JOYSTICK_STOPPED;
        }
        return 0;
    }

    public void setJoystickAlpha(int alpha) {
        JOYSTICK_ALPHA = alpha;

        if(paint != null) {
            paint.setAlpha(alpha);
        }
    }

    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        if(mLayout.getBackground() != null) {
            mLayout.getBackground().setAlpha(alpha);
        }
    }

    public int getJoystickAlpha() {
        return JOYSTICK_ALPHA;
    }

    public int getLayoutAlpha() {
        return LAYOUT_ALPHA;
    }

    public int getJoystickWidth() {
        return joystickWidth;
    }

    public int getJoystickHeight() {

        return joystickHeight;
    }

    public void setLayoutSize(int width, int height) {
        layoutParams.width = width;
        layoutParams.height = height;
    }

    public int getLayoutWidth() {
        return layoutParams.width;
    }

    public int getLayoutHeight() {
        return layoutParams.height;
    }

    private void draw() {
        try {
            mLayout.removeView(drawCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLayout.addView(drawCanvas);

    }

    private double calibrationAngle(float x, float y) {
        if(x>= 0 && y>= 0) {
            return Math.toDegrees(Math.atan(y /x));
        } else if(x < 0 && y >= 0) {
            return Math.toDegrees(Math.atan(y / x)) + 180;
        } else if(x < 0 && y < 0) {
            return Math.toDegrees(Math.atan(y / x)) + 180;
        } else if(x >= 0 && y < 0) {
            return Math.toDegrees(Math.atan(y / x)) + 360;
        }
        return 0;
    }

    private class DrawCanvas extends View {

        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(joystickImage, x, y, paint);
        }

        private void setPosition(float positionX, float positionY) {
            x = positionX - (joystickWidth / 2);
            y = positionY - (joystickHeight / 2);
        }
    }
}
