package stormstock.fw.tran;

abstract public class IStrategySelect {
	// Ñ¡¹É²ßÂÔ
	public static class SelectResult {
		public SelectResult() {
			bSelect = false;
			fPriority = 0.0f;
		}
		public boolean bSelect;
		public float fPriority;
	}
	abstract public void strategy_select(Context ctx, SelectResult out_sr);
}
