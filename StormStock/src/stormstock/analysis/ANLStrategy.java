package stormstock.analysis;

import java.util.List;

abstract public class ANLStrategy {
	abstract public boolean strategy_preload(ANLStock cANLStock);
	abstract public void strategy_select(String date, ANLStockPool spool, List<String> selectStockList);
}
