package model;

import java.io.File;

import application.MainController;
import javafx.scene.image.Image;

public class Model {
	private static Model instance;
	
	public static Model getInstance() {
		if(instance == null) instance = new Model();
		return instance;
	}
	
	public SFXProject loadDefinitionImage(File source, MainController controller) {
		//画像を開く
		Image defImage = new Image(source.toURI().toString());
		
		return new SFXProject(defImage, controller);
	}

	public boolean checkAbleToApplyPattern(SFXProject project, File source) {
		//画像を開く
		Image patternImage = new Image(source.toURI().toString());
		
		//縦横を取得する
		int width = (int)patternImage.getWidth();
		int height = (int)patternImage.getHeight();
		
		DefinitionLayers layers = project.getDefinitionLayers();
		
		return layers.width == width && layers.height == height;
	}
}
