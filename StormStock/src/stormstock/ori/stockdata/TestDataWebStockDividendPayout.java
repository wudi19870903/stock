package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataWebStockDividendPayout.DividendPayout;

public class TestDataWebStockDividendPayout {

	public static void main(String[] args){
		List<DividendPayout> retList = new ArrayList<DividendPayout>();
		int ret = DataWebStockDividendPayout.getDividendPayout("600060", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = retList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
