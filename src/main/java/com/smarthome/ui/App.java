package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.factory.Version2DeviceFactory;
import com.smarthome.persistence.Database;
import com.smarthome.persistence.dao.DeviceDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;
import com.smarthome.persistence.dao.RoomDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Enumeration;
import java.util.List;

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

        // Order matters:
        //   1. Load persisted state (silent — no observers attached yet)
        //   2. Seed demo rooms/devices ONLY if database is empty
        //   3. Attach DaoEventBridge so subsequent state changes persist
        loadPersistedState();
        seedDemoDataIfEmpty();
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

    /**
     * Loads any rooms and devices already persisted to SQLite. Devices
     * appear with their saved family / type / state — the DAO uses the
     * Abstract Factory at runtime to reconstruct the right variant.
     */
    private void loadPersistedState() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        RoomDAO roomDAO = new RoomDAO();
        DeviceDAO deviceDAO = new DeviceDAO();

        List<Room> rooms = roomDAO.findAll();
        for (Room r : rooms) {
            for (Device d : deviceDAO.findByRoom(r.getRoomId())) {
                r.addDevice(d);
            }
            hub.addRoom(r);
        }
    }

    /**
     * Seeds demo rooms and devices only if the persistence layer is
     * empty (first run). Persists everything so subsequent launches
     * reload the same setup.
     */
    private void seedDemoDataIfEmpty() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        if (!hub.getRooms().isEmpty()) {
            return;
        }
        Version2DeviceFactory factory = new Version2DeviceFactory();
        RoomDAO roomDAO = new RoomDAO();
        DeviceDAO deviceDAO = new DeviceDAO();

        Room kitchen = new Room("kitchen", "Kitchen");
        kitchen.addDevice(factory.createLight("Ceiling Light"));
        kitchen.addDevice(factory.createThermostat("Kitchen Thermostat"));
        hub.addRoom(kitchen);
        roomDAO.insert(kitchen);

        Room livingRoom = new Room("living-room", "Living Room");
        livingRoom.addDevice(factory.createLight("Lamp"));
        livingRoom.addDevice(factory.createCamera("Living Room Camera"));
        hub.addRoom(livingRoom);
        roomDAO.insert(livingRoom);

        Room frontDoor = new Room("front-door", "Front Door");
        frontDoor.addDevice(factory.createDoorLock("Smart Lock"));
        frontDoor.addDevice(factory.createCamera("Doorbell Camera"));
        hub.addRoom(frontDoor);
        roomDAO.insert(frontDoor);

        // Persist every device created by the seed
        for (Room room : hub.getRooms()) {
            Enumeration<Device> it = room.devices();
            while (it.hasMoreElements()) {
                deviceDAO.insert(it.nextElement(), room.getRoomId());
            }
        }
    }

    /**
     * Attaches the {@link DaoEventBridge} to every device so each notify
     * (a) appends a row to the device_events audit log and (b) updates
     * the device's live-state row in the devices table. Demo payoff:
     * close and reopen the app and devices retain their last state.
     */
    private void attachPersistenceObservers() {
        DaoEventBridge bridge = new DaoEventBridge(new DeviceEventDAO(), new DeviceDAO());
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
