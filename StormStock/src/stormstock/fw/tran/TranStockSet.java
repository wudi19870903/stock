package stormstock.fw.tran;

import stormstock.fw.stockdata.Stock;

abstract public class TranStockSet {
	// 交易股票集
	abstract public boolean strategy_stockset(Stock cStock);
}
