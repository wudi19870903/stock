package stormstock.app.progtran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.tran.ITranStockSetFilter;

public class TranStockSet extends ITranStockSetFilter {

	@Override
	public boolean tran_stockset_byLatestInfo(StockInfo cStockInfo) {
		if(cStockInfo.ID.compareTo("000001") >= 0 && cStockInfo.ID.compareTo("000200") <= 0) {	
			BLog.output("TEST", "add stockpool %s %s\n", cStockInfo.ID, cStockInfo.name);
			return true;
		}
		return false;
	}
}
