package stormstock.fw.stockselect;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.GlobalStockObj;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.tran.IStrategySelect;
import stormstock.fw.tran.IStrategySelect.SelectResult;
import stormstock.fw.tran.StockContext;

public class SelectWorkRequest extends BQThreadRequest {
	
	public SelectWorkRequest(String date, String time)
	{
		m_date = date;
		m_time = time;
	}
	@Override
	public void doAction() {
		BLog.output("SELECT", "WorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		List<String> selectList = new ArrayList<String>();
		
		{
			IStrategySelect cIStrategySelect = GlobalUserObj.getCurrentStrategySelect();
			List<String> cTranStockIDSet = GlobalStockObj.getTranStockIDSet();
			if(null!=cTranStockIDSet && null!=cIStrategySelect)
			{
				for(int i=0; i<cTranStockIDSet.size(); i++)
				{
					String stockID = cTranStockIDSet.get(i);
					
					// 缓存交易股票的所有历史数据
					if(!StockDataProvider.isCachedStockDayData(stockID))
					{
						List<StockDay> cStockDayList = StockDataProvider.getHistoryData(stockID);
						StockDataProvider.cacheHistoryData(stockID, cStockDayList);
					}
					
					// 进行选股
					
					List<StockDay> cStockDayList = StockDataProvider.getHistoryData(stockID, m_date);
					StockInfo cStockInfo = StockDataProvider.getLatestStockInfo(stockID);
					
					
					
					StockContext ctx = new StockContext();
					ctx.setDate(m_date);
					ctx.setTime(m_time);
					ctx.setCurStockDayData(cStockDayList);
					ctx.setCurStockInfo(cStockInfo);
					
					SelectResult selectRes = new SelectResult();
					
					cIStrategySelect.strategy_select(ctx, selectRes);
					
					if(selectRes.bSelect == true)
					{
						selectList.add(stockID);
						BLog.output("TEST", "selectList %d\n", stockID);
					}
				}
			}
		}
		
		
		Transaction.SelectStockCompleteNotify.Builder msg_builder = Transaction.SelectStockCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		Transaction.SelectStockCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_SELECTSTOCKCOMPLETENOTIFY", msg);
	}

	private String m_date;
	private String m_time;
}
