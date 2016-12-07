package stormstock.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.analysis.ANLImgShow.CurvePoint;
import stormstock.analysis.ANLStockDayKData.DetailData;

public class ANLBTEngine {
	public ANLBTEngine()
	{
		stockListstore = new ArrayList<ANLStock>();
		m_eigenObjMap = new HashMap<String, ANLEigen>();
		m_strategyObj = null;
		m_cANLStockPool = new ANLStockPool();
		cUserAcc = new ANLUserAcc(m_cANLStockPool);
	}
	public void addEigen(ANLEigen cEigen)
	{
		m_eigenObjMap.put(cEigen.getClass().getSimpleName(), cEigen); 
	}
	public void setStrategy(ANLStrategy cStrategy)
	{
		m_strategyObj = cStrategy;
	}
	public void runBT(String beginDate, String endDate)
	{
		if(null == m_strategyObj) {
			ANLLog.outputConsole("m_strategyObj is null\n");
			return;
		}
		
		// ------------------------------------------------------------------------------
		// 账户对象初始化
		cUserAcc.init(100000.0f);
		
		// ------------------------------------------------------------------------------
		// 遍历所有股票，让用户筛选到股票池
		// ANLLog.outputConsole("loading user stock pool ...\n");
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLStock cANLStock = ANLDataProvider.getANLStock(stockId);
			if(null!= cANLStock && m_strategyObj.strategy_preload(cANLStock))
			{
				stockListstore.add(cANLStock);
				// ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
			}
		}
		ANLLog.outputConsole("===> load success, stockCnt(%d)\n", stockListstore.size());
		
		// ------------------------------------------------------------------------------
		// 从上证指数中确认回调天数
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
            // ANLLog.outputConsole("%s data generate\n", cANLDayKData.date);
			// 从存储股票列表中提取相应天数的数据到用户股票池中，回调给用户测试
			for(int iS=0;iS<stockListstore.size();iS++)
			{
				ANLStock cANLStockStore = stockListstore.get(iS);
				// fmt.format("   Stock %s generate\n", cANLStockStore.id);
				// 获取用户池中的相应股票对象
				ANLStock cANLStockUser = null;
				for(int iUS=0;iUS<m_cANLStockPool.stockList.size();iUS++)
				{
					ANLStock cANLStockUserFind = m_cANLStockPool.stockList.get(iUS);
					if(cANLStockUserFind.id.compareTo(cANLStockStore.id) == 0)
					{
						cANLStockUser = cANLStockUserFind;
					}
				}
				if(null == cANLStockUser)
				{
					cANLStockUser = new  ANLStock(cANLStockStore.id, cANLStockStore.curBaseInfo);
					m_cANLStockPool.stockList.add(cANLStockUser);
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
				for(Map.Entry<String, ANLEigen> entry:m_eigenObjMap.entrySet()){     
					String eigenKey = entry.getKey();
					float eigenVal = entry.getValue().calc(cANLStockUser);
					//ANLLog.outputConsole("ANLEigen %s %.3f\n", entry.getKey(), entry.getValue().calc(cANLStockUser));
					cANLStockUser.eigenMap.put(eigenKey, eigenVal);
				}   
			}
			
			// 更新账户日期
			cUserAcc.update(cANLDayKData.date);
			
			// 当前股票池回调给用户
			List<String> cSelectStockList = new ArrayList<String>();
			ANLLog.outputLog("---> strategy_enter date(%s) stockCnt(%d)\n", cANLDayKData.date, m_cANLStockPool.stockList.size());
			m_strategyObj.strategy_select(cANLDayKData.date, m_cANLStockPool, cSelectStockList);
			ANLLog.outputLog("    strategy_enter date(%s) result [ ", cANLDayKData.date);
			for(int j=0; j< cSelectStockList.size(); j++)// 遍历可操作票
			{
				String stockId = cSelectStockList.get(j);
				ANLLog.outputLog("%s ", stockId);
			}
			ANLLog.outputLog("]\n");
			
			// 账户操作交易
			int iMaxHoldCnt = 3; // 最大持股个数
			for(int iHold = 0; iHold < cUserAcc.stockList.size(); iHold++) // 遍历持仓票，进行卖出判断
			{
				ANLUserAcc.ANLUserAccStock cANLUserAccStock = cUserAcc.stockList.get(iHold);
				float cprice = m_cANLStockPool.getStock(cANLUserAccStock.id).GetLastPrice();
				if(cANLUserAccStock.holdDayCnt > 10) // 持有一定时间卖出
				{
					cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
				}
				float shouyi = (cprice - cANLUserAccStock.buyPrices)/cANLUserAccStock.buyPrices;
				if(shouyi > 0.02 || shouyi < -0.02) // 止盈止损卖出
				{
					cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
				}
			}
			int iNeedBuyCnt = iMaxHoldCnt - cUserAcc.stockList.size();
			for(int iBuy = 0; iBuy< iNeedBuyCnt; iBuy++) // 手中持票数量不足时进行买入
			{
				float usedMoney = cUserAcc.money/(iNeedBuyCnt-iBuy);//拿出相应仓位钱
				for(int j=0; j< cSelectStockList.size(); j++)// 遍历可操作票
				{
					String stockId = cSelectStockList.get(j);
					if(m_cANLStockPool.getStock(stockId).GetLastDate().compareTo(cANLDayKData.date)!=0) //股票最后日期与当前最后日期不同 继续下一个
					{
						continue;
					}
					boolean alreayHas = false;
					for(int k = 0; k < cUserAcc.stockList.size(); k++) // 遍历持仓票，判断是否已经持有
					{
						ANLUserAcc.ANLUserAccStock cANLUserAccStock = cUserAcc.stockList.get(k);
						if(stockId.compareTo(cANLUserAccStock.id) == 0)
						{
							alreayHas = true;
							break;
						}
					}
					if(!alreayHas)
					{
						String buy_id = stockId;
						float buy_price = m_cANLStockPool.getStock(buy_id).GetLastPrice();
						int buy_amount = (int)(usedMoney/buy_price)/100*100;
						cUserAcc.buyStock(buy_id, buy_price, buy_amount);
						break;
					}
				}
			}
        } 
	}
	
	private List<ANLStock> stockListstore;
	private Map<String, ANLEigen> m_eigenObjMap;
	private ANLStrategy m_strategyObj;
	private ANLStockPool m_cANLStockPool;
	public ANLUserAcc cUserAcc;
	
}
