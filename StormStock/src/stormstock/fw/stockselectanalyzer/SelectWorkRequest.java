package stormstock.fw.stockselectanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.event.StockSelectAnalysis;
import stormstock.fw.event.Transaction;
import stormstock.fw.tranbase.account.AccountAccessor;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.com.IStrategySelect;
import stormstock.fw.tranbase.com.IStrategySelect.SelectResult;
import stormstock.fw.tranbase.com.TranContext.Target;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataAccessor;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultLatestStockInfo;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockInfo;

public class SelectWorkRequest extends BQThreadRequest {
	
	/*
	 * SelectResultWrapper类，用于选股优先级排序
	 */
	static private class SelectResultWrapper {
		public SelectResultWrapper(){
			selectRes = new SelectResult();
		}
		// 优先级从大到小排序
		static public class SelectResultCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				SelectResultWrapper c1 = (SelectResultWrapper)object1;
				SelectResultWrapper c2 = (SelectResultWrapper)object2;
				int iCmp = Float.compare(c1.selectRes.fPriority, c2.selectRes.fPriority);
				if(iCmp > 0) 
					return -1;
				else if(iCmp < 0) 
					return 1;
				else
					return 0;
			}
		}
		public String stockId;
		public SelectResult selectRes;
	}
	
	public SelectWorkRequest(String date, String time, List<String> stockIDList)
	{
		m_date = date;
		m_time = time;
		m_stockIDList = stockIDList;
	}
	@Override
	public void doAction() 
	{
		BLog.output("SELECT", "SelectWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		StockDataIF stockDataIF = GlobalUserObj.getCurStockDataIF();
		
		IStrategySelect cIStrategySelect = GlobalUserObj.getCurrentStrategySelect();
		List<String> cTranStockIDSet = m_stockIDList;
		
		BLog.output("SELECT", "    TranStockIDSet count(%s)\n",cTranStockIDSet.size());
		
		// 回调给用户生成cSelectResultWrapperList后进行排序
		List<SelectResultWrapper> cSelectResultWrapperList = new ArrayList<SelectResultWrapper>();
		if(null!=cTranStockIDSet)
		{
			for(int i=0; i<cTranStockIDSet.size(); i++)
			{
				String stockID = cTranStockIDSet.get(i);
				SelectResultWrapper cSRW = new SelectResultWrapper();
				cSRW.stockId = stockID;
				
				// 构造当时股票数据
				ResultLatestStockInfo cResultLatestStockInfo = stockDataIF.getLatestStockInfo(stockID);
				ResultHistoryData cResultHistoryData = stockDataIF.getHistoryData(stockID, m_date);
				if(0 == cResultLatestStockInfo.error 
						&& 0 == cResultHistoryData.error
						&& cResultHistoryData.resultList.size()>0
						&& cResultHistoryData.resultList.get(cResultHistoryData.resultList.size()-1).date().compareTo(m_date) == 0)
				{
					Stock cStock = new Stock();
					cStock.setCurLatestStockInfo(cResultLatestStockInfo.stockInfo);
					cStock.setCurStockDayData(cResultHistoryData.resultList);
					StockDataAccessor cStockDataAccessor = stockDataIF.getStockDataAccessor(m_date, m_time);
					Target cTarget = new Target(cStock, null);
					TranContext ctx = new TranContext(m_date, m_time, 
							cTarget,  // 目标不包含 持股信息
							null,  // 不带账户访问器
							cStockDataAccessor); // 带当天的数据访问器
					
					
					// log
					BLog.output("SELECT", "    [%s %s] strategy_select stockID:%s (%s) close:%.2f \n", 
						ctx.date(), ctx.time(), 
						ctx.target().stock().getCurLatestStockInfo().ID ,
						ctx.target().stock().GetLastDate() ,
						ctx.target().stock().GetLastClosePrice());
					
					// 进行用户选股
					cIStrategySelect.strategy_select(ctx, cSRW.selectRes);
					
					// 如果选择后，把结果添加到cSelectResultWrapperList
					if(cSRW.selectRes.bSelect){
						cSelectResultWrapperList.add(cSRW);
					}
				}
				else
				// 不能成功做成股票数据，忽略选股操作
				{
					BLog.output("SELECT", "    Cannot Generate %s stock %s, ignore!\n", m_date, stockID);
				}
			}
			Collections.sort(cSelectResultWrapperList, new SelectResultWrapper.SelectResultCompare());
		}
		
		int iSelectCount = cSelectResultWrapperList.size();
		int iSelectMaxCount  = cIStrategySelect.strategy_select_max_count();
		int iAddCount = iSelectCount>iSelectMaxCount?iSelectMaxCount:iSelectCount;
		
		StockSelectAnalysis.StockSelectAnalysisCompleteNotify.Builder msg_builder = StockSelectAnalysis.StockSelectAnalysisCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		for(int i=0; i<iAddCount; i++)
		{
			msg_builder.addStockID(cSelectResultWrapperList.get(i).stockId);
		}
		StockSelectAnalysis.StockSelectAnalysisCompleteNotify msg = msg_builder.build();
		BLog.output("SELECT", "    Selected count(%s)\n",msg.getStockIDList().size());
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKSELECTANALYSISCOMPLETENOTIFY", msg);
	}

	private String m_date;
	private String m_time;
	private List<String> m_stockIDList;
}
