package application;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Model;
import model.SFXProject;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		MainController controller = new MainController(Model.getInstance());

		try {
			BorderPane root = new BorderPane();

			//***** ツールバー *****
			EditToolBar toolBar = new EditToolBar(controller);

			//***** キャンバス *****
			CanvasPane canvas = new CanvasPane(root, controller);

			//***** プロパティ *****
			LayerPane layers = new LayerPane(controller);
			PropertiesPane modifies = new PropertiesPane(controller);
			SplitPane property = new SplitPane(layers, modifies);
			property.setPrefWidth(250);
			property.setOrientation(Orientation.VERTICAL);
			StatusPane status = new StatusPane(controller);

			//***** オブジェクトをペインに設定する *****
			root.setTop(toolBar);
			root.setCenter(canvas);
			root.setRight(property);
			root.setBottom(status);

			//プロジェクトを初期化する
			controller.setProject(new SFXProject());

			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
