package persistance;

import domain.Bill;
import domain.FilterCondition;

import java.util.Date;
import java.util.List;

public interface BillDAO {
	void insertBill(Bill bill); //插入
	void updateBill(Bill bill); //更新
	void deleteBill(Bill bill); //删除
	List<Bill> getFilteredBills(FilterCondition condition); //返回筛选结果
	List<Bill> getSearchedBills(String keyword); //返回搜索结果
	List<Bill> getMonthlyBills(Date date); //返回当月账单
	List<Bill> getAllBills(); //返回所有账单
	List<Bill> getMonthlyIncomeBills(Date date); //返回当月收入账单
	List<Bill> getMonthlyExpenditureBills(Date date); //返回当月支出账单
}
