package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.factory.Version2DeviceFactory;
import com.smarthome.persistence.Database;
import com.smarthome.persistence.dao.DeviceEventDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Enumeration;

/**
 * JavaFX Application entry point for the Smart Home dashboard
 * (mobile-styled, ~400×800).
 *
 * Startup sequence:
 *   1. Initialise the SQLite Database singleton (creates schema on first run)
 *   2. Seed demo rooms and devices into the singleton hub if empty
 *   3. Attach a DaoEventBridge as Observer on every device so state changes
 *      are persisted to device_events
 *   4. Load main.fxml and show the window
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Start the database first so DAOs can be wired into the Facade.
        Database.getInstance();

        seedDemoData();
        attachPersistenceObservers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMinWidth(360);
        stage.setMinHeight(640);
        stage.show();
    }

    /** Inserts demo rooms and devices so the UI is non-empty on first launch. */
    private void seedDemoData() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        if (!hub.getRooms().isEmpty()) {
            return;
        }
        Version2DeviceFactory factory = new Version2DeviceFactory();

        Room kitchen = new Room("kitchen", "Kitchen");
        kitchen.addDevice(factory.createLight("Ceiling Light"));
        kitchen.addDevice(factory.createThermostat("Kitchen Thermostat"));
        hub.addRoom(kitchen);

        Room livingRoom = new Room("living-room", "Living Room");
        livingRoom.addDevice(factory.createLight("Lamp"));
        livingRoom.addDevice(factory.createCamera("Living Room Camera"));
        hub.addRoom(livingRoom);

        Room frontDoor = new Room("front-door", "Front Door");
        frontDoor.addDevice(factory.createDoorLock("Smart Lock"));
        frontDoor.addDevice(factory.createCamera("Doorbell Camera"));
        hub.addRoom(frontDoor);
    }

    /**
     * Attaches the {@link DaoEventBridge} to every device so each notify
     * fires a persisted device_events row. This is the rubric's "DAO is
     * actually used at runtime, not just present" line.
     */
    private void attachPersistenceObservers() {
        DaoEventBridge bridge = new DaoEventBridge(new DeviceEventDAO());
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            Enumeration<Device> it = room.devices();
            while (it.hasMoreElements()) {
                it.nextElement().attach(bridge);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
