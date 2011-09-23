package me.pirogoeth.Waypoint.Util;

import java.util.logging.Logger;

public class RegistryException extends Exception {
    protected static String error;
    public Logger log = Logger.getLogger("Minecraft");
    public RegistryException () {
        super();
        error = "An unknown error occurred.";
        log.warning(error);
    }
    public RegistryException (String err) {
        super(err);
        error = err;
        log.warning(error);
    }
    public static String getError () {
        return error;
    }
}