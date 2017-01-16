package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.CommonDef.*;

import stormstock.ori.stockdata.DataWebStockDayDetail.ResultDayDetail;

public class TestDataWebStockDayDetail {
	public static void main(String[] args){
		ResultDayDetail cResultDayDetail = DataWebStockDayDetail.getDayDetail("300163", "2015-02-16");
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
			System.out.println("ERROR:" + cResultDayDetail.error);
		}
	}
}
