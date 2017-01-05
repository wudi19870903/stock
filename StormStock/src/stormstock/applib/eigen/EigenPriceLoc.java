package stormstock.applib.eigen;

import java.util.List;

import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockUtils;
import stormstock.fw.tran.eigen.IEigenStockDayList;

/*
 * ��ֱλ�ñ���
 * ���ڼ����N�յĴ�ֱ�۸�λ�ñ���
 * ���磺��ʷ ���100 ���0 ��ǰ25��������ֵΪ0.25
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
