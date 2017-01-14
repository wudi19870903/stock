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
		public String date;
		public String time;
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
		public String date;
		public String time;
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
			createDate = "0000-00-00";
			createTime = "00:00:00";
			holdDayCnt = 0;
			totalAmount = 0;
			totalCanSell = 0;
			holdAvePrice = 0.0f;
			curPrice = 0.0f;
			transactionCosts = 0.0f;
		}
		
		public float profit() // 利润值（盈亏金额，不计算交易费用）
		{
			return (curPrice - holdAvePrice)*totalAmount;
		}
		public float profitRatio() // 利润比（盈亏比例）
		{
			return (curPrice - holdAvePrice)/holdAvePrice;
		}
		
		/**
		 * 成员 *************************************************************
		 */
		public String stockID; // 股票ID
		public String createDate; // 建仓日期
		public String createTime; // 建仓时间
		public int holdDayCnt; // 持股天数（当天买入为0天）
		public int totalAmount; // 持有总量（股）
		public int totalCanSell; // 可卖数量
		public float holdAvePrice; // 持有均价 
		public float curPrice;   // 当前价格
		public float transactionCosts; // 交易费用
	}
	
}
