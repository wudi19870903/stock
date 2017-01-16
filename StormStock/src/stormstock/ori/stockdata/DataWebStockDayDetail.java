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

import stormstock.ori.stockdata.DataWebStockDayK.DayKData;

public class DataWebStockDayDetail {
	public static class DayDetailItem implements Comparable
	{
		public String time;
		public float price;
		public float volume; // ��λ ��
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			DayDetailItem sdto = (DayDetailItem)o;
		    return this.time.compareTo(sdto.time);
		}
	}
	/*
	 * �������ȡĳֻ��Ʊĳ���ڵĽ���ϸ������
	 * ����0Ϊ�ɹ�������ֵΪʧ��
	 */
	public static int getDayDetail(String id, String date, List<DayDetailItem> out_list)
	{
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
			return -10;
		}
		
		if(out_list.size() > 0) return -20;
		
		try
		{
			urlStr = urlStr + "date=" + date + "&symbol=" + tmpId;
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	                //���ó�ʱ��Ϊ3��  
	        conn.setConnectTimeout(3*1000);  
	        //��ֹ���γ���ץȡ������403����  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	        //�õ�������  
	        InputStream inputStream = conn.getInputStream();   
	        //��ȡ�Լ�����  
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
	        	
	        	out_list.add(cDayDetailItem);
	        }
	        
	        if(out_list.size() <= 0) return -30;
		}
		catch(Exception e)
		{
			System.out.println("Exception[WebStockDayDetail]:" + e.getMessage()); 
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
