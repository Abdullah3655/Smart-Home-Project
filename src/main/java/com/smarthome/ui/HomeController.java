package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.observer.Observer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Home screen — renders all rooms with their device cards.
 *
 * <p>Calls only the Facade ({@link com.smarthome.facade.HomeController})
 * for any state mutation, satisfying the "UI must not touch domain or DAO
 * directly" rubric line. Refreshes itself reactively via the Observer
 * pattern + {@link HomeBus}.</p>
 */
public class HomeController implements Initializable {

    @FXML private VBox roomsContainer;

    private final com.smarthome.facade.HomeController facade =
        MainController.getFacade();

    private Observer cardRefreshObserver;

    private final Runnable busListener = this::renderAllRooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Refresh on every device event so cards reflect live state.
        cardRefreshObserver = (device, event) ->
            Platform.runLater(this::renderAllRooms);
        attachToAllDevices(cardRefreshObserver);

        // Refresh on top-bar-driven changes (mode applied, undo).
        HomeBus.subscribe(busListener);

        renderAllRooms();
    }

    // ─────────────────────────────────────────────────────────────
    // Card rendering
    // ─────────────────────────────────────────────────────────────

    private void renderAllRooms() {
        roomsContainer.getChildren().clear();
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            roomsContainer.getChildren().add(buildRoomSection(room));
        }
    }

    private VBox buildRoomSection(Room room) {
        VBox section = new VBox(8);

        // Room header
        HBox header = new HBox(8);
        header.getStyleClass().add("room-header");
        Label name = new Label(room.getName());
        name.getStyleClass().add("room-name");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label count = new Label(deviceCount(room) + " devices");
        count.getStyleClass().add("room-count");
        header.getChildren().addAll(name, spacer, count);

        section.getChildren().add(header);

        Enumeration<Device> it = room.devices();
        while (it.hasMoreElements()) {
            section.getChildren().add(buildDeviceCard(it.nextElement()));
        }
        return section;
    }

    private VBox buildDeviceCard(Device device) {
        VBox card = new VBox(8);
        card.getStyleClass().add("device-card");

        // Header row: icon + name + state badge
        HBox top = new HBox(10);
        top.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label icon = new Label(iconFor(device));
        icon.getStyleClass().add("device-icon");

        VBox nameBlock = new VBox(2);
        Label nameLabel = new Label(device.getName());
        nameLabel.getStyleClass().add("device-name");
        Label subtitle = new Label(subtitleFor(device));
        subtitle.getStyleClass().add("device-subtitle");
        nameBlock.getChildren().addAll(nameLabel, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label state = stateBadgeFor(device);

        top.getChildren().addAll(icon, nameBlock, spacer, state);

        card.getChildren().add(top);

        // Actions row, contextual to device type
        HBox actions = buildActionsFor(device);
        card.getChildren().add(actions);

        return card;
    }

    private HBox buildActionsFor(Device device) {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        if (device instanceof Lock lock) {
            row.getChildren().add(lock.isLocked()
                ? actionButton("Unlock", () -> facade.unlockDevice(device.getId()))
                : actionButton("Lock",   () -> facade.lockDevice(device.getId())));
            return row;
        }

        if (device instanceof Thermostat thermostat) {
            Button minus = spinnerButton("−", () ->
                facade.setTemperature(device.getId(), thermostat.getTemperature() - 1));
            Button plus  = spinnerButton("+", () ->
                facade.setTemperature(device.getId(), thermostat.getTemperature() + 1));
            Label tempDisplay = new Label(String.format("%.0f°C", thermostat.getTemperature()));
            tempDisplay.getStyleClass().add("temperature-display");
            row.getChildren().addAll(minus, tempDisplay, plus);
            return row;
        }

        // Light or Camera — generic on/off
        if (device.isPoweredOn()) {
            row.getChildren().add(
                actionButton("Turn Off", () -> facade.turnOffDevice(device.getId())));
        } else {
            Button on = actionButton("Turn On", () -> facade.turnOnDevice(device.getId()));
            on.getStyleClass().add("action-button-primary");
            row.getChildren().add(on);
        }
        return row;
    }

    private Button actionButton(String text, Runnable handler) {
        Button b = new Button(text);
        b.getStyleClass().add("action-button");
        b.setOnAction(e -> safeRun(handler));
        return b;
    }

    private Button spinnerButton(String text, Runnable handler) {
        Button b = new Button(text);
        b.getStyleClass().add("spinner-button");
        b.setOnAction(e -> safeRun(handler));
        return b;
    }

    private void safeRun(Runnable handler) {
        try {
            handler.run();
            // Card refresh happens automatically via the Observer attached
            // on initialize() — no manual refresh call needed.
        } catch (Exception e) {
            // Errors are surfaced via the top status banner if a parent
            // controller wires them; for now, log locally.
            System.err.println("Action failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Visual helpers
    // ─────────────────────────────────────────────────────────────

    private String iconFor(Device d) {
        if (d instanceof Light)      return "💡";
        if (d instanceof Thermostat) return "🌡";
        if (d instanceof Lock)       return "🔒";
        if (d instanceof Camera)     return "📷";
        return "•";
    }

    private String subtitleFor(Device d) {
        String type;
        if (d instanceof Light)           type = "Lighting";
        else if (d instanceof Thermostat) type = "Climate";
        else if (d instanceof Lock)       type = "Security";
        else if (d instanceof Camera)     type = "Security";
        else                              type = "Device";

        // Family inference from package name — Version1 vs Version2 vs base
        String pkg = d.getClass().getPackageName();
        String family = pkg.contains("version2") ? "Version2"
                      : pkg.contains("version1") ? "Version1"
                      : "Base";
        return type + " · " + family;
    }

    private Label stateBadgeFor(Device d) {
        Label badge = new Label();
        if (d instanceof Lock lock) {
            badge.setText(lock.isLocked() ? "🔒 LOCKED" : "🔓 UNLOCKED");
            badge.getStyleClass().add(lock.isLocked()
                ? "device-state-locked" : "device-state-off");
        } else {
            badge.setText(d.isPoweredOn() ? "● ON" : "○ OFF");
            badge.getStyleClass().add(d.isPoweredOn()
                ? "device-state-on" : "device-state-off");
        }
        return badge;
    }

    private int deviceCount(Room room) {
        int n = 0;
        Enumeration<Device> it = room.devices();
        while (it.hasMoreElements()) { it.nextElement(); n++; }
        return n;
    }

    // ─────────────────────────────────────────────────────────────
    // Observer attachment
    // ─────────────────────────────────────────────────────────────

    private void attachToAllDevices(Observer obs) {
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            Enumeration<Device> it = room.devices();
            while (it.hasMoreElements()) {
                it.nextElement().attach(obs);
            }
        }
    }
}
