package util;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Loading {
        private Stage dialogStage;
        private ProgressIndicator progressIndicator;
        public Scene scene;

        public Loading() {
            dialogStage = new Stage();
            dialogStage.setAlwaysOnTop(true);
            progressIndicator = new ProgressIndicator();
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initStyle(StageStyle.TRANSPARENT);

            Label label = new Label("数据加载中, 请稍后...");
            label.setTextFill(Color.BLUE);
            progressIndicator.setProgress(-1F);
            VBox vBox = new VBox();
            vBox.setSpacing(10);
            vBox.setBackground(Background.EMPTY);
            vBox.getChildren().addAll(progressIndicator,label);
            Scene scene = new Scene(vBox);

            scene.setFill(null);
            dialogStage.setScene(scene);
        }

        public void activateProgressBar() {
            dialogStage.show();
        }

        public void cancelProgressBar() {
            dialogStage.close();
        }


    }
