import pro.beam.interactive.net.packet.Protocol;

import java.security.Key;

/**
 * Created by jamy on 05/02/16.
 */
public class KeyHandler {
    private Integer key;
    private KeyType type;
    private boolean state;
    private int presses;
    private Double treshold;
    private Double progress;

    private static int shakeCooldown = 60000;

    public KeyHandler(Integer key, KeyType type, Double treshold) {
        this.key = key;
        this.type = type;
        this.state = false;
        presses = 0;
        this.treshold = treshold;
    }

    // TODO: subclass these types
    public boolean handleKey(Protocol.Report.TactileInfo tInfo, Protocol.Report.Users users) {
        if (type == KeyType.PRESS) {
            if (users.getQuorum() == 0.0) return pressKey(0.0);
            return pressKey(tInfo.getHolding() / (double) users.getQuorum());
        }
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
        if (holdingPct >= treshold) {
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
            return presses / treshold;
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
