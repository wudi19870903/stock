package stormstock.data;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

import stormstock.data.WebStockDayK.DayKData;
import stormstock.data.WebStockDividendPayout.DividendPayout;

public class DataEngine extends DataEngineBase
{
	public static class StockKData {
		// eg: 2008-01-02 09:35:00
		public String datetime;
		public float open;
		public float close;
		public float low;
		public float high;
		public float volume;
		public float amount; 
	}
	public static List<StockKData> getStock(String id) {
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
		
		List<StockKData> listStockKData =new ArrayList<StockKData>();
		
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
                	StockKData cStockKData = new StockKData();
                    String[] cols = tempString.split(",");
                    
                    cStockKData.datetime = cols[iposDate];
                    cStockKData.open = Float.parseFloat(cols[iposOpen]);
                    cStockKData.close = Float.parseFloat(cols[iposClose]);
                    cStockKData.low = Float.parseFloat(cols[iposLow]);
                    cStockKData.high = Float.parseFloat(cols[iposHigh]);
                    cStockKData.volume = Float.parseFloat(cols[iposVolume]);
                    cStockKData.amount = Float.parseFloat(cols[iposAmount]);

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
	
	public static int getDayKDataQianFuQuan(String id, List<DayKData> out_list)
	{
		int retgetDayKData = getDayKData(id, out_list);
		List<DividendPayout> retDividendPayoutList = new ArrayList<DividendPayout>();
		int retgetDividendPayout = getDividendPayout(id, retDividendPayoutList);
		if(0 != retgetDayKData || 0 != retgetDividendPayout) return -10;
		for(int i = retDividendPayoutList.size() -1; i >= 0 ; i--)  
        {  
			DividendPayout cDividendPayout = retDividendPayoutList.get(i);    
//			System.out.println(cDividendPayout.date);
//			System.out.println(cDividendPayout.songGu);
//			System.out.println(cDividendPayout.zhuanGu);
//			System.out.println(cDividendPayout.paiXi);
			
			boolean bChangeFlag = false;
			float moreGu = cDividendPayout.songGu + cDividendPayout.zhuanGu;
			float paiXi = cDividendPayout.paiXi;
            
    		for(int j = out_list.size()-1; j >=0; j--)  
            {  
    			DayKData cDayKData = out_list.get(j);  
    			
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
		for(int i = 0; i < out_list.size()-1; i++)  
        {  
			DayKData cDayKData = out_list.get(i);  
			DayKData cDayKDataNext = out_list.get(i+1);  
            float close = cDayKData.close;
            float opennext = cDayKDataNext.open;
            float changeper = Math.abs((opennext-close)/close);
            if(changeper > 0.15) 
        	{
            	System.out.println("Check Data Error : XXXXXXXXXXXXXXXXXXXXXXXX");
            	System.out.println("date:" + cDayKData.date);
            	System.out.println("close:" + close);
            	System.out.println("opennext:" + opennext);
            	System.out.println("changeper:" + Math.abs(changeper));
            	return 0;
        	}
        } 
		return 0;
	}
	
	public static void main(String[] args) {
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = getDayKDataQianFuQuan("600600", retList);
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