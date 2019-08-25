package application;

import java.util.ArrayList;
import java.util.List;

import enums.EditMode;
import enums.PreviewMode;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import model.utility.ColorUtility;
import model.utility.ImageUtility;

public class EditToolBar extends ToolBar {
	private final List<ToggleButton> listIndexSelector;

	public EditToolBar(MainController controller) {
		listIndexSelector = new ArrayList<>();

		Button btnDefLoad = new Button();
		btnDefLoad.setGraphic(new ImageView(ImageUtility.toImage("images/defadd.png", 0)));
		btnDefLoad.setOnAction(evt -> controller.loadDefinitionImage());
		this.getItems().add(btnDefLoad);


		Button btnSaveProject = new Button();
		btnSaveProject.setGraphic(new ImageView(ImageUtility.toImage("images/projsave.png", 0)));
		btnSaveProject.setOnAction(evt -> controller.saveProject());
		controller.ProjectProperty().addListener(evt -> btnSaveProject.disableProperty().set(!controller.getProject().projectActive));
		this.getItems().add(btnSaveProject);

		Button btnLoadPRoject = new Button();
		btnLoadPRoject.setGraphic(new ImageView(ImageUtility.toImage("images/projopen.png", 0)));
		btnLoadPRoject.setOnAction(evt -> controller.loadProject());
		this.getItems().add(btnLoadPRoject);

		Button btnApplyPattern = new Button();
		btnApplyPattern.setGraphic(new ImageView(ImageUtility.toImage("images/paintapply.png", 0)));
		btnApplyPattern.setOnAction(evt -> controller.applyPattern());
		controller.ProjectProperty().addListener(evt -> btnApplyPattern.disableProperty().set(!controller.getProject().projectActive));
		this.getItems().add(btnApplyPattern);

		Button btnExportPattern = new Button();
		btnExportPattern.setGraphic(new ImageView(ImageUtility.toImage("images/paintexport.png", 0)));
		btnExportPattern.setOnAction(evt -> controller.exportPattern());
		controller.ProjectProperty().addListener(evt -> btnExportPattern.disableProperty().set(!controller.getProject().projectActive));
		this.getItems().add(btnExportPattern);

		Button btnExportProject = new Button();
		btnExportProject.setGraphic(new ImageView(ImageUtility.toImage("images/compexport.png", 0)));
		btnExportProject.setOnAction(evt -> controller.exportProject());
		controller.ProjectProperty().addListener(evt -> btnExportProject.disableProperty().set(!controller.getProject().projectActive));
		this.getItems().add(btnExportProject);

		Button btnCloseProject = new Button("閉じる");
		btnLoadPRoject.setGraphic(new ImageView(ImageUtility.toImage("images/projopen.png", 0)));
		btnCloseProject.setOnAction(evt -> controller.closeProject());
		this.getItems().add(btnCloseProject);

		this.getItems().add(new Separator(Orientation.VERTICAL));

		ToggleGroup previewMode = new ToggleGroup();

		ToggleButton tglShadowOnly = new ToggleButton("影のみ");
		tglShadowOnly.setToggleGroup(previewMode);
		tglShadowOnly.selectedProperty().addListener((evt, ov, nv) -> {
			if(nv) controller.setPreviewMode(PreviewMode.SHADOWONLY);
		});
		controller.PreviewModeProperty().addListener(evt ->
						tglShadowOnly.selectedProperty().set(controller.getPreviewMode() == PreviewMode.SHADOWONLY));
		tglShadowOnly.setSelected(true);
		this.getItems().add(tglShadowOnly);

		ToggleButton tglMapOnly = new ToggleButton("マップのみ");
		tglMapOnly.setToggleGroup(previewMode);
		tglMapOnly.selectedProperty().addListener((evt, ov, nv) -> {
			if(nv) controller.setPreviewMode(PreviewMode.INDEXMAP);
		});
		controller.PreviewModeProperty().addListener(evt ->
						tglMapOnly.selectedProperty().set(controller.getPreviewMode() == PreviewMode.INDEXMAP));
		this.getItems().add(tglMapOnly);

		ToggleButton tglRealColor = new ToggleButton("影なし");
		tglRealColor.setToggleGroup(previewMode);
		tglRealColor.selectedProperty().addListener((evt, ov, nv) -> {
			if(nv) controller.setPreviewMode(PreviewMode.REALINDEX);
		});
		controller.PreviewModeProperty().addListener(evt ->
						tglRealColor.selectedProperty().set(controller.getPreviewMode() == PreviewMode.REALINDEX));
		this.getItems().add(tglRealColor);


		ToggleButton tglPreview = new ToggleButton("完全");
		tglPreview.setToggleGroup(previewMode);
		tglPreview.selectedProperty().addListener((evt, ov, nv) -> {
			if(nv) controller.setPreviewMode(PreviewMode.EXPORTPREVIEW);
		});
		controller.PreviewModeProperty().addListener(evt ->
						tglPreview.selectedProperty().set(controller.getPreviewMode() == PreviewMode.EXPORTPREVIEW));
		this.getItems().add(tglPreview);

		ToggleButton tglReduced = new ToggleButton("減色");
		tglReduced.setToggleGroup(previewMode);
		tglReduced.selectedProperty().addListener((evt, ov, nv) -> {
			if(nv) controller.setPreviewMode(PreviewMode.REDUCED);
		});
		controller.PreviewModeProperty().addListener(evt ->
						tglReduced.selectedProperty().set(controller.getPreviewMode() == PreviewMode.REDUCED));
		this.getItems().add(tglReduced);

		this.getItems().add(new Separator(Orientation.VERTICAL));
		
		//プロジェクトがリセットされたらボタンをリセットする
		ToggleGroup propertyGroup = new ToggleGroup();
		controller.ProjectProperty().addListener(evt -> updateIndexButtons(controller, propertyGroup));
	}

