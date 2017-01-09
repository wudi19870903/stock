package stormstock.fw.tranbase.account;

import java.util.List;

abstract public class IAccountOpe {
	
	public static class HoldStock
	{
		public String id; // 股票ID
		public int totalAmount; // 持有量（股）
		public int totalCanSell; // 可卖数量
		public float buyPrice; // 成本价格 
		public float transactionCosts; // 交易费用
	}
	
	public static class StockTranOrder
	{
		public String id;
		public int amount; 
		public float price;
	}

	public IAccountOpe()
	{
		
	}
	
	// 隔日开始账户初始化
	abstract public boolean newDayInit();
	// 推送买单委托，返回实际下单量
	abstract public int pushBuyOrder(String id, float price, int amount); 
	// 推送卖单委托，返回实际下单量
	abstract public int pushSellOrder(String id, float price, int amount);
	// 获得账户总资产（股票总市值+现金）
	abstract public float getTotalAssets();
	// 获得账户可用资金（现金）
	abstract public float getAvailableMoney();
	
	// 获得买单委托列表(未成交的)
	abstract public List<StockTranOrder> getBuyOrderList();
	// 获得持股列表（包含已经持有的，与当天下单成交的）
	abstract public List<HoldStock> getHoldStockList();
	// 获得卖单委托列表(未成交的)
	abstract public List<StockTranOrder> getSellOrderList();
}
