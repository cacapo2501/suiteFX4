package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.utility.ImageUtility;

public class DrawingCanvas extends Canvas {
	
	public DrawingCanvas(MainController controller) {
		widthProperty().addListener(evt -> drawImage(controller));
		heightProperty().addListener(evt -> drawImage(controller));
		
		controller.PreviewImageProperty().addListener(evt -> drawImage(controller));
		controller.ZoomProperty().addListener(evt -> drawImage(controller));
		controller.HorizontalScrollPositionProperty().addListener(evt -> drawImage(controller));
		controller.VerticalScrollPositionProperty().addListener(evt -> drawImage(controller));
		
		this.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> { pointerMoved(controller, evt); });
	}
	
	//マウスポインタが動いたときの処理
	private void pointerMoved(MainController controller, MouseEvent me) {
		if(!controller.getProject().projectActive) return;
		
		double pointerX = me.getX();
		double pointerY = me.getY();
		double zoom = controller.getZoom();
		double scrollX = controller.HorizontalScrollPositionProperty().get();
		double scrollY = controller.VerticalScrollPositionProperty().get();
		double canvasWidth = this.getWidth();
		double canvasHeight = this.getHeight();
		double imageWidth = controller.getProject().getDefinitionLayers().width;
		double imageHeight = controller.getProject().getDefinitionLayers().height;
		
		controller.setMessage("ポインタの位置 = (" + getCurrentCurosorPosition(pointerX, zoom, scrollX, canvasWidth, imageWidth)
							+ ", "  + getCurrentCurosorPosition(pointerY, zoom, scrollY, canvasHeight, imageHeight) + ")");
	}

	//画像上におけるカーソルの現在位置を取得する
	private int getCurrentCurosorPosition(double pointer, double zoom, double scroll, double canvasRange,
			double imageRange) {
		
		double offset = scroll * Math.max(imageRange * zoom - canvasRange, 0);
		double zoomedpos = offset + pointer;
		
		return (int)(zoomedpos / zoom);
	}

	private void drawImage(MainController controller) {
		GraphicsContext gc = this.getGraphicsContext2D();
		
		gc.setFill(Color.GRAY);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		Image currentImage = controller.getPreviewImage();
		if(currentImage == null) {
			return;
		}
		
		double offsetX = Math.max(currentImage.getWidth() * controller.getZoom() - this.getWidth(), 0) * controller.HorizontalScrollPositionProperty().get();
		double offsetY = Math.max(currentImage.getHeight() * controller.getZoom() - this.getHeight(), 0) * controller.VerticalScrollPositionProperty().get();
		
		if(controller.getZoom() == 1) {
			gc.drawImage(controller.getPreviewImage(), -offsetX, -offsetY);
		}
		else {
			int trimWidth = (int)Math.min(Math.ceil(this.getWidth() / controller.getZoom()), currentImage.getWidth());
			int trimHeight = (int)Math.min(Math.ceil(this.getHeight() / controller.getZoom()), currentImage.getHeight());
			int topleftX = (int)Math.floor(offsetX / controller.getZoom());
			int topleftY = (int)Math.floor(offsetY / controller.getZoom());
			if(trimWidth + topleftX < currentImage.getWidth()) trimWidth++;
			if(trimHeight + topleftY < currentImage.getHeight()) trimHeight++;
			Image trimmedImage = ImageUtility.trimImage(currentImage, topleftX, topleftY, trimWidth, trimHeight);
			Image zoomedImage = ImageUtility.zoomImage(trimmedImage, (int)controller.getZoom());
			
			int canvasOffsetX = (int)Math.floor(topleftX * controller.getZoom() - offsetX);
			int canvasOffsetY = (int)Math.floor(topleftY * controller.getZoom() - offsetY);
			
			gc.drawImage(zoomedImage, canvasOffsetX, canvasOffsetY);
		}
		
		controller.currentDisplayLeft = (int)offsetX;
		controller.currentDisplayTop = (int)offsetY;
	}

	@Override
	public boolean isResizable() {
		return true;
	}
}
