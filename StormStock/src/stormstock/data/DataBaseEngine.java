package stormstock.data;

import java.io.File;

import stormstock.data.WebStockDayDetail.DayDetailItem;

public class DataBaseEngine {
	public static class DayKData
	{
		// 2015-09-18
		public String date;
		public float open;
		public float close;
		public float low;
		public float high;
		public float volume;
	}
	public static int updateStockData(String id) {
		File dataDir =new File("data");
		if  (!dataDir .exists() && !dataDir.isDirectory())      
		{        
			dataDir.mkdir();    
		}
		File stockIdDir =new File("data/" + id);
		if  (!stockIdDir .exists() && !stockIdDir.isDirectory())      
		{        
			stockIdDir.mkdir();    
		}
		String stockDayKDataFile = "data/" + id + "/dayk.txt";
		
		
		return 0;
	}
	public static void main(String[] args) {
		int ret = updateStockData("300163");
		if(0 == ret)
		{
			System.out.println("SUCC:" + ret);
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
