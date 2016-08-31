package eu.europa.ec.fisheries.mdr.exception;

/**
 * Created by kovian on 30/08/2016.
 */
public class ActivityCacheInitException extends Exception {
    public ActivityCacheInitException() {
    }

    public ActivityCacheInitException(String message) {
        super(message);
    }

    public ActivityCacheInitException(String message, Throwable cause) {
        super(message, cause);
    }
}