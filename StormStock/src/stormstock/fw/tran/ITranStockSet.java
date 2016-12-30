package stormstock.fw.tran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;

public class ITranStockSet {
	// 交易股票集
	public boolean strategy_stockset(Stock cStock) // 默认前200只
	{
		if(cStock.id.compareTo("000001") >= 0 && cStock.id.compareTo("000200") <= 0) {	
			BLog.output("TEST", "add stockpool %s %s\n", cStock.id, cStock.curBaseInfo.name);
			return true;
		}
		return false;
	}
}
