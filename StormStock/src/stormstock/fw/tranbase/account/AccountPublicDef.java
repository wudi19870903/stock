package stormstock.fw.tranbase.account;

public class AccountPublicDef {
	
	/*
	 * 交易动作枚举
	 */
	public enum TRANACT 
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
		public String stockID;
		public int amount; 
		public float price;
		
		public void CopyFrom(CommissionOrder c)
		{
			tranOpe = c.tranOpe;
			stockID = c.stockID;
			amount = c.amount;
			price = c.price;
		}
	}
	
	/*
	 * 股票交割单定义
	 */
	public static class DeliveryOrder 
	{
		public TRANACT tranOpe;       // 交易动作
		public String stockID;        // 股票ID
		public int amount;            // 交易量
		public float holdAvePrice;    // 持有均价
		public float tranPrice;       // 交易价格
		public float transactionCost; // 交易费用
		
		public void CopyFrom(DeliveryOrder c)
		{
			tranOpe = c.tranOpe;
			stockID = c.stockID;
			amount = c.amount;
			holdAvePrice = c.holdAvePrice;
			tranPrice = c.tranPrice;
			transactionCost = c.transactionCost;
		}
	}
	
	/*
	 * 持股定义
	 */
	public static class HoldStock 
	{
		public HoldStock()
		{
			stockID = "";
			totalAmount = 0;
			totalCanSell = 0;
			holdAvePrice = 0.0f;
			transactionCosts = 0.0f;
		}
		public String stockID; // 股票ID
		public int totalAmount; // 持有总量（股）
		public int totalCanSell; // 可卖数量
		public float holdAvePrice; // 持有均价 
		public float transactionCosts; // 交易费用
	}
	
}
