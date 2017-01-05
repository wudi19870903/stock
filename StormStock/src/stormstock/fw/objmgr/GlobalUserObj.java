package stormstock.fw.objmgr;

import stormstock.fw.tran.strategy.IStrategyClear;
import stormstock.fw.tran.strategy.IStrategyCreate;
import stormstock.fw.tran.strategy.IStrategySelect;
import stormstock.fw.tran.ITranStockSetFilter;

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
}
