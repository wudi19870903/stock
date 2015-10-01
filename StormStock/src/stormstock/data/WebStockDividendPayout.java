package stormstock.data;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.HtmlPage;

import stormstock.data.WebStockList.StockItem;

public class WebStockDividendPayout {
	public static void getDividendPayout()
	{
		try{  
			String urlStr = "http://vip.stock.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/300163.phtml";
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	        //设置超时间为3秒  
	        conn.setConnectTimeout(3*1000);  
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			InputStream inputStream = conn.getInputStream(); 
			byte[] getData = readInputStream(inputStream); 
			String data = new String(getData, "gbk");  
			//System.out.println(data);
//			FileWriter fw = null; 
//			fw = new FileWriter("D:/test.txt");
//			fw.write(data);
//			fw.close();
	        
//			Parser myParser;     
//	        NodeList nodeList = null;     
//	        myParser =Parser.createParser(data, "utf-8");     
//	        NodeFilter tableFilter = new NodeClassFilter(TableTag.class);     
//	        OrFilter lastFilter = new OrFilter();     
//	        lastFilter.setPredicates(new NodeFilter[] { tableFilter });     
//	        nodeList =myParser.parse(lastFilter);
//	        System.out.println(nodeList.size());
//	        for (int i = 0; i <=nodeList.size(); i++) {
//	        	if (nodeList.elementAt(i) instanceof TableTag) {     
//	        		  
//                    TableTag tag = (TableTag)nodeList.elementAt(i);     
//                       
//                     System.out.println(tag.getChildrenHTML());  
//                     System.out.println("-----------------------------------------------------");  
//	        	}
//	        }
			
	        Parser parser = Parser.createParser(data, "utf-8"); 
 
            TagNameFilter filter1 = new TagNameFilter("tbody");  
            NodeList list1 = parser.parse(filter1);  
            System.out.println(list1.size());
            if(list1.size() != 2)
            {
            	return;
            }
            
            Node cNodeFenHong = list1.elementAt(0);
            System.out.println(cNodeFenHong.toHtml());
            
//            
//            Node cNodeFenHong = list1.elementAt(0);
//            TableTag tag = (TableTag)cNodeFenHong; 
//            System.out.println(tag.getChildrenHTML());
//            System.out.println(cNodeFenHong.getFirstChild());
//            
//            System.out.println(list1.size());
//            {
//            	Node nodet = cNodeFenHong;
//
//            	System.out.println("XXX");
//    			System.out.println(cNodeFenHong.toHtml());
//    			System.out.println("XXX");
//            }

            
            
//            Node cNodePeiGu = list1.elementAt(1);
             
            
//			 TagNameFilter filter2=new TagNameFilter ("li");  
//	         Parser p = Parser.createParser(cQoxNode.toHtml(), "gbk");
//			 NodeList list2=p.extractAllNodesThatMatch(filter2);
// 					
//			 int allCount=0;
//             for(int i=0;i<list2.size();i++)  
//             {
//             	Node cNode = list2.elementAt(i);
//             	String tmpStr = cNode.toPlainTextString();
//             	int il = tmpStr.indexOf("(");
//             	int ir = tmpStr.indexOf(")");
//             	String name = tmpStr.substring(0, il);
//             	String id = tmpStr.substring(il+1,ir);
//             	if(id.startsWith("60") || id.startsWith("00") || id.startsWith("30"))
//             	{
//             		if(id.length() == 6)
//             		{
//                 		// System.out.println(name + "," + id);
//                 		allCount++;
//                 		StockItem cStockItem = new StockItem();
//                 		cStockItem.name = name;
//                 		cStockItem.id = id;
//             		}
//             	}
//             }
             // System.out.println(allCount); 

        }catch (Exception e) {  
        	System.out.println(e.getMessage()); 
            // TODO: handle exception  
        }  
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
	public static void main(String[] args){
		getDividendPayout();
	}
}
