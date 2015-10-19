package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStockDayKData;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class Test {
	public static Formatter fmt = new Formatter(System.out);
	public static void rmlog()
	{
		File cfile =new File("test.txt");
		cfile.delete();
	}
	public static void outputLog(String s)
	{
		fmt.format("%s", s);
		File cfile =new File("test.txt");
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	public static class TestItem1 implements Comparable
	{
		public String Id;
		public float newPrice;
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			TestItem1 fo = (TestItem1)o;
			if(this.newPrice >= fo.newPrice)
				return 1;
			else
				return -1;
		}
		
	}
	public static class TestItem2 implements Comparable
	{
		public String Id;
		public float newPricePer;
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			TestItem2 fo = (TestItem2)o;
			if(this.newPricePer >= fo.newPricePer)
				return 1;
			else
				return -1;
		}
		
	}
	
	public static void main(String[] args) {
		rmlog();
		String logstr;
		
		List<TestItem1> retTestItem1List = new ArrayList<TestItem1>();
		
		List<TestItem2> retTestItem2List = new ArrayList<TestItem2>();
		
		// TODO Auto-generated method stub
		List<StockItem> retStockList = DataEngine.getLocalAllStock(); // 测试本地所有股票
		for(int i =0; i< retStockList.size(); i++)
		{
			StockItem cStockItem = retStockList.get(i);

			List<DayKData> retList = new ArrayList<DayKData>();
			int retgetDayKDataQianFuQuan = DataEngine.getDayKDataQianFuQuan(cStockItem.id, retList);
			if(0 == retgetDayKDataQianFuQuan && retList.size()>1)
			{
				DayKData cDayKDataLast = retList.get(retList.size()-1);
				if(cDayKDataLast.date.equals("2015-10-19"))
				{
					float newPrice = cDayKDataLast.close * 1.1f;
					String tmpStr = String.format("%.2f", newPrice);
					newPrice = Float.parseFloat(tmpStr);
					
					float newPer = (newPrice - cDayKDataLast.close)/cDayKDataLast.close * 100;
					tmpStr = String.format("%.2f", newPer);
					newPer = Float.parseFloat(tmpStr);
					
					logstr = String.format("%s %.2f %.2f %.2f%%\r\n", cStockItem.id, cDayKDataLast.close, newPrice,newPer);
					outputLog(logstr);
					
					TestItem1 cTestItem1 = new TestItem1();
					cTestItem1.Id = cStockItem.id;
					cTestItem1.newPrice = newPrice;
					retTestItem1List.add(cTestItem1);
					
					
					TestItem2 cTestItem2 = new TestItem2();
					cTestItem2.Id = cStockItem.id;
					cTestItem2.newPricePer = newPer;
					retTestItem2List.add(cTestItem2);
				}
				
			}
		}
		
		
		logstr = String.format("===================================\r\n");
		outputLog(logstr);
		
		Collections.sort(retTestItem1List);
		Collections.sort(retTestItem2List);
		
		for(int i=0; i < retTestItem1List.size(); i ++)
		{
			TestItem1 ccTestItem1 = retTestItem1List.get(i);
			logstr = String.format("%s %.2f \r\n", ccTestItem1.Id, ccTestItem1.newPrice);
			outputLog(logstr);
		}
		
		logstr = String.format("===================================\r\n");
		outputLog(logstr);
		
		for(int i=0; i < retTestItem2List.size(); i ++)
		{
			TestItem2 ccTestItem2 = retTestItem2List.get(i);
			logstr = String.format("%s %.2f \r\n", ccTestItem2.Id, ccTestItem2.newPricePer);
			outputLog(logstr);
		}
		
	}

}
