package stormstock.fw.tranbase.account;

import java.util.List;

import stormstock.fw.tranbase.account.AccountElementDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountElementDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountElementDef.HoldStock;

public class RealAccountOpe extends IAccountOpe {

	@Override
	public boolean newDayInit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int pushBuyOrder(String id, float price, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int pushSellOrder(String id, float price, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAvailableMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<CommissionOrder> getCommissionOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HoldStock> getHoldStockList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeliveryOrder> getDeliveryOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

}
