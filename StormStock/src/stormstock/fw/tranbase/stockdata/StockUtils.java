package stormstock.fw.tranbase.stockdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stormstock.fw.base.BLog;

/**
 * 
 * @author wudi
 *
 * 股票数据扩展计算类
 * 1-股票日K机能计算
 * 2-股票日内分时机能计算
 */
public class StockUtils {
	
	/*
	 * 日K基本机能
	 * ------------------------------------------------------------------------------------------------------
	 */
	
	// 计算index的临近日平均价（count=1时，表示昨天，今天，明天的均价）
	static public float GetAveNear(List<StockDay> dayklist, int count, int index)
	{
		if(dayklist.size() == 0) return 0.0f;
		float value = 0.0f;
		int iB = index-count;
		int iE = index+count;
		if(iB<0) iB=0;
		if(iE>dayklist.size()-1) iB=dayklist.size()-1;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = dayklist.get(i);  
			sum = sum + cDayKData.midle();
			sumcnt++;
        }
		value = sum/sumcnt;
		return value;
	}
	
	// 均线计算，计算date日期前count天均线价格
	static public float GetMA(List<StockDay> dayklist, int count, int index)
	{
		if(dayklist.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = index;
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = dayklist.get(i);  
			sum = sum + cDayKData.close();
			sumcnt++;
        }
		value = sum/sumcnt;
		return value;
	}
	
	
	// 计算某日收盘涨跌幅（参考开盘）
	static public float GetInreaseRatioRefOpen(List<StockDay> dayklist, int index)
	{
		float ratio = 0.0f;
		if(index >= 0 && index < dayklist.size())
		{
			StockDay cStockDayCur = dayklist.get(index);
			if(cStockDayCur.close() != 0)
			{
				ratio = (cStockDayCur.close() - cStockDayCur.open())/cStockDayCur.close();
			}
		}
		return ratio;
	}
	static public float GetInreaseRatioRefOpen(List<StockDay> dayklist, String date)
	{
		int index = StockUtils.indexDayK(dayklist, date);
		return GetInreaseRatioRefOpen(dayklist, index);
	}
	
	// 计算某日收盘涨跌幅（参考昨日收盘）
	static public float GetInreaseRatio(List<StockDay> dayklist, int index)
	{
		float ratio = 0.0f;
		if(index > 0 && index < dayklist.size())
		{
			StockDay cStockDayCur = dayklist.get(index);
			StockDay cStockDayBefore = dayklist.get(index-1);
			if(cStockDayBefore.close() != 0)
			{
				ratio = (cStockDayCur.close() - cStockDayBefore.close())/cStockDayBefore.close();
			}
		}
		return ratio;
	}
	static public float GetInreaseRatio(List<StockDay> dayklist, String date)
	{
		int index = StockUtils.indexDayK(dayklist, date);
		return GetInreaseRatio(dayklist, index);
	}

	
	// 查找日期索引，返回list中某日期index索引, -1为没有找到
	static public int indexDayK(List<StockDay> dayklist, String dateStr)
	{
		int index = -1;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date().compareTo(dateStr) == 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// 查找日期索引，返回list中某日期（含）之后的第一天index索引
	static public int indexDayKAfterDate(List<StockDay> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date().compareTo(dateStr) >= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// 查找日期索引，返回list中某日期（含）之前的第一天index索引
	static public int indexDayKBeforeDate(List<StockDay> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date().compareTo(dateStr) <= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// 计算i到j日的最高价格的索引
	static public int indexHigh(List<StockDay> dayklist, int i, int j)
	{
		int index = i;
		float high = -100000.0f;
		for(int k = i; k<=j; k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high() > high) 
			{
				high = cDayKDataTmp.high();
				index = k;
			}
		}
		return index;
	}
	
	// 计算i到j日的最低价格的索引
	static public int indexLow(List<StockDay> dayklist, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low() < low) 
			{
				low = cDayKDataTmp.low();
				index = k;
			}
		}
		return index;
	}
	
	public static List<StockDay> subStockDayData(List<StockDay> oriList, String fromDate, String endDate)
	{
		List<StockDay> newStockDayData = new ArrayList<StockDay>();
		for(int i = 0; i <oriList.size(); i++)  
        {  
			StockDay cStockDay = oriList.get(i);  
			if(cStockDay.date().compareTo(fromDate) >= 0 &&
					cStockDay.date().compareTo(endDate) <= 0)
			{
				StockDay cNewStockDay = new StockDay();
				cNewStockDay.CopyFrom(cStockDay);
				newStockDayData.add(cNewStockDay);
			}
        }
		return newStockDayData;
	}
	
	public static List<StockTime> subStockTimeData(List<StockTime> oriList, String fromTime, String endTime)
	{
		List<StockTime> newStockTimeData = new ArrayList<StockTime>();
		for(int i = 0; i <oriList.size(); i++)  
        {  
			StockTime cStockTime = oriList.get(i);  
			if(cStockTime.time.compareTo(fromTime) >= 0 &&
					cStockTime.time.compareTo(endTime) <= 0)
			{
				StockTime cNewStockTime = new StockTime();
				cNewStockTime.CopyFrom(cStockTime);
				newStockTimeData.add(cNewStockTime);
			}
        }
		return newStockTimeData;
	}
	
	
	/*
	 * 日内分时基本机能
	 * ------------------------------------------------------------------------------------------------------
	 */
	
	// 计算i到j日的最高价格的索引
	static public int indexStockTimeHigh(List<StockTime> list, int i, int j)
	{
		int index = i;
		float high = -100000.0f;
		for(int k = i; k<=j; k++ )
		{
			StockTime cStockTime = list.get(k);
			if(cStockTime.price > high) 
			{
				high = cStockTime.price;
				index = k;
			}
		}
		return index;
	}
	
	// 计算i到j日的最低价格的索引
	static public int indexStockTimeLow(List<StockTime> list, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			StockTime cStockTime = list.get(k);
			if(cStockTime.price < low) 
			{
				low = cStockTime.price;
				index = k;
			}
		}
		return index;
	}
}
