package stormstock.fw.tranbase.stockdata;

import java.util.List;

public class StockDay {

	public StockDay()
	{
	} 
	
	public void CopyFrom(StockDay c)
	{
		m_date = c.m_date;
		m_bEndTran = c.m_bEndTran;
		m_stockTimeList = c.m_stockTimeList;
		m_open = c.m_open;
		m_close = c.m_close;
		m_high = c.m_high;
		m_low = c.m_low;
		m_volume = c.m_volume;
	}

	// 构造设置历史日K
	public void set(String date, float open, float close, float low, float high, float volume)
	{
		m_date = date;
		m_bEndTran = true;
		m_stockTimeList = null;
		m_open = open;
		m_close = close;
		m_low = low;
		m_high = high;
		m_volume = volume;
	}
	// 构造设置分时日K
	public void set(String date, List<StockTime> stockTimeList)
	{
		m_date = date;
		m_bEndTran = false;
		m_stockTimeList = stockTimeList;
		
		m_open = 0.0f;
		m_close = 0.0f;
		m_low = 100000.0f;
		m_high = -100000.0f;
		m_volume = 0.0f;
		for(int i=0; i<m_stockTimeList.size();i++)
		{
			StockTime cStockTime = m_stockTimeList.get(i);
			if(0 == i)
			{
				m_open = cStockTime.price;
			}
			if(m_stockTimeList.size()-1 == i)
			{
				m_close = cStockTime.price;
			}
			if(cStockTime.price < m_low)
			{
				m_low = cStockTime.price;
			}
			if(cStockTime.price > m_high)
			{
				m_high = cStockTime.price;
			}
		}
	}
	
	public String date()
	{
		return m_date;
	}
	public boolean isEndTran()
	{
		return m_bEndTran;
	}
	public List<StockTime> getStockTimeList()
	{
		if(isEndTran())
		{
			return null;
		}
		else
		{
			return m_stockTimeList;
		}
	}
	public float open()
	{
		if(isEndTran())
		{
			return m_open;
		}
		else
		{
			return m_stockTimeList.get(0).price;
		}
	}
	public float close()
	{
		if(isEndTran())
		{
			return m_close;
		}
		else
		{
			return m_stockTimeList.get(m_stockTimeList.size()-1).price;
		}
	}
	public float high()
	{
		if(isEndTran())
		{
			return m_high;
		}
		else
		{
			float high = -10000.00f;
			for(int i=0; i< m_stockTimeList.size();i++)
			{
				if(m_stockTimeList.get(i).price > high)
				{
					high = m_stockTimeList.get(i).price;
				}
			}
			return high;
		}
	}
	public float low()
	{
		if(isEndTran())
		{
			return m_low;
		}
		else
		{
			float low = 10000.00f;
			for(int i=0; i< m_stockTimeList.size();i++)
			{
				if(m_stockTimeList.get(i).price < low)
				{
					low = m_stockTimeList.get(i).price;
				}
			}
			return low;
		}
	}
	public float midle()
	{
		return (open() + close()) / 2;
	}
	public float volume()
	{
		if(isEndTran())
		{
			return m_volume;
		}
		else
		{
			return 0.0f;
		}
	}
	
	
	/**
	 * 成员 *********************************************************************
	 */
	private String m_date;
	
	private boolean m_bEndTran;
	private List<StockTime> m_stockTimeList;
	
	private float m_open;
	private float m_close;
	private float m_low;
	private float m_high;
	private float m_volume;
}
