package scene.search;

import domain.Bill;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import scene.detail.DetailController;
import scene.main.MainController;
import util.Util;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchBillController implements Initializable {
	private DetailController detailController = MainController.getDetailController();

	@FXML
	private TextField searchTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			String keyword = searchTextField.getText();
			if (keyword.trim().isEmpty())
				return;

			List<Bill> searchedBills = Util.getBillDAO().getSearchedBills(keyword);
			if (searchedBills.isEmpty())
				detailController.getMessageLabel().setText("未找到与“" + keyword + "”相关的账单记录！");
			else
				detailController.getMessageLabel().setText("");
			detailController.refreshView(searchedBills);

			detailController.getSearchKeywordLabel().setText("关键词：" + keyword);
			detailController.getFilterConditionLabel().setText("");
		});
	}
}
