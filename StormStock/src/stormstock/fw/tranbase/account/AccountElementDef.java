package stormstock.fw.tranbase.account;

public class AccountElementDef {
	
	/*
	 * ���׶���ö��
	 */
	enum TRANACT 
	{
		BUY,
		SELL,
	}
	
	/*
	 * ��Ʊί�е�����
	 */
	public static class CommissionOrder 
	{
		public TRANACT tranOpe; // ���׶���
		public String id;
		public int amount; 
		public float price;
		
		public void CopyFrom(CommissionOrder c)
		{
			tranOpe = c.tranOpe;
			id = c.id;
			amount = c.amount;
			price = c.price;
		}
	}
	
	/*
	 * ��Ʊ�������
	 */
	public static class DeliveryOrder 
	{
		public TRANACT tranOpe;       // ���׶���
		public String stockID;        // ��ƱID
		public int amount;            // ������
		public float holdAvePrice;    // ���о���
		public float tranPrice;       // ���׼۸�
		public float transactionCost; // ���׷���
		
		public void CopyFrom(DeliveryOrder c)
		{
			tranOpe = c.tranOpe;
			stockID = c.stockID;
			amount = c.amount;
			holdAvePrice = c.holdAvePrice;
			tranPrice = c.tranPrice;
			transactionCost = c.transactionCost;
		}
	}
	
	/*
	 * �ֹɶ���
	 */
	public static class HoldStock 
	{
		public String id; // ��ƱID
		public int totalAmount; // ���������ɣ�
		public int totalCanSell; // ��������
		public float holdAvePrice; // ���о��� 
		public float transactionCosts; // ���׷���
	}
	
}
