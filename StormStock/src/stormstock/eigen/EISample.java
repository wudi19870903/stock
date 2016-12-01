package stormstock.eigen;

import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLStock;

public class EISample extends ANLEigen {
	// 计算离60日均线的价格偏离百分比
	public float calc(ANLStock cANLStock)
	{
		float MA60 = cANLStock.GetMA(60, cANLStock.GetLastDate());
		float curPrice = cANLStock.GetLastPrice();
		float val = (curPrice-MA60)/MA60;
		return val;
	}
}
