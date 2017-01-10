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
		public TRANACT tranOpe; // ���׶���
		public String id;
		public int amount; 
		public float price;
		public float transactionCost;
		
		public void CopyFrom(DeliveryOrder c)
		{
			tranOpe = c.tranOpe;
			id = c.id;
			amount = c.amount;
			price = c.price;
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
		public float buyPrice; // �ɱ��۸� 
		public float transactionCosts; // ���׷���
	}
	
}
