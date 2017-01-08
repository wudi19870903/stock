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
		// �˻����³�ʼ��
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
		
		// ѡ�����ų��Ѿ����е�
		List<HoldStock> cStockHoldList =  getStockHoldList();
		for(int i=0;i<cStockHoldList.size();i++)
		{
			m_stockSelectList.remove(cStockHoldList.get(i).id);
		}
	}
	
	// ѡ���б��ȡ
	public List<String> getStockSelectList()
	{
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!help_inAccount(stockID))  // ѡ���б��ų����Ѿ��������б��
			{
				newList.add(stockID);
			}
		}
		return newList;
	}
	// �жϹ�Ʊ�Ƿ������ ��ί���б������б�����ί���б���
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
	
	// �����ί���б�δ�ɽ���
	public List<StockTranOrder> getBuyOrderList()
	{
		return m_accountOpe.getBuyOrderList();
	}
	
	
	// ��������б�
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
	 * ��Ա-----------------------------------------------------------------
	 */
	// �˻�������
	private IAccountOpe m_accountOpe;
	// ѡ���б�
	private List<String> m_stockSelectList;
}
