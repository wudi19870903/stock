package stormstock.fw.tranbase.stockdata;

import java.util.List;

/*
 * ��Ʊ���ݵ�ĳ����ʱ��ķ�����
 * ���Է������й�Ʊ��Ϣ
 */
public class StockDataAccessor {
	
	public StockDataAccessor(String date, String time, StockDataIF cStockDataIF)
	{
		m_date = date;
		m_time = time;
		m_stockDataIF = cStockDataIF;
	}
	
	/*
	 * ��ȡ���й�ƱId�б�
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
