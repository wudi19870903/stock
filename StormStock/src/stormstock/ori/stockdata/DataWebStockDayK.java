package stormstock.ori.stockdata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import stormstock.ori.stockdata.CommonDef.*;

public class DataWebStockDayK {
	/*
	 * 从网络获取某只股票的日K数据
	 * 传入999999代表上证指数
	 * 返回0为成功，其他值为失败
	 */
	public static class ResultDayKData
	{
		public ResultDayKData()
		{
			error = 0;
			resultList = new ArrayList<DayKData>();
		}
		public int error;
		public List<DayKData> resultList;
	}
	public static ResultDayKData getDayKData(String id, String begin_date, String end_date)
	{
		ResultDayKData cResultDayKData = new ResultDayKData();
		// e.g "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?symbol=sz000002&begin_date=20160101&end_date=21000101"
		String urlStr = "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?";
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
			cResultDayKData.error = -10;
			return cResultDayKData;
		}
		urlStr = urlStr + "symbol=" + tmpId + "&begin_date=" + begin_date + "&end_date=" + end_date;
		
		try
		{
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
	        //System.out.println(data.toString()); 
//	        //文件保存位置  
//	        File file = new File("D:/test.txt");      
//	        FileOutputStream fos = new FileOutputStream(file);       
//	        fos.write(getData);   
//	        if(fos!=null){  
//	            fos.close();    
//	        }  
//	        if(inputStream!=null){  
//	            inputStream.close();  
//	        }  
//	        System.out.println("info:"+urlStr+" download success"); 
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        StringReader sr = new StringReader(data);
	        InputSource is = new InputSource(sr);
	        Document doc = builder.parse(is);
	        Element rootElement = doc.getDocumentElement();
	        // 检查返回数据有效性
	        if(!rootElement.getTagName().contains("control")) 
	        {
	        	cResultDayKData.error = -30;
	        	return cResultDayKData;
	        }

	        NodeList contents = rootElement.getElementsByTagName("content");
	        int lenList = contents.getLength();
	        for (int i = 0; i < lenList; i++) {
	        	DayKData cDayKData = new DayKData();
	        	Node cnode = contents.item(i);
	        	String date = ((Element)cnode).getAttribute("d");
	        	String open = ((Element)cnode).getAttribute("o");
	        	String high = ((Element)cnode).getAttribute("h");
	        	String close = ((Element)cnode).getAttribute("c");
	        	String low = ((Element)cnode).getAttribute("l");
	        	String volume = ((Element)cnode).getAttribute("v");
	        	cDayKData.date = date;
	        	cDayKData.open = Float.parseFloat(open);
	        	cDayKData.close = Float.parseFloat(close);
	        	cDayKData.low = Float.parseFloat(low);
	        	cDayKData.high = Float.parseFloat(high);
	        	cDayKData.volume = Float.parseFloat(volume);
	        	
	        	cResultDayKData.resultList.add(cDayKData);
	        }
		}
		catch(Exception e)
		{
			System.out.println("Exception[WebStockDayK]:" + e.getMessage()); 
        	cResultDayKData.error = -1;
        	return cResultDayKData;
		}
		
		Collections.sort(cResultDayKData.resultList);
		
		return cResultDayKData;
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
