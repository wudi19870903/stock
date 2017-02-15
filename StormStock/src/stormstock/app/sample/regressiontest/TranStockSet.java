package stormstock.app.sample.regressiontest;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.com.ITranStockSetFilter;
import stormstock.fw.tranbase.stockdata.StockInfo;

public class TranStockSet extends ITranStockSetFilter {

	@Override
	public boolean tran_stockset_byLatestStockInfo(StockInfo cStockInfo) {
		if(cStockInfo.ID.compareTo("000001") >= 0 && cStockInfo.ID.compareTo("000200") <= 0) {	
			return true;
		}
		return false;
	}
}
