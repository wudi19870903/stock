package stormstock.app.sample.regressiontest;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.com.ITranStockSetFilter;
import stormstock.fw.tranbase.stockdata.StockInfo;

public class TranStockSet extends ITranStockSetFilter {

	@Override
	public boolean tran_stockset_byLatestStockInfo(StockInfo cStockInfo) {
		if(cStockInfo.ID.compareTo("0001000") >= 0 && cStockInfo.ID.compareTo("002000") <= 0) {	
			return true;
		}
		return false;
	}
}
