package application;

import enums.EditMode;
import enums.ParameterType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import model.DefinitionLayers;
import model.utility.ColorUtility;

public class PropertiesPane extends ScrollPane {

	public PropertiesPane(MainController controller) {
		GridPane basePane = new GridPane();

		basePane.getColumnConstraints().add(new ColumnConstraints());
		basePane.getColumnConstraints().add(new ColumnConstraints());
		basePane.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);

		controller.ProjectProperty().addListener(evt -> {
			//登録済みのコントロールをすべて削除する
			basePane.getChildren().clear();
			basePane.getRowConstraints().clear();

			if(!controller.getProject().projectActive) return;

			//新しいコントロールを登録する
			DefinitionLayers layers = controller.getProject().getDefinitionLayers();
			setSliderPropertyItem(basePane, "影の強さ", ParameterType.SHADOWSTRENGTH, EditMode.GENERALS, layers.shadowstrength, 0, 6, controller);
			setSliderPropertyItem(basePane, "光沢の強さ", ParameterType.BRIGHTNESSSTRENGTH, EditMode.GENERALS, layers.brightnessStrength, 0, 4, controller);
			setColorPickerItem(basePane, "光沢の色", ParameterType.BRIGHTCOLOR, EditMode.GENERALS, 0xFFFF_FFFF, controller);

			setSliderPropertyItem(basePane, "ぼかしの強さ", ParameterType.BLURSTRENGTH, EditMode.GENERALS, layers.blurStrength, 0, 1, controller);
			setCheckBoxItem(basePane, "特殊色をぼかす", ParameterType.BLURSPCOLOR, EditMode.GENERALS, false, controller);

			setTextPropertyItem(basePane, "インデックス名", ParameterType.INDEXNAME, EditMode.INDEXES, null, controller);
			setColorPickerItem(basePane, "インデックスの色1", ParameterType.INDEXCOLOR1, EditMode.INDEXES, 0, controller);
			setSliderPropertyItem(basePane, "色1の相対位置", ParameterType.INDEXPOS1, EditMode.INDEXES, 0.25, 0, 1, controller);
			setCheckBoxItem(basePane, "色2を使用する", ParameterType.USINGCOLOR2, EditMode.INDEXES, false, controller);
			setColorPickerItem(basePane, "インデックスの色2", ParameterType.INDEXCOLOR2, EditMode.INDEXES, 0, controller);
			setSliderPropertyItem(basePane, "色2の相対位置", ParameterType.INDEXPOS2, EditMode.INDEXES, 0, 0, 1, controller);
			setCheckBoxItem(basePane, "色3を使用する", ParameterType.USINGCOLOR3, EditMode.INDEXES, false, controller);
			setColorPickerItem(basePane, "インデックスの色3", ParameterType.INDEXCOLOR3, EditMode.INDEXES, 0, controller);
			setSliderPropertyItem(basePane, "色3の相対位置", ParameterType.INDEXPOS3, EditMode.INDEXES, 1, 0, 1, controller);
			setCheckBoxItem(basePane, "このインデックスをぼかす", ParameterType.BLURTHISINDEX, EditMode.INDEXES, false, controller);
			setAddTextureButtonItem(basePane, "テクスチャを追加する", EditMode.INDEXES, controller);
			setSliderPropertyItem(basePane, "テクスチャの適用度", ParameterType.TEXTURERATIO, EditMode.INDEXES, 0, 0, 1, controller);
			setSliderPropertyItem(basePane, "テクスチャの回転", ParameterType.TEXTUREROTATE, EditMode.INDEXES, 0, -180, 180, controller);
			setSliderPropertyItem(basePane, "元画像X方向シフト", ParameterType.TEXTUREOFFSETX, EditMode.INDEXES, 0, 0, 1, controller);
			setSliderPropertyItem(basePane, "元画像Y方向シフト", ParameterType.TEXTUREOFFSETY, EditMode.INDEXES, 0, 0, 1, controller);
			setSliderPropertyItem(basePane, "横の縮小率", ParameterType.TEXTUREZOOMX, EditMode.INDEXES, 0, -0.5, 1.5, controller);
			setSliderPropertyItem(basePane, "縦の縮小率", ParameterType.TEXTUREZOOMY, EditMode.INDEXES, 0, -0.5, 1.5, controller);
			setSliderPropertyItem(basePane, "横のシャーリング", ParameterType.TEXTURESHEARX, EditMode.INDEXES, 0, -0.5, 0.5, controller);
			setSliderPropertyItem(basePane, "縦のシャーリング", ParameterType.TEXTURESHEARY, EditMode.INDEXES, 0, -0.5, 0.5, controller);
		});

