package it.polimi.ingsw.am43.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Utility class providing a centralized, shared thread pool for managing
 * scheduled actions on the server.
 */
public class ServerScheduler {
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private ServerScheduler() {}
}
