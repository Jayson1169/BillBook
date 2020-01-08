package scene.add;

import domain.Bill;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import scene.detail.DetailController;
import scene.main.Main;
import scene.main.MainController;
import util.Util;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static util.Util.isAmountLegal;

public class AddBillController implements Initializable {
	private ToggleGroup propertyGroup = new ToggleGroup();
	private DetailController detailController = MainController.getDetailController();

	@FXML
	private HBox propertyHBox;
	@FXML
	private ComboBox<String> categoryBox;
	@FXML
	private TextField amountField;
	@FXML
	private Label noticeLabel;
	@FXML
	private TextArea remarkArea;
	@FXML
	private DatePicker datePicker;

	@FXML
	public void addAction() {
        noticeLabel.setText("");

		String amountString = amountField.getText();
        if(isAmountLegal(amountString)) {
            Bill bill = new Bill();
            try {
                bill.setDate(Util.getDateFormat().parse(datePicker.getValue().toString()));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

            double amount = Double.parseDouble(amountString);
            RadioButton button = (RadioButton) propertyGroup.getSelectedToggle();
            if (button.getText().equals("支出"))
                amount = -amount;

            bill.setAmount(amount);
            bill.setCategory(categoryBox.getValue());
            bill.setRemark(remarkArea.getText());
            Util.getBillDAO().insertBill(bill);

            amountField.clear();
            remarkArea.clear();
            detailController.resetAction();

	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setHeaderText(null);
	        alert.setTitle("提示");
	        alert.setContentText("账单添加成功！");
	        alert.showAndWait();
        }
        else
            noticeLabel.setText("金额输入有误，请重新输入");
	}

	@FXML
	private void cancelAction(ActionEvent event) {
		((Button)event.getSource()).getScene().getWindow().hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<Node> radioButtons = propertyHBox.getChildren();
		for (Node node : radioButtons) {
			RadioButton radioButton = (RadioButton) node;
			radioButton.setToggleGroup(propertyGroup);
		}

		propertyGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			RadioButton button = (RadioButton) newValue;
			if (button.getText().equals("收入"))
				categoryBox.getItems().setAll(Bill.INCOME_CATEGORIES);
			else
				categoryBox.getItems().setAll(Bill.EXPENDITURE_CATEGORIES);
			categoryBox.getSelectionModel().selectLast();
		});
		propertyGroup.selectToggle(propertyGroup.getToggles().get(1));

		datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			LocalDate localDate = LocalDate.now();
			if (datePicker.getValue().isAfter(localDate))
				datePicker.valueProperty().setValue(localDate);
		});
		datePicker.valueProperty().setValue(LocalDate.now());
	}
}
