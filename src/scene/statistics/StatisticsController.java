package scene.statistics;

import domain.Bill;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.Util;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

public class StatisticsController implements Initializable {
	private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	private Map<String, Double> expenditureMap = new HashMap<>();
	private Map<String, Double> incomeMap = new HashMap<>();
	private double expenditureSum = 0;
	private double incomeSum = 0;
	private boolean canEnter = true; //用于防止二次监听

	private ToggleGroup propertyGroup = new ToggleGroup();

	@FXML
	private PieChart pieChart;
	@FXML
	private VBox amountSumVBox;
	@FXML
	private HBox propertyHBox;
	@FXML
	private Spinner<Integer> yearSpinner;
	@FXML
	private Spinner<Integer> monthSpinner;

	private void viewExpenditure() {
		amountSumVBox.getChildren().clear();

		if (expenditureSum > 0) {
			pieChart.setTitle("    支出\n¥" + Util.getDecimalFormat().format(expenditureSum));
			pieChartData.clear();
			for (String key : expenditureMap.keySet())
				pieChartData.add(new PieChart.Data(key, expenditureMap.get(key)));

			for (int i = 0; i < Bill.EXPENDITURE_CATEGORIES.length; i++)
				if (expenditureMap.containsKey(Bill.EXPENDITURE_CATEGORIES[i])) {
					Label label = new Label(Bill.EXPENDITURE_CATEGORIES[i] + "：  ¥" +
					                        Util.getDecimalFormat().format(expenditureMap.get(Bill.EXPENDITURE_CATEGORIES[i])));
					label.setStyle("-fx-background-color:POWDERBLUE;" +
					               "-fx-pref-height: 35;" +
					               "-fx-pref-width: 200;" +
					               "-fx-font-size: 14");
					amountSumVBox.getChildren().add(label);
				}
		}
		else
			pieChart.setTitle("当月无支出");
	}

	private void viewIncome() {
		amountSumVBox.getChildren().clear();

		if (incomeSum > 0) {
			pieChart.setTitle("    收入\n¥" +Util.getDecimalFormat().format(incomeSum));
			pieChartData.clear();
			for (String key : incomeMap.keySet())
				pieChartData.add(new PieChart.Data(key, incomeMap.get(key)));

			for (int i = 0; i < Bill.INCOME_CATEGORIES.length; i++)
				if (incomeMap.containsKey(Bill.INCOME_CATEGORIES[i])) {
					Label label = new Label(Bill.INCOME_CATEGORIES[i] + "：  ¥" +
					                        Util.getDecimalFormat().format(incomeMap.get(Bill.INCOME_CATEGORIES[i])));
					label.setStyle("-fx-background-color:POWDERBLUE;" +
							"-fx-pref-height: 35;" +
							"-fx-pref-width: 200;" +
							       "-fx-font-size: 14");
					amountSumVBox.getChildren().add(label);
				}
		}
		else
			pieChart.setTitle("当月无收入");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<Node> radioButtons = propertyHBox.getChildren();
		for (Node node : radioButtons) {
			RadioButton radioButton = (RadioButton)node;
			radioButton.setToggleGroup(propertyGroup);
		}

		propertyGroup.selectToggle(propertyGroup.getToggles().get(1));
		propertyGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<Toggle> toggles = propertyGroup.getToggles();
			for (Toggle toggle : toggles) {
				RadioButton button = (RadioButton)toggle;
				if (propertyGroup.getSelectedToggle() == toggle) {
					button.setTextFill(Color.BLACK);
					button.setFont(Font.font("System", FontWeight.BOLD, 14));
				}
				else {
					button.setTextFill(Color.GRAY);
					button.setFont(Font.font("System", FontWeight.NORMAL, 14));
				}
			}
			refreshView();
		});


		yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2099));
		monthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 13, 1));

		yearSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			yearSpinner.getEditor().setText(newValue + "年");
			loadPieChartData();
			refreshView();
		});
		monthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (canEnter) {
				if (newValue == 0) {
					canEnter = false;
					monthSpinner.getValueFactory().setValue(12);
					canEnter = true;
					yearSpinner.decrement();
				}
				else if (newValue == 13) {
					canEnter = false;
					monthSpinner.getValueFactory().setValue(1);
					canEnter = true;
					yearSpinner.increment();
				}
				else {
					loadPieChartData();
					refreshView();
				}
				monthSpinner.getEditor().setText(monthSpinner.getValue() + "月");
			}
		});

		LocalDate today = LocalDate.now();
		yearSpinner.getValueFactory().setValue(today.getYear());
		monthSpinner.getValueFactory().setValue(today.getMonthValue());
	}

	private void loadPieChartData() {
		pieChartData.clear();
		expenditureMap.clear();
		incomeMap.clear();
		amountSumVBox.getChildren().clear();
		expenditureSum = incomeSum = 0;

		LocalDate date = LocalDate.now();
		date = date.withYear(yearSpinner.getValue());
		date = date.withMonth(monthSpinner.getValue());

		List<Bill> monthBillList = new ArrayList<>();
		try {
			monthBillList = Util.getBillDAO().getMonthlyBills(Util.getDateFormat().parse(date.toString()));
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		for (Bill bill : monthBillList) {
			if (bill.getAmount() < 0) {
				if (expenditureMap.containsKey(bill.getCategory())) {
					double preValue = expenditureMap.get(bill.getCategory());
					expenditureMap.put(bill.getCategory(), preValue - bill.getAmount());
				}
				else
					expenditureMap.put(bill.getCategory(), -bill.getAmount());
			}
			else {
				if (incomeMap.containsKey(bill.getCategory())) {
					double preValue = incomeMap.get(bill.getCategory());
					incomeMap.put(bill.getCategory(), preValue + bill.getAmount());
				}
				else
					incomeMap.put(bill.getCategory(), bill.getAmount());
			}
		}

		for (String key : expenditureMap.keySet())
			expenditureSum += expenditureMap.get(key);
		for (String key : incomeMap.keySet())
			incomeSum += incomeMap.get(key);

		pieChart.setData(pieChartData);
	}

	private void refreshView() {
		RadioButton button = (RadioButton) propertyGroup.getSelectedToggle();
		if (button.getText().equals("收入"))
			viewIncome();
		else
			viewExpenditure();
	}
}
