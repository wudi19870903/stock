package stormstock.fw.tran;

abstract public class StrategyClear {
	// 清仓下单策略
	public static class ClearResult {
	}
	abstract public void strategy_clear(Context ctx, ClearResult out_sr);
}
