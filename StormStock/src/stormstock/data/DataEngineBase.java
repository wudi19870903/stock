package stormstock.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import stormstock.data.WebStockAllList.StockItem;
import stormstock.data.WebStockDayDetail.DayDetailItem;
import stormstock.data.WebStockDayK.DayKData;
import stormstock.data.WebStockDividendPayout.DividendPayout;
import stormstock.data.WebStockRealTimeInfo.RealTimeInfo;

public class DataEngineBase {

	public static int getDayKData(String id, List<DayKData> out_list)
	{
		String stockDayKFileName = s_DataDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);
		if(!cfile.exists())
		{
			downloadStockDayk(id);
		}
		if(!cfile.exists()) return -10;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
            	DayKData cDayKData = new DayKData();
            	String[] cols = tempString.split(",");
            	
            	cDayKData.date = cols[0];
	        	cDayKData.open = Float.parseFloat(cols[1]);
	        	cDayKData.close = Float.parseFloat(cols[2]);
	        	cDayKData.low = Float.parseFloat(cols[3]);
	        	cDayKData.high = Float.parseFloat(cols[4]);
	        	cDayKData.volume = Float.parseFloat(cols[5]);
	        	out_list.add(cDayKData);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	public static int getDividendPayout(String id, List<DividendPayout> out_list)
	{
		String stockDividendPayoutFileName = s_DataDir + "/" + id + "/" + s_DividendPayoutFile;
		File cfile=new File(stockDividendPayoutFileName);
		if(!cfile.exists())
		{
			downloadStockDividendPayout(id);
		}
		if(!cfile.exists()) return -10;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
            	String[] cols = tempString.split(",");
            	
            	DividendPayout cDividendPayout = new DividendPayout();
            	cDividendPayout.date = cols[0];
                cDividendPayout.songGu = Float.parseFloat(cols[1]);
                cDividendPayout.zhuanGu = Float.parseFloat(cols[2]);
                cDividendPayout.paiXi = Float.parseFloat(cols[3]);
                out_list.add(cDividendPayout);
                
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	public static int getDayDetail(String id, String date, List<DayDetailItem> out_list)
	{
		String stockDataDetailFileName = s_DataDir + "/" + id + "/" + date + ".txt";
		File cfile=new File(stockDataDetailFileName);
		if(!cfile.exists())
		{
			downloadStockDataDetail(id, date);
		}
		if(!cfile.exists()) return -10;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // System.out.println("line " + line + ": " + tempString);
            	DayKData cDayKData = new DayKData();
            	String[] cols = tempString.split(",");

            	DayDetailItem cDayDetailItem = new DayDetailItem();
	        	cDayDetailItem.time = cols[0];
	        	cDayDetailItem.price = Float.parseFloat(cols[1]);
	        	cDayDetailItem.volume = Float.parseFloat(cols[2]);
	        	
	        	out_list.add(cDayDetailItem);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	public static int downloadStockDayk(String id)
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
	public static int downloadStockDividendPayout(String id)
	{
		if(0 != mkStocDataDir(id)) return -10;
		String stockDividendPayoutFileName = s_DataDir + "/" + id + "/" + s_DividendPayoutFile;
		
		RealTimeInfo cRealTimeInfo = new RealTimeInfo();
		int retGetDividendPayout = WebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) return -20;
		String curAvailidDate = cRealTimeInfo.date;
		String curAvailidTime = cRealTimeInfo.time;
		
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
	public static int downloadStockDataDetail(String id, String date) {
		if(0 != mkStocDataDir(id)) return -20;
		String stockDataDetailFileName = s_DataDir + "/" + id + "/" + date + ".txt";
		
		RealTimeInfo cRealTimeInfo = new RealTimeInfo();
		int retGetDividendPayout = WebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) return -20;
		String curAvailidDate = cRealTimeInfo.date;
		String curAvailidTime = cRealTimeInfo.time;
		
		List<DayDetailItem> retList = new ArrayList<DayDetailItem>();
		int ret = WebStockDayDetail.getDayDetail(id, date, retList);
		if(0 == ret)
		{
			try
			{
				File cfile =new File(stockDataDetailFileName);
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int i = 0; i < retList.size(); i++)  
		        {  
					DayDetailItem cDayDetailItem = retList.get(i);  
//			            System.out.println(cDayDetailItem.time + "," 
//			            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
					cOutputStream.write((cDayDetailItem.time + ",").getBytes());
					cOutputStream.write((cDayDetailItem.price + ",").getBytes());
					cOutputStream.write((cDayDetailItem.volume + "\n").getBytes());
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
			return -30;
		}
		return 0;
	}
	
	private static int mkStocDataDir(String id)
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
	private static void test_getDayKData()
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = getDayKData("300163", retList);
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
	private static void test_getDividendPayout()
	{
		List<DividendPayout> retList = new ArrayList<DividendPayout>();
		int ret = getDividendPayout("300163", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = retList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("getDividendPayout ERROR:" + ret);
		}
	}
	private static void test_getDayDetail()
	{
		List<DayDetailItem> retList = new ArrayList<DayDetailItem>();
		int ret = getDayDetail("300163", "2015-02-16", retList);
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
			System.out.println("getDayDetail ERROR:" + ret);
		}
	}
	private static void test_downloadStockDayk()
	{
		int retdownloadStockDayk = downloadStockDayk("300163");
		if(0 != retdownloadStockDayk)
		{
			System.out.println("downloadStockDayk ERROR:" + retdownloadStockDayk);
		}
	}
	private static void test_downloadStockDividendPayout()
	{
		int retdownloadStockDividendPayout = downloadStockDividendPayout("300163");
		if(0 != retdownloadStockDividendPayout)
		{
			System.out.println("downloadStockDividendPayout ERROR:" + retdownloadStockDividendPayout);
		}
	}
	private static void test_downloadStockDataDetail()
	{
		int retdownloadStockDataDetail = downloadStockDataDetail("300163", "2015-09-30");
		if(0 != retdownloadStockDataDetail)
		{
			System.out.println("downloadStockDataDetail ERROR:" + retdownloadStockDataDetail);	
		}
	}
	private static String s_DataDir = "data";
	private static String s_daykFile = "dayk.txt";
	private static String s_DividendPayoutFile = "dividendPayout.txt";
	
	public static void main(String[] args) {
		test_getDayKData();
		test_getDividendPayout();
		test_getDayDetail();
//		test_downloadStockDayk();
//		test_downloadStockDividendPayout();
//		test_downloadStockDataDetail();
	}
}
