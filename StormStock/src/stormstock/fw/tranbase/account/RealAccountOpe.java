package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.ori.capi.CATHSAccount;
import stormstock.ori.capi.CATHSAccount.ResultDealOrderList;

public class RealAccountOpe extends IAccountOpe {

	public RealAccountOpe(String accountID, String password)
	{
		m_stockSelectList = new ArrayList<String>();
		BLog.output("ACCOUNT", "Account REAL AccountID:%s Password:%s\n", 
				accountID, password);
	}
	
	@Override
	public boolean newDayInit(String date, String time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int pushBuyOrder(String date, String time, String id, float price, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int pushSellOrder(String date, String time, String id, float price, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAvailableMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStockSelectList(List<String> stockIDList) {
		m_stockSelectList.clear();
		for(int i=0; i<stockIDList.size();i++)
		{
			String newstockID = stockIDList.get(i);
			m_stockSelectList.add(newstockID);
		}
		
		// 选股中排除已经持有的
		List<HoldStock> cStockHoldList =  getHoldStockList(null,null);
		for(int i=0;i<cStockHoldList.size();i++)
		{
			m_stockSelectList.remove(cStockHoldList.get(i).stockID);
		}
	}

	@Override
	public List<String> getStockSelectList() {
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!help_inAccount(stockID))  // 选股列表排除掉已经在买入列表的
			{
				newList.add(stockID);
			}
		}
		return newList;
	}
	// 帮助函数 判断股票是否存在于 买卖单委托列表，持有列表中
	private boolean help_inAccount(String stockID)
	{
		List<CommissionOrder> cCommissionOrderList = this.getCommissionOrderList();
		for(int i=0;i<cCommissionOrderList.size();i++)
		{
			if(cCommissionOrderList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = this.getHoldStockList(null,null);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<CommissionOrder> getCommissionOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HoldStock> getHoldStockList(String date, String time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DealOrder> getDealOrderList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private List<String> m_stockSelectList; // 选股列表
}
