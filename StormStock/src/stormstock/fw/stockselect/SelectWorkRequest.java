package stormstock.fw.stockselect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.tran.strategy.IStrategySelect;
import stormstock.fw.tran.strategy.IStrategySelect.SelectResult;
import stormstock.fw.tran.strategy.StockContext;

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
				
				// 缓存交易股票的所有历史数据
				if(!StockDataProvider.isCachedStockDayData(stockID))
				{
					List<StockDay> cStockDayList = StockDataProvider.getHistoryData(stockID);
					StockDataProvider.cacheHistoryData(stockID, cStockDayList);
				}
				
				// 构造当时股票数据
				Stock cStock = new Stock();
				List<StockDay> cStockDayList = StockDataProvider.getHistoryData(stockID, m_date);
				StockInfo cStockInfo = StockDataProvider.getLatestStockInfo(stockID);
				StockContext ctx = new StockContext();
				cStock.setDate(m_date);
				cStock.setTime(m_time);
				cStock.setCurStockDayData(cStockDayList);
				cStock.setCurLatestStockInfo(cStockInfo);
				ctx.setCurStock(cStock);
				
				// 进行用户选股
				cIStrategySelect.strategy_select(ctx, cSRW.selectRes);
				
				// 如果选择后，把结果添加到cSelectResultWrapperList
				if(cSRW.selectRes.bSelect){
					cSelectResultWrapperList.add(cSRW);
				}
			}
			Collections.sort(cSelectResultWrapperList, new SelectResultWrapper.SelectResultCompare());
		}
		
		int iSelectCount = cSelectResultWrapperList.size();
		int iSelectMaxCount  = cIStrategySelect.strategy_select_max_count();
		int iAddCount = iSelectCount>iSelectMaxCount?iSelectMaxCount:iSelectCount;
		
		Transaction.SelectStockCompleteNotify.Builder msg_builder = Transaction.SelectStockCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		for(int i=0; i<iAddCount; i++)
		{
			msg_builder.addSelectedID(cSelectResultWrapperList.get(i).stockId);
		}
		Transaction.SelectStockCompleteNotify msg = msg_builder.build();
		BLog.output("SELECT", "    Selected count(%s)\n",msg.getSelectedIDList().size());
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_SELECTSTOCKCOMPLETENOTIFY", msg);
	}

	private String m_date;
	private String m_time;
	private List<String> m_stockIDList;
}
