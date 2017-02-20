package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class RunSimpleStockTimeListTest {
	public static class ResultCheckStockTimeList
	{
		public ResultCheckStockTimeList()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public float data;
	}
	public static ResultCheckStockTimeList check(List<StockTime> list, int i)
	{
		ResultCheckStockTimeList cResultCheck = new ResultCheckStockTimeList();
		
		return cResultCheck;
	}
	
	public static void main(String[] args) {
		
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		String stockID = "300163";
		String date = "2016-12-13";
		ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, date, "09:30:00", "15:00:00");
		List<StockTime> list = cResultDayDetail.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		
		s_StockTimeListCurve.setCurve(list);
		for(int i = 0; i < list.size(); i++)  
        {  
			StockTime cCurStockTime = list.get(i);
			
			ResultCheckStockTimeList cResultCheck = check(list, i);

			if(cResultCheck.bCheck)
			{
				BLog.output("TEST", "date:%s  %.3f\n", 
						cCurStockTime.time, cResultCheck.data);
			}
        } 
		
		s_StockTimeListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("RunSimpleStockTimeListTest.jpg");
}
