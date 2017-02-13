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
		
		// ѡ�����ų��Ѿ����е�
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
			if(!help_inAccount(stockID))  // ѡ���б��ų����Ѿ��������б��
			{
				newList.add(stockID);
			}
		}
		return newList;
	}
	// �������� �жϹ�Ʊ�Ƿ������ ������ί���б������б���
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
	 * ��Ա-----------------------------------------------------------------------
	 */
	private List<String> m_stockSelectList; // ѡ���б�
}
