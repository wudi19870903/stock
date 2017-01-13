package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;

public class MockAccountOpe extends IAccountOpe {
	
	public MockAccountOpe(float money, float transactionCostsRatio)
	{
		super();
		m_money = money;
		m_transactionCostsRatio = transactionCostsRatio;
		m_commissionOrderList = new ArrayList<CommissionOrder>();
		m_holdStockList = new ArrayList<HoldStock>();
		m_deliveryOrderList = new ArrayList<DeliveryOrder>();
	}
	
	@Override
	public boolean newDayInit() 
	{ 
		// ��һ��ʱ��δ�ɽ�ί�е����
		m_commissionOrderList.clear();
		
		// ��һ��ʱ�����гֹɾ�����
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.totalCanSell = cHoldStock.totalAmount;
		}
		
		// ��һ��ʱ��������
		m_deliveryOrderList.clear();
		
		return true; 
	}

	@Override
	public int pushBuyOrder(String date, String time, String stockID, float price, int amount) {
		
		// ��������׼��
		int maxBuyAmount = (int)(m_money/price);
		int realBuyAmount = Math.min(maxBuyAmount, amount);
		realBuyAmount = realBuyAmount/100*100; 
		
		// ��ȡ���ж���
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.stockID == stockID)
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		if(null == cHoldStock)
		{
			HoldStock cNewHoldStock = new HoldStock();
			cNewHoldStock.stockID = stockID;
			cNewHoldStock.curPrice = price;
			m_holdStockList.add(cNewHoldStock);
			cHoldStock = cNewHoldStock;
		}
		
		// ���ö���
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		int oriTotalAmount = cHoldStock.totalAmount;
		float oriHoldAvePrice = cHoldStock.holdAvePrice;
		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount)/cHoldStock.totalAmount;
		cHoldStock.curPrice = price;

		cHoldStock.transactionCosts = cHoldStock.transactionCosts + transactionCosts;
		m_money = m_money - realBuyAmount*price;
		
		// ���ɽ��
		DeliveryOrder cDeliveryOrder = new DeliveryOrder();
		cDeliveryOrder.date = date;
		cDeliveryOrder.time = time;
		cDeliveryOrder.tranOpe = TRANACT.BUY;
		cDeliveryOrder.stockID = stockID;
		cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
		cDeliveryOrder.tranPrice = price;
		cDeliveryOrder.amount = realBuyAmount;
		cDeliveryOrder.transactionCost = 0.0f; // ����Ľ��׷�����ȫ������ʱ����
		m_deliveryOrderList.add(cDeliveryOrder);
		
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String date, String time, String stockID, float price, int amount) {
		
		// ��ȡ���ж���
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.stockID.equals(stockID))
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		
		if(null != cHoldStock)
		{
			// ��������׼��
			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
			realSellAmount = realSellAmount/100*100;
			
			// ���ö���
			int oriTotalAmount = cHoldStock.totalAmount;
			float oriHoldAvePrice = cHoldStock.holdAvePrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			if(cHoldStock.totalAmount == 0) // �����򲻼�������۸� ����
			{
				cHoldStock.holdAvePrice = 0.0f;
			}
			else
			{
				cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount - price*realSellAmount)/cHoldStock.totalAmount;
			}
			cHoldStock.curPrice = price;
			m_money = m_money + price*realSellAmount;
			
			// ��ּ���
			float DeliveryOrder_transactionCost = 0.0f;
			if(cHoldStock.totalAmount == 0)
			{
				m_money = m_money - cHoldStock.transactionCosts;
				DeliveryOrder_transactionCost = cHoldStock.transactionCosts;
				m_holdStockList.remove(cHoldStock);
			}
			
			// ���ɽ��
			DeliveryOrder cDeliveryOrder = new DeliveryOrder();
			cDeliveryOrder.date = date;
			cDeliveryOrder.time = time;
			cDeliveryOrder.tranOpe = TRANACT.SELL;
			cDeliveryOrder.stockID = stockID;
			cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
			cDeliveryOrder.tranPrice = price;
			cDeliveryOrder.amount = realSellAmount;
			cDeliveryOrder.transactionCost = DeliveryOrder_transactionCost; // ����Ľ��׷�����ȫ������ʱ����
			m_deliveryOrderList.add(cDeliveryOrder);
			
			return realSellAmount;
		}
	
		return 0;
	}

	@Override
	public float getAvailableMoney() {
		return m_money;
	}

	@Override
	public List<CommissionOrder> getCommissionOrderList() {
		return m_commissionOrderList;
	}
	
	@Override
	public List<HoldStock> getHoldStockList(String date, String time) {
		// ��������ʱ�����ʱ�����³ֹ��ּ�
		if(null != date && null != time)
		{
			for(int i = 0; i< m_holdStockList.size(); i++)
			{
				HoldStock cHoldStock = m_holdStockList.get(i);
				StockTime out_cStockTime = new StockTime();
				boolean bRet = StockDataIF.getStockTime(cHoldStock.stockID, date, time, out_cStockTime);
				if(bRet) cHoldStock.curPrice = out_cStockTime.price;
			}
		}
		return m_holdStockList;
	}
	
	@Override
	public List<DeliveryOrder> getDeliveryOrderList() {
		return m_deliveryOrderList;
	}
	
	/**
	 * ��Ա-----------------------------------------------------------------------
	 */
	private float m_money;
	private float m_transactionCostsRatio;
	private List<CommissionOrder> m_commissionOrderList; // ģ���˻���  �µ�ֱ�ӳɽ�, ί�е�һֱδ��
	private List<HoldStock> m_holdStockList;
	private List<DeliveryOrder> m_deliveryOrderList;
}