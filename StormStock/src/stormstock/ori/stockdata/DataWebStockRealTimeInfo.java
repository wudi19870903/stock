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


public class DataWebStockRealTimeInfo {
	public static class ResultRealTimeInfo implements Comparable
	{
		public int error;
		
		public String name;
		public String date;
		public String time;
		public float curPrice;
		
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			ResultRealTimeInfo sdto = (ResultRealTimeInfo)o;
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
	
	public static class ResultRealTimeInfoMore extends ResultRealTimeInfo
	{
		public float allMarketValue; //����ֵ
		public float circulatedMarketValue; // ��ͨ��ֵ
		public float peRatio; //��ӯ��
	}
	
	/*
	 * �������ȡĳֻ��Ʊ��ǰ��Ϣ������������ ���� ʱ�� �۸�
	 * ����0Ϊ�ɹ�������ֵΪʧ��
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
		else if(id.startsWith("99")) // ��ָ֤��
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
			cResultRealTimeInfo.name = cols[0];
			cResultRealTimeInfo.curPrice = Float.parseFloat(cols[3]);
			cResultRealTimeInfo.date = cols[30];
			cResultRealTimeInfo.time = cols[31];
			if(cResultRealTimeInfo.date.length() < 2 || cResultRealTimeInfo.name.length() < 2)
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
	 * �������ȡĳֻ��Ʊ���൱ǰ��Ϣ��������Ϣ������ֵ����ͨ��ֵ����ӯ�ʣ�
	 * ����0Ϊ�ɹ�������ֵΪʧ��
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
		
		cResultRealTimeInfoMore.name = cResultRealTimeInfoBase.name;
		cResultRealTimeInfoMore.date = cResultRealTimeInfoBase.date;
		cResultRealTimeInfoMore.time = cResultRealTimeInfoBase.time;
		cResultRealTimeInfoMore.curPrice = cResultRealTimeInfoBase.curPrice;
		
		
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
			cResultRealTimeInfoMore.error = -3;
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
			cResultRealTimeInfoMore.allMarketValue = Float.parseFloat(cells[45]); //����ֵ
			cResultRealTimeInfoMore.circulatedMarketValue = Float.parseFloat(cells[44]); // ��ͨ��ֵ
			if(cells[39].length() != 0)
				cResultRealTimeInfoMore.peRatio = Float.parseFloat(cells[39]); //��ӯ��
			else
				cResultRealTimeInfoMore.peRatio = 0.0f;
			
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
