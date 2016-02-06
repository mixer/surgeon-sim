import pro.beam.interactive.net.packet.Protocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

class KeyMap {
    private static KeyMap keyMap = new KeyMap();
    private HashSet<Integer> allowed = new HashSet<Integer>();

    private HashMap<Integer, KeyHandler> handlers;

    private Protocol.ProgressUpdate.Builder builder;

    public KeyMap() {
        handlers = new HashMap<Integer, KeyHandler>();
        handlers.put(0, new KeyHandler(65, KeyType.PRESS, 0.5));
        handlers.put(1, new KeyHandler(83, KeyType.PRESS, 0.5));
        handlers.put(2, new KeyHandler(68, KeyType.PRESS, 0.5));
        handlers.put(3, new KeyHandler(70, KeyType.PRESS, 0.5));
        handlers.put(4, new KeyHandler(32, KeyType.PRESS, 0.5));

        handlers.put(5, new KeyHandler(-1, KeyType.SHAKE, 10.0));

        builder = Protocol.ProgressUpdate.newBuilder();
    }

    public boolean handleInput(Protocol.Report.TactileInfo tInfo, Protocol.Report.Users users) {
        boolean pressed = false;
        if (handlers.containsKey(tInfo.getId())) {
            KeyHandler kHandler = handlers.get(tInfo.getId());
            pressed = kHandler.handleKey(tInfo, users);

            Protocol.ProgressUpdate.TactileUpdate.Builder tacBuilder = Protocol.ProgressUpdate.TactileUpdate.newBuilder();
            tacBuilder
                    .setId(tInfo.getId())
                    .setFired(pressed)
                    .setProgress(kHandler.getProgress());

            if (pressed) tacBuilder.setCooldown(kHandler.getCooldown());

            builder.addTactile(tacBuilder);
        }

        return pressed;
    }

    public Protocol.ProgressUpdate getProgressUpdate() {
        Protocol.ProgressUpdate pu = builder.build();
        builder = Protocol.ProgressUpdate.newBuilder();

        return pu;
    }

    public static KeyMap getInstance() {
        return keyMap;
    }

    public boolean isKeyAllowed(int keyCode) {
        return allowed.contains(keyCode);
    }

    public HashSet<Integer> getAllowed() {
        return allowed;
    }
}