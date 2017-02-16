package stormstock.app.sample.regressiontestlite;

import stormstock.fw.tranengine_lite.ANLEigen;
import stormstock.fw.tranengine_lite.ANLStock;

/*
 * 垂直位置比例
 * 用于计算近N日的垂直价格位置比例
 * 例如：历史 最高100 最低0 当前25，则特征值为0.25
 */
public class EigenPriceLoc extends ANLEigen {
	@Override
	public Object calc(ANLStock cANLStock, Object... args) {
		int cntDay = (int)args[0];
		if(cANLStock.historyData.size() == 0) 
			return 0.0f;
		String date = cANLStock.GetLastDate();
		float lowPrice = cANLStock.GetLow(cntDay, date);
		float highPrice = cANLStock.GetHigh(cntDay, date);
		float curPrice = cANLStock.GetLastClosePrice();
		return (curPrice-lowPrice)/(highPrice-lowPrice);
	}
}
