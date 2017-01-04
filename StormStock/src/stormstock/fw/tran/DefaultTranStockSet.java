package stormstock.fw.tran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;

public class DefaultTranStockSet extends ITranStockSetFilter {

	@Override
	public boolean strategy_stockset(Stock cStock) {
		// TODO Auto-generated method stub
		if(cStock.id.compareTo("000001") >= 0 && cStock.id.compareTo("000200") <= 0) {	
			BLog.output("TEST", "add stockpool %s %s\n", cStock.id, cStock.latestBaseInfo.name);
			return true;
		}
		return false;
	}
}
