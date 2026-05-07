package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.decorator.LoggingDeviceDecorator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


// Screen controller for wrapping devices with decorators and testing wrappers.
public class DecoratorController implements Initializable {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private ChoiceBox<DeviceChoice> deviceChoiceBox;
    @FXML private Label wrapStatusLabel;
    @FXML private VBox logContainer;

    private LoggingDeviceDecorator wrapped;
    private Device unwrappedTarget;
    private Room ownerRoom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateDeviceChoices();
    }

    private void populateDeviceChoices() {
        List<DeviceChoice> choices = new ArrayList<>();
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            for (Device device : room.devices()) {
                choices.add(new DeviceChoice(room, device));
            }
        }
        deviceChoiceBox.setItems(FXCollections.observableArrayList(choices));
        deviceChoiceBox.setConverter(new StringConverter<>() {
            @Override public String toString(DeviceChoice c) {
                return c == null ? "" : c.room.getName() + " · " + c.device.getName();
            }
            @Override public DeviceChoice fromString(String s) { return null; }
        });
    }

    @FXML
    private void onWrap() {
        DeviceChoice choice = deviceChoiceBox.getValue();
        if (choice == null) {
            setStatus("Pick a device first.");
            return;
        }
        if (wrapped != null) {
            setStatus("Already wrapped: " + wrapped.getName() + ". Unwrap first.");
            return;
        }

        unwrappedTarget = choice.device;
        ownerRoom = choice.room;

        ownerRoom.removeDevice(unwrappedTarget.getId());
        wrapped = new LoggingDeviceDecorator(unwrappedTarget);
        ownerRoom.addDevice(wrapped);

        setStatus("Wrapped " + wrapped.getName() + " with LoggingDeviceDecorator. "
                + "Toggle it to see captured calls below.");
        clearLog();
        appendLog("• decorator attached at " + LocalTime.now().format(CLOCK));
    }

    @FXML
    private void onUnwrap() {
        if (wrapped == null || ownerRoom == null) {
            setStatus("Nothing wrapped.");
            return;
        }
        ownerRoom.removeDevice(wrapped.getId());
        ownerRoom.addDevice(unwrappedTarget);
        setStatus("Unwrapped " + unwrappedTarget.getName() + ".");
        appendLog("• decorator detached at " + LocalTime.now().format(CLOCK));

        wrapped = null;
        unwrappedTarget = null;
        ownerRoom = null;
    }

    @FXML
    private void onWrappedTurnOn() {
        if (wrapped == null) { setStatus("Wrap a device first."); return; }
        wrapped.turnOn();
        renderCapturedLog();
    }

    @FXML
    private void onWrappedTurnOff() {
        if (wrapped == null) { setStatus("Wrap a device first."); return; }
        wrapped.turnOff();
        renderCapturedLog();
    }

    private void renderCapturedLog() {
        Platform.runLater(() -> {
            clearLog();
            for (String entry : wrapped.getLog()) {
                appendLog("• " + entry);
            }
        });
    }

    private void setStatus(String text) { wrapStatusLabel.setText(text); }

    private void clearLog() { logContainer.getChildren().clear(); }

    private void appendLog(String text) {
        Label entry = new Label(text);
        entry.getStyleClass().add("decorator-log-row");
        logContainer.getChildren().add(entry);
    }

    
    private record DeviceChoice(Room room, Device device) {}
}
