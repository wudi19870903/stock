package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.base.BTypeDefine.RefFloat;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.account.AccountStore.StoreEntity;

public class Account {
	
	// 构造账户实体时，需要传入操作接口（模拟，真实）
	public Account(ACCOUNTTYPE eAccType, String accountID, String password)
	{
		IAccountOpe cIAccountOpe = null;
		if(eAccType == ACCOUNTTYPE.MOCK)
		{
			cIAccountOpe = new MockAccountOpe(accountID, password, true);
		} 
		else if(eAccType == ACCOUNTTYPE.REAL)
		{
			cIAccountOpe = new RealAccountOpe(accountID, password);
		}
		m_cIAccountOpe = cIAccountOpe;
		m_holdStockInvestigationDaysMap = new HashMap<String, Integer>();
		m_stockSelectList = new ArrayList<String>();
		m_accountStore = new AccountStore(accountID, password);
		
		preLoad(); // 加载一次股票调查天数表
		
		load(); // 加载选股表
		store(); // 存储选股表
	}
	
	// ***********************************************************************
	// 基本接口，直接调用操作接口
	
	// 隔日开始账户初始化
	public int newDayInit(String date, String time)
	{
		int iNewDayInit = m_cIAccountOpe.newDayInit(date, time);
		
		load();
		
		return iNewDayInit;
	}
	
	public int newDayTranEnd(String date, String time)
	{
		int iNewDayTranEnd = m_cIAccountOpe.newDayTranEnd(date, time);
		
		if(0 == iNewDayTranEnd)
		{
			// 更新调查天数map
			Map<String, Integer> newHoldStockInvestigationDaysMap = new HashMap<String, Integer>();
			
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iGetHoldStockList = getHoldStockList(date, time, cHoldStockList);
			if(0 == iGetHoldStockList)
			{
				for(int i=0; i<cHoldStockList.size();i++)
				{
					HoldStock cHoldStock = cHoldStockList.get(i);
					newHoldStockInvestigationDaysMap.put(cHoldStock.stockID, 0);
				}
				for(Map.Entry<String, Integer> entry:newHoldStockInvestigationDaysMap.entrySet()){   
					String key = entry.getKey();
					int iInvestigationDays = 0;
					if(m_holdStockInvestigationDaysMap.containsKey(key))
					{
						iInvestigationDays = m_holdStockInvestigationDaysMap.get(key);
					}
					entry.setValue(iInvestigationDays);
				} 
				for(Map.Entry<String, Integer> entry:newHoldStockInvestigationDaysMap.entrySet()){   
					int iInvestigationDays = entry.getValue();
					entry.setValue(iInvestigationDays+1);
				} 
				m_holdStockInvestigationDaysMap.clear();
				m_holdStockInvestigationDaysMap.putAll(newHoldStockInvestigationDaysMap);
			}
			else
			{
				iNewDayTranEnd = -201;
			}
		}
		
		return iNewDayTranEnd; 
	}
	
	// 推送买单委托，返回实际下单量
	public int pushBuyOrder(String date, String time, String id, int amount, float price)
	{
		return m_cIAccountOpe.pushBuyOrder(date, time, id, amount, price);
	}
	
	// 推送卖单委托，返回实际下单量
	public int pushSellOrder(String date, String time, String id, int amount, float price)
	{
		return m_cIAccountOpe.pushSellOrder(date, time, id, amount, price);
	}
	
	// 获得账户可用资金（现金）
	public int getAvailableMoney(RefFloat out_availableMoney)
	{
		return m_cIAccountOpe.getAvailableMoney(out_availableMoney);
	}
	
	// 获得委托列表(未成交的，包含买入和卖出的)
	public int getCommissionOrderList(List<CommissionOrder> out_list)
	{
		return m_cIAccountOpe.getCommissionOrderList(out_list);
	}
	
