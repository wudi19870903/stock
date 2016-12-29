package stormstock.analysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.analysis.ANLUtils;
import stormstock.analysis.ANLImgShow.CurvePoint;
import stormstock.analysis.ANLStockDayKData.DetailData;
import stormstock.analysis.ANLStrategy.SelectResult;
import stormstock.ori.stockdata.DataEngine;

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
		m_cSelectStockList = new ArrayList<String>();
		m_cSellStockList = new ArrayList<String>();
		
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
		ANLLog.outputLog("==> # loading test stock list ... \n");
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
		ANLLog.outputLog("    # load success, stockCnt(%d) \n", m_stockListstore.size());
		
		// ------------------------------------------------------------------------------
		// 从上证指数中确认回调天数
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cShangZhengKData = cANLStock.historyData.get(i);  
			String curDateStr = cShangZhengKData.date;
			m_poiList_shangzheng.add(new CurvePoint(i,cShangZhengKData.close));
			
			// 做成当天股票池
			generateStockPoolToday(curDateStr);
            
			// 更新账户到当前日期
			m_cUserAcc.update(curDateStr);
			ANLLog.outputLog("--> # date(%s) stockCnt(%d)\n", curDateStr, m_cANLStockPool.stockList.size());
			
			// 开盘操作：模拟账户交易（根据昨天晚上算出的买入卖出表）
			mockTransaction(curDateStr, m_cSelectStockList, m_cSellStockList);
			
			// 收盘操作：晚上执行策略回调，获得选入股票列表与卖出列表（再下一次循环中做交易）
			callStockPoolUserSelect(curDateStr, m_cSelectStockList); // 回调策略获得选入列表
			callStockPoolAccSell(curDateStr, m_cSellStockList); // 回调账户获得卖出列表
			callSelectSellFix(m_cSelectStockList, m_cSellStockList); // 买表中剔除卖出的重合部分
			
			m_cUserAcc.printInfo();
			m_poiList_money.add(new CurvePoint(i,m_cUserAcc.GetTotalAssets()));
        }
		
		m_cImageShow.addLogicCurveSameRatio(m_poiList_shangzheng, 1);
		m_cImageShow.addLogicCurveSameRatio(m_poiList_money, 2);
		m_cImageShow.GenerateImage();
		ANLLog.outputLog("==> # run back test over!");
	}
	
	public void runBTRealtimeMock(String beginDate, String endDate)
	{
		if(null == m_strategyObj) {
			ANLLog.outputConsole("m_strategyObj is null\n");
			return;
		}
		
		// ------------------------------------------------------------------------------
		// 获得实时回到区间，调用realtimeEngine接口
		Date cDateBegin = ANLUtils.GetDate(beginDate);
		Date cDateEnd = ANLUtils.GetDate(endDate);
		Date curDateTime = cDateBegin;
		if(curDateTime.getMinutes()!=0)
		{
			curDateTime.setTime(curDateTime.getTime() + 60*1000*1);
			curDateTime.setSeconds(0);
		}
		if(curDateTime.compareTo(cDateEnd) <= 0)
		{
			do
			{
				// 回调给实时引擎,每整数分钟调用执行引擎
				realtimeEngine(curDateTime, false);
				// 当前时间加1分钟
				curDateTime.setTime(curDateTime.getTime() + 60*1000);
			} while(curDateTime.compareTo(cDateEnd) <= 0);
		}
		ANLLog.outputLog("==> # run back test over! \n");
	}
	
	public void runRealtimeLoop()
	{
		ANLLog.outputConsole("==> # run realtime loop ... \n");
		if(null == m_strategyObj) {
			ANLLog.outputConsole("m_strategyObj is null\n");
			return;
		}
		do
		{
			// 获取当前时间
			Date cDate = new Date(); 
			if(cDate.getSeconds() == 0)
			{
				// 回调给实时引擎,每整数分钟回调
				realtimeEngine(cDate, true);
				
				try
				{
					Thread.sleep(1000);  //回调后+1秒，避免1秒内连续调用
				}
				catch (Exception e)  
				{  
					ANLLog.outputConsole(e.getMessage());  
				}  
			}
			
			// 等待0.5秒
			try
			{
				Thread.sleep(500); 
			}
			catch (Exception e)  
			{  
				ANLLog.outputConsole(e.getMessage());  
			}  
			
		} while(true);
	}
	
	private void realtimeEngine(Date datetime, boolean realFlag)
	{
		/*
		 * 每天9点26分，执行一次
		 * 1 判断当时是否是交易日，如果不是今天什么都不做
		 */
		String curDateStr = ANLUtils.GetDateStr(datetime);
		String curTimeStr = ANLUtils.GetTimeStr(datetime);
		if(adapter_IsTrasectionDay(curDateStr, realFlag))
		{
			/*
			 * 每天9点27分，执行一次
			 * 1 加载昨天选择结果（股票对象与买入策略）
			 * 2 加载已经买入的结果（股票对象，买入策略，卖出策略）
			 */
			if(curTimeStr.compareTo("09:27:00")==0)
			{
				ANLLog.outputLog("--> # date(%s) loading all, stockCnt(%d)\n", curDateStr, m_cANLStockPool.stockList.size());
			}
			
			/*
			 * 每天 9点30分-11点30分  13点00分-15点00分，每分钟执行一次
			 * 1 对昨天选择结果的股票回调买入策略
			 * 2对已经买入的股票回调卖出策略
			 */
			if(curTimeStr.compareTo("09:30:00")==0)
			{
				ANLLog.outputConsole("    # [%s] begin trasection...\n", curTimeStr);
			}
			
			/*
			 * 每天 18点00分分，执行一次
			 * 1 统计信息收集，当日报表更新至文件
			 */
			if(curTimeStr.compareTo("18:00:00")==0)
			{
				ANLLog.outputConsole("    # [%s] update report\n", curTimeStr);
			}
			
			
			/*
			 * 每天22点00分，执行一次数据更新
			 * 1 清除所有内存缓冲数据
			 * 2 更新全部股票数据
			 * 3 执行选股策略获得选股列表，选择结果（股票对象与买入策略）保存到文件
			 */
			if(curTimeStr.compareTo("22:00:00")==0)
			{
				ANLLog.outputConsole("    # [%s] clearall and run select strategy\n", curTimeStr);
				
				// 1 清除所有内存缓冲数据
				m_stockListstore.clear();
				m_cANLStockPool.clear();
				m_cSelectStockList.clear();
				
				// 2 更新全部股票数据
				DataEngine.updateAllLocalStocks(curDateStr);
				
				// 3 执行选股策略获得选股列表，选择结果（股票对象与买入策略）保存到文件
				// preload 获得preload股票表，做成股票池
				ANLLog.outputLog("    # loading test stock list ... \n");
				List<String> cStockList = ANLDataProvider.getAllStocks();
				for(int i=0; i<cStockList.size();i++)
				{
					String stockId = cStockList.get(i);
					ANLStock cANLStock = ANLDataProvider.getANLStock(stockId, curDateStr);
					if(null!= cANLStock && m_strategyObj.strategy_preload(cANLStock))
					{
						m_stockListstore.add(cANLStock);
						//ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
					}
				}
				generateStockPoolToday(curDateStr);
				ANLLog.outputLog("    # load success, stockCnt(%d) \n", m_stockListstore.size());
				// select 回调策略获得选入列表
				callStockPoolUserSelect(curDateStr, m_cSelectStockList);
			}
		}
	}
	
	private boolean adapter_IsTrasectionDay(String curDateStr,  boolean realFlag)
	{
		return true;
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
			// 为股票设置特征表
			cANLStockUser.addEigenMap(m_eigenObjMap); 
		}
	}
	
	private void callStockPoolUserSelect(String date, List<String> out_selectList)
	{
		out_selectList.clear();
		
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
		
		ANLLog.outputLog("    # select(%d) [ ", out_selectList.size());
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
	
	private void callStockPoolAccSell(String date, List<String> out_sellList)
	{
		out_sellList.clear();
		
		for(int iHold = 0; iHold < m_cUserAcc.stockList.size(); iHold++) // 遍历持仓票，进行卖出判断
		{
			ANLUserAcc.ANLUserAccStock cANLUserAccStock = m_cUserAcc.stockList.get(iHold);
			ANLStock cANLStock = m_cANLStockPool.getStock(cANLUserAccStock.id);
			float cprice = cANLStock.GetLastClosePrice();
			if(cANLUserAccStock.holdDayCnt > 10) // 持有一定时间卖出
			{
				out_sellList.add(cANLUserAccStock.id);
				continue;
			}
			float shouyi = (cprice - cANLUserAccStock.buyPrices)/cANLUserAccStock.buyPrices;
			if(shouyi > 0.02 || shouyi < -0.02) // 止盈止损卖出
			{
				out_sellList.add(cANLUserAccStock.id);
				continue;
			}
		}
		ANLLog.outputLog("    # sell(%d) [ ", out_sellList.size());
		if(out_sellList.size() == 0) ANLLog.outputLog("null ");
		for(int j=0; j< out_sellList.size(); j++)// 遍历可操作票
		{
			String stockId = out_sellList.get(j);
			ANLLog.outputLog("%s ", stockId);
			if (j >= 7 && out_sellList.size()-1 > 8) {
				ANLLog.outputLog("... ", stockId);
				break;
			}
		}
		ANLLog.outputLog("]\n");
	}
	
	private void callSelectSellFix(List<String> out_selectList, List<String> out_sellList)
	{
		List<String> cSelectSaveList = new ArrayList<String>(); // 找到需要保留哪些选中的
		
		for(int iSelect = 0; iSelect < out_selectList.size(); iSelect++)
		{
			String sid_select = out_selectList.get(iSelect);
			
			boolean bFindInSell = false;
			for(int iSell = 0; iSell < out_sellList.size(); iSell++)
			{
				String sid_sell = out_sellList.get(iSell);
				if(sid_sell == sid_select)
				{
					bFindInSell = true;
					break;
				}
			}
			
			if(!bFindInSell)
			{
				cSelectSaveList.add(sid_select);
			}
		}
		
		out_selectList.clear();
		out_selectList.addAll(cSelectSaveList);
	}
	
	private void mockTransaction(String date, List<String> cSelectStockList, List<String> cSellStockList)
	{
		// 账户操作交易
		int iMaxHoldCnt = 3; // 最大持股个数
		
		// 卖出操作
		for(int i = 0; i < cSellStockList.size(); i++)
		{
			String sid = cSellStockList.get(i);
			float fLastOpenRatio = m_cANLStockPool.getStock(sid).GetLastOpenRatio(); // 开盘涨跌幅
			if(fLastOpenRatio > -0.095) // 只有没跌停时候才能卖出
			{
				float cprice = m_cANLStockPool.getStock(sid).GetLastOpenPrice(); // 开盘价卖出
				int amount = m_cUserAcc.GetStockAmount(sid);
				m_cUserAcc.sellStock(sid, cprice, amount);
			}
		}
		
		// 买入操作
		int iNeedBuyCnt = iMaxHoldCnt - m_cUserAcc.stockList.size();
		if(iNeedBuyCnt > iMaxHoldCnt) iNeedBuyCnt = iMaxHoldCnt; //意外保护
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
					float fLastOpenRatio = m_cANLStockPool.getStock(buy_id).GetLastOpenRatio(); // 开盘涨跌幅
					if(fLastOpenRatio < 0.095) // 只有没涨停时候才能买入
					{
						float buy_price = m_cANLStockPool.getStock(buy_id).GetLastOpenPrice();//开盘价买入
						int buy_amount = (int)(usedMoney/buy_price)/100*100;
						m_cUserAcc.buyStock(buy_id, buy_price, buy_amount);
						break;
					}
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
	// 用户昨日选的股票
	List<String> m_cSelectStockList;
	// 用户昨日判断需要卖出的股票
	List<String> m_cSellStockList;
	// 操作账户对象
	public ANLUserAcc m_cUserAcc;
	// 用户添加的特征表
	private Map<String, ANLEigen> m_eigenObjMap;
	// 用户设置的策略对象
	private ANLStrategy m_strategyObj;
}
