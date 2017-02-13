package stormstock.ori.capi;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.ori.capi.CATHSAccount.CommissionOrder;
import stormstock.ori.capi.CATHSAccount.DealOrder;
import stormstock.ori.capi.CATHSAccount.HoldStock;
import stormstock.ori.capi.CATHSAccount.ResultAllStockMarketValue;
import stormstock.ori.capi.CATHSAccount.ResultAvailableMoney;
import stormstock.ori.capi.CATHSAccount.ResultCommissionOrderList;
import stormstock.ori.capi.CATHSAccount.ResultDealOrderList;
import stormstock.ori.capi.CATHSAccount.ResultHoldStockList;
import stormstock.ori.capi.CATHSAccount.ResultTotalAssets;

public class TestCATHSAccount {

	// for test: please be carefull
	public static void main(String[] args) {
        // TODO Auto-generated method stub
        //System.getProperty("java.library.path");
		Formatter fmt = new Formatter(System.out);
		fmt.format("### main begin\n");
		
		// 初始化测试
		{
			int iInitRet = CATHSAccount.initialize();
	        fmt.format("CATHSAccount.initialize err(%d)\n", iInitRet);
		}
        
		// 可用资金测试
		{
	        ResultAvailableMoney cResultAvailableMoney = CATHSAccount.getAvailableMoney();
	        fmt.format("CATHSAccount.getAvailableMoney err(%d) AvailableMoney(%.2f)\n", cResultAvailableMoney.error, cResultAvailableMoney.availableMoney);
		}

		// 总资产测试
		{
	        ResultTotalAssets cResultTotalAssets = CATHSAccount.getTotalAssets();
	        fmt.format("CATHSAccount.getTotalAssets err(%d) TotalAssets(%.2f)\n", cResultTotalAssets.error, cResultTotalAssets.totalAssets);
		}

		// 所有股票总市值测试
		{
	        ResultAllStockMarketValue cResultAllStockMarketValue = CATHSAccount.getAllStockMarketValue();
	        fmt.format("CATHSAccount.getAllStockMarketValue err(%d) AvailableMoney(%.2f)\n", cResultAllStockMarketValue.error, cResultAllStockMarketValue.allStockMarketValue);
		}

		// 持股列表测试
		{
	        ResultHoldStockList cResultHoldStockList = CATHSAccount.getHoldStockList();
	        fmt.format("CATHSAccount.getHoldStockList err(%d) resultList size(%d)\n", cResultHoldStockList.error, cResultHoldStockList.resultList.size());
	        for(int i=0;i<cResultHoldStockList.resultList.size();i++)
	        {
	        	HoldStock cHoldStock = cResultHoldStockList.resultList.get(i);
	        	fmt.format("    {%s %d %d %.3f %.3f %.3f}\n", cHoldStock.stockID, cHoldStock.totalAmount, cHoldStock.availableAmount,
	        			cHoldStock.refProfitLoss, cHoldStock.refPrimeCostPrice, cHoldStock.curPrice);
	        }
		}

		// 当日委托单测试
		{
	        ResultCommissionOrderList cResultCommissionOrderList = CATHSAccount.getCommissionOrderList();
	        fmt.format("CATHSAccount.getCommissionOrderList err(%d) resultList size(%d)\n", cResultCommissionOrderList.error, cResultCommissionOrderList.resultList.size());
	        for(int i=0;i<cResultCommissionOrderList.resultList.size();i++)
	        {
	        	CommissionOrder cCommissionOrder = cResultCommissionOrderList.resultList.get(i);
	        	fmt.format("    {%s %s %s %d %.3f %d %.3f}\n", cCommissionOrder.time, cCommissionOrder.stockID, cCommissionOrder.tranAct.toString(),
	        			cCommissionOrder.commissionAmount, cCommissionOrder.commissionPrice, 
	        			cCommissionOrder.dealAmount, cCommissionOrder.dealPrice);
	        }
		}
        
		// 当日成交单测试
		{
	        ResultDealOrderList cResultDealOrderList = CATHSAccount.getDealOrderList();
	        fmt.format("CATHSAccount.getDealOrderList err(%d) resultList size(%d)\n", cResultDealOrderList.error, cResultDealOrderList.resultList.size());
	        for(int i=0;i<cResultDealOrderList.resultList.size();i++)
	        {
	        	DealOrder cDealOrder = cResultDealOrderList.resultList.get(i);
	        	fmt.format("    {%s %s %s %d %.3f}\n", cDealOrder.time, cDealOrder.stockID, cDealOrder.tranAct.toString(),
	        			cDealOrder.dealAmount, cDealOrder.dealPrice);
	        }
		}
        
		// 买入委托测试
		{
	        int iBuyRet = CATHSAccount.buyStock("601988", 100, 0.07f);
	        fmt.format("CATHSAccount.buyStock err(%d)\n", iBuyRet);
		}
        
		// 卖出委托测试
		{
	        int iSellRet = CATHSAccount.sellStock("601988", 100, 190.7f);
	        fmt.format("CATHSAccount.sellStock err(%d)\n", iSellRet);
		}

        fmt.format("### main end\n");
    }
}
