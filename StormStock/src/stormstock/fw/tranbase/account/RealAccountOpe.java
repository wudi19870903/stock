package stormstock.fw.tranbase.account;

import java.util.List;

public class RealAccountOpe extends IAccountOpe {
	
	public RealAccountOpe()
	{
		super();
	}

	@Override
	public float getTotalAssets() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAvailableMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<HoldStock> getHoldStockList() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean newDayInit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<StockTranOrder> getBuyOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StockTranOrder> getSellOrderList() {
		// TODO Auto-generated method stub
		return null;
	}
}
