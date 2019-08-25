package model;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

import application.MainController;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import model.utility.ColorUtility;
import model.utility.ImageUtility;

public class DefinitionIndex implements Serializable {
	private final int width, height;
	private int mappedData[];
	private int colorArgb, indexArgb, color2Argb, color3Argb;
	private double minDegree, maxDegree;
	private final double allowRange;
	public boolean blurMe, usingColor2, usingColor3;
	public double position1, position2, position3;
	private int textureWidth, textureHeight, textureData[];
	public double textureRotate, textureZoomX, textureZoomY, textureRatio, textureShearX, textureShearY;
	public double textureOffsetX, textureOffsetY;
	public String name;

	public DefinitionIndex() {
		this.width = 0;
		this.height = 0;
		this.indexArgb = 0xFFFF_FFFF;
		this.colorArgb = 0xFFFF_FFFF;
		this.color2Argb = 0xFFFF_FFFF;
		this.color3Argb = 0xFFFF_FFFF;
		this.position1 = 0;
		this.position2 = 0;
		this.position3 = 0;
		this.minDegree = 0;
		this.maxDegree = 0;
		this.allowRange = 0;
		this.blurMe = false;
		this.usingColor2 = false;
		this.usingColor3 = false;
		this.textureData = null;
		this.textureWidth = 0;
		this.textureHeight = 0;
		this.textureRotate = 0;
		this.textureZoomX = 0;
		this.textureZoomY = 0;
		this.textureRatio = 0;
		this.textureShearX = 0;
		this.textureShearY = 0;
		this.textureOffsetX = 0;
		this.textureOffsetY = 0;
		this.mappedData = null;
		this.name = new String();
	}

	public DefinitionIndex(String name, int width, int height, int colorArgb, double hueDegree, double allowRange) {
		this.width = width;
		this.height = height;
		this.indexArgb = colorArgb;
		this.colorArgb = colorArgb;
		this.color2Argb = 0xFF00_0000;
		this.color3Argb = 0xFFFF_FFFF;
		this.position1 = 0.25;
		this.position2 = 1;
		this.position3 = 0;
		this.minDegree = hueDegree;
		this.maxDegree = hueDegree;
		this.allowRange = allowRange;
		this.blurMe = true;
		this.usingColor2 = false;
		this.usingColor3 = false;
		this.textureData = null;
		this.textureWidth = 0;
		this.textureHeight = 0;
		this.textureRotate = 0;
		this.textureZoomX = 0;
		this.textureZoomY = 0;
		this.textureRatio = 0;
		this.textureShearX = 0;
		this.textureShearY = 0;
		this.textureOffsetX = 0;
		this.textureOffsetY = 0;
		this.name = name;
	}

	public final int getRealColor(int index) {
		if(index == 0) return colorArgb;
		if(index == 1) return color2Argb;
		if(index == 2) return color3Argb;
		return colorArgb;
	}
	public final int getRealColor(int x, int y, double shadowStrength) {
		int realColor = getGradiationColor(shadowStrength);
		if(mappedData == null) return realColor;
		int mapColor = ColorUtility.mixColor(mappedData[x + y * width], 0xFF00_0000, 1 - shadowStrength);

		return ColorUtility.mixColor(realColor, mapColor, 1 - textureRatio);
	}
	public final void setRealColor(int argb, int index) {
		if(index == 0) colorArgb = argb;
		if(index == 1) color2Argb = argb;
		if(index == 2) color3Argb = argb;
	}
	public final int getIndexColor() { return indexArgb; }
	public final int getGradiationColor(double shadowStrength) {
		SortedMap<Double, Integer> grad = new TreeMap<>();
		grad.put(position1, colorArgb);
		if(usingColor2) grad.put(position2, color2Argb);
		if(usingColor3) grad.put(position3, color3Argb);

		if(grad.size() == 1) {
			return ColorUtility.mixColor(colorArgb, 0xFF00_0000, 1 - shadowStrength);
		}

		SortedMap<Double, Integer> prev = grad.headMap(shadowStrength);
		SortedMap<Double, Integer> next = grad.tailMap(shadowStrength);

		if(prev.size() == 0) {
			return next.get(next.firstKey());
		}
		else if(next.size() == 0) {
			return prev.get(prev.lastKey());
		}
		else {
			double prevPos = prev.lastKey();
			double nextPos = next.firstKey();
			int prevColor = prev.get(prevPos);
			int nextColor = next.get(nextPos);

			return ColorUtility.mixColor(prevColor, nextColor, (nextPos - shadowStrength) / (nextPos - prevPos));
		}
	}

