package it.polimi.ingsw.am43.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerScheduler {
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private ServerScheduler() {}
}
