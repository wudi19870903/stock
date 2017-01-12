package stormstock.fw.report;

import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;

public class ReportPublicDef {
	
	/*
	 * 清仓交割单
	 */
	public static class ClearanceDeliveryOrder {
		public String clearDate;
		public String clearTime;
		public String stockID;        // 股票ID
		public int amount;            // 交易量
		public float holdAvePrice;    // 持有均价
		public float sellPrice;       // 交易价格
		public float transactionCost; // 交易费用
		
		public void CopyFrom(ClearanceDeliveryOrder c)
		{
			stockID = c.stockID;
			amount = c.amount;
			holdAvePrice = c.holdAvePrice;
			sellPrice = c.sellPrice;
			transactionCost = c.transactionCost;
		}
	}
}
