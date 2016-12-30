package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.event.Transaction;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockUtils;

public class EntityHistoryTest {
	void run(String beginDate, String endDate)
	{
		Stock cStockShangZheng = StockDataProvider.getStock("999999");
		int iB = StockUtils.indexDayKAfterDate(cStockShangZheng.historyData, beginDate);
		int iE = StockUtils.indexDayKBeforeDate(cStockShangZheng.historyData, endDate);
		
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cStockDayShangZheng = cStockShangZheng.historyData.get(i);  
			String curDateStr = cStockDayShangZheng.date;
			BLog.output("CTRL", "%s ----------------------------------->>> \n", curDateStr);
			String curTimeStgr = "00:00:00";
			
			// 6:00 更新历史数据通知
			// 等待更新完毕通知
			curTimeStgr = "06:00:00";
			//BLog.output("CTRL", "[%s] UpdateDataReq \n",curTimeStgr);
			
			// 8:00选股通知
			// 选股完毕通知
			
			// 9:30-11:30 1:00-3:00 每分钟 发送 买卖请求
			// 等待买卖请求处理完毕通知
        }
	
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_ENGINEEXIT", Transaction.TranEngineExitNotify.newBuilder().build());
	}
}
