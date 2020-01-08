package scene.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.Loading;

public class Main extends Application {
	private static Scene scene;
	private static Loading loading;
	private static Stage stage;

	static {
		loading = new Loading();
		loading.activateProgressBar();
	}

	public static Scene getScene() {
		return scene;
	}

	public static Loading getLoading() {
		return loading;
	}

	public static Stage getStage() {
		return stage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/main/main.fxml"));
		Parent root = loader.load();

		scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("懒人记账");
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resource/img/icon.jpg")));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
