package stormstock.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.analysis.ANLImgShow.CurvePoint;
import stormstock.analysis.ANLStockDayKData.DetailData;
import stormstock.analysis.ANLStrategy.SelectResult;

/*
 * ANL Back Test Engine class
 */
public class ANLBTEngine {
	/*
	 * SelectResultWrapper类，用于选股优先级排序
	 */
	static private class SelectResultWrapper {
		public SelectResultWrapper(){
			selectRes = new SelectResult();
		}
		// 优先级从大到小排序
		static public class SelectResultCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				SelectResultWrapper c1 = (SelectResultWrapper)object1;
				SelectResultWrapper c2 = (SelectResultWrapper)object2;
				int iCmp = Float.compare(c1.selectRes.fPriority, c2.selectRes.fPriority);
				if(iCmp > 0) 
					return -1;
				else if(iCmp < 0) 
					return 1;
				else
					return 0;
			}
		}
		public String stockId;
		public SelectResult selectRes;
	}

	/*
	 * 构造
	 */
	public ANLBTEngine(String name)
	{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		m_logfilename = name + ".txt";
		ANLLog.init(m_logfilename);
		String imgfilename = name + ".jpg";
		m_cImageShow = new ANLImgShow(1600,900,imgfilename);
		m_poiList_shangzheng = new ArrayList<CurvePoint>();
		m_poiList_money = new ArrayList<CurvePoint>();
		
		m_stockListstore = new ArrayList<ANLStock>();
		m_cANLStockPool = new ANLStockPool();
		
		m_cUserAcc = new ANLUserAcc(m_cANLStockPool);
		
		m_eigenObjMap = new HashMap<String, ANLEigen>();
		m_strategyObj = null;
	}
	
	/*
	 * 为测试引擎添加特征对象，用于计算股票的特征值
	 */
	public void addEigen(ANLEigen cEigen)
	{
		m_eigenObjMap.put(cEigen.getClass().getSimpleName(), cEigen); 
	}
	
	/*
	 * 为测试引擎设置要测试的策略
	 */
	public void setStrategy(ANLStrategy cStrategy)
	{
		m_strategyObj = cStrategy;
	}
	
	/*
	 * 进行股票回测
	 * beginDate: 开始日期 例如 “2016-01-01”
	 * endDate: 结束日期 例如 “2016-12-31”
	 */
	public void runBT(String beginDate, String endDate)
	{
		if(null == m_strategyObj) {
			ANLLog.outputConsole("m_strategyObj is null\n");
			return;
		}
		
		// ------------------------------------------------------------------------------
		// 账户对象初始化
		m_cUserAcc.init(100000.0f);
		
		// ------------------------------------------------------------------------------
		// 遍历所有股票，预加载到所有股票列表
		ANLLog.outputConsole("loading user stock pool ...");
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLStock cANLStock = ANLDataProvider.getANLStock(stockId);
			if(null!= cANLStock && m_strategyObj.strategy_preload(cANLStock))
			{
				m_stockListstore.add(cANLStock);
				// ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
			}
		}
		ANLLog.outputConsole("==> load success, stockCnt(%d)\n", m_stockListstore.size());
		
		// ------------------------------------------------------------------------------
		// 从上证指数中确认回调天数
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cShangZhengKData = cANLStock.historyData.get(i);  
			String curDate = cShangZhengKData.date;
			m_poiList_shangzheng.add(new CurvePoint(i,cShangZhengKData.close));
			
			// 做成当天股票池
			generateStockPoolToday(curDate);
            
			// 更新账户当前日期
			m_cUserAcc.update(curDate);
			
			// 当前股票池中股票全部回调给用户，获得select股票列表
			List<String> cSelectStockList = new ArrayList<String>();
			callStockPoolUserSelect(curDate, cSelectStockList);
			
			// 模拟账户交易
			mockTransaction(curDate, cSelectStockList);
			m_poiList_money.add(new CurvePoint(i,m_cUserAcc.GetTotalAssets()));
        }
		
		m_cImageShow.writeLogicCurve(m_poiList_shangzheng, 1);
		m_cImageShow.writeLogicCurve(m_poiList_money, 2);
		m_cImageShow.GenerateImage();
		ANLLog.outputLog("--> run back test over!");
	}
	
	private void generateStockPoolToday(String date)
	{
		// ANLLog.outputConsole("%s data generate\n", cANLDayKData.date);
		// 从存储股票列表中提取相应天数的数据到用户股票池中，回调给用户测试
		for(int iS=0;iS<m_stockListstore.size();iS++)
		{
			ANLStock cANLStockStore = m_stockListstore.get(iS);
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
				if(cANLStockStoreKData.date.compareTo(date) <= 0)
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
				Object eigenVal = entry.getValue().calc(cANLStockUser);
				//ANLLog.outputConsole("ANLEigen %s %.3f\n", entry.getKey(), entry.getValue().calc(cANLStockUser));
				cANLStockUser.eigenMap.put(eigenKey, eigenVal);
			}   
		}
	}
	
	private void callStockPoolUserSelect(String date, List<String> out_selectList)
	{
		ANLLog.outputLog("--> strategy_enter date(%s) stockCnt(%d)\n", date, m_cANLStockPool.stockList.size());
		
		// 回调给用户生成cSelectResultWrapperList后进行排序
		List<SelectResultWrapper> cSelectResultWrapperList = new ArrayList<SelectResultWrapper>();
		for(int iStockIndex = 0; iStockIndex < m_cANLStockPool.stockList.size(); iStockIndex++)
		{
			ANLStock cCurStock = m_cANLStockPool.stockList.get(iStockIndex);
			
			SelectResultWrapper cSRW = new SelectResultWrapper();
			cSRW.stockId = cCurStock.id;
			m_strategyObj.strategy_select(date, cCurStock, cSRW.selectRes);
			if(cSRW.selectRes.bSelect){
				cSelectResultWrapperList.add(cSRW);
			}
		} 
		Collections.sort(cSelectResultWrapperList, new SelectResultWrapper.SelectResultCompare());
		
		// 排序后的SelectResultWrapperList添加到selectlist中
		for(int iSRW = 0; iSRW < cSelectResultWrapperList.size(); iSRW++)
		{
			String stockId = cSelectResultWrapperList.get(iSRW).stockId;
			out_selectList.add(stockId);
		}
		
		ANLLog.outputLog("    # strategy_enter date(%s) select(%d) [ ", date, out_selectList.size());
		if(out_selectList.size() == 0) ANLLog.outputLog("null ");
		for(int j=0; j< out_selectList.size(); j++)// 遍历可操作票
		{
			String stockId = out_selectList.get(j);
			ANLLog.outputLog("%s ", stockId);
			if (j >= 7 && out_selectList.size()-1 > 8) {
				ANLLog.outputLog("... ", stockId);
				break;
			}
		}
		ANLLog.outputLog("]\n");
	}
	
	private void mockTransaction(String date, List<String> cSelectStockList)
	{
		// 账户操作交易
		int iMaxHoldCnt = 3; // 最大持股个数
		for(int iHold = 0; iHold < m_cUserAcc.stockList.size(); iHold++) // 遍历持仓票，进行卖出判断
		{
			ANLUserAcc.ANLUserAccStock cANLUserAccStock = m_cUserAcc.stockList.get(iHold);
			float cprice = m_cANLStockPool.getStock(cANLUserAccStock.id).GetLastPrice();
			if(cANLUserAccStock.holdDayCnt > 10) // 持有一定时间卖出
			{
				m_cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
			float shouyi = (cprice - cANLUserAccStock.buyPrices)/cANLUserAccStock.buyPrices;
			if(shouyi > 0.02 || shouyi < -0.02) // 止盈止损卖出
			{
				m_cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
		}
		int iNeedBuyCnt = iMaxHoldCnt - m_cUserAcc.stockList.size();
		for(int iBuy = 0; iBuy< iNeedBuyCnt; iBuy++) // 手中持票数量不足时进行买入
		{
			float usedMoney = m_cUserAcc.money/(iNeedBuyCnt-iBuy);//拿出相应仓位钱
			for(int j=0; j< cSelectStockList.size(); j++)// 遍历可操作票
			{
				String stockId = cSelectStockList.get(j);
				if(m_cANLStockPool.getStock(stockId).GetLastDate().compareTo(date)!=0) //股票最后日期与当前最后日期不同 继续下一个
				{
					continue;
				}
				boolean alreayHas = false;
				for(int k = 0; k < m_cUserAcc.stockList.size(); k++) // 遍历持仓票，判断是否已经持有
				{
					ANLUserAcc.ANLUserAccStock cANLUserAccStock = m_cUserAcc.stockList.get(k);
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
					m_cUserAcc.buyStock(buy_id, buy_price, buy_amount);
					break;
				}
			}
		}
	}
	
	// log文件名
	private String m_logfilename;
	// 交易显示图像
	private ANLImgShow m_cImageShow;
	List<CurvePoint> m_poiList_shangzheng; // 上证曲线点
	List<CurvePoint> m_poiList_money; // 账户资金曲线点
	// 股票总数据列表
	private List<ANLStock> m_stockListstore;
	// 每天回调给用户的股票池
	private ANLStockPool m_cANLStockPool;
	// 操作账户对象
	public ANLUserAcc m_cUserAcc;
	// 用户添加的特征表
	private Map<String, ANLEigen> m_eigenObjMap;
	// 用户设置的策略对象
	private ANLStrategy m_strategyObj;
}
