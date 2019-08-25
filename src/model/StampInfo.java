package model;

import java.io.Serializable;

public class StampInfo implements Serializable {
	public String name;
	public final int width, height;
	public final int data[];
	public int posX, posY;
	
	public StampInfo() {
		this.name = "Undefined";
		this.width = 0;
		this.height = 0;
		this.data = new int[0];
		posX = 0;
		posY = 0;
	}
	
	public StampInfo(String name, int width, int height, int data[], int initialX, int initialY) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.data = data;
		this.posX = initialX;
		this.posY = initialY;
	}
}
