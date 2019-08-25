package application;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class StatusPane extends GridPane{

	public StatusPane(MainController controller) {
		Label labStatus = new Label();
		Label labZoom = new Label("ズーム : ");
		ToggleButton x1Button = new ToggleButton("x1");
		ToggleButton x2Button = new ToggleButton("x2");
		ToggleButton x4Button = new ToggleButton("x4");
		ToggleButton x8Button = new ToggleButton("x8");
		ToggleButton x16Button = new ToggleButton("x16");
		
		ToggleGroup zoomGroup = new ToggleGroup();
		x1Button.setToggleGroup(zoomGroup);
		x2Button.setToggleGroup(zoomGroup);
		x4Button.setToggleGroup(zoomGroup);
		x8Button.setToggleGroup(zoomGroup);
		x16Button.setToggleGroup(zoomGroup);
		
		x1Button.selectedProperty().set(true);
		x1Button.setOnAction(evt -> changeZoom(controller,1));
		x2Button.setOnAction(evt -> changeZoom(controller, 2));
		x4Button.setOnAction(evt -> changeZoom(controller, 4));
		x8Button.setOnAction(evt -> changeZoom(controller, 8));
		x16Button.setOnAction(evt -> changeZoom(controller, 16));
		
		labZoom.setPadding(new Insets(3, 10, 3, 10));
		labStatus.textProperty().bind(controller.StatusMessageProperty());
		
		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().add(new ColumnConstraints());
		
		this.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
		
		this.addRow(0, labStatus, labZoom, x1Button, x2Button, x4Button, x8Button, x16Button);
	}
	
	private void changeZoom(MainController controller, double zoom) {
		controller.setZoom(zoom);
		controller.setMessage("倍率を" + zoom + "倍に変更しました。");
	}
}
