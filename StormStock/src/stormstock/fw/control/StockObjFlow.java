package stormstock.fw.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.fw.base.BLog;

public class StockObjFlow {
	/*
	 * StockIDSet：  股票交易集合（样本集）
	 */
	public static void setTranStockIDSet(List<String> stockSet)
	{
		s_stockIDSet = stockSet;
	}
	public static List<String> getTranStockIDSet()
	{
		return s_stockIDSet;
	}
	private static List<String> s_stockIDSet = new ArrayList<String>();
}
