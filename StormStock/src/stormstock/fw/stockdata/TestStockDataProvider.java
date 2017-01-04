package stormstock.fw.stockdata;

import java.util.List;

import stormstock.fw.base.BLog;

public class TestStockDataProvider {
	
	public static void test_getAllStocks()
	{
		List<String> stockIDList = StockDataProvider.getAllStockID();
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
		for(int i=2; i<9; i++)
		{
			String stockID = String.format("00000%d", i);
			StockInfo cStockInfo = StockDataProvider.getLatestStockInfo(stockID);
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
		List<StockDay> historyData = StockDataProvider.getHistoryData("000001", "2016-01-01",  "2016-12-31");
		for(int i=0; i< historyData.size(); i++)
		{
			StockDay cStockDay = historyData.get(i);
			BLog.output("TEST", "cStockDay %s open %.3f close %.3f\n", 
					cStockDay.date, cStockDay.open, cStockDay.close);
		}
	}
	
	public static void test_getDayDetail()
	{ 
		List<StockTime> detailData = StockDataProvider.getDayDetail("000001", "2016-01-04", "14:30:00");
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
		//test_getHistoryData();
		test_getDayDetail();
		
		BLog.output("TEST", "TestStockDataProvider End\n");
	}
}
