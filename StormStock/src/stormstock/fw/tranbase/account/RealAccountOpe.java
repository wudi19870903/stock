package stormstock.fw.tranbase.account;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BTypeDefine.RefFloat;
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
	public int newDayInit(String date, String time) {
		// TODO Auto-generated method stub
		return 0;
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
	public int getAvailableMoney(RefFloat out_availableMoney) {
		// TODO Auto-generated method stub
		out_availableMoney.value = 0.0f;
		return 0;
	}

	@Override
	public int setStockSelectList(List<String> stockIDList) {
		m_stockSelectList.clear();
		for(int i=0; i<stockIDList.size();i++)
		{
			String newstockID = stockIDList.get(i);
			m_stockSelectList.add(newstockID);
		}
		
		// 选股中排除已经持有的
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		getHoldStockList(null,null,cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			m_stockSelectList.remove(cHoldStockList.get(i).stockID);
		}
		
		return 0;
	}

	@Override
	public int getStockSelectList(List<String> out_list) {
		out_list.clear();
		for(int i=0; i< m_stockSelectList.size();i++)
		{
			String stockID = m_stockSelectList.get(i);
			if(!help_inAccount(stockID))  // 选股列表排除掉已经在买入列表的
			{
				out_list.add(stockID);
			}
		}
		return 0;
	}
	// 帮助函数 判断股票是否存在于 买卖单委托列表，持有列表中
	private boolean help_inAccount(String stockID)
	{
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
		for(int i=0;i<cCommissionOrderList.size();i++)
		{
			if(cCommissionOrderList.get(i).stockID.equals(stockID))
			{
				return true;
			}
		}
		
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		this.getHoldStockList(null,null,cHoldStockList);
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
	public int getCommissionOrderList(List<CommissionOrder> out_list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHoldStockList(String date, String time, List<HoldStock> out_list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDealOrderList(List<DealOrder> out_list) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private List<String> m_stockSelectList; // 选股列表
}
