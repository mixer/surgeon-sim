package pro.beam.games.surgeonsim;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;

public class MouseController implements Observer {

    public static final double deadzone = 0.10;
    private static MouseController mCtrl;
    private Robot robot;
    private boolean LMB = false;
    private int minWidth;
    private int minHeight;
    private int maxWidth;
    private int maxHeight;
    private Random random;
    private int width;
    private int height;
    private int pixX;
    private int pixY;
    private double sensitivity;

    public MouseController(double sens) {
        this.sensitivity = sens;
        this.robot = ControlRobot.getRobot();
        this.random = new Random();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();

        minWidth = (int) (width * deadzone);
        minHeight = (int) (height * deadzone);

        maxWidth = width - minWidth;
        maxHeight = height - minHeight;

        System.out.format("Screen resolution: %dx%d\n", width, height);
        System.out.format("Border deadzone of %d%%, (minW: %d, minH: %d, maxW: %d, maxH: %d)\n", (int) (deadzone * 100), minWidth, minHeight, maxWidth, maxHeight);
        System.out.println("Mouse movement box of " + sens * 100 + "% in the center of the screen.");
    }

    public static void init(double sens) {

        mCtrl = new MouseController(sens);
    }

    public static MouseController getInstance() {
        return mCtrl;
    }

    public void setMouseX(double relativeX) {
        pixX = ((Double) Math.floor(getRelative(relativeX) * width)).intValue();
        if (pixX < minWidth) pixX = minWidth;
        if (pixX > maxWidth) pixX = maxWidth;
    }

    public void setMouseY(double relativeY) {
        pixY = ((Double) Math.floor(getRelative(relativeY) * height)).intValue();
        if (pixY < minHeight) pixY = minHeight;
        if (pixY > maxHeight) pixY = maxHeight;
    }

    private double getRelative(double relative) {
        if (relative > 1.0) relative = 1.0;
        relative -= 0.5;
        relative *= sensitivity;
        return relative + 0.5;
    }

    public boolean moveMouse() {
        if (!BPSurgeonSim.getStatusListener().getActive()) {
            return false;
        }
        robot.mouseMove(pixX, pixY);
        return true;
    }

    public boolean setLMB(boolean activate) {
        if (!BPSurgeonSim.getStatusListener().getActive()) {
            return false;
        }
        if (activate == LMB) {
            return false;
        }
        if (activate) {
            LMB = true;
            robot.mousePress(InputEvent.BUTTON1_MASK);

        } else {
            LMB = false;
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

        }
        return true;
    }

    public void shakeMouse() {
        System.out.println("I'd like my mouse shaken, not stirred.");

        final Timer t = new Timer();

        // Move the mouse randomly every 200ms
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                randomDirection();
            }
        }, 0, 400);

        // Stop moving after 3sec
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                t.cancel();
            }
        }, 5000);
    }

    public void randomDirection() {
        setMouseX(random.nextDouble());
        setMouseY(random.nextDouble());

        moveMouse();
    }

    public void update(Observable o, Object arg) {
        if (o.getClass().equals(StatusListener.class)) {
            StatusListener lis = (StatusListener) o;
            // If the listener will be disabled, disable all keypresses
            if (!lis.getActive()) {
                setLMB(false);
            }
        }
    }
}
