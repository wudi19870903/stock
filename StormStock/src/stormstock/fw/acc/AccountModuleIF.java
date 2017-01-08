package stormstock.fw.acc;

import java.util.List;

import stormstock.fw.acc.IAccountOpe.HoldStock;
import stormstock.fw.acc.IAccountOpe.StockTranOrder;
import stormstock.fw.base.BModuleBase.BModuleInterface;

public class AccountModuleIF extends BModuleInterface {
	
	public enum ACCIFTYPE // 账户接口类型
	{
		MOCK,
		REAL,
	}
	
	public AccountModuleIF(AccountControler cAccountControler)
	{
		m_accountControler = cAccountControler;
	}
	
	/*
	 * 账户隔日初始化
	 * 完成股票买卖单的更新等，比如隔日后股票可以卖出
	 */
	public boolean newDayInit()
	{
		return m_accountControler.newDayInit();
	}
	
	// 设置当前账户操作接口类型 模拟接口或真实接口
	public void setAccountIFType(ACCIFTYPE eAccIFType)
	{
		m_accountControler.setAccountIFType(eAccIFType);
	}
	
	// 获得账户总资产 市值+现金
	public float getTotalAssets()
	{
		return m_accountControler.getTotalAssets();
	}
	
	// 获得账户可用现金
	public float getAvailableMoney()
	{
		return m_accountControler.getAvailableMoney();
	}
	
	/*
	 *  推送买单委托
	 *  返回实际推送量
	 */
	public int pushBuyOrder(String stockID, float price, int amount)
	{
		return m_accountControler.pushBuyOrder(stockID, price, amount);
	}
	
	/*
	 *  推送卖单委托
	 *  返回实际推送量
	 */
	public int pushSellOrder(String stockID, float price, int amount)
	{
		return m_accountControler.pushSellOrder(stockID, price, amount);
	}
	
	/*
	 * 选股列表: 
	 * 此表是根据每天晚上的历史K线进行选入的股票集合
	 * 上层第二天应用此列表进行买入策略的分析
	 */
	// 选股列表：增加
	public void addStockSelectList(List<String> stockIDList)
	{
		m_accountControler.addStockSelectList(stockIDList);
	}
	// 选股列表：获取
	public List<String> getStockSelectList()
	{
		return m_accountControler.getStockSelectList();
	}
	// 选股列表：清除
	public boolean clearStockSelectList()
	{
		return m_accountControler.clearStockSelectList();
	}
	
	/*
	 * 买单委托列表（未成交）: 
	 */
	public List<StockTranOrder> getBuyOrderList()
	{
		return m_accountControler.getBuyOrderList();
	}

	/*
	 * 持股列表: 
	 * 上层可以根据此表进行卖出策略的执行
	 */
	// 持股列表：获取
	public List<HoldStock> getStockHoldList()
	{
		return m_accountControler.getStockHoldList();
	}
	public HoldStock getStockHold(String stockID)
	{
		return m_accountControler.getStockStock(stockID);
	}
	

	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private AccountControler m_accountControler;
}
