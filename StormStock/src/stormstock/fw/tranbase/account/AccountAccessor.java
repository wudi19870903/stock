package stormstock.fw.tranbase.account;

import java.util.List;

import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;

/*
 * 用户账户的某日期时间的访问器
 * 可以访问账户当前信息
 */
public class AccountAccessor {
	public AccountAccessor(String date, String time, AccountControlIF cAccountControlIF)
	{
		m_date = date;
		m_time = time;
		m_accountControlIF = cAccountControlIF;
	}
	
	public float getTotalAssets()
	{
		return m_accountControlIF.getTotalAssets(m_date, m_time);
	}
	
	public float getAvailableMoney()
	{
		return m_accountControlIF.getAvailableMoney();
	}
	
	public List<CommissionOrder> getCommissionOrderList()
	{
		return m_accountControlIF.getCommissionOrderList();
	}
	
	public List<HoldStock> getStockHoldList()
	{
		return m_accountControlIF.getStockHoldList(m_date, m_time);
	}
	
	public HoldStock getStockHold(String stockID)
	{
		return m_accountControlIF.getStockHold(m_date, m_time, stockID);
	}
	
	public List<DeliveryOrder> getDeliveryOrderList()
	{
		return m_accountControlIF.getDeliveryOrderList();
	}

	private String m_date;
	private String m_time;
	private AccountControlIF m_accountControlIF;
}
