package util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Util de debug simple (comentarios cortos)
public class Debug {
    private static final boolean ENABLED;
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    static {
        String prop = System.getProperty("game.debug", "false");
        ENABLED = "true".equalsIgnoreCase(prop) || "1".equals(prop);
    }

    public static boolean isEnabled() { return ENABLED; }

    // imprime mensaje de debug con hora y hebra
    public static void log(String tag, String msg) {
        if (!ENABLED) return;
        String time = LocalTime.now().format(TF);
        String thread = Thread.currentThread().getName();
        System.err.println("[" + time + "] [" + thread + "] [" + tag + "] " + msg);
    }

    public static void log(String msg) { log("DBG", msg); }
}
