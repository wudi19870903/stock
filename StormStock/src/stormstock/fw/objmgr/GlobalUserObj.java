package stormstock.fw.objmgr;

import stormstock.fw.tran.strategy.IStrategyClear;
import stormstock.fw.tran.strategy.IStrategyCreate;
import stormstock.fw.tran.strategy.IStrategySelect;

import java.util.Map;

import stormstock.fw.tran.ITranStockSetFilter;
import stormstock.fw.tran.eigen.IEigenStock;

public class GlobalUserObj {
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
	 * CurrentStrategyCreate
	 */
	public static IStrategyClear getCurrenStrategyClear()
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
