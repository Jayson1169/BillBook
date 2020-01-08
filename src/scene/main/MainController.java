package scene.main;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import scene.date.DateController;
import scene.detail.DetailController;
import scene.statistics.StatisticsController;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	private static final String HELP_URL = "http://106.54.209.139/BillBook/help/help.html";
	private static DateController dateController;
	private static DetailController detailController;
	private static StatisticsController statisticsController;

	public static DateController getDateController() {
		return dateController;
	}

	public static DetailController getDetailController() {
		return detailController;
	}

	public static StatisticsController getStatisticsController() {
		return statisticsController;
	}

	@FXML
	private TabPane tabPane;

	@FXML
	private void addBillAction() {
		try {
			Alert alert = new Alert(Alert.AlertType.NONE);
			alert.setHeaderText(null);
			alert.setTitle("添加账单");

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/add/addBill.fxml"));
			alert.getDialogPane().setContent(loader.load());
			alert.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {});
			alert.initOwner(Main.getStage());
			alert.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void aboutAction() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setTitle("关于……");
		alert.setContentText("\t\t\t\t关于“懒人记账”\n\n" +
				"\t为您提供随时随地记账服务，通过账单搜索助您明确开支去向，动态图文报表帮您了解生活中开支服务！\n\n");
		alert.showAndWait();
	}

	@FXML
	private void helpAction() {
		try {
			Desktop.getDesktop().browse(new URI(HELP_URL));
		}
		catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			try {
				ObservableList<Tab> tabs = tabPane.getTabs();

				FXMLLoader dateFXMLLoader = new FXMLLoader(getClass().getResource("/scene/date/date.fxml"));
				FXMLLoader detailFXMLLoader = new FXMLLoader(getClass().getResource("/scene/detail/detail.fxml"));
				FXMLLoader staticsFXMLLoader = new FXMLLoader(getClass().getResource("/scene/statistics/statistics.fxml"));
				tabs.get(0).setContent(dateFXMLLoader.load());
				dateController = dateFXMLLoader.getController();
				dateController.setTabs(tabPane);
				dateController.setScene(Main.getScene());

				tabs.get(1).setContent(detailFXMLLoader.load());
				detailController = detailFXMLLoader.getController();
				dateController.setDetailController(detailController);

				tabs.get(2).setContent(staticsFXMLLoader.load());
				statisticsController = staticsFXMLLoader.getController();

				Main.getLoading().cancelProgressBar();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}