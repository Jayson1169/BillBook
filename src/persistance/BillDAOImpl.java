package persistance;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import domain.Bill;
import domain.FilterCondition;
import util.ExceptionHandler;
import util.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillDAOImpl implements BillDAO {
	private Connection connection;

	private static final String INSERT_BILL_SQL =
			"INSERT INTO bill (`date`, category, amount, remark) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_BILL_SQL =
			"UPDATE bill SET `date` = ?, category = ?, amount = ?, remark = ? WHERE id = ?";
	private static final String DELETE_BILL_SQL =
			"DELETE FROM bill WHERE id = ?";
	private static final String GET_FILTERED_BILLS_SQL =
			"SELECT * FROM bill WHERE (amount BETWEEN ? AND ? OR amount BETWEEN ? AND ?) " +
					"AND category LIKE ? AND `date` BETWEEN ? AND ? ORDER BY `date` DESC ";
	private static final String GET_SEARCHED_BILLS_SQL =
			"SELECT * FROM bill WHERE category LIKE ? OR remark LIKE ? ORDER BY `date` DESC ";
	private static final String GET_MONTHLY_BILLS_DESC_SQL =
			"SELECT * FROM bill WHERE `date` BETWEEN ? AND ? ORDER BY `date` DESC ";
	private static final String GET_MONTHLY_BILLS_ASC_SQL =
			"SELECT * FROM bill WHERE `date` BETWEEN ? AND ? ORDER BY `date` ASC ";
	private static final String GET_ALL_BILLS_SQL =
			"SELECT * FROM bill ORDER BY `date` DESC";

	public BillDAOImpl() {
		try {
			connection = DBUtil.getConnection();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接失败"+"\n" + "当前网络不可用，请检查您的网络设置。");
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insertBill(Bill bill) {
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(INSERT_BILL_SQL);
			preparedStatement.setString(1, Util.getDateFormat().format(bill.getDate()));
			preparedStatement.setString(2, bill.getCategory());
			preparedStatement.setDouble(3, bill.getAmount());
			preparedStatement.setString(4, bill.getRemark());
			preparedStatement.executeUpdate();

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM bill");
			resultSet.last();
			bill.setId(resultSet.getInt(1));

			resultSet.close();
			statement.close();
			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateBill(Bill bill) {
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(UPDATE_BILL_SQL);
			preparedStatement.setString(1, Util.getDateFormat().format(bill.getDate()));
			preparedStatement.setString(2, bill.getCategory());
			preparedStatement.setDouble(3, bill.getAmount());
			preparedStatement.setString(4, bill.getRemark());
			preparedStatement.setInt(5, bill.getId());
			preparedStatement.executeUpdate();

			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteBill(Bill bill) {
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(DELETE_BILL_SQL);
			preparedStatement.setInt(1, bill.getId());
			preparedStatement.executeUpdate();

			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Bill> getFilteredBills(FilterCondition condition) {
		List<Bill> bills = new ArrayList<>();
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(GET_FILTERED_BILLS_SQL);
			if (condition.getProperty().equals("收入")) {
				preparedStatement.setDouble(1, condition.getMinAmount());
				preparedStatement.setDouble(2, condition.getMaxAmount());
				preparedStatement.setDouble(3, condition.getMinAmount());
				preparedStatement.setDouble(4, condition.getMaxAmount());
			}
			else if (condition.getProperty().equals("支出")) {
				preparedStatement.setDouble(1, -condition.getMaxAmount());
				preparedStatement.setDouble(2, -condition.getMinAmount());
				preparedStatement.setDouble(3, -condition.getMaxAmount());
				preparedStatement.setDouble(4, -condition.getMinAmount());
			}
			else {
				preparedStatement.setDouble(1, condition.getMinAmount());
				preparedStatement.setDouble(2, condition.getMaxAmount());
				preparedStatement.setDouble(3, -condition.getMaxAmount());
				preparedStatement.setDouble(4, -condition.getMinAmount());
			}

			if (condition.getCategory().equals("不限"))
				preparedStatement.setString(5, "%");
			else
				preparedStatement.setString(5, "%" + condition.getCategory() + "%");

			preparedStatement.setString(6, Util.getDateFormat().format(condition.getStartDate()));
			preparedStatement.setString(7, Util.getDateFormat().format(condition.getEndDate()));

			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Bill bill = new Bill();
				bill.setId(resultSet.getInt(1));
				bill.setDate(resultSet.getDate(2));
				bill.setCategory(resultSet.getString(3));
				bill.setAmount(resultSet.getDouble(4));
				bill.setRemark(resultSet.getString(5));
				bills.add(bill);
			}

			resultSet.close();
			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return bills;
	}

	@Override
	public List<Bill> getSearchedBills(String keyword) {
		List<Bill> bills = new ArrayList<>();
		String temp = "%" + keyword + "%";
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(GET_SEARCHED_BILLS_SQL);
			preparedStatement.setString(1, temp);
			preparedStatement.setString(2, temp);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Bill bill = new Bill();
				bill.setId(resultSet.getInt(1));
				bill.setDate(resultSet.getDate(2));
				bill.setCategory(resultSet.getString(3));
				bill.setAmount(resultSet.getDouble(4));
				bill.setRemark(resultSet.getString(5));
				bills.add(bill);
			}

			resultSet.close();
			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return bills;
	}

	@Override
	public List<Bill> getMonthlyBills(Date date) {
		List<Bill> bills = new ArrayList<>();
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(GET_MONTHLY_BILLS_DESC_SQL);
			LocalDate startDate = LocalDate.parse(Util.getDateFormat().format(date)).withDayOfMonth(1);
			LocalDate endDate = LocalDate.parse(Util.getDateFormat().format(date)).withDayOfMonth(Util.getDaysOfMonth(date));
			preparedStatement.setString(1, startDate.toString());
			preparedStatement.setString(2, endDate.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Bill bill = new Bill();
				bill.setId(resultSet.getInt(1));
				bill.setDate(resultSet.getDate(2));
				bill.setCategory(resultSet.getString(3));
				bill.setAmount(resultSet.getDouble(4));
				bill.setRemark(resultSet.getString(5));
				bills.add(bill);
			}

			resultSet.close();
			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return bills;
	}

	@Override
	public List<Bill> getAllBills() {
		List<Bill> bills = new ArrayList<>();
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(GET_ALL_BILLS_SQL);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Bill bill = new Bill();
				bill.setId(resultSet.getInt(1));
				bill.setDate(resultSet.getDate(2));
				bill.setCategory(resultSet.getString(3));
				bill.setAmount(resultSet.getDouble(4));
				bill.setRemark(resultSet.getString(5));
				bills.add(bill);
			}

			resultSet.close();
			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return bills;
	}

	@Override
	public List<Bill> getMonthlyExpenditureBills(Date date) {
		List<Bill> bills = null;
		Date date1 = new Date();
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(GET_MONTHLY_BILLS_ASC_SQL);
			LocalDate startDate = LocalDate.parse(Util.getDateFormat().format(date)).withDayOfMonth(1);
			LocalDate endDate = LocalDate.parse(Util.getDateFormat().format(date)).withDayOfMonth(Util.getDaysOfMonth(date));
			preparedStatement.setString(1, startDate.toString());
			preparedStatement.setString(2, endDate.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			if(resultSet.next()){
				bills = new ArrayList<>();
				if(resultSet.getDouble(4) < 0){
					Bill bill = new Bill();
					bill.setId(resultSet.getInt(1));
					bill.setDate(resultSet.getDate(2));
					bill.setCategory(resultSet.getString(3));
					bill.setAmount(resultSet.getDouble(4));
					bill.setRemark(resultSet.getString(5));
					bills.add(bill);
				}
			}

			while (resultSet.next()) {
				if(resultSet.getDouble(4) < 0) {
					if (resultSet.getDate(2).equals(date1)) {
						bills.get(bills.size()-1).setAmount(bills.get(bills.size()-1).getAmount() + resultSet.getDouble(4));
					}else {
						Bill bill = new Bill();
						bill.setId(resultSet.getInt(1));
						bill.setDate(resultSet.getDate(2));
						bill.setCategory(resultSet.getString(3));
						bill.setAmount(resultSet.getDouble(4));
						bill.setRemark(resultSet.getString(5));
						bills.add(bill);
					}
					date1 = resultSet.getDate(2);
				}

			}
			resultSet.close();
			preparedStatement.close();
		}
		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return bills;
	}

	@Override
	public List<Bill> getMonthlyIncomeBills(Date date) {
		List<Bill> bills = null;
		Date date1 = new Date();
		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(GET_MONTHLY_BILLS_ASC_SQL);
			LocalDate startDate = LocalDate.parse(Util.getDateFormat().format(date)).withDayOfMonth(1);
			LocalDate endDate = LocalDate.parse(Util.getDateFormat().format(date)).withDayOfMonth(Util.getDaysOfMonth(date));
			preparedStatement.setString(1, startDate.toString());
			preparedStatement.setString(2, endDate.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			if(resultSet.next()){
				bills = new ArrayList<>();
				if(resultSet.getDouble(4) > 0){
					Bill bill = new Bill();
					bill.setId(resultSet.getInt(1));
					bill.setDate(resultSet.getDate(2));
					bill.setCategory(resultSet.getString(3));
					bill.setAmount(resultSet.getDouble(4));
					bill.setRemark(resultSet.getString(5));
					bills.add(bill);
				}
			}

			while (resultSet.next()) {
				if (resultSet.getDouble(4) > 0) {
					if (resultSet.getDate(2).equals(date1)) {
						bills.get(bills.size() - 1).setAmount(bills.get(bills.size() - 1).getAmount() + resultSet.getDouble(4));
					} else {
						Bill bill = new Bill();
						bill.setId(resultSet.getInt(1));
						bill.setDate(resultSet.getDate(2));
						bill.setCategory(resultSet.getString(3));
						bill.setAmount(resultSet.getDouble(4));
						bill.setRemark(resultSet.getString(5));
						bills.add(bill);
					}
					date1 = resultSet.getDate(2);
				}
			}
			resultSet.close();
			preparedStatement.close();
		}

		catch (CommunicationsException e) {
			ExceptionHandler.handleException("数据库连接中断，请检查网络连接！");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return bills;
	}
}
