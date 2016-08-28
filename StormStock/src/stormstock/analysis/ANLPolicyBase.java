package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLPolicyBase {
	public static Formatter fmt = new Formatter(System.out);
	public static String strLogName = "ANLPolicyBase.txt";
	
	// 测试统计结果全局记录
	public static String stockId;
	public static int succCnt = 0;
	public static int failCnt = 0;
	public static float initMoney = 10000.0f;
	public static float curMoney = initMoney;
	
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
	// 查找日期索引
	public static int indexDayK(List<ANLStockDayKData> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) <= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	// 计算i到j日的价格均值
	public static float priceAve(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float ave = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			ave = ave + cDayKDataTmp.close;
		}
		ave = ave/(j-i+1);
		return ave;
	}
	// 计算i到j日的最高价格
	public static float priceHigh(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float high = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high > high) 
				high = cDayKDataTmp.high;
		}
		return high;
	}
	// 计算i到j日的最低价格
	public static float priceLow(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low < low) 
				low = cDayKDataTmp.low;
		}
		return low;
	}
	// 计算i到j日的最高价格的索引
	public static int indexHigh(List<ANLStockDayKData> dayklist, int i, int j)
	{
		int index = i;
		float high = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high > high) 
			{
				high = cDayKDataTmp.high;
				index = k;
			}
		}
		return index;
	}
	// 计算i到j日的最低价格的索引
	public static int indexLow(List<ANLStockDayKData> dayklist, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low < low) 
			{
				low = cDayKDataTmp.low;
				index = k;
			}
		}
		return index;
	}
	// 计算i到j日的波动幅度参数,数值越大波动越大
	public static float waveParam(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float wave = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			float wave1 = Math.abs((cDayKDataTmp.close - cDayKDataTmp.open)/cDayKDataTmp.open);
			float wave2 = (cDayKDataTmp.high - cDayKDataTmp.low)/cDayKDataTmp.low;
			wave = wave + (wave1+wave2)/2;
		}
		wave = wave/(j-i+1);
		return wave;
	}
	
	// 确认i日是否是下挫企稳点
	public static class XiaCuoRange
	{
		public int iBeginIndex;
		public int iHighIndex;
		public int iLowIndex;
		public int iEndEndex;
		public float highPrice;
		public float lowPrice;
		public float maxZhenFu()
		{
			return (lowPrice-highPrice)/highPrice;
		}
		public float xiaCuoXieLv()
		{
			return maxZhenFu()/(iLowIndex-iHighIndex);
		}
	}
	public static XiaCuoRange CheckXiaCuoRange(List<ANLStockDayKData> dayklist, int i)
	{
		String logstr;
		int iCheckE = i;
		// 检查当前天，前6到20天区间满足急跌企稳趋势
		for(int iCheckB = i-6; iCheckB>=i-20; iCheckB--)
		{
			if(iCheckB >= 0)
			{
				// @ 最高点与最低点在区间位置的判断
				boolean bCheckHighLowIndex = false;
				int indexHigh = indexHigh(dayklist, iCheckB, iCheckE);
				int indexLow = indexLow(dayklist, iCheckB, iCheckE);
				if(indexHigh>iCheckB && indexHigh<=(iCheckB+iCheckE)/2
						&& indexLow > (iCheckB+iCheckE)/2 && indexLow < iCheckE)
				{
					bCheckHighLowIndex = true;
				}
				
				// @ 最高点与最低点下挫幅度判断
				boolean bCheckXiaCuo = false;
				float highPrice = dayklist.get(indexHigh).high;
				float lowPrice = dayklist.get(indexLow).low;
				float xiaCuoZhenFu = (lowPrice-highPrice)/highPrice;
				float xiaCuoMinCheck = -1.5f*0.01f*(indexLow-indexHigh);
				if(xiaCuoMinCheck > -0.06f) xiaCuoMinCheck = -0.06f;
				if(xiaCuoZhenFu < xiaCuoMinCheck)
				{
					bCheckXiaCuo = true;
				}
				
				// @ 前区间，后区间价位判断
				boolean bCheck3 = true;
				int cntDay = iCheckE-iCheckB;
				float midPrice = (highPrice+lowPrice)/2;
				for(int c= iCheckB; c<iCheckB + cntDay/3; c++)
				{
					if(dayklist.get(c).low < midPrice)
					{
						bCheck3 = false;
					}
				}
				for(int c= iCheckE; c>iCheckE - cntDay/3; c--)
				{
					if(dayklist.get(c).low > midPrice)
					{
						bCheck3 = false;
					}
				}
				
				if(bCheckHighLowIndex && 
						bCheckXiaCuo && 
						bCheck3)
				{
//					logstr = String.format("    Test findLatestXiaCuoRange [%s,%s] ZhenFu(%.3f,%.3f)\n",
//							dayklist.get(iCheckB).date,
//							dayklist.get(iCheckE).date,
//							xiaCuoZhenFu,xiaCuoMinCheck);
//					outputLog(logstr);
					
					XiaCuoRange retXiaCuoRange = new XiaCuoRange();
					retXiaCuoRange.iBeginIndex = iCheckB;
					retXiaCuoRange.iEndEndex = iCheckE;
					retXiaCuoRange.iHighIndex = indexHigh;
					retXiaCuoRange.iLowIndex = indexLow;
					retXiaCuoRange.highPrice = highPrice;
					retXiaCuoRange.lowPrice = lowPrice;
					return retXiaCuoRange;
				}
			}
		}
		return null;
	}
	
	// 进行测试交易
	public static String transectionAnalysis(List<ANLStockDayKData> dayklist, 
			String enterDate, float enterPrice)
	{
		int iMaxTranDays = 10;
		float zhisun = -0.04f;
		float zhiying = 0.04f;
		String logstr;

		int iTranBegin = indexDayK(dayklist, enterDate);
		float zhisunPrice = enterPrice * (1+zhisun);
		float zhiyingPrice = enterPrice * (1+zhiying);
		
		
		float outPrice = 0.0f;
		int outIndex = 0;
		for(int i = iTranBegin+1; i < iTranBegin + iMaxTranDays; i++ )
		{
			if(i >= dayklist.size())
			{
				// 现有数据不够了，需要等待交易
				break;
			}
			ANLStockDayKData cDayKDataTmp = dayklist.get(i);
			if(cDayKDataTmp.low < zhisunPrice) {
				outPrice = zhisunPrice;
				outIndex = i;
				// 止损出局
				break;
			}
			if(cDayKDataTmp.high > zhiyingPrice) {
				outPrice = zhiyingPrice;
				outIndex = i;
				// 止盈出局
				break;
			}
			if(i == iTranBegin + iMaxTranDays - 1)
			{
				outPrice = cDayKDataTmp.close;
				outIndex = i;
				// 超时出局
				break;
			}
		}
		
		if(outIndex > 0)
		{
			float profit = (outPrice - enterPrice)/enterPrice;
			if(profit>0)
			{
				succCnt = succCnt + 1;
			}
			else
			{
				failCnt = failCnt + 1;
			}
			curMoney = curMoney*(1+profit);
			
			logstr = String.format("TRAN[ %s %.2f -> %s %.2f %.3f ]",
					dayklist.get(iTranBegin).date, enterPrice,
					dayklist.get(outIndex).date, outPrice, 
					profit);
			//outputLog(logstr);
		}
		else
		{
			logstr = String.format("TRAN[ %s %.2f -> wait 0.00 0.000 ]",
					dayklist.get(iTranBegin).date, enterPrice
					);
			//outputLog(logstr);
		}
		return logstr;
	}
	public static void initProfitInfo(String inStockId) {
		stockId = inStockId;
		succCnt = 0;
		failCnt = 0;
		curMoney = initMoney;
	}
	public static void printProfitInfo() {
//		String logstr;
//		logstr = String.format("\n --- printProfitInfo (stockId) ---\n");
//		outputLog(logstr);
//		logstr = String.format("    succCnt: %d \n", succCnt);
//		outputLog(logstr);
//		logstr = String.format("    failCnt: %d \n", failCnt);
//		outputLog(logstr);
//		logstr = String.format("    initMoney: %.2f \n", initMoney);
//		outputLog(logstr);
//		logstr = String.format("    curMoney: %.2f \n", curMoney);
//		outputLog(logstr);
//		logstr = String.format("    totalProfit: %.3f \n", (curMoney- initMoney)/initMoney);
//		outputLog(logstr);
	}
	
	// 分析股票id从fromDate日期到toDate日期，只输出enableLogMinDate天的之后的分析log
	public static void analysisOne(String id, String fromDate, String toDate, String enableLogMinDate)
	{
		initProfitInfo(id);
		
		String logstr;
		
		ANLStock cANLStock = ANLStockPool.getANLStock(id);
		if(null == cANLStock) return;
		
		int iB = indexDayK(cANLStock.historyData, fromDate);
		int iE = indexDayK(cANLStock.historyData, toDate);
		int iLogEnable = indexDayK(cANLStock.historyData, enableLogMinDate);
		boolean bLogEnable = false;
		//String dateLogEnable = cANLStock.historyData.get(retXiaCuoRange.iBeginIndex).date;

		XiaCuoRange cLastXiaCuoRange = new XiaCuoRange();
		for(int i = iB; i<= iE; i++)
		{
			
//			logstr = String.format("CheckDay %s\n",
//					cANLStock.historyData.get(i).date);
//			outputLog(logstr);
			if(i >= iLogEnable) bLogEnable = true;
			
			// 急跌确认
			XiaCuoRange retXiaCuoRange = CheckXiaCuoRange(cANLStock.historyData, i);
			if(null != retXiaCuoRange && cLastXiaCuoRange.highPrice != retXiaCuoRange.highPrice)
			{
				
				float hisSucc = 0.0f;
				if(0!=(failCnt+succCnt)) hisSucc = (float)succCnt/(failCnt+succCnt);
				logstr = String.format("S( %s ) CXCR[ %s %s %.3f %.3f ] "
						+ "HIS[ %.3f %d ] ",
						stockId, cANLStock.historyData.get(retXiaCuoRange.iBeginIndex).date,
						cANLStock.historyData.get(retXiaCuoRange.iEndEndex).date,
						retXiaCuoRange.maxZhenFu(),retXiaCuoRange.xiaCuoXieLv(),
						hisSucc, failCnt+succCnt);
					
				// 回踩确认
				int iEnterIndex = retXiaCuoRange.iEndEndex + 1;
				float enterPrice = 0.0f;
				float latastHigh = cANLStock.historyData.get(retXiaCuoRange.iEndEndex).high;
				for(int k = retXiaCuoRange.iEndEndex + 1; k<=retXiaCuoRange.iEndEndex + 6 && k<cANLStock.historyData.size(); k++)
				{
					if(cANLStock.historyData.get(k).high > latastHigh) 
						latastHigh = cANLStock.historyData.get(k).high;
					
					if(cANLStock.historyData.get(k).low < (latastHigh + retXiaCuoRange.lowPrice)/2)
					{
						iEnterIndex = k;
						enterPrice = (latastHigh + retXiaCuoRange.lowPrice)/2;
						// 交易测试阶段
						logstr += transectionAnalysis(cANLStock.historyData, 
								cANLStock.historyData.get(iEnterIndex).date,enterPrice);
						break;
					}
				}
			
				logstr+= "\n";
				if(bLogEnable)
					outputLog(logstr);
				
				cLastXiaCuoRange = retXiaCuoRange;
			}
		}
		
		printProfitInfo();
	}
	
	public static void main(String[] args) {
		strLogName = "ANLPolicyBase.txt";
		rmlog();
		outputLog("Main Begin\n\n");
		// 股票列表
		List<StockItem> cStockList = new ArrayList<StockItem>();
		cStockList.add(new StockItem("300312"));
//		cStockList.add(new StockItem("300132"));
//		cStockList.add(new StockItem("002344"));
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
			analysisOne(stockId, "2008-01-01", "2116-01-01", "2016-07-27");
		}
		
		outputLog("\n\nMain End");
	}
}
