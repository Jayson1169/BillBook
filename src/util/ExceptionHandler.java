package util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class ExceptionHandler {
	public static void handleException(String exceptionMessage) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText(null);
		ButtonType buttonTypeOne = new ButtonType("查看更多解决方案");
		ButtonType buttonTypeTwo = new ButtonType("确定");
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

		alert.setTitle("意想不到的事情发生了……");
		alert.setContentText(exceptionMessage);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne){
			Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
			alert2.setTitle("数据库连接失败解决方案");
			alert2.setHeaderText(null);
			alert2.setContentText("当前网络连接断开或不稳定\n" +
			                      "可尝试：断开网络，重新连接后再尝试启动。");
			alert2.showAndWait();
			handleException(exceptionMessage);
		}else{
				Runtime re = Runtime.getRuntime();
				BufferedReader output;
				try {
					Process cmd = re.exec("java -jar F://BillBook.jar");
					output = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				Platform.exit();
		}
		Platform.exit();
	}
}
