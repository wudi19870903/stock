package stormstock.app.progtran;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.com.ITranStockSetFilter;
import stormstock.fw.tranbase.stockdata.StockInfo;

public class TranStockSet extends ITranStockSetFilter {

	@Override
	public boolean tran_stockset_byLatestStockInfo(StockInfo cStockInfo) {
		return true;
	}
}
