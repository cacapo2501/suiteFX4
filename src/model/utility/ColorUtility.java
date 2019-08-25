package model.utility;

import enums.SpecialColor;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ColorUtility {

	public static Color toColor(int color) {
		return Color.rgb(getRed(color), getGreen(color), getBlue(color));
	}

	private static int getBlue(int color) { return color & 0x0000_00FF; }
	private static int getGreen(int color) { return (color & 0x0000_FF00) >>> 8; }
	private static int getRed(int color) { return (color & 0x00FF_0000) >>> 16; }

	public static int toArgb(int red, int green, int blue) {
		int ir = red, ig = green, ib = blue;
		if(ir > 255) ir = 255;
		if(ir < 0)   ir = 0;
		if(ig > 255) ig = 255;
		if(ig < 0)   ig = 0;
		if(ib > 255) ib = 255;
		if(ib < 0)   ib = 0;
		return 0xFF00_0000 | (ir << 16) | (ig << 8) | ib;
	}

	public static boolean isSpecial(Color c) {
		for(SpecialColor spc : SpecialColor.values()) {
			if(isEqual(c, spc.getColor())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEqual(Color c1, Color c2) {
		return toArgb(c1) == toArgb(c2);
	}

	public static int toArgb(Color c) {
		int red = (int)(c.getRed() * 255);
		int green = (int)(c.getGreen() * 255);
		int blue = (int)(c.getBlue() * 255);
		return 0xff00_0000 | (red << 16) | (green << 8) | blue;
	}

	public static int rgbToArgb(int rgb) {
		return 0xff00_0000 | rgb;
	}

	public static  SpecialColor getSpecialColor(Color c) {
		for(SpecialColor spc : SpecialColor.values()) {
			if(isEqual(spc.getColor(), c)) {
				return spc;
			}
		}

		return null;
	}

	public static int modifyColor(int sourceRgb, double shadow, double brightness, int brightnessRgb) {
		int srcRed = getRed(sourceRgb);
		int srcGreen = getGreen(sourceRgb);
		int srcBlue = getBlue(sourceRgb);

		int brightnessRed = getRed(brightnessRgb);
		int brightnessGreen = getGreen(brightnessRgb);
		int brightnessBlue = getBlue(brightnessRgb);

		double mod_shadow = Math.min(Math.max(shadow, 0), 1);
		double mod_brightness = Math.min(Math.max(brightness, 0), 1);

		int resRed = (int)(srcRed * mod_shadow * (1 - mod_brightness) + mod_brightness * brightnessRed);
		int resGreen = (int)(srcGreen * mod_shadow * (1 - mod_brightness) + mod_brightness * brightnessGreen);
		int resBlue = (int)(srcBlue * mod_shadow * (1 - mod_brightness) + mod_brightness * brightnessBlue);

		return toArgb(resRed, resGreen, resBlue);
	}

	public static int mergeColors(int[] color, double[] weight) {
		if(color.length == weight.length) {
			double sumR = 0, sumG = 0, sumB = 0;
			double weight_total = 0;

			//重みの合計値を求める
			for(int i = 0; i < weight.length; i++) {
				weight_total += weight[i];
			}

			//色を加算する
			for(int i = 0; i < color.length; i++) {
				if(weight[i] != 0) {
					sumR += getRed(color[i]) * weight[i];
					sumG += getGreen(color[i]) * weight[i];
					sumB += getBlue(color[i]) * weight[i];
				}
			}

			int iR = (int)(sumR / weight_total);
			int iG = (int)(sumG / weight_total);
			int iB = (int)(sumB / weight_total);

			return 0xFF00_0000 | (iR << 16) | (iG << 8) | iB;
		}
		return 0;
	}

	public static double rangeColors(int color1, int color2) {
		double diffRed = getRed(color1) / 255. - getRed(color2) / 255.;
		double diffGreen = getGreen(color1) / 255. - getGreen(color2) / 255.;
		double diffBlue = getBlue(color1) / 255. - getBlue(color2) / 255.;

		return Math.sqrt(diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue);
	}

	public static int mixColor(int color1, int color2, double color1Ratio) {
		int red = (int)(getRed(color1) * color1Ratio + getRed(color2) * (1 - color1Ratio));
		int green = (int)(getGreen(color1) * color1Ratio + getGreen(color2) * (1 - color1Ratio));
		int blue = (int)(getBlue(color1) * color1Ratio + getBlue(color2) * (1 - color1Ratio));

		return toArgb(red, green, blue);
	}

	public static Paint toColor(int argb, double opacity) {
		return Color.rgb(getRed(argb), getGreen(argb), getBlue(argb), opacity);
	}

	public static int replaceUnspecialColor(int colorArgb) {
		int iRed = getRed(colorArgb) - 1;
		int resArgb = colorArgb & 0xFF00_FFFF;
		return resArgb | (iRed << 16);
	}

	public static int reduce15bitColor(int rgb) {
		int iRed = (getRed(rgb)) & 0xF8;
		int iGreen = (getGreen(rgb)) & 0xF8;
		int iBlue = (getBlue(rgb)) & 0xF8;

		return toArgb(iRed, iGreen, iBlue);
	}
}
