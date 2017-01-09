package stormstock.fw.tranbase.com;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockInfo;

abstract public class ITranStockSetFilter {
	// 交易股票集
	abstract public boolean tran_stockset_byLatestStockInfo(StockInfo cStockInfo);
}
