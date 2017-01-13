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
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.com.IStrategySelect;
import stormstock.fw.tranbase.com.IStrategySelect.SelectResult;
import stormstock.fw.tranbase.com.StockContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockInfo;

public class SelectWorkRequest extends BQThreadRequest {
	
	/*
	 * SelectResultWrapper�࣬����ѡ�����ȼ�����
	 */
	static private class SelectResultWrapper {
		public SelectResultWrapper(){
			selectRes = new SelectResult();
		}
		// ���ȼ��Ӵ�С����
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
		
		// �ص����û�����cSelectResultWrapperList���������
		List<SelectResultWrapper> cSelectResultWrapperList = new ArrayList<SelectResultWrapper>();
		if(null!=cTranStockIDSet)
		{
			for(int i=0; i<cTranStockIDSet.size(); i++)
			{
				String stockID = cTranStockIDSet.get(i);
				SelectResultWrapper cSRW = new SelectResultWrapper();
				cSRW.stockId = stockID;
				
				// ���潻�׹�Ʊ��������ʷ����
				if(!StockDataIF.isCachedStockDayData(stockID))
				{
					List<StockDay> cStockDayList = StockDataIF.getHistoryData(stockID);
					StockDataIF.cacheHistoryData(stockID, cStockDayList);
				}
				
				// ���쵱ʱ��Ʊ����
				Stock cStock = new Stock();
				List<StockDay> cStockDayList = StockDataIF.getHistoryData(stockID, m_date);
				StockInfo cStockInfo = StockDataIF.getLatestStockInfo(stockID);
				cStock.setCurStockDayData(cStockDayList);
				cStock.setCurLatestStockInfo(cStockInfo);
				
				StockContext ctx = new StockContext();
				ctx.setDate(m_date);
				ctx.setTime(m_time);
				ctx.setStock(cStock);
				
				// �����û�ѡ��
				cIStrategySelect.strategy_select(ctx, cSRW.selectRes);
				
				// ���ѡ��󣬰ѽ�����ӵ�cSelectResultWrapperList
				if(cSRW.selectRes.bSelect){
					cSelectResultWrapperList.add(cSRW);
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