package stormstock.fw.acc;

import java.util.List;

public class RealAccount extends IAccount {
	public RealAccount()
	{
		super();
	}

	@Override
	public float getTotalAssets() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float GetTotalMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float GetAvailableTotalMoney() {
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
}
