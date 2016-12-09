package stormstock.run.bt;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLDataProvider;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;

public class RunBTStrategyAve {
	public static Formatter fmt = new Formatter(System.out);
	public static String strLogName = "ANLPolicyAve.txt";
	public static float fStopProfit = 1.0f;  //止盈点
	public static float fStopLoss = 2.0f;  //止损点
	public static int nMaxTradeDays = 5;  //最大交易时间（天）
	
	public static void rmlog()
	{
		File cfile =new File(strLogName);
		cfile.delete();
	}
	public static void outputLog(String s, boolean enable)
	{
		if(!enable) return;
		fmt.format("%s", s);
		File cfile =new File(strLogName);
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
	
	//反弹数据
	public static class BounceData implements Comparable
	{
		public String id;  //股票ID
		public int total;  //满足条件（前期有一波较大涨幅现在已经调整到均线附近）的次数
		public int incCount;  //满足条件后上涨（调整到均线后5日内涨幅超过2个点）的次数
		public int decCount;  //满足条件后下跌（调整到均线后5日内跌幅超过2个点）的次数
		public float incRate;  //上涨率
		public float decRate;  //下跌率
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			BounceData sdto = (BounceData)arg0;
			if(this.incRate <= sdto.incRate)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
	}
	
	/*
	 * 功能：判断未来几日内最大涨幅是否超过incRate
	 * 参数：today：满足条件的这天在dayK_list的索引
	 * 参数：dayK_list：日K数据
	 * 返回值：是否成功
	 * */
	public static boolean isIncInFuture(int today, List<ANLStockDayKData> dayK_list)
	{
		ANLStockDayKData cTodayKData = dayK_list.get(today);
		float todayClose = cTodayKData.close;
		int nDayAfter = today+nMaxTradeDays+2;
		if(nDayAfter >= dayK_list.size())
		{
			return false;
		}
		
		//满足条件后第2天买，第3天才能开始交易，所以从第3天开始算
		for(int i=today+2; i<=nDayAfter ; ++i)
		{
			ANLStockDayKData cDayKData = dayK_list.get(i);
			
			//如果买完后先到达了止损位，则算失败
			if(cDayKData.close < todayClose)
			{
				float decRate = (todayClose-cDayKData.close)/todayClose*100;
				if(decRate >= fStopLoss)
				{
					break;
				}
			}
			
			if(cDayKData.close > todayClose)
			{
				float realRate = (cDayKData.close-todayClose)/todayClose*100;
				if(realRate >= fStopProfit)
				{
					//outputLog(String.format("future inc rate:%.2f\n", realRate));
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	/*
	 * 功能：判断未来几日内最大跌幅是否超过decRate
	 * 参数：today：满足条件的这天在dayK_list的索引
	 * 参数：dayK_list：日K数据
	 * 返回值：是否成功
	 * */
	public static boolean isDecInFuture(int today, List<ANLStockDayKData> dayK_list)
	{
		ANLStockDayKData cTodayKData = dayK_list.get(today);
		float todayClose = cTodayKData.close;
		int nDayAfter = today+nMaxTradeDays+2;
		if(nDayAfter >= dayK_list.size())
		{
			return false;
		}
		
		//满足条件后第2天买，第3天才能开始交易，所以从第3天开始算
		for(int i=today+2; i<=nDayAfter ; ++i)
		{
			ANLStockDayKData cDayKData = dayK_list.get(i);
			
			//如果买完后先到达了止损位，则算失败
			if(cDayKData.close < todayClose)
			{
				float realDecRate = (todayClose-cDayKData.close)/todayClose*100;
				if(realDecRate >= fStopLoss)
				{
					return true;
				}
			}
			
			if(cDayKData.close > todayClose)
			{
				float realRate = (cDayKData.close-todayClose)/todayClose*100;
				if(realRate >= fStopProfit)
				{
					//outputLog(String.format("future inc rate:%.2f\n", realRate));
					break;
				}
			}
		}
		
		
		return false;
	}
	
	/*
	 * 功能：判断最近是否强势
	 * 参数：day：多少日
	 * 参数：incRate：涨幅幅度
	 * 参数：today：满足条件的这天在dayK_list的索引
	 * 参数：dayK_list：日K数据
	 * 返回值：是否成功
	 * */
	public static boolean isRecentStrong(int day, float incRate, int today, List<ANLStockDayKData> dayK_list)
	{
		int nDayBefore = today-day;
		if(nDayBefore < 0)
		{
			return false;
		}
		
		float low = 1000.0f;
		float high = 0.0f;
		ANLStockDayKData cNDayBeforeKData = dayK_list.get(nDayBefore);
		for(int i=nDayBefore; i<=today ; ++i)
		{
			ANLStockDayKData cDayKData = dayK_list.get(i);
			if(cDayKData.close < low)
			{
				low = cDayKData.close;
			}
			if(cDayKData.close > high)
			{
				high = cDayKData.close;
			}
		}
		
		int midIndex = today-day/2;
		ANLStockDayKData cMidDayKData = dayK_list.get(midIndex);
		
		if(cMidDayKData.close > cNDayBeforeKData.close) //判断是否是上涨
		{
			float realIncRate = (high-low)/low*100;
			if(realIncRate>=incRate)
			{
				//System.out.println(String.format("date:%s,incRate:%.2f\n", cTodayKData.date,realIncRate));
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * 功能：计算n日均线
	 * n：几日均线
	 * 参数：index：要计算的日期在dayK_list中的索引
	 * 参数：dayK_list：日K数据
	 * 返回值：均线
	 * */
	public static float getNDayAverage(int n, int index, List<ANLStockDayKData> dayK_list)
	{
		if(n <= 0)
		{
			return 0.0f;
		}
		
		if(-1 == index)
		{
			return 0.0f;
		}
		
		int indexNDayBefore = index-(n-1);
		if(indexNDayBefore < 0)
		{
			indexNDayBefore = 0;
		}
		int count = index-indexNDayBefore+1;
		
		float nDayAve = 0.0f;
		for(int j=indexNDayBefore; j<=index ; ++j)
		{
			ANLStockDayKData cDayKData = dayK_list.get(j);
			nDayAve += cDayKData.close;
		}
		
		nDayAve /= count;
		
		return nDayAve;
	}
	
	/*
	 * 功能：判断条件是否满足（当日最低价跌破或接近均线价格并且前期较强势）
	 * 参数：nAveOfDays：几日均线
	 * 参数：index：要计算的日期在dayK_list中的索引
	 * 参数：dayK_list：日K数据
	 * 返回值：是否满足条件
	 * */
	public static boolean isDaySatisfied(int nAveOfDays,int index,List<ANLStockDayKData> dayK_list)
	{
		if(index < 0)
		{
			return false;
		}
		
		if(index >= dayK_list.size())
		{
			return false;
		}
		
		float fNDayAve = getNDayAverage(nAveOfDays,index,dayK_list);
		
		ANLStockDayKData cDayKData = dayK_list.get(index);
		if(cDayKData.close<cDayKData.open  //当日是下跌的
				&& cDayKData.open>fNDayAve  //开盘并没有破均线
				&& (cDayKData.low/*-0.15*/)<=fNDayAve)  //最低点跌破或接近均线(TODO：0.15只是大概值)
		{
			int day = 10;
			float incRate = 15.0f;
			switch(nAveOfDays)
			{
				case 5:
				{
					day = 5;
					incRate = 10.0f;
				}
					break;
				case 10:
					break;
				case 20:
				{
					day = 10;
					incRate = 15.0f;
				}
					break;
				case 30:
				{
					day = 15;
					incRate = 20.0f;
				}
					break;
				case 60:
					break;
				default:
					break;
			}
			
			if(isRecentStrong(day,incRate,index,dayK_list))  //并且近期强势
			{
				return true;
			}
		}
		
		return false;
	}

	/*
	 * 功能：分析某只股票
	 * 参数：id：股票ID
	 * 参数：bounceList：满足条件的股票的反弹数据
	 * 参数：nAveOfDays：几日均线
	 * 参数：bCalToday:是否只计算今天满足条件的
	 * */
	public static void analyzeOne(String id,List<BounceData> bounceList,int nAveOfDays,boolean bCalToday)
	{
		ANLStock cANLStock = ANLDataProvider.getANLStock(id);
		if(null == cANLStock)
		{
			return;
		}
		
		int size = cANLStock.historyData.size();
		if(size < nAveOfDays)
		{
			return;
		}

		if(bCalToday)
		{
			//计算最后一天
			if(!isDaySatisfied(nAveOfDays,size-1,cANLStock.historyData))
			{
				return;
			}
		}
		
		int total = 0;
		int incCount = 0;
		int decCount = 0;
		
		for (int i =nAveOfDays; i< size; i++)
		{
			if(isDaySatisfied(nAveOfDays,i,cANLStock.historyData))
			{
				++total;
				
				if(isIncInFuture(i,cANLStock.historyData))
				{
					//ANLStockDayKData cDayKData = cANLStock.historyData.get(i);
					//System.out.println(cDayKData.date);
					++incCount;
				}
				
				if(isDecInFuture(i,cANLStock.historyData))
				{
					++decCount;
				}
			}
		}
		
		if(total > 15)
		{
			float incRate = (float)incCount/total*100;
			float decRate = (float)decCount/total*100;
			
			BounceData tempData = new BounceData();
			tempData.id = id;
			tempData.incCount = incCount;
			tempData.decCount = decCount;
			tempData.total = total;
			tempData.incRate = incRate;
			tempData.decRate = decRate;
			
			bounceList.add(tempData);
		}
	}

	
	public static void main(String[] args) {
		rmlog();
		outputLog("Main Begin\n\n");
		
		// 股票列表
		List<StockItem> cStockList = new ArrayList<StockItem>();
		//cStockList.add(new StockItem("002054"));
		if(cStockList.size() <= 0)
		{
			cStockList = DataEngine.getLocalAllStock();
		}
		
		List<BounceData> bounceList = new ArrayList<BounceData>();
		
		boolean bCalToday = false;
		int nAveOfDay = 20;
		
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i).id;
			
			analyzeOne(stockId,bounceList,nAveOfDay,bCalToday);
		}

		//按成功率降序
		Collections.sort(bounceList);
		Collections.reverse(bounceList);
		
		int total = 0;
		int incCount = 0;
		float incRate = 0.0f;
		int decCount = 0;
		float decRate = 0.0f;
		
		//打印出每个股票的反弹数据
		for(int i=0; i<bounceList.size();i++)
		{
			BounceData tempData = bounceList.get(i);
			total += tempData.total;
			incCount += tempData.incCount;
			decCount += tempData.decCount;
			
			String strLog = String.format("%d:,  id:%s,total:%d,incCount:%d,decCount:%d,incRate:%.2f,decRate:%.2f\n",
					i,tempData.id,tempData.total,tempData.incCount,tempData.decCount,tempData.incRate,tempData.decRate);
			outputLog(strLog);
		}

		if(!bCalToday)
		{
			//打印出汇总的反弹数据
			if(total > 0)
			{
				incRate = (float)incCount/total*100;
				decRate = (float)decCount/total*100;
			}
			
			String strLog = String.format("sum:  total:%d,incCount:%d,decCount:%d,incRate:%.2f,decRate:%.2f\n",
					total,incCount,decCount,incRate,decRate);
			outputLog(strLog);
		}
		
		
		outputLog("\n\nMain End");
	}
}
