package stormstock.fw.tranbase.account;

import java.util.List;

abstract public class IAccountOpe {
	
	public static class HoldStock
	{
		public String id; // ��ƱID
		public int totalAmount; // ���������ɣ�
		public int totalCanSell; // ��������
		public float buyPrice; // �ɱ��۸� 
		public float transactionCosts; // ���׷���
	}
	
	public static class StockTranOrder
	{
		public String id;
		public int amount; 
		public float price;
	}

	public IAccountOpe()
	{
		
	}
	
	// ���տ�ʼ�˻���ʼ��
	abstract public boolean newDayInit();
	// ������ί�У�����ʵ���µ���
	abstract public int pushBuyOrder(String id, float price, int amount); 
	// ��������ί�У�����ʵ���µ���
	abstract public int pushSellOrder(String id, float price, int amount);
	// ����˻����ʲ�����Ʊ����ֵ+�ֽ�
	abstract public float getTotalAssets();
	// ����˻������ʽ��ֽ�
	abstract public float getAvailableMoney();
	
	// �����ί���б�(δ�ɽ���)
	abstract public List<StockTranOrder> getBuyOrderList();
	// ��óֹ��б������Ѿ����еģ��뵱���µ��ɽ��ģ�
	abstract public List<HoldStock> getHoldStockList();
	// �������ί���б�(δ�ɽ���)
	abstract public List<StockTranOrder> getSellOrderList();
}
