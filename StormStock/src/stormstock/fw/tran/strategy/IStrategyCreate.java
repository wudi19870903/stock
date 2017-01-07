package stormstock.fw.tran.strategy;

abstract public class IStrategyCreate {
	// 建仓下单策略
	public static class CreateResult {
		public CreateResult() {
			bCreate = false;
			fPositionRatio = 0.3f;
			fMaxMoney = 10000*100;
		}
		public boolean bCreate; // 建仓标志
		public float fPositionRatio; // 买入仓位
		public float fMaxMoney; // 建仓最大金额（最大值限制）
	}
	
	abstract public void strategy_create(StockContext ctx, CreateResult out_sr);
	abstract public int strategy_create_max_count(StockContext ctx, CreateResult out_sr);
}
