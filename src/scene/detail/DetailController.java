package scene.detail;

import domain.Bill;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import scene.main.Main;
import util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DetailController implements Initializable {
	@FXML
	private ListView<HBox> billsListView;

	@FXML
	private Label filterConditionLabel;

	@FXML
	private Label searchKeywordLabel;

	@FXML
	private Label messageLabel;

	@FXML
	private void searchAction() {
		try {
			resetAction();
			Alert alert = new Alert(Alert.AlertType.NONE);
			alert.setHeaderText(null);
			alert.setTitle("搜索……");

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/search/searchBill.fxml"));
			alert.getDialogPane().setContent(loader.load());

			ButtonType okButton = new ButtonType("确定");
			ButtonType cancelButton = new ButtonType("取消");
			alert.getButtonTypes().setAll(okButton, cancelButton);
			alert.initOwner(Main.getStage());
			if (alert.showAndWait().get() == cancelButton)
				resetAction();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void filterAction() {
		try {
			resetAction();
			Alert alert = new Alert(Alert.AlertType.NONE);
			alert.setHeaderText(null);
			alert.setTitle("筛选……");

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/filter/filterBill.fxml"));
			alert.getDialogPane().setContent(loader.load());

			ButtonType okButton = new ButtonType("确定");
			ButtonType cancelButton = new ButtonType("取消");
			alert.getButtonTypes().setAll(okButton, cancelButton);
			alert.initOwner(Main.getStage());
			if (alert.showAndWait().get() == cancelButton)
				resetAction();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void resetAction() {
		filterConditionLabel.setText("");
		searchKeywordLabel.setText("");
		messageLabel.setText("");
		refreshView(Util.getBillDAO().getAllBills());
	}

	private ObservableList<HBox> billPaneList = FXCollections.observableArrayList();
	private ArrayList<BillPaneController> controllers = new ArrayList();
	private List<Bill> bills = Util.getBillDAO().getAllBills();

	public void refreshView(List<Bill> bills) {
		billPaneList.clear();
		for (Bill bill : bills) {

			HBox billPane = null;
			BillPaneController controller = null;
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/detail/billPane.fxml"));
				billPane = loader.load();
				controller = loader.getController();
				controller.setDate(bill.getDate());
				controller.setAmount(bill.getAmount());
				controller.setCategory(bill.getCategory());
				controller.setRemark(bill.getRemark());

				controller.setBillPaneList(billPaneList);
				controller.setControllers(controllers);
				controller.setBill(bill);
				controllers.add(controller);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			billPaneList.add(billPane);
			controller.setIndex(billPaneList.indexOf(billPane));
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		billsListView.setItems(billPaneList);
		refreshView(bills);
	}

	public Label getFilterConditionLabel() {
		return filterConditionLabel;
	}

	public Label getSearchKeywordLabel() {
		return searchKeywordLabel;
	}

	public Label getMessageLabel() {
		return messageLabel;
	}
}