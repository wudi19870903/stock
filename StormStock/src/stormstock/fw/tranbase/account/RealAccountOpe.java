package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BTypeDefine.RefFloat;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.ori.capi.CATHSAccount;
import stormstock.ori.capi.CATHSAccount.ResultAvailableMoney;
import stormstock.ori.capi.CATHSAccount.ResultCommissionOrderList;
import stormstock.ori.capi.CATHSAccount.ResultDealOrderList;
import stormstock.ori.capi.CATHSAccount.ResultHoldStockList;

public class RealAccountOpe extends IAccountOpe {

	public RealAccountOpe(String accountID, String password)
	{
		m_stockSelectList = new ArrayList<String>();
		m_holdStockInvestigationDaysMap = new HashMap<String, Integer>();
		
		BLog.output("ACCOUNT", " @RealAccountOpe Construct AccountID:%s Password:%s\n", 
				accountID, password);
	}
	
	@Override
	public int newDayInit(String date, String time) {
		int iInitRet = CATHSAccount.initialize();
		BLog.output("ACCOUNT", " @RealAccountOpe newDayInit err(%d) \n", 
				date, time,
				iInitRet);
		
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
			iInitRet = -201;
		}
		
		return iInitRet;
	}

	@Override
	public int pushBuyOrder(String date, String time, String id, float price, int amount) {
		int iBuyRet = CATHSAccount.buyStock(id, amount, price);
		BLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %s] [%s %.3f %d %.3f] \n", 
				date, time,
				iBuyRet, id, price, amount, price*amount);
		return iBuyRet;
	}

	@Override
	public int pushSellOrder(String date, String time, String id, float price, int amount) {
		int iSellRet = CATHSAccount.sellStock(id, amount, price);
		BLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %s] [%s %.3f %d %.3f] \n", 
				date, time,
				iSellRet, id, price, amount, price*amount);
		return 0;
	}

	@Override
	public int getAvailableMoney(RefFloat out_availableMoney) {
		ResultAvailableMoney cResultAvailableMoney = CATHSAccount.getAvailableMoney();
		out_availableMoney.value = cResultAvailableMoney.availableMoney;
		BLog.output("ACCOUNT", " @RealAccountOpe getAvailableMoney err(%d) availableMoney(%.3f) \n", 
				cResultAvailableMoney.error, cResultAvailableMoney.availableMoney);
		return cResultAvailableMoney.error;
	}

	@Override
	public int setStockSelectList(List<String> stockIDList) {
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
		
		return 0;
	}

	@Override
	public int getStockSelectList(List<String> out_list) {
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
	// 帮助函数 判断股票是否存在于 买卖单委托列表，持有列表中
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
	
	@Override
	public int getCommissionOrderList(List<CommissionOrder> out_list) {
		
		out_list.clear();
		
		ResultCommissionOrderList cResultCommissionOrderList = CATHSAccount.getCommissionOrderList();
		BLog.output("ACCOUNT", " @RealAccountOpe getCommissionOrderList err(%d) CommissionOrderList size(%d) \n", 
				cResultCommissionOrderList.error, cResultCommissionOrderList.resultList.size());
		
		for(int i=0;i<cResultCommissionOrderList.resultList.size();i++)
        {
			stormstock.ori.capi.CATHSAccount.CommissionOrder cCommissionOrder = cResultCommissionOrderList.resultList.get(i);
			
			CommissionOrder cNewItem = new CommissionOrder();
			cNewItem.time = cCommissionOrder.time;
			cNewItem.stockID = cCommissionOrder.stockID;
			if (cCommissionOrder.tranAct == stormstock.ori.capi.CATHSAccount.TRANACT.BUY)
			{
				cNewItem.tranAct = TRANACT.BUY;
			}
			else if(cCommissionOrder.tranAct == stormstock.ori.capi.CATHSAccount.TRANACT.SELL)
			{
				cNewItem.tranAct = TRANACT.SELL;
			}
			cNewItem.amount = cCommissionOrder.commissionAmount;
			cNewItem.price = cCommissionOrder.commissionPrice;

			out_list.add(cNewItem);
        }
        
		return cResultCommissionOrderList.error;
	}

	@Override
	public int getHoldStockList(String date, String time, List<HoldStock> out_list) {
		
		out_list.clear();
		
		ResultHoldStockList cResultHoldStockList = CATHSAccount.getHoldStockList();
		BLog.output("ACCOUNT", " @RealAccountOpe getHoldStockList err(%d) HoldStockList size(%d) \n", 
				cResultHoldStockList.error, cResultHoldStockList.resultList.size());
		
        for(int i=0;i<cResultHoldStockList.resultList.size();i++)
        {
        	stormstock.ori.capi.CATHSAccount.HoldStock cHoldStock = cResultHoldStockList.resultList.get(i);
        	
        	HoldStock cNewItem = new HoldStock();
        	cNewItem.stockID = cHoldStock.stockID;
        	cNewItem.totalAmount = cHoldStock.totalAmount;
        	cNewItem.availableAmount = cHoldStock.availableAmount;
        	cNewItem.refPrimeCostPrice = cHoldStock.refPrimeCostPrice;
        	cNewItem.curPrice = cHoldStock.curPrice;
        	cNewItem.investigationDays = 0;
        	if(m_holdStockInvestigationDaysMap.containsKey(cNewItem.stockID))
        	{
        		cNewItem.investigationDays = m_holdStockInvestigationDaysMap.get(cNewItem.stockID);
        	}

			out_list.add(cNewItem);
        }
	        
		
		return cResultHoldStockList.error;
	}

	@Override
	public int getDealOrderList(List<DealOrder> out_list) {
		
		out_list.clear();
		
        ResultDealOrderList cResultDealOrderList = CATHSAccount.getDealOrderList();
		BLog.output("ACCOUNT", " @RealAccountOpe getDealOrderList err(%d) HoldStockList size(%d) \n", 
				cResultDealOrderList.error, cResultDealOrderList.resultList.size());
		
        for(int i=0;i<cResultDealOrderList.resultList.size();i++)
        {
        	stormstock.ori.capi.CATHSAccount.DealOrder cDealOrder = cResultDealOrderList.resultList.get(i);

        	DealOrder cNewItem = new DealOrder();
        	cNewItem.time = cDealOrder.time;
        	cNewItem.stockID = cDealOrder.stockID;
			if (cDealOrder.tranAct == stormstock.ori.capi.CATHSAccount.TRANACT.BUY)
			{
				cNewItem.tranAct = TRANACT.BUY;
			}
			else if(cDealOrder.tranAct == stormstock.ori.capi.CATHSAccount.TRANACT.SELL)
			{
				cNewItem.tranAct = TRANACT.SELL;
			}
        	cNewItem.amount = cDealOrder.dealAmount;
        	cNewItem.price = cDealOrder.dealPrice;
    		
        	out_list.add(cNewItem);
        }
        
		return cResultDealOrderList.error;
	}
	
	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private List<String> m_stockSelectList; // 选股列表
	
	private Map<String, Integer> m_holdStockInvestigationDaysMap;
}
