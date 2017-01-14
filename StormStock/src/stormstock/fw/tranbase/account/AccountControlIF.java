package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;

/*
 * �˻�������
 * ���Ի�ȡ�˻���Ϣ
 * ���Բ����˻�����
 */
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
	
	/*
	 * ���ĳ����ʱ����˻�������
	 * ���Ի�ȡ�˻���Ϣ
	 */
	public AccountAccessor getAccountAccessor(String date, String time)
	{
		return new AccountAccessor(date, time, this);
	}
	
	public void setAccountType(ACCOUNTTYPE eAccIFType)
	{
		IAccountOpe cIAccountOpe = null;
		if(eAccIFType == ACCOUNTTYPE.MOCK)
		{
			float moneyInit = 100000.00f;
			float transactionCostsRatio = 0.0016f;
			BLog.output("ACCOUNT", "setAccountType MOCK money:%.2f transactionCostsRatio:%.4f\n", 
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
	
	/*
	 * �˻������ڳ�ʼ��
	 * �ֹɾ�������
	 */
	public boolean newDayInit()
	{
		BLog.output("ACCOUNT", "new day reset...\n");
		// �˻����³�ʼ��
		m_account.newDayInit();
		return true;
	}
	
	// ��ȡ�˻����ʲ�����������ʱ����ȷ���ɼۣ�
	public float getTotalAssets(String date, String time)
	{
		return m_account.getTotalAssets(date, time);
	}
	
	public float getAvailableMoney()
	{
		return m_account.getAvailableMoney();
	}

	/*
	 * ����ί��
	 * ʱ���������ɽ��
	 */
	public int pushBuyOrder(String date, String time, String stockID, float price, int amount)
	{
		return m_account.pushBuyOrder(date, time, stockID, price, amount);
	}

	/*
	 * ������ί��
	 * ʱ���������ɽ��
	 */
	public int pushSellOrder(String date, String time, String stockID, float price, int amount)
	{
		return m_account.pushSellOrder(date, time, stockID, price, amount);
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
		List<HoldStock> cStockHoldList =  getStockHoldList(null,null);
		for(int i=0;i<cStockHoldList.size();i++)
		{
			m_stockSelectList.remove(cStockHoldList.get(i).stockID);
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
	// �������� �жϹ�Ʊ�Ƿ������ ������ί���б������б���
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
		
		List<HoldStock> cHoldStockList = m_account.getHoldStockList(null,null);
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
	
	// ���ί���б�δ�ɽ���
	public List<CommissionOrder> getCommissionOrderList()
	{
		return m_account.getCommissionOrderList();
	}
	// �����ί���б�δ�ɽ���
	public List<CommissionOrder> getBuyCommissionOrderList()
	{
		return m_account.getBuyCommissionOrderList();
	}
	// �������ί���б�δ�ɽ���
	public List<CommissionOrder> getSellCommissionOrderList()
	{
		return m_account.getSellCommissionOrderList();
	}
	
	
	/*
	 * ��óֹ��б�
	 * ʱ���û������ּ�
	 * �������null���򲻸����ּ�
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
	
	// ��ý���б�
	public List<DeliveryOrder> getDeliveryOrderList()
	{
		return m_account.getDeliveryOrderList();
	}
	
	/**
	 * ��Ա-----------------------------------------------------------------
	 */
	// �˻�ʵ��
	private AccountEntity m_account;
	// ѡ���б�
	private List<String> m_stockSelectList;
}
