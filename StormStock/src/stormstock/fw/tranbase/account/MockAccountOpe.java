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
		// 新一天时，未成交委托单清空
		m_commissionOrderList.clear();
		
		// 新一天时，所有持股均可卖
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.totalCanSell = cHoldStock.totalAmount;
		}
		
		// 新一天时，交割单清空
		m_deliveryOrderList.clear();
		
		return true; 
	}

	@Override
	public int pushBuyOrder(String stockID, float price, int amount) {
		
		// 买入量标准化
		int maxBuyAmount = (int)(m_money/price);
		int realBuyAmount = Math.min(maxBuyAmount, amount);
		realBuyAmount = realBuyAmount/100*100; 
		
		// 获取持有对象
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
			m_holdStockList.add(cNewHoldStock);
			cHoldStock = cNewHoldStock;
		}
		
		// 重置对象
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		int oriTotalAmount = cHoldStock.totalAmount;
		float oriHoldAvePrice = cHoldStock.holdAvePrice;
		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount)/cHoldStock.totalAmount;
		cHoldStock.transactionCosts = cHoldStock.transactionCosts + transactionCosts;
		m_money = m_money - realBuyAmount*price;
		
		// 生成交割单
		DeliveryOrder cDeliveryOrder = new DeliveryOrder();
		cDeliveryOrder.tranOpe = TRANACT.BUY;
		cDeliveryOrder.stockID = stockID;
		cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
		cDeliveryOrder.tranPrice = price;
		cDeliveryOrder.amount = realBuyAmount;
		cDeliveryOrder.transactionCost = 0.0f; // 交割单的交易费用在全部卖出时结算
		m_deliveryOrderList.add(cDeliveryOrder);
		
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String stockID, float price, int amount) {
		
		// 获取持有对象
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
			// 卖出量标准化
			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
			realSellAmount = realSellAmount/100*100;
			
			// 重置对象
			int oriTotalAmount = cHoldStock.totalAmount;
			float oriHoldAvePrice = cHoldStock.holdAvePrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			if(cHoldStock.totalAmount == 0) // 卖光则不计算买入价格 清零
			{
				cHoldStock.holdAvePrice = 0.0f;
			}
			else
			{
				cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount - price*realSellAmount)/cHoldStock.totalAmount;
			}
			m_money = m_money + price*realSellAmount;
			
			// 清仓计算
			float DeliveryOrder_transactionCost = 0.0f;
			if(cHoldStock.totalAmount == 0)
			{
				m_money = m_money - cHoldStock.transactionCosts;
				DeliveryOrder_transactionCost = cHoldStock.transactionCosts;
				m_holdStockList.remove(cHoldStock);
			}
			
			// 生成交割单
			DeliveryOrder cDeliveryOrder = new DeliveryOrder();
			cDeliveryOrder.tranOpe = TRANACT.SELL;
			cDeliveryOrder.stockID = stockID;
			cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
			cDeliveryOrder.tranPrice = price;
			cDeliveryOrder.amount = realSellAmount;
			cDeliveryOrder.transactionCost = DeliveryOrder_transactionCost; // 交割单的交易费用在全部卖出时结算
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
	 * 成员-----------------------------------------------------------------------
	 */
	private float m_money;
	private float m_transactionCostsRatio;
	private List<CommissionOrder> m_commissionOrderList; // 模拟账户中  下单直接成交, 次list一直未空
	private List<HoldStock> m_holdStockList;
	private List<DeliveryOrder> m_deliveryOrderList;
}
