package domain;

import java.util.Date;

public class Bill {
	private int id;
	private Date date;
	private String category;
	private double amount;
	private String remark;

	public static final String[] INCOME_CATEGORIES = {
			"工资入账", "投资理财", "经营所得", "其他",
	}; //收入类别
	public static final String[] EXPENDITURE_CATEGORIES = {
			"餐饮美食", "生活日用", "文体教育", "休闲娱乐", "交通出行", "业务缴费", "服饰美容", "其他",
	}; //支出类别

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
