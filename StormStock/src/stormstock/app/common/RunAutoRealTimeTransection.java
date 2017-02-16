package stormstock.app.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import stormstock.ori.capi.CATHSAccount;
import stormstock.ori.capi.CATHSAccount.ResultAllStockMarketValue;
import stormstock.ori.capi.CATHSAccount.ResultAvailableMoney;
import stormstock.ori.capi.CATHSAccount.ResultTotalAssets;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;
import stormstock.ori.stockdata.CommonDef.*;

public class RunAutoRealTimeTransection {
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
	public static void rmlog()
	{
		File cfile =new File("LOG_AutoRealTimeTransection.txt");
		cfile.delete();
	}
	public static void outputLog(String s)
	{
		fmt.format("%s", s);
		File cfile =new File("LOG_AutoRealTimeTransection.txt");
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	public static class SellStockItem
	{
		public SellStockItem(String in_stockID, 
				float in_hSellPrice,
				float in_lSellPrice,
				int in_sellAmount)
		{
			m_stockID = in_stockID;
			m_hSellPrice = in_hSellPrice;
			m_lSellPrice = in_lSellPrice;
			m_sellAmount = in_sellAmount;
			m_bSelledFlag = false;
			m_iTrySellTimes = 0;
		}
		
		public static int sellStock(String stockId, int amount, float price)
		{
			String logstr = "";
			// call real api to sell
			int iRet = 0;
			iRet = CATHSAccount.sellStock(stockId, amount, price);
			if(0 == iRet)
			{
				logstr = String.format("[CallTHSAPI] sellStock stockId[%s] price[%.3f] amount[%d] OK\n", 
						stockId, price, amount);
				outputLog(logstr);
			}
			else
			{
				logstr = String.format("[CallTHSAPI] sellStock stockId[%s] price[%.3f] amount[%d] NG error:%d\n", 
						stockId, price, amount, iRet);
				outputLog(logstr);
			}
			return iRet;
		}
		
		public int CheckSell()
		{
			String logstr = "";
			//获得实时信息
			ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(m_stockID);
			if(0 == cResultRealTimeInfo.error)
			{
				logstr = String.format("    RealTimeInfo %s(%s) %s %.3f\n", 
						m_stockID, cResultRealTimeInfo.realTimeInfo.name, 
						cResultRealTimeInfo.realTimeInfo.time, cResultRealTimeInfo.realTimeInfo.curPrice);
				outputLog(logstr);
				if((cResultRealTimeInfo.realTimeInfo.time.compareTo("09:30:00") > 0 
						&& cResultRealTimeInfo.realTimeInfo.time.compareTo("11:30:00") < 0)
						||
						(cResultRealTimeInfo.realTimeInfo.time.compareTo("13:00:00") > 0 
								&& cResultRealTimeInfo.realTimeInfo.time.compareTo("15:00:00") < 0)
						)
				{
					// 交易时间
				}
				else
				{
					logstr = String.format("    [Warning] not transection time!\n");
					outputLog(logstr);
					//非交易时间
					return -8;
				}

				if(m_bSelledFlag)
				{
					//已经卖出过了, 就不再卖了
					return -2;
				}
				
				if(cResultRealTimeInfo.realTimeInfo.curPrice >= m_hSellPrice
					|| cResultRealTimeInfo.realTimeInfo.curPrice <= m_lSellPrice
					)
				{
					m_iTrySellTimes++;
					if(m_iTrySellTimes > 10)
					{
						logstr = String.format("    [Warning] try sell times over max times! m_stockID:%s\n", m_stockID);
						outputLog(logstr);
						return -3;
					}
					//小于1分 当前价 卖出
					int iSellFlag = sellStock(m_stockID, m_sellAmount, cResultRealTimeInfo.realTimeInfo.curPrice-0.01f);
					if(iSellFlag == 0)
					{
						m_bSelledFlag = true;
					}
				}
			}
			else
			{
				//获得实时信息失败，继续获取下一个
				logstr = String.format("    [Warnning] Get realtime info stockId[%s] failed!\n", m_stockID);
				outputLog(logstr);
			}
			
			return 0;
		}
		
		String m_stockID;
		float m_hSellPrice;
		float m_lSellPrice;
		int m_sellAmount;
		boolean m_bSelledFlag;
		int m_iTrySellTimes;
	}
	public static List<SellStockItem> GetSellConfig(String configFileName)
	{
		List<SellStockItem> retList = new ArrayList<SellStockItem>();
		
		String xmlStr = "";
		File cfile=new File(configFileName);
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
	        int fileLen = (int)cfile.length();
	        char[] chars = new char[fileLen];
	        reader.read(chars);
	        xmlStr = String.valueOf(chars);
//			String tempString = "";
//			while ((tempString = reader.readLine()) != null) {
//				xmlStr = xmlStr + tempString + "\n";
//	        }
			reader.close();
			//fmt.format("XML:\n" + xmlStr);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader sr = new StringReader(xmlStr);
		    InputSource is = new InputSource(sr);
		    Document doc = builder.parse(is);
		    Element rootElement = doc.getDocumentElement();
		    
		    // 检查返回数据有效性
		    if(!rootElement.getTagName().contains("control")) return retList;
	
		    NodeList contents = rootElement.getElementsByTagName("content");
	        int lenList = contents.getLength();
	        for (int i = 0; i < lenList; i++) {
	        	DayKData cDayKData = new DayKData();
	        	Node cnode = contents.item(i);
	        	String stockID = ((Element)cnode).getAttribute("stockID");
	        	String hSellPrice = ((Element)cnode).getAttribute("hSellPrice");
	        	String lSellPrice = ((Element)cnode).getAttribute("lSellPrice");
	        	String sellAmount = ((Element)cnode).getAttribute("sellAmount");
	        	
	        	SellStockItem cSellStockItem = new SellStockItem(
	        			stockID, 
	        			Float.parseFloat(hSellPrice), 
	        			Float.parseFloat(lSellPrice), 
	        			Integer.parseInt(sellAmount));
	        	
	        	retList.add(cSellStockItem);
	        }
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return retList;
		}
		
		return retList;
	}
	public static int autoSell(String configFileName)
	{
		int iEveryCheckTimeSec = 120; //120秒刷新一次，检查是否需要卖出
		String logstr = "";
		
		List<SellStockItem> retList = GetSellConfig(configFileName);
		if(retList.size()!=0)
		{
			// 账户确认
			int iInitRet = CATHSAccount.initialize();
			if(0 != iInitRet)
			{
				logstr = String.format("[ERROR] CATHSAccount.initialize failed! err:%d\n", iInitRet);
				outputLog(logstr);
				return -30;
			}
			
			// print info
			logstr = "Please check sell info... \n";
			outputLog(logstr);	
			
			ResultAvailableMoney cResultAvailableMoney = CATHSAccount.getAvailableMoney();
	        //fmt.format("CATHSAccount.getAvailableMoney err(%d) AvailableMoney(%.2f)\n", cResultAvailableMoney.error, cResultAvailableMoney.availableMoney);
	        ResultTotalAssets cResultTotalAssets = CATHSAccount.getTotalAssets();
	        //fmt.format("CATHSAccount.getTotalAssets err(%d) AvailableMoney(%.2f)\n", cResultTotalAssets.error, cResultTotalAssets.totalAssets);
	        ResultAllStockMarketValue cResultAllStockMarketValue = CATHSAccount.getAllStockMarketValue();
	        //fmt.format("CATHSAccount.getAllStockMarketValue err(%d) AvailableMoney(%.2f)\n", cResultAllStockMarketValue.error, cResultAllStockMarketValue.allStockMarketValue);
	        float avalableMoney = cResultAvailableMoney.availableMoney;
	        float allMoney = cResultTotalAssets.totalAssets;
	        float allStockMarketValue = cResultAllStockMarketValue.allStockMarketValue;
	        logstr = String.format("[THS Account] allMoney:%.3f\n", allMoney);
	        outputLog(logstr);
	        logstr = String.format("[THS Account] avalableMoney:%.3f\n", avalableMoney);
	        outputLog(logstr);
	        logstr = String.format("[THS Account] allStockMarketValue:%.3f\n", allStockMarketValue);
			outputLog(logstr);
	        
			for(int i = 0; i < retList.size(); i++)  
	        {  
				SellStockItem cSellStockItem = retList.get(i);  
				
				ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(cSellStockItem.m_stockID);
				if(0 == cResultRealTimeInfo.error)
				{
					logstr = String.format("[SellContent] %s(%s) highSell:%.3f lowSell:%.3f amount:%d\n",
		            		cSellStockItem.m_stockID, cResultRealTimeInfo.realTimeInfo.name, cSellStockItem.m_hSellPrice, cSellStockItem.m_lSellPrice, cSellStockItem.m_sellAmount);
					outputLog(logstr);
				}
				else
				{
					logstr = String.format("[Warnning] could not get RealTimeInfo, stockID:%s\n", cSellStockItem.m_stockID);
					outputLog(logstr);
					logstr = String.format("[SellContent] %s highSell:%.3f lowSell:%.3f amount:%d\n",
		            		cSellStockItem.m_stockID, cSellStockItem.m_hSellPrice, cSellStockItem.m_lSellPrice, cSellStockItem.m_sellAmount);
					outputLog(logstr);
				}
	        }
			logstr = "\ninput \"YES\" to continue...\n";
			outputLog(logstr);
			Scanner input=new Scanner(System.in);
			String s=input.next();
			outputLog(s);
			if(s.length()!=3 || !s.equals("YES"))
			{
				logstr = "input invalid!\n";
				outputLog(logstr);
				return -20;
			}
			
			// now check and wait sell
			try
			{
				logstr = String.format("\nNow Enter main loop, check current price and sell!\n");
				outputLog(logstr);
				while(true)
				{
					SimpleDateFormat dffull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
					String CurrentDateTime = dffull.format(new Date());
					SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");//设置日期格式
					String CurrentTime = dfTime.format(new Date());
					if((CurrentTime.compareTo("09:30:00") > 0 
							&& CurrentTime.compareTo("11:30:00") < 0)
							||
							(CurrentTime.compareTo("13:00:00") > 0 
									&& CurrentTime.compareTo("15:00:00") < 0)
							)
					{
						// 交易时间
						logstr = String.format(">>> LocalTime: %s\n", CurrentDateTime);
						outputLog(logstr);
						
						// 对所有股票进行检查
						for(int i = 0; i < retList.size(); i++)  
				        {  
							SellStockItem cSellStockItem = retList.get(i); 
							cSellStockItem.CheckSell();
				        }
					}
					else
					{
						//非交易时间
						logstr = String.format(">>> LocalTime: %s (not transection time)\n", CurrentDateTime);
						outputLog(logstr);
					}

					// 刷新间隔
					Thread.sleep(1000*iEveryCheckTimeSec); 
				}
			}
			catch(Exception e)
			{
				logstr = String.format(e.getMessage()); 
				outputLog(logstr);
				return -1;
			}
		}
		else
		{
			logstr = String.format("GetSellConfig %s SellList is null!\n" + configFileName);
			outputLog(logstr);
		}
	    return 0;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		rmlog();
		autoSell("AutoSellConfig.xml");
	}

}
