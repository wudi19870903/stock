package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLPolicyBase;
import stormstock.analysis.ANLPolicyX1;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLStockPool;
import stormstock.analysis.ANLPolicyBase.RetExitCheck;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class RunFindEnterStock {
	public static Formatter fmt = new Formatter(System.out);
	static class DistriStockItem
	{
		public String id;
	}
	public static class DistributionItem implements Comparable
	{
		public DistributionItem()
		{
			date="";
			distriStockIdList =  new ArrayList<String>();
		}
		public String date;
		public List<String> distriStockIdList;
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			DistributionItem sdto = (DistributionItem)o;
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
		public void add(String date, String id)
		{
			// 找到 对应日期的 DistributionItem 对象, 如果没有添加新的
			DistributionItem cDistributionItem = null;
			for(int iDate=0; iDate<distributionList.size();iDate++)
			{
				if(distributionList.get(iDate).date.contains(date))
				{
					cDistributionItem = distributionList.get(iDate);
				}
			}
			if(null == cDistributionItem)
			{
				cDistributionItem = new DistributionItem();
				cDistributionItem.date = date;
				distributionList.add(cDistributionItem);
			}
			cDistributionItem.distriStockIdList.add(id);
		}
		public void printResult(String FileName)
		{
			Collections.sort(distributionList);
			
			fmt.format("printResult ================================================================\n");
			try
			{
				File cfile =new File(FileName);
				cfile.delete();
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int iDate=0; iDate<distributionList.size();iDate++)
				{
					DistributionItem cDistributionItem = distributionList.get(iDate);
					String strLine = "Distribution date:" + cDistributionItem.date + "\n";
					cOutputStream.write(strLine.getBytes());
					for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockIdList.size();iStockItem++)
					{
						String id = cDistributionItem.distriStockIdList.get(iStockItem);
						strLine = "    StockId:" + id + "\n";
						cOutputStream.write(strLine.getBytes());
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
	public static void main(String[] args) {
		System.out.println("### Main Begin: RunFindEnterStock");
		
		ANLPolicyBase cPolicy = new ANLPolicyX1();
		int LatastDayCnt = 100;
		DistributionResult cDistributionResult = new DistributionResult();
		List<StockItem> retListAll = new ArrayList<StockItem>();
		int retgetAllStockList = DataWebStockAllList.getAllStockList(retListAll);
		if(retListAll.size() != 0)
		{
			for(int iStock = 0; iStock < retListAll.size(); iStock++)  
	        {  
				StockItem cStockItem = retListAll.get(iStock);
				String stockId = cStockItem.id;
				
				fmt.format("check stockId:%s\n", stockId);
				ANLStock cANLStock = ANLStockPool.getANLStockNF(stockId);
				if(null == cANLStock)
				{
					continue;
				}
				fmt.format("check stockId:%s\n", stockId);
				int lenlist = cANLStock.historyData.size();
				if(lenlist < LatastDayCnt+1) continue;
				
				int iCheckDayEnd = lenlist-1;
				int iCheckDayBegin = iCheckDayEnd - LatastDayCnt + 1;;

				for(int iIndex = iCheckDayBegin; iIndex <= iCheckDayEnd; iIndex++) 
				{
					ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iIndex);
					
//					if(stockId.contains("000766"))
//					{
//						fmt.format("    check date:%s\n", cCheckDayKData.date);
//					}
					
					if(cPolicy.enterCheck(cANLStock, iIndex))
					{
						fmt.format("  Policy find date:%s\n", cCheckDayKData.date);
						cDistributionResult.add(cCheckDayKData.date, cANLStock.id);
						iIndex = iIndex + 40;
					}

				}
	        } 
		}
		else
		{
			fmt.format("ERROR retgetAllStockList: NULL");
		}
		
		cDistributionResult.printResult("RunFindEnterStock.txt");
		System.out.println("### Main End: Result: RunFindEnterStock.txt");
	}
}
