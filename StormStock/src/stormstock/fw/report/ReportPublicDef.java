package stormstock.fw.report;

import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;

public class ReportPublicDef {
	
	/*
	 * ��ֽ��
	 */
	public static class ClearanceDeliveryOrder {
		public String clearDate;
		public String clearTime;
		public String stockID;        // ��ƱID
		public int amount;            // ������
		public float holdAvePrice;    // ���о���
		public float sellPrice;       // ���׼۸�
		public float transactionCost; // ���׷���
		
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
