package stormstock.fw.tranbase.account;

public class AccountPublicDef {
	
	/*
	 * ���׶���ö��
	 */
	public enum TRANACT 
	{
		BUY,
		SELL,
	}
	
	/*
	 * ��Ʊί�е�����
	 */
	public static class CommissionOrder 
	{
		public String date;
		public String time;
		public TRANACT tranOpe; // ���׶���
		public String stockID;
		public int amount; 
		public float price;
		
		public void CopyFrom(CommissionOrder c)
		{
			tranOpe = c.tranOpe;
			stockID = c.stockID;
			amount = c.amount;
			price = c.price;
		}
	}
	
	/*
	 * ��Ʊ�������
	 */
	public static class DeliveryOrder 
	{
		public String date;
		public String time;
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
		public HoldStock()
		{
			stockID = "";
			totalAmount = 0;
			totalCanSell = 0;
			holdAvePrice = 0.0f;
			curPrice = 0.0f;
			transactionCosts = 0.0f;
		}
		public String stockID; // ��ƱID
		public int totalAmount; // �����������ɣ�
		public int totalCanSell; // ��������
		public float holdAvePrice; // ���о��� 
		public float curPrice;   // ��ǰ�۸�
		public float transactionCosts; // ���׷���
	}
	
}
