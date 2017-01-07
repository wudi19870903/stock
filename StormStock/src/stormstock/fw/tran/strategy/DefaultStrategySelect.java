package stormstock.fw.tran.strategy;

public class DefaultStrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(StockContext ctx, SelectResult out_sr) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int strategy_select_max_count()
	{ 
		return 5; 
	}
}
