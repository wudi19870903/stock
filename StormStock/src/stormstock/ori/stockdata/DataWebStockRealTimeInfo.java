package stormstock.ori.stockdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import stormstock.ori.stockdata.CommonDef.*;


public class DataWebStockRealTimeInfo {
	
	public static class ResultRealTimeInfo
	{
		public ResultRealTimeInfo()
		{
			error = 0;
			realTimeInfo = new RealTimeInfo();
		}
		public int error;
		public RealTimeInfo realTimeInfo;
	}
	
	public static class ResultRealTimeInfoMore
	{
		public ResultRealTimeInfoMore()
		{
			error = 0;
			realTimeInfoMore = new RealTimeInfoMore();
		}
		public int error;
		public RealTimeInfoMore realTimeInfoMore;
	}
	
	/*
	 * 从网络获取某只股票当前信息（基本：名字 日期 时间 价格）
	 * 返回0为成功，其他值为失败
	 */
	public static ResultRealTimeInfo getRealTimeInfo(String id)
	{
		ResultRealTimeInfo cResultRealTimeInfo = new ResultRealTimeInfo();
		// e.g http://hq.sinajs.cn/list=sz300163
		String urlStr = "http://hq.sinajs.cn/list=";
		String tmpId = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			tmpId = "sh" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			tmpId = "sz" + id;
		}
		else if(id.startsWith("99")) // 上证指数
		{
			tmpId = "sh" + "000001";
		}
		else
		{
			cResultRealTimeInfo.error = -10;
			return cResultRealTimeInfo;
		}
		urlStr = urlStr + tmpId;
		
		try{  
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();   
	        
	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间 
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			InputStream inputStream = conn.getInputStream(); 
			byte[] getData = readInputStream(inputStream); 
			String data = new String(getData, "gbk");  
			//System.out.println(data);     
			String[] cells = data.split("\"");
			int lenCells = cells.length;
			String validdata = cells[lenCells - 2];
			//System.out.println(validdata);     
			String[] cols = validdata.split(",");
			cResultRealTimeInfo.realTimeInfo.name = cols[0];
			cResultRealTimeInfo.realTimeInfo.curPrice = Float.parseFloat(cols[3]);
			cResultRealTimeInfo.realTimeInfo.date = cols[30];
			cResultRealTimeInfo.realTimeInfo.time = cols[31];
			if(cResultRealTimeInfo.realTimeInfo.date.length() < 2 || cResultRealTimeInfo.realTimeInfo.name.length() < 2)
			{
				System.out.println("Exception[DataWebStockRealTimeInfo]: invalid data"); 
				cResultRealTimeInfo.error = -2;
				return cResultRealTimeInfo;
			}
			
        }catch (Exception e) {  
        	System.out.println("Exception[DataWebStockRealTimeInfo]:" + e.getMessage()); 
            // TODO: handle exception  
        	cResultRealTimeInfo.error = -1;
			return cResultRealTimeInfo;
        }  
		return cResultRealTimeInfo;
	}
	/*
	 * 从网络获取某只股票更多当前信息（基本信息，总市值，流通市值，市盈率）
	 * 返回0为成功，其他值为失败
	 */
	public static ResultRealTimeInfoMore getRealTimeInfoMore(String id)
	{
		ResultRealTimeInfoMore cResultRealTimeInfoMore = new ResultRealTimeInfoMore();
		
		// get base info
		ResultRealTimeInfo cResultRealTimeInfoBase = getRealTimeInfo(id);
		if(0 != cResultRealTimeInfoBase.error) 
		{
			cResultRealTimeInfoMore.error = -2;
			return cResultRealTimeInfoMore;
		}
		
		cResultRealTimeInfoMore.realTimeInfoMore.name = cResultRealTimeInfoBase.realTimeInfo.name;
		cResultRealTimeInfoMore.realTimeInfoMore.date = cResultRealTimeInfoBase.realTimeInfo.date;
		cResultRealTimeInfoMore.realTimeInfoMore.time = cResultRealTimeInfoBase.realTimeInfo.time;
		cResultRealTimeInfoMore.realTimeInfoMore.curPrice = cResultRealTimeInfoBase.realTimeInfo.curPrice;
		
		
		// e.g http://qt.gtimg.cn/q=sz000858
		String urlStr = "http://qt.gtimg.cn/q=";
		String tmpId = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			tmpId = "sh" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			tmpId = "sz" + id;
		}
		else if(id.startsWith("99")) // 上证指数
		{
			tmpId = "sh" + "000001"; // 上证指数没有更多基本信息
			cResultRealTimeInfoMore.error = 0;
			return cResultRealTimeInfoMore;
		}
		else
		{
			cResultRealTimeInfoMore.error = -10;
			return cResultRealTimeInfoMore;
		}
		urlStr = urlStr + tmpId;
		
		try{  
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    

	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			InputStream inputStream = conn.getInputStream(); 
			byte[] getData = readInputStream(inputStream); 
			String data = new String(getData, "gbk");  
			//System.out.println(data);     
			String[] cells = data.split("~");
//			for(int i =0; i< cells.length; i++)
//			{
//				System.out.println(cells[i]);
//			}
			cResultRealTimeInfoMore.realTimeInfoMore.allMarketValue = Float.parseFloat(cells[45]); //总市值
			cResultRealTimeInfoMore.realTimeInfoMore.circulatedMarketValue = Float.parseFloat(cells[44]); // 流通市值
			if(cells[39].length() != 0)
				cResultRealTimeInfoMore.realTimeInfoMore.peRatio = Float.parseFloat(cells[39]); //市盈率
			else
				cResultRealTimeInfoMore.realTimeInfoMore.peRatio = 0.0f;
			
        }catch (Exception e) {  
        	System.out.println("Exception[getRealTimeInfoMore]:" + e.getMessage()); 
            // TODO: handle exception  
			cResultRealTimeInfoMore.error = -1;
        	return cResultRealTimeInfoMore;
        }  
		return cResultRealTimeInfoMore;
	}
	
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }  
}
