package stormstock.app.sample.regressiontest;

import stormstock.fw.tran.strategy.IStrategyClear;
import stormstock.fw.tran.strategy.StockContext;

public class StrategyClear extends IStrategyClear {

	@Override
	public void strategy_clear(StockContext ctx, ClearResult out_sr) {
		// TODO Auto-generated method stub
		out_sr.bClear = true;
	}

}
