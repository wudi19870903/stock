package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataEngineBase.StockBaseInfo;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLStock {
	public ANLStock()
	{
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
	}	 
	public String id;
	public StockBaseInfo curBaseInfo;
	public List<ANLStockDayKData> historyData;
}
