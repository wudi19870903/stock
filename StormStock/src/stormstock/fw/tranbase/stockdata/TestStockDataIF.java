package stormstock.fw.tranbase.stockdata;

import java.util.List;

import stormstock.fw.base.BLog;

public class TestStockDataIF {
	
	public static void test_getAllStocks()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		
		List<String> stockIDList = cStockDataIF.getAllStockID();
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
	}
	
	public static void test_getLatestStockInfo()
	{
		StockDataIF cStockDataIF = new StockDataIF();
		for(int i=2; i<9; i++)
		{
			String stockID = String.format("00000%d", i);
			StockInfo cStockInfo = cStockDataIF.getLatestStockInfo(stockID);
			if(null!=cStockInfo)
			{
				BLog.output("TEST", "cStockInfo [%s][%s]\n",
						stockID, cStockInfo.name);
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
		
		List<StockDay> cStockDayShangZhengList = cStockDataIF.getHistoryData("999999");
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
		List<StockTime> detailData = cStockDataIF.getDayDetail("000001", "2016-01-04", "14:30:00");
		if(null != detailData)
		{
			for(int i=0; i< detailData.size(); i++)
			{
				StockTime cStockDayDetail = detailData.get(i);
				BLog.output("TEST", "cStockDayDetail %s price %.3f \n", 
						cStockDayDetail.time, cStockDayDetail.price);
			}
		}
	}
	
	public static void main(String[] args) {
		BLog.output("TEST", "TestStockDataProvider Begin\n");
		
		//test_getAllStocks();
		//test_getLatestStockInfo();
		test_getHistoryData();
		//test_getDayDetail();
		
		BLog.output("TEST", "TestStockDataProvider End\n");
	}
}