	// 获得持股列表（包含已经持有的，与当天下单成交的）
	public int getHoldStockList(String date, String time, List<HoldStock> out_list)
	{
		int iGetHoldStockList = m_cIAccountOpe.getHoldStockList(date, time, out_list);
		if(0 == iGetHoldStockList)
		{
			for(int i=0;i<out_list.size();i++)
	        {
	        	HoldStock cHoldStock = out_list.get(i);
	        	if(m_holdStockInvestigationDaysMap.containsKey(cHoldStock.stockID))
	        	{
	        		cHoldStock.investigationDays = m_holdStockInvestigationDaysMap.get(cHoldStock.stockID);
	        	}
	        	else
	        	{
	        		cHoldStock.investigationDays = 0;
	        	}
	        }
		}
		return iGetHoldStockList;
	}
	
	// 获得当日交割单列表（已成交的，包含买入和卖出的）
	public int getDealOrderList(List<DealOrder> out_list)
	{
		return m_cIAccountOpe.getDealOrderList(out_list);
	}
		
	// ***********************************************************************
	// 扩展接口，用于实现在基础功能之上的扩展
	
	// 选股列表设置
	public int setStockSelectList(List<String> stockIDList)
	{
		m_stockSelectList.clear();
		for(int i=0; i<stockIDList.size();i++)
		{
			String newstockID = stockIDList.get(i);
			m_stockSelectList.add(newstockID);
		}
		
		// 选股中排除已经持有的
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		getHoldStockList(null,null,cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			m_stockSelectList.remove(cHoldStockList.get(i).stockID);
		}
		
		store();
		
		return 0;
	}
	// 选股列表获取
	public int getStockSelectList(List<String> out_list)
	{
		out_list.clear();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!help_inAccount(stockID))  // 选股列表排除掉已经在买入列表的
			{
				out_list.add(stockID);
			}
		}
		return 0;
	}
	// 帮助函数判断股票是否存在于 买卖单委托列表，持有列表中
	private boolean help_inAccount(String stockID)
	{
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
		for(int i=0;i<cCommissionOrderList.size();i++)
		{
			if(cCommissionOrderList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		this.getHoldStockList(null,null,cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		return false;
	}
	
	// 获得买委托列表(未成交的)
	public List<CommissionOrder> getBuyCommissionOrderList()
	{
		List<CommissionOrder> cBuyCommissionOrderList = new ArrayList<CommissionOrder>();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.BUY)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				cBuyCommissionOrderList.add(cNewCommissionOrder);
			}
		}
		return cBuyCommissionOrderList;
	}
	
	// 获得卖委托列表(未成交的)
	public List<CommissionOrder> getSellCommissionOrderList()
	{
		List<CommissionOrder> cSellCommissionOrderList = new ArrayList<CommissionOrder>();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.SELL)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				cSellCommissionOrderList.add(cNewCommissionOrder);
			}
		}
		return cSellCommissionOrderList;
	}
	
	// 获得买交割单列表(已成交的)
	public List<DealOrder> getBuyDealOrderList()
	{
		List<DealOrder> cBuyDealOrderList = new ArrayList<DealOrder>();
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		getDealOrderList(cDealOrderList);
		for(int i= 0;i<cDealOrderList.size();i++)
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			if(cDealOrder.tranAct == TRANACT.BUY)
			{
				DealOrder cNewcDealOrder = new DealOrder();
				cNewcDealOrder.CopyFrom(cDealOrder);
				cBuyDealOrderList.add(cNewcDealOrder);
			}
		}
		return cBuyDealOrderList;
	}
	
	// 获得卖交割单列表(已成交的)
	public List<DealOrder> getSellDealOrderList()
	{
		List<DealOrder> cSellDealOrderList = new ArrayList<DealOrder>();
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		getDealOrderList(cDealOrderList);
		for(int i= 0;i<cDealOrderList.size();i++)
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			if(cDealOrder.tranAct == TRANACT.SELL)
			{
				DealOrder cNewDealOrder = new DealOrder();
				cNewDealOrder.CopyFrom(cDealOrder);
				cSellDealOrderList.add(cNewDealOrder);
			}
		}
		return cSellDealOrderList;
	}
	
	// 获得账户总资产
	public float getTotalAssets(String date, String time) {
		
		float all_marketval = 0.0f;
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		getHoldStockList(date, time, cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			all_marketval = all_marketval + cHoldStock.curPrice*cHoldStock.totalAmount;
		}
		RefFloat availableMoney = new RefFloat();
		getAvailableMoney(availableMoney);
		float all_asset = all_marketval + availableMoney.value;
		return all_asset;
	}
	
	// 只加载股票考察天数表
	private void preLoad()
	{
		StoreEntity cStoreEntity = m_accountStore.load();
		if(null != cStoreEntity)
		{
			m_holdStockInvestigationDaysMap.clear();
			if(null != cStoreEntity.initHoldStockInvestigationDaysMap)
		    	m_holdStockInvestigationDaysMap.putAll(cStoreEntity.initHoldStockInvestigationDaysMap);
		}
	}
	// 加载选股表
	private void load()
	{
		StoreEntity cStoreEntity = m_accountStore.load();
		if(null != cStoreEntity)
		{
		    m_stockSelectList.clear();
		    if(null != cStoreEntity.stockSelectList)
		    	m_stockSelectList.addAll(cStoreEntity.stockSelectList);
		}
	}
	// 保存选股表
	private void store()
	{
		StoreEntity cStoreEntity = new StoreEntity();
		cStoreEntity.stockSelectList = m_stockSelectList;
		m_accountStore.store(cStoreEntity);
	}
	
	public void printAccount(String date, String time)
	{
		BLog.output("ACCOUNT", "    ---ACCOUNT---INFO---\n");
		float fTotalAssets = this.getTotalAssets(date, time);
		RefFloat availableMoney = new RefFloat();
		this.getAvailableMoney(availableMoney);
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		this.getHoldStockList(date, time, cHoldStockList);
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		this.getDealOrderList(cDealOrderList);
		
		// 打印资产
		BLog.output("ACCOUNT", "    -TotalAssets: %.3f\n", fTotalAssets);
		BLog.output("ACCOUNT", "    -AvailableMoney: %.3f\n", availableMoney.value);
		
		// 打印持股
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			BLog.output("ACCOUNT", "    -HoldStock: %s %d %d %.3f %.3f %.3f %d\n", 
					cHoldStock.stockID,
					cHoldStock.totalAmount, cHoldStock.availableAmount,
					cHoldStock.refPrimeCostPrice, cHoldStock.curPrice, cHoldStock.totalAmount*cHoldStock.curPrice, 
					cHoldStock.investigationDays);
		}
		
		// 打印成交单
		for(int i=0; i<cDealOrderList.size(); i++ )
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cDealOrder.tranAct == TRANACT.SELL ) tranOpe = "SELL";
				
			BLog.output("ACCOUNT", "    -DealOrder: %s %s %s %d %.3f\n", 
					cDealOrder.time, tranOpe, cDealOrder.stockID, 
					cDealOrder.amount, cDealOrder.price);
		}
		
		// 选股
		if(m_stockSelectList.size() > 0)
		{
			String logStr = "";
			logStr += String.format("    -SelectList:[ ");
			for(int i=0; i<m_stockSelectList.size(); i++ )
			{
				String stockId = m_stockSelectList.get(i);
				logStr += String.format("%s ", stockId);
				if (i >= 7 && m_stockSelectList.size()-1 > 16) {
					logStr += String.format("... ", stockId);
					break;
				}
			}
			logStr += String.format("]");
			BLog.output("ACCOUNT", "%s\n", logStr);
		}
	}

	/** **********************************************************************
	 * 账户操作接口，可以设置为模拟或真实
	 */
	private IAccountOpe m_cIAccountOpe;
	private Map<String, Integer> m_holdStockInvestigationDaysMap;
	private List<String> m_stockSelectList; // 选股列表
	private AccountStore m_accountStore;
}
