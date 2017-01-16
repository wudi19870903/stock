package stormstock.ori.stockdata;

import stormstock.ori.stockdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.ResultRealTimeInfoMore;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
		{
			System.out.println("getRealTimeInfo -----------------------------------");
			ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo("600030");
			if(0 == cResultRealTimeInfo.error)
			{ 
				System.out.println(cResultRealTimeInfo.name);
				System.out.println(cResultRealTimeInfo.curPrice);
				System.out.println(cResultRealTimeInfo.date);
		        System.out.println(cResultRealTimeInfo.time);
			}
			else
			{
				System.out.println("ERROR:" + cResultRealTimeInfo.error);
			}
		}
		{
			System.out.println("getRealTimeInfoMore -----------------------------------");
			ResultRealTimeInfoMore cResultRealTimeInfoMore = DataWebStockRealTimeInfo.getRealTimeInfoMore("300028");
			if(0 == cResultRealTimeInfoMore.error)
			{ 
				System.out.println(cResultRealTimeInfoMore.name);
				System.out.println(cResultRealTimeInfoMore.curPrice);
				System.out.println(cResultRealTimeInfoMore.allMarketValue);
				System.out.println(cResultRealTimeInfoMore.circulatedMarketValue);
				System.out.println(cResultRealTimeInfoMore.peRatio);
				System.out.println(cResultRealTimeInfoMore.date);
				System.out.println(cResultRealTimeInfoMore.time);
			}
			else
			{
				System.out.println("ERROR:" + cResultRealTimeInfoMore.error);
			}
		}
	}

}
