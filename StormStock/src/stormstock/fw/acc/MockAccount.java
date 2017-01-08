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
		
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.id == id)
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		
		if(null != cHoldStock)
		{
			cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		}
		else
		{
			HoldStock cNewHoldStock = new HoldStock();
			cNewHoldStock.id = id;
			cNewHoldStock.buyPrices = price;
			cNewHoldStock.totalAmount = realBuyAmount;
			m_holdStockList.add(cNewHoldStock);
		}
		
		return realBuyAmount;
	}

	@Override
	public int pushSellOrder(String id, float price, int amount) {
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.id.equals(id))
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		if(null != cHoldStock)
		{
			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			if(0 == cHoldStock.totalAmount)
			{
				m_holdStockList.remove(cHoldStock);
			}
			return realSellAmount;
		}
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
