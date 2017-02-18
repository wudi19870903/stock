package stormstock.fw.stockclearanalyzer;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.event.StockClearAnalysis;
import stormstock.fw.event.Transaction;
import stormstock.fw.stockcreateanalyzer.StockTimeDataCache;
import stormstock.fw.tranbase.account.AccountAccessor;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.com.IStrategyClear;
import stormstock.fw.tranbase.com.IStrategyClear.ClearResult;
import stormstock.fw.tranbase.com.TranContext.Target;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataAccessor;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockInfo;
import stormstock.fw.tranbase.stockdata.StockTime;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultLatestStockInfo;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;

public class ClearWorkRequest extends BQThreadRequest {
	/*
	 * ClearResultWrapper类
	 */
	static private class ClearResultWrapper {
		public ClearResultWrapper(){
			clearRes = new ClearResult();
		}
		public String stockId;
		public float fPrice;
		public ClearResult clearRes;
	}
	
	public ClearWorkRequest(String date, String time, List<String> stockIDList)
	{
		m_date = date;
		m_time = time;
		m_stockIDList = stockIDList;
	}
	
	@Override
	public void doAction() {
		
		BLog.output("CLEAR", "ClearWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		StockDataIF stockDataIF = GlobalUserObj.getCurStockDataIF();
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		
		
		IStrategyClear cIStrategyClear = GlobalUserObj.getCurrentStrategyClear();
		List<String> stockIDHoldList = m_stockIDList;
		
		BLog.output("CLEAR", "    stockIDHoldList count(%s)\n", stockIDHoldList.size());
		
		List<ClearResultWrapper> cClearResultWrapperList = new ArrayList<ClearResultWrapper>();
		
		for(int i=0; i<stockIDHoldList.size(); i++)
		{
			String stockID = stockIDHoldList.get(i);
			
			// 构造当时股票数据(昨日日K，今日当前分时)
			
			ResultLatestStockInfo cResultLatestStockInfo = stockDataIF.getLatestStockInfo(stockID);
			String yesterday_date = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_date, -1);
			ResultHistoryData cResultHistoryData = stockDataIF.getHistoryData(stockID, yesterday_date);
			ResultStockTime cResultStockTime = stockDataIF.getStockTime(stockID, m_date, m_time);
			if(0 == cResultLatestStockInfo.error
					&& 0 == cResultHistoryData.error
					&& 0 == cResultStockTime.error)
			{
				// 做成当日股票数据
				StockTimeDataCache.addStockTime(stockID, m_date, cResultStockTime.stockTime());

				List<StockTime> cStockTimeList = StockTimeDataCache.getStockTimeList(stockID, m_date);
				StockDay curStockDay = new StockDay();
				curStockDay.set(m_date, cStockTimeList);
				cResultHistoryData.resultList.add(curStockDay);
				
				Stock cStock = new Stock();
				cStock.setCurLatestStockInfo(cResultLatestStockInfo.stockInfo);
				cStock.setCurStockDayData(cResultHistoryData.resultList);
				
				// 做成 ctx
				AccountAccessor cAccountAccessor = accIF.getAccountAccessor(m_date, m_time);
				StockDataAccessor cStockDataAccessor = stockDataIF.getStockDataAccessor(yesterday_date, m_time);
				Target cTarget = new Target(cStock, cAccountAccessor.getHoldStock(stockID));
				TranContext ctx = new TranContext(m_date, m_time, 
						cTarget,  // 目标对象带有 股票数据与持股信息
						cAccountAccessor, // ctx带有账户访问器
						cStockDataAccessor);// ctx带有昨日数据访问器（用户不能查看今天得其他k线）
				
				// 构造ClearResultWrapper
				ClearResultWrapper cClearResultWrapper = new ClearResultWrapper();
				cClearResultWrapper.stockId = stockID;
				cClearResultWrapper.fPrice = cResultStockTime.stockTime().price;
				
				List<StockTime> stockTimeList = ctx.target().stock().getLatestStockTimeList();
				if(stockTimeList.size()>0)
				{
					// log
					int iBegin = 0;
					int iEnd = stockTimeList.size()-1;
					int cnt = stockTimeList.size();
					
					BLog.output("CLEAR", "    [%s %s] strategy_clear stockID:%s (%s) (%s %.2f)...(%s %.2f) cnt(%d)\n", 
							ctx.date(), ctx.time(), 
							ctx.target().stock().getCurLatestStockInfo().ID,ctx.target().stock().GetLastDate(),
							stockTimeList.get(iBegin).time, stockTimeList.get(iBegin).price,
							stockTimeList.get(iEnd).time, stockTimeList.get(iEnd).price, 
							cnt);
					
					// 回调给用户
					cIStrategyClear.strategy_clear(ctx, cClearResultWrapper.clearRes);
					
					if(cClearResultWrapper.clearRes.bClear)
					{
						cClearResultWrapperList.add(cClearResultWrapper);
					}
				}

			}
			else
			{
				BLog.output("CLEAR", "    Cannot Generate %s %s stock %s, ignore!\n", m_date, m_time, stockID);
			}
		}
		
		// 根据清仓策略，做成清仓项
		StockClearAnalysis.StockClearAnalysisCompleteNotify.Builder msg_builder = StockClearAnalysis.StockClearAnalysisCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		for(int i = 0; i< cClearResultWrapperList.size(); i++)
		{
			ClearResultWrapper cClearResultWrapper = cClearResultWrapperList.get(i);
			StockClearAnalysis.StockClearAnalysisCompleteNotify.ClearItem.Builder cItemBuild = msg_builder.addItemBuilder();
			cItemBuild.setStockID(cClearResultWrapper.stockId);
			cItemBuild.setPrice(cClearResultWrapper.fPrice);
			// 调用账户获取持有量
			HoldStock cHoldStock = accIF.getHoldStock(null, null, cClearResultWrapper.stockId);
			cItemBuild.setAmount(cHoldStock.availableAmount);
		}
		StockClearAnalysis.StockClearAnalysisCompleteNotify msg = msg_builder.build();
		
		BLog.output("CLEAR", "    stockIDClearList count(%d)\n", msg.getItemList().size());
		for(int i=0; i< msg.getItemList().size(); i++)
		{
			String stockID = msg.getItemList().get(i).getStockID();
			float price = msg.getItemList().get(i).getPrice();
			int amount = msg.getItemList().get(i).getAmount();
			BLog.output("CLEAR", "        -Stock(%s) price(%.2f) amount(%d)\n", stockID,price,amount);
		}
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCLEARANALYSISCOMPLETENOTIFY", msg);
		
	}

	private String m_date;
	private String m_time;
	private List<String> m_stockIDList;
}
