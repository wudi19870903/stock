package stormstock.fw.tran;

abstract public class IStrategySelect {
	// ѡ�ɲ���
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
