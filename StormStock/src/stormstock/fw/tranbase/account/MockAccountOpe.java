package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;

public class MockAccountOpe extends IAccountOpe {
	
	public MockAccountOpe(String accountName, String password)
	{
		super();
		
		m_money = 100000.00f;
		m_transactionCostsRatio = 0.0016f;
		m_stockSelectList = new ArrayList<String>();
		m_commissionOrderList = new ArrayList<CommissionOrder>();
		m_holdStockList = new ArrayList<HoldStock>();
		m_deliveryOrderList = new ArrayList<DeliveryOrder>();
		
		BLog.output("ACCOUNT", "Account MOCK AccountName:%s Password:%s money:%.2f transactionCostsRatio:%.4f\n", 
				accountName, password, m_money, m_transactionCostsRatio);
	}
	
	@Override
	public boolean newDayInit(String date, String time) 
	{ 
		// 新一天时，未成交委托单清空
		m_commissionOrderList.clear();
		
		// 新一天时，所有持股均可卖，持仓天数+1
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.totalCanSell = cHoldStock.totalAmount;
			cHoldStock.holdDayCnt = cHoldStock.holdDayCnt + 1;
		}
		
		// 新一天时，交割单清空
		m_deliveryOrderList.clear();
		
		return true; 
	}

	@Override
	public int pushBuyOrder(String date, String time, String stockID, float price, int amount) {
		
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
			cNewHoldStock.createDate = date;
			cNewHoldStock.createTime = time;
			cNewHoldStock.curPrice = price;
			m_holdStockList.add(cNewHoldStock);
			cHoldStock = cNewHoldStock;
		}
		
		// 重置对象
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		int oriTotalAmount = cHoldStock.totalAmount;
		float oriHoldAvePrice = cHoldStock.holdAvePrice;
		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount)/cHoldStock.totalAmount;
		cHoldStock.curPrice = price;

		cHoldStock.transactionCost = cHoldStock.transactionCost + transactionCosts;
		m_money = m_money - realBuyAmount*price;
		
		BLog.output("ACCOUNT", " @Buy [%s %s] [%s %.3f %d %.3f(%.3f) %.3f] \n", 
				date, time,
				stockID, price, realBuyAmount, price*realBuyAmount, transactionCosts, m_money);
		
		// 生成交割单
		DeliveryOrder cDeliveryOrder = new DeliveryOrder();
		cDeliveryOrder.date = date;
		cDeliveryOrder.time = time;
		cDeliveryOrder.tranAct = TRANACT.BUY;
		cDeliveryOrder.stockID = stockID;
		cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
		cDeliveryOrder.tranPrice = price;
		cDeliveryOrder.amount = realBuyAmount;
		cDeliveryOrder.transactionCost = 0.0f; // 交割单的交易费用在全部卖出时结算
		m_deliveryOrderList.add(cDeliveryOrder);
		
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String date, String time, String stockID, float price, int amount) {
		
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
			cHoldStock.curPrice = price;
			m_money = m_money + price*realSellAmount;
			
			// 清仓计算
			float DeliveryOrder_transactionCost = 0.0f;
			if(cHoldStock.totalAmount == 0)
			{
				m_money = m_money - cHoldStock.transactionCost;
				DeliveryOrder_transactionCost = cHoldStock.transactionCost;
				m_holdStockList.remove(cHoldStock);
			}
			
			BLog.output("ACCOUNT", " @Sell [%s %s] [%s %.3f %d %.3f(%.3f) %.3f] \n", 
					date, time,
					stockID, price, realSellAmount, price*realSellAmount, cHoldStock.transactionCost, m_money);
			
			// 生成交割单
			DeliveryOrder cDeliveryOrder = new DeliveryOrder();
			cDeliveryOrder.date = date;
			cDeliveryOrder.time = time;
			cDeliveryOrder.tranAct = TRANACT.SELL;
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
	public void setStockSelectList(List<String> stockIDList) {
		m_stockSelectList.clear();
		for(int i=0; i<stockIDList.size();i++)
		{
			String newstockID = stockIDList.get(i);
			m_stockSelectList.add(newstockID);
		}
		
		// 选股中排除已经持有的
		List<HoldStock> cStockHoldList =  getHoldStockList(null,null);
		for(int i=0;i<cStockHoldList.size();i++)
		{
			m_stockSelectList.remove(cStockHoldList.get(i).stockID);
		}
	}

	@Override
	public List<String> getStockSelectList() {
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
	// 帮助函数 判断股票是否存在于 买卖单委托列表，持有列表中
	private boolean help_inAccount(String stockID)
	{
		List<CommissionOrder> cCommissionOrderList = this.getCommissionOrderList();
		for(int i=0;i<cCommissionOrderList.size();i++)
		{
			if(cCommissionOrderList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = this.getHoldStockList(null,null);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<CommissionOrder> getCommissionOrderList() {
		return m_commissionOrderList;
	}
	
	@Override
	public List<HoldStock> getHoldStockList(String date, String time) {
		// 当有日期时间参数时，更新持股现价
		if(null != date && null != time)
		{
			for(int i = 0; i< m_holdStockList.size(); i++)
			{
				HoldStock cHoldStock = m_holdStockList.get(i);
				ResultStockTime cResultStockTime = GlobalUserObj.getCurStockDataIF().getStockTime(cHoldStock.stockID, date, time);
				if(0 == cResultStockTime.error)
				{
					cHoldStock.curPrice = cResultStockTime.stockTime.price;
				}
			}
		}
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
	
	private List<String> m_stockSelectList; // 选股列表
	
	private List<CommissionOrder> m_commissionOrderList; // 模拟账户中  下单直接成交, 委托单一直未空
	private List<HoldStock> m_holdStockList;
	private List<DeliveryOrder> m_deliveryOrderList;
}
