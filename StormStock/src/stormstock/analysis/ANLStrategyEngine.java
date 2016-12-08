package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.analysis.ANLImgShow.CurvePoint;

abstract public class ANLStrategyEngine {
	
	// 所有股票加载那些股票到内存，返回true则参与回测系统
	abstract public boolean strategy_preload(ANLStock cANLStock);
	// 每天用户股票池回调，测试系统自动回调，确定买入卖出
	abstract public void strategy_today(String date, ANLStockPool spool);

	//------------------------------------------------------------------------------------
	
	public ANLStrategyEngine()
	{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		String logfilename = this.getClass().getSimpleName() + ".txt";
		ANLLog.init(logfilename);
		String imgfilename = this.getClass().getSimpleName() + ".jpg";
		cImageShow = new ANLImgShow(1600,900,imgfilename);
		// create inner object
		stockListstore = new ArrayList<ANLStock>();
		eigenObjMap = new HashMap<String, ANLEigen>();
		cANLStockPool = new ANLStockPool();
		cUserAcc = new ANLUserAcc(cANLStockPool);
	}
	protected void addEigen(ANLEigen cEigen)
	{
		eigenObjMap.put(cEigen.getClass().getSimpleName(), cEigen); 
	}
	protected void run()
	{
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		ANLStockDayKData cANLDayKDataBegin = cANLStock.historyData.get(0);  
		ANLStockDayKData cANLDayKDataEnd = cANLStock.historyData.get(cANLStock.historyData.size()-1);  
		run(cANLDayKDataBegin.date, cANLDayKDataEnd.date);
	}
	protected void run(String beginDate, String endDate) {
		// 账户对象初始化
		cUserAcc.init(100000.0f);
		// 遍历所有股票，让用户筛选到用户股票池
		// ANLLog.outputConsole("loading user stock pool ...\n");
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLStock cANLStock = ANLDataProvider.getANLStock(stockId);
			if(null!= cANLStock && strategy_preload(cANLStock))
			{
				stockListstore.add(cANLStock);
				// ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
			}
		}
		ANLLog.outputConsole("load success, stock count : %d\n", stockListstore.size());
		
		List<CurvePoint> PoiList_shangzhen = new ArrayList<CurvePoint>();
		List<CurvePoint> PoiList_money = new ArrayList<CurvePoint>();
		
		// 从上证指数中确认回调天数
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
			PoiList_shangzhen.add(new CurvePoint(i,cANLDayKData.close));
            // ANLLog.outputConsole("%s data generate\n", cANLDayKData.date);
			// 从存储股票列表中提取相应天数的数据到用户股票池中，回调给用户测试
			for(int iS=0;iS<stockListstore.size();iS++)
			{
				ANLStock cANLStockStore = stockListstore.get(iS);
				// fmt.format("   Stock %s generate\n", cANLStockStore.id);
				// 获取用户池中的相应股票对象
				ANLStock cANLStockUser = null;
				for(int iUS=0;iUS<cANLStockPool.stockList.size();iUS++)
				{
					ANLStock cANLStockUserFind = cANLStockPool.stockList.get(iUS);
					if(cANLStockUserFind.id.compareTo(cANLStockStore.id) == 0)
					{
						cANLStockUser = cANLStockUserFind;
					}
				}
				if(null == cANLStockUser)
				{
					cANLStockUser = new  ANLStock(cANLStockStore.id, cANLStockStore.curBaseInfo);
					cANLStockPool.stockList.add(cANLStockUser);
				}
				// 添加相应的天数数据，添加完毕后移除
				int iRemoveCnt = 0;
				for(int iStore = 0; iStore<cANLStockStore.historyData.size();iStore++)
				{
					ANLStockDayKData cANLStockStoreKData = cANLStockStore.historyData.get(iStore);
					// fmt.format("   check date %s\n", cANLStockStoreKData.date);
					if(cANLStockStoreKData.date.compareTo(cANLDayKData.date) <= 0)
					{
						ANLStockDayKData cpObj = new ANLStockDayKData(cANLStockStoreKData, cANLStockUser);
						cANLStockUser.historyData.add(cpObj);
						iRemoveCnt++;
					}
				}
				for(int iRmCnt = 0;iRmCnt<iRemoveCnt;iRmCnt++)
				{
					cANLStockStore.historyData.remove(0);
				}
				// 为股票计算特征值
				for(Map.Entry<String, ANLEigen> entry:eigenObjMap.entrySet()){     
					String eigenKey = entry.getKey();
					Object eigenVal = entry.getValue().calc(cANLStockUser);
					//ANLLog.outputConsole("ANLEigen %s %.3f\n", entry.getKey(), entry.getValue().calc(cANLStockUser));
					cANLStockUser.eigenMap.put(eigenKey, eigenVal);
				}   
			}
			
			// 回调给用户
			cUserAcc.update(cANLDayKData.date);
			strategy_today(cANLDayKData.date, cANLStockPool);
            
            PoiList_money.add(new CurvePoint(i,cUserAcc.GetTotalAssets()));
        } 
		
		cImageShow.writeLogicCurve(PoiList_shangzhen, 1);
		cImageShow.writeLogicCurve(PoiList_money, 2);
		cImageShow.GenerateImage();
	}
	
	private List<ANLStock> stockListstore;
	private Map<String, ANLEigen> eigenObjMap;
	public ANLUserAcc cUserAcc;
	private ANLStockPool cANLStockPool;
	private ANLImgShow cImageShow;
}
