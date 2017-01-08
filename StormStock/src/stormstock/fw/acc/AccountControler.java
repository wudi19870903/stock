package stormstock.fw.acc;

import java.util.ArrayList;
import java.util.List;

import stormstock.analysis.ANLUserAcc.ANLUserAccStock;
import stormstock.fw.acc.AccountControler.StockCreate;
import stormstock.fw.base.BLog;

public class AccountControler {
	
	public AccountControler()
	{
		m_stockSelectList = new ArrayList<String>();
		m_stockCreateList = new ArrayList<StockCreate>();
	}
	
	public void setCurAccount(IAccount cIAccount)
	{
		m_account = cIAccount;
	}
	public float getTotalAssets()
	{
		float all_marketval = 0.0f;
		for(int i=0;i<m_stockCreateList.size();i++)
		{
			StockCreate cStockCreate = m_stockCreateList.get(i);
			all_marketval = all_marketval + cStockCreate.price*cStockCreate.amount;
		}
		float all_asset = all_marketval + m_account.GetAvailableTotalMoney();
		return all_asset;
	}
	public float getAvailableTotalMoney()
	{
		return m_account.GetAvailableTotalMoney();
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
	}
	// 选股列表获取
	public List<String> getStockSelectList()
	{
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!isInStockCreate(stockID))  // 选股列表排除掉已经在买入列表的
			{
				newList.add(stockID);
			}
		}
		return newList;
	}

	// 买入
	public int buyStock(String stockID, float price, int amount)
	{
		for(int i=0; i< m_stockCreateList.size();i++)
		{
			StockCreate cStockCreate = m_stockCreateList.get(i);
			if(cStockCreate.stockID == stockID)
			{
				return 0;
			}
		}
		
		int succCount = m_account.pushBuyOrder(stockID, price, amount);
		if(succCount >0)
		{
			StockCreate newStockCreate = new StockCreate();
			newStockCreate.stockID = stockID;
			newStockCreate.price = price;
			newStockCreate.amount = amount;
			m_stockCreateList.add(newStockCreate);
			return succCount;
		}
		return 0;
	}
	// 获得买入列表
	public List<StockCreate> getStockCreateList()
	{
		return m_stockCreateList;
	}
	public boolean isInStockCreate(String stockID)
	{
		for(int i=0;i<m_stockCreateList.size();i++)
		{
			StockCreate cStockCreate = m_stockCreateList.get(i);
			if(cStockCreate.stockID.compareTo(stockID) == 0)
			{
				return true;
			}
		}
		return false;
	}
	
	
	private IAccount m_account;
	
	// 选股列表
	private List<String> m_stockSelectList;
	// 已买入列表
	public static class StockCreate
	{
		String stockID;
		float price;
		int amount;
	}
	private List<StockCreate> m_stockCreateList;
}
