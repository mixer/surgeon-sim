package pro.beam.games.surgeonsim;

import java.awt.*;

public class ControlRobot {
    private static Robot rob;

    public static Robot getRobot() {
        if (rob == null) {
            try {
                rob = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        return rob;
    }
}
