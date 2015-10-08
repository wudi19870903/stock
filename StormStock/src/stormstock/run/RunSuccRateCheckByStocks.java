package stormstock.run;

import stormstock.analysis.ANLPolicyBase;
import stormstock.analysis.ANLPolicyBase.RetExitCheck;
import stormstock.analysis.ANLPolicyX1;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLStockPool;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

public class RunSuccRateCheckByStocks {
	
	public static Formatter fmt = new Formatter(System.out);
	
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
		if(0 != count)
		{
			List<StockItem> retListAll = new ArrayList<StockItem>();
			int ret = DataWebStockAllList.getAllStockList(retListAll);
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
		}
		return retList;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("### Main Begin");
		
		ANLPolicyBase cPolicy = new ANLPolicyX1();
		List<StockItem> cStockList = new ArrayList<StockItem>();
//		cStockList.add(new StockItem("600452"));
//		cStockList.add(new StockItem("600020"));
// 		cStockList.add(new StockItem("002344"));
//		cStockList.add(new StockItem("002695"));
//		cStockList.add(new StockItem("300041"));
//		cStockList.add(new StockItem("600030"));
		if(cStockList.size() <= 0)
		{
			cStockList = getRandomStock(30);
		}

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
			int iCheckDayBegin = 0;
			int iCheckDayEnd = lenlist-100;
			for(int iIndex = iCheckDayBegin; iIndex < iCheckDayEnd; iIndex++) 
			{
				ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iIndex);
				if(cPolicy.enterCheck(cANLStock, iIndex))
				{
					RetExitCheck cRetExitCheck = cPolicy.exitCheck(cANLStock, iIndex);
					if(cRetExitCheck.profitPer < 0.0f)
					{
						cProfitResult.failCnt = cProfitResult.failCnt + 1;
						fmt.format("   # EnterDate: %s  [NG] ExitDate:%s profit:%.3f\n", 
								cCheckDayKData.date, 
								cANLStock.historyData.get(cRetExitCheck.iExit).date,
								cRetExitCheck.profitPer);
					}
					else
					{
						cProfitResult.succCnt = cProfitResult.succCnt + 1;
						fmt.format("   # EnterDate: %s  [OK] ExitDate:%s profit:%.3f\n", 
								cCheckDayKData.date, 
								cANLStock.historyData.get(cRetExitCheck.iExit).date,
								cRetExitCheck.profitPer);
					}
					iIndex = iIndex + 40;
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

		System.out.println("### Main End");
	}
}
