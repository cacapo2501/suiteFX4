package model.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

public class ImageUtility {

	public static Image trimImage(Image source, int offsetX, int offsetY, int trimWidth, int trimHeight) {
		int srcData[] = toIntArray(source);
		int dstData[] = new int[trimWidth * trimHeight];

		int srcWidth = (int)source.getWidth();
		for(int i = 0;i < trimWidth; i++) {
			for(int j = 0; j < trimHeight; j++) {
				dstData[i + j * trimWidth] = srcData[offsetX + i + (offsetY + j) * srcWidth];
			}
		}

		return toImage(dstData, trimWidth, trimHeight);
	}

	public static Image zoomImage(Image srcImage, int zoom) {
		int dstWidth = (int)srcImage.getWidth() * zoom;
		int dstHeight = (int)srcImage.getHeight() * zoom;

		int srcData[] = toIntArray(srcImage);
		int dstData[] = new int[dstWidth * dstHeight];

		int srcWidth = (int)srcImage.getWidth();
		for(int x = 0; x < dstWidth; x++) {
			for(int y = 0; y < dstHeight; y++) {
				int srcX = x / zoom;
				int srcY = y / zoom;
				dstData[x + y * dstWidth] = srcData[srcX + srcY * srcWidth];
			}
		}

		return toImage(dstData, dstWidth, dstHeight);
	}

	public static Image toImage(String uri, int transparent) {
		Image source = new Image(uri);

		int data[] = toIntArray(source);

		for(int i = 0;i < data.length; i++) {
			if((data[i] & 0x00FF_FFFF) == transparent) {
				data[i] = 0;
			}
		}

		return toImage(data, (int)source.getWidth(), (int)source.getHeight());
	}

	private static Image toImage(int[] data, int width, int height) {
		WritableImage resImage = new WritableImage(width, height);
		PixelWriter writer = resImage.getPixelWriter();

		writer.setPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), data, 0, width);

		return resImage;
	}

	public static int[] toIntArray(Image image) {
		PixelReader reader = image.getPixelReader();

		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		int res[] = new int[width * height];

		reader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), res, 0, width);

		return res;
	}

	public static Image toImage(int width, int height, int[] data) {
		return toImage(data, width, height);
	}

	public static Image toImage(int width, int height, int[] data, double opacity) {
		int iOpacity = (int)((1 - opacity) * 255);
		iOpacity <<= 24;

		int modData[] = new int[data.length];
		for(int i = 0;i < data.length; i++) {
			modData[i] = iOpacity | (data[i] & 0x00FF_FFFF);
		}
		return toImage(modData, width, height);
	}

	public static Image toImage(int width, int height, int[] data, int xOffset, int yOffset) {
		int iOpacity = 255;
		iOpacity <<= 24;

		int modData[] = new int[data.length];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int shiftX = (x + xOffset) % width;
				int shiftY = (y + yOffset) % height;
				modData[shiftX + shiftY * width] = iOpacity | (data[x + y * width] & 0x00FF_FFFF);
			}
		}
		return toImage(modData, width, height);
	}

	public static boolean saveAsPNG24Image(Image image, File target) {
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		PixelReader reader = image.getPixelReader();

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				bimg.setRGB(x, y, reader.getArgb(x, y));
			}
		}

		try {
			ImageIO.write(bimg, "png", target);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
