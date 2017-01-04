package stormstock.app.progtran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.tran.ITranStockSetFilter;

public class TranStockSet extends ITranStockSetFilter {

	@Override
	public boolean strategy_stockset(Stock cStock) {
		if(cStock.id.compareTo("000001") >= 0 && cStock.id.compareTo("000200") <= 0) {	
			BLog.output("TEST", "add stockpool %s %s\n", cStock.id, cStock.latestInfo.name);
			return true;
		}
		return false;
	}

}
