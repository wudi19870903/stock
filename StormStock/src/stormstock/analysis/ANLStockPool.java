package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

public class ANLStockPool {
	public ANLStockPool()
	{
		stockList = new ArrayList<ANLStock>();
	}
	public ANLStock getStock(String id)
	{
		ANLStock cANLStock = null;
		for(int i=0;i<stockList.size();i++)
		{
			ANLStock tmp = stockList.get(i);
			if(tmp.id.compareTo(id) == 0)
			{
				cANLStock = tmp;
				break;
			}
		}
		return cANLStock;
	}
	
	public List<ANLStock> stockList;
}
