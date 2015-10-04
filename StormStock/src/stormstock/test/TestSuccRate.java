package stormstock.test;

import stormstock.analysis.ANLPolicy;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLStockPool;
import stormstock.data.WebStockAllList;
import stormstock.data.WebStockAllList.StockItem;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

public class TestSuccRate {
	public static class ProfitResult
	{
		public ProfitResult()
		{
			id = "000000";
			succCnt = 0;
			failCnt = 0;
		}
		public String id;
		public int succCnt;
		public int failCnt;
	}
	public static boolean CheckProfit(ANLStock cANLStock, int iEnter, ProfitResult cProfitResult)
	{
		int iCheckDayBegin = iEnter;
		int iCheckDayEnd = iEnter+40;
		float currentPrice = cANLStock.historyData.get(iCheckDayBegin).close;
		for(int iCheckDay = iCheckDayBegin; iCheckDay <iCheckDayEnd; iCheckDay++)
		{
			ANLStockDayKData checkDayKData = cANLStock.historyData.get(iCheckDay);
			float highDay = checkDayKData.high;
			float lowDay = checkDayKData.low;
			float midDay = (highDay + lowDay)/2;
			float profit = (midDay-currentPrice)/currentPrice;
			if(profit>0.1)
			{
				cProfitResult.succCnt = cProfitResult.succCnt + 1;
				return true;
			}
			if(profit<-0.1)
			{
				cProfitResult.failCnt = cProfitResult.failCnt + 1;
				return false;
			}   	
		}
		cProfitResult.failCnt = cProfitResult.failCnt + 1;
		return false;
	}
	public static StockItem popRandomStock(List<StockItem> in_list)
	{
		if(in_list.size() == 0) return null;
		Random random = new Random();
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockItem cStockItem = new  StockItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockItem;
	}
	public static List<StockItem> getRandomStock(int count)
	{
		List<StockItem> retList = new ArrayList<StockItem>();
		
		List<StockItem> retListAll = new ArrayList<StockItem>();
		int ret = WebStockAllList.getAllStockList(retListAll);
		if(0 == ret)
		{
			for(int i = 0; i < count; i++)  
	        {  
				StockItem cStockItem = popRandomStock(retListAll);
				retList.add(cStockItem);
	        } 
		}
		else
		{
		}
		return retList;
	}
	
	public static void Check()
	{
		List<StockItem> cStockList = getRandomStock(20);
		
//		cStockList.clear();
//		cStockList.add(new StockItem("002246"));
//		cStockList.add(new StockItem("000421"));
//		cStockList.add(new StockItem("002118"));
//		cStockList.add(new StockItem("600030"));
//		cStockList.add(new StockItem("600818"));
//		cStockList.add(new StockItem("601766"));
		
		Formatter fmt = new Formatter(System.out);

		List<ProfitResult> cListProfitResult = new ArrayList<ProfitResult>();
		
		for(int i=0; i<cStockList.size();i++)
		{
			ProfitResult cProfitResult = new ProfitResult();
			
			String stockId = cStockList.get(i).id;
		
			ANLStock cANLStock = ANLStockPool.getANLStockNF(stockId);
			if(null == cANLStock)
			{
				continue;
			}
			fmt.format("=======================>Stock Checking: %s\n", stockId);
			cProfitResult.id = stockId;
			int lenlist = cANLStock.historyData.size();
			if(lenlist < 200)
			    continue;
			int iCheckDayBegin = 100;
			int iCheckDayEnd = lenlist-60;
			for(int iIndex = iCheckDayBegin; iIndex < iCheckDayEnd; iIndex++) 
			{
				ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iIndex);
				if(ANLPolicy.enterCheck(cANLStock, iIndex))
				{
					fmt.format("    EnterDate: %s", cCheckDayKData.date);
					if(CheckProfit(cANLStock, iIndex, cProfitResult))
					{
						fmt.format(" OK!\n");
					}
					else
					{
						fmt.format(" NG!\n");
					}
				}
			}
			
			cListProfitResult.add(cProfitResult);
		}
		
		fmt.format("======================================================\n");
		int allSucc = 0;
		int allFail = 0;
		for(int iIndex = 0; iIndex < cListProfitResult.size(); iIndex++) 
		{
			ProfitResult cProfitResult = cListProfitResult.get(iIndex);
			fmt.format("StockId:%s succCnt:%d failCnt:%d\n", cProfitResult.id, cProfitResult.succCnt, cProfitResult.failCnt); 
			allSucc = allSucc + cProfitResult.succCnt;
			allFail = allFail + cProfitResult.failCnt;
		}
		float succRate = 0.0f;
		if((allSucc+allFail)!=0)
		{
			succRate = (float)allSucc/(float)(allSucc+allFail)*100;
		}
		fmt.format("succRate:%.3f%%\n", succRate);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("### Main Begin");
		
		Check();

		System.out.println("### Main End");
	}
}
