package stormstock.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import stormstock.data.WebStockAllList.StockItem;
import stormstock.data.WebStockDayDetail.DayDetailItem;
import stormstock.data.WebStockDayK.DayKData;
import stormstock.data.WebStockDividendPayout.DividendPayout;
import stormstock.data.WebStockRealTimeInfo.RealTimeInfo;

public class DataBaseEngine {
	public static String s_DataDir = "data";
	public static String s_daykFile = "dayk.txt";
	public static String s_DividendPayoutFile = "dividendPayout.txt";
	public static int mkStocDataDir(String id)
	{
		File dataDir =new File(s_DataDir);
		if  (!dataDir .exists() && !dataDir.isDirectory())      
		{        
			dataDir.mkdir();    
		}
		File stockIdDir =new File(s_DataDir + "/" + id);
		if  (!stockIdDir .exists() && !stockIdDir.isDirectory())      
		{        
			stockIdDir.mkdir();    
		}
		if(stockIdDir.exists())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	public static int updateStockData(String id) {
		
		RealTimeInfo cRealTimeInfo = new RealTimeInfo();
		int retGetDividendPayout = WebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) 
		{
			System.out.println("Invalide Web data: " + id); 
			return -20;
		}
		
		if(0 != mkStocDataDir(id)) return -10;
		if(0 != updateStocData_Dayk(id))
		{
			return -10;
		}
		if(0 != updateStocData_DividendPayout(id))
		{
			return -20;
		}
		return 0;
	}
	public static int updateStocData_DividendPayout(String id)
	{
		if(0 != mkStocDataDir(id)) return -10;
		String stockDividendPayoutFileName = s_DataDir + "/" + id + "/" + s_DividendPayoutFile;
		
//		RealTimeInfo cRealTimeInfo = new RealTimeInfo();
//		int retGetDividendPayout = WebStockRealTimeInfo.getDividendPayout(id, cRealTimeInfo);
//		if(0 != retGetDividendPayout) return -20;
//		String updateDate = cRealTimeInfo.date;
		
		File cfile =new File(stockDividendPayoutFileName);
		// System.out.println("updateStocData_DividendPayout:" + id);
		List<DividendPayout> retList = new ArrayList<DividendPayout>();
		int ret = WebStockDividendPayout.getDividendPayout(id, retList);
		if(0 == ret)
		{
			try
			{
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int i = 0; i < retList.size(); i++)  
		        {  
					DividendPayout cDividendPayout = retList.get(i);
					// System.out.println(cDividendPayout.date); 
					cOutputStream.write((cDividendPayout.date + ",").getBytes());
					cOutputStream.write((cDividendPayout.songGu + ",").getBytes());
					cOutputStream.write((cDividendPayout.zhuanGu + ",").getBytes());
					cOutputStream.write((cDividendPayout.paiXi + "\n").getBytes());
		        } 
				cOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + ret);
			return -10;
		}
		return 0;
	}
	public static int updateStocData_Dayk(String id)
	{
		if(0 != mkStocDataDir(id)) return -10;
		String stockDayKFileName = s_DataDir + "/" + id + "/" + s_daykFile;
		
		RealTimeInfo cRealTimeInfo = new RealTimeInfo();
		int retGetDividendPayout = WebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) return -20;
		String curAvailidDate = cRealTimeInfo.date;
		String curAvailidTime = cRealTimeInfo.time;
		
		File cfile =new File(stockDayKFileName);
		//System.out.println("updateStocData_Dayk:" + id);
		List<DayKData> retList = new ArrayList<DayKData>();
		String paramToDate = curAvailidDate.replace("-", "");
		int ret = WebStockDayK.getDayKData(id, "20080101", paramToDate, retList);
		if(0 == ret)
		{
			try
			{
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int i = 0; i < retList.size(); i++)  
		        {  
					DayKData cDayKData = retList.get(i);  
//		            System.out.println(cDayKData.date + "," 
//		            		+ cDayKData.open + "," + cDayKData.close);  
		            cOutputStream.write((cDayKData.date + ",").getBytes());
		            cOutputStream.write((cDayKData.open + ",").getBytes());
		            cOutputStream.write((cDayKData.close + ",").getBytes());
		            cOutputStream.write((cDayKData.low + ",").getBytes());
		            cOutputStream.write((cDayKData.high + ",").getBytes());
		            cOutputStream.write((cDayKData.volume + "\n").getBytes());
		        } 
				cOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
		return 0;
	}
	public static void main(String[] args) {
		
		List<StockItem> retList = new ArrayList<StockItem>();
		int retRetAllStockList = WebStockAllList.getAllStockList(retList);
		if(0 == retRetAllStockList)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
				if(!cStockItem.id.equals("300163")) continue;
				System.out.println(cStockItem.name + "," + cStockItem.id); 
	            
	            int retUpdateStockData = updateStockData(cStockItem.id);
	    		if(0 == retUpdateStockData)
	    		{
	    			System.out.println("Update SUCC:" + retUpdateStockData);
	    		}
	    		else
	    		{
	    			System.out.println("ERROR:" + retUpdateStockData);
	    		}
	    		
	        } 
		}
	}
}
