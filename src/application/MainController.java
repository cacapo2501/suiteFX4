package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import enums.EditMode;
import enums.PreviewMode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.DefinitionLayers;
import model.Model;
import model.SFXProject;
import model.utility.ImageUtility;

public class MainController {
	private final BooleanProperty commonEditEnabled = new SimpleBooleanProperty();
	public final BooleanProperty CommonEditEnabled() { return commonEditEnabled; }

	private final StringProperty statusMessageProperty = new SimpleStringProperty();
	public final StringProperty StatusMessageProperty() { return statusMessageProperty; }
	public void setMessage(String value) { statusMessageProperty.set(value); };

	private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1);
	public final DoubleProperty ZoomProperty() { return zoomProperty; }
	public double getZoom() { return zoomProperty.get(); }
	public void setZoom(double value) { zoomProperty.set(value); }

	private final ObjectProperty<Image> previewImageProperty = new SimpleObjectProperty<Image>(null);
	public final ObjectProperty<Image> PreviewImageProperty() { return previewImageProperty; }
	public final void setPreviewImage(Image image) { previewImageProperty.set(image); }
	public Image getPreviewImage() { return previewImageProperty.get(); };

	private final ObjectProperty<Image> textureImageProperty = new SimpleObjectProperty<>(null);
	public final ObjectProperty<Image> TextureImageProperty() { return textureImageProperty; }
	public final void setTextureImage(Image image) { textureImageProperty.set(image); }
	public final Image getTextureImage(int index) { return textureImageProperty.get(); }

	private final ObjectProperty<SFXProject> projectProperty = new SimpleObjectProperty<>(new SFXProject());
	public final ObjectProperty<SFXProject> ProjectProperty() { return projectProperty; }
	public final void setProject(SFXProject project) { projectProperty.set(project); }
	public final SFXProject getProject() { return projectProperty.get(); }

	private final DoubleProperty horizontalScrollPositionProperty = new SimpleDoubleProperty();
	public final DoubleProperty HorizontalScrollPositionProperty() { return horizontalScrollPositionProperty; }
	private final DoubleProperty verticalScrollPositionProperty = new SimpleDoubleProperty();
	public final DoubleProperty VerticalScrollPositionProperty() { return verticalScrollPositionProperty; }

	private final ObjectProperty<PreviewMode> previewModeProperty = new SimpleObjectProperty<>(PreviewMode.EXPORTPREVIEW);
	public final ObjectProperty<PreviewMode> PreviewModeProperty() { return previewModeProperty; }
	public final PreviewMode getPreviewMode() { return previewModeProperty.get(); }
	public final void setPreviewMode(PreviewMode mode) { previewModeProperty.set(mode); }

	private final ObjectProperty<EditMode> editModeProperty = new SimpleObjectProperty<>(EditMode.GENERALS);
	private final IntegerProperty editIndexProperty = new SimpleIntegerProperty(0);
	public final ObjectProperty<EditMode> EditModeProperty() { return editModeProperty; }
	public final IntegerProperty EditIndexProperty() { return editIndexProperty; }
	public final EditMode getEditMode() { return editModeProperty.get(); }
	public final int getEditIndex() { return editIndexProperty.get(); }
	public final void setEditMode(EditMode mode, int index) {
		editIndexProperty.set(index);
		editModeProperty.set(mode);
	}

	private final ObjectProperty<ToggleButton> selectedButtonProperty = new SimpleObjectProperty<>();
	public final ObjectProperty<ToggleButton> SelectedButtonProperty() { return selectedButtonProperty; }
	public ToggleButton getSelectedButton() { return selectedButtonProperty.get(); }
	public void setSelectedButton(ToggleButton button) { selectedButtonProperty.set(button); }
	public int currentDisplayLeft, currentDisplayTop;				//現在表示されている左上の座標

	public MainController(Model model) {
		projectProperty.addListener(evt -> setPreviewImage(getProject().getPreviewImage(getPreviewMode())));
		previewModeProperty.addListener(evt -> setPreviewImage(getProject().getPreviewImage(getPreviewMode())));
	}

	public void loadDefinitionImage() {
		//ユーザにファイルを選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("定義画像ファイル", "*.png"));
		File defFile = chooser.showOpenDialog(null);
		if(defFile != null) {
			setProject(Model.getInstance().loadDefinitionImage(defFile, this));
		}
	}
	public void closeProject() {
		setProject(new SFXProject());
	}

	public void saveProject() {
		//保存先を選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("SuiteFX4プロジェクトファイル", "*.sfx4"));
		File projFile = chooser.showSaveDialog(null);
		if(projFile != null) {
			try(FileOutputStream fos = new FileOutputStream(projFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos)){
				oos.writeObject(getProject().getDefinitionLayers());
			} catch (FileNotFoundException e) {
				setMessage("保存先が見つかりませんでした。保存できません。");
			} catch (IOException e) {
				setMessage("保存に失敗しました。");
			}
		};
	}
	public void loadProject() {
		//ユーザにファイルを選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("SuiteFX4プロジェクトファイル", "*.sfx4"));
		File projFile = chooser.showOpenDialog(null);
		if(projFile != null) {
			try(FileInputStream fis = new FileInputStream(projFile);
				ObjectInputStream ois = new ObjectInputStream(fis)){
				DefinitionLayers layers = (DefinitionLayers)ois.readObject();
				setProject(new SFXProject(layers, this));
			} catch (FileNotFoundException e) {
				setMessage("ファイルが見つかりませんでした。読み込みできません。");
			} catch (IOException e) {
				setMessage("ファイルが壊れています。読み込みできません。");
			} catch (ClassNotFoundException e) {
				setMessage("ファイルが壊れています。読み込みできません。");
			}
		}
	}
	public void applyPattern() {
		//ユーザにファイルを選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("塗装パターン画像ファイル", "*.png"));
		File patternFile = chooser.showOpenDialog(null);
		if(patternFile != null) {
			if(Model.getInstance().checkAbleToApplyPattern(getProject(), patternFile)) {
				Image patternImage = new Image(patternFile.toURI().toString());
				setProject(new SFXProject(getProject().getDefinitionLayers(), patternImage, this));
			}
		}
	}

	public Image getTextureSource() {
		//ユーザにファイルを選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("テクスチャ画像ファイル", "*.png", "*.jpg"));
		File defFile = chooser.showOpenDialog(null);
		if(defFile != null) {
			return new Image(defFile.toURI().toString());
		}
		return null;
	}

	public void exportPattern() {
		//ユーザにファイルを選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("塗装パターン画像ファイル", "*.png"));
		File patternFile = chooser.showSaveDialog(null);
		if(patternFile != null) {
			if(ImageUtility.saveAsPNG24Image(getProject().getPreviewImage(PreviewMode.INDEXMAP), patternFile)) {
				setMessage("塗装パターンベース画像をエクスポートしました。");
			}
			else {
				setMessage("塗装パターンベース画像のエクスポートに失敗しました。");
			}
		}
	}

	public void exportProject() {
		//ユーザにファイルを選択させる
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("フルイメージファイル", "*.png"));
		File patternFile = chooser.showSaveDialog(null);
		if(patternFile != null) {
			if(ImageUtility.saveAsPNG24Image(getProject().getPreviewImage(PreviewMode.EXPORTPREVIEW), patternFile)) {
				setMessage("フルイメージをエクスポートしました。");
			}
			else {
				setMessage("フルイメージのエクスポートに失敗しました。");
			}
		}
	}
}
