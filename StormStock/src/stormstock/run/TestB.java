package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLPolicyBase;
import stormstock.analysis.ANLPolicyXY;
import stormstock.analysis.ANLStockDayKData;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.run.RunSuccRateCheckByStocks.ProfitResult;

public class TestB {
	public static Formatter fmt = new Formatter(System.out);
	public static void rmlog()
	{
		File cfile =new File("test.txt");
		cfile.delete();
	}
	public static void outputLog(String s, boolean enable)
	{
		if(!enable) return;
		fmt.format("%s", s);
		File cfile =new File("test.txt");
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	public static void outputLog(String s)
	{
		outputLog(s, true);
	}
	
	// i是否是短期下挫企稳日
	public static boolean ShenFuXiaCuoQiWen(List<DayKData> dayklist, int i)
	{
		String logstr;
		DayKData cDayKData = dayklist.get(i);
		// 计算i的前五天最低下挫价位点
		int zuidi_index = 0;
		float last5low = 1000.0f;
		for(int j = i-5; j!=i; j++ )
		{
			DayKData cDayKDataTmp = dayklist.get(j);
			if(cDayKDataTmp.low < last5low) {
				last5low = cDayKDataTmp.low;
				zuidi_index = j;
			}
		}
		// 计算前2天均值与 后两2均值
		DayKData cDayKDataFirst1 = dayklist.get(i-5);
		DayKData cDayKDataFirst2 = dayklist.get(i-4);
		float pre2ave = (cDayKDataFirst1.low + cDayKDataFirst2.low)/2;
		DayKData cDayKDataLast1 = dayklist.get(i-1);
		DayKData cDayKDataLast2 = dayklist.get(i-0);
		float last2ave = (cDayKDataLast1.low + cDayKDataLast2.low)/2;
		float xiacuo = (last2ave - pre2ave)/pre2ave;

		
		// 前5日最低点是i-1天
		if(zuidi_index == i-1 && cDayKData.low > last5low)
		{
//			logstr = String.format("    %.2f\n",
//					xiacuo);
//			outputLog(logstr);
			//趋势判断幅度判断
			if(xiacuo < -0.05)
			{
				return true;
			}
		}
		return false;
	}
	
	public static void transectionAnalysis(List<DayKData> dayklist, int m)
	{
		String logstr;
		DayKData cDayKDataPre1 = dayklist.get(m-1);
		float minPreK = cDayKDataPre1.close;
		if(cDayKDataPre1.close > cDayKDataPre1.open) minPreK = cDayKDataPre1.open;
		DayKData cDayKDataPre2 = dayklist.get(m-2);
		
		boolean bEnter = false;
		int iEnter = 0;
		float enterPrice = 0.0f;
		for(int j = m; j < m+5; j++ )
		{
			DayKData cDayKDataTmp = dayklist.get(j);
			if(cDayKDataTmp.low < minPreK) {
				logstr = String.format("    Enter %s %.2f\n",
						cDayKDataTmp.date,minPreK);
				outputLog(logstr);
				bEnter = true;
				iEnter = j;
				enterPrice = minPreK;
				break;
			}
		}
		if(bEnter)
		{
			for(int j = iEnter+1; j < iEnter+5; j++ )
			{
				DayKData cDayKDataTmp = dayklist.get(j);
				if(cDayKDataTmp.low < enterPrice*(1-0.03)) {
					logstr = String.format("        - Fail\n",
							cDayKDataTmp.date);
					outputLog(logstr);
					break;
				}
				if(cDayKDataTmp.high > enterPrice*(1+0.01))
				{
					logstr = String.format("        - Succ\n",
							cDayKDataTmp.date);
					outputLog(logstr);
					break;
				}
			}
		}
		
	}
	public static void analysisOne(String id, int maxDayCount)
	{
		String logstr;
		
		List<DayKData> retList = new ArrayList<DayKData>();
		DataEngine.getDayKDataQianFuQuan(id, retList);
		
		for (int i =10; i< retList.size()-1; i++)
		{
			boolean bSFXCQW = ShenFuXiaCuoQiWen(retList, i);
			
			if(bSFXCQW)
			{
				DayKData cDayKData = retList.get(i);
				logstr = String.format("%s\n",
						cDayKData.date);
				outputLog(logstr);
				transectionAnalysis(retList, i+1);
			}
		}
	}
	
	public static void main(String[] args) {
		rmlog();
		outputLog("Main Begin\n\n");
		// 股票列表
		List<StockItem> cStockList = new ArrayList<StockItem>();
		cStockList.add(new StockItem("300312"));
//		cStockList.add(new StockItem("300191"));
// 		cStockList.add(new StockItem("002344"));
//		cStockList.add(new StockItem("002695"));
//		cStockList.add(new StockItem("300041"));
//		cStockList.add(new StockItem("600030"));
		if(cStockList.size() <= 0)
		{
			// cStockList =  DataEngine.getLocalRandomStock(30);
			cStockList = DataEngine.getLocalAllStock();
		}
		
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i).id;
			analysisOne(stockId, 10000);
		}
		outputLog("\n\nMain End");
	}

}
