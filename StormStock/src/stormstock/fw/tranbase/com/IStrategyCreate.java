package stormstock.fw.tranbase.com;

abstract public class IStrategyCreate {
	// 建仓下单策略
	public static class CreateResult {
		public CreateResult() {
			bCreate = false;
			fMaxPositionRatio = 0.3333f;
			fMaxMoney = 10000*100;
		}
		public boolean bCreate; // 建仓标志
		public float fMaxPositionRatio; // 买入仓位
		public float fMaxMoney; // 建仓最大金额（最大值限制）
	}
	
	abstract public void strategy_create(TranContext ctx, CreateResult out_sr);
	abstract public int strategy_create_max_count();
}
