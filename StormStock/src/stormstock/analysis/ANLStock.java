package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLStock {
	public ANLStock()
	{
		historyData = new ArrayList<ANLStockDayKData>();
	}	 
	public String id;
	public List<ANLStockDayKData> historyData;
}
