package stormstock.fw.tranbase.account;

import java.util.List;

import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.stockdata.StockDay;

abstract public class IAccountOpe {
	
	public IAccountOpe() { }
	
	// ���տ�ʼ�˻���ʼ��
	abstract public boolean newDayInit();
	// ������ί�У�����ʵ���µ���
	abstract public int pushBuyOrder(String id, float price, int amount); 
	// ��������ί�У�����ʵ���µ���
	abstract public int pushSellOrder(String id, float price, int amount);
	// ����˻������ʽ��ֽ�
	abstract public float getAvailableMoney();
	
	// ���ί���б�(δ�ɽ��ģ����������������)
	abstract public List<CommissionOrder> getCommissionOrderList();
	// ��óֹ��б������Ѿ����еģ��뵱���µ��ɽ��ģ�
	abstract public List<HoldStock> getHoldStockList();
	// ��õ��ս���б��ѳɽ��ģ���������������ģ�
	abstract public List<DeliveryOrder> getDeliveryOrderList();
}
