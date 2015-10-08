package stormstock.data;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataWebStockDayK.DayKData;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataWebStockDayK.getDayKData("600030", "20150901", "20151001", retList);
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

}
