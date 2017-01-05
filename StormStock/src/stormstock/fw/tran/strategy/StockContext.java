package stormstock.fw.tran.strategy;

import stormstock.fw.stockdata.Stock;

public class StockContext {
	
	public Stock getCurStock() { return m_curStock; }
	public void setCurStock(Stock cStock) { m_curStock = cStock; }
	
	private Stock m_curStock;
}
