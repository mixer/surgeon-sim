package pro.beam.games.surgeonsim;

import pro.beam.api.BeamAPI;
import pro.beam.interactive.event.EventListener;
import pro.beam.interactive.net.packet.Protocol;
import pro.beam.interactive.net.packet.Protocol.ProgressUpdate;
import pro.beam.interactive.robot.Robot;
import pro.beam.interactive.robot.RobotBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BPSurgeonSim {
    private static final StatusListener statusListener = new StatusListener();
    private static int channelId;
    private static double sensitivity = 0.22;
    private static boolean mouse = false, keyboard = false, updateQuorum = false, debug = false, shake = true;

    public static void main(String[] args) throws Exception {
        for (String str : args) {
            if (str.equals("-k")) keyboard = true;
            if (str.equals("-m")) mouse = true;
            if (str.equals("-d")) debug = true;
            if (str.equals("--noshake")) shake = false;
            if (str.startsWith("-c")) channelId = Integer.parseInt(str.replace("-c", ""));
            if (str.startsWith("-s")) sensitivity = Double.parseDouble(str.replace("-s", ""));
        }

        if (mouse || shake) {
            MouseController.init(sensitivity, shake);
        }
        if (keyboard) {
            KeyboardController.init();
        }

        BeamAPI beam;

        String[] creds = getCredentials();
        String username = creds[0];
        String password = creds[1];

        beam = new BeamAPI();

        final Robot robot = new RobotBuilder().username(username).password(password).channel(channelId).build(beam).get();

        //Listen for report events on the robot.
        robot.on(Protocol.Report.class, new EventListener<Protocol.Report>() {
            public void handle(Protocol.Report report) {
                if (keyboard) {
                    for (Protocol.Report.TactileInfo tInfo : report.getTactileList()) {
                        KeyMap.getInstance().handleInput(tInfo, report.getUsers());
                    }
                }

                ProgressUpdate pu = KeyMap.getInstance().getProgressUpdate();

                try {
                    robot.write(pu);
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

            FileInputStream inputStream = new FileInputStream(fileName);

            while (inputStream.read(buffer) != -1) {
                creds += new String(buffer);
            }

            // Always close files.
            inputStream.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }

        return creds.split(System.lineSeparator());
    }

    public static StatusListener getStatusListener() {
        return statusListener;
    }
}
