package pro.beam.games.surgeonsim;

import pro.beam.api.BeamAPI;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.channel.BeamChannel;
import pro.beam.api.services.impl.ChannelsService;
import pro.beam.api.services.impl.UsersService;
import pro.beam.interactive.net.packet.Protocol;
import pro.beam.interactive.robot.Robot;
import pro.beam.interactive.robot.RobotBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class BPSurgeonSim {
    public static BeamAPI beam;
    public static double sensitivity = 0.22;
    private static StatusListener statusListener;

    public static void login(String username, String oauthToken) throws ExecutionException, InterruptedException {
        beam = new BeamAPI(oauthToken);
        BeamUser user = beam.use(UsersService.class).getCurrent().get();
        final Robot robot = new RobotBuilder().channel(user.channel).build(beam, false).get();

        //Listen for report events on the robot.
        robot.on(Protocol.Report.class, report -> {
            for (Protocol.Report.TactileInfo tInfo : report.getTactileList()) {
                KeyMap.getInstance().handleInput(tInfo, report.getUsers());
            }

            Protocol.ProgressUpdate pu = KeyMap.getInstance().getProgressUpdate();

            try {
                robot.write(pu);
            } catch (IOException ex) {
                System.err.println("Failed to send packet.");
                ex.printStackTrace();
            }
        });

        init();
    }

    public static StatusListener getStatusListener() {
        return statusListener;
    }

    public static void init() {
        statusListener = new StatusListener();
        MouseController.init(sensitivity);
        KeyboardController.init();
    }
}
