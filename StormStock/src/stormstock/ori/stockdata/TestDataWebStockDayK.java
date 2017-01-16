package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.CommonDef.*;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		
		ResultDayKData cResultDayKData = DataWebStockDayK.getDayKData("600030", "20150901", "20151001");
		
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

}
