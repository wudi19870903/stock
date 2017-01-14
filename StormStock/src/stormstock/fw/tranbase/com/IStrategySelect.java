package stormstock.fw.tranbase.com;

abstract public class IStrategySelect {
	
	public static class SelectResult {
		public SelectResult() {
			bSelect = false;
			fPriority = 0.0f;
		}
		public boolean bSelect;
		public float fPriority;
	}
	
	abstract public void strategy_select(TranContext ctx, SelectResult out_sr);
	
	abstract public int strategy_select_max_count();
}
