package com.smarthome.ui;

import com.smarthome.command.CommandInvoker;
import com.smarthome.facade.HomeController;
import com.smarthome.persistence.dao.CommandsLogDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Owns the top bar (title + Undo), the segmented mode picker, the sliding
 * status banner, the bottom navigation, and the central screen-host.
 *
 * Constructs the production {@link HomeController} (Facade) wired with real
 * DAOs so command logging and event history persist to SQLite. Each
 * sub-screen controller (Home / History / Decorator) shares this Facade
 * via a static accessor on the App level so all three see the same
 * {@link CommandInvoker} and audit log.
 */
public class MainController implements Initializable {

    @FXML private Button undoButton;
    @FXML private Button modeEcoButton;
    @FXML private Button modeSleepButton;
    @FXML private Button modeAwayButton;
    @FXML private HBox statusBanner;
    @FXML private Label statusBannerText;
    @FXML private StackPane screenHost;
    @FXML private Button navHomeButton;
    @FXML private Button navHistoryButton;
    @FXML private Button navDecoratorButton;

    /** Singleton Facade shared across all UI screens for this app run. */
    private static HomeController sharedFacade;

    public static HomeController getFacade() {
        if (sharedFacade == null) {
            // Defensive: build a default facade if the UI somehow boots without
            // App.start() having pre-wired one. Production path always pre-wires.
            sharedFacade = buildProductionFacade();
        }
        return sharedFacade;
    }

    private static HomeController buildProductionFacade() {
        DeviceEventDAO eventDAO = new DeviceEventDAO();
        CommandsLogDAO commandsDAO = new CommandsLogDAO();
        CommandInvoker invoker = new CommandInvoker(commandsDAO);
        return new HomeController(
            com.smarthome.core.SmartHomeHub.getInstance(),
            invoker,
            eventDAO,
            commandsDAO
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Build the production Facade once and stash it for sub-controllers.
        sharedFacade = buildProductionFacade();

        // Default landing screen
        loadScreen("/fxml/home.fxml", navHomeButton);
    }

    // ─────────────────────────────────────────────────────────────
    // Top bar
    // ─────────────────────────────────────────────────────────────

    @FXML
    private void onUndo() {
        boolean undone = sharedFacade.undoLastAction();
        if (undone) {
            showBanner("✓ Undid last action.", "status-banner-success");
            // Notify any active screen to refresh
            HomeBus.notifyDataChanged();
        } else {
            showBanner("Nothing to undo.", null);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Mode picker (segmented)
    // ─────────────────────────────────────────────────────────────

    @FXML private void onModeEco()   { applyMode("ECO",   modeEcoButton);   }
    @FXML private void onModeSleep() { applyMode("SLEEP", modeSleepButton); }
    @FXML private void onModeAway()  { applyMode("AWAY",  modeAwayButton);  }

    private void applyMode(String name, Button activated) {
        try {
            sharedFacade.setAutomationMode(name);
            highlightMode(activated);
            showBanner("✓ " + name + " mode applied to the whole home.",
                       "status-banner-success");
            HomeBus.notifyDataChanged();
        } catch (Exception e) {
            showBanner("Could not apply " + name + ": " + e.getMessage(),
                       "status-banner-error");
        }
    }

    private void highlightMode(Button activated) {
        clearActive(modeEcoButton, modeSleepButton, modeAwayButton);
        addClass(activated, "mode-segment-active");
    }

    private void clearActive(Button... buttons) {
        for (Button b : buttons) {
            b.getStyleClass().removeAll("mode-segment-active");
        }
    }

    private void addClass(Node node, String cls) {
        if (!node.getStyleClass().contains(cls)) {
            node.getStyleClass().add(cls);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Status banner (sliding, auto-fade)
    // ─────────────────────────────────────────────────────────────

    public void showBanner(String text, String variantClass) {
        statusBannerText.setText(text);
        statusBanner.getStyleClass().removeAll(
            "status-banner-success", "status-banner-error");
        if (variantClass != null) {
            addClass(statusBanner, variantClass);
        }
        statusBanner.setManaged(true);
        statusBanner.setVisible(true);

        PauseTransition fade = new PauseTransition(Duration.seconds(4));
        fade.setOnFinished(e -> {
            statusBanner.setManaged(false);
            statusBanner.setVisible(false);
        });
        fade.play();
    }

    // ─────────────────────────────────────────────────────────────
    // Bottom navigation
    // ─────────────────────────────────────────────────────────────

    @FXML private void onNavHome()      { loadScreen("/fxml/home.fxml",      navHomeButton);      }
    @FXML private void onNavHistory()   { loadScreen("/fxml/history.fxml",   navHistoryButton);   }
    @FXML private void onNavDecorator() { loadScreen("/fxml/decorator.fxml", navDecoratorButton); }

    private void loadScreen(String fxmlPath, Button activated) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node screen = loader.load();
            screenHost.getChildren().setAll(screen);

            highlightNav(activated);
        } catch (Exception e) {
            showBanner("Failed to load screen: " + e.getMessage(),
                       "status-banner-error");
            e.printStackTrace();
        }
    }

    private void highlightNav(Button activated) {
        for (Button b : new Button[]{navHomeButton, navHistoryButton, navDecoratorButton}) {
            b.getStyleClass().removeAll("nav-button-active");
        }
        addClass(activated, "nav-button-active");
    }

}
