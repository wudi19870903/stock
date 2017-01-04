package stormstock.app.progtran;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.tran.IStrategySelect;
import stormstock.fw.tran.StockContext;

public class StrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(StockContext ctx, SelectResult out_sr) {
		// 特征：价值位置250日周期
		float EigenPriceLocLong = 0;
		// 离60日均线偏离百分比
		float MADeviation60 = 0;
		// 离250日均线偏离百分比
		float MADeviation250 = 0;
		if(MADeviation60 < -0.1 && MADeviation250 < -0.06 
				&& EigenPriceLocLong < 0.4 && EigenPriceLocLong > 0.1) {
			out_sr.bSelect = true;
			//out_sr.fPriority = (float) Math.random();
			//ANLLog.outputLog("    stock %s %s %s %.2f EigenSample1(%.3f) EigenSample2(%.3f)\n", in_stock.id, in_stock.curBaseInfo.name, in_stock.GetLastDate(), in_stock.GetLastPrice(),EigenSample1,EigenSample2);
		}
	}

}
