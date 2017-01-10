package stormstock.app.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import stormstock.fw.tranengine_lite.ANLStock;
import stormstock.fw.tranengine_lite.ANLDataProvider;
import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngineBase;
import stormstock.ori.stockdata.DataWebStockAllList;
import stormstock.ori.stockdata.DataWebStockAllList.StockItem;
import stormstock.ori.stockdata.DataWebStockDayK;
import stormstock.ori.stockdata.DataWebStockDayK.DayKData;
import stormstock.ori.stockdata.DataWebStockDividendPayout.DividendPayout;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.RealTimeInfo;

public class RunUpdateData {
	public static Formatter fmt = new Formatter(System.out);
	public static void main(String[] args) {
		
		DataEngine.updateAllLocalStocks("2016-12-18");
		
	}
}
