package stormstock.fw.tran;

import stormstock.fw.base.BLog;
import stormstock.fw.stockdata.Stock;

public class ITranStockSet {
	// ���׹�Ʊ��
	public boolean strategy_stockset(Stock cStock) // Ĭ��ǰ200ֻ
	{
		if(cStock.id.compareTo("000001") >= 0 && cStock.id.compareTo("000200") <= 0) {	
			BLog.output("TEST", "add stockpool %s %s\n", cStock.id, cStock.curBaseInfo.name);
			return true;
		}
		return false;
	}
}
