package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stormstock.analysis.ANLPolicyBase;
import stormstock.analysis.ANLPolicyX1;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLStockPool;
import stormstock.analysis.ANLPolicyBase.RetExitCheck;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class RunSuccRateCheckByDays {
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
	static class DistriStockItem
	{
		public String id;
		public int iEnter;
		public RetExitCheck cRetExitCheck;
	}
	public static class DistributionItem implements Comparable
	{
		public DistributionItem()
		{
			date="";
			distriStockItemList =  new ArrayList<DistriStockItem>();
		}
		public String date;
		public List<DistriStockItem> distriStockItemList;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			DistributionItem sdto = (DistributionItem)arg0;
		    return this.date.compareTo(sdto.date);
		}
	}
	public static class CheckResult
	{
		public CheckResult()
		{
			distributionList = new ArrayList<DistributionItem>();
		}
		public List<DistributionItem> distributionList;
		public void add(ANLStock cANLStock, int iEnter, RetExitCheck cRetExitCheck)
		{
			String stockId = cANLStock.id;
			String enterDate = cANLStock.historyData.get(iEnter).date;
			float protit = cRetExitCheck.profitPer;
			int costDayCnt = cRetExitCheck.iExit-iEnter;
			
			// 找到 对应日期的 DistributionItem 对象, 没有就创建
			DistributionItem cDistributionItem = null;
			for(int iDate=0; iDate<distributionList.size();iDate++)
			{
				// 
				if(distributionList.get(iDate).date.contains(enterDate))
				{
					cDistributionItem =  distributionList.get(iDate);
					break;
				}
			}
			if(null == cDistributionItem)
			{
				cDistributionItem = new DistributionItem();
				cDistributionItem.date = enterDate;
				distributionList.add(cDistributionItem);
			}
			
			// 找到 对应日期的 DistriStockItem 对象, 没有就创建
			DistriStockItem cDistriStockItem = null;
			for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
			{
				DistriStockItem tmpObj = cDistributionItem.distriStockItemList.get(iStockItem);
				if(tmpObj.id.contains(stockId))
				{
					cDistriStockItem = tmpObj;
					break;
				}
			}
			if(null == cDistriStockItem)
			{
				cDistriStockItem = new DistriStockItem();
				cDistriStockItem.id = stockId;
				cDistributionItem.distriStockItemList.add(cDistriStockItem);
			}
			
			// 赋值
			cDistriStockItem.iEnter = iEnter;
			cDistriStockItem.cRetExitCheck = new RetExitCheck(cRetExitCheck);
			
			if(cRetExitCheck.hasEnoughDays)
			{
				fmt.format("   # Check StockID:%s EnterDate: %s profit:%.3f costDayCnt:%d\n", 
						stockId, enterDate, protit, costDayCnt);
			}
			else
			{
				fmt.format("   # Check StockID:%s EnterDate: %s Wait More day!!!\n", 
						stockId, enterDate, protit, costDayCnt);
			}
			
			
		}
		public void printResult(String FileName)
		{
			Collections.sort(distributionList);
			try
			{
				File cfile =new File(FileName);
				cfile.delete();
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				String wLine = "";
				
				wLine = String.format("================================================================\n");
				cOutputStream.write(wLine.getBytes());
		        int iOKCntSum = 0;
		        int iOKCostDayCntSum = 0;
		        float iOKProfitSum = 0.0f;
		        int iNGCntSum = 0;
		        int iNGCostDayCntSum = 0;
		        float iNGProfitSum = 0;
				for(int iDate=0; iDate<distributionList.size();iDate++)
				{
					DistributionItem cDistributionItem = distributionList.get(iDate);
					wLine = String.format("Check date:%s\n", cDistributionItem.date);
					cOutputStream.write(wLine.getBytes());
					for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
					{
						DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(iStockItem);
						String SuccFlag = "";
						if(cDistriStockItem.cRetExitCheck.hasEnoughDays)
						{
							if(cDistriStockItem.cRetExitCheck.profitPer > 0)
							{
								SuccFlag = "[OK]";
								iOKCntSum++;
								iOKCostDayCntSum = iOKCostDayCntSum + (cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter);
								iOKProfitSum = iOKProfitSum + cDistriStockItem.cRetExitCheck.profitPer;
							}
							else
							{
								SuccFlag = "[NG]";
								iNGCntSum++;
								iNGCostDayCntSum = iNGCostDayCntSum +  (cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter);
								iNGProfitSum = iNGProfitSum + cDistriStockItem.cRetExitCheck.profitPer;
							}
							wLine = String.format("    StockId:%s %s Profit:%f costDayCnt:%d\n", 
									cDistriStockItem.id, 
									SuccFlag, 
									cDistriStockItem.cRetExitCheck.profitPer, 
									(cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter));
						}
						else
						{
							wLine = String.format("    StockId:%s Wait More day!!!\n", cDistriStockItem.id);
						}
						
						cOutputStream.write(wLine.getBytes());
					}
				}
				
				float succRate = (float)iOKCntSum/(iOKCntSum+iNGCntSum);
				float OKAveCostDayCnt = (float)iOKCostDayCntSum/iOKCntSum;
				float OKAveProfit = iOKProfitSum/iOKCntSum;
				float NGAveCostDayCnt = (float)iNGCostDayCntSum/iNGCntSum;
				float NGAveProfit = iNGProfitSum/iNGCntSum;
				wLine = String.format("\n# ------------------------------------ #\n");
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				wLine = String.format("    suncRate:%.3f\n", succRate);
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				wLine = String.format("    OKAveCostDayCnt:%.3f OKAveProfit:%.3f\n", OKAveCostDayCnt, OKAveProfit);
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				wLine = String.format("    NGAveCostDayCnt:%.3f NGAveProfit:%.3f\n", NGAveCostDayCnt, NGAveProfit);
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				cOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println("Exception:" + e.getMessage()); 
			}
		}
	}
	
	public static CheckResult generateSuccDistribution(ANLPolicyBase cANLPolicyBase,
			List<StockItem> cStockList, int maxBeforeDayCnt)
	{
		CheckResult cCheckResult = new CheckResult();
		
		// 初始化分布列表，根据上证指数
		List<DayKData> retList999999 = new ArrayList<DayKData>();
		int retgetDayKDataQianFuQuan = DataEngine.getDayKDataQianFuQuan("999999", retList999999);
		if(0 == retgetDayKDataQianFuQuan)
		{
			// 初始化列表添加项目
			for(int i = 0; i < retList999999.size(); i++)  
	        {  
				DayKData cDayKData = retList999999.get(i);  
				//fmt.format("date:%s \n", cDayKData.date);  
				DistributionItem cDistributionItem = new DistributionItem();
				cDistributionItem.date = cDayKData.date;
				cCheckResult.distributionList.add(cDistributionItem);
	        } 
		}
		else
		{
			fmt.format("ERROR retgetDayKDataQianFuQuan:%d", retgetDayKDataQianFuQuan);
			return cCheckResult;
		}

		if(cStockList.size() != 0)
		{
			for(int iStock = 0; iStock < cStockList.size(); iStock++)  
	        {  
				StockItem cStockItem = cStockList.get(iStock);
				String stockId = cStockItem.id;
				
				// 对单只股票进行计算
				fmt.format("check for stockId:%s ...\n", stockId);
				ANLStock cANLStock = ANLStockPool.getANLStockNF(stockId);
				if(null == cANLStock)
				{
					continue;
				}
				
				int lenlist = cANLStock.historyData.size();
				int iBegin = 0;
				if(lenlist > maxBeforeDayCnt)
				{
					iBegin = lenlist-maxBeforeDayCnt;
				}
				for(; iBegin < lenlist; iBegin++) 
				{
					ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iBegin);
					if(cANLPolicyBase.enterCheck(cANLStock, iBegin))
					{
						RetExitCheck cRetExitCheck = cANLPolicyBase.exitCheck(cANLStock, iBegin);

						cCheckResult.add(cANLStock, iBegin, cRetExitCheck);
						
						iBegin = iBegin + 20;
					}
				}
	        } 
		}
		else
		{
			fmt.format("ERROR retgetAllStockList: NULL");
		}
		
		return cCheckResult;
	}
	
	
	
	public static void MockTrasection(CheckResult cCheckResult, String filename)
	{
		fmt.format("\nMockTrasection ------------------------------------->>>> \n");
		
		String curSelectStockID = "";
		ANLStock curANLStock = null;
		String curEnterDate = "";
		String curExitDate = "";
		int curCostDayCnt = 0;
		float curProfit = 0.0f;
		
		float myAcc = 1.0f;
		int myMockTrasectionOKCnt = 0;
		int myMockTrasectionNGCnt = 0;
		
		for(int i=0; i< cCheckResult.distributionList.size(); i++)
		{
			DistributionItem cDistributionItem = cCheckResult.distributionList.get(i);
			if(cDistributionItem.distriStockItemList.size() > 3)
			{
				List<Integer> cRandIntList = MoreRandomInt(cDistributionItem.distriStockItemList.size(),3);

				float onepartMyAcc = myAcc/3;
				myAcc = 0.0f;
				int curmaxCostCnt = 0;
				for(int cc =0; cc<3; cc++)
				{
					int randomIndex = cRandIntList.get(cc);
					DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(randomIndex);
					curSelectStockID = cDistriStockItem.id;
					curANLStock = ANLStockPool.getANLStockNF(curSelectStockID);
					curEnterDate = curANLStock.historyData.get(cDistriStockItem.iEnter).date;
					curExitDate = curANLStock.historyData.get(cDistriStockItem.cRetExitCheck.iExit).date;
					curProfit = cDistriStockItem.cRetExitCheck.profitPer;
					curCostDayCnt =cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter +1;
					if(curCostDayCnt > curmaxCostCnt)
						curmaxCostCnt = curCostDayCnt;
					fmt.format("Transection stock:%s enter:%s exit:%s profit:%.3f\n", 
							curSelectStockID, curEnterDate, curExitDate, curProfit);
					
					onepartMyAcc = onepartMyAcc*(1+curProfit);
					myAcc = myAcc + onepartMyAcc;
					
					if(curProfit>0)
					{
						myMockTrasectionOKCnt++;
					}
					else
					{
						myMockTrasectionNGCnt++;
					}
				}
				i = i + curmaxCostCnt;
			}
		}
		
		fmt.format("Transection End: myAcc: %.3f\n", myAcc);
		fmt.format("                 MockTrasection myMockTrasectionOKCnt: %d\n", myMockTrasectionOKCnt);
		fmt.format("                 MockTrasection myMockTrasectionNGCnt: %d\n", myMockTrasectionNGCnt);
		fmt.format("                 MockTrasection SuccRate: %.3f\n", 
				(float)myMockTrasectionOKCnt/(myMockTrasectionOKCnt+myMockTrasectionNGCnt));
	}
	
	static List<Integer> MoreRandomInt(int max, int cnt)
	{
		List<Integer> retList = new ArrayList<Integer>() ;
		
		//一共选择3个
		for(int i=0 ;i<cnt;i++)
		{
			//随机向后选取空位
			int randomInt = Math.abs(random.nextInt());
			int randomMoveCnt = randomInt % max;
			
			int icheckNum = 0;
			while(true)
			{
				// 检查当前这个数字是否未被选
				boolean bInList = false;
				for(int ick =0; ick<retList.size();ick++)
				{
					int tmpNb = retList.get(ick);
					if(tmpNb == icheckNum)
					{
						bInList = true;
						break;
					}
				}
				
				if(!bInList)
				{
					if(randomMoveCnt == 0)
					{
						retList.add(icheckNum);
						break;
					}
					randomMoveCnt--;
				}
				//检查下一个
				icheckNum++;
				icheckNum = icheckNum % max;
			}
		}
		return retList;
	}
	// 测试若干股票后iTestMaxDaysCnt天的成功率
	// 结果可以查看每天的进入点分布  与 最近的策略进入点
	public static void main(String[] args) {
		System.out.println("### Main Begin");
		
		///////////////////////////////////////////////////////////////////////////////////
		// run param 
		
		// param1: 策略
		ANLPolicyBase cPolicy = new ANLPolicyX1();
		// param2: 股票列表
		List<StockItem> retStockList = null;
		retStockList = DataWebStockAllList.getRandomStock(200); // 只测试若干随机
		//retStockList = new ArrayList<StockItem>(); // 测试所有股票
		//DataWebStockAllList.getAllStockList(retStockList);
		// param3: 测试向前最大天数
		int iTestMaxDaysCnt = 1000;
		
		///////////////////////////////////////////////////////////////////////////////////

		CheckResult cCheckResult = generateSuccDistribution(cPolicy, retStockList, iTestMaxDaysCnt);
		
		// 模拟交易
		MockTrasection(cCheckResult, "MockTrasection.txt");
		
		cCheckResult.printResult("SuccRate.txt");
		
		System.out.println("### Main End");
	}
}
