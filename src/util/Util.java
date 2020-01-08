package util;

import persistance.BillDAO;
import persistance.BillDAOImpl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
	private static BillDAO billDAO = new BillDAOImpl();

	private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static BillDAO getBillDAO() {
		return billDAO;
	}

	public static DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public static SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public static boolean isAmountLegal(String amountString) {
		if (amountString.trim().isEmpty())
			return false;
		int index = -1;
		for (int i = 0; i < amountString.length(); i++) {
			if (!Character.isDigit(amountString.charAt(i))) {
				if (amountString.charAt(i) != 46)
					return false;
				index = i;
			}
		}

		return ((amountString.length() - index) <= 3) || (index == -1);
	}

	public static int getDaysOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
