package stormstock.fw.tranbase.account;

public class AccountPublicDef {
	
	public enum ACCOUNTTYPE 
	{
		MOCK,
		REAL,
	}
	
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
		public TRANACT tranAct; // ���׶���
		public String stockID;
		public int amount; 
		public float price;
		
		public void CopyFrom(CommissionOrder c)
		{
			tranAct = c.tranAct;
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
		public TRANACT tranAct;       // ���׶���
		public String stockID;        // ��ƱID
		public int amount;            // ������
		public float holdAvePrice;    // ���о���
		public float tranPrice;       // ���׼۸�
		public float transactionCost; // ���׷���
		
		public void CopyFrom(DeliveryOrder c)
		{
			tranAct = c.tranAct;
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
			createDate = "0000-00-00";
			createTime = "00:00:00";
			holdDayCnt = 0;
			totalAmount = 0;
			totalCanSell = 0;
			holdAvePrice = 0.0f;
			curPrice = 0.0f;
			transactionCost = 0.0f;
		}
		
		public float profit() // ����ֵ��ӯ���������㽻�׷��ã�
		{
			return (curPrice - holdAvePrice)*totalAmount;
		}
		public float profitRatio() // ����ȣ�ӯ��������
		{
			return (curPrice - holdAvePrice)/holdAvePrice;
		}
		
		/**
		 * ��Ա *************************************************************
		 */
		public String stockID; // ��ƱID
		public String createDate; // ��������
		public String createTime; // ����ʱ��
		public int holdDayCnt; // �ֹ���������������Ϊ0�죩
		public int totalAmount; // �����������ɣ�
		public int totalCanSell; // ��������
		public float holdAvePrice; // ���о��� 
		public float curPrice;   // ��ǰ�۸�
		public float transactionCost; // ���׷���
	}
	
}
