package stormstock.fw.tranbase.com;

import stormstock.fw.tranbase.account.AccountAccessor;
import stormstock.fw.tranbase.stockdata.Stock;

public class StockContext {
	
	public String date() 
	{ 
		return m_date; 
	}
	public String time() 
	{ 
		return m_time; 
	}
	
	public Stock stock() 
	{ 
		return m_stock; 
	}
	
	public AccountAccessor account() 
	{ 
		return m_account; 
	}
	
	public Object getStockEigen(String eigenName, Object... args)
	{
		IEigenStock cIEigenStock = GlobalUserObj.getStockEigen(eigenName);
		if(null == cIEigenStock) 
			return null;
		Object engenObj = cIEigenStock.calc(m_stock, args);
		return engenObj;
	}
	
	// *********************************************************************************
	
	public void setDate(String date) 
	{ 
		m_date = date; 
	}
	public void setTime(String time) 
	{ 
		m_time = time; 
	}
	public void setStock(Stock cStock) 
	{ 
		m_stock = cStock; 
	}
	public void setAccountAccessor(AccountAccessor cAccount)
	{
		m_account = cAccount;
	}
	
	private String m_date;
	private String m_time;
	private Stock m_stock;
	private AccountAccessor m_account;
}
