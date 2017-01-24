package stormstock.fw.tranbase.account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;

public class MockAccountOpe extends IAccountOpe {
	
	public MockAccountOpe(String accountID, String password)
	{
		super();
		
		m_transactionCostsRatio = 0.0016f;
		
		m_accountID = accountID;
		m_password = password;
				
		// ����������
		{
			m_money = 100000.00f;
			m_stockSelectList = new ArrayList<String>();
			m_commissionOrderList = new ArrayList<CommissionOrder>();
			m_holdStockList = new ArrayList<HoldStock>();
			m_deliveryOrderList = new ArrayList<DeliveryOrder>();
			syncAccountDataToMem();
		}

		BLog.output("ACCOUNT", "Account MOCK AccountID:%s Password:%s money:%.2f transactionCostsRatio:%.4f\n", 
				m_accountID, password, m_money, m_transactionCostsRatio);
	}
	
	@Override
	public boolean newDayInit(String date, String time) 
	{ 
		// ��һ��ʱ��δ�ɽ�ί�е����
		m_commissionOrderList.clear();
		
		// ��һ��ʱ�����гֹɾ��������ֲ�����+1
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.totalCanSell = cHoldStock.totalAmount;
			cHoldStock.holdDayCnt = cHoldStock.holdDayCnt + 1;
		}
		
		// ��һ��ʱ��������
		m_deliveryOrderList.clear();
		
