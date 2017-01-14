package stormstock.fw.tranbase.com;

import java.util.Map;

import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.stockdata.StockDataIF;

public class GlobalUserObj {
	
	/*
	 * CurrentStockDataIF
	 */
	public static StockDataIF getCurStockDataIF()
	{
		return s_stockDataIF;
	}
	public static void setCurrentStockDataIF(StockDataIF cStockDataIF)
	{
		s_stockDataIF = cStockDataIF;
	}
	private static StockDataIF s_stockDataIF = null;
	
	
	/*
	 * CurrentAccountControlIF
	 */
	public static AccountControlIF getCurAccountControlIF()
	{
		return s_accountControlIF;
	}
	public static void setCurrentAccountControlIF(AccountControlIF cAccountControlIF)
	{
		s_accountControlIF = cAccountControlIF;
	}
	private static AccountControlIF s_accountControlIF = null;
	
	/*
	 * CurrentTranStockSetFilter
	 */
	public static ITranStockSetFilter getCurrentTranStockSetFilter()
	{
		return s_tranStockSetFilter;
	}
	public static void setCurrentTranStockSetFilter(ITranStockSetFilter cFilter)
	{
		s_tranStockSetFilter = cFilter;
	}
	private static ITranStockSetFilter s_tranStockSetFilter = null;
	
	/*
	 * CurrentStrategySelect
	 */
	public static IStrategySelect getCurrentStrategySelect()
	{
		return s_strategySelect;
	}
	public static void setCurrentStrategySelect(IStrategySelect strategySelect)
	{
		s_strategySelect = strategySelect;
	}
	private static IStrategySelect s_strategySelect = null;

	/*
	 * CurrentStrategyCreate
	 */
	public static IStrategyCreate getCurrentStrategyCreate()
	{
		return s_strategyCreate;
	}
	public static void setCurrentStrategyCreate(IStrategyCreate strategyCreate)
	{
		s_strategyCreate = strategyCreate;
	}
	private static IStrategyCreate s_strategyCreate = null;
	
	/*
	 * CurrenStrategyClear
	 */
	public static IStrategyClear getCurrentStrategyClear()
	{
		return s_strategyClear;
	}
	public static void setCurrentStrategyClear(IStrategyClear strategyClear)
	{
		s_strategyClear = strategyClear;
	}
	private static IStrategyClear s_strategyClear = null;
	
	/*
	 * CurrentStockEigenMap
	 */
	public static IEigenStock getStockEigen(String name)
	{
		if(s_cStockEigenMap.containsKey(name))
		{
			return s_cStockEigenMap.get(name);
		}
		return null;
	}
	public static void setCurrentStockEigenMap(Map<String, IEigenStock> cStockEigenMap)
	{
		s_cStockEigenMap = cStockEigenMap;
	}
	private static Map<String, IEigenStock> s_cStockEigenMap;
}
