package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLDataProvider;
import stormstock.data.DataEngine;
import stormstock.data.DataEngineBase;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;
import stormstock.data.DataWebStockRealTimeInfo;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class RunUpdateData {
	public static Formatter fmt = new Formatter(System.out);
	public static void main(String[] args) {
		
		DataEngine.updateAllLocalStocks();
		
	}
}
