package stormstock.fw.acc;

import java.util.ArrayList;
import java.util.List;

import stormstock.analysis.ANLLog;
import stormstock.fw.acc.IAccountOpe.HoldStock;
import stormstock.fw.objmgr.GlobalTranTime;
import stormstock.fw.stockcreate.StockTimeDataCache;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockTime;

public class MockAccountOpe extends IAccountOpe {
	
	public MockAccountOpe(float money, float transactionCostsRatio)
	{
		super();
		m_money = money;
		m_transactionCostsRatio = transactionCostsRatio;
		m_buyOrderList = new ArrayList<StockTranOrder>();
		m_holdStockList = new ArrayList<HoldStock>();
		m_sellOrderList = new ArrayList<StockTranOrder>();
	}
	
	@Override
	public boolean newDayInit() 
	{ 
		// 新一天时，所有持股均可卖
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.totalCanSell = cHoldStock.totalAmount;
		}
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
			cHoldStock.buyPrice = (oriPrice*oriAmount - price*realSellAmount)/cHoldStock.totalAmount;
			m_money = m_money + price*amount;
			
			if(cHoldStock.totalAmount == 0)
			{
				m_money = m_money - cHoldStock.transactionCosts;
				m_holdStockList.remove(cHoldStock);
			}
			
			return realSellAmount;
		}
	
		return 0;
	}

	@Override
	public float getTotalAssets() {
		String curTranDate = GlobalTranTime.getTranDate();
		String curTranTime = GlobalTranTime.getTranTime();
		
		float all_marketval = 0.0f;
		List<HoldStock> cHoldStockList = getHoldStockList();
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			float curTranPrice = 0.0f;

			StockTime cStockTime = new StockTime();
			boolean bGetStockTime = StockDataProvider.getStockTime(cHoldStock.id, curTranDate, curTranTime, cStockTime);
			if(bGetStockTime)
			{
				curTranPrice = cStockTime.price;
			}
			else
			{
				List<StockDay> cStockDayList = StockDataProvider.getHistoryData(cHoldStock.id, curTranDate);
				curTranPrice = cStockDayList.get(cStockDayList.size()-1).close;
			}
					
			all_marketval = all_marketval + curTranPrice*cHoldStock.totalAmount;
		}
		float all_asset = all_marketval + getAvailableMoney();
		return all_asset;
	}

	@Override
	public float getAvailableMoney() {
		return m_money;
	}

	@Override
	public List<StockTranOrder> getBuyOrderList() {
		return m_buyOrderList;
	}
	
	@Override
	public List<HoldStock> getHoldStockList() {
		return m_holdStockList;
	}
	
	@Override
	public List<StockTranOrder> getSellOrderList() {
		return m_sellOrderList;
	}
	
	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private float m_money;
	private float m_transactionCostsRatio;
	private List<StockTranOrder> m_buyOrderList; // 模拟账户中  下单直接成交，此list一直是空
	private List<HoldStock> m_holdStockList;
	private List<StockTranOrder> m_sellOrderList; // 模拟账户中  下单直接成交，此list一直是空
}
