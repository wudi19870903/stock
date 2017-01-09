package stormstock.app.sample.regressiontest;

import stormstock.fw.tranbase.com.IEigenStock;
import stormstock.fw.tranbase.stockdata.Stock;

public class StockEigen {
	/*
	 * 特征EigenPriceLoc类
	 * 垂直位置比例
	 * 用于计算近N日的垂直价格位置比例
	 * 例如：历史 最高100 最低0 当前25，则特征值为0.25
	 */
	public static class EigenSamplePriceLoc extends IEigenStock {
		@Override
		public Object calc(Stock cStock, Object... args) {
			int cntDay = (int)args[0];
			if(cStock.getCurStockDayData().size() == 0) 
				return 0.0f;
			String date = cStock.GetLastDate();
			float lowPrice = cStock.GetLow(cntDay, date);
			float highPrice = cStock.GetHigh(cntDay, date);
			float curPrice = cStock.GetLastClosePrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	/*
	 * 特征EigenSampleMADeviation类
	 * 用于计算当前价格偏离N日均线价格的百分比
	 */
	public static class EigenSampleMADeviation extends IEigenStock {
		@Override
		public Object calc(Stock cStock, Object... args) {
			int dayCnt = (int)args[0];
			float MAVal = cStock.GetMA(dayCnt, cStock.GetLastDate());
			float curPrice = cStock.GetLastClosePrice();
			float val = (curPrice-MAVal)/MAVal;
			return val;
		}

	}
}
