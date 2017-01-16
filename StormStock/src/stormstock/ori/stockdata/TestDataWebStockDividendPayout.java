package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.CommonDef.*;
import stormstock.ori.stockdata.DataWebStockDividendPayout.ResultDividendPayout;

public class TestDataWebStockDividendPayout {

	public static void main(String[] args){
		ResultDividendPayout cResultDividendPayout = DataWebStockDividendPayout.getDividendPayout("600060");
		if(0 == cResultDividendPayout.error)
		{
			for(int i = 0; i < cResultDividendPayout.resultList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultDividendPayout.error);
		}
	}
}
