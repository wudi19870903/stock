package stormstock.fw.tran;

abstract public class IStrategyCreate {
	// �����µ�����
	public static class CreateResult {
	}
	abstract public void strategy_create(StockContext ctx, CreateResult out_sr);
}
