package stormstock.ori.stockdata;
import java.io.File;
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.RandomAccessFile;  
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import stormstock.ori.stockdata.CommonDef.StockSimpleItem;
import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList;
import stormstock.ori.stockdata.DataWebStockDayDetail.ResultDayDetail;
import stormstock.ori.stockdata.CommonDef.*;

import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.DataWebStockDividendPayout.ResultDividendPayout;


public class DataEngine extends DataEngineBase
{
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);

	public static List<ExKData> getStock(String id) {
		Formatter fmt = new Formatter(System.out);
		String dataFileName = "";
		File root = new File("data");
		File[] fs = root.listFiles();
		if(fs == null)
		{
			fmt.format("[ERROR] not found stock file data [%s] in dir:data\n", id);
			return null;
		}
		for(int i=0; i<fs.length; i++){
			if(!fs[i].isDirectory()){
				if(fs[i].getName().contains(id))
				{
					dataFileName = fs[i].getAbsolutePath();
				}
			}
		}
		
		List<ExKData> listStockKData =new ArrayList<ExKData>();
		
        int iposDate = -1;
        int iposOpen = -1;
        int iposClose = -1;
        int iposLow = -1;
        int iposHigh = -1;
        int iposVolume = -1;
        int iposAmount = -1;
        int iIndex = -1;
		File file = new File(dataFileName);  
		if(!file.exists())
		{
			fmt.format("[ERROR] not found stock file data [%s] in dir:data\n", id);
			return null;
		}
        BufferedReader reader = null;  
        try {   
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            while ((tempString = reader.readLine()) != null) {   
            	tempString = tempString.trim();
            	if(tempString == "")
            		continue;
            	iIndex = iIndex + 1;
            	if(iIndex == 0)
            	{
            		String[] cols = tempString.split(",");
            		for(int iCheck = 0; iCheck < cols.length; iCheck++)
            		{
            			String checkStr = cols[iCheck].trim().toLowerCase();
            			if(checkStr.contains("date") && iposDate == -1)
            			{
            				iposDate = iCheck;
            			}
            			if(checkStr.contains("open") && iposOpen == -1)
            			{
            				iposOpen = iCheck;
            			}
            			if(checkStr.contains("close") && iposClose == -1)
            			{
            				iposClose = iCheck;
            			}
            			if(checkStr.contains("low") && iposLow == -1)
            			{
            				iposLow = iCheck;
            			}
            			if(checkStr.contains("high") && iposHigh == -1)
            			{
            				iposHigh = iCheck;
            			}
            			if(checkStr.contains("volume") && iposVolume == -1)
            			{
            				iposVolume = iCheck;
            			}
            			if(checkStr.contains("amount") && iposAmount == -1)
            			{
            				iposAmount = iCheck;
            			}
            		}
            	}
            	else
            	{
            		ExKData cStockKData = new ExKData();
                    String[] cols = tempString.split(",");
                    
                    cStockKData.datetime = cols[iposDate];
                    cStockKData.open = Float.parseFloat(cols[iposOpen]);
                    cStockKData.close = Float.parseFloat(cols[iposClose]);
                    cStockKData.low = Float.parseFloat(cols[iposLow]);
                    cStockKData.high = Float.parseFloat(cols[iposHigh]);
                    cStockKData.volume = Float.parseFloat(cols[iposVolume]);
                    //cStockKData.amount = Float.parseFloat(cols[iposAmount]);

                    listStockKData.add(cStockKData); 
            	}
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        
		return listStockKData;
	}
	
	public static ResultDayKData getDayKDataQianFuQuan(String id)
	{
		ResultDayKData cResultDayKData = getDayKData(id);
		ResultDividendPayout cResultDividendPayout = getDividendPayout(id);
		if(0 != cResultDayKData.error || 0 != cResultDividendPayout.error) 
		{
			cResultDayKData.error = -10;
			cResultDayKData.resultList.clear();
			return cResultDayKData;
		}
		
		for(int i = cResultDividendPayout.resultList.size() -1; i >= 0 ; i--)  
        {  
			DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);    
//			System.out.println(cDividendPayout.date);
//			System.out.println(cDividendPayout.songGu);
//			System.out.println(cDividendPayout.zhuanGu);
//			System.out.println(cDividendPayout.paiXi);
			
			boolean bChangeFlag = false;
			float moreGu = cDividendPayout.songGu + cDividendPayout.zhuanGu;
			float paiXi = cDividendPayout.paiXi;
            
    		for(int j = cResultDayKData.resultList.size()-1; j >=0; j--)  
            {  
    			DayKData cDayKData = cResultDayKData.resultList.get(j);  
    			
    			if(!bChangeFlag)
    			{
        			if(cDayKData.date.compareTo(cDividendPayout.date) <= 0)
        			{
//        				System.out.println("----------------------- ");
//        				System.out.println("date:" + cDividendPayout.date);
//        				System.out.println("moreGu:  " + moreGu);
//        				System.out.println("paiXi:  " + paiXi);
        				bChangeFlag = true;
        			}
    			}

    			
    			if(cDayKData.date.compareTo(cDividendPayout.date) < 0)
    			{
    				// pre days need change
    				// System.out.println("X  " + cDayKData.date);
    				
    				cDayKData.open = ((cDayKData.open*10)-paiXi)/(10 + moreGu);
    				cDayKData.open = (int)(cDayKData.open*1000)/(float)1000.0;
    				
    				cDayKData.close = ((cDayKData.close*10)-paiXi)/(10 + moreGu);
    				cDayKData.close = (int)(cDayKData.close*1000)/(float)1000.0;
    				
    				cDayKData.low = ((cDayKData.low*10)-paiXi)/(10 + moreGu);
    				cDayKData.low = (int)(cDayKData.low*1000)/(float)1000.0;
    				
    				cDayKData.high = ((cDayKData.high*10)-paiXi)/(10 + moreGu);
    				cDayKData.high = (int)(cDayKData.high*1000)/(float)1000.0;
    			}
            }
        }
		// checking
		for(int i = 0; i < cResultDayKData.resultList.size()-1; i++)  
        {  
			DayKData cDayKData = cResultDayKData.resultList.get(i);  
			DayKData cDayKDataNext = cResultDayKData.resultList.get(i+1);  
            float close = cDayKData.close;
            float opennext = cDayKDataNext.open;
            float changeper = Math.abs((opennext-close)/close);
            if(changeper > 0.15) 
        	{
//            	System.out.println("Warnning: Check getDayKDataQianFuQuan error! id:" + id
//            			+ " date:" + cDayKData.date);
//            	System.out.println("close:" + close);
//            	System.out.println("opennext:" + opennext);
//            	System.out.println("changeper:" + Math.abs(changeper));
            	cResultDayKData.error = -30;
            	cResultDayKData.resultList.clear();
            	return cResultDayKData;
        	}
        } 
		return cResultDayKData;
	}

	public static class ExKData {
		// eg: "2008-01-02 09:35:00"
		public String datetime;
		public float open;
		public float close;
		public float low;
		public float high;
		public float volume;
		public String getTime()
		{
			return datetime.split(" ")[1];
		}
		public String getDate()
		{
			return datetime.split(" ")[0];
		}
	}
	public static class ResultMinKDataOneDay
	{
		public ResultMinKDataOneDay()
		{
			error = 0;
			exKDataList = new ArrayList<ExKData>();
		}
		public int error;
		public List<ExKData> exKDataList;
	}
	public static ResultMinKDataOneDay get5MinKDataOneDay(String id, String date)
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = new ResultMinKDataOneDay();
		
		ResultDayDetail cResultDayDetail = getDayDetail(id, date);
		if(0 == cResultDayDetail.error && cResultDayDetail.resultList.size() != 0)
		{
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i5Min = 5*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            // add 上午
            for(int i = 0; i < 24; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec093000 + i5Min*i - i5Min*2;
                    iSecEnd = iSec093000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 23)
            	{
                    iSecBegin = iSec093000 + i5Min*i;
                    iSecEnd = iSec093000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 23)
            	{
            		iSecBegin = iSec093000 + i5Min*i;
                    iSecEnd = iSec093000 + i5Min*(i+1) + i5Min*2;
                    iStdSecEnd = iSec093000 + i5Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<DayDetailItem> tmpList = new ArrayList<DayDetailItem>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				DayDetailItem cDayDetailItem = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cDayDetailItem.time + "," 
//    	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
    	            int iSec = Integer.parseInt(cDayDetailItem.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[1])*60
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cDayDetailItem);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K5MinOpen = 0.0f;
    			float K5MinClose = 0.0f;
    			float K5MinLow = 0.0f;
    			float K5MinHigh = 0.0f;
    			float K5MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				DayDetailItem cDayDetailItem = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cDayDetailItem.price;
    					K5MinClose = cDayDetailItem.price;
    					K5MinLow = cDayDetailItem.price;
    					K5MinHigh = cDayDetailItem.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cDayDetailItem.price;
    				if(cDayDetailItem.price > K5MinHigh) K5MinHigh = cDayDetailItem.price;
    				if(cDayDetailItem.price < K5MinLow) K5MinLow = cDayDetailItem.price;
    				K5MinVolume = K5MinVolume + cDayDetailItem.volume;
    				//System.out.println(cDayDetailItem.time);
    			}
    			ExKData cExKData = new ExKData();
    			cExKData.datetime = date + " " + StdEndTimeStr;
    			cExKData.open = K5MinOpen;
    			cExKData.close = K5MinClose;
    			cExKData.low = K5MinLow;
    			cExKData.high = K5MinHigh;
    			cExKData.volume = K5MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.exKDataList.add(cExKData);
    			//System.out.println("cExKData.datetime:" + cExKData.datetime);
            }
            // add 下午
            for(int i = 0; i < 24; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec130000 + i5Min*i - i5Min*2;
                    iSecEnd = iSec130000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 23)
            	{
                    iSecBegin = iSec130000 + i5Min*i;
                    iSecEnd = iSec130000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 23)
            	{
            		iSecBegin = iSec130000 + i5Min*i;
                    iSecEnd = iSec130000 + i5Min*(i+1) + i5Min*2;
                    iStdSecEnd = iSec130000 + i5Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<DayDetailItem> tmpList = new ArrayList<DayDetailItem>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				DayDetailItem cDayDetailItem = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cDayDetailItem.time + "," 
//    	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
    	            int iSec = Integer.parseInt(cDayDetailItem.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[1])*60
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cDayDetailItem);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K5MinOpen = 0.0f;
    			float K5MinClose = 0.0f;
    			float K5MinLow = 0.0f;
    			float K5MinHigh = 0.0f;
    			float K5MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				DayDetailItem cDayDetailItem = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cDayDetailItem.price;
    					K5MinClose = cDayDetailItem.price;
    					K5MinLow = cDayDetailItem.price;
    					K5MinHigh = cDayDetailItem.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cDayDetailItem.price;
    				if(cDayDetailItem.price > K5MinHigh) K5MinHigh = cDayDetailItem.price;
    				if(cDayDetailItem.price < K5MinLow) K5MinLow = cDayDetailItem.price;
    				K5MinVolume = K5MinVolume + cDayDetailItem.volume;
    				//System.out.println(cDayDetailItem.time);
    			}
    			ExKData cExKData = new ExKData();
    			cExKData.datetime = date + " " + StdEndTimeStr;
    			cExKData.open = K5MinOpen;
    			cExKData.close = K5MinClose;
    			cExKData.low = K5MinLow;
    			cExKData.high = K5MinHigh;
    			cExKData.volume = K5MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.exKDataList.add(cExKData);
    			//System.out.println("cExKData.datetime:" + cExKData.datetime);
            }
		}
		else
		{
			System.out.println("[ERROR] get5MinKDataOneDay: " + id + " # " + date);
			cResultMinKDataOneDay.error = -10;
			return cResultMinKDataOneDay;
		}
		return cResultMinKDataOneDay;
	}
	
	public static ResultMinKDataOneDay get1MinKDataOneDay(String id, String date)
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = new ResultMinKDataOneDay();
		
		ResultDayDetail cResultDayDetail = getDayDetail(id, date);
		if(0 == cResultDayDetail.error && cResultDayDetail.resultList.size() != 0)
		{
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i1Min = 1*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            float preClosePrice = 0.0f;
            // add 上午
            for(int i = 0; i < 120; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec093000 + i1Min*i - i1Min*2;
                    iSecEnd = iSec093000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 119)
            	{
                    iSecBegin = iSec093000 + i1Min*i;
                    iSecEnd = iSec093000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 119)
            	{
            		iSecBegin = iSec093000 + i1Min*i;
                    iSecEnd = iSec093000 + i1Min*(i+1) + i1Min*2;
                    iStdSecEnd = iSec093000 + i1Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<DayDetailItem> tmpList = new ArrayList<DayDetailItem>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				DayDetailItem cDayDetailItem = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cDayDetailItem.time + "," 
//    	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
    	            int iSec = Integer.parseInt(cDayDetailItem.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[1])*60
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cDayDetailItem);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K1MinOpen = preClosePrice;
    			float K1MinClose = preClosePrice;
    			float K1MinLow = preClosePrice;
    			float K1MinHigh = preClosePrice;
    			float K1MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				DayDetailItem cDayDetailItem = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cDayDetailItem.price;
    					K1MinClose = cDayDetailItem.price;
    					K1MinLow = cDayDetailItem.price;
    					K1MinHigh = cDayDetailItem.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cDayDetailItem.price;
    				if(cDayDetailItem.price > K1MinHigh) K1MinHigh = cDayDetailItem.price;
    				if(cDayDetailItem.price < K1MinLow) K1MinLow = cDayDetailItem.price;
    				K1MinVolume = K1MinVolume + cDayDetailItem.volume;
    				//System.out.println(cDayDetailItem.time);
    			}
    			ExKData cExKData = new ExKData();
    			cExKData.datetime = date + " " + StdEndTimeStr;
    			cExKData.open = K1MinOpen;
    			cExKData.close = K1MinClose;
    			cExKData.low = K1MinLow;
    			cExKData.high = K1MinHigh;
    			cExKData.volume = K1MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.exKDataList.add(cExKData);
    			//System.out.println("cExKData.datetime:" + cExKData.datetime);
    			preClosePrice = cExKData.close;
            }
            // add 下午
            for(int i = 0; i < 120; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec130000 + i1Min*i - i1Min*2;
                    iSecEnd = iSec130000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 119)
            	{
                    iSecBegin = iSec130000 + i1Min*i;
                    iSecEnd = iSec130000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 119)
            	{
            		iSecBegin = iSec130000 + i1Min*i;
                    iSecEnd = iSec130000 + i1Min*(i+1) + i1Min*2;
                    iStdSecEnd = iSec130000 + i1Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<DayDetailItem> tmpList = new ArrayList<DayDetailItem>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				DayDetailItem cDayDetailItem = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cDayDetailItem.time + "," 
//    	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
    	            int iSec = Integer.parseInt(cDayDetailItem.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[1])*60
    	            		+ Integer.parseInt(cDayDetailItem.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cDayDetailItem);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K1MinOpen = preClosePrice;
    			float K1MinClose = preClosePrice;
    			float K1MinLow = preClosePrice;
    			float K1MinHigh = preClosePrice;
    			float K1MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				DayDetailItem cDayDetailItem = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cDayDetailItem.price;
    					K1MinClose = cDayDetailItem.price;
    					K1MinLow = cDayDetailItem.price;
    					K1MinHigh = cDayDetailItem.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cDayDetailItem.price;
    				if(cDayDetailItem.price > K1MinHigh) K1MinHigh = cDayDetailItem.price;
    				if(cDayDetailItem.price < K1MinLow) K1MinLow = cDayDetailItem.price;
    				K1MinVolume = K1MinVolume + cDayDetailItem.volume;
    				//System.out.println(cDayDetailItem.time);
    			}
    			ExKData cExKData = new ExKData();
    			cExKData.datetime = date + " " + StdEndTimeStr;
    			cExKData.open = K1MinOpen;
    			cExKData.close = K1MinClose;
    			cExKData.low = K1MinLow;
    			cExKData.high = K1MinHigh;
    			cExKData.volume = K1MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.exKDataList.add(cExKData);
    			//System.out.println("cExKData.datetime:" + cExKData.datetime);
    			preClosePrice = cExKData.close;
            }
		}
		else
		{
			System.out.println("[ERROR] get1MinKDataOneDay: " + id + " # " + date);
			cResultMinKDataOneDay.error = -10;
			return cResultMinKDataOneDay;
		}
		return cResultMinKDataOneDay;
	}
	
	public static ResultAllStockList getLocalAllStock()
	{
		ResultAllStockList cResultAllStockList = new ResultAllStockList();
		
		List<StockSimpleItem> retListAll = cResultAllStockList.resultList;
			
		// emu local
		File root = new File("data");
		File[] fs = root.listFiles();
		if(fs == null)
		{
			fmt.format("[ERROR] not found dir:data\n");
			cResultAllStockList.error = -10;
			return cResultAllStockList;
		}
		for(int i=0; i<fs.length; i++){
			if(fs[i].isDirectory()){
				String dirName = fs[i].getName();
				if(dirName.length()==6 
					&& (dirName.startsWith("6") || dirName.startsWith("3") || dirName.startsWith("0"))
						)
				{
					StockSimpleItem cStockSimpleItem = new StockSimpleItem();
					cStockSimpleItem.id = dirName;
					retListAll.add(cStockSimpleItem);
				}
				
			}
		}
		return cResultAllStockList;
	}
	public static List<StockSimpleItem> getLocalRandomStock(int count)
	{
		List<StockSimpleItem> retList = new ArrayList<StockSimpleItem>();
		if(0 != count)
		{
			List<StockSimpleItem> retListAll = new ArrayList<StockSimpleItem>();
			
			// emu local
			File root = new File("data");
			File[] fs = root.listFiles();
			if(fs == null)
			{
				fmt.format("[ERROR] not found dir:data\n");
				return null;
			}
			for(int i=0; i<fs.length; i++){
				if(fs[i].isDirectory()){
					String dirName = fs[i].getName();
					if(dirName.length()==6 
						&& (dirName.startsWith("6") || dirName.startsWith("3") || dirName.startsWith("0"))
							)
					{
						StockSimpleItem cStockSimpleItem = new StockSimpleItem();
						cStockSimpleItem.id = dirName;
						retListAll.add(cStockSimpleItem);
					}
					
				}
			}
			
			if(retListAll.size()!=0)
			{
				for(int i = 0; i < count; i++)  
		        {  
					StockSimpleItem cStockSimpleItem = popRandomStock(retListAll);
					retList.add(cStockSimpleItem);
		        } 
			}
		}
		return retList;
	}
	
	/*
	 * 更新数据到指定日期
	 */
	public static int updateAllLocalStocks(String dateStr)
	{
		ResultUpdatedStocksDate cResultUpdatedStocksDate = DataEngineBase.getUpdatedStocksDate();
		if(0 == cResultUpdatedStocksDate.error)
		{
			if(cResultUpdatedStocksDate.date.compareTo(dateStr) >= 0)
			{
				fmt.format("update success! (current is newest, local: %s)\n", cResultUpdatedStocksDate.date);
				return 0;
			}
		}
		
		int iRetupdateStock = 0;
		// 更新指数k
		String ShangZhiId = "999999";
		String ShangZhiName = "上证指数";
		iRetupdateStock = DataEngineBase.updateStock(ShangZhiId);
		String newestDate = "";
		
		ResultDayKData cResultDayKData = DataEngine.getDayKDataQianFuQuan(ShangZhiId);
		if(0 == cResultDayKData.error && cResultDayKData.resultList.size() > 0)
		{
			newestDate = cResultDayKData.resultList.get(cResultDayKData.resultList.size()-1).date;
		}
		if(iRetupdateStock >= 0)
		{

			fmt.format("update success: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, iRetupdateStock, newestDate);
		}
		else
		{
			fmt.format("update ERROR: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, iRetupdateStock, newestDate);
		}
		
		// 更新所有k
		ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
		if(0 == cResultAllStockList.error)
		{
			for(int i = 0; i < cResultAllStockList.resultList.size(); i++)  
	        {  
				StockSimpleItem cStockSimpleItem = cResultAllStockList.resultList.get(i);  
	            iRetupdateStock = DataEngineBase.updateStock(cStockSimpleItem.id);
	            
	            ResultDayKData cResultDayKDataQFQ = DataEngine.getDayKDataQianFuQuan(ShangZhiId);
	            
	    		if(0 == cResultDayKDataQFQ.error && cResultDayKDataQFQ.resultList.size() > 0)
	    		{
	    			fmt.format("update success: %s (%s) item:%d date:%s\n", cStockSimpleItem.id, cStockSimpleItem.name, iRetupdateStock, newestDate);
	    		}
	            else
	            {
	            	fmt.format("update ERROR: %s (%s) item:%d date:%s\n", cStockSimpleItem.id, cStockSimpleItem.name, iRetupdateStock, newestDate);
	            }
	            
	        } 
			System.out.println("update finish, count:" + cResultAllStockList.resultList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + cResultAllStockList.error);
		}
		DataEngineBase.updateStocksFinish(dateStr);
		return iRetupdateStock;
	}
	
	private static StockSimpleItem popRandomStock(List<StockSimpleItem> in_list)
	{
		if(in_list.size() == 0) return null;
		
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockSimpleItem cStockSimpleItem = new  StockSimpleItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockSimpleItem;
	}
}