package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataWebStockDayDetail.DayDetailItem;

public class TestDataWebStockDayDetail {
	public static void main(String[] args){
		List<DayDetailItem> retList = new ArrayList<DayDetailItem>();
		int ret = DataWebStockDayDetail.getDayDetail("300163", "2015-02-16", retList);
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
			System.out.println("ERROR:" + ret);
		}
	}
}
