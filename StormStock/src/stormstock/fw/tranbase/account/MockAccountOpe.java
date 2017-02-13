package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.account.MockAccountOpeStore.StoreEntity;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;

public class MockAccountOpe extends IAccountOpe {
	
	public MockAccountOpe(String accountID, String password, boolean connectFlag)
	{
		super();
		
		m_transactionCostsRatio = 0.0016f;
		
		m_accountID = accountID;
		m_password = password;
		
		m_dataSyncFlag = connectFlag;
		m_mockAccountOpeStore = new MockAccountOpeStore(m_accountID, m_password);

		// 交易流数据
		{
			m_money = 100000.00f;
			m_stockSelectList = new ArrayList<String>();
			m_commissionOrderList = new ArrayList<CommissionOrder>();
			m_holdStockList = new ArrayList<HoldStock>();
			m_dealOrderList = new ArrayList<DealOrder>();
			
			//load
			load();
		}

		BLog.output("ACCOUNT", "Account MOCK AccountID:%s Password:%s money:%.2f transactionCostsRatio:%.4f\n", 
				m_accountID, password, m_money, m_transactionCostsRatio);
	}
	
	@Override
	public boolean newDayInit(String date, String time) 
	{ 
		// 新一天时，未成交委托单清空
		m_commissionOrderList.clear();
		
		// 新一天时，所有持股均可卖，监控天数加1
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.availableAmount = cHoldStock.totalAmount;
			cHoldStock.investigationDays = cHoldStock.investigationDays + 1;
		}
		
		// 新一天时，交割单清空
		m_dealOrderList.clear();
		
		store();
		
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
			cNewHoldStock.curPrice = price;
			m_holdStockList.add(cNewHoldStock);
			cHoldStock = cNewHoldStock;
		}
		
		// 重置对象 (交易费用直接体现在参考成本价里)
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		int oriTotalAmount = cHoldStock.totalAmount;
		float oriHoldAvePrice = cHoldStock.refPrimeCostPrice;
		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		cHoldStock.refPrimeCostPrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount + transactionCosts)/cHoldStock.totalAmount;
		cHoldStock.curPrice = price;
		
		m_money = m_money - realBuyAmount*price;
		
		BLog.output("ACCOUNT", " @Buy [%s %s] [%s %.3f %d %.3f(%.3f) %.3f] \n", 
				date, time,
				stockID, price, realBuyAmount, price*realBuyAmount, transactionCosts, m_money);
		
		// 生成交割单
		DealOrder cDealOrder = new DealOrder();
		cDealOrder.time = time;
		cDealOrder.tranAct = TRANACT.BUY;
		cDealOrder.stockID = stockID;
		cDealOrder.amount = realBuyAmount;
		cDealOrder.price = price;
		
		m_dealOrderList.add(cDealOrder);
		
		store();
		
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
			
			// 重置对象 (交易费用在卖出价钱中扣除)
			float transactionCosts = m_transactionCostsRatio*price*realSellAmount;
			int oriTotalAmount = cHoldStock.totalAmount;
			float oriHoldAvePrice = cHoldStock.refPrimeCostPrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			cHoldStock.curPrice = price;
			m_money = m_money + price*realSellAmount - transactionCosts;
			if(cHoldStock.totalAmount == 0) // 卖光则不计算买入价格 清零
			{
				cHoldStock.refPrimeCostPrice = 0.0f;
			}
			else
			{
				cHoldStock.refPrimeCostPrice = (oriHoldAvePrice*oriTotalAmount - price*realSellAmount - transactionCosts)/cHoldStock.totalAmount;
			}
			
			// 清仓计算
			if(cHoldStock.totalAmount == 0)
			{
				m_holdStockList.remove(cHoldStock);
			}
			
			BLog.output("ACCOUNT", " @Sell [%s %s] [%s %.3f %d %.3f(%.3f) %.3f] \n", 
					date, time,
					stockID, price, realSellAmount, price*realSellAmount, transactionCosts, m_money);
			
			// 生成交割单
			DealOrder cDealOrder = new DealOrder();
			cDealOrder.time = time;
			cDealOrder.tranAct = TRANACT.SELL;
			cDealOrder.stockID = stockID;
			cDealOrder.price = price;
			cDealOrder.amount = realSellAmount;
			m_dealOrderList.add(cDealOrder);
			
			store();
			
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
		
		store();
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
	public List<DealOrder> getDealOrderList() {
		return m_dealOrderList;
	}
	
	private void load()
	{
		if(m_dataSyncFlag)
		{
			StoreEntity cStoreEntity = m_mockAccountOpeStore.load();
			if(null != cStoreEntity)
			{
			    m_money = cStoreEntity.money;
			    m_stockSelectList.clear();
			    m_stockSelectList.addAll(cStoreEntity.stockSelectList);
			    m_commissionOrderList.clear();
			    m_commissionOrderList.addAll(cStoreEntity.commissionOrderList);
			    m_holdStockList.clear();
			    m_holdStockList.addAll(cStoreEntity.holdStockList);
			    m_dealOrderList.clear();
			    m_dealOrderList.addAll(cStoreEntity.dealOrderList);
			}
		}
	}
	
	private void store()
	{
		if(m_dataSyncFlag)
		{
			StoreEntity cStoreEntity = new StoreEntity();
			cStoreEntity.money = m_money;
			cStoreEntity.stockSelectList = m_stockSelectList;
			cStoreEntity.commissionOrderList = m_commissionOrderList;
			cStoreEntity.holdStockList = m_holdStockList;
			cStoreEntity.dealOrderList = m_dealOrderList;
			m_mockAccountOpeStore.store(cStoreEntity);
		}
	}

	/**
	 * 成员-----------------------------------------------------------------------
	 */

	private float m_transactionCostsRatio;
	private String m_accountID;
	private String m_password;
	
	private boolean m_dataSyncFlag;
	private MockAccountOpeStore m_mockAccountOpeStore;

	private float m_money;
	private List<String> m_stockSelectList; // 选股列表
	private List<CommissionOrder> m_commissionOrderList; // 模拟账户中  下单直接成交, 委托单一直未空
	private List<HoldStock> m_holdStockList;
	private List<DealOrder> m_dealOrderList;
}
