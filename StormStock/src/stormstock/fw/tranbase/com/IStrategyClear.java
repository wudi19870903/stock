package stormstock.fw.tranbase.com;

abstract public class IStrategyClear {
	// 清仓下单策略
	public static class ClearResult {
		public ClearResult() {
			bClear = false;
		}
		public boolean bClear; // 清仓标志
	}
	abstract public void strategy_clear(TranContext ctx, ClearResult out_sr);
}