	private void updateIndexButtons(MainController controller, ToggleGroup propertyGroup) {
		//既存のレイヤートグルボタンを削除する
		for(ToggleButton tgl : listIndexSelector) {
			this.getItems().remove(tgl);
		}
		listIndexSelector.clear();

		if(!controller.getProject().projectActive) return;

		//新プロジェクトのレイヤートグルボタンを追加する
		ToggleButton generating = generateToggleButton(EditMode.GENERALS, 0, "GEN", 0x0000_A0A0, controller);
		generating.setToggleGroup(propertyGroup);
		this.getItems().add(generating);
		listIndexSelector.add(generating);

		generating.setSelected(true);

		generating = generateToggleButton(EditMode.INDEXES, -1, "IDX", 0x00A0_00A0, controller);
		generating.setToggleGroup(propertyGroup);
		this.getItems().add(generating);
		listIndexSelector.add(generating);

		/*
		generating = generateToggleButton(EditMode.STAMPS, -1, "STAMP", 0x0000_0000, controller);
		generating.setToggleGroup(propertyGroup);
		this.getItems().add(generating);
		listIndexSelector.add(generating);
		*/

	}

	private ToggleButton generateToggleButton(EditMode mode, int index, String text, int color, MainController controller) {
		ToggleButton tglButton = new ToggleButton(text);

		tglButton.setBackground(new Background(generateButtonFillOfIndex(ColorUtility.toColor(color))));
		tglButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(0.5))));
		Color clrIndex = ColorUtility.toColor(color);
		tglButton.selectedProperty().addListener(evt -> {
			if(tglButton.isSelected()) {
				tglButton.setBackground(new Background(generateButtonFillOfIndex(clrIndex), generatePressedFillOfIndex()));
				tglButton.setBorder(new Border(new BorderStroke(Color.AQUA, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(0.5))));
				controller.setEditMode(mode, index);
			}
			else {
				tglButton.setBackground(new Background(generateButtonFillOfIndex(clrIndex)));
				tglButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(0.5))));
			}
		});

		return tglButton;
	}

	private BackgroundFill generateButtonFillOfIndex(Color clrIndex) {
		Stop stopTrans = new Stop(0.5, Color.TRANSPARENT);
		Stop stopIndex = new Stop(0.75, clrIndex);
		LinearGradient paint = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stopTrans, stopIndex);
		return new BackgroundFill(paint, new CornerRadii(2), new Insets(1));
	}

	private BackgroundFill generatePressedFillOfIndex() {
		Stop stopDark = new Stop(0, Color.gray(0, 0.2));
		Stop stopTrans = new Stop(0.75, Color.TRANSPARENT);
		LinearGradient paint = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stopDark, stopTrans);
		return new BackgroundFill(paint, new CornerRadii(2), new Insets(1));
	}
}
