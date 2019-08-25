package enums;

import javafx.scene.paint.Color;

public enum SpecialColor {
	TRANSPARENT	( 1, "0xE7FFFF", true , false, false, false),
	MENUGRAY1		( 2, "0x6B6B6B", false, false, false, true ),
	MENUGRAY2		( 3, "0x9B9B9B", false, false, false, true ),
	MENUGRAY3		( 4, "0xB3B3B3", false, false, false, true ),
	MENUGRAY4		( 5, "0xC9C9C9", false, false, false, true ),
	MENUGRAY5		( 6, "0xDFDFDF", false, false, false, true ),
	WINDOW			( 7, "0x57656F", false, true , false, false),
	BLUELIGHT		( 8, "0x7F9BF1", false, false, true , false),
	YELLOWLIGHT	( 9, "0xFFFF53", false, false, true , false),
	REDLIGHT		(10, "0xFF211D", false, false, true , false),
	GREENLIGHT		(11, "0x01DD01", false, false, true , false),
	WHITELIGHT		(12, "0xE3E3FF", false, false, true , false),
	BLUEWINDOW		(13, "0xC1B1D1", false, true , false, false),
	DARKWINDOW		(14, "0x4D4D4D", false, true , false, false),
	PINKLIGHT		(15, "0xFF017F", false, false, true , false),
	DARKBLUELIGHT	(16, "0x0101FF", false, false, true , false),
	PRIMARY1		(17, "0x244B67", false, false, false, false),
	PRIMARY2		(18, "0x395E7C", false, false, false, false),
	PRIMARY3		(19, "0x4C7191", false, false, false, false),
	PRIMARY4		(20, "0x6084A7", false, false, false, false),
	PRIMARY5		(21, "0x7497BD", false, false, false, false),
	PRIMARY6		(22, "0x88ABD3", false, false, false, false),
	PRIMARY7		(23, "0x9CBEE9", false, false, false, false),
	PRIMARY8		(24, "0xB0D2FF", false, false, false, false),
	SECONDARY1		(25, "0x7B5803", false, false, false, false),
	SECONDARY2		(26, "0x8E6F04", false, false, false, false),
	SECONDARY3		(27, "0xA18605", false, false, false, false),
	SECONDARY4		(28, "0xB49D07", false, false, false, false),
	SECONDARY5		(29, "0xC6B408", false, false, false, false),
	SECONDARY6		(30, "0xD9CB0A", false, false, false, false),
	SECONDARY7		(31, "0xECE20B", false, false, false, false),
	SECONDARY8		(32, "0xFFF90D", false, false, false, false),
	
	;
	private int order;
	private Color color;
	private boolean transparent, window, light, gray;
	private SpecialColor(int order, String value, boolean transparent, 
							boolean window, boolean light, boolean gray) {
		//ソースの色
		this.color = Color.web(value);
		//透過フラグ
		this.transparent = transparent;
		//窓属性
		this.window = window;
		//灯火属性
		this.light = light;
		//灰色属性
		this.gray = gray;
	}
	public Color getColor() { return color; }
	public boolean isTransparent() { return transparent; }
	public boolean isWindow() { return window; }
	public boolean isLight() { return light; }
	public boolean isGray() { return gray; }
	public int getOrder() { return order; }
	
}
