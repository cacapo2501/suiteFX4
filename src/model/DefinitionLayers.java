package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import enums.PreviewMode;
import enums.SpecialColor;
import javafx.scene.paint.Color;
import model.utility.ColorUtility;

public class DefinitionLayers implements Serializable {
	public final int width, height;
	private DefinitionIndex index[];
	private SpecialColor spcolor[];
	private double shadow[];
	private int uneditable[];
	private double brightness[];
	private final List<DefinitionIndex> idxColor;
	private final List<StampInfo> stampList;
	public double shadowstrength;
	public double brightnessStrength;
	public int brightnessColorArgb;
	public double blurStrength;
	public boolean blurSpecialColor;

	public DefinitionLayers() {
		width = 0;
		height = 0;
		index = new DefinitionIndex[width * height];
		idxColor = new ArrayList<>();
		stampList = new ArrayList<>();
		spcolor = new SpecialColor[width * height];
		shadow = new double[width * height];
		uneditable = new int[width * height];
		brightness = new double[width * height];
		shadowstrength = 1;
		brightnessStrength = 1;
		brightnessColorArgb = 0xFFFF_FFFF;
		blurStrength = 0;
		blurSpecialColor = false;
	}

	public DefinitionLayers(int width, int height) {
		this.width = width;
		this.height = height;
		shadow = new double[width * height];
		brightness = new double[width * height];
		brightnessColorArgb = ColorUtility.toArgb(255, 255, 255);
		index = new DefinitionIndex[width * height];
		spcolor = new SpecialColor[width * height];
		uneditable = new int[width * height];

		idxColor = new ArrayList<DefinitionIndex>();
		stampList = new ArrayList<>();

		brightnessStrength = 1;
		shadowstrength = 1;
		blurStrength = 0;
		blurSpecialColor = false;
	}