		this.setContent(basePane);
		this.setFitToWidth(true);

		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
	}

	private void setSliderPropertyItem(GridPane target, String name, ParameterType type,
						EditMode mode, double init, double min, double max, MainController controller) {
		Slider slider = new Slider(min, max, init);
		controller.EditModeProperty().addListener(evt -> {
			if(controller.getEditIndex() == -1) return;
			slider.valueProperty().set((double)controller.getProject().getValue(type, controller.getEditIndex()));
		});
		controller.EditIndexProperty().addListener(evt -> {
			if(controller.getEditIndex() == -1) return;
			slider.valueProperty().set((double)controller.getProject().getValue(type, controller.getEditIndex()));
		});
		slider.valueProperty().addListener((ov, oldval, newval) -> {
			controller.getProject().setValue(controller, (int) target.getWidth(), 30, type, controller.getEditIndex(), newval);
			controller.setPreviewImage(controller.getProject().getPreviewImage(controller.getPreviewMode()));
		});
		slider.maxWidthProperty().set(Double.MAX_VALUE);
		slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boolean doubleClicked = event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
				if(doubleClicked) {
					slider.valueProperty().set(init);
				}
			}

		});
		setPropertyItem(target, name, slider, mode, controller);
	}

	private void setTextPropertyItem(GridPane target, String name, ParameterType type,
			EditMode mode, String init, MainController controller) {
		TextField text = new TextField(init);
		controller.SelectedButtonProperty().addListener(evt -> {
			if(controller.getSelectedButton() != null) text.setText(controller.getSelectedButton().getText());
		});
		text.textProperty().addListener((ov, oldval, newval) -> {
			if(controller.getEditMode() != EditMode.INDEXES || controller.getEditIndex() == -1) return;
			controller.getProject().setValue(controller, type, controller.getEditIndex(), newval);
			controller.getSelectedButton().setText(controller.getProject().getDefinitionLayers().getIndex(controller.getEditIndex()).name);
		});
		text.maxWidthProperty().set(Double.MAX_VALUE);

		setPropertyItem(target, name, text, mode, controller);
	}

	private void setColorPickerItem(GridPane target, String name, ParameterType type,
			EditMode mode, int init, MainController controller) {
		ColorPicker picker = new ColorPicker(ColorUtility.toColor(init));
		controller.EditModeProperty().addListener(evt -> {
			if(controller.getEditIndex() == -1) return;
			picker.valueProperty().set(ColorUtility.toColor((int)controller.getProject().getValue(type, controller.getEditIndex())));
		});
		controller.EditIndexProperty().addListener(evt -> {
			if(controller.getEditIndex() == -1) return;
			picker.valueProperty().set(ColorUtility.toColor((int)controller.getProject().getValue(type, controller.getEditIndex())));
		});
		picker.valueProperty().addListener((ov, oldval, newval) -> {
			controller.getProject().setValue(controller, (int) target.getWidth(), 30, type, controller.getEditIndex(), ColorUtility.toArgb(newval));
			controller.setPreviewImage(controller.getProject().getPreviewImage(controller.getPreviewMode()));
		});
		picker.maxWidthProperty().set(Double.MAX_VALUE);
		setPropertyItem(target, name, picker, mode, controller);
	}

	private void setPropertyItem(GridPane target, String name, Control control, EditMode mode, MainController controller) {
		Label labIndex = new Label(name);
		control.prefWidthProperty().set(USE_COMPUTED_SIZE);

		RowConstraints rowObject = generateRowConstraints(controller, mode);

		target.getRowConstraints().add(rowObject);
		GridPane.setConstraints(labIndex, 0, target.getRowConstraints().size() - 1);
		GridPane.setConstraints(control, 1, target.getRowConstraints().size() - 1);
		target.getChildren().addAll(labIndex, control);

		labIndex.visibleProperty().bind(visibleBindings(controller, mode));
		control.visibleProperty().bind(visibleBindings(controller, mode));

		GridPane.setHgrow(control, Priority.ALWAYS);
	}

	private void setCheckBoxItem(GridPane target, String text, ParameterType type, EditMode mode, boolean init, MainController controller) {
		CheckBox checkItem = new CheckBox(text);

		controller.EditModeProperty().addListener(evt -> {
			if(controller.getEditIndex() == -1) return;
			checkItem.selectedProperty().set((int)controller.getProject().getValue(type, controller.getEditIndex()) == 1 ? true : false);
		});
		controller.EditIndexProperty().addListener(evt -> {
			if(controller.getEditIndex() == -1) return;
			checkItem.selectedProperty().set((int)controller.getProject().getValue(type, controller.getEditIndex()) == 1 ? true : false);
		});
		checkItem.selectedProperty().set(init);
		checkItem.selectedProperty().addListener((ov, oldval, newval) -> {
			controller.getProject().setValue(controller, (int) target.getWidth(), 30, type, controller.getEditIndex(), newval ? 1 : 0);
			controller.setPreviewImage(controller.getProject().getPreviewImage(controller.getPreviewMode()));
		});
		checkItem.maxWidthProperty().set(Double.MAX_VALUE);
		checkItem.prefWidthProperty().set(USE_COMPUTED_SIZE);

		RowConstraints rowObject = generateRowConstraints(controller, mode);

		target.getRowConstraints().add(rowObject);
		GridPane.setConstraints(checkItem, 0, target.getRowConstraints().size() - 1);
		GridPane.setColumnSpan(checkItem, 2);
		target.getChildren().add(checkItem);
		checkItem.visibleProperty().bind(visibleBindings(controller, mode));
		GridPane.setHgrow(checkItem, Priority.ALWAYS);
	}

	private void setAddTextureButtonItem(GridPane target, String text, EditMode mode, MainController controller) {
		//キャンバス行の定義
		ImageView imgView = new ImageView();
		imgView.prefHeight(30);
		imgView.imageProperty().bind(controller.TextureImageProperty());

		//ボタン行の定義
		Button button = new Button(text);
		button.setOnAction(evt -> {
			controller.getProject().setTextureImage(controller, (int) target.getWidth(), 30, controller.getEditIndex(), controller.getTextureSource());
			controller.setPreviewImage(controller.getProject().getPreviewImage(controller.getPreviewMode()));
		});
		button.maxWidthProperty().set(Double.MAX_VALUE);
		button.prefWidthProperty().set(USE_COMPUTED_SIZE);

		//ボタン行の追加
		RowConstraints rowObject = generateRowConstraints(controller, mode);

		target.getRowConstraints().add(rowObject);
		GridPane.setConstraints(button, 0, target.getRowConstraints().size() - 1);
		GridPane.setColumnSpan(button, 2);
		target.getChildren().add(button);
		button.visibleProperty().bind(visibleBindings(controller, mode));
		GridPane.setHgrow(button, Priority.ALWAYS);

		//キャンバス行の追加
		rowObject = generateRowConstraints(controller,  mode);

		target.getRowConstraints().add(rowObject);
		GridPane.setConstraints(imgView, 0, target.getRowConstraints().size() - 1);
		GridPane.setColumnSpan(imgView, 2);
		target.getChildren().add(imgView);
		imgView.visibleProperty().bind(visibleBindings(controller, mode));
		GridPane.setHgrow(imgView, Priority.ALWAYS);
	}

	private RowConstraints generateRowConstraints(MainController controller,  EditMode mode) {
		RowConstraints rowObject = new RowConstraints(30);
		controller.EditModeProperty().addListener(evt -> {
			if(controller.getEditMode() == mode && controller.getEditIndex() != -1) {
				rowObject.setPrefHeight(30);
			}
			else {
				rowObject.setPrefHeight(0);
			}
		});
		controller.EditIndexProperty().addListener(evt -> {
			if(controller.getEditMode() == mode && controller.getEditIndex() != -1) {
				rowObject.setPrefHeight(30);
			}
			else {
				rowObject.setPrefHeight(0);
			}
		});

		return rowObject;
	}

	private BooleanBinding visibleBindings(MainController controller, EditMode mode) {
		return Bindings.and(Bindings.not(Bindings.equal(-1, controller.EditIndexProperty())), Bindings.equal(controller.EditModeProperty(), mode));
	}
}
