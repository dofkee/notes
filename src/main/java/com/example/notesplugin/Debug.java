
package com.example.notesplugin;

public class Debug {

    public static boolean debug = true;

    public static void log(String str) {
        if (debug) {
            System.out.println("[PLG] " + str);
        }
    }
}
