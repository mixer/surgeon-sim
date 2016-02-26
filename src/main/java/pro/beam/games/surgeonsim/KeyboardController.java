package pro.beam.games.surgeonsim;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/*
    This class basically just wraps Java's Robot interface to allow for key control.
 */
public class KeyboardController implements Observer {
    private static KeyboardController kbCtrl;
    private Robot robot;
    private HashMap<Integer, Boolean> status;
    private HashMap<Integer, Integer> presses;

    public KeyboardController() {
        this.status = new HashMap<Integer, Boolean>();
        this.presses = new HashMap<Integer, Integer>();
        for (Integer i : KeyMap.getInstance().getAllowed()) {
            this.status.put(i, false);
            this.presses.put(i, 0);
        }

        this.robot = ControlRobot.getRobot();

        BPSurgeonSim.getStatusListener().addObserver(this);
    }

    public static void init() {
        kbCtrl = new KeyboardController();
    }

    public static KeyboardController getInstance() {
        return kbCtrl;
    }


    public boolean setMovement(int keyCode, boolean activate) {
        if (!BPSurgeonSim.getStatusListener().getActive()) {
            return false;
        }
        if (KeyMap.getInstance().isKeyAllowed(keyCode)) {
            if (activate == status.get(keyCode)) {
                return false;
            }
            if (activate) {
                System.out.println("Pushing " + keyCode);
                this.status.put(keyCode, true);
                robot.keyPress(keyCode);
            } else {
                System.out.println("Unpushing " + keyCode);
                this.status.put(keyCode, false);
                robot.keyRelease(keyCode);
            }
            return true;
        }
        return false;
    }

    public void printDebug() {
        System.out.println("== CURRENT ==");
        for (Integer i : KeyMap.getInstance().getAllowed()) {
            System.out.printf("Key: %d currently: %s, pressing: %d\n", i, this.status.get(i) ? "pressed" : "released", this.presses.get(i));
        }
        System.out.println("=============");
    }

    public void update(Observable o, Object arg) {
        if (o.getClass().equals(StatusListener.class)) {
            StatusListener lis = (StatusListener) o;
            // If the listener will be disabled, disable all key presses
            if (!lis.getActive()) {
                for (Map.Entry<Integer, Boolean> entry : status.entrySet()) {
                    if (entry.getValue()) {
                        robot.keyRelease(entry.getKey());
                        status.put(entry.getKey(), false);
                    }
                }
            }
        }
    }
}
