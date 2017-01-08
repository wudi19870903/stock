package stormstock.fw.acc;

import java.util.List;

abstract public class IAccount {
	public class HoldStock
	{
		public String id; // ��ƱID
		public int totalAmount; // ���������ɣ�
		public int totalCanSell; // ��������
		public float buyPrices; // �ɱ��۸�
		public float transactionCosts; // ���׷���
	}
	
	public IAccount()
	{
		
	}
	
	// ����ʵ���µ���
	abstract public int pushBuyOrder(String id, float price, int amount); 
	abstract public int pushSellOrder(String id, float price, int amount);
	
	abstract public float getTotalAssets();
	
	abstract public float GetTotalMoney();
	abstract public float GetAvailableTotalMoney();
	
	abstract public List<HoldStock> getHoldStockList();
}
