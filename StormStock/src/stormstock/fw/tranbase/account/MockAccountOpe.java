package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountElementDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountElementDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountElementDef.HoldStock;
import stormstock.fw.tranbase.account.AccountElementDef.TRANACT;
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
	public int pushBuyOrder(String id, float price, int amount) {
		
		int maxBuyAmount = (int)(m_money/price);
		int realBuyAmount = maxBuyAmount>amount?amount:maxBuyAmount;
		
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.id == id)
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		
		if(null != cHoldStock)
		{
			cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
			
			int oriAmount = cHoldStock.totalAmount;
			float oriPrice = cHoldStock.buyPrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
			cHoldStock.buyPrice = (oriPrice*oriAmount + price*realBuyAmount)/cHoldStock.totalAmount;
			cHoldStock.transactionCosts = cHoldStock.transactionCosts + transactionCosts;
			
		}
		else
		{
			HoldStock cNewHoldStock = new HoldStock();
			cNewHoldStock.id = id;
			cNewHoldStock.buyPrice = price;
			cNewHoldStock.totalAmount = realBuyAmount;
			cNewHoldStock.transactionCosts = transactionCosts;
			m_holdStockList.add(cNewHoldStock);
		}
		
		m_money = m_money - realBuyAmount*price;
		
		// ���뽻�
		DeliveryOrder cDeliveryOrder = new DeliveryOrder();
		cDeliveryOrder.tranOpe = TRANACT.BUY;
		cDeliveryOrder.id = id;
		cDeliveryOrder.price = price;
		cDeliveryOrder.amount = realBuyAmount;
		cDeliveryOrder.transactionCost = 0.0f; // ����Ľ��׷�����ȫ������ʱ����
		m_deliveryOrderList.add(cDeliveryOrder);
		
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String id, float price, int amount) {
		
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.id.equals(id))
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		
		if(null != cHoldStock)
		{
			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
			
			int oriAmount = cHoldStock.totalAmount;
			float oriPrice = cHoldStock.buyPrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			
			if(cHoldStock.totalAmount == 0) // �����򲻼�������۸� ����
			{
				cHoldStock.buyPrice = 0.0f;
			}
			else
			{
				cHoldStock.buyPrice = (oriPrice*oriAmount - price*realSellAmount)/cHoldStock.totalAmount;
			}
			m_money = m_money + price*realSellAmount;
			
			
			float DeliveryOrder_transactionCost = 0.0f;
			if(cHoldStock.totalAmount == 0)
			{
				m_money = m_money - cHoldStock.transactionCosts;
				DeliveryOrder_transactionCost = cHoldStock.transactionCosts;
				m_holdStockList.remove(cHoldStock);
			}
			
			// �������
			DeliveryOrder cDeliveryOrder = new DeliveryOrder();
			cDeliveryOrder.tranOpe = TRANACT.SELL;
			cDeliveryOrder.id = id;
			cDeliveryOrder.price = price;
			cDeliveryOrder.amount = realSellAmount;
			cDeliveryOrder.transactionCost = DeliveryOrder_transactionCost; // ���ʱ�������
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
	public List<HoldStock> getHoldStockList() {
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
	private List<CommissionOrder> m_commissionOrderList; // ģ���˻���  �µ�ֱ�ӳɽ�, ��listһֱδ��
	private List<HoldStock> m_holdStockList;
	private List<DeliveryOrder> m_deliveryOrderList;
}
