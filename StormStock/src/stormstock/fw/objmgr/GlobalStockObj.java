package stormstock.fw.objmgr;

import java.util.ArrayList;
import java.util.List;

public class GlobalStockObj {
	
	/*
	 * StockIDSet
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
	
	
	/*
	 * StockIDSelect
	 */
	public static void setStockIDSelect(List<String> stockSelect)
	{
		s_stockIDSelect = stockSelect;
	}
	public static List<String> getStockIDSelect()
	{
		return s_stockIDSelect;
	}
	private static List<String> s_stockIDSelect = new ArrayList<String>();
	
}
