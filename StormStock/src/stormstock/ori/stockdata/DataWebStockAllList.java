package stormstock.ori.stockdata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;

import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import stormstock.ori.stockdata.CommonDef.StockSimpleItem;


public class DataWebStockAllList {
	/*
	 * 从网络中获得所有股票ID与名字
	 * 返回0为成功，其他值为失败
	 */
	public static class ResultAllStockList
	{
		public ResultAllStockList()
		{
			error = 0;
			resultList = new ArrayList<StockSimpleItem>();
		}
		public int error;
		public List<StockSimpleItem> resultList;
	}
	public static ResultAllStockList getAllStockList()
	{
		ResultAllStockList cResultAllStockList = new ResultAllStockList();
		try{  
			String allStockListUrl = "http://quote.eastmoney.com/stocklist.html";
//            URL url = new URL(allStockListUrl);  
//            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
//            InputStream inputStream = conn.getInputStream(); 
//            byte[] getData = readInputStream(inputStream); 
//            String data = new String(getData, "gbk");  
//            System.out.println(data);
//            FileWriter fw = null; 
//            fw = new FileWriter("D:/add2.txt");
//            fw.write(data);
//            fw.close();
            
			URL url = new URL(allStockListUrl);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    

	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	        
            Parser parser = new Parser(conn); 
            parser.setEncoding("gbk");
            TagNameFilter filter1 = new TagNameFilter("DIV");  
            NodeList list1 = parser.parse(filter1);  
            
            // find qox node
            Node cQoxNode = null;
            if(list1!=null){  
                //System.out.println("list.size()==="+list.size());  
                for(int i=0; i<list1.size(); i++)  
                {
                	Node cNode = list1.elementAt(i);
                	if(cNode.getText().contains("class=\"qox\""))
                	{
                		cQoxNode = cNode;
                		break;
                	}
                }
            }  
            
			 TagNameFilter filter2=new TagNameFilter ("li");  
	         Parser p = Parser.createParser(cQoxNode.toHtml(), "gbk");
			 NodeList list2=p.extractAllNodesThatMatch(filter2);
 					
			 int allCount=0;
             for(int i=0;i<list2.size();i++)  
             {
             	Node cNode = list2.elementAt(i);
             	String tmpStr = cNode.toPlainTextString();
             	int il = tmpStr.indexOf("(");
             	int ir = tmpStr.indexOf(")");
             	String name = tmpStr.substring(0, il);
             	String id = tmpStr.substring(il+1,ir);
             	if(id.startsWith("60") || id.startsWith("00") || id.startsWith("30"))
             	{
             		if(id.length() == 6)
             		{
                 		// System.out.println(name + "," + id);
                 		allCount++;
                 		StockSimpleItem cStockItem = new StockSimpleItem();
                 		cStockItem.name = name;
                 		cStockItem.id = id;
                 		cResultAllStockList.resultList.add(cStockItem);
             		}
             	}
             }
             // System.out.println(allCount); 
             
             if(cResultAllStockList.resultList.size() <= 0) 
        	 {
            	 cResultAllStockList.error = -30;
        	 }

        }catch (Exception e) {  
        	System.out.println("Exception[WebStockAllList]:" + e.getMessage()); 
            // TODO: handle exception  
        	cResultAllStockList.error = -1;
        }  
		return cResultAllStockList;
	}

	public static class ResultRandomStock
	{
		public ResultRandomStock()
		{
			error = 0;
			resultList = new ArrayList<StockSimpleItem>();
		}
		public int error;
		public List<StockSimpleItem> resultList;
	}
	public static ResultRandomStock getRandomStock(int count)
	{
		ResultRandomStock cResultRandomStock = new ResultRandomStock();
		
		if(0 != count)
		{
			ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
			if(0 == cResultAllStockList.error)
			{
				for(int i = 0; i < count; i++)  
		        {  
					StockSimpleItem cStockItem = popRandomStock(cResultAllStockList.resultList);
					cResultRandomStock.resultList.add(cStockItem);
		        } 
			}
			else
			{
			}
		}
		return cResultRandomStock;
	}
	
	private static StockSimpleItem popRandomStock(List<StockSimpleItem> in_list)
	{
		if(in_list.size() == 0) return null;
		
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockSimpleItem cStockItem = new  StockSimpleItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockItem;
	}
	
//	private static String ENCODE = "GBK";
//    private static void message( String szMsg ) {
//        try{ 
//        	System.out.println(new String(szMsg.getBytes(ENCODE), System.getProperty("file.encoding"))); 
//        	Write(new String(szMsg.getBytes(ENCODE), System.getProperty("file.encoding")));
//        	Write("\n");
//        	}    
//        catch(Exception e )
//        {}
//    }
//    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
//        byte[] buffer = new byte[1024];    
//        int len = 0;    
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
//        while((len = inputStream.read(buffer)) != -1) {    
//            bos.write(buffer, 0, len);    
//        }    
//        bos.close();    
//        return bos.toByteArray();    
//    }    
//    public static void Write(String data) throws IOException
//    {
//        FileWriter fw = null; 
//        fw = new FileWriter("D:/add2xxx.txt", true);
//        fw.write(data);
//        fw.close();
//    }

	public static Random random = new Random();
}
