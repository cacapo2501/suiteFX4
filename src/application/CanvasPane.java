package application;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class CanvasPane extends GridPane {
	public CanvasPane(BorderPane root, MainController controller) {		
		Pane canvasBase = new Pane();
		DrawingCanvas mainCanvas = new DrawingCanvas(controller);
		mainCanvas.widthProperty().bind(canvasBase.widthProperty());
		mainCanvas.heightProperty().bind(canvasBase.heightProperty());
		canvasBase.getChildren().add(mainCanvas);
		this.add(canvasBase, 0, 0);
		
		ScrollBar scrHorizontal = new ScrollBar();
		scrHorizontal.maxProperty().set(1);
		scrHorizontal.minProperty().set(0);
		scrHorizontal.valueProperty().set(0);
		controller.HorizontalScrollPositionProperty().bind(scrHorizontal.valueProperty());
		scrHorizontal.visibleAmountProperty().set(1);
		scrHorizontal.setOrientation(Orientation.HORIZONTAL);
		this.add(scrHorizontal, 0, 1);
		
		ScrollBar scrVertical = new ScrollBar();
		scrVertical.maxProperty().set(1);
		scrVertical.minProperty().set(0);
		scrVertical.valueProperty().set(0);
		controller.VerticalScrollPositionProperty().bind(scrVertical.valueProperty());
		scrVertical.visibleAmountProperty().set(1);
		scrVertical.setOrientation(Orientation.VERTICAL);
		this.add(scrVertical, 1, 0);

		controller.PreviewImageProperty().addListener(evt -> setVisibleAmount(mainCanvas, controller, scrHorizontal, scrVertical));
		controller.ZoomProperty().addListener(evt -> setVisibleAmount(mainCanvas, controller, scrHorizontal, scrVertical));
		mainCanvas.widthProperty().addListener(evt -> setVisibleAmount(mainCanvas, controller, scrHorizontal, scrVertical));
		mainCanvas.heightProperty().addListener(evt -> setVisibleAmount(mainCanvas, controller, scrHorizontal, scrVertical));
		
		CanvasPane.setHgrow(canvasBase, Priority.ALWAYS);
		CanvasPane.setVgrow(canvasBase, Priority.ALWAYS);
	}
	
	private void setVisibleAmount(DrawingCanvas canvas, MainController controller, ScrollBar scrHorizontal, ScrollBar scrVertical) {
		scrVertical.setVisibleAmount(
				controller.getPreviewImage() == null ? 1. : (canvas.getHeight() / controller.getPreviewImage().getHeight() / controller.getZoom()));
		scrHorizontal.setVisibleAmount(
				controller.getPreviewImage() == null ? 1. : (canvas.getWidth() / controller.getPreviewImage().getWidth() / controller.getZoom()));
		
	}
}
