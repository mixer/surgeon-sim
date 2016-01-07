import java.util.HashSet;

class KeyMap {
    private static KeyMap keyMap = new KeyMap();
    private HashSet<Integer> allowed = new HashSet<Integer>();

    public KeyMap() {
        int[] values = { 32, 65, 68, 70, 83 };
        for (int i : values) {
            allowed.add(i);
        }
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