	public DefinitionLayers(int width, int height, int color[]) {
		this(width, height);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int i = x + y * width;
				Color c = ColorUtility.toColor(color[i]);
				spcolor[i] = ColorUtility.getSpecialColor(c);

				if(spcolor[i] == null && (c.getSaturation() == 1. || c.getBrightness() == 1.)) {
					//インデックスカラーとして登録する
					DefinitionIndex idx = null;
					for(DefinitionIndex selected : idxColor) {
						if(selected.isHueAllowed(c.getHue())) {
							idx = selected;
						}
					}

					//最適なインデックスカラーが見つからない場合は登録する
					if(idx == null) {
						int argbIndex = ColorUtility.toArgb(Color.hsb(c.getHue(), 1, 1));
						idx = new DefinitionIndex("インデックス" + (idxColor.size() + 1), width, height, argbIndex, c.getHue(), 15);
						idxColor.add(idx);
					}
					//最適なインデックスカラーが見つかった場合は拡張する
					else {
						idx.expandRange(c.getHue());
					}
					index[i] = idx;

					//影の強さ・光沢の強さを登録する
					shadow[i] = 1. - c.getBrightness();
					brightness[i] = 1. - c.getSaturation();
				}
				if(spcolor[i] == null && index[i] == null) {
					uneditable[i] = ColorUtility.toArgb(c);
				}
			}
		}
	}

	public DefinitionLayers(DefinitionLayers layers, int[] pattern) {
		//インデックスを生成する
		List<DefinitionIndex> patternCount = new ArrayList<>();
		DefinitionIndex newIndex[] = new DefinitionIndex[layers.width * layers.height];
		for(int i = 0; i < layers.width * layers.height; i++) {
			if(layers.index[i] != null) {
				Color clrPattern = ColorUtility.toColor(pattern[i]);
				for(DefinitionIndex idx : patternCount) {
					if(ColorUtility.isEqual(clrPattern, ColorUtility.toColor(idx.getRealColor(i % layers.width, i / layers.width, 0)))){
						newIndex[i] = idx;
						break;
					}
				}

				if(newIndex[i] == null) {
					newIndex[i] = new DefinitionIndex("インデックス" + (patternCount.size() + 1), layers.width, layers.height, pattern[i], clrPattern.getHue(), 0);
					patternCount.add(newIndex[i]);
				}
			}
		}

		//スタンプ情報を引き継ぐ
		this.stampList = layers.stampList;

		this.width = layers.width;
		this.height = layers.height;
		shadow = layers.shadow;
		brightness = layers.brightness;
		brightnessColorArgb = layers.brightnessColorArgb;
		index = newIndex;
		spcolor = layers.spcolor;
		uneditable = layers.uneditable;

		idxColor = patternCount;

		brightnessStrength = 1;
		shadowstrength = 1;
		blurStrength = 0;
		blurSpecialColor = false;
	}

	//画像イメージをピクセルデータ配列で取得する
	public int[] getColorData(PreviewMode mode) {
		int res[] = new int[width * height];
		int sppixel = ColorUtility.toArgb(128, 0, 0);
		int transpixel = ColorUtility.toArgb(255, 255, 255);

		//**** 編集用配列にデータをコピーする ****
		SpecialColor modSpecial[] = new SpecialColor[this.spcolor.length];
		DefinitionIndex modIndex[] = new DefinitionIndex[this.index.length];
		int modUneditable[] = new int[this.uneditable.length];

		for(int i = 0; i < width * height; i++) {
			modSpecial[i] = spcolor[i];
			modIndex[i] = index[i];
			modUneditable[i] = uneditable[i];
		}

		//**** 色情報を算出する ****
		for(int i = 0; i < res.length; i++) {
			switch(mode) {
			case SHADOWONLY:
				if(modSpecial[i] != null) {
					if(modSpecial[i].isTransparent())	res[i] = transpixel;
					else							res[i] = sppixel;
				}
				else if(modIndex[i] != null) {
					int grayLevel = (int)(255. - shadow[i] * shadowstrength * 255.);
					res[i] = ColorUtility.toArgb(grayLevel, grayLevel, grayLevel);
				}
				else {
					res[i] = sppixel;
				}
				break;

			case INDEXMAP:
				if(modSpecial[i] != null) {
					if(modSpecial[i].isTransparent())	res[i] = transpixel;
					else							res[i] = sppixel;
				}
				else if(modIndex[i] != null) {
					res[i] = modIndex[i].getIndexColor();
				}
				else {
					res[i] = sppixel;
				}
				break;

			case REALINDEX:
				if(modSpecial[i] != null) {
					if(modSpecial[i].isTransparent())	res[i] = transpixel;
					else							res[i] = sppixel;
				}
				else if(modIndex[i] != null) {
					int x = i % width;
					int y = i / width;
					res[i] = modIndex[i].getRealColor(x, y, shadow[i] * shadowstrength);
				}
				else {
					res[i] = modUneditable[i];
				}

				//特殊色であったら置換する
				if(ColorUtility.isSpecial(ColorUtility.toColor(res[i]))) {
					res[i] = ColorUtility.replaceUnspecialColor(res[i]);
				}

				break;

			case EXPORTPREVIEW:
			case REDUCED:
				int x = i % width, y = i / width;

				//**** ぼかしの処理 ****
				if(isBlurable(x, y)) {
					//ぼかしを入れる色の場合
					int idxUpper = getPosition(x, y - 1);
					int idxLower = getPosition(x, y + 1);
					int idxLeft = getPosition(x - 1, y);
					int idxRight= getPosition(x + 1, y);

					double weight[] = new double[5];
					int clrIndex[] = new int[5];

					weight[0] = 1.;
					clrIndex[0] = getRealColor(i, x, y, false);

					clrIndex[1] = getBlurColor(idxLeft, idxUpper, x, y);
					clrIndex[2] = getBlurColor(idxRight, idxUpper, x, y);
					clrIndex[3] = getBlurColor(idxRight, idxLower, x, y);
					clrIndex[4] = getBlurColor(idxLeft, idxLower, x, y);

					for(int j = 0; j < 4; j++) {
						weight[j + 1] = clrIndex[j + 1] == 0 ? 0 : blurStrength;
					}

					res[i] = ColorUtility.mergeColors(clrIndex, weight);

					//特殊色であったら置換する
					if(modSpecial[i] == null && ColorUtility.isSpecial(ColorUtility.toColor(res[i]))) {
						res[i] = ColorUtility.replaceUnspecialColor(res[i]);
					}

					if(mode == PreviewMode.REDUCED) {
						res[i] = ColorUtility.reduce15bitColor(res[i]);
					}
				}
				else {
					//ぼかしを入れない色の場合
					res[i] = getRealColor(i, x, y, false);

					//特殊色であったら置換する
					if(modSpecial[i] == null && ColorUtility.isSpecial(ColorUtility.toColor(res[i]))) {
						res[i] = ColorUtility.replaceUnspecialColor(res[i]);
					}



					if(mode == PreviewMode.REDUCED) {
						res[i] = ColorUtility.reduce15bitColor(res[i]);
					}

				}


				break;
			}
		}

		return res;
	}

	//混ぜられる色どうしであるか検定する
	private int getBlurColor(int index1, int index2, int x, int y) {
		if(index1 < 0 || index2 < 0)	return 0;

		SpecialColor sp1 = spcolor[index1], sp2 = spcolor[index2];
		DefinitionIndex idx1 = index[index1], idx2 = index[index2];
		int unedit1 = uneditable[index1], unedit2 = uneditable[index2];

		if(sp1 != null || sp2 != null) {
			if(sp1 == sp2 && sp1 != null && !sp1.isTransparent() && blurSpecialColor) return ColorUtility.toArgb(sp1.getColor());
			return 0;
		}
		if(idx1 != null && idx1 == idx2) return idx1.getRealColor(x, y, shadow[x + y * width] * shadowstrength);
		if(idx1 == null && idx2 == null && ColorUtility.rangeColors(unedit1, unedit2) <= 0.25) {
			return ColorUtility.mixColor(unedit1, unedit2, 0.5);
		}
		return 0;
	}

	//影・光沢未設定の色を取得する
	private int getRealColor(int i, int x, int y, boolean reduced) {
		if(spcolor[i] != null)	{
			return ColorUtility.toArgb(spcolor[i].getColor());
		}

		int iRet = 0;		//回答する色
		if(index[i] != null) {
			iRet = index[i].getRealColor(x, y, shadow[i] * shadowstrength);
			iRet = ColorUtility.mixColor(brightnessColorArgb, iRet, brightness[i] * brightnessStrength);
		}
		else {
			iRet = uneditable[i];
		}

		//減色ありの場合は減色する
		if(reduced) iRet = ColorUtility.reduce15bitColor(iRet);

		return iRet;
	}

	//ぼかしとして使える色か検定する
	private boolean isBlurable(int x, int y) {
		if(getPosition(x, y) < 0) return false;
		int idx = getPosition(x, y);
		if(spcolor[idx] != null) return false;
		if(index[idx] != null) {
			return index[idx].blurMe;
		}
		return true;
	}

	private int getPosition(int x, int y) {
		int index = x + y * width;
		if(index < 0 || width * height <= index) index = -1;
		return index;
	}

	//インデックスの数を求める
	public int getIndexSize() { return idxColor.size(); }

	//インデックス定義を取得する
	public DefinitionIndex getIndex(int number) {
		if(number < 0 || number >= idxColor.size()) return null;
		return idxColor.get(number);
	}

	//スタンプリストの総数を取得する
	public int getStampListSize() { return stampList.size(); }

	//スタンプを追加する
	public void addStamp(StampInfo stamp) {
		stampList.add(stamp);
	}
}