	//許容する温度であるか検定する
	public boolean isHueAllowed(double targetHue) {
		double centerDegree = minDegree / 2 + maxDegree / 2;
		double maxAllowed = centerDegree + allowRange / 2;
		if(maxAllowed >= 360) maxAllowed -= 360;
		double minAllowed = centerDegree - allowRange / 2;
		if(maxAllowed <= 0) minAllowed += 360;

		if(maxAllowed < minAllowed) {
			return targetHue <= maxAllowed || minAllowed <= targetHue;
		}
		else {
			return minAllowed <= targetHue && targetHue <= maxAllowed;
		}
	}

	//指定した温度を範囲に追加する
	public void expandRange(double addingHue) {
		if(!isHueAllowed(addingHue)) return;

		//指定した温度がすでに範囲内の場合は処理終了
		if(maxDegree < minDegree) {
			if(addingHue <= maxDegree || minDegree <= addingHue) return;
		}
		else {
			if(minDegree <= addingHue && addingHue <= maxDegree) return;
		}

		//追加範囲がMAX寄りの場合はMAXを変更する
		double addingMinRange = addingHue - minDegree;
		if(addingMinRange < 0) addingMinRange += 360;
		double maxAddingRange = maxDegree - addingHue;
		if(maxAddingRange < 0) maxAddingRange += 360;

		if(addingMinRange < maxAddingRange)	maxDegree = addingHue;
		else								minDegree = addingHue;

		//中間色をインデックスの色に変更する
		double centerHue = (minDegree + maxDegree) / 2;
		if(maxDegree < minDegree) centerHue -= 360 / 2;
		if(centerHue < 0) centerHue += 360;
		indexArgb = ColorUtility.toArgb(Color.hsb(centerHue, 1, 1));
	}

	//テクスチャを設定する
	public void setTexture(int imageWidth, int imageHeight, int[] colors) {
		this.textureWidth = imageWidth;
		this.textureHeight = imageHeight;
		this.textureData = colors;
	}

	//テクスチャのプレビュー画像を生成する
	public Image getTexturePreview(int width, int height) {
		int previewData[] = new int[width * height];

		if(textureData == null) {
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					previewData[x + y * width] = getRealColor(x, y, (double)x / width);
				}
			}
		}
		else {
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					previewData[x + y * width] = textureData[(x % this.textureWidth) + (y % this.textureHeight) * (this.textureWidth)];
				}
			}
		}

		return ImageUtility.toImage(width, height, previewData);
	}


	//テクスチャまたはリアルカラーを適用する
	public void applyTexture(MainController controller, int requestWidth, int requestHeight) {
		mappedData = new int[width * height];

		if(textureData == null) {
			//リアルカラーを設定する
			mappedData = null;
		}
		else {
			//テクスチャを設定する
			Canvas canvas = new Canvas(width, height);
			GraphicsContext gc = canvas.getGraphicsContext2D();
			Affine affine = new Affine();
			affine.appendScale(Math.pow(4, -textureZoomX), Math.pow(4, -textureZoomY));
			affine.appendRotation(textureRotate, width / 2, height / 2);
			affine.appendShear(textureShearX, textureShearY);

			Point2D topLeft = new Point2D(0, 0);
			Point2D topRight = new Point2D(width, 0);
			Point2D bottomRight = new Point2D(width, height);
			Point2D bottomLeft = new Point2D(0, height);
			double invLeft = 0, invRight = width, invTop = 0,invBottom = height;
			try {
				topLeft = affine.inverseTransform(topLeft);
				topRight = affine.inverseTransform(topRight);
				bottomRight = affine.inverseTransform(bottomRight);
				bottomLeft = affine.inverseTransform(bottomLeft);

				invLeft = Math.min(Math.min(topLeft.getX(), topRight.getX()), Math.min(bottomLeft.getX(), bottomRight.getX()));
				invRight = Math.max(Math.max(topLeft.getX(), topRight.getX()), Math.max(bottomLeft.getX(), bottomRight.getX()));
				invTop = Math.min(Math.min(topLeft.getY(), topRight.getY()), Math.min(bottomLeft.getY(), bottomRight.getY()));
				invBottom = Math.max(Math.max(topLeft.getY(), topRight.getY()), Math.max(bottomLeft.getY(), bottomRight.getY()));

			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}

			int pixelOffsetX = (int)(textureWidth * textureOffsetX);
			int pixelOffsetY = (int)(textureHeight * textureOffsetY);
			Image textureImage = ImageUtility.toImage(textureWidth, textureHeight, textureData, pixelOffsetX, pixelOffsetY);
			gc.transform(affine);

			for(double x = invLeft; x < invRight; x += textureWidth) {
				for(double y = invTop; y < invBottom; y += textureHeight) {
					gc.drawImage(textureImage, x, y);
				}
			}

			mappedData = ImageUtility.toIntArray(canvas.snapshot(null, null));
		}

		if(controller != null) controller.setTextureImage(getTexturePreview(requestWidth, requestHeight));
	}
}
