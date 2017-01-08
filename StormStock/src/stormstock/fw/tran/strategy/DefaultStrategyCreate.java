package stormstock.fw.tran.strategy;

public class DefaultStrategyCreate extends IStrategyCreate {

	@Override
	public void strategy_create(StockContext ctx, CreateResult out_sr) {
		out_sr.bCreate = false;
	}

	@Override
	public int strategy_create_max_count() {
		return 0;
	}

}
