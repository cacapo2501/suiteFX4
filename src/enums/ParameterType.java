package enums;

public enum ParameterType {
	SHADOWSTRENGTH,
	BRIGHTNESSSTRENGTH,
	BRIGHTCOLOR,
	INDEXCOLOR1,				//integer:このインデックスの色（１色目）
	INDEXCOLOR2,				//integer:このインデックスの色（２色目）
	INDEXCOLOR3,				//integer:このインデックスの色（３色目）
	INDEXPOS1,					//Double:１色目の位置
	INDEXPOS2,					//Double:２色目の位置
	INDEXPOS3,					//Double:３色目の位置
	BLURSTRENGTH,				//Double:ぼかしの強さ
	BLURSPCOLOR,				//integer:特殊色をぼかすかどうか
	BLURTHISINDEX, 			//Boolean:このインデックスをぼかす
	USINGCOLOR2,				//Boolean:２色目を使用するか
	USINGCOLOR3,				//Boolean:２色目を使用するか
	TEXTUREROTATE,			//double:テクスチャの回転
	TEXTUREOFFSETX,			//double:テクスチャ横方向のオフセット
	TEXTUREOFFSETY,			//double:テクスチャ縦方向のオフセット
	TEXTUREZOOMX,				//double:テクスチャの横の縮小率
	TEXTUREZOOMY,				//double:テクスチャの縦の縮小率
	TEXTURERATIO,				//double:テクスチャの強さ
	TEXTURESHEARX,			//double:横方向のシャーリング
	TEXTURESHEARY,			//double:縦方向のシャーリング
	INDEXNAME,					//String:インデックスの名称
}
