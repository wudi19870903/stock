package stormstock.fw.tranbase.account;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsXML;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;

public class MockAccountOpeStore {
	
	public static class StoreEntity
	{
		public float money;
		List<String> stockSelectList;
		List<CommissionOrder> commissionOrderList;
		List<HoldStock> holdStockList;
		List<DeliveryOrder> deliveryOrderList;
	}
	
	public MockAccountOpeStore(String accountID, String password)
	{
		m_accountID = accountID;
		m_password = password;
		m_accXMLFile = "account\\account_" + m_accountID + ".xml";
	}
	
	public boolean storeInit()
	{
		Document doc=null;
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder= factory.newDocumentBuilder();
			doc=builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 创建元素
		Element root=doc.createElement("account");
		root.setAttribute("ID", m_accountID);
		root.setAttribute("password", m_password);
        doc.appendChild(root);
		
		TransformerFactory tfactory=TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(null != doc && null != transformer)
		{
			DOMSource source=new DOMSource(doc);
			StreamResult result=new StreamResult(new File(m_accXMLFile));
			transformer.setOutputProperty("encoding","GBK");
			try {
				transformer.transform(source,result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}
	
	public boolean store(StoreEntity cStoreEntity)
	{
		File cfile=new File(m_accXMLFile);
		if(cfile.exists())
		{
			cfile.delete();
		}
		
		Document doc=null;
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder= factory.newDocumentBuilder();
			doc=builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 创建元素
		Element root=doc.createElement("account");
		root.setAttribute("ID", m_accountID);
		root.setAttribute("password", m_password);
        doc.appendChild(root);
        
        if(null != cStoreEntity)
        {
        	// money
        	String totalVal = String.format("%.3f", cStoreEntity.money);
        	Element Node_Money=doc.createElement("Money");
        	Node_Money.setAttribute("total", totalVal);
        	root.appendChild(Node_Money);
        	
        	// StockSelectList
        	Element Node_StockSelectList=doc.createElement("SelectList");
        	root.appendChild(Node_StockSelectList);
        	for(int i=0;i<cStoreEntity.stockSelectList.size();i++)
        	{
        		String stockID = cStoreEntity.stockSelectList.get(i);
        		
        		Element Node_Stock = doc.createElement("Stock");
        		Node_Stock.setAttribute("stockID", stockID);
        		Node_StockSelectList.appendChild(Node_Stock);
        	}
        	
        	// CommissionOrderList
        	Element Node_CommissionOrderList=doc.createElement("CommissionOrderList");
        	root.appendChild(Node_CommissionOrderList);
        	for(int i=0;i<cStoreEntity.commissionOrderList.size();i++)
        	{
        		CommissionOrder cCommissionOrder = cStoreEntity.commissionOrderList.get(i);
        		String tranactVal = "";
        		if(cCommissionOrder.tranAct == TRANACT.BUY) tranactVal= "BUY";
        		if(cCommissionOrder.tranAct == TRANACT.SELL) tranactVal= "SELL";
        		String amountVal = String.format("%d", cCommissionOrder.amount);
        		String priceVal =String.format("%.3f", cCommissionOrder.price);
        				
        		Element Node_Stock = doc.createElement("Stock");
        		Node_Stock.setAttribute("date", cCommissionOrder.date);
        		Node_Stock.setAttribute("time", cCommissionOrder.time);
        		Node_Stock.setAttribute("tranact", tranactVal);
        		Node_Stock.setAttribute("stockID", cCommissionOrder.stockID);
        		Node_Stock.setAttribute("amount", amountVal);
        		Node_Stock.setAttribute("price", priceVal);
        		
        		Node_CommissionOrderList.appendChild(Node_Stock);
        	}
        	
        	// HoldStockList
        	Element Node_HoldStockList=doc.createElement("HoldStockList");
        	root.appendChild(Node_HoldStockList);
        	for(int i=0;i<cStoreEntity.holdStockList.size();i++)
        	{
        		HoldStock cHoldStock = cStoreEntity.holdStockList.get(i);
        		String holdDayCntVal = String.format("%d", cHoldStock.holdDayCnt);
        		String totalAmountVal =String.format("%d", cHoldStock.totalAmount);
        		String totalCanSellVal =String.format("%d", cHoldStock.totalCanSell);
        		String holdAvePriceVal =String.format("%.3f", cHoldStock.holdAvePrice);
        		String curPriceVal =String.format("%.3f", cHoldStock.curPrice);
        		String transactionCostVal =String.format("%.3f", cHoldStock.transactionCost);
        				
        		Element Node_Stock = doc.createElement("Stock");
        		Node_Stock.setAttribute("createDate", cHoldStock.createDate);
        		Node_Stock.setAttribute("createTime", cHoldStock.createTime);
        		Node_Stock.setAttribute("holdDayCnt", holdDayCntVal);
        		Node_Stock.setAttribute("totalAmount", totalAmountVal);
        		Node_Stock.setAttribute("totalCanSell", totalCanSellVal);
        		Node_Stock.setAttribute("holdAvePrice", holdAvePriceVal);
        		Node_Stock.setAttribute("curPrice", curPriceVal);
        		Node_Stock.setAttribute("transactionCost", transactionCostVal);
        		Node_HoldStockList.appendChild(Node_Stock);
        	}
        	
        	// CommissionOrderList
        	Element Node_DeliveryOrderList=doc.createElement("DeliveryOrderList");
        	root.appendChild(Node_DeliveryOrderList);
        	for(int i=0;i<cStoreEntity.deliveryOrderList.size();i++)
        	{
        		DeliveryOrder cDeliveryOrder = cStoreEntity.deliveryOrderList.get(i);
        		String tranactVal = "";
        		if(cDeliveryOrder.tranAct == TRANACT.BUY) tranactVal= "BUY";
        		if(cDeliveryOrder.tranAct == TRANACT.SELL) tranactVal= "SELL";
        		String amountVal = String.format("%d", cDeliveryOrder.amount);
        		String holdAvePriceVal =String.format("%.3f", cDeliveryOrder.holdAvePrice);
        		String tranPriceVal =String.format("%.3f", cDeliveryOrder.tranPrice);
        		String transactionCostVal =String.format("%.3f", cDeliveryOrder.transactionCost);
        				
        		Element Node_Stock = doc.createElement("Stock");
        		Node_Stock.setAttribute("date", cDeliveryOrder.date);
        		Node_Stock.setAttribute("time", cDeliveryOrder.time);
        		Node_Stock.setAttribute("tranact", tranactVal);
        		Node_Stock.setAttribute("stockID", cDeliveryOrder.stockID);
        		Node_Stock.setAttribute("amount", amountVal);
        		Node_Stock.setAttribute("holdAvePrice", holdAvePriceVal);
        		Node_Stock.setAttribute("tranPrice", tranPriceVal);
        		Node_Stock.setAttribute("transactionCost", transactionCostVal);
        		
        		Node_DeliveryOrderList.appendChild(Node_Stock);
        	}
        }
		
        
		TransformerFactory tfactory=TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获得最终oriXmlStr
		String oriXmlStr ="";
		if(null != doc && null != transformer)
		{
			transformer.setOutputProperty("encoding","GBK");
			DOMSource source=new DOMSource(doc);
			
			StringWriter writer = new StringWriter();
			StreamResult result=new StreamResult(writer);
			try {
				transformer.transform(source,result);
				oriXmlStr = writer.toString();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 格式化XmlStr
		String formatedXmlStr = "";
		try {
			formatedXmlStr = BUtilsXML.format(oriXmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 更新到文件
		File cfile_new = new File(m_accXMLFile);
		try {
			FileWriter fw = new FileWriter(cfile_new.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(formatedXmlStr);
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public StoreEntity load()
	{
		BLog.output("ACCOUNT", "MockAccountOpeStore load\n");
		
		String xmlStr = "";
		File cfile=new File(m_accXMLFile);
		if(!cfile.exists())
		{
			BLog.output("ACCOUNT", "MockAccountOpeStore storeInit (no file)\n");
			storeInit();
			return null; // 没有文件 load失败
		}
		
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
			if(xmlStr.length()<=0)
			{
				BLog.output("ACCOUNT", "MockAccountOpeStore storeInit (no content)\n");
				storeInit(); 
				return null; // 没有内容 load失败
			}
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader sr = new StringReader(xmlStr);
		    InputSource is = new InputSource(sr);
		    Document doc = builder.parse(is);
		    Element rootElement = doc.getDocumentElement();
		    
		    // 检查返回数据有效性
		    if(!rootElement.getTagName().contains("account")) 
			{
		    	BLog.output("ACCOUNT", "MockAccountOpeStore storeInit (no account root)\n");
				storeInit(); 
				return null; // 没有root load失败
			}
		    
		    // 账户属性判断并加载
		    String accountID = rootElement.getAttribute("ID");
		    String password = rootElement.getAttribute("password");
		    if(!accountID.equals(m_accountID) || !password.equals(m_password))
			{
		    	BLog.output("ACCOUNT", "MockAccountOpeStore storeInit (accountID or password error)\n");
				storeInit();
				return null; // 账号秘密不对 load失败
			}
		    
		    String acc_date = rootElement.getAttribute("date");
		    //BLog.output("ACCOUNT", "accountID:%s password:%s acc_date:%s \n", accountID, password, acc_date);
	
		    // 钱加载
		    float money = 0.0f;
		    {
		    	NodeList nodelist_Money = rootElement.getElementsByTagName("Money");
		    	if(nodelist_Money.getLength() == 1)
		    	{
		    		Node Node_Money = nodelist_Money.item(0);
		    		String total = ((Element)Node_Money).getAttribute("total");
		    		money = Float.parseFloat(total);
		    	}
		    }
		    
		    // 选股列表加载
		    List<String> stockSelectList = new ArrayList<String>();
		    {
		    	NodeList nodelist_SelectList = rootElement.getElementsByTagName("SelectList");
		        if(nodelist_SelectList.getLength() == 1)
	        	{
		        	Node Node_SelectList = nodelist_SelectList.item(0);
		        	NodeList nodelist_Stock = Node_SelectList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
				        	String stockID = ((Element)node_Stock).getAttribute("stockID");
				        	//BLog.output("ACCOUNT", "stockID:%s \n", stockID);
				        	stockSelectList.add(stockID); 
			        	}
			        }
	        	}
		    }
		    
		    // 委托单加载
		    List<CommissionOrder> commissionOrderList = new ArrayList<CommissionOrder>();
		    {
		    	NodeList nodelist_CommissionOrderList = rootElement.getElementsByTagName("CommissionOrderList");
		        if(nodelist_CommissionOrderList.getLength() == 1)
	        	{
		        	Node Node_CommissionOrderList = nodelist_CommissionOrderList.item(0);
		        	NodeList nodelist_Stock = Node_CommissionOrderList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String date = ((Element)node_Stock).getAttribute("date");
			        		String time = ((Element)node_Stock).getAttribute("time");
				        	String tranAct = ((Element)node_Stock).getAttribute("tranAct");
				        	String stockID = ((Element)node_Stock).getAttribute("stockID");
				        	String amount = ((Element)node_Stock).getAttribute("amount");
				        	String price = ((Element)node_Stock).getAttribute("price");
				        	
				        	CommissionOrder cCommissionOrder = new CommissionOrder();
				        	cCommissionOrder.date = date;
				        	cCommissionOrder.time = time;
				        	if(tranAct.equals("BUY")) cCommissionOrder.tranAct = TRANACT.BUY;
				        	if(tranAct.equals("SELL")) cCommissionOrder.tranAct = TRANACT.SELL;
				        	cCommissionOrder.stockID = stockID;
				        	cCommissionOrder.amount = Integer.parseInt(amount);
				        	cCommissionOrder.price = Float.parseFloat(price);
				        	commissionOrderList.add(cCommissionOrder);
			        	}
			        }
	        	}
		    }
		    
		    // 持股加载
		    List<HoldStock> holdStockList = new ArrayList<HoldStock>();
		    {
		    	NodeList nodelist_HoldStockList = rootElement.getElementsByTagName("HoldStockList");
		        if(nodelist_HoldStockList.getLength() == 1)
	        	{
		        	Node Node_HoldStockList = nodelist_HoldStockList.item(0);
		        	NodeList nodelist_Stock = Node_HoldStockList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String stockID = ((Element)node_Stock).getAttribute("stockID");
			        		String createDate = ((Element)node_Stock).getAttribute("createDate");
				        	String createTime = ((Element)node_Stock).getAttribute("createTime");
				        	String holdDayCnt = ((Element)node_Stock).getAttribute("holdDayCnt");
				        	String totalAmount = ((Element)node_Stock).getAttribute("totalAmount");
				        	String totalCanSell = ((Element)node_Stock).getAttribute("totalCanSell");
				        	String holdAvePrice = ((Element)node_Stock).getAttribute("holdAvePrice");
				        	String curPrice = ((Element)node_Stock).getAttribute("curPrice");
				        	String transactionCost = ((Element)node_Stock).getAttribute("transactionCost");
				        	
				        	HoldStock cHoldStock = new HoldStock();
				        	cHoldStock.stockID = stockID;
				        	cHoldStock.createDate = createDate;
				        	cHoldStock.createTime = createTime;
				        	cHoldStock.holdDayCnt = Integer.parseInt(holdDayCnt);
				        	cHoldStock.totalAmount = Integer.parseInt(totalAmount);
				        	cHoldStock.totalCanSell = Integer.parseInt(totalCanSell);
				        	cHoldStock.holdAvePrice = Float.parseFloat(holdAvePrice);
				        	cHoldStock.curPrice = Float.parseFloat(curPrice);
				        	cHoldStock.transactionCost = Float.parseFloat(transactionCost);
				        	holdStockList.add(cHoldStock);
			        	}
			        }
	        	}
		    }
		    
		    // 交割单加载 
		    List<DeliveryOrder> deliveryOrderList = new ArrayList<DeliveryOrder>();
		    {
		    	NodeList nodelist_DeliveryOrderList = rootElement.getElementsByTagName("DeliveryOrderList");
		        if(nodelist_DeliveryOrderList.getLength() == 1)
	        	{
		        	Node Node_DeliveryOrderList = nodelist_DeliveryOrderList.item(0);
		        	NodeList nodelist_Stock = Node_DeliveryOrderList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String date = ((Element)node_Stock).getAttribute("date");
			        		String time = ((Element)node_Stock).getAttribute("time");
				        	String tranAct = ((Element)node_Stock).getAttribute("tranAct");
				        	String stockID = ((Element)node_Stock).getAttribute("stockID");
				        	String amount = ((Element)node_Stock).getAttribute("amount");
				        	String holdAvePrice = ((Element)node_Stock).getAttribute("holdAvePrice");
				        	String tranPrice = ((Element)node_Stock).getAttribute("tranPrice");
				        	String transactionCost = ((Element)node_Stock).getAttribute("transactionCost");
				        	
				        	DeliveryOrder cDeliveryOrder = new DeliveryOrder();
				        	cDeliveryOrder.date = date;
				        	cDeliveryOrder.time = time;
				        	if(tranAct.equals("BUY")) cDeliveryOrder.tranAct = TRANACT.BUY;
				        	if(tranAct.equals("SELL")) cDeliveryOrder.tranAct = TRANACT.SELL;
				        	cDeliveryOrder.stockID = stockID;
				        	cDeliveryOrder.amount = Integer.parseInt(amount);
				        	cDeliveryOrder.holdAvePrice = Float.parseFloat(holdAvePrice);
				        	cDeliveryOrder.tranPrice = Float.parseFloat(tranPrice);
				        	cDeliveryOrder.transactionCost = Float.parseFloat(transactionCost);
				        	deliveryOrderList.add(cDeliveryOrder);
			        	}
			        }
	        	}
		    }
		    
		    StoreEntity cStoreEntity = new StoreEntity();
		    cStoreEntity.money = money;
		    cStoreEntity.stockSelectList = stockSelectList;
		    cStoreEntity.commissionOrderList = commissionOrderList;
		    cStoreEntity.holdStockList = holdStockList;
		    cStoreEntity.deliveryOrderList = deliveryOrderList;
		    return cStoreEntity;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return null;
		}
	}

	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private String m_accountID;
	private String m_password;
	private String m_accXMLFile;
}
