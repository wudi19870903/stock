package stormstock.analysis;

abstract public class ANLStrategy {
	public static class SelectResult {
		public SelectResult() {
			bSelect = false;
			fPriority = 0.0f;
		}
		public boolean bSelect;
		public float fPriority;
	}
	
	abstract public boolean strategy_preload(ANLStock cANLStock);
	abstract public void strategy_select(String in_date, ANLStock in_stock, SelectResult out_sr);
}
