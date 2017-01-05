package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.analysis.ANLEigen;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.tran.eigen.IEigenStock;

public class Stock {
	
	public Stock()
	{
		m_LatestStockInfo = new StockInfo();
		m_stockDayList = new ArrayList<StockDay>();
		m_stockTimeList = new ArrayList<StockTime>();
	}	 
	
	// 获得最后一天的昨日收盘价
	public float GetLastYesterdayClosePrice()
	{
		if(m_stockDayList.size() > 1) // 2天以上
			return m_stockDayList.get(m_stockDayList.size()-2).close;
		else if(m_stockDayList.size() > 0) // 只有一天情况，昨收就是今开
			return m_stockDayList.get(m_stockDayList.size()-1).open;
		else
			return 0.0f;
	}
	
	// 获得最后一天的开盘价
	public float GetLastOpenPrice()
	{
		if(m_stockDayList.size() > 0)
			return m_stockDayList.get(m_stockDayList.size()-1).open;
		else
			return 0.0f;
	}

	// 获得最后一天的开盘时的百分比
	public float GetLastOpenRatio()
	{
		if(m_stockDayList.size() > 0)
		{
			float fYesterdayClose = GetLastYesterdayClosePrice();
			float fLastOpen = GetLastOpenPrice();
			float fLastOpenRatio = (fLastOpen - fYesterdayClose)/fYesterdayClose;
			return fLastOpenRatio;
		}
		else
			return 0.0f;
	}
	
	// 获得最后一天的收盘价
	public float GetLastClosePrice()
	{
		if(m_stockDayList.size() > 0)
			return m_stockDayList.get(m_stockDayList.size()-1).close;
		else
			return 0.0f;
	}
	
	// 获得第一天的日期
	public String GetFirstDate()
	{
		if(m_stockDayList.size() > 0)
			return m_stockDayList.get(0).date;
		else
			return "0000-00-00";
	}
	
	// 获得最后一天的日期
	public String GetLastDate()
	{
		if(m_stockDayList.size() > 0)
			return m_stockDayList.get(m_stockDayList.size()-1).date;
		else
			return "0000-00-00";
	}
	
	// 均线计算，计算date日期前count天均线价格
	public float GetMA(int count, String date)
	{
		if(m_stockDayList.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = StockUtils.indexDayKBeforeDate(m_stockDayList, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = m_stockDayList.get(i);  
			sum = sum + cDayKData.close;
			sumcnt++;
			//Log.outputConsole("%s %.2f\n", cDayKData.date, cDayKData.close);
        }
		value = sum/sumcnt;
		return value;
	}
	
	// 高值计算，计算date日期前count天最高价格
	public float GetHigh(int count, String date)
	{
		if(m_stockDayList.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = StockUtils.indexDayKBeforeDate(m_stockDayList, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = m_stockDayList.get(i);  
			if(cDayKData.high >= value)
			{
				value = cDayKData.high;
			}
			//Log.outputConsole("%s %.2f\n", cDayKData.date, cDayKData.close);
        }
		return value;
	}
	
	// 低值计算，计算date日期前count天最低价格
	public float GetLow(int count, String date)
	{
		if(m_stockDayList.size() == 0) return 0.0f;
		float value = 10000.0f;
		int iE = StockUtils.indexDayKBeforeDate(m_stockDayList, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = m_stockDayList.get(i);  
			if(cDayKData.low <= value)
			{
				value = cDayKData.low;
			}
			//Log.outputConsole("%s %.2f\n", cDayKData.date, cDayKData.close);
        }
		return value;
	}
	
	public StockDay GetDayK(String date)
	{
		int i = StockUtils.indexDayKAfterDate(m_stockDayList, date);
		if(i>=0)
		{
			return m_stockDayList.get(i);
		}
		else
		{
			return null;
		}
	}
	
	public Object getEngen(String name, Object... args)
	{
		IEigenStock cIEigenStock = GlobalUserObj.getStockEigen(name);
		if(null == cIEigenStock) return null;
		Object engenObj = cIEigenStock.calc(this, args);
		return engenObj;
	}
	
	/*
	 * ******************************************************************************************
	 */
	
	public String getDate() { return m_date; }
	public void setDate(String date) { m_date = date; }
	
	public String getTime() { return m_time; }
	public void setTime(String time) { m_time = time; }
	
	public StockInfo getCurLatestStockInfo() { return m_LatestStockInfo; }
	public void setCurLatestStockInfo(StockInfo stockInfo) { m_LatestStockInfo = stockInfo; }
	
	public List<StockDay> getCurStockDayData() { return m_stockDayList; }
	public void setCurStockDayData(List<StockDay> stockDayData) { m_stockDayList = stockDayData; }
	
	public List<StockTime> getCurStockTimeData() { return m_stockTimeList; }
	public void setCurStockTimeData(List<StockTime> stockTimeData) { m_stockTimeList = stockTimeData; }
	
	private String m_date;
	private String m_time;
	
	private StockInfo m_LatestStockInfo;
	private List<StockDay> m_stockDayList;
	private List<StockTime> m_stockTimeList;
}
