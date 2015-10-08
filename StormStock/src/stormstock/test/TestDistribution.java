package stormstock.test;

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
import stormstock.run.RunFindEnterStock.DistributionItem;

public class TestDistribution {
	public static Formatter fmt = new Formatter(System.out);
	static class DistriStockItem
	{
		public String id;
		public float profit;
		public int costDayCnt;
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
	public static class DistributionResult
	{
		public DistributionResult()
		{
			distributionList = new ArrayList<DistributionItem>();
		}
		public List<DistributionItem> distributionList;
		public void add(String date, String id, float protit, int costDayCnt)
		{
			DistributionItem cDistributionItem = null;
			for(int iDate=0; iDate<distributionList.size();iDate++)
			{
				// 找到 对应日期的 DistributionItem 对象
				if(distributionList.get(iDate).date.contains(date))
				{
					cDistributionItem =  distributionList.get(iDate);
					break;
				}
			}
			if(null == cDistributionItem)
			{
				cDistributionItem = new DistributionItem();
				cDistributionItem.date = date;
				distributionList.add(cDistributionItem);
			}
			
			DistriStockItem cDistriStockItem = null;
			for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
			{
				DistriStockItem tmpObj = cDistributionItem.distriStockItemList.get(iStockItem);
				if(tmpObj.id.contains(id))
				{
					cDistriStockItem = tmpObj;
					break;
				}
			}
			if(null == cDistriStockItem)
			{
				cDistriStockItem = new DistriStockItem();
				cDistriStockItem.id = id;
				cDistributionItem.distriStockItemList.add(cDistriStockItem);
			}
			cDistriStockItem.profit = protit;
			cDistriStockItem.costDayCnt = costDayCnt;
			fmt.format("   # DistributionResult StockID:%s EnterDate: %s profit:%.3f costDayCnt:%d\n", 
				id,date,protit,costDayCnt);
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
					wLine = String.format("Distribution date:%s\n", cDistributionItem.date);
					cOutputStream.write(wLine.getBytes());
					for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
					{
						DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(iStockItem);
						String SuccFlag = "";
						if(cDistriStockItem.profit > 0)
						{
							SuccFlag = "[OK]";
							iOKCntSum++;
							iOKCostDayCntSum = iOKCostDayCntSum + cDistriStockItem.costDayCnt;
							iOKProfitSum = iOKProfitSum + cDistriStockItem.profit;
						}
						else
						{
							SuccFlag = "[NG]";
							iNGCntSum++;
							iNGCostDayCntSum = iNGCostDayCntSum + cDistriStockItem.costDayCnt;
							iNGProfitSum = iNGProfitSum + cDistriStockItem.profit;
						}
						wLine = String.format("    StockId:%s %s Profit:%f costDayCnt:%d\n", 
								cDistriStockItem.id, SuccFlag, cDistriStockItem.profit, cDistriStockItem.costDayCnt);
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
		// 策略
		ANLPolicyBase cPolicy = new ANLPolicyX1();
		List<StockItem> retStockList = null;
		// 只测试若干
		retStockList = getRandomStock(30);
		// 测试所有股票
		//retStockList = new ArrayList<StockItem>();
		//int retgetAllStockList = DataWebStockAllList.getAllStockList(retListAll);
		// 是否更新上证指数交易日总分布
		boolean bUpdate999999 = false;
		
		///////////////////////////////////////////////////////////////////////////////////

		DistributionResult cDistributionResult = new DistributionResult();
		
		// 初始化分布列表，根据上证指数
		List<DayKData> retList999999 = new ArrayList<DayKData>();
		if(bUpdate999999)DataEngine.updateStock("999999");
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
				cDistributionResult.distributionList.add(cDistributionItem);
	        } 
		}
		else
		{
			fmt.format("ERROR retgetDayKDataQianFuQuan:%d", retgetDayKDataQianFuQuan);
			return;
		}

		if(retStockList.size() != 0)
		{
			for(int iStock = 0; iStock < retStockList.size(); iStock++)  
	        {  
				StockItem cStockItem = retStockList.get(iStock);
				String stockId = cStockItem.id;
				
				// 对单只股票进行计算
				fmt.format("check for stockId:%s\n", stockId);
				ANLStock cANLStock = ANLStockPool.getANLStockNF(stockId);
				if(null == cANLStock)
				{
					continue;
				}
				
				int lenlist = cANLStock.historyData.size();
				int iCheckDayBegin = 0;
				int iCheckDayEnd = lenlist-100;
				for(int iIndex = iCheckDayBegin; iIndex < iCheckDayEnd; iIndex++) 
				{
					ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iIndex);
					if(cPolicy.enterCheck(cANLStock, iIndex))
					{
						RetExitCheck cRetExitCheck = cPolicy.exitCheck(cANLStock, iIndex);
						int costDayCnt = cRetExitCheck.iExit -iIndex;
						if(cRetExitCheck.profitPer < 0.0f)
						{
							cDistributionResult.add(cCheckDayKData.date, 
									stockId, cRetExitCheck.profitPer, costDayCnt);
						}
						else
						{
							cDistributionResult.add(cCheckDayKData.date, 
									stockId, cRetExitCheck.profitPer, costDayCnt);
						}
						iIndex = iIndex + 40;
					}
				}
	        } 
		}
		else
		{
			fmt.format("ERROR retgetAllStockList: NULL");
		}
		
		cDistributionResult.printResult("TestDistribution.txt");
		
		System.out.println("### Main End");
	}
}
