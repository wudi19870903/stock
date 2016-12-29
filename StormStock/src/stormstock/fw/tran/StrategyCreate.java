package stormstock.fw.tran;

abstract public class StrategyCreate {
	// 建仓下单策略
	public static class CreateResult {
	}
	abstract public void strategy_create(Context ctx, CreateResult out_sr);
}
