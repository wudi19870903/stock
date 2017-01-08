package stormstock.fw.acc;

import java.util.List;

import stormstock.fw.acc.AccountControler.StockCreate;
import stormstock.fw.base.BModuleBase.BModuleInterface;

public class AccountModuleIF extends BModuleInterface {
	public AccountModuleIF(AccountControler cAccountControler)
	{
		m_accountControler = cAccountControler;
	}
	
	// 设置当前账户
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
	
	// 选股列表添加合并
	public void addStockSelectList(List<String> stockIDList)
	{
		m_accountControler.addStockSelectList(stockIDList);
	}
	// 选股列表获取
	public List<String> getStockSelectList()
	{
		return m_accountControler.getStockSelectList();
	}
	
	// 买入调用
	public int buyStock(String stockID, float price, int amount)
	{
		return m_accountControler.buyStock(stockID, price, amount);
	}
	// 持有列表
	public List<StockCreate> getStockCreateList()
	{
		return m_accountControler.getStockCreateList();
	}
	// 是否已经持有判断
	public boolean isInStockCreate(String stockID)
	{
		return m_accountControler.isInStockCreate(stockID);
	}
	
	
	private AccountControler m_accountControler;
}
