package stormstock.app.sample.regressiontest;
import java.util.List;

import stormstock.fw.tranbase.com.IStrategySelect;
import stormstock.fw.tranbase.com.StockContext;
import stormstock.fw.tranbase.stockdata.Stock;

public class StrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(StockContext ctx, SelectResult out_sr) {
		Stock curStock = ctx.stock();
		// 特征：价值位置250日周期
		float EigenPriceLocLong = (float)ctx.getStockEigen("EigenSamplePriceLoc", 250);
		// 离60日均线偏离百分比
		float MADeviation60 = (float)ctx.getStockEigen("EigenSampleMADeviation", 60);
		// 离250日均线偏离百分比
		float MADeviation250 = (float)ctx.getStockEigen("EigenSampleMADeviation", 250);
		if(MADeviation60 < -0.1 && MADeviation250 < -0.06 
				&& EigenPriceLocLong < 0.4 && EigenPriceLocLong > 0.1) {
			out_sr.bSelect = true;
			//out_sr.fPriority = (float) Math.random();
			//ANLLog.outputLog("    stock %s %s %s %.2f EigenSample1(%.3f) EigenSample2(%.3f)\n", in_stock.id, in_stock.curBaseInfo.name, in_stock.GetLastDate(), in_stock.GetLastPrice(),EigenSample1,EigenSample2);
		}
	}

	@Override
	public int strategy_select_max_count() {
		// TODO Auto-generated method stub
		return 5;
	}

}
