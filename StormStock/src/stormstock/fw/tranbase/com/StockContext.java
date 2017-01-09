package stormstock.fw.tranbase.com;

import stormstock.fw.tranbase.stockdata.Stock;

public class StockContext {
	
	public Stock getCurStock() 
	{ 
		return m_curStock; 
	}
	
	public void setCurStock(Stock cStock) 
	{ 
		m_curStock = cStock; 
	}
	
	public Object getCurStockEigen(String eigenName, Object... args)
	{
		IEigenStock cIEigenStock = GlobalUserObj.getStockEigen(eigenName);
		if(null == cIEigenStock) 
			return null;
		Object engenObj = cIEigenStock.calc(m_curStock, args);
		return engenObj;
	}
	
	private Stock m_curStock;
}
