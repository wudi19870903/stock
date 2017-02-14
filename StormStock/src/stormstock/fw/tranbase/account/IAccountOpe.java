package stormstock.fw.tranbase.account;

import java.util.List;

import stormstock.fw.base.BTypeDefine.RefFloat;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.stockdata.StockDay;

abstract public class IAccountOpe {
	
	public IAccountOpe() { }
	
	// ���տ�ʼ�˻���ʼ��
	abstract public int newDayInit(String date, String time);
	// ������ί�У�����ʵ���µ���
	abstract public int pushBuyOrder(String date, String time, String id, float price, int amount); 
	// ��������ί�У�����ʵ���µ���
	abstract public int pushSellOrder(String date, String time, String id, float price, int amount);
	// ����˻������ʽ��ֽ�
	abstract public int getAvailableMoney(RefFloat out_availableMoney);
	
	// �趨��ȡ��ѡ���б�
	abstract public int setStockSelectList(List<String> stockIDList);
	abstract public int getStockSelectList(List<String> out_list);
	
	// ���ί���б�(δ�ɽ��ģ����������������)
	abstract public int getCommissionOrderList(List<CommissionOrder> out_list);
	// ��óֹ��б������Ѿ����еģ��뵱���µ��ɽ��ģ�
	abstract public int getHoldStockList(String date, String time, List<HoldStock> out_list);
	// ��õ��ս���б��ѳɽ��ģ���������������ģ�
	abstract public int getDealOrderList(List<DealOrder> out_list);
}
