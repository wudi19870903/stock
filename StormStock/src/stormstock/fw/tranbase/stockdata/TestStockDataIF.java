package stormstock.fw.tranbase.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BImageCurve;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultLatestStockInfo;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;

public class TestStockDataIF {
	
	public static void test_updateAllLocalStocks()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		String curDate = "2016-12-01";
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
		
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData("000004");
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
		BImageCurve cBImageCurve = new BImageCurve(1600,900,"test_DayDetail.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		
		StockDataIF cStockDataIF = new StockDataIF();
		ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail("000985", "2016-03-09", "09:30:00", "15:00:00");
		if(0 == cResultDayDetail.error)
		{
			for(int i=0; i< cResultDayDetail.resultList.size(); i++)
			{
				StockTime cStockDayDetail = cResultDayDetail.resultList.get(i);
				PoiList.add(new CurvePoint(i,cStockDayDetail.price));
				BLog.output("TEST", "%s %.2f\n", cStockDayDetail.time, cStockDayDetail.price);
			}
		}
		
		cBImageCurve.addLogicCurveSameRatio(PoiList, 1);
		cBImageCurve.GenerateImage();
	}
	
	public static void test_getStockTime()
	{
		boolean bTestSingle = true;
		boolean bTest9to15 = true;
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		if(bTestSingle)
		{
			String testDate = BUtilsDateTime.GetCurDateStr();
			String testTime = BUtilsDateTime.GetCurTimeStr();

			
			ResultStockTime cResultStockTime = cStockDataIF.getStockTime("999999", testDate, testTime);
		
			if(0 == cResultStockTime.error)
			{
				BLog.output("TEST", "[%s %s] %.2f (%s %s)\n", testDate, testTime, 
						cResultStockTime.price, cResultStockTime.date, cResultStockTime.time);
			}
			else
			{
				BLog.output("TEST", "[%s %s] error %d\n", testDate, testTime, cResultStockTime.error);
			}
		}
		
		if(bTest9to15)
		{
			String testDate = "2016-03-09";
			String testTime = "09:00:00";
			while(true)
			{
				ResultStockTime cResultStockTime = cStockDataIF.getStockTime("000985", testDate, testTime);
				
				if(0 == cResultStockTime.error)
				{
					BLog.output("TEST", "[%s %s] %.2f (%s %s)\n", testDate, testTime, 
							cResultStockTime.price, cResultStockTime.date, cResultStockTime.time);
				}
				
				if(testTime.compareTo("15:00:00") >= 0)
				{
					break;
				}
			
				testTime = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(testTime, 5);
			}
		}

	}
	
	
	public static void main(String[] args) {
		BLog.start();
		BLog.output("TEST", "TestStockDataProvider Begin\n");
		BLog.config_setTag("STOCKDATA", true);
		
		//test_updateAllLocalStocks();
		//test_getAllStocks();
		//test_getLatestStockInfo();
		//test_getHistoryData();
		//test_getDayDetail();
		test_getStockTime();
		
		BLog.output("TEST", "TestStockDataProvider End\n");
		BLog.stop();
	}
}
