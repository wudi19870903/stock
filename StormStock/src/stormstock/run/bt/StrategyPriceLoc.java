package stormstock.run.bt;

import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLStock;

/*
 * 价格的垂直位置比例
 */
public class StrategyPriceLoc {
	
	/*
	 * 超长期垂直位置比例，4年周期
	 */
	public static class StrategyPriceLocLongLong extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(250*4, date);
			float highPrice = cANLStock.GetHigh(250*4, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	
	/*
	 * 长期垂直位置比例，1年周期
	 */
	public static class StrategyPriceLocLong extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(250, date);
			float highPrice = cANLStock.GetHigh(250, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	
	/*
	 * 中期垂直位置比例，60天周期
	 */
	public static class StrategyPriceLocMid extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(60, date);
			float highPrice = cANLStock.GetHigh(60, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	
	/*
	 * 短期垂直位置比例，20天周期
	 */
	public static class StrategyPriceLocShort extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(20, date);
			float highPrice = cANLStock.GetHigh(20, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
}
