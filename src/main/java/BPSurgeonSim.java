import pro.beam.api.BeamAPI;
import pro.beam.interactive.event.EventListener;
import pro.beam.interactive.net.packet.Protocol;
import pro.beam.interactive.net.packet.Protocol.ProgressUpdate;
import pro.beam.interactive.robot.Robot;
import pro.beam.interactive.robot.RobotBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.Key;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by your mom on 04/01/16.
 */
public class BPSurgeonSim {
    //These details are used to get past lab HTTP auth.
    private static final String httpUser = "beampro";
    private static final String httpPass = "11679";

    //These should be filled in with your Beam details
    private static String username;
    private static String password;
    private static int channelId;
    private static double sensitivity = 0.2;
    private static boolean lab = false, mouse = false, keyboard = false, updateQuorum = false, debug = false;

    private static int currentQuorum = 0;

    private static final double THRESHOLD = 0.5;

    private static final StatusListener statusListener = new StatusListener();

    public static void main(String[] args) throws Exception {
        for (String str : args) {
            if (str.equals("-k")) keyboard = true;
            if (str.equals("-m")) mouse = true;
            if (str.startsWith("-c")) channelId = Integer.parseInt(str.replace("-c", ""));
            if (str.startsWith("-s")) sensitivity = Double.parseDouble(str.replace("-s", ""));
            if (str.equals("--lab")) lab = true;
            if (str.equals("--debug")) debug = true;
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("QU");
                updateQuorum = true;
            }
        }, 0L, 30000L);
        if (debug) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (KeyboardController.getInstance() != null)
                        KeyboardController.getInstance().printDebug();
                }
            }, 0L, 2000L);
        }

        if (mouse) {
            MouseController.init(sensitivity);
        }
        if (keyboard) {
            KeyboardController.init();
        }

        BeamAPI beam;

        String[] creds = getCredentials();
        username = creds[0];
        password = creds[1];

        if (lab) {
            beam = new BeamAPI(URI.create("https://lab.beam.pro/api/v1/"), httpUser, httpPass);
        } else {
            beam = new BeamAPI();
        }

        final Robot robot = new RobotBuilder().username(username).password(password).channel(channelId).build(beam).get();

        //Listen for report events on the robot.
        robot.on(Protocol.Report.class, new EventListener<Protocol.Report>() {
            public void handle(Protocol.Report report) {
                if (updateQuorum) {
                    System.out.println("Quorum update, start: " + currentQuorum);
                    updateQuorum = false;
                    KeyboardController.getInstance().reset();
                    currentQuorum = report.getQuorum();
                    System.out.println("now: " + currentQuorum);
                }
//                System.out.println("REPORT " + report.getQuorum() + report.getConnected());
                ProgressUpdate.Builder builder = ProgressUpdate.newBuilder();

                if (KeyboardController.getInstance() != null) {
                    //Iterate over all the tactile actions performed in the report
                    for (Protocol.Report.TactileInfo tactile : report.getTactileList()) {
                        int keyCode = tactile.getCode();
                        int pressed = 0;

                        //Check if more than 50% of people were holding it down.
                        boolean passedThreshold = tactile.getDown().getMean() > THRESHOLD;

//                        System.out.println("Holding " + (tactile.getDown().getFrequency()) + " quorum: " + report.getQuorum() + " Connected: " + report.getConnected());
                        boolean changed;
                        if (keyCode == 0 && mouse) {
                            changed = MouseController.getInstance().setLMB(passedThreshold);
                        } else {
                            pressed = KeyboardController.getInstance().diffPress(keyCode, (int) (tactile.getDown().getFrequency() - tactile.getUp().getFrequency()));

                            // Quorum is either the current quorum, the quorum from the report, or the max held down buttons
                            currentQuorum = Math.max(KeyboardController.getInstance().getMax(), Math.max(currentQuorum, report.getQuorum()));

                            changed = KeyboardController.getInstance().setMovement(keyCode, ((float) pressed) / ((float) currentQuorum) >= THRESHOLD);
                        }

//                        System.out.println("Tactile " + tactile.getCode() + " " + changed);

                        if (currentQuorum == 0) {
                            currentQuorum = 1;
                        }

                        float progress = (float) pressed / (float) currentQuorum;
                        if (progress < 0) {
                            progress = 0;
                        }
                        if (progress >= 1) {
                            progress = (float) 0.99;
                        }

                        builder.addProgress(builder.getProgressCount(),
                                ProgressUpdate.Progress
                                        .newBuilder()
                                        .setCode(keyCode)
                                        .setFired(passedThreshold && changed)
                                        .setTarget(ProgressUpdate.Progress.TargetType.TACTILE)
                                        .setProgress(progress));
                    }
                }

                if (MouseController.getInstance() != null) {
                    for (Protocol.Report.JoystickInfo joystickInfo : report.getJoystickList()) {
//                        System.out.println(joystickInfo.getInfo().getMean());
                        if (joystickInfo.getAxis() == 0) {
                            MouseController.getInstance().setMouseX(joystickInfo.getInfo().getMean());
                        } else if (joystickInfo.getAxis() == 1) {
                            MouseController.getInstance().setMouseY(joystickInfo.getInfo().getMean());
                        }
                    }
                    if (report.getJoystickCount() > 0) {
                        MouseController.getInstance().moveMouse();
                    }
                }

                if (builder.getProgressCount() == 0) return;
                try {
                    robot.write(builder.build());
                } catch (IOException ex) {
                    System.err.println("Failed to send packet.");
                    ex.printStackTrace();
                }

            }
        });

        System.out.println("READY FOR TETRIS!");
    }

    public static String[] getCredentials() {
        // The name of the file to open.
        String fileName = "credentials.txt";

        String creds = "";

        try {
            // Use this for reading the data.
            byte[] buffer = new byte[1000];

            FileInputStream inputStream =
                    new FileInputStream(fileName);

            int total = 0;
            int nRead = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                creds += new String(buffer);
                total += nRead;
            }

            // Always close files.
            inputStream.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }

        return creds.split(System.lineSeparator());
    }

    public static StatusListener getStatusListener() {
        return statusListener;
    }
}
