package stormstock.fw.tran;

abstract public class IStrategyClear {
	// ����µ�����
	public static class ClearResult {
	}
	abstract public void strategy_clear(StockContext ctx, ClearResult out_sr);
}
