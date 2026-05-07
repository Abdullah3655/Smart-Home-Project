package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;
import com.smarthome.persistence.dao.DeviceEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * History screen — feed of device events.
 *
 * <p>Loads recent events from the DAO once at screen mount, then appends
 * new events live via Observer. Demonstrates DAO + Observer working
 * together: past events come from SQLite (DAO), new events arrive in
 * real time without polling (Observer).</p>
 */
public class HistoryController implements Initializable {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_ROWS = 200;

    @FXML private VBox eventsContainer;

    private final com.smarthome.facade.HomeController facade =
        MainController.getFacade();

    private Observer liveObserver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHistoricalEvents();
        attachLiveObserver();
    }

    private void loadHistoricalEvents() {
        List<DeviceEvent> recent = facade.getEventHistory();
        // Newest first (DAO returns newest first already)
        for (DeviceEvent e : recent) {
            eventsContainer.getChildren().add(buildRowForPersisted(e));
        }
        if (recent.isEmpty()) {
            Label empty = new Label("No events yet — toggle some devices on the Home tab to see them appear here.");
            empty.getStyleClass().add("empty-state");
            empty.setWrapText(true);
            eventsContainer.getChildren().add(empty);
        }
    }

    private void attachLiveObserver() {
        liveObserver = (device, event) -> Platform.runLater(() ->
            prependRow(buildRowForLive(device.getName(), event)));
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            Enumeration<Device> it = room.devices();
            while (it.hasMoreElements()) {
                it.nextElement().attach(liveObserver);
            }
        }
    }

    private void prependRow(HBox row) {
        // Insert just below the screen-title labels (positions 0 and 1)
        int insertAt = Math.min(2, eventsContainer.getChildren().size());
        eventsContainer.getChildren().add(insertAt, row);

        // Keep the feed bounded
        while (eventsContainer.getChildren().size() > MAX_ROWS + 2) {
            eventsContainer.getChildren().remove(eventsContainer.getChildren().size() - 1);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Row builders
    // ─────────────────────────────────────────────────────────────

    private HBox buildRowForPersisted(DeviceEvent e) {
        String time = e.timestamp() != null
            ? e.timestamp().atZone(ZoneId.systemDefault()).toLocalTime().format(CLOCK)
            : "--:--:--";
        return buildRow(time, "device " + shortId(e.deviceId()), e.eventType());
    }

    private HBox buildRowForLive(String deviceName, String eventType) {
        return buildRow(LocalTime.now().format(CLOCK), deviceName, eventType);
    }

    private HBox buildRow(String time, String deviceLabel, String eventType) {
        HBox row = new HBox(10);
        row.getStyleClass().add("event-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label dot = new Label(dotFor(eventType));
        dot.getStyleClass().add(dotClassFor(eventType));

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("event-time");

        Label deviceLbl = new Label(deviceLabel);
        deviceLbl.getStyleClass().add("event-device");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label typeLabel = new Label(eventType);
        typeLabel.getStyleClass().add("event-type");

        row.getChildren().addAll(dot, timeLabel, deviceLbl, spacer, typeLabel);
        return row;
    }

    private String dotFor(String eventType) {
        return switch (eventType) {
            case "TURNED_ON" -> "●";
            case "TURNED_OFF" -> "○";
            case "LOCKED", "UNLOCKED" -> "▣";
            case "TEMP_CHANGED" -> "🌡";
            case "BRIGHTNESS_CHANGED" -> "✦";
            default -> "•";
        };
    }

    private String dotClassFor(String eventType) {
        return switch (eventType) {
            case "TURNED_ON" -> "event-dot-on";
            case "LOCKED", "UNLOCKED" -> "event-dot-locked";
            case "TEMP_CHANGED", "BRIGHTNESS_CHANGED" -> "event-dot-temp";
            default -> "event-dot-off";
        };
    }

    private String shortId(String id) {
        if (id == null) return "?";
        return id.length() > 8 ? id.substring(0, 8) + "…" : id;
    }
}
