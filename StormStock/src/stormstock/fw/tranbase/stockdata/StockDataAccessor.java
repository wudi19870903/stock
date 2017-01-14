package stormstock.fw.tranbase.stockdata;

import java.util.List;

/*
 * 股票数据的某日期时间的访问器
 * 可以访问所有股票信息
 */
public class StockDataAccessor {
	
	public StockDataAccessor(String date, String time, StockDataIF cStockDataIF)
	{
		m_date = date;
		m_time = time;
		m_stockDataIF = cStockDataIF;
	}
	
	/*
	 * 获取所有股票Id列表
	 */
	public List<String> getAllStockID()
	{
		return m_stockDataIF.getAllStockID();
	}
	
	public List<StockDay> getHistoryData(String stockID)
	{
		return m_stockDataIF.getHistoryData(stockID, m_date);
	}
	
	
	private String m_date;
	private String m_time;
	private StockDataIF m_stockDataIF;
}
