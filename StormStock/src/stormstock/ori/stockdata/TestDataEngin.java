package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.CommonDef.StockSimpleItem;
import stormstock.ori.stockdata.DataEngine.ExKData;
import stormstock.ori.stockdata.DataEngine.ResultMinKDataOneDay;
import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList;
import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.CommonDef.*;

public class TestDataEngin {
	private static void test_getDayKDataQianFuQuan()
	{
		ResultDayKData cResultDayKData = DataEngine.getDayKDataQianFuQuan("300222");
		if(0 == cResultDayKData.error)
		{
			for(int i = 0; i < cResultDayKData.resultList.size(); i++)  
	        {  
				DayKData cDayKData = cResultDayKData.resultList.get(i);  
	            System.out.println(cDayKData.date + "," 
	            		+ cDayKData.open + "," + cDayKData.close);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultDayKData.error);
		}
	}
	private static void test_get5MinKDataOneDay()
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = DataEngine.get5MinKDataOneDay("000920", "2016-08-25");
		//int ret = get5MinKDataOneDay("600316", "2010-06-28", retList);
		if(0 == cResultMinKDataOneDay.error)
		{
			for(int i = 0; i < cResultMinKDataOneDay.exKDataList.size(); i++)  
	        {  
				ExKData cExKData = cResultMinKDataOneDay.exKDataList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultMinKDataOneDay.error);
		}
	}
	private static void test_get1MinKDataOneDay()
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = DataEngine.get1MinKDataOneDay("000004", "2016-10-12");
		//int ret = get5MinKDataOneDay("600316", "2010-06-28", retList);
		if(0 == cResultMinKDataOneDay.error)
		{
			for(int i = 0; i < cResultMinKDataOneDay.exKDataList.size(); i++)  
	        {  
				ExKData cExKData = cResultMinKDataOneDay.exKDataList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultMinKDataOneDay.error);
		}
	}
	private static void test_getLocalRandomStock()
	{
		List<StockSimpleItem> retList = DataEngine.getLocalRandomStock(3);
		for(int i = 0; i < retList.size(); i++)  
        {  
			StockSimpleItem cStockSimpleItem = retList.get(i);  
            System.out.println("cStockSimpleItem: id:" + cStockSimpleItem.id);  
        } 
	}
	
	private static void test_checkStockData()
	{
		//----------------------
		
//		int ErrorCnt_1 = 0;
//		int ErrorCnt_2 = 0;
//		int ErrorCnt_3 = 0;
//		ResultAllStockList cResultAllStockList = DataEngine.getLocalAllStock();
//		if(0 == cResultAllStockList.error)
//		{
//			for(int i=0; i<cResultAllStockList.resultList.size();i++)
//			{
//				StockSimpleItem cStockSimpleItem = cResultAllStockList.resultList.get(i);
//				int err = DataEngine.checkStockData(cStockSimpleItem.id);
//				if(0 == err)
//				{
//					//System.out.println("stockID:" + cStockSimpleItem.id + " checkStockData OK\n");	
//				}
//				else
//				{
//					if(-1 == err)
//						ErrorCnt_1++;
//					if(-2 == err)
//						ErrorCnt_2++;
//					if(-3 == err)
//					{
//						ErrorCnt_3++;
//						System.out.println("stockID:" + cStockSimpleItem.id + " checkStockData NG err(" + err + ")");	
//					}
//						
//					
//				}
//			}
//		}
//		System.out.println("ErrorCnt_1:" + ErrorCnt_1 );	
//		System.out.println("ErrorCnt_2:" + ErrorCnt_2 );	
//		System.out.println("ErrorCnt_3:" + ErrorCnt_3 );	
		
		//----------------------
		String stockID = "600030";
		if(0 == DataEngine.checkStockData(stockID))
		{
			System.out.println("stockID:" + stockID + " checkStockData OK\n");	
		}
		else
		{
			System.out.println("stockID:" + stockID + " checkStockData NG\n");	
//			if(0 == DataEngineBase.rmStockDataDir(stockID))
//			{
//				System.out.println("stockID:" + stockID + " rmStockDataDir OK\n");	
//			}
//			else
//			{
//				System.out.println("stockID:" + stockID + " rmStockDataDir NG\n");	
//			}
		}
	}
	
	public static void main(String[] args) {
		//test_getDayKDataQianFuQuan();
		//test_get5MinKDataOneDay();
		//test_get1MinKDataOneDay();
		//test_getLocalRandomStock();
		test_checkStockData();
	}
}
