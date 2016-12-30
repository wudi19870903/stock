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
			
			// 6:00 ������ʷ����֪ͨ
			// �ȴ��������֪ͨ
			curTimeStgr = "06:00:00";
			//BLog.output("CTRL", "[%s] UpdateDataReq \n",curTimeStgr);
			
			// 8:00ѡ��֪ͨ
			// ѡ�����֪ͨ
			
			// 9:30-11:30 1:00-3:00 ÿ���� ���� ��������
			// �ȴ��������������֪ͨ
        }
	
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_ENGINEEXIT", Transaction.TranEngineExitNotify.newBuilder().build());
	}
}
