package stormstock.fw.tran.strategy;

abstract public class IStrategyCreate {
	// �����µ�����
	public static class CreateResult {
		public CreateResult() {
			bCreate = false;
		}
		public boolean bCreate;
	}
	abstract public void strategy_create(StockContext ctx, CreateResult out_sr);
}
