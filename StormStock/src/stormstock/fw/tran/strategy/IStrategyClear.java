package stormstock.fw.tran.strategy;

abstract public class IStrategyClear {
	// ����µ�����
	public static class ClearResult {
	}
	abstract public void strategy_clear(StockContext ctx, ClearResult out_sr);
}
