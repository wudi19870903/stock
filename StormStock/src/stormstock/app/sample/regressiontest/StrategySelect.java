package stormstock.app.sample.regressiontest;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.tran.strategy.IStrategySelect;
import stormstock.fw.tran.strategy.StockContext;

public class StrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(StockContext ctx, SelectResult out_sr) {
		Stock curStock = ctx.getCurStock();
		// 特征：价值位置250日周期
		float EigenPriceLocLong = (float)curStock.getEngen("EigenSamplePriceLoc", 250);
		// 离60日均线偏离百分比
		float MADeviation60 = (float)curStock.getEngen("EigenSampleMADeviation", 60);
		// 离250日均线偏离百分比
		float MADeviation250 = (float)curStock.getEngen("EigenSampleMADeviation", 250);
		if(MADeviation60 < -0.1 && MADeviation250 < -0.06 
				&& EigenPriceLocLong < 0.4 && EigenPriceLocLong > 0.1) {
			out_sr.bSelect = true;
			//out_sr.fPriority = (float) Math.random();
			//ANLLog.outputLog("    stock %s %s %s %.2f EigenSample1(%.3f) EigenSample2(%.3f)\n", in_stock.id, in_stock.curBaseInfo.name, in_stock.GetLastDate(), in_stock.GetLastPrice(),EigenSample1,EigenSample2);
		}
	}

}
