package stormstock.fw.control;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;

public class StockObjFlow {
	/*
	 * StockIDSet��  ��Ʊ���׼��ϣ���������
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
	 * StockIDSelect��ѡ���б�
	 */
	public static void setStockIDSelect(List<String> stockSelect)
	{
		s_stockIDSelect = stockSelect;
	}
	public static List<String> getStockIDSelect()
	{
		List<String> newList = new ArrayList<String>();
		for(int i=0; i< s_stockIDSelect.size();i++)
		{
			String stockID = s_stockIDSelect.get(i);
			if(!isInStockIDCreate(stockID))  // ѡ���б��ų����Ѿ��������б��
			{
				newList.add(stockID);
			}
		}
		return newList;
	}
	private static List<String> s_stockIDSelect = new ArrayList<String>();
	
	/*
	 * StockIDCreate�������б�
	 */
	public static void addStockIDCreate(String stockID)
	{
		s_stockIDCreate.add(stockID);
	}
	public static List<String> getStockIDCreate()
	{
		return s_stockIDCreate;
	}
	public static boolean isInStockIDCreate(String stockID)
	{
		return s_stockIDCreate.contains(stockID);
	}
	private static List<String> s_stockIDCreate = new ArrayList<String>();
}
