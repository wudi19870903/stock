package stormstock.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import stormstock.data.WebStockAllList;
import stormstock.data.WebStockAllList.StockItem;
import stormstock.data.WebStockDayDetail.DayDetailItem;
import stormstock.data.WebStockDayK.DayKData;

public class TestDistribution {
	public static Formatter fmt = new Formatter(System.out);
	static class DistriStockItem
	{
		public String id;
		public float profit;
	}
	public static class DistributionItem
	{
		public DistributionItem()
		{
			date="";
			distriStockItemList =  new ArrayList<DistriStockItem>();
		}
		public String date;
		public List<DistriStockItem> distriStockItemList;
	}
	public static class DistributionResult
	{
		public DistributionResult()
		{
			distributionList = new ArrayList<DistributionItem>();
		}
		public List<DistributionItem> distributionList;
		public void add(String date, String id, float protit)
		{
			
			for(int iDate=0; iDate<distributionList.size();iDate++)
			{
				// 找到 对应日期的 DistributionItem 对象
				if(distributionList.get(iDate).date.contains(date))
				{
					DistributionItem cDistributionItem = distributionList.get(iDate);
					//找到对应的股票DistriStockItem对象 如果没有 创建一个
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
					fmt.format("   # DistributionResult StockID:%s EnterDate: %s profit:%.3f\n", 
						id,date,protit);
					break;
				}
			}
		}
		public void printResult(String FileName)
		{
			try
			{
				File cfile =new File(FileName);
				cfile.delete();
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
		        Formatter fmtFile = new Formatter(cOutputStream);
				
		        fmtFile.format("================================================================\n");
				for(int iDate=0; iDate<distributionList.size();iDate++)
				{
					DistributionItem cDistributionItem = distributionList.get(iDate);
					fmtFile.format("Distribution date:%s\n", cDistributionItem.date);
					for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
					{
						DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(iStockItem);
						String SuccFlag = "[NG]";
						if(cDistriStockItem.profit > 0)
						{
							SuccFlag = "[OK]";
						}
						fmtFile.format("    StockId:%s %s Profit:%f\n", cDistriStockItem.id, SuccFlag, cDistriStockItem.profit);
					}
				}
				
				cOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
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
		}
		return retList;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("### Main Begin");

		ANLPolicyBase cPolicy = new ANLPolicyX1();
		
		// 初始化分布列表，根据上证指数
		DistributionResult cDistributionResult = new DistributionResult();
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
				cDistributionResult.distributionList.add(cDistributionItem);
	        } 
		}
		else
		{
			fmt.format("ERROR retgetDayKDataQianFuQuan:%d", retgetDayKDataQianFuQuan);
			return;
		}
		

		// 对所有股票进行检查，然后填进列表
		List<StockItem> retListAll = new ArrayList<StockItem>();
		// 测试所有股票
		int retgetAllStockList = WebStockAllList.getAllStockList(retListAll);
		// 只测试若干
		//retListAll = getRandomStock(20);
		if(retListAll.size() != 0)
		{
			for(int iStock = 0; iStock < retListAll.size(); iStock++)  
	        {  
				StockItem cStockItem = retListAll.get(iStock);
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
						if(cRetExitCheck.profitPer < 0.0f)
						{
							cDistributionResult.add(cCheckDayKData.date, stockId, cRetExitCheck.profitPer);
						}
						else
						{
							cDistributionResult.add(cCheckDayKData.date, stockId, cRetExitCheck.profitPer);
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
