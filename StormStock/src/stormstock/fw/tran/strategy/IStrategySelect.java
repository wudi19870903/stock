package stormstock.fw.tran.strategy;

abstract public class IStrategySelect {
	
	public static class SelectResult {
		public SelectResult() {
			bSelect = false;
			fPriority = 0.0f;
		}
		public boolean bSelect;
		public float fPriority;
	}
	
	abstract public void strategy_select(StockContext ctx, SelectResult out_sr);
	
	public int strategy_select_max_count()
	{ 
		return 10; 
	}
}
