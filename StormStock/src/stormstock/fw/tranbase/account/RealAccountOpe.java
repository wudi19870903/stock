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
		int iInitRet = CATHSAccount.initialize();
		BLog.output("ACCOUNT", " @RealAccountOpe Construct AccountID:%s Password:%s err(%d)\n", 
				accountID, password, iInitRet);
		
	}
	
	@Override
	public int newDayInit(String date, String time) {
		int iInitRet = CATHSAccount.initialize();
		BLog.output("ACCOUNT", " @RealAccountOpe newDayInit err(%d) \n", 
				date, time,
				iInitRet);
		return iInitRet;
	}

	@Override
	public int newDayTranEnd(String date, String time) {
		// do nothing
		return 0;
	}
	
	@Override
	public int pushBuyOrder(String date, String time, String id, int amount, float price) {
		int iBuyRet = CATHSAccount.buyStock(id, amount, price);
		BLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %s] [%s %d %.3f %.3f] \n", 
				date, time,
				iBuyRet, id, amount, price, amount*price);
		return iBuyRet;
	}

	@Override
	public int pushSellOrder(String date, String time, String id, int amount, float price) {
		int iSellRet = CATHSAccount.sellStock(id, amount, price);
		BLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %s] [%s %d %.3f %.3f] \n", 
				date, time,
				iSellRet, id, amount, price, amount*price);
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
}
