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

import stormstock.ori.stockdata.DataWebStockDividendPayout.DividendPayout;

public class DataWebStockRealTimeInfo {
	public static class RealTimeInfo implements Comparable
	{
		// base
		public String name;
		public String date;
		public String time;
		public float curPrice;
		// more
		public float allMarketValue; //����ֵ
		public float circulatedMarketValue; // ��ͨ��ֵ
		public float peRatio; //��ӯ��
		
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			RealTimeInfo sdto = (RealTimeInfo)o;
			int iCheck1 = this.date.compareTo(sdto.date);
			if(0 == iCheck1)
			{
				int iCheck2 = this.time.compareTo(sdto.time);
				return iCheck2;
			}
			else
			{
				return iCheck1;
			}
		}
	}
	public static int getRealTimeInfo(String id, RealTimeInfo out_obj)
	{
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
		else if(id.startsWith("99")) // ��ָ֤��
		{
			tmpId = "sh" + "000001";
		}
		else
		{
			return -10;
		}
		urlStr = urlStr + tmpId;
		
		try{  
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	        //���ó�ʱ��Ϊ3��  
	        conn.setConnectTimeout(3*1000);  
	        //��ֹ���γ���ץȡ������403����  
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
			out_obj.name = cols[0];
			out_obj.curPrice = Float.parseFloat(cols[3]);
			out_obj.date = cols[30];
			out_obj.time = cols[31];
			if(out_obj.date.length() < 2 || out_obj.name.length() < 2)
			{
				System.out.println("Exception[DataWebStockRealTimeInfo]: invalid data"); 
				return -2;
			}
			
        }catch (Exception e) {  
        	System.out.println("Exception[DataWebStockRealTimeInfo]:" + e.getMessage()); 
            // TODO: handle exception  
        	return -1;
        }  
		return 0;
	}
	public static int getRealTimeInfoMore(String id, RealTimeInfo out_obj)
	{
		// get base info
		int retBase = getRealTimeInfo(id, out_obj);
		if(0 != retBase) return retBase;
		
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
		else if(id.startsWith("99")) // ��ָ֤��
		{
			tmpId = "sh" + "000001"; // ��ָ֤��û�и��������Ϣ
			return 0;
		}
		else
		{
			return -10;
		}
		urlStr = urlStr + tmpId;
		
		try{  
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	        //���ó�ʱ��Ϊ3��  
	        conn.setConnectTimeout(3*1000);  
	        //��ֹ���γ���ץȡ������403����  
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
			out_obj.allMarketValue = Float.parseFloat(cells[45]); //����ֵ
			out_obj.circulatedMarketValue = Float.parseFloat(cells[44]); // ��ͨ��ֵ
			if(cells[39].length() != 0)
				out_obj.peRatio = Float.parseFloat(cells[39]); //��ӯ��
			else
				out_obj.peRatio = 0.0f;
			
        }catch (Exception e) {  
        	System.out.println("Exception[getRealTimeInfoMore]:" + e.getMessage()); 
            // TODO: handle exception  
        	return -1;
        }  
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
