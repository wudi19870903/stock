package stormstock.fw.tran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockInfo;

abstract public class ITranStockSetFilter {
	// ���׹�Ʊ��
	abstract public boolean tran_stockset_byLatestInfo(StockInfo cStockInfo);
}
