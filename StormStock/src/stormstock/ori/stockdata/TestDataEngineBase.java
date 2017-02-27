package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataEngineBase.ResultUpdateStock;
import stormstock.ori.stockdata.DataEngineBase.ResultUpdatedStocksDate;
import stormstock.ori.stockdata.DataWebStockDayDetail.ResultDayDetail;
import stormstock.ori.stockdata.CommonDef.*;

import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.DataWebStockDividendPayout.ResultDividendPayout;


public class TestDataEngineBase {
	private static void test_getDayKData()
	{
		ResultDayKData cResultDayKData = DataEngineBase.getDayKData("000004");
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
	private static void test_getDividendPayout()
	{
		ResultDividendPayout cResultDividendPayout = DataEngineBase.getDividendPayout("300556");
		if(0 == cResultDividendPayout.error)
		{
			for(int i = 0; i < cResultDividendPayout.resultList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("getDividendPayout ERROR:" + cResultDividendPayout.error);
		}
	}
	private static void test_getDayDetail()
	{
		ResultDayDetail cResultDayDetail = DataEngineBase.getDayDetail("000004", "2016-03-23");
		if(0 == cResultDayDetail.error)
		{
			for(int i = 0; i < cResultDayDetail.resultList.size(); i++)  
	        {  
				DayDetailItem cDayDetailItem = cResultDayDetail.resultList.get(i);  
	            System.out.println(cDayDetailItem.time + "," 
	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
	        } 
		}
		else
		{
			System.out.println("getDayDetail ERROR:" + cResultDayDetail.error);
		}
	}
	private static void test_downloadStockDayk()
	{
		int retdownloadStockDayk = DataEngineBase.downloadStockDayk("300163");
		if(0 != retdownloadStockDayk)
		{
			System.out.println("downloadStockDayk ERROR:" + retdownloadStockDayk);
		}
		else
		{
			System.out.println("downloadStockDayk OK");
		}
	}
	private static void test_downloadStockDividendPayout()
	{
		int retdownloadStockDividendPayout = DataEngineBase.downloadStockDividendPayout("300163");
		if(0 != retdownloadStockDividendPayout)
		{
			System.out.println("downloadStockDividendPayout ERROR:" + retdownloadStockDividendPayout);
		}
		else
		{
			System.out.println("downloadStockDividendPayout OK");
		}
	}
	private static void test_downloadStockDataDetail()
	{
		int retdownloadStockDataDetail = DataEngineBase.downloadStockDataDetail("300163", "2015-09-30");
		if(0 != retdownloadStockDataDetail)
		{
			System.out.println("downloadStockDataDetail ERROR:" + retdownloadStockDataDetail);	
		}
		else
		{
			System.out.println("downloadStockDataDetail OK");
		}
	}
	private static void test_updateStock()
	{
		ResultUpdateStock cResultUpdateStock = DataEngineBase.updateStock("000157"); 
		if(cResultUpdateStock.error < 0)
		{
			System.out.println("updateStock ERROR:" + cResultUpdateStock.error);	
		}
		else
		{
			System.out.println("updateStock OK:" + cResultUpdateStock.updateCnt);	
		}
	}
	
	private static void test_getUpdatedStocksDate()
	{
		ResultUpdatedStocksDate cResultUpdatedStocksDate = DataEngineBase.getUpdatedStocksDate();
		System.out.println("updatedDate:" + cResultUpdatedStocksDate.date);	
	}
	
	public static void main(String[] args) {
//		test_getDayKData();
//		test_getDividendPayout();
//		test_getDayDetail();
//		test_downloadStockDayk();
//		test_downloadStockDividendPayout();
//		test_downloadStockDataDetail();
		test_updateStock();
//		test_getUpdatedStocksDate();
	}
}
