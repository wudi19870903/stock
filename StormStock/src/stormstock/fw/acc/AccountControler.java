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
	
	// ѡ���б���Ӻϲ�
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
	// ѡ���б��ȡ
	public List<String> getStockSelectList()
	{
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!isInStockCreate(stockID))  // ѡ���б��ų����Ѿ��������б��
			{
				newList.add(stockID);
			}
		}
		return newList;
	}

	// ����
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
	// ����
	public int sellStock(String stockID, float price, int amount)
	{
		StockCreate cStockCreate = getStockCreate(stockID);
		int sellmount = Math.min(cStockCreate.amount, amount);
		if(sellmount<=0) return 0;
		
		int succCount = m_account.pushSellOrder(stockID, price, sellmount);
		if(succCount > 0)
		{
			cStockCreate.amount = cStockCreate.amount - succCount;
			
			// �Ƴ��������б�
			if(cStockCreate.amount == 0)
			{
				m_stockCreateList.remove(cStockCreate);
			}

			return succCount;
		}
		return 0;
	}
	
	// ��������б�
	public List<StockCreate> getStockCreateList()
	{
		return m_stockCreateList;
	}
	public StockCreate getStockCreate(String stockID)
	{
		for(int i=0;i<m_stockCreateList.size();i++)
		{
			StockCreate cStockCreate = m_stockCreateList.get(i);
			if(cStockCreate.stockID.equals(stockID))
				return cStockCreate;
		}
		return null;
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
	
	// ѡ���б�
	private List<String> m_stockSelectList;
	// �������б�
	public static class StockCreate
	{
		public String stockID;
		public float price;
		public int amount;
	}
	private List<StockCreate> m_stockCreateList;
}
