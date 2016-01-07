import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatusListener extends Observable implements NativeKeyListener {
    private boolean active = false;
    public void nativeKeyPressed(NativeKeyEvent e) {

        if (e.getKeyCode() == 65) {
            active = !active;
            if (active) {
                System.out.println("Enabling input");
            } else {
                System.out.println("Disabling input");
            }
            this.setChanged();
            this.notifyObservers();
        }

    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

    public void nativeKeyTyped(NativeKeyEvent e) {}

    public boolean getActive() {
        return active;
    }

    public StatusListener() {
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    System.err.println("Failed to deregister native hook");
                    ex.printStackTrace();
                }
            }
        });

        GlobalScreen.addNativeKeyListener(this);

        // Get the logger for "org.jnativehook" and set the level to warning.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }
}