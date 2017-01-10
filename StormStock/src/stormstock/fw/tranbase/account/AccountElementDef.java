package stormstock.fw.tranbase.account;

public class AccountElementDef {
	
	/*
	 * 交易动作枚举
	 */
	enum TRANACT 
	{
		BUY,
		SELL,
	}
	
	/*
	 * 股票委托单定义
	 */
	public static class CommissionOrder 
	{
		public TRANACT tranOpe; // 交易动作
		public String id;
		public int amount; 
		public float price;
		
		public void CopyFrom(CommissionOrder c)
		{
			tranOpe = c.tranOpe;
			id = c.id;
			amount = c.amount;
			price = c.price;
		}
	}
	
	/*
	 * 股票交割单定义
	 */
	public static class DeliveryOrder 
	{
		public TRANACT tranOpe; // 交易动作
		public String id;
		public int amount; 
		public float price;
		public float transactionCost;
		
		public void CopyFrom(DeliveryOrder c)
		{
			tranOpe = c.tranOpe;
			id = c.id;
			amount = c.amount;
			price = c.price;
			transactionCost = c.transactionCost;
		}
	}
	
	/*
	 * 持股定义
	 */
	public static class HoldStock 
	{
		public String id; // 股票ID
		public int totalAmount; // 持有量（股）
		public int totalCanSell; // 可卖数量
		public float buyPrice; // 成本价格 
		public float transactionCosts; // 交易费用
	}
	
}
