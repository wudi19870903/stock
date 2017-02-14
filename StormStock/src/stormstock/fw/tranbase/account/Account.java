package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BTypeDefine.RefFloat;
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
	public int newDayInit(String date, String time)
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
	public int getAvailableMoney(RefFloat out_availableMoney)
	{
		return m_cIAccountOpe.getAvailableMoney(out_availableMoney);
	}
	
	// ���ί���б�(δ�ɽ��ģ����������������)
	public int getCommissionOrderList(List<CommissionOrder> out_list)
	{
		return m_cIAccountOpe.getCommissionOrderList(out_list);
	}
	
	// ��óֹ��б������Ѿ����еģ��뵱���µ��ɽ��ģ�
	public int getHoldStockList(String date, String time, List<HoldStock> out_list)
	{
		return m_cIAccountOpe.getHoldStockList(date, time, out_list);
	}
	
	// ��õ��ս���б��ѳɽ��ģ���������������ģ�
	public int getDealOrderList(List<DealOrder> out_list)
	{
		return m_cIAccountOpe.getDealOrderList(out_list);
	}
		
	// ***********************************************************************
	// ��չ�ӿڣ�����ʵ���ڻ�������֮�ϵ���չ
	
	// ѡ���б�����
	public int setStockSelectList(List<String> stockIDList)
	{
		return m_cIAccountOpe.setStockSelectList(stockIDList);
	}
	// ѡ���б��ȡ
	public int getStockSelectList(List<String> out_list)
	{
		return m_cIAccountOpe.getStockSelectList(out_list);
	}

	// �����ί���б�(δ�ɽ���)
	public List<CommissionOrder> getBuyCommissionOrderList()
	{
		List<CommissionOrder> cBuyCommissionOrderList = new ArrayList<CommissionOrder>();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
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
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
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
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		getDealOrderList(cDealOrderList);
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
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		getDealOrderList(cDealOrderList);
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
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		getHoldStockList(date, time, cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			all_marketval = all_marketval + cHoldStock.curPrice*cHoldStock.totalAmount;
		}
		RefFloat availableMoney = new RefFloat();
		getAvailableMoney(availableMoney);
		float all_asset = all_marketval + availableMoney.value;
		return all_asset;
	}
	
	public void printAccount(String date, String time)
	{
		BLog.output("ACCOUNT", "    ---ACCOUNT---INFO---\n");
		float fTotalAssets = this.getTotalAssets(date, time);
		RefFloat availableMoney = new RefFloat();
		this.getAvailableMoney(availableMoney);
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		this.getHoldStockList(date, time, cHoldStockList);
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		this.getDealOrderList(cDealOrderList);
		
		BLog.output("ACCOUNT", "    -TotalAssets: %.3f\n", fTotalAssets);
		BLog.output("ACCOUNT", "    -AvailableMoney: %.3f\n", availableMoney.value);
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
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
