package stormstock.data;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;

public class TestDataEngineBase {
	private static void test_getDayKData()
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngineBase.getDayKData("300163", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DayKData cDayKData = retList.get(i);  
	            System.out.println(cDayKData.date + "," 
	            		+ cDayKData.open + "," + cDayKData.close);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
	private static void test_getDividendPayout()
	{
		List<DividendPayout> retList = new ArrayList<DividendPayout>();
		int ret = DataEngineBase.getDividendPayout("300163", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = retList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("getDividendPayout ERROR:" + ret);
		}
	}
	private static void test_getDayDetail()
	{
		List<DayDetailItem> retList = new ArrayList<DayDetailItem>();
		int ret = DataEngineBase.getDayDetail("300163", "2015-02-16", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DayDetailItem cDayDetailItem = retList.get(i);  
	            System.out.println(cDayDetailItem.time + "," 
	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
	        } 
		}
		else
		{
			System.out.println("getDayDetail ERROR:" + ret);
		}
	}
	private static void test_downloadStockDayk()
	{
		int retdownloadStockDayk = DataEngineBase.downloadStockDayk("300163");
		if(0 != retdownloadStockDayk)
		{
			System.out.println("downloadStockDayk ERROR:" + retdownloadStockDayk);
		}
	}
	private static void test_downloadStockDividendPayout()
	{
		int retdownloadStockDividendPayout = DataEngineBase.downloadStockDividendPayout("300163");
		if(0 != retdownloadStockDividendPayout)
		{
			System.out.println("downloadStockDividendPayout ERROR:" + retdownloadStockDividendPayout);
		}
	}
	private static void test_downloadStockDataDetail()
	{
		int retdownloadStockDataDetail = DataEngineBase.downloadStockDataDetail("300163", "2015-09-30");
		if(0 != retdownloadStockDataDetail)
		{
			System.out.println("downloadStockDataDetail ERROR:" + retdownloadStockDataDetail);	
		}
	}
	private static void test_updateStock()
	{
		int retupdateStock = DataEngineBase.updateStock("000024");
		if(retupdateStock < 0)
		{
			System.out.println("updateStock ERROR:" + retupdateStock);	
		}
		else
		{
			System.out.println("updateStock OK:" + retupdateStock);	
		}
	}
	
	public static void main(String[] args) {
//		test_getDayKData();
//		test_getDividendPayout();
//		test_getDayDetail();
//		test_downloadStockDayk();
//		test_downloadStockDividendPayout();
//		test_downloadStockDataDetail();
		test_updateStock();
	}
}
