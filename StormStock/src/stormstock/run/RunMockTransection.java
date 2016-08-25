package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.run.RunSuccRateCheckByDays.CheckResult;
import stormstock.run.RunSuccRateCheckByDays.DistriStockItem;
import stormstock.run.RunSuccRateCheckByDays.DistributionItem;

public class RunMockTransection {
//
//	public static Random random = new Random();
//	public static Formatter fmt = new Formatter(System.out);
//	
//	
//	///////////////////////////////////////////////////////////////////////////
//	
//	static List<Integer> MoreRandomInt(int max, int cnt)
//	{
//		List<Integer> retList = new ArrayList<Integer>() ;
//		
//		//一共选择3个
//		for(int i=0 ;i<cnt;i++)
//		{
//			//随机向后选取空位
//			int randomInt = Math.abs(random.nextInt());
//			int randomMoveCnt = randomInt % max;
//			
//			int icheckNum = 0;
//			while(true)
//			{
//				// 检查当前这个数字是否未被选
//				boolean bInList = false;
//				for(int ick =0; ick<retList.size();ick++)
//				{
//					int tmpNb = retList.get(ick);
//					if(tmpNb == icheckNum)
//					{
//						bInList = true;
//						break;
//					}
//				}
//				
//				if(!bInList)
//				{
//					if(randomMoveCnt == 0)
//					{
//						retList.add(icheckNum);
//						break;
//					}
//					randomMoveCnt--;
//				}
//				//检查下一个
//				icheckNum++;
//				icheckNum = icheckNum % max;
//			}
//		}
//		return retList;
//	}
//	// 同时出现多个介入点时 随机介入
//	public static void MockTrasection_random(CheckResult cCheckResult, String filename)
//	{
//		fmt.format("\nMockTrasection ------------------------------------->>>> \n");
//		
//		String curSelectStockID = "";
//		ANLStock curANLStock = null;
//		String curEnterDate = "";
//		String curExitDate = "";
//		int curCostDayCnt = 0;
//		float curProfit = 0.0f;
//		
//		float myAcc = 1.0f;
//		int myMockTrasectionOKCnt = 0;
//		int myMockTrasectionNGCnt = 0;
//		
//		for(int i=0; i< cCheckResult.distributionList.size(); i++)
//		{
//			DistributionItem cDistributionItem = cCheckResult.distributionList.get(i);
//			if(cDistributionItem.distriStockItemList.size() > 3)
//			{
//				List<Integer> cRandIntList = MoreRandomInt(cDistributionItem.distriStockItemList.size(),3);
//
//				float onepartMyAcc = myAcc/3;
//				myAcc = 0.0f;
//				int curmaxCostCnt = 0;
//				for(int cc =0; cc<3; cc++)
//				{
//					int randomIndex = cRandIntList.get(cc);
//					DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(randomIndex);
//					curSelectStockID = cDistriStockItem.stockId;
//					curANLStock = ANLStockPool.getANLStockNF(curSelectStockID);
//					curEnterDate = curANLStock.historyData.get(cDistriStockItem.iEnter).date;
//					curExitDate = curANLStock.historyData.get(cDistriStockItem.cRetExitCheck.iExit).date;
//					curProfit = cDistriStockItem.cRetExitCheck.profitPer;
//					curCostDayCnt =cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter +1;
//					if(curCostDayCnt > curmaxCostCnt)
//						curmaxCostCnt = curCostDayCnt;
//					fmt.format("Transection stock:%s enter:%s exit:%s profit:%.3f\n", 
//							curSelectStockID, curEnterDate, curExitDate, curProfit);
//					
//					onepartMyAcc = onepartMyAcc*(1+curProfit);
//					myAcc = myAcc + onepartMyAcc;
//					
//					if(curProfit>0)
//					{
//						myMockTrasectionOKCnt++;
//					}
//					else
//					{
//						myMockTrasectionNGCnt++;
//					}
//				}
//				i = i + curmaxCostCnt;
//			}
//		}
//		
//		fmt.format("Transection End: myAcc: %.3f\n", myAcc);
//		fmt.format("                 MockTrasection myMockTrasectionOKCnt: %d\n", myMockTrasectionOKCnt);
//		fmt.format("                 MockTrasection myMockTrasectionNGCnt: %d\n", myMockTrasectionNGCnt);
//		fmt.format("                 MockTrasection SuccRate: %.3f\n", 
//				(float)myMockTrasectionOKCnt/(myMockTrasectionOKCnt+myMockTrasectionNGCnt));
//	}
//
//	
//	///////////////////////////////////////////////////////////////////////////
//	
//	// 介入点时间排序后，等差介入
//	public static void MockTrasection_DengCha(CheckResult cCheckResult, String filename)
//	{
//		fmt.format("\nMockTrasection2 ------------------------------------->>>> \n");
//		
//		String curSelectStockID = "";
//		ANLStock curANLStock = null;
//		String curEnterDate = "";
//		String curExitDate = "";
//		float curProfit = 0.0f;
//		
//		float myAcc = 1.0f;
//		int myMockTrasectionOKCnt = 0;
//		int myMockTrasectionNGCnt = 0;
//		int curGenCnt = 0;
//		for(int i=0; i< cCheckResult.distributionList.size(); i++)
//		{
//			DistributionItem cDistributionItem = cCheckResult.distributionList.get(i);
//			for(int j =0; j< cDistributionItem.distriStockItemList.size(); j ++)
//			{
//				curGenCnt++;
//				if(curGenCnt%10==0)
//				{
//					DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(j);
//					
//					curSelectStockID = cDistriStockItem.stockId;
//					curANLStock = ANLStockPool.getANLStockNF(curSelectStockID);
//					curEnterDate = curANLStock.historyData.get(cDistriStockItem.iEnter).date;
//					curExitDate = curANLStock.historyData.get(cDistriStockItem.cRetExitCheck.iExit).date;
//					curProfit = cDistriStockItem.cRetExitCheck.profitPer;
//					fmt.format("Transection stock:%s enter:%s exit:%s profit:%.3f myAcc:%.3f\n", 
//							curSelectStockID, curEnterDate, curExitDate, curProfit, myAcc);
//					
//					myAcc = myAcc*(1+curProfit);
//					
//					if(curProfit>0)
//					{
//						myMockTrasectionOKCnt++;
//					}
//					else
//					{
//						myMockTrasectionNGCnt++;
//					}
//					
//				}
//				
//			}
//		}
//		
//		fmt.format("Transection End: myAcc: %.3f\n", myAcc);
//		fmt.format("                 MockTrasection myMockTrasectionOKCnt: %d\n", myMockTrasectionOKCnt);
//		fmt.format("                 MockTrasection myMockTrasectionNGCnt: %d\n", myMockTrasectionNGCnt);
//		fmt.format("                 MockTrasection SuccRate: %.3f\n", 
//				(float)myMockTrasectionOKCnt/(myMockTrasectionOKCnt+myMockTrasectionNGCnt));
//	}
//	
//	
//	///////////////////////////////////////////////////////////////////////////
//	
//	public static class OneStockAcc
//	{
//		String id;
//		float beginMoney;
//		float profit;
//		String beginDate;
//		String endDate;
//	}
//	public static class Account
//	{
//		public Account()
//		{
//			stockAccList = new ArrayList<OneStockAcc>();
//			asset = 1.0f;
//		}
//		public void updateDate(String Date)
//		{
//			for(int i=0; i< stockAccList.size();i++)
//			{
//				OneStockAcc cStockAcc = stockAccList.get(i);
//				if(Date.compareTo(cStockAcc.endDate) >= 0)
//				{
////					fmt.format("XXXXXXXXXXXX asset=%.3f beginMoney:%.3f profit:%.3f\n", 
////							asset, cStockAcc.beginMoney, cStockAcc.profit);
//					asset = asset + cStockAcc.beginMoney*(1+cStockAcc.profit);
//					stockAccList.remove(cStockAcc);
//					
//				}
//			}
//		}
//		public float getOneAvailablePartAcc()
//		{
//			float retPartAcc = 0.0f;
//			int imaxPart = 3;
//			int ileft = imaxPart - stockAccList.size();
//			if(ileft>0)
//			{
//				return asset/imaxPart;
//			}
//			else
//			{
//				return 0.0f;
//			}
//		}
//		public void useOnePartBuyStock(OneStockAcc cStockAcc)
//		{
//			asset = asset - cStockAcc.beginMoney;
//			stockAccList.add(cStockAcc);
//		}
//		public float getAllAss()
//		{
//			float retAll = asset;
//			for(int i=0; i< stockAccList.size();i++)
//			{
//				OneStockAcc cStockAcc = stockAccList.get(i);
//				retAll = retAll + cStockAcc.beginMoney;
//			}
//			return retAll;
//		}
//		public List<OneStockAcc> stockAccList;
//		float asset;
//	}
//	// 根据账户分仓介入
//	public static void MockTrasection_withMockAccount(CheckResult cCheckResult, String filename)
//	{
//		try
//		{
//			File cfile =new File(filename);
//			cfile.delete();
//			FileOutputStream cOutputStream = new FileOutputStream(cfile);
//			String wLine = "";
//			
//			wLine = String.format("\nMockTrasection_withMockAccount ------------------------------------->>>> \n");
//			cOutputStream.write(wLine.getBytes());
//			fmt.format(wLine);
//			
//			Account cAccount = new Account();
//			
//			String curSelectStockID = "";
//			ANLStock curANLStock = null;
//			String curEnterDate = "";
//			String curExitDate = "";
//			float curProfit = 0.0f;
//			
//			int myMockTrasectionOKCnt = 0;
//			int myMockTrasectionNGCnt = 0;
//			int curGenCnt = 0;
//			for(int i=0; i< cCheckResult.distributionList.size(); i++)
//			{
//				DistributionItem cDistributionItem = cCheckResult.distributionList.get(i);
//				
//				for(int j =0; j< cDistributionItem.distriStockItemList.size(); j ++)
//				{
//					curGenCnt++;
//					
//					if(curGenCnt%5==0)
//					if(true)
//					{
//						DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(j);
//						
//						curSelectStockID = cDistriStockItem.stockId;
//						curANLStock = ANLStockPool.getANLStockNF(curSelectStockID);
//						curEnterDate = curANLStock.historyData.get(cDistriStockItem.iEnter).date;
//						curExitDate = curANLStock.historyData.get(cDistriStockItem.cRetExitCheck.iExit).date;
//						curProfit = cDistriStockItem.cRetExitCheck.profitPer;
//						
//						float OneAvailablePartAcc = cAccount.getOneAvailablePartAcc();
//						if(OneAvailablePartAcc > 0)
//						{
//							OneStockAcc cStockAcc = new OneStockAcc();
//							cStockAcc.id = curSelectStockID;
//							cStockAcc.beginDate = curEnterDate;
//							cStockAcc.endDate = curExitDate;
//							cStockAcc.profit = curProfit;
//							cStockAcc.beginMoney = OneAvailablePartAcc;
//							
//							cAccount.useOnePartBuyStock(cStockAcc);
//							
//							
//							wLine = String.format("Transection stock:%s enter:%s exit:%s profit:%.3f myAcc:%.3f\n", 
//									curSelectStockID, curEnterDate, curExitDate, curProfit, cAccount.getAllAss());
//							cOutputStream.write(wLine.getBytes());
//							fmt.format(wLine);
//							
//							if(curProfit>0)
//							{
//								myMockTrasectionOKCnt++;
//							}
//							else
//							{
//								myMockTrasectionNGCnt++;
//							}
//							
//						}
//					}
//				}
//				
//				cAccount.updateDate(cDistributionItem.date);
//				
//			}
//			
//			wLine = String.format("Transection End: myAcc: %.3f\n", cAccount.asset);
//			cOutputStream.write(wLine.getBytes());
//			fmt.format(wLine);
//			
//			wLine = String.format("                 MockTrasection myMockTrasectionOKCnt: %d\n", myMockTrasectionOKCnt);
//			cOutputStream.write(wLine.getBytes());
//			fmt.format(wLine);
//			
//			wLine = String.format("                 MockTrasection myMockTrasectionNGCnt: %d\n", myMockTrasectionNGCnt);
//			cOutputStream.write(wLine.getBytes());
//			fmt.format(wLine);
//			
//			wLine = String.format("                 MockTrasection SuccRate: %.3f\n", 
//					(float)myMockTrasectionOKCnt/(myMockTrasectionOKCnt+myMockTrasectionNGCnt));
//			cOutputStream.write(wLine.getBytes());
//			fmt.format(wLine);
//			
//			cOutputStream.close();
//		}
//		catch(Exception e)
//		{
//			System.out.println("Exception:" + e.getMessage()); 
//		}
//	}
//
//	// 模拟交易
//	// 依赖于 先计算分布后，再试图交易
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		System.out.println("### Main Begin");
//		
//		///////////////////////////////////////////////////////////////////////////////////
//		// run param 
//		
////		// param1: 策略
////		ANLPolicyBase cPolicy = new ANLPolicyX1();
////		// param2: 股票列表
////		List<StockItem> retStockList = null;
////		retStockList = DataWebStockAllList.getRandomStock(200); // 只测试若干随机
//////		retStockList = new ArrayList<StockItem>(); // 测试所有股票
//////		DataWebStockAllList.getAllStockList(retStockList);
////		// param3: 测试向前最大天数
////		int iTestMaxDaysCnt = 1250;
//		
//		///////////////////////////////////////////////////////////////////////////////////
//
////		CheckResult cCheckResult = RunSuccRateCheckByDays.generateSuccDistribution(cPolicy, retStockList, iTestMaxDaysCnt);
////		
////		// 模拟交易
////		MockTrasection_withMockAccount(cCheckResult, "LOG_MockTransection.txt");
////		
////		cCheckResult.printResult("LOG_MockTransection_SuccRateCheck.txt");
////		
////		System.out.println("### Main End");
//		
//	}

}
