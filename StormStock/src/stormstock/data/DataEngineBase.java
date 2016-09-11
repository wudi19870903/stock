package stormstock.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class DataEngineBase {

	public static int getDayKData(String id, List<DayKData> out_list)
	{
		String stockDayKFileName = s_DataDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);
		if(!cfile.exists())
		{
			//downloadStockDayk(id);
			return 0;
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
	public static int setDayKData(String id, List<DayKData> in_list)
	{
		String stockDayKFileName = s_DataDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < in_list.size(); i++)  
	        {  
				DayKData cDayKData = in_list.get(i);  
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
		return 0;
	}
	public static int getDividendPayout(String id, List<DividendPayout> out_list)
	{
		String stockDividendPayoutFileName = s_DataDir + "/" + id + "/" + s_DividendPayoutFile;
		File cfile=new File(stockDividendPayoutFileName);
		if(!cfile.exists())
		{
			//downloadStockDividendPayout(id);
			return 0;
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
		int retGetDividendPayout = DataWebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) return -20;
		String curAvailidDate = cRealTimeInfo.date;
		String curAvailidTime = cRealTimeInfo.time;
		
		File cfile =new File(stockDayKFileName);
		//System.out.println("updateStocData_Dayk:" + id);
		List<DayKData> retList = new ArrayList<DayKData>();
		String paramToDate = curAvailidDate.replace("-", "");
		int ret = DataWebStockDayK.getDayKData(id, "20080101", paramToDate, retList);
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
		int retGetDividendPayout = DataWebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) return -20;
		String curAvailidDate = cRealTimeInfo.date;
		String curAvailidTime = cRealTimeInfo.time;
		
		File cfile =new File(stockDividendPayoutFileName);
		// System.out.println("updateStocData_DividendPayout:" + id);
		List<DividendPayout> retList = new ArrayList<DividendPayout>();
		int ret = DataWebStockDividendPayout.getDividendPayout(id, retList);
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
		int retGetDividendPayout = DataWebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
		if(0 != retGetDividendPayout) return -20;
		String curAvailidDate = cRealTimeInfo.date;
		String curAvailidTime = cRealTimeInfo.time;
		
		List<DayDetailItem> retList = new ArrayList<DayDetailItem>();
		int ret = DataWebStockDayDetail.getDayDetail(id, date, retList);
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
	public static class StockBaseInfo
	{
		public String name;
		public float price; // 元
		public float allMarketValue; // 亿
		public float circulatedMarketValue; // 亿
		public float peRatio;
		public StockBaseInfo()
		{
			name = "";
			price = 0.0f;
			allMarketValue = 0.0f;
			circulatedMarketValue = 0.0f;
			peRatio = 0.0f;
		}
	}
	public static int saveStockBaseData(String id, StockBaseInfo baseData) 
	{
		if(0 != mkStocDataDir(id)) return -10;
		String stockBaseInfoFileName = s_DataDir + "/" + id + "/" + s_BaseInfoFile;
		File cfile =new File(stockBaseInfoFileName);
		// System.out.println("saveStockBaseData:" + id);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			String s = String.format("%s,%.3f,%.3f,%.3f,%.3f", 
					baseData.name, baseData.price, 
					baseData.allMarketValue, baseData.circulatedMarketValue, baseData.peRatio);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	public static int getStockBaseData(String id, StockBaseInfo baseData) 
	{
		String stockBaseInfoFileName = s_DataDir + "/" + id + "/" + s_BaseInfoFile;
		File cfile=new File(stockBaseInfoFileName);
		if(!cfile.exists()) return -10;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
            	String[] cols = tempString.split(",");
            	
            	baseData.name = cols[0];
            	baseData.price = Float.parseFloat(cols[1]);
            	baseData.allMarketValue = Float.parseFloat(cols[2]);
            	baseData.circulatedMarketValue = Float.parseFloat(cols[3]);
            	baseData.peRatio = Float.parseFloat(cols[4]);

                break;
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
	public static int updateStock(String id)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String CurrentDate = df.format(new Date());
		int curyear = Integer.parseInt(CurrentDate.split("-")[0]);
		int curmonth = Integer.parseInt(CurrentDate.split("-")[1]);
		int curday = Integer.parseInt(CurrentDate.split("-")[2]);
		Calendar xcal = Calendar.getInstance();
		xcal.set(curyear, curmonth-1, curday);
		int cw = xcal.get(Calendar.DAY_OF_WEEK);
		while(cw == 1 || cw == 7)
		{
			xcal.add(Calendar.DATE, -1);
			cw = xcal.get(Calendar.DAY_OF_WEEK);
		}
		Date curValiddate = xcal.getTime();
		String curValiddateStr = df.format(curValiddate);
		// 当前有效日期，上一个交易日（非周六周日）
		// System.out.println("CurrentValidDate:" + curValiddateStr);
		
		List<DayKData> retListLocal = new ArrayList<DayKData>();
		int retgetDayKData = DataEngineBase.getDayKData(id, retListLocal);
		List<DividendPayout> retListLocalDividend = new ArrayList<DividendPayout>();
		int retgetDividendPayout = DataEngineBase.getDividendPayout(id, retListLocalDividend);
		
		if(0 == retgetDayKData 
			&& 0 == retgetDividendPayout 
			&& retListLocal.size() != 0 
			/*&& retListLocalDividend.size() != 0 */)
		{
			// 本地有数据
			DayKData cDayKDataLast = retListLocal.get(retListLocal.size()-1);
			String localDataLastDate = cDayKDataLast.date; // 本地数据最后日期
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// 如果当前日期大于本地最后数据日期，需要继续检测
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				RealTimeInfo cRealTimeInfo = new RealTimeInfo();
				int retgetRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfoMore(id, cRealTimeInfo);
				if(0 == retgetRealTimeInfo)
				{
					// save basedata
					StockBaseInfo cStockBaseData = new StockBaseInfo();
					cStockBaseData.name = cRealTimeInfo.name;
					cStockBaseData.price = cRealTimeInfo.curPrice;
					cStockBaseData.allMarketValue = cRealTimeInfo.allMarketValue;
					cStockBaseData.circulatedMarketValue = cRealTimeInfo.circulatedMarketValue;
					cStockBaseData.peRatio = cRealTimeInfo.peRatio;
					saveStockBaseData(id, cStockBaseData);
					
					String webValidLastDate = cRealTimeInfo.date;
					if(cRealTimeInfo.time.compareTo("15:00:00") < 0)
					{
						int year = Integer.parseInt(cRealTimeInfo.date.split("-")[0]);
						int month = Integer.parseInt(cRealTimeInfo.date.split("-")[1]);
						int day = Integer.parseInt(cRealTimeInfo.date.split("-")[2]);
						int hour = Integer.parseInt(cRealTimeInfo.time.split(":")[0]);
						int min = Integer.parseInt(cRealTimeInfo.time.split(":")[1]);
						int sec = Integer.parseInt(cRealTimeInfo.time.split(":")[2]);
						Calendar cal0 = Calendar.getInstance();
						cal0.set(year, month-1, day, hour, min, sec);
						// 获取上一个非周末的日期
						cal0.add(Calendar.DATE, -1);
						int webwk = cal0.get(Calendar.DAY_OF_WEEK);
						while(webwk == 1 || webwk == 7)
						{
							cal0.add(Calendar.DATE, -1);
							webwk = cal0.get(Calendar.DAY_OF_WEEK);
						}
						
						Date vdate = cal0.getTime();
						webValidLastDate = df.format(vdate);
					}
					// System.out.println("webValidLastDate:" + webValidLastDate);
					
					if(webValidLastDate.compareTo(cDayKDataLast.date) > 0)
					{
						// 需要追加更新
						int year = Integer.parseInt(localDataLastDate.split("-")[0]);
						int month = Integer.parseInt(localDataLastDate.split("-")[1]);
						int day = Integer.parseInt(localDataLastDate.split("-")[2]);
						Calendar cal1 = Calendar.getInstance();
						cal1.set(year, month-1, day);
						cal1.add(Calendar.DATE, 1);
						Date fromDate = cal1.getTime();
						String fromDateStr = df.format(fromDate).replace("-", "");
						String toDateStr = webValidLastDate.replace("-", "");
						//System.out.println("fromDateStr:" + fromDateStr);
						//System.out.println("toDateStr:" + toDateStr);
						
						List<DayKData> retListMore = new ArrayList<DayKData>();
						int retgetDayKDataMore = DataWebStockDayK.getDayKData(id, fromDateStr, toDateStr, retListMore);
						if(0 == retgetDayKDataMore)
						{
							for(int i = 0; i < retListMore.size(); i++)  
					        {  
								DayKData cDayKData = retListMore.get(i);  
								retListLocal.add(cDayKData);
					        } 
							int retsetDayKData = DataEngineBase.setDayKData(id, retListLocal);
							if(0 == retsetDayKData)
							{
								// 更新复权因子数据
								if(0 == DataEngineBase.downloadStockDividendPayout(id))
									// 追加成功
									return retListMore.size();
								else
									// 更新复权因子失败
									return -80;
							}
							else
							{
								//保存本地数据失败
								return -50;
							}
						}
						else
						{
							// 网络获取追加数据失败
							return -40;
						}
						
					}
					else
					{
						// 已经和网络最新有效日线一样
						return 0;
					}
				}
				else
				{
					// 获取网络最新有效交易日期失败
					return -20;
				}
			}
			else
			{
				// 本地数据已经是最新
				return 0;
			}
		}
		else
		{
			// 本地没有数据，需要试图重新下载
			int retdownloadStockDayk =  DataEngineBase.downloadStockDayk(id);
			int retdownloadStockDividendPayout =  DataEngineBase.downloadStockDividendPayout(id);
			if(0 == retdownloadStockDayk 
					&& 0 == retdownloadStockDividendPayout)
			{
				retgetDayKData = DataEngineBase.getDayKData(id, retListLocal);
				
				//最新数据下载成功
				return retListLocal.size();
			}
			else
			{
				// 重新下载失败
				return -10;
			}
		}
		//return -100;
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
	
	
	private static String s_DataDir = "data";
	private static String s_daykFile = "dayk.txt";
	private static String s_DividendPayoutFile = "dividendPayout.txt";
	private static String s_BaseInfoFile = "baseInfo.txt";

}
