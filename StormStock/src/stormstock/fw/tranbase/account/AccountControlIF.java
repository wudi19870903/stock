package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;

/*
 * �˻�������
 * ���Ի�ȡ�˻���Ϣ
 * ���Բ����˻�����
 */
public class AccountControlIF {
	
	public AccountControlIF()
	{
		m_account = null;
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
	 * �˻������ڳ�ʼ��
	 * �ֹɾ�������
	 */
	public boolean newDayInit(String date, String time)
	{
		BLog.output("ACCOUNT", "[%s %s] account new day reset \n", date, time);
		// �˻����³�ʼ��
		m_account.newDayInit(date, time);
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
	
	public void setStockSelectList(List<String> stockIDList)
	{
		m_account.setStockSelectList(stockIDList);
	}
	
	public List<String> getStockSelectList()
	{
		return m_account.getStockSelectList();
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
}
