package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.event.Transaction;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockUtils;

public class CtrlWorkThread extends BThread {
	
	public CtrlWorkThread(Transaction.ControllerStartNotify cControllerStartNotify)
	{
		m_cControllerStartNotify = cControllerStartNotify;
	}
	
	@Override
	public void run() {
		if(m_cControllerStartNotify.getHistoryTestTran())
		{
			run_historytest();
		}
		else
		{
			BLog.output("TEST", "Controller onTranStartNotify Realtime transaction\n");
		}
	}
	
	void run_historytest()
	{
		String beginDate = m_cControllerStartNotify.getBeginDate();
		String endDate = m_cControllerStartNotify.getEndDate();
		BLog.output("TEST", "Controller onTranStartNotify history test transaction [%s -> %s]\n", beginDate, endDate);
		
		Stock cStockShangZheng = StockDataProvider.getStock("999999");
		int iB = StockUtils.indexDayKAfterDate(cStockShangZheng.historyData, beginDate);
		int iE = StockUtils.indexDayKBeforeDate(cStockShangZheng.historyData, endDate);
		
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cStockDayShangZheng = cStockShangZheng.historyData.get(i);  
			String curDateStr = cStockDayShangZheng.date;
			BLog.output("TEST", "%s\n",curDateStr);
			
			
        }
	
//		BEventSys.EventSender cSender = new BEventSys.EventSender();
//		cSender.Send("BEV_TRAN_ENGINEEXIT", Transaction.TranEngineExitNotify.newBuilder().build());
	}

	private Transaction.ControllerStartNotify m_cControllerStartNotify;
}
