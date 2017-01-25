package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;

/*
 * 账户控制器
 * 可以获取账户信息
 * 可以操作账户动作
 */
public class AccountControlIF {
	
	public AccountControlIF()
	{
		m_account = null;
	}
	
	/*
	 * 获得某日期时间的账户访问器
	 * 可以获取账户信息
	 */
	public AccountAccessor getAccountAccessor(String date, String time)
	{
		return new AccountAccessor(date, time, this);
	}
	
	public void setAccountType(ACCOUNTTYPE eAccIFType)
	{
		if(ACCOUNTTYPE.MOCK == eAccIFType)
		{
			m_account = new AccountEntity(eAccIFType, "mock001", "mock001_password");
		}
		if(ACCOUNTTYPE.REAL == eAccIFType)
		{
			m_account = new AccountEntity(eAccIFType, "xxx", "xxx");
		}
	}
	
	public void printAccount(String date, String time)
	{
		m_account.printAccount(date, time);
	}
	
	/*
	 * 账户新日期初始化
	 * 持股均可卖出
	 */
	public boolean newDayInit(String date, String time)
	{
		BLog.output("ACCOUNT", "[%s %s] account new day reset \n", date, time);
		// 账户重新初始化
		m_account.newDayInit(date, time);
		return true;
	}
	
	// 获取账户总资产（根据日期时间来确定股价）
	public float getTotalAssets(String date, String time)
	{
		return m_account.getTotalAssets(date, time);
	}
	
	public float getAvailableMoney()
	{
		return m_account.getAvailableMoney();
	}
	
	public void setStockSelectList(List<String> stockIDList)
	{
		m_account.setStockSelectList(stockIDList);
	}
	
	public List<String> getStockSelectList()
	{
		return m_account.getStockSelectList();
	}

	/*
	 * 下买单委托
	 * 时间用于生成交割单
	 */
	public int pushBuyOrder(String date, String time, String stockID, float price, int amount)
	{
		return m_account.pushBuyOrder(date, time, stockID, price, amount);
	}

	/*
	 * 下卖单委托
	 * 时间用于生成交割单
	 */
	public int pushSellOrder(String date, String time, String stockID, float price, int amount)
	{
		return m_account.pushSellOrder(date, time, stockID, price, amount);
	}
	
	// 获得委托列表（未成交）
	public List<CommissionOrder> getCommissionOrderList()
	{
		return m_account.getCommissionOrderList();
	}
	// 获得买单委托列表（未成交）
	public List<CommissionOrder> getBuyCommissionOrderList()
	{
		return m_account.getBuyCommissionOrderList();
	}
	// 获得卖单委托列表（未成交）
	public List<CommissionOrder> getSellCommissionOrderList()
	{
		return m_account.getSellCommissionOrderList();
	}
	
	
	/*
	 * 获得持股列表
	 * 时间用户更新现价
	 * 如果传入null，则不更新现价
	 */
	public List<HoldStock> getStockHoldList(String date, String time)
	{
		return m_account.getHoldStockList(date, time);
	}
	public HoldStock getStockHold(String date, String time, String stockID)
	{
		List<HoldStock> cStockHoldList = getStockHoldList(date, time);
		for(int i=0;i<cStockHoldList.size();i++)
		{
			if(cStockHoldList.get(i).stockID.equals(stockID))
			{
				return cStockHoldList.get(i);
			}
		}
		return null;
	}
	
	// 获得交割单列表
	public List<DeliveryOrder> getDeliveryOrderList()
	{
		return m_account.getDeliveryOrderList();
	}
	
	/**
	 * 成员-----------------------------------------------------------------
	 */
	// 账户实体
	private AccountEntity m_account;
}
