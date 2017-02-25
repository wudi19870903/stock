package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BTypeDefine.RefFloat;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
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
			m_account = new Account(eAccIFType, "mock001", "mock001_password");
		}
		if(ACCOUNTTYPE.REAL == eAccIFType)
		{
			m_account = new Account(eAccIFType, "xxx", "xxx");
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
		boolean bRet = true;
		BLog.output("ACCOUNT", "[%s %s] account new day reset \n", date, time);
		// 账户重新初始化
		int err = m_account.newDayInit(date, time);
		if(0 != err)
		{
			bRet = false;
			BLog.error("ACCOUNT", "[%s %s] m_account.newDayInit err(%d) \n", date, time, err);
		}
		return bRet;
	}
	
	public boolean newDayTranEnd(String date, String time)
	{
		boolean bRet = true;
		BLog.output("ACCOUNT", "[%s %s] account new day tran end \n", date, time);
		// 账户当日结束
		int err = m_account.newDayTranEnd(date, time);
		if(0 != err)
		{
			bRet = false;
			BLog.error("ACCOUNT", "[%s %s] m_account.newDayTranEnd err(%d) \n", date, time, err);
		}
		return bRet;
	}
	
	// 获取账户总资产（根据日期时间来确定股价）
	public float getTotalAssets(String date, String time)
	{
		return m_account.getTotalAssets(date, time);
	}
	
	public int getAvailableMoney(RefFloat out_availableMoney)
	{
		return m_account.getAvailableMoney(out_availableMoney);
	}
	
	public int setStockSelectList(List<String> stockIDList)
	{
		return m_account.setStockSelectList(stockIDList);
	}
	
	public int getStockSelectList(List<String> out_list)
	{
		return m_account.getStockSelectList(out_list);
	}

	/*
	 * 下买单委托
	 * 时间用于生成交割单
	 */
	public int pushBuyOrder(String date, String time, String stockID, int amount, float price)
	{
		return m_account.pushBuyOrder(date, time, stockID, amount, price);
	}

	/*
	 * 下卖单委托
	 * 时间用于生成交割单
	 */
	public int pushSellOrder(String date, String time, String stockID, int amount, float price)
	{
		return m_account.pushSellOrder(date, time, stockID, amount, price);
	}
	
	// 获得委托列表（未成交）
	public int getCommissionOrderList(List<CommissionOrder> out_list)
	{
		return m_account.getCommissionOrderList(out_list);
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
	public int getHoldStockList(String date, String time, List<HoldStock> out_list)
	{
		return m_account.getHoldStockList(date, time, out_list);
	}
	public HoldStock getHoldStock(String date, String time, String stockID)
	{
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		getHoldStockList(date, time, cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				return cHoldStockList.get(i);
			}
		}
		return null;
	}
	
	// 获得交割单列表
	public int getDealOrderList(List<DealOrder> out_list)
	{
		return m_account.getDealOrderList(out_list);
	}
	
	/**
	 * 成员-----------------------------------------------------------------
	 */
	// 账户实体
	private Account m_account;
}
