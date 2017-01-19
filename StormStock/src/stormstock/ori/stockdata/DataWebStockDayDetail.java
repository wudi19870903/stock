package stormstock.ori.stockdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import stormstock.ori.stockdata.CommonDef.DayDetailItem;

public class DataWebStockDayDetail {
	/*
	 * 从网络获取某只股票某日内的交易细节数据
	 * 返回0为成功，其他值为失败
	 */
	public static class ResultDayDetail
	{
		public ResultDayDetail()
		{
			error = 0;
			resultList = new ArrayList<DayDetailItem>();
		}
		public int error;
		public List<DayDetailItem> resultList;
	}
	public static ResultDayDetail getDayDetail(String id, String date)
	{
		ResultDayDetail cResultDayDetail = new ResultDayDetail();
		
		// e.g "http://market.finance.sina.com.cn/downxls.php?date=2015-02-16&symbol=sz300163"
		String urlStr = "http://market.finance.sina.com.cn/downxls.php?";
		String tmpId = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			tmpId = "sh" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			tmpId = "sz" + id;
		}
		else
		{
			cResultDayDetail.error = -10;
			return cResultDayDetail;
		}
		
		try
		{
			urlStr = urlStr + "date=" + date + "&symbol=" + tmpId;
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	                 
	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	        //得到输入流  
	        InputStream inputStream = conn.getInputStream();   
	        //获取自己数组  
	        byte[] getData = readInputStream(inputStream);    
	        String data = new String(getData, "gbk");  
	        //System.out.println(data);
	        String[] lines = data.split("\n");
	        for(int i=0; i < lines.length; i++)
	        {
	        	if(i==0) continue;
	        	String line = lines[i].trim();
	        	String[] cols = line.split("\t");
	        	
	        	DayDetailItem cDayDetailItem = new DayDetailItem();
	        	cDayDetailItem.time = cols[0];
	        	cDayDetailItem.price = Float.parseFloat(cols[1]);
	        	cDayDetailItem.volume = Float.parseFloat(cols[3]);
	        	
	        	cResultDayDetail.resultList.add(cDayDetailItem);
	        }
	        
	        if(cResultDayDetail.resultList.size() <= 0) 
        	{
	        	cResultDayDetail.error = -30;
	        	return cResultDayDetail;
        	}
		}
		catch(Exception e)
		{
			System.out.println("Exception[WebStockDayDetail]:" + e.getMessage()); 
        	cResultDayDetail.error = -1;
        	return cResultDayDetail;
		}
	
		Collections.sort(cResultDayDetail.resultList);
		return cResultDayDetail;
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
