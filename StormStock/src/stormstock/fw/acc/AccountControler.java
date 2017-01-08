package stormstock.fw.acc;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.acc.AccountModuleIF.ACCIFTYPE;
import stormstock.fw.acc.IAccountOpe.HoldStock;
import stormstock.fw.acc.IAccountOpe.StockTranOrder;
import stormstock.fw.base.BLog;

public class AccountControler {
	
	public AccountControler()
	{
		m_accountOpe = null;
		m_stockSelectList = new ArrayList<String>();
	}
	
	public void setAccountIFType(ACCIFTYPE eAccIFType)
	{
		if(eAccIFType == ACCIFTYPE.MOCK)
		{
			m_accountOpe = new MockAccountOpe(100000.00f, 0.0016f);
		} 
		else if(eAccIFType == ACCIFTYPE.REAL)
		{
			m_accountOpe = new RealAccountOpe();
		}
	}
	
	public boolean newDayInit()
	{
		// 账户重新初始化
		m_accountOpe.newDayInit();
		return true;
	}
	
	public float getTotalAssets()
	{
		return m_accountOpe.getTotalAssets();
	}
	
	public float getAvailableMoney()
	{
		return m_accountOpe.getAvailableMoney();
	}

	public int pushBuyOrder(String stockID, float price, int amount)
	{
		return m_accountOpe.pushBuyOrder(stockID, price, amount);
	}

	public int pushSellOrder(String stockID, float price, int amount)
	{
		return m_accountOpe.pushSellOrder(stockID, price, amount);
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
			m_stockSelectList.remove(cStockHoldList.get(i).id);
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
	// 判断股票是否存在于 买单委托列表，持有列表，卖单委托列表中
	public boolean help_inAccount(String stockID)
	{
		List<StockTranOrder> cBuyOrderList = m_accountOpe.getBuyOrderList();
		for(int i=0;i<cBuyOrderList.size();i++)
		{
			if(cBuyOrderList.get(i).id.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = m_accountOpe.getHoldStockList();
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).id.equals(stockID))
			{
				return true;
			}
		}
		
		List<StockTranOrder> cSellOrderList = m_accountOpe.getSellOrderList();
		for(int i=0;i<cSellOrderList.size();i++)
		{
			if(cSellOrderList.get(i).id.equals(stockID))
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
	public List<StockTranOrder> getBuyOrderList()
	{
		return m_accountOpe.getBuyOrderList();
	}
	
	
	// 获得买入列表
	public List<HoldStock> getStockHoldList()
	{
		return m_accountOpe.getHoldStockList();
	}
	public HoldStock getStockStock(String stockID)
	{
		List<HoldStock> cStockHoldList = getStockHoldList();
		for(int i=0;i<cStockHoldList.size();i++)
		{
			if(cStockHoldList.get(i).id.equals(stockID))
			{
				return cStockHoldList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 成员-----------------------------------------------------------------
	 */
	// 账户操作器
	private IAccountOpe m_accountOpe;
	// 选股列表
	private List<String> m_stockSelectList;
}
