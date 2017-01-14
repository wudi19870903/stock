package stormstock.fw.stockcreateanalyzer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.event.StockCreateAnalysis;
import stormstock.fw.event.Transaction;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.com.IStrategyCreate;
import stormstock.fw.tranbase.com.IStrategyCreate.CreateResult;
import stormstock.fw.tranbase.com.StockContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockInfo;
import stormstock.fw.tranbase.stockdata.StockTime;

public class CreateWorkRequest extends BQThreadRequest {
	
	/*
	 * CreateResultWrapper类
	 */
	static private class CreateResultWrapper {
		public CreateResultWrapper(){
			createRes = new CreateResult();
		}
		public String stockId;
		public float fPrice;
		public CreateResult createRes;
	}
	
	public CreateWorkRequest(String date, String time, List<String> stockIDList)
	{
		m_date = date;
		m_time = time;
		m_stockIDList = stockIDList;
	}
	
	@Override
	public void doAction() {
		BLog.output("CREATE", "CreateWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		
		IStrategyCreate cIStrategyCreate = GlobalUserObj.getCurrentStrategyCreate();
		List<String> stockIDSelectList = m_stockIDList;
		
		BLog.output("CREATE", "    stockIDSelectList count(%s)\n", stockIDSelectList.size());
		
		List<CreateResultWrapper> cCreateResultWrapperList = new ArrayList<CreateResultWrapper>();
		
		for(int i=0; i<stockIDSelectList.size(); i++)
		{
			String stockID = stockIDSelectList.get(i);
			
			// 构造当时股票数据(昨日日K，今日当前分时)
			
			StockInfo cStockInfo = StockDataIF.getLatestStockInfo(stockID);
			
			String yesterday_date = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_date, -1);
			List<StockDay> cStockDayList = StockDataIF.getHistoryData(stockID, yesterday_date);
			
			StockTime cStockTime = new StockTime();
			boolean bGetStockTime = StockDataIF.getStockTime(stockID, m_date, m_time, cStockTime);
			if(bGetStockTime)
			{
				StockTimeDataCache.addStockTime(stockID, m_date, cStockTime);
			}
			List<StockTime> cStockTimeList = StockTimeDataCache.getStockTimeList(stockID, m_date);
			StockDay curStockDay = new StockDay();
			curStockDay.set(m_date, cStockTimeList);
			
			cStockDayList.add(curStockDay);
			
			BLog.output("CREATE", "        -Stock:%s cStockTimeList size(%d)\n", stockID, cStockTimeList.size());
			
			Stock cStock = new Stock();
			cStock.setCurLatestStockInfo(cStockInfo);
			cStock.setCurStockDayData(cStockDayList);
			
			StockContext ctx = new StockContext();
			ctx.setDate(m_date);
			ctx.setTime(m_time);
			ctx.setStock(cStock);
			ctx.setAccountAccessor(accIF.getAccountAccessor(m_date, m_time));
			
			if(bGetStockTime) // 只有获取当前价格成功时才回调给用户
			{
				CreateResultWrapper cCreateResultWrapper = new CreateResultWrapper();
				cCreateResultWrapper.stockId = stockID;
				cCreateResultWrapper.fPrice = cStockTime.price;
				cIStrategyCreate.strategy_create(ctx, cCreateResultWrapper.createRes);
				if(cCreateResultWrapper.createRes.bCreate)
				{
					cCreateResultWrapperList.add(cCreateResultWrapper);
				}
			}
		}
			
		int create_max_count = cIStrategyCreate.strategy_create_max_count();
		int alreadyCount = accIF.getStockHoldList(null, null).size() + accIF.getBuyCommissionOrderList().size();
		int buyStockCount = create_max_count - alreadyCount;
		buyStockCount = Math.min(buyStockCount,cCreateResultWrapperList.size());
		
		StockCreateAnalysis.StockCreateAnalysisCompleteNotify.Builder msg_builder = StockCreateAnalysis.StockCreateAnalysisCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		for(int i = 0; i< buyStockCount; i++)
		{
			CreateResultWrapper cCreateResultWrapper = cCreateResultWrapperList.get(i);
			StockCreateAnalysis.StockCreateAnalysisCompleteNotify.CreateItem.Builder cItemBuild = msg_builder.addItemBuilder();
			cItemBuild.setStockID(cCreateResultWrapper.stockId);
			cItemBuild.setPrice(cCreateResultWrapper.fPrice);
			
			// 买入量
			float totalAssets = accIF.getTotalAssets(m_date, m_time);
			float fMaxPositionRatio = cCreateResultWrapper.createRes.fMaxPositionRatio; 
			float fMaxPositionMoney = totalAssets*fMaxPositionRatio; // 最大买入仓位钱
			float fMaxMoney = cCreateResultWrapper.createRes.fMaxMoney; // 最大买入钱
			float buyMoney = Math.min(fMaxMoney, fMaxPositionMoney);
			
			int amount = (int)(buyMoney/cCreateResultWrapper.fPrice);
			amount = amount/100*100; // 买入整手化
			cItemBuild.setAmount(amount);
		}
		StockCreateAnalysis.StockCreateAnalysisCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		
		BLog.output("CREATE", "    stockIDCreateList count(%d)\n", msg.getItemList().size());
		for(int i=0; i< msg.getItemList().size(); i++)
		{
			String stockID = msg.getItemList().get(i).getStockID();
			float price = msg.getItemList().get(i).getPrice();
			int amount = msg.getItemList().get(i).getAmount();
			BLog.output("CREATE", "        -Stock(%s) price(%.2f) amount(%d)\n", stockID,price,amount);
		}
		
		cSender.Send("BEV_TRAN_STOCKCREATEANALYSISCOMPLETENOTIFY", msg);
		
	}
	
	private String m_date;
	private String m_time;
	private List<String> m_stockIDList;
}
