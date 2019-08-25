package model;

import application.MainController;
import enums.ParameterType;
import enums.PreviewMode;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import model.utility.ImageUtility;

public class SFXProject {
	private final int width, height;
	private final DefinitionLayers layers;
	public final boolean projectActive;

	public SFXProject() {
		this.width = 0;
		this.height = 0;
		this.layers = new DefinitionLayers(0, 0);
		projectActive = false;

	}

	public SFXProject(Image defImage, MainController controller) {
		this.width = (int)defImage.getWidth();
		this.height = (int)defImage.getHeight();
		projectActive = true;

		//ピクセル情報を取得する
		int pixel[] = new int[width * height];
		PixelReader reader = defImage.getPixelReader();
		reader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixel, 0, width);

		this.layers = new DefinitionLayers(width, height, pixel);
		controller.setPreviewMode(PreviewMode.EXPORTPREVIEW);
	}

	public SFXProject(DefinitionLayers layers, MainController controller) {
		this.width = layers.width;
		this.height = layers.height;
		this.layers = layers;

		projectActive = true;

		controller.setPreviewMode(PreviewMode.EXPORTPREVIEW);
	}

	public SFXProject(DefinitionLayers layers, Image patternImage, MainController controller) {
		this.width = (int)patternImage.widthProperty().get();
		this.height = (int)patternImage.heightProperty().get();
		this.layers = new DefinitionLayers(layers, ImageUtility.toIntArray(patternImage));

		projectActive = true;

		controller.setPreviewMode(PreviewMode.EXPORTPREVIEW);
		controller.setPreviewImage(this.getPreviewImage(controller.getPreviewMode()));
	}

	public DefinitionLayers getDefinitionLayers() { return layers; }

	public Image getPreviewImage(PreviewMode mode) {
		if(width * height == 0) return null;

		int pixel[] = layers.getColorData(mode);

		WritableImage resImage = new WritableImage(width, height);
		PixelWriter writer = resImage.getPixelWriter();
		writer.setPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), pixel, 0, width);

		return (Image)resImage;
	}

	public Image getTextureImages(int index, int width) {
		if(getDefinitionLayers().getIndex(index) == null) return null;

		return getDefinitionLayers().getIndex(index).getTexturePreview(width, 30);
	}

	public void setShadowStrength(double value) {
		layers.shadowstrength = value;
	}

	public void setValue(MainController controller, int previewWidth, int previewHeight, ParameterType type, int index, Number value) {
		switch(type) {
		case SHADOWSTRENGTH:
			layers.shadowstrength = (double)value;
			break;
		case BRIGHTNESSSTRENGTH:
			layers.brightnessStrength = (double) value;
			break;
		case BRIGHTCOLOR:
			layers.brightnessColorArgb = (int)value;
			break;
		case BLURSTRENGTH:
			layers.blurStrength = (double)value;
			break;
		case BLURSPCOLOR:
			layers.blurSpecialColor = ((int)value != 0);
			break;
		case USINGCOLOR2:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).usingColor2 = ((int)value != 0);
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case USINGCOLOR3:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).usingColor3 = ((int)value != 0);
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case INDEXCOLOR1:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).setRealColor((int)value, 0);
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case INDEXCOLOR2:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).setRealColor((int)value, 1);
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case INDEXCOLOR3:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).setRealColor((int)value, 2);
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case INDEXPOS1:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).position1 = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case INDEXPOS2:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).position2 = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case INDEXPOS3:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).position3 = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case BLURTHISINDEX:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).blurMe = ((int)value != 0);
			break;
		case TEXTUREOFFSETX:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureOffsetX = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTUREOFFSETY:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureOffsetY = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTUREROTATE:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureRotate = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTUREZOOMX:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureZoomX = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTUREZOOMY:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureZoomY = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTURERATIO:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureRatio = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTURESHEARX:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureShearX = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		case TEXTURESHEARY:
			if(layers.getIndex(index) == null) return;
			layers.getIndex(index).textureShearY = (double)value;
			layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
			break;
		default:
			break;
		}
	}

	public void setValue(MainController controller, ParameterType type, int index, String value) {
		switch(type) {
		case INDEXNAME:
			layers.getIndex(index).name = value;
			break;
		default:
			break;
		}

	}

	public Number getValue(ParameterType type, int index) {
		switch(type) {
		case SHADOWSTRENGTH:
			return layers.shadowstrength;
		case BRIGHTNESSSTRENGTH:
			return layers.brightnessStrength;
		case BRIGHTCOLOR:
			return layers.brightnessColorArgb;
		case BLURSTRENGTH:
			return layers.blurStrength;
		case BLURSPCOLOR:
			return layers.blurSpecialColor ? 1: 0;
		case INDEXCOLOR1:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).getRealColor(0);
		case INDEXCOLOR2:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).getRealColor(1);
		case INDEXCOLOR3:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).getRealColor(2);
		case INDEXPOS1:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).position1;
		case INDEXPOS2:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).position2;
		case INDEXPOS3:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).position3;
		case BLURTHISINDEX:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).blurMe ? 1 : 0;
		case USINGCOLOR2:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).usingColor2 ? 1 : 0;
		case USINGCOLOR3:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).usingColor3 ? 1 : 0;
		case TEXTUREOFFSETX:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureOffsetX;
		case TEXTUREOFFSETY:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureOffsetY;
		case TEXTUREROTATE:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureRotate;
		case TEXTUREZOOMX:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureZoomX;
		case TEXTUREZOOMY:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureZoomY;
		case TEXTURERATIO:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureRatio;
		case TEXTURESHEARX:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureShearX;
		case TEXTURESHEARY:
			if(layers.getIndex(index) == null) return 0;
			return layers.getIndex(index).textureShearY;
		default:
			break;
		}

		return 0;
	}

	//テクスチャを追加する
	public void setTextureImage(MainController controller, int previewWidth, int previewHeight, int index, Image source) {
		if(source == null) return;

		int imageWidth = (int)source.getWidth();
		int imageHeight = (int)source.getHeight();
		int colors[] = ImageUtility.toIntArray(source);

		if(layers.getIndex(index) == null) return;
		layers.getIndex(index).setTexture(imageWidth, imageHeight, colors);
		layers.getIndex(index).applyTexture(controller, previewWidth, previewHeight);
	}

	//スタンプを追加する
	public void addStamp(MainController controller, String name, Image source) {
		int imageWidth = (int)source.getWidth();
		int imageHeight = (int)source.getHeight();
		int imageData[] = ImageUtility.toIntArray(source);

		layers.addStamp(new StampInfo(name, imageWidth, imageHeight, imageData, controller.currentDisplayLeft, controller.currentDisplayTop));
	}

	public Image getTexuture(int index, int width, int height) {
		if(layers.getIndex(index) == null) return null;
		return layers.getIndex(index).getTexturePreview(width, height);
	}
}
