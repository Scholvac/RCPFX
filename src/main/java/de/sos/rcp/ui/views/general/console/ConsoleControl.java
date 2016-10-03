package de.sos.rcp.ui.views.general.console;

import java.util.logging.Level;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ConsoleControl extends VBox
{
	
	public ConsoleControl() {
		LogView logView = new LogView();
        logView.setPrefWidth(400);

        ChoiceBox<Level> filterLevel = new ChoiceBox<>(
                FXCollections.observableArrayList(
                        Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE
                )
        );
        filterLevel.getSelectionModel().select(Level.FINE);
        logView.filterLevelProperty().bind(
                filterLevel.getSelectionModel().selectedItemProperty()
        );

        ToggleButton showTimestamp = new ToggleButton("Show Timestamp");
        logView.showTimeStampProperty().bind(showTimestamp.selectedProperty());

        ToggleButton tail = new ToggleButton("Tail");
        logView.tailProperty().bind(tail.selectedProperty());

        ToggleButton pause = new ToggleButton("Pause");
        logView.pausedProperty().bind(pause.selectedProperty());

        Slider rate = new Slider(0.1, 60, 60);
        logView.refreshRateProperty().bind(rate.valueProperty());
        Label rateLabel = new Label();
        rateLabel.textProperty().bind(Bindings.format("Update: %.2f fps", rate.valueProperty()));
        rateLabel.setStyle("-fx-font-family: monospace;");
        VBox rateLayout = new VBox(rate, rateLabel);
        rateLayout.setAlignment(Pos.CENTER);

        HBox controls = new HBox(
                10,
                filterLevel,
                showTimestamp,
                tail,
                pause,
                rateLayout
        );
        controls.setMinHeight(Region.USE_PREF_SIZE);

        getChildren().add(controls);
        getChildren().add(logView);
        VBox.setVgrow(logView, Priority.ALWAYS);
	}
}
