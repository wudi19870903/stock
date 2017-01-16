package stormstock.fw.tranbase.stockdata;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultLatestStockInfo;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;

public class TestStockDataIF {
	
	public static void test_updateAllLocalStocks()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		String curDate = "2016-01-01";
		while(true)
		{
			cStockDataIF.updateAllLocalStocks(curDate);
			BLog.output("TEST", "cStockDataIF.updateAllLocalStocks %s\n", curDate);
			
			
			if(curDate.compareTo("2016-06-01") >= 0)
			{
				break;
			}
			curDate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(curDate, 1);
		}
	}
	
	public static void test_getAllStocks()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		String curDate = "2016-01-01";
		while(true)
		{
			ResultAllStockID cResultAllStockID = cStockDataIF.getAllStockID();
			List<String> stockIDList = cResultAllStockID.resultList;
			BLog.output("TEST", "stock count: %d\n", stockIDList.size());
			for(int i=0; i<stockIDList.size(); i++)
			{
				String stockID = stockIDList.get(i);
				BLog.output("TEST", "stockID: %s\n", stockID);
				if(i>10)
				{
					break;
				}
			}
			
			if(curDate.compareTo("2016-06-01") >= 0)
			{
				break;
			}
			curDate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(curDate, 1);
		}
	}
	
	public static void test_getLatestStockInfo()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		for(int i=2; i<9; i++)
		{
			String stockID = String.format("00000%d", i);
			ResultLatestStockInfo cResultLatestStockInfo = cStockDataIF.getLatestStockInfo(stockID);
			if(0 == cResultLatestStockInfo.error)
			{
				BLog.output("TEST", "cStockInfo [%s][%s]\n",
						stockID, cResultLatestStockInfo.stockInfo.name);
			}
			else
			{
				BLog.output("TEST", "cStockInfo [%s] cStockInfo = null\n",
						stockID);
			}
		}
	}
	
	public static void test_getHistoryData()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		
//		List<StockDay> historyData = StockDataIF.getHistoryData("000001", "2016-01-01",  "2016-12-31");
//		for(int i=0; i< historyData.size(); i++)
//		{
//			StockDay cStockDay = historyData.get(i);
//			BLog.output("TEST", "cStockDay %s open %.3f close %.3f\n", 
//					cStockDay.date, cStockDay.open, cStockDay.close);
//		}
		
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData("999999");
		List<StockDay> cStockDayShangZhengList = cResultHistoryData.resultList;
		BLog.output("TEST", "cStockDayShangZhengList(%d)\n", 
				cStockDayShangZhengList.size());
		for(int i=cStockDayShangZhengList.size()-1; i > cStockDayShangZhengList.size()-10 ; i--)
		{
			StockDay cStockDay = cStockDayShangZhengList.get(i);
			BLog.output("TEST", "cStockDay %s open %.3f close %.3f\n", 
					cStockDay.date(), cStockDay.open(), cStockDay.close());
		}
	}
	
	public static void test_getDayDetail()
	{ 
		StockDataIF cStockDataIF = new StockDataIF();
		String curTime = "09:30:00";
		while(true)
		{
			ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail("000001", "2016-07-27", "09:30:00", curTime);
			List<StockTime> detailData = cResultDayDetail.resultList;
			
			String teststr ="";
			if(null != detailData)
			{
				for(int i=0; i< detailData.size(); i++)
				{
					StockTime cStockDayDetail = detailData.get(i);
					teststr = teststr + String.format("%.2f(%s) ", cStockDayDetail.price,cStockDayDetail.time);
				}
			}
			
			BLog.output("TEST", "[->%s] %s \n", 
					curTime, teststr);
			
			
			if(curTime.compareTo("11:30:00") >= 0)
			{
				break;
			}
			curTime = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTime, 1);
		}
	}
	
	public static void test_getStockTime()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		String curTime = "09:00:00";
		while(true)
		{

			ResultStockTime cResultStockTime = cStockDataIF.getStockTime("000001", "2016-07-27", curTime);
			
			if(0 == cResultStockTime.error)
			{
				BLog.output("TEST", "[%s] %.2f\n", cResultStockTime.stockTime.time, cResultStockTime.stockTime.price);
			}
			
			if(curTime.compareTo("15:00:00") >= 0)
			{
				break;
			}
			curTime = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTime, 5);
		}
	}
	
	
	public static void main(String[] args) {
		BLog.output("TEST", "TestStockDataProvider Begin\n");
		BLog.config_setTag("STOCKDATA", true);
		
		//test_updateAllLocalStocks();
		// test_getAllStocks();
		//test_getLatestStockInfo();
		// test_getHistoryData();
		//test_getDayDetail();
		//test_getStockTime();
		
		BLog.output("TEST", "TestStockDataProvider End\n");
	}
}
