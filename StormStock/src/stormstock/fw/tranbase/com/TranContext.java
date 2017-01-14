package stormstock.fw.tranbase.com;

import stormstock.fw.tranbase.account.AccountAccessor;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataAccessor;

public class TranContext {
	
	public static class Target
	{
		public Target(Stock cStock, HoldStock cHoldStock)
		{
			m_stock = cStock;
			m_holdStock = cHoldStock;
		}
		public Object getStockEigen(String eigenName, Object... args)
		{
			IEigenStock cIEigenStock = GlobalUserObj.getStockEigen(eigenName);
			if(null == cIEigenStock) 
				return null;
			Object engenObj = cIEigenStock.calc(m_stock, args);
			return engenObj;
		}
		public Stock stock() 
		{ 
			return m_stock; 
		}
		public HoldStock holdStock() 
		{ 
			return m_holdStock; 
		}
		
		// 股票数据
		private Stock m_stock;
		// 持股数据
		private HoldStock m_holdStock;
	}
	
	public TranContext(String date, String time,
			Target cTarget, 
			AccountAccessor cAccountAccessor,
			StockDataAccessor cStockDataAccessor)
	{
		m_date = date;
		m_time = time;
		m_target = cTarget;
		m_accountAccessor = cAccountAccessor;
		m_stockDataAccessor = cStockDataAccessor;
	}
	
	public String date() 
	{ 
		return m_date; 
	}
	public String time() 
	{ 
		return m_time; 
	}
	public Target target() 
	{ 
		return m_target; 
	}
	public AccountAccessor accountAccessor() 
	{ 
		return m_accountAccessor; 
	}
	public StockDataAccessor stockDataAccessor()
	{
		return m_stockDataAccessor;
	}
	
	/**
	 * 成员 ************************************************************************
	 */
	
	// 当前日期时间
	private String m_date;
	private String m_time;
	
	// 交易目标
	private Target m_target;
	
	// 账户访问器
	private AccountAccessor m_accountAccessor;
	// 股票数据访问器
	private StockDataAccessor m_stockDataAccessor;
}
