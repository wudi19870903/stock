package stormstock.fw.tran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;

abstract public class ITranStockSetFilter {
	// ���׹�Ʊ��
	abstract public boolean strategy_stockset(Stock cStock);
}
