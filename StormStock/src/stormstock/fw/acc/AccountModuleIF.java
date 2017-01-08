package stormstock.fw.acc;

import java.util.List;

import stormstock.fw.acc.IAccountOpe.HoldStock;
import stormstock.fw.acc.IAccountOpe.StockTranOrder;
import stormstock.fw.base.BModuleBase.BModuleInterface;

public class AccountModuleIF extends BModuleInterface {
	
	public enum ACCIFTYPE // �˻��ӿ�����
	{
		MOCK,
		REAL,
	}
	
	public AccountModuleIF(AccountControler cAccountControler)
	{
		m_accountControler = cAccountControler;
	}
	
	/*
	 * �˻����ճ�ʼ��
	 * ��ɹ�Ʊ�������ĸ��µȣ�������պ��Ʊ��������
	 */
	public boolean newDayInit()
	{
		return m_accountControler.newDayInit();
	}
	
	// ���õ�ǰ�˻������ӿ����� ģ��ӿڻ���ʵ�ӿ�
	public void setAccountIFType(ACCIFTYPE eAccIFType)
	{
		m_accountControler.setAccountIFType(eAccIFType);
	}
	
	// ����˻����ʲ� ��ֵ+�ֽ�
	public float getTotalAssets()
	{
		return m_accountControler.getTotalAssets();
	}
	
	// ����˻������ֽ�
	public float getAvailableMoney()
	{
		return m_accountControler.getAvailableMoney();
	}
	
	/*
	 *  ������ί��
	 *  ����ʵ��������
	 */
	public int pushBuyOrder(String stockID, float price, int amount)
	{
		return m_accountControler.pushBuyOrder(stockID, price, amount);
	}
	
	/*
	 *  ��������ί��
	 *  ����ʵ��������
	 */
	public int pushSellOrder(String stockID, float price, int amount)
	{
		return m_accountControler.pushSellOrder(stockID, price, amount);
	}
	
	/*
	 * ѡ���б�: 
	 * �˱��Ǹ���ÿ�����ϵ���ʷK�߽���ѡ��Ĺ�Ʊ����
	 * �ϲ�ڶ���Ӧ�ô��б����������Եķ���
	 */
	// ѡ���б�����
	public void addStockSelectList(List<String> stockIDList)
	{
		m_accountControler.addStockSelectList(stockIDList);
	}
	// ѡ���б���ȡ
	public List<String> getStockSelectList()
	{
		return m_accountControler.getStockSelectList();
	}
	// ѡ���б����
	public boolean clearStockSelectList()
	{
		return m_accountControler.clearStockSelectList();
	}
	
	/*
	 * ��ί���б�δ�ɽ���: 
	 */
	public List<StockTranOrder> getBuyOrderList()
	{
		return m_accountControler.getBuyOrderList();
	}

	/*
	 * �ֹ��б�: 
	 * �ϲ���Ը��ݴ˱�����������Ե�ִ��
	 */
	// �ֹ��б���ȡ
	public List<HoldStock> getStockHoldList()
	{
		return m_accountControler.getStockHoldList();
	}
	public HoldStock getStockHold(String stockID)
	{
		return m_accountControler.getStockStock(stockID);
	}
	

	/**
	 * ��Ա-----------------------------------------------------------------------
	 */
	private AccountControler m_accountControler;
}
