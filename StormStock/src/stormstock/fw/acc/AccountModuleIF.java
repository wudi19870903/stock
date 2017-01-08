package stormstock.fw.acc;

import java.util.List;

import stormstock.fw.acc.AccountControler.StockCreate;
import stormstock.fw.base.BModuleBase.BModuleInterface;

public class AccountModuleIF extends BModuleInterface {
	public AccountModuleIF(AccountControler cAccountControler)
	{
		m_accountControler = cAccountControler;
	}
	
	// ���õ�ǰ�˻�
	public void setCurAccount(IAccount cIAccount)
	{
		m_accountControler.setCurAccount(cIAccount);
	}
	public float getTotalAssets()
	{
		return m_accountControler.getTotalAssets();
	}
	public float getAvailableTotalMoney()
	{
		return m_accountControler.getAvailableTotalMoney();
	}
	
	// ѡ���б���Ӻϲ�
	public void addStockSelectList(List<String> stockIDList)
	{
		m_accountControler.addStockSelectList(stockIDList);
	}
	// ѡ���б��ȡ
	public List<String> getStockSelectList()
	{
		return m_accountControler.getStockSelectList();
	}
	
	// �������
	public int buyStock(String stockID, float price, int amount)
	{
		return m_accountControler.buyStock(stockID, price, amount);
	}
	// �����б�
	public List<StockCreate> getStockCreateList()
	{
		return m_accountControler.getStockCreateList();
	}
	public StockCreate getStockCreate(String stockID)
	{
		return m_accountControler.getStockCreate(stockID);
	}
	// �Ƿ��Ѿ������ж�
	public boolean isInStockCreate(String stockID)
	{
		return m_accountControler.isInStockCreate(stockID);
	}
	
	// ��������
	public int sellStock(String stockID, float price, int amount)
	{
		return m_accountControler.sellStock(stockID, price, amount);
	}
	
	private AccountControler m_accountControler;
}
