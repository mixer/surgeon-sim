import java.awt.*;

/**
 * Created by jamy on 04/01/16.
 */
public class ControlRobot {
    private static Robot rob;

    public static Robot getRobot() {
        if (rob == null) {
            try {
                rob = new Robot();

            } catch (Exception e) {}
        }

        return rob;
    }
}
