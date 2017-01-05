package stormstock.applib.eigen;

import java.util.List;

import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockUtils;
import stormstock.fw.tran.eigen.IEigenStockDayList;

/*
 * 垂直位置比例
 * 用于计算近N日的垂直价格位置比例
 * 例如：历史 最高100 最低0 当前25，则特征值为0.25
 */
public class EigenPriceLoc  extends IEigenStockDayList {

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
