package stormstock.data;

import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
		RealTimeInfo cRealTimeInfo = new RealTimeInfo();
		int ret = DataWebStockRealTimeInfo.getRealTimeInfo("600030", cRealTimeInfo);
		if(0 == ret)
		{ 
			System.out.println(cRealTimeInfo.name);
			System.out.println(cRealTimeInfo.curPrice);
			System.out.println(cRealTimeInfo.date);
	        System.out.println(cRealTimeInfo.time);
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}

}
