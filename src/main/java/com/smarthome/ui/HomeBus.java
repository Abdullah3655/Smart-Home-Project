package com.smarthome.ui;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tiny in-process event bus so the {@link MainController} can tell whichever
 * screen is currently mounted to refresh after a top-bar action (mode change
 * or undo).
 *
 * <p>OBSERVER PATTERN at the UI level — keeps screen controllers decoupled
 * from MainController.</p>
 */
public final class HomeBus {

    /** Listeners are runnables called on the JavaFX Application Thread. */
    private static final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    private HomeBus() {}   // utility class

    /** Register a listener; usually called by a screen controller's initialize(). */
    public static void subscribe(Runnable listener) {
        listeners.add(listener);
    }

    /** Unsubscribe — important when a screen is unloaded so we don't leak. */
    public static void unsubscribe(Runnable listener) {
        listeners.remove(listener);
    }

    /** Fire an event to every listener; safe from any thread. */
    public static void notifyDataChanged() {
        Platform.runLater(() -> {
            for (Runnable r : new ArrayList<>(listeners)) {
                try {
                    r.run();
                } catch (Exception e) {
                    System.err.println("HomeBus listener threw: " + e.getMessage());
                }
            }
        });
    }
}
