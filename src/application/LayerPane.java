package application;

import enums.EditMode;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import model.utility.ColorUtility;

public class LayerPane extends VBox {
	private ToggleGroup indexGroup = new ToggleGroup();

	public LayerPane(MainController controller) {

		//プロジェクトが生成されたときの処理
		controller.ProjectProperty().addListener(evt -> {
			this.getChildren().clear();
		});

		//モードが変更されたときの処理
		controller.EditModeProperty().addListener(evt -> {
			//ペインをクリアする
			this.getChildren().clear();

			//リストを作成する
			VBox baseList = new VBox();

			//ボタンを作成する
			if(controller.getEditMode() == EditMode.GENERALS) return;
			if(controller.getEditMode() != EditMode.INDEXES) {
				HBox buttonPane = new HBox();

				Button btnItemAdd = new Button("＋");
				Button btnMoveUp = new Button("▲");
				Button btnMoveDown = new Button("▼");
				Button btnItemRemove = new Button("×");

				buttonPane.getChildren().addAll(btnItemAdd, btnMoveUp, btnMoveDown, btnItemRemove);
				buttonPane.visibleProperty().bind(Bindings.not(Bindings.equal(controller.EditModeProperty(), EditMode.INDEXES)));

				this.getChildren().add(buttonPane);
			}

			//リストを作成する
			ScrollPane listPane = new ScrollPane();

			if(controller.getEditMode() == EditMode.INDEXES) {
				//インデックスのトグルボタンを生成する
				for(int i = 0; i < controller.getProject().getDefinitionLayers().getIndexSize(); i++) {
					ToggleButton tglIndex = createIndexToggleButton(controller, i);
					baseList.getChildren().add(tglIndex);
				}

				controller.EditIndexProperty().addListener(evt2 -> {
					if(controller.getEditIndex() != -1) {
						controller.setSelectedButton((ToggleButton) baseList.getChildren().get(controller.getEditIndex()));
					}
				});
			}

			listPane.setContent(baseList);
			listPane.setHbarPolicy(ScrollBarPolicy.NEVER);

			//このペインの設定
			this.visibleProperty().bind(Bindings.not(Bindings.equal(controller.EditModeProperty(), EditMode.GENERALS)));

			this.getChildren().add(listPane);
		});
	}

	private ToggleButton createIndexToggleButton(MainController controller, int index) {
		ToggleButton tglIndex = new ToggleButton(controller.getProject().getDefinitionLayers().getIndex(index).name);
		tglIndex.setToggleGroup(indexGroup);
		tglIndex.setBackground(new Background(createIndexColorBG((int)controller.getProject().getDefinitionLayers().getIndex(index).getIndexColor()), createUnpushedEffectBG()));
		tglIndex.selectedProperty().addListener((evt) -> {
			if(tglIndex.isSelected()) {
				tglIndex.setBackground(new Background(createIndexColorBG((int)controller.getProject().getDefinitionLayers().getIndex(index).getIndexColor()), createPushedEffectBG()));
				controller.setEditMode(controller.getEditMode(), index);
			}
			else {
				tglIndex.setBackground(new Background(createIndexColorBG((int)controller.getProject().getDefinitionLayers().getIndex(index).getIndexColor()), createUnpushedEffectBG()));
			}
		});
		tglIndex.visibleProperty().bind(Bindings.equal(controller.EditModeProperty(), EditMode.INDEXES));

		return tglIndex;
	}

	private BackgroundFill createUnpushedEffectBG() {
		return new BackgroundFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.4, Color.TRANSPARENT), new Stop(1, Color.grayRgb(0, 0.2))), null, null);
	}

	private BackgroundFill createPushedEffectBG() {
		return new BackgroundFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.6, Color.TRANSPARENT), new Stop(0, Color.grayRgb(0, 0.2))), null, null);
	}

	private BackgroundFill createIndexColorBG(int colorArgb) {
		return new BackgroundFill(new LinearGradient(0.5, 0, 0.8, 0.9, true, CycleMethod.NO_CYCLE, new Stop(0, Color.TRANSPARENT), new Stop(1, ColorUtility.toColor(colorArgb))), null, null);
	}
}
