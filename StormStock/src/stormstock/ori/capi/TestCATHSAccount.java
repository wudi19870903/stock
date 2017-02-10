package stormstock.ori.capi;

import java.util.Formatter;

import stormstock.ori.capi.CATHSAccount.ResultAllStockMarketValue;
import stormstock.ori.capi.CATHSAccount.ResultAvailableMoney;
import stormstock.ori.capi.CATHSAccount.ResultTotalAssets;

public class TestCATHSAccount {

	// for test: please be carefull
	public static void main(String[] args) {
        // TODO Auto-generated method stub
        //System.getProperty("java.library.path");
		Formatter fmt = new Formatter(System.out);
		fmt.format("### main begin\n");
		
        int iInitRet = CATHSAccount.initialize();
        fmt.format("CATHSAccount.initialize = %d\n", iInitRet);

        ResultAvailableMoney cResultAvailableMoney = CATHSAccount.getAvailableMoney();
        fmt.format("CATHSAccount.getAvailableMoney err(%d) AvailableMoney(%.2f)\n", cResultAvailableMoney.error, cResultAvailableMoney.availableMoney);
        
        ResultTotalAssets cResultTotalAssets = CATHSAccount.getTotalAssets();
        fmt.format("CATHSAccount.getTotalAssets err(%d) AvailableMoney(%.2f)\n", cResultTotalAssets.error, cResultTotalAssets.totalAssets);
        
        ResultAllStockMarketValue cResultAllStockMarketValue = CATHSAccount.getAllStockMarketValue();
        fmt.format("CATHSAccount.getAllStockMarketValue err(%d) AvailableMoney(%.2f)\n", cResultAllStockMarketValue.error, cResultAllStockMarketValue.allStockMarketValue);
        
        int iBuyRet = CATHSAccount.buyStock("601988", 100, 0.7f);
        fmt.format("CATHSAccount.buyStock = %d\n", iBuyRet);
        
        int iSellRet = CATHSAccount.sellStock("601988", 100, 10.7f);
        fmt.format("CATHSAccount.sellStock = %d\n", iSellRet);
        
        fmt.format("### main end\n");
    }
}
