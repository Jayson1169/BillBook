package scene.filter;

import domain.Bill;
import domain.FilterCondition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import scene.detail.DetailController;
import scene.main.MainController;
import util.Util;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FilterBillController implements Initializable {
	private ToggleGroup propertyFilterGroup = new ToggleGroup();
	private boolean canEnter = true; //用于防止二次监听
	private DetailController detailController = MainController.getDetailController();

	@FXML
	private HBox propertyFilterHBox;
	@FXML
	private ComboBox<String> categoryFilterBox;
	@FXML
	private Spinner<Double> minAmountFilterSpinner;
	@FXML
	private Spinner<Double> maxAmountFilterSpinner;
	@FXML
	private DatePicker startDateFilterPicker;
	@FXML
	private DatePicker endDateFilterPicker;

	@FXML
	void filterAction() {
		FilterCondition filterCondition = new FilterCondition();
		RadioButton button = (RadioButton) propertyFilterGroup.getSelectedToggle();
		filterCondition.setProperty(button.getText());
		filterCondition.setCategory(categoryFilterBox.getValue());
		filterCondition.setMinAmount(minAmountFilterSpinner.getValue());
		filterCondition.setMaxAmount(maxAmountFilterSpinner.getValue());
		try {
			filterCondition.setStartDate(Util.getDateFormat().parse(startDateFilterPicker.getValue().toString()));
			filterCondition.setEndDate(Util.getDateFormat().parse(endDateFilterPicker.getValue().toString()));
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		List<Bill> filteredBills = Util.getBillDAO().getFilteredBills(filterCondition);
		if (filteredBills.isEmpty())
			detailController.getMessageLabel().setText("未找到符合条件的账单记录！");
		else
			detailController.getMessageLabel().setText("");
		detailController.refreshView(filteredBills);
		detailController.getFilterConditionLabel().setText(filterCondition.toString());
		detailController.getSearchKeywordLabel().setText("");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() ->{
			List<Node> radioButtons = propertyFilterHBox.getChildren();
			for (Node node : radioButtons) {
				RadioButton radioButton = (RadioButton)node;
				radioButton.setToggleGroup(propertyFilterGroup);
			}

			propertyFilterGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
				RadioButton button = (RadioButton) newValue;
				categoryFilterBox.getItems().setAll("不限");

				if (button.getText().equals("收入"))
					categoryFilterBox.getItems().addAll(Bill.INCOME_CATEGORIES);
				else if (button.getText().equals("支出"))
					categoryFilterBox.getItems().addAll(Bill.EXPENDITURE_CATEGORIES);
				categoryFilterBox.setValue("不限");

				filterAction();
			});

			minAmountFilterSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 10));
			maxAmountFilterSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 100, 10));
			minAmountFilterSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (canEnter) {
					double minAmount = minAmountFilterSpinner.getValue();
					double maxAmount = maxAmountFilterSpinner.getValue();
					if (minAmount > maxAmount) {
						canEnter = false;
						minAmountFilterSpinner.getValueFactory().setValue(maxAmount);
						canEnter = true;
					}
					filterAction();
				}
			});
			maxAmountFilterSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (canEnter) {
					double minAmount = minAmountFilterSpinner.getValue();
					double maxAmount = maxAmountFilterSpinner.getValue();
					if (maxAmount < minAmount) {
						canEnter = false;
						maxAmountFilterSpinner.getValueFactory().setValue(minAmount);
						canEnter = true;
					}
					filterAction();
				}
			});

			startDateFilterPicker.valueProperty().setValue(LocalDate.now().minusWeeks(1));
			endDateFilterPicker.valueProperty().setValue(LocalDate.now());
			startDateFilterPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (canEnter) {
					LocalDate startDate = startDateFilterPicker.getValue();
					LocalDate endDate = endDateFilterPicker.getValue();
					if (startDate.isAfter(endDate)) {
						canEnter = false;
						startDateFilterPicker.valueProperty().setValue(endDate);
						canEnter = true;
					}
					filterAction();
				}
			});
			endDateFilterPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (canEnter) {
					LocalDate startDate = startDateFilterPicker.getValue();
					LocalDate endDate = endDateFilterPicker.getValue();
					if (endDate.isBefore(startDate)) {
						canEnter = false;
						endDateFilterPicker.valueProperty().setValue(startDate);
						canEnter = true;
					}
					filterAction();
				}
			});

			//最后设置初始值
			propertyFilterGroup.selectToggle(propertyFilterGroup.getToggles().get(2));
		});
	}
}