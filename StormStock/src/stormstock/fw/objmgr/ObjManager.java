package stormstock.fw.objmgr;

import stormstock.fw.tran.IStrategyClear;
import stormstock.fw.tran.IStrategyCreate;
import stormstock.fw.tran.IStrategySelect;
import stormstock.fw.tran.ITranStockSetFilter;

public class ObjManager {
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
	
	private static ITranStockSetFilter s_tranStockSetFilter = null;
	private static IStrategySelect s_strategySelect = null;
	private static IStrategyCreate s_strategyCreate = null;
	private static IStrategyClear s_strategyClear = null;
}
