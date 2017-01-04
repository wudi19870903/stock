package stormstock.fw.tran;

abstract public class IStrategyCreate {
	// 建仓下单策略
	public static class CreateResult {
	}
	abstract public void strategy_create(StockContext ctx, CreateResult out_sr);
}
