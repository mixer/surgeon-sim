package pro.beam.games.surgeonsim;

import pro.beam.interactive.net.packet.Protocol;

public class KeyHandler {
    @SuppressWarnings("FieldCanBeLocal")
    private static int shakeCooldown = 60000;
    private Integer key;
    private KeyType type;
    private int presses;
    private Double threshold;
    private Double progress;

    public KeyHandler(Integer key, KeyType type, Double threshold) {
        this.key = key;
        this.type = type;
        presses = 0;
        this.threshold = threshold;
    }

    // TODO: subclass these types
    public boolean handleKey(Protocol.Report.TactileInfo tInfo, Protocol.Report.Users users) {
        if (type == KeyType.PRESS) {
            if (users.getQuorum() == 0.0) return pressKey(0.0);
            return pressKey(tInfo.getHolding() / (double) users.getQuorum());
        }
        //noinspection SimplifiableIfStatement
        if (type == KeyType.SHAKE) {
            return shakeScreen(tInfo.getPressFrequency());
        }

        return false;
    }

    private boolean shakeScreen(Double count) {
        presses += count;

        if (presses >= 10) {
            presses = 0;

            MouseController mc = MouseController.getInstance();
            if (mc == null) {
                System.out.println("Shake disabled, not firing.");
                return false;
            }

            mc.shakeMouse();
            return true;
        }
        return false;
    }

    private boolean pressKey(Double holdingPct) {
        progress = holdingPct * 2;
        if (progress < 0.0) progress = 0.0;
        if (progress > 1.0) progress = 1.0;
        if (holdingPct >= threshold) {
            KeyboardController.getInstance().setMovement(key, true);
            return true;
        } else {
            KeyboardController.getInstance().setMovement(key, false);
            return false;
        }
    }

    public Double getProgress() {
        if (type == KeyType.PRESS) {
            return progress;
        } else if (type == KeyType.SHAKE) {
            return presses / threshold;
        }
        return 0.0;
    }

    public int getCooldown() {
        if (type == KeyType.SHAKE) {
            return shakeCooldown;
        }
        return 0;
    }

    public Integer getKeyCode() {
        return key;
    }
}
