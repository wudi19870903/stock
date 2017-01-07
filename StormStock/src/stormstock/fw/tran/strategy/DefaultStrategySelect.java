package stormstock.fw.tran.strategy;

public class DefaultStrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(StockContext ctx, SelectResult out_sr) {
		out_sr.bSelect = false;
	}
	
	@Override
	public int strategy_select_max_count()
	{ 
		return 0; 
	}
}
