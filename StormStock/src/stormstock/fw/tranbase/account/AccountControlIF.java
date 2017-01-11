package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountElementDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountElementDef.HoldStock;

public class AccountControlIF {
	
	public enum ACCOUNTTYPE 
	{
		MOCK,
		REAL,
	}
	
	public AccountControlIF()
	{
		m_account = null;
		m_stockSelectList = new ArrayList<String>();
	}
	
	
	public void setAccountType(ACCOUNTTYPE eAccIFType)
	{
		IAccountOpe cIAccountOpe = null;
		if(eAccIFType == ACCOUNTTYPE.MOCK)
		{
			float moneyInit = 100000.00f;
			float transactionCostsRatio = 0.0016f;
			BLog.output("ACCOUNT", "setAccountType MOCK money:%.2f transactionCostsRatio:%.2f\n", 
					moneyInit, transactionCostsRatio);
			cIAccountOpe = new MockAccountOpe(moneyInit, transactionCostsRatio);
		} 
		else if(eAccIFType == ACCOUNTTYPE.REAL)
		{
			BLog.output("ACCOUNT", "setAccountType REAL \n");
			cIAccountOpe = new RealAccountOpe();
		}
		m_account = new AccountEntity(cIAccountOpe);
	}
	
	public boolean newDayInit()
	{
		BLog.output("ACCOUNT", "------>>> newDayInit <<<------ \n");
		// 账户重新初始化
		m_account.newDayInit();
		return true;
	}
	
	public float getTotalAssets()
	{
		return m_account.getTotalAssets();
	}
	
	public float getAvailableMoney()
	{
		return m_account.getAvailableMoney();
	}

	public int pushBuyOrder(String stockID, float price, int amount)
	{
		return m_account.pushBuyOrder(stockID, price, amount);
	}

	public int pushSellOrder(String stockID, float price, int amount)
	{
		return m_account.pushSellOrder(stockID, price, amount);
	}
	
	
	// 选股列表添加合并
	public void addStockSelectList(List<String> stockIDList)
	{
		for(int i=0; i<stockIDList.size();i++)
		{
			String newstockID = stockIDList.get(i);
			if(!m_stockSelectList.contains(newstockID))
			{
				m_stockSelectList.add(newstockID);
			}
		}
		
		// 选股中排除已经持有的
		List<HoldStock> cStockHoldList =  getStockHoldList();
		for(int i=0;i<cStockHoldList.size();i++)
		{
			m_stockSelectList.remove(cStockHoldList.get(i).stockID);
		}
	}
	
	// 选股列表获取
	public List<String> getStockSelectList()
	{
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!help_inAccount(stockID))  // 选股列表排除掉已经在买入列表的
			{
				newList.add(stockID);
			}
		}
		return newList;
	}
	// 帮助函数 判断股票是否存在于 买卖单委托列表，持有列表中
	private boolean help_inAccount(String stockID)
	{
		List<CommissionOrder> cCommissionOrderList = m_account.getCommissionOrderList();
		for(int i=0;i<cCommissionOrderList.size();i++)
		{
			if(cCommissionOrderList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = m_account.getHoldStockList();
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		return false;
	}
	public boolean clearStockSelectList()
	{
		m_stockSelectList.clear();
		return true;
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
	
	
	// 获得买入列表
	public List<HoldStock> getStockHoldList()
	{
		return m_account.getHoldStockList();
	}
	public HoldStock getStockHold(String stockID)
	{
		List<HoldStock> cStockHoldList = getStockHoldList();
		for(int i=0;i<cStockHoldList.size();i++)
		{
			if(cStockHoldList.get(i).stockID.equals(stockID))
			{
				return cStockHoldList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 成员-----------------------------------------------------------------
	 */
	// 账户实体
	private AccountEntity m_account;
	// 选股列表
	private List<String> m_stockSelectList;
}
