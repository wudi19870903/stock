package stormstock.fw.tran;

abstract public class StrategyClear {
	// ����µ�����
	public static class ClearResult {
	}
	abstract public void strategy_clear(Context ctx, ClearResult out_sr);
}
