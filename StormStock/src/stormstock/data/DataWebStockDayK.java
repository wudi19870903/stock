package stormstock.data;

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

public class DataWebStockDayK {
	public static class DayKData implements Comparable
	{
		// 2015-09-18
		public String date;
		public float open;
		public float close;
		public float low;
		public float high;
		public float volume;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			DayKData sdto = (DayKData)arg0;
		    return this.date.compareTo(sdto.date);
		}
	}
	// 600001 20080101 20151010
	public static int getDayKData(String id, String begin_date, String end_date, List<DayKData> out_list)
	{
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
			return -10;
		}
		urlStr = urlStr + "symbol=" + tmpId + "&begin_date=" + begin_date + "&end_date=" + end_date;
		
		if(out_list.size() > 0) return -20;
		
		try
		{
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	                //设置超时间为3秒  
	        conn.setConnectTimeout(3*1000);  
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
	        if(!rootElement.getTagName().contains("control")) return -30;

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
	        	out_list.add(cDayKData);
	        }
		}
		catch(Exception e)
		{
			System.out.println("Exception[WebStockDayK]:" + e.getMessage()); 
			return -1;
		}
		
		Collections.sort(out_list);
		
		return 0;
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