		return true; 
	}

	@Override
	public int pushBuyOrder(String date, String time, String stockID, float price, int amount) {
		
		// ��������׼��
		int maxBuyAmount = (int)(m_money/price);
		int realBuyAmount = Math.min(maxBuyAmount, amount);
		realBuyAmount = realBuyAmount/100*100; 
		
		// ��ȡ���ж���
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.stockID == stockID)
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		if(null == cHoldStock)
		{
			HoldStock cNewHoldStock = new HoldStock();
			cNewHoldStock.stockID = stockID;
			cNewHoldStock.createDate = date;
			cNewHoldStock.createTime = time;
			cNewHoldStock.curPrice = price;
			m_holdStockList.add(cNewHoldStock);
			cHoldStock = cNewHoldStock;
		}
		
		// ���ö���
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		int oriTotalAmount = cHoldStock.totalAmount;
		float oriHoldAvePrice = cHoldStock.holdAvePrice;
		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount)/cHoldStock.totalAmount;
		cHoldStock.curPrice = price;

		cHoldStock.transactionCost = cHoldStock.transactionCost + transactionCosts;
		m_money = m_money - realBuyAmount*price;
		
		BLog.output("ACCOUNT", " @Buy [%s %s] [%s %.3f %d %.3f(%.3f) %.3f] \n", 
				date, time,
				stockID, price, realBuyAmount, price*realBuyAmount, transactionCosts, m_money);
		
		// ���ɽ��
		DeliveryOrder cDeliveryOrder = new DeliveryOrder();
		cDeliveryOrder.date = date;
		cDeliveryOrder.time = time;
		cDeliveryOrder.tranAct = TRANACT.BUY;
		cDeliveryOrder.stockID = stockID;
		cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
		cDeliveryOrder.tranPrice = price;
		cDeliveryOrder.amount = realBuyAmount;
		cDeliveryOrder.transactionCost = 0.0f; // ����Ľ��׷�����ȫ������ʱ����
		m_deliveryOrderList.add(cDeliveryOrder);
		
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String date, String time, String stockID, float price, int amount) {
		
		// ��ȡ���ж���
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.stockID.equals(stockID))
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		
		if(null != cHoldStock)
		{
			// ��������׼��
			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
			realSellAmount = realSellAmount/100*100;
			
			// ���ö���
			int oriTotalAmount = cHoldStock.totalAmount;
			float oriHoldAvePrice = cHoldStock.holdAvePrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			if(cHoldStock.totalAmount == 0) // �����򲻼�������۸� ����
			{
				cHoldStock.holdAvePrice = 0.0f;
			}
			else
			{
				cHoldStock.holdAvePrice = (oriHoldAvePrice*oriTotalAmount - price*realSellAmount)/cHoldStock.totalAmount;
			}
			cHoldStock.curPrice = price;
			m_money = m_money + price*realSellAmount;
			
			// ��ּ���
			float DeliveryOrder_transactionCost = 0.0f;
			if(cHoldStock.totalAmount == 0)
			{
				m_money = m_money - cHoldStock.transactionCost;
				DeliveryOrder_transactionCost = cHoldStock.transactionCost;
				m_holdStockList.remove(cHoldStock);
			}
			
			BLog.output("ACCOUNT", " @Sell [%s %s] [%s %.3f %d %.3f(%.3f) %.3f] \n", 
					date, time,
					stockID, price, realSellAmount, price*realSellAmount, cHoldStock.transactionCost, m_money);
			
			// ���ɽ��
			DeliveryOrder cDeliveryOrder = new DeliveryOrder();
			cDeliveryOrder.date = date;
			cDeliveryOrder.time = time;
			cDeliveryOrder.tranAct = TRANACT.SELL;
			cDeliveryOrder.stockID = stockID;
			cDeliveryOrder.holdAvePrice = oriHoldAvePrice;
			cDeliveryOrder.tranPrice = price;
			cDeliveryOrder.amount = realSellAmount;
			cDeliveryOrder.transactionCost = DeliveryOrder_transactionCost; // ����Ľ��׷�����ȫ������ʱ����
			m_deliveryOrderList.add(cDeliveryOrder);
			
			return realSellAmount;
		}
	
		return 0;
	}

	@Override
	public float getAvailableMoney() {
		return m_money;
	}

	@Override
	public void setStockSelectList(List<String> stockIDList) {
		m_stockSelectList.clear();
		for(int i=0; i<stockIDList.size();i++)
		{
			String newstockID = stockIDList.get(i);
			m_stockSelectList.add(newstockID);
		}
		
		// ѡ�����ų��Ѿ����е�
		List<HoldStock> cStockHoldList =  getHoldStockList(null,null);
		for(int i=0;i<cStockHoldList.size();i++)
		{
			m_stockSelectList.remove(cStockHoldList.get(i).stockID);
		}
	}

	@Override
	public List<String> getStockSelectList() {
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!help_inAccount(stockID))  // ѡ���б��ų����Ѿ��������б��
			{
				newList.add(stockID);
			}
		}
		return newList;
	}
	// �������� �жϹ�Ʊ�Ƿ������ ������ί���б������б���
	private boolean help_inAccount(String stockID)
	{
		List<CommissionOrder> cCommissionOrderList = this.getCommissionOrderList();
		for(int i=0;i<cCommissionOrderList.size();i++)
		{
			if(cCommissionOrderList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = this.getHoldStockList(null,null);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<CommissionOrder> getCommissionOrderList() {
		return m_commissionOrderList;
	}
	
	@Override
	public List<HoldStock> getHoldStockList(String date, String time) {
		// ��������ʱ�����ʱ�����³ֹ��ּ�
		if(null != date && null != time)
		{
			for(int i = 0; i< m_holdStockList.size(); i++)
			{
				HoldStock cHoldStock = m_holdStockList.get(i);
				ResultStockTime cResultStockTime = GlobalUserObj.getCurStockDataIF().getStockTime(cHoldStock.stockID, date, time);
				if(0 == cResultStockTime.error)
				{
					cHoldStock.curPrice = cResultStockTime.stockTime.price;
				}
			}
		}
		return m_holdStockList;
	}
	
	@Override
	public List<DeliveryOrder> getDeliveryOrderList() {
		return m_deliveryOrderList;
	}
	
	private void syncAccountDataToFile()
	{
		
	}
	
	private void syncAccountDataToMem()
	{
		BLog.output("ACCOUNT", "syncAccountDataToMem \n");
		
		String accXMLFile = "account\\account_" + m_accountID + ".xml";
		String xmlStr = "";
		File cfile=new File(accXMLFile);
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
				return;
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader sr = new StringReader(xmlStr);
		    InputSource is = new InputSource(sr);
		    Document doc = builder.parse(is);
		    Element rootElement = doc.getDocumentElement();
		    
		    // ��鷵��������Ч��
		    if(!rootElement.getTagName().contains("account")) 
		    	return;
		    
		    // �˻������жϲ�����
		    String accountID = rootElement.getAttribute("ID");
		    String password = rootElement.getAttribute("password");
		    if(!accountID.equals(m_accountID) || !password.equals(m_password))
		    	return;
		    String acc_date = rootElement.getAttribute("date");
		    BLog.output("ACCOUNT", "accountID:%s password:%s acc_date:%s \n", accountID, password, acc_date);
	
		    // Ǯ����
		    {
		    	NodeList nodelist_Money = rootElement.getElementsByTagName("Money");
		    	if(nodelist_Money.getLength() == 1)
		    	{
		    		Node Node_Money = nodelist_Money.item(0);
		    		String total = ((Element)Node_Money).getAttribute("total");
		    		m_money = Float.parseFloat(total);
		    	}
		    }
		    
		    // ѡ���б����
		    m_stockSelectList.clear();
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
				        	BLog.output("ACCOUNT", "stockID:%s \n", stockID);
				        	m_stockSelectList.add(stockID); 
			        	}
			        }
	        	}
		    }
		    
		    // ί�е�����
		    m_commissionOrderList.clear();
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
				        	m_commissionOrderList.add(cCommissionOrder);
			        	}
			        }
	        	}
		    }
		    
		    // ������� 
		    m_deliveryOrderList.clear();
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
				        	m_deliveryOrderList.add(cDeliveryOrder);
			        	}
			        }
	        	}
		    }
		    
		    // �ֹɼ���
		    m_holdStockList.clear();
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
				        	m_holdStockList.add(cHoldStock);
			        	}
			        }
	        	}
		    }
		    // ȫ���������
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return;
		}
	}
	
	/**
	 * ��Ա-----------------------------------------------------------------------
	 */
	
	private float m_transactionCostsRatio;
	private String m_accountID;
	private String m_password;
	
	private float m_money;
	private List<String> m_stockSelectList; // ѡ���б�
	private List<CommissionOrder> m_commissionOrderList; // ģ���˻���  �µ�ֱ�ӳɽ�, ί�е�һֱδ��
	private List<HoldStock> m_holdStockList;
	private List<DeliveryOrder> m_deliveryOrderList;
}
