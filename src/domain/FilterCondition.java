package domain;

import scene.main.Main;
import util.Util;

import java.util.Date;

public class FilterCondition {
	private String property;
	private String category;
	private Date startDate;
	private Date endDate;
	private double minAmount;
	private double maxAmount;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	@Override
	public String toString() {
		return "性质： " + property +
		       "\n类别： " + category +
		       "\n金额： ¥" + Util.getDecimalFormat().format(minAmount) + " ~ " + Util.getDecimalFormat().format(maxAmount) +
		       "\n日期： " + Util.getDateFormat().format(startDate) + " ~ " + Util.getDateFormat().format(endDate);
	}
}
