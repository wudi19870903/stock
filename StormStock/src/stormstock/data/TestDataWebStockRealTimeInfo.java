package stormstock.data;

import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
//		{
//			System.out.println("getRealTimeInfo -----------------------------------");
//			RealTimeInfo cRealTimeInfo = new RealTimeInfo();
//			int ret = DataWebStockRealTimeInfo.getRealTimeInfo("600030", cRealTimeInfo);
//			if(0 == ret)
//			{ 
//				System.out.println(cRealTimeInfo.name);
//				System.out.println(cRealTimeInfo.curPrice);
//				System.out.println(cRealTimeInfo.date);
//		        System.out.println(cRealTimeInfo.time);
//			}
//			else
//			{
//				System.out.println("ERROR:" + ret);
//			}
//		}
		{
			System.out.println("getRealTimeInfoMore -----------------------------------");
			RealTimeInfo cRealTimeInfo = new RealTimeInfo();
			int ret = DataWebStockRealTimeInfo.getRealTimeInfoMore("300028", cRealTimeInfo);
			if(0 == ret)
			{ 
				System.out.println(cRealTimeInfo.name);
				System.out.println(cRealTimeInfo.curPrice);
				System.out.println(cRealTimeInfo.allMarketValue);
				System.out.println(cRealTimeInfo.circulatedMarketValue);
				System.out.println(cRealTimeInfo.peRatio);
				System.out.println(cRealTimeInfo.date);
				System.out.println(cRealTimeInfo.time);
			}
			else
			{
				System.out.println("ERROR:" + ret);
			}
		}
	}

}
