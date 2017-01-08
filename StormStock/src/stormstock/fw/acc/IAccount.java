package stormstock.fw.acc;

import java.util.List;

abstract public class IAccount {
	public class HoldStock
	{
		public String id; // 股票ID
		public int totalAmount; // 持有量（股）
		public int totalCanSell; // 可卖数量
		public float buyPrices; // 成本价格
		public float transactionCosts; // 交易费用
	}
	
	public IAccount()
	{
		
	}
	
	// 返回实际下单量
	abstract public int pushBuyOrder(String id, float price, int amount); 
	abstract public int pushSellOrder(String id, float price, int amount);
	
	abstract public float getTotalAssets();
	
	abstract public float GetTotalMoney();
	abstract public float GetAvailableTotalMoney();
	
	abstract public List<HoldStock> getHoldStockList();
}
