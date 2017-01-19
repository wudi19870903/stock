package stormstock.ori.stockdata;

import stormstock.ori.stockdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.ResultRealTimeInfoMore;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
		
		String stockID = "300550";
		
		{
			System.out.println("getRealTimeInfo -----------------------------------");
			ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(stockID);
			if(0 == cResultRealTimeInfo.error)
			{ 
				System.out.println(cResultRealTimeInfo.realTimeInfo.name);
				System.out.println(cResultRealTimeInfo.realTimeInfo.curPrice);
				System.out.println(cResultRealTimeInfo.realTimeInfo.date);
		        System.out.println(cResultRealTimeInfo.realTimeInfo.time);
			}
			else
			{
				System.out.println("ERROR:" + cResultRealTimeInfo.error);
			}
		}
		{
			System.out.println("getRealTimeInfoMore -----------------------------------");
			ResultRealTimeInfoMore cResultRealTimeInfoMore = DataWebStockRealTimeInfo.getRealTimeInfoMore(stockID);
			if(0 == cResultRealTimeInfoMore.error)
			{ 
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.name);
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.curPrice);
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.allMarketValue);
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.circulatedMarketValue);
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.peRatio);
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.date);
				System.out.println(cResultRealTimeInfoMore.realTimeInfoMore.time);
			}
			else
			{
				System.out.println("ERROR:" + cResultRealTimeInfoMore.error);
			}
		}
	}

}
