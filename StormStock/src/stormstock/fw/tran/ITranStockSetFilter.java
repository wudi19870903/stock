package stormstock.fw.tran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;

abstract public class ITranStockSetFilter {
	// 交易股票集
	abstract public boolean strategy_stockset(Stock cStock);
}
