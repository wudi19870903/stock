package stormstock.fw.tran;

import java.util.List;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.stockdata.StockTime;

public class StockContext {
	
	/*
	 * date time
	 */
	public String getDate() { return m_date; }
	public String getTime() { return m_time; }
	
	/*
	 * current stock
	 */
	public StockInfo getCurStockInfo() { return m_stockInfo; }
	public List<StockDay> getCurStockDayData() { return m_stockDayData; }
	public List<StockTime> getCurStockTimeData() { return m_stockTimeData; }
	
	// ********************************************************************************
	
	public void setDate(String date) { m_date = date; }
	public void setTime(String time) { m_time = time; }
	
	public void setCurStockInfo(StockInfo stockInfo) { m_stockInfo = stockInfo; }
	public void setCurStockDayData(List<StockDay> stockDayData) { m_stockDayData = stockDayData; }
	public void setCurStockTimeData(List<StockTime> stockTimeData) { m_stockTimeData = stockTimeData; }
	
	private String m_date;
	private String m_time;
	
	private StockInfo m_stockInfo;
	private List<StockDay> m_stockDayData;
	private List<StockTime> m_stockTimeData;
}
