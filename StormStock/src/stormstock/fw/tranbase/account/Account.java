package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;

public class Account {
	
	// �����˻�ʵ��ʱ����Ҫ��������ӿڣ�ģ�⣬��ʵ��
	public Account(ACCOUNTTYPE eAccType, String accountID, String password)
	{
		IAccountOpe cIAccountOpe = null;
		if(eAccType == ACCOUNTTYPE.MOCK)
		{
			cIAccountOpe = new MockAccountOpe(accountID, password, true);
		} 
		else if(eAccType == ACCOUNTTYPE.REAL)
		{
			cIAccountOpe = new RealAccountOpe(accountID, password);
		}
		m_cIAccountOpe = cIAccountOpe;
	}
	
	// ***********************************************************************
	// �����ӿڣ�ֱ�ӵ��ò����ӿ�
	
	// ���տ�ʼ�˻���ʼ��
	public boolean newDayInit(String date, String time)
	{
		return m_cIAccountOpe.newDayInit(date, time);
	}
	
	// ������ί�У�����ʵ���µ���
	public int pushBuyOrder(String date, String time, String id, float price, int amount)
	{
		return m_cIAccountOpe.pushBuyOrder(date, time, id, price, amount);
	}
	
	// ��������ί�У�����ʵ���µ���
	public int pushSellOrder(String date, String time, String id, float price, int amount)
	{
		return m_cIAccountOpe.pushSellOrder(date, time, id, price, amount);
	}
	
	// ����˻������ʽ��ֽ�
	public float getAvailableMoney()
	{
		return m_cIAccountOpe.getAvailableMoney();
	}
	
	// ���ί���б�(δ�ɽ��ģ����������������)
	public List<CommissionOrder> getCommissionOrderList()
	{
		return m_cIAccountOpe.getCommissionOrderList();
	}
	
	// ��óֹ��б������Ѿ����еģ��뵱���µ��ɽ��ģ�
	public List<HoldStock> getHoldStockList(String date, String time)
	{
		return m_cIAccountOpe.getHoldStockList(date, time);
	}
	
	// ��õ��ս���б��ѳɽ��ģ���������������ģ�
	public List<DealOrder> getDealOrderList()
	{
		return m_cIAccountOpe.getDealOrderList();
	}
		
	// ***********************************************************************
	// ��չ�ӿڣ�����ʵ���ڻ�������֮�ϵ���չ
	
	// ѡ���б�����
	public void setStockSelectList(List<String> stockIDList)
	{
		m_cIAccountOpe.setStockSelectList(stockIDList);
	}
	// ѡ���б��ȡ
	public List<String> getStockSelectList()
	{
		return m_cIAccountOpe.getStockSelectList();
	}

	// �����ί���б�(δ�ɽ���)
	public List<CommissionOrder> getBuyCommissionOrderList()
	{
		List<CommissionOrder> cBuyCommissionOrderList = new ArrayList<CommissionOrder>();
		List<CommissionOrder> cCommissionOrderList = getCommissionOrderList();
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.BUY)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				cBuyCommissionOrderList.add(cNewCommissionOrder);
			}
		}
		return cBuyCommissionOrderList;
	}
	
	// �����ί���б�(δ�ɽ���)
	public List<CommissionOrder> getSellCommissionOrderList()
	{
		List<CommissionOrder> cSellCommissionOrderList = new ArrayList<CommissionOrder>();
		List<CommissionOrder> cCommissionOrderList = getCommissionOrderList();
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.SELL)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				cSellCommissionOrderList.add(cNewCommissionOrder);
			}
		}
		return cSellCommissionOrderList;
	}
	
	// ����򽻸�б�(�ѳɽ���)
	public List<DealOrder> getBuyDealOrderList()
	{
		List<DealOrder> cBuyDealOrderList = new ArrayList<DealOrder>();
		List<DealOrder> cDealOrderList = getDealOrderList();
		for(int i= 0;i<cDealOrderList.size();i++)
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			if(cDealOrder.tranAct == TRANACT.BUY)
			{
				DealOrder cNewcDealOrder = new DealOrder();
				cNewcDealOrder.CopyFrom(cDealOrder);
				cBuyDealOrderList.add(cNewcDealOrder);
			}
		}
		return cBuyDealOrderList;
	}
	
	// ���������б�(�ѳɽ���)
	public List<DealOrder> getSellDealOrderList()
	{
		List<DealOrder> cSellDealOrderList = new ArrayList<DealOrder>();
		List<DealOrder> cDealOrderList = getDealOrderList();
		for(int i= 0;i<cDealOrderList.size();i++)
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			if(cDealOrder.tranAct == TRANACT.SELL)
			{
				DealOrder cNewDealOrder = new DealOrder();
				cNewDealOrder.CopyFrom(cDealOrder);
				cSellDealOrderList.add(cNewDealOrder);
			}
		}
		return cSellDealOrderList;
	}
	
	// ����˻����ʲ�
	public float getTotalAssets(String date, String time) {
		
		float all_marketval = 0.0f;
		List<HoldStock> cHoldStockList = getHoldStockList(date, time);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			all_marketval = all_marketval + cHoldStock.curPrice*cHoldStock.totalAmount;
		}
		float all_asset = all_marketval + getAvailableMoney();
		return all_asset;
	}
	
	public void printAccount(String date, String time)
	{
		BLog.output("ACCOUNT", "    ---ACCOUNT---INFO---\n");
		float fTotalAssets = this.getTotalAssets(date, time);
		float fAvailableMoney = this.getAvailableMoney();
		List<HoldStock> cStockHoldList = this.getHoldStockList(date, time);
		List<DealOrder> cDealOrderList = this.getDealOrderList();
		
		BLog.output("ACCOUNT", "    -TotalAssets: %.3f\n", fTotalAssets);
		BLog.output("ACCOUNT", "    -AvailableMoney: %.3f\n", fAvailableMoney);
		for(int i=0; i<cStockHoldList.size(); i++ )
		{
			HoldStock cHoldStock = cStockHoldList.get(i);
			BLog.output("ACCOUNT", "    -HoldStock: %s %.3f %.3f %d %.3f\n", 
					cHoldStock.stockID, 
					cHoldStock.refPrimeCostPrice, cHoldStock.curPrice, cHoldStock.totalAmount,
					cHoldStock.curPrice*cHoldStock.totalAmount);
		}
		for(int i=0; i<cDealOrderList.size(); i++ )
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cDealOrder.tranAct == TRANACT.SELL ) tranOpe = "SELL";
				
			BLog.output("ACCOUNT", "    -DealOrder: %s %s %s %d %.3f\n", 
					cDealOrder.time, tranOpe, cDealOrder.stockID, 
					cDealOrder.amount, cDealOrder.price);
		}
	}

	/** **********************************************************************
	 * �˻������ӿڣ���������Ϊģ�����ʵ
	 */
	private IAccountOpe m_cIAccountOpe;
}
