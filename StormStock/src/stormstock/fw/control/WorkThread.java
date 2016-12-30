package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.event.Transaction;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockUtils;

public class WorkThread extends BThread {
	
	public WorkThread(Transaction.ControllerStartNotify cControllerStartNotify)
	{
		m_cControllerStartNotify = cControllerStartNotify;
	}
	
	@Override
	public void run() {
		if(m_cControllerStartNotify.getHistoryTestTran())
		{
			EntityHistoryTest cEntityHistoryTest = new EntityHistoryTest();
			String beginDate = m_cControllerStartNotify.getBeginDate();
			String endDate = m_cControllerStartNotify.getEndDate();
			BLog.output("CTRL", "WorkThread cEntityHistoryTest.run [%s -> %s]\n", beginDate, endDate);
			
			cEntityHistoryTest.run(beginDate, endDate);
		}
		else
		{
			EntityRealTime cEntityRealTime = new EntityRealTime();
			BLog.output("CTRL", "WorkThread cEntityRealTime.run\n");
			
			cEntityRealTime.run();
		}
	}

	private Transaction.ControllerStartNotify m_cControllerStartNotify;
}
