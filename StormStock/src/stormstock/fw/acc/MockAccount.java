package stormstock.fw.acc;

import java.util.ArrayList;
import java.util.List;

public class MockAccount extends IAccount {
	public MockAccount(float money, float transactionCostsRatio)
	{
		super();
		m_money = money;
		m_transactionCostsRatio = transactionCostsRatio;
		m_holdStockList = new ArrayList<HoldStock>();
	}

	@Override
	public int pushBuyOrder(String id, float price, int amount) {
		int maxBuyAmount = (int)(m_money/price);
		int realBuyAmount = maxBuyAmount>amount?amount:maxBuyAmount;
		m_money = m_money - realBuyAmount*price;
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String id, float price, int amount) {
		// TODO Auto-generated method stub
		return 0;
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
		return m_money;
	}

	@Override
	public List<HoldStock> getHoldStockList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private float m_money;
	private float m_transactionCostsRatio;
	private List<HoldStock> m_holdStockList;
}
