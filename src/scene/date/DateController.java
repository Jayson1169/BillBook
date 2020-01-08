package scene.date;

import domain.Bill;
import domain.FilterCondition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import persistance.BillDAO;
import scene.detail.DetailController;
import scene.filter.FilterBillController;
import util.Util;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class DateController implements Initializable {

	private boolean canEnter = true; //用于防止二次监听
	private BillDAO billDAO = Util.getBillDAO();

	//日历
	@FXML
	public Spinner<Integer> yearSpinner;
	@FXML
	public Spinner<Integer> monthSpinner;

	//悬浮框
	@FXML
	public Label label1;

	//折线图组件
	@FXML
	public LineChart lineChart;
	@FXML
	public CategoryAxis date_Axis;
	@FXML
	public NumberAxis money_Axis;

	//支出组件
	@FXML
	public Label expenditure_1;
	@FXML
	public Label expenditure_2;
	@FXML
	public Label expenditure_3;
	@FXML
	public Separator expenditure_4;

	//收入组件
	@FXML
	public Label income_1;
	@FXML
	public Label income_2;
	@FXML
	public Label income_3;
	@FXML
	public Separator income_4;

	public Scene scene;
	public TabPane tabPane;
	private static DetailController detailController;
	public int clicka = 0;

	//绑定折线图数据链
	XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();

	//清空折线图
	public void clear() {
		series = new XYChart.Series<>();
		lineChart.getData().clear();
	}

	//更改账单
	public void clickDate() throws ParseException {
		if(clicka == 0) {
			setExpenditure(getDate());
			setIncome_3(getDate());
		}else{
			setIncome(getDate());
			setExpenditure_3(getDate());
		}
		setFilter();
	}

	//获取日期
	public String getDate() {
		String year = yearSpinner.getValue().toString();
		String month = monthSpinner.getValue().toString();
		return year + "-" + month + "-22";
	}

	//点击支出
	public void click_Expenditure() throws ParseException {

		clicka = 0;
		//设置收入组件样式
		income_1.setTextFill(Color.web("#8f8d8d"));
		income_2.setTextFill(Color.web("#8f8d8d"));
		income_3.setTextFill(Color.web("#8f8d8d"));
		income_4.setStyle(" -fx-background-color: #ffffff");

		//设置支出组件样式
		expenditure_1.setTextFill(Color.web("#535353"));
		expenditure_2.setTextFill(Color.web("#535353"));
		expenditure_3.setTextFill(Color.web("#000000"));
		expenditure_4.setStyle("-fx-background-color: #0262d0");

		//更改折线图
		clear();
		setExpenditure(getDate());
		setFilter();
	}

	//点击收入
	public void click_Income() throws ParseException {

		clicka = 1;
		//设置支出组件样式
		expenditure_1.setTextFill(Color.web("#8f8d8d"));
		expenditure_2.setTextFill(Color.web("#8f8d8d"));
		expenditure_3.setTextFill(Color.web("#8f8d8d"));
		expenditure_4.setStyle("-fx-background-color: #ffffff");

		//设置收入组件样式
		income_1.setTextFill(Color.web("#535353"));
		income_2.setTextFill(Color.web("#535353"));
		income_3.setTextFill(Color.web("#000000"));
		income_4.setStyle("-fx-background-color: #0262d0");

		//更改折线图
		clear();
		setIncome(getDate());
		setFilter();
	}

	//鼠标进入区域给予提示
	public void changeMouse() {
		scene.setCursor(Cursor.HAND);
	}

	//鼠标恢复初始化
	public void initMouse() {
		scene.setCursor(Cursor.DEFAULT);
	}

	//支出账单
	public void setExpenditure(String date) throws ParseException {

		double total_money = 0;

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = format1.parse(date);

		List<Bill> filteredBills = billDAO.getMonthlyExpenditureBills(today);

		//折线图数据类
		series.setName("支出");

		if (filteredBills != null) {
			//遍历当月账单
			for (Bill filteredBill : filteredBills) {

				String month = new SimpleDateFormat("yyyy-MM-dd").format(filteredBill.getDate()).substring(5);
				double money = -filteredBill.getAmount();

				//计算支出总和
				total_money = total_money + money;

				//向数据链添加数据
				series.getData().add(new XYChart.Data<>(month, money));

			}

			expenditure_3.setText("" + total_money);

			//将数据链添加到折线图中
			lineChart.getData().add(series);
		}
		else {
			expenditure_3.setText("" + 0.00);
			clear();
		}
	}

	//收入账单
	public void setIncome(String date) throws ParseException {
		double total_money = 0.00;

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = format1.parse(date);

		List<Bill> filteredBills = billDAO.getMonthlyIncomeBills(today);

		//折线图数据类
		series.setName("收入");

		if (filteredBills != null) {
			//遍历当月账单
			for (Bill filteredBill : filteredBills) {
				String month = new SimpleDateFormat("yyyy-MM-dd").format(filteredBill.getDate()).substring(5);
				double money = filteredBill.getAmount();

				//计算支出总和
				total_money = total_money + money;

				//向数据链添加数据
				series.getData().add(new XYChart.Data<>(month, money));
			}


			income_3.setText("" + total_money);

			//将数据链添加到折线图中
			lineChart.getData().add(series);
		}
		else {
			income_3.setText("" + 0.00);
			clear();
		}
	}

	//今日收入
	public void setExpenditure_3(String date) throws ParseException {

		double total_money = 0;

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = format1.parse(date);

		List<Bill> filteredBills = billDAO.getMonthlyExpenditureBills(today);


		if (filteredBills != null) {
			//遍历当月账单
			for (Bill filteredBill : filteredBills) {

				double money = filteredBill.getAmount();
				//计算支出总和
				total_money = total_money - money;
			}
		}
		expenditure_3.setText("" + total_money);
	}

	//今日收入
	public void setIncome_3(String date) throws ParseException {

		double total_money = 0;

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = format1.parse(date);

		List<Bill> filteredBills = billDAO.getMonthlyIncomeBills(today);


		if (filteredBills != null) {
			//遍历当月账单
			for (Bill filteredBill : filteredBills) {

				double money = filteredBill.getAmount();
				//计算支出总和
				total_money = total_money + money;
			}
		}
		income_3.setText("" + total_money);
	}

	//计算星期
	public String getWeek(int y, int m, int d) {
		String[] week = new String[]{
				"周日", "周一", "周二", "周三", "周四", "周五", "周六"
		};

		if (m < 3) {
			m += 12;
			--y;
		}
		int w = (d + 1 + 2 * m + 3 * (m + 1) / 5 + y + (y >> 2) - y / 100 + y / 400) % 7;
		return week[w];
	}


	public void filterAction(String detail, String date) {
		FilterCondition filterCondition = new FilterCondition();
		filterCondition.setProperty(detail);
		filterCondition.setCategory("不限");
		filterCondition.setMinAmount(0);
		filterCondition.setMaxAmount(999999);
		try {
			filterCondition.setStartDate(Util.getDateFormat().parse(date));
			filterCondition.setEndDate(Util.getDateFormat().parse(date));
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
		detailController.getSearchKeywordLabel().setText("");
	}

	//设置监听器
	public void setFilter() {
		//折线图数据点击监听--此处可跳转至其他页面
		for (int a = 0; a < series.getData().size(); a++) {
			final int b = a;
			series.getData().get(a).getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {

				String date = yearSpinner.getValue() + "-" + series.getData().get(b).getXValue();
				filterAction(series.getName(), date);

				tabPane.getSelectionModel().select(1);
			});

			series.getData().get(a).getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {

				int year = Integer.parseInt(yearSpinner.getValue().toString());
				String data = series.getData().get(b).getXValue();
				int month = Integer.parseInt(data.substring(0, data.indexOf("-")));
				int day = Integer.parseInt(data.substring(data.indexOf("-")));

				scene.setCursor(Cursor.HAND);
				label1.setOpacity(1);
				label1.setText("  " + series.getData().get(b).getXValue().replace("-", "月") + "日 " + getWeek(year, month, day) +
				               "\n" + "     ￥" + series.getData().get(b).getYValue() + " >");
				label1.setLayoutX(scene.getX() + e.getSceneX() + 15);
				label1.setLayoutY(scene.getY() + e.getSceneY() - 100);

			});

			series.getData().get(a).getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
				scene.setCursor(Cursor.MOVE);
				label1.setOpacity(0);
			});

		}
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2099));
		monthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 13, 1));

		yearSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			yearSpinner.getEditor().setText(" " + newValue + "年");

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
				monthSpinner.getEditor().setText("  " + monthSpinner.getValue() + "月");
			}
		});

		//默认日期
		LocalDate today = LocalDate.now();
		yearSpinner.getValueFactory().setValue(today.getYear());
		monthSpinner.getValueFactory().setValue(today.getMonthValue());

		try {
			setIncome_3(getDate());
			//默认显示今日支出
			setExpenditure(getDate());
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		setFilter();
	}

	public void setTabs(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	public void setDetailController(DetailController detailController) {
		DateController.detailController = detailController;
	}
}