package stormstock.analysis;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ANLPolicyXY extends ANLPolicyBase {
	public static Formatter fmt = new Formatter(System.out);

	public boolean enterCheck(ANLStock cANLStock, 
			int iCheckDay) 
	{	
		if(iCheckDay < 100 || iCheckDay > cANLStock.historyData.size()-1)
		{
			return false;
		}
		
		int iBeginCheck = iCheckDay - 10;
		int iEndCheck = iCheckDay - 0;
		
		//if(!cCheckDayKData.date.replace("-", "").contains("20130117")) return false;
		//fmt.format("master cur_date: %s close:%f\n", cCheckDayKData.date,cCheckDayKData.close);
		
		float leftMidPos = 0.0f;
		//fmt.format(" L1: %s \n", cANLStock.historyData.get(iCheckDay-20).date);
		for(int i=iBeginCheck; i<iBeginCheck+3;i++)
		{
			ANLStockDayKData tmp = cANLStock.historyData.get(i);
			leftMidPos = leftMidPos + (tmp.open + tmp.close)/2;
		}
		leftMidPos = leftMidPos/3;
		//fmt.format(" leftMidPos: %f \n", leftMidPos);
		
		float rightMidPos = 0.0f;
		//fmt.format(" R1: %s \n", cANLStock.historyData.get(iCheckDay-4).date);
		for(int i=iEndCheck-2; i<=iEndCheck;i++)
		{
			ANLStockDayKData tmp = cANLStock.historyData.get(i);
			rightMidPos = rightMidPos + (tmp.open + tmp.close)/2;
		}
		rightMidPos = rightMidPos/3;
		//fmt.format(" rightMidPos: %f \n", rightMidPos);
		
		float baseAdd = (rightMidPos - leftMidPos)/(iEndCheck-iBeginCheck-2);
		if(baseAdd < 0) return false;
		float base0 = leftMidPos - baseAdd*1;
		
		//fmt.format(" base0: %f \n", base0);
		
		int index = 0;
		for(int i = iBeginCheck; i<=iEndCheck;i++)
		{
			float CurBaseMid = base0 + baseAdd*index;
			ANLStockDayKData tmp = cANLStock.historyData.get(i);
			float midprice = (tmp.open + tmp.close)/2;
			float check = Math.abs((midprice-CurBaseMid)/CurBaseMid);
			
			//fmt.format(" cDate:%s CurBaseMid: %f check:%f \n", tmp.date, CurBaseMid, check);
			if(check > 0.05)
			{
				//fmt.format(" False cDate:%s\n", tmp.date);
				return false;
			}
			
//			if(i == iCheckDay)
//			{
//				float pl = (tmp.close - CurBaseMid)/CurBaseMid;
//				float p2 = (tmp.close - tmp.open)/tmp.open;
//				if(pl > 0 || p2 > -0.03) return false;
//				if(p2 < -0.07) return false;
//			}
			
			index++;
		}
		return true;
	}
	
	public RetExitCheck exitCheck(ANLStock cANLStock, int iEnter)
	{
		int maxWaitDay = 20;
		
		// 后面没有足够天数检查
		if(iEnter >= cANLStock.historyData.size() - maxWaitDay - 1)
		{
			return new RetExitCheck(false, -1, 0.0f);
		}
		
		float expectPer = 0.08f;
		int iCheckDay = iEnter+1;
		float currentPrice = cANLStock.historyData.get(iEnter).close;
		float profit = 0.0f;
		for(; iCheckDay < iEnter + maxWaitDay; iCheckDay++)
		{
			ANLStockDayKData checkDayKData = cANLStock.historyData.get(iCheckDay);
			float highDay = checkDayKData.high;
			float lowDay = checkDayKData.low;
			float closeDay = checkDayKData.close;
			float profitLow = (lowDay-currentPrice)/currentPrice;
			float profitHigh = (highDay-currentPrice)/currentPrice;
			float profitClose = (closeDay-currentPrice)/currentPrice;
			//fmt.format("CheckProfit date:%s profit:%f\n", checkDayKData.date, profit);
			if(profitLow < -expectPer)
			{
				profit = -expectPer;
				break;
			}   
			if(profitHigh > expectPer)
			{
				profit = expectPer;
				break;
			}
			if(iCheckDay == iEnter + maxWaitDay - 1)
			{
				profit = profitClose;
			}
		}
		return new RetExitCheck(true, iCheckDay, profit);
	}

	/////////////////////////////////////////////////////////////
	// FindIndexPriceTuPo
	
	private static boolean CheckBoFeng(ANLStock cANLStock, int iCheckDay, int checkdayCount)
	{
		// 查找区间最高价
		float maxprice = cANLStock.historyData.get(iCheckDay-checkdayCount/2).close;
		for(int iIndex = iCheckDay-checkdayCount/2; iIndex < iCheckDay+checkdayCount/2; iIndex++)  
		{
			if(cANLStock.historyData.get(iIndex).close > maxprice)
			{
				maxprice = cANLStock.historyData.get(iIndex).close;
			}
		}
		if(maxprice == cANLStock.historyData.get(iCheckDay).close)
		{
			// 判断满足山峰型
			float L2 = cANLStock.historyData.get(iCheckDay-checkdayCount/4).getMA(5);
			float L1 = cANLStock.historyData.get(iCheckDay-checkdayCount/6).getMA(5);
			float M = cANLStock.historyData.get(iCheckDay).getMA(5);
			float R1 = cANLStock.historyData.get(iCheckDay+checkdayCount/4).getMA(5);
			float R2 = cANLStock.historyData.get(iCheckDay+checkdayCount/2).getMA(5);
			if(cANLStock.historyData.get(iCheckDay).date.replace("-","").contains("20090915"))
			{
//				fmt.format("XXXXXXXXXX    L2: %f\n", L2);
//				fmt.format("XXXXXXXXXX    L1: %f\n", L1);
//				fmt.format("XXXXXXXXXX    M: %f\n", M);
//				fmt.format("XXXXXXXXXX    R1: %f\n", R1);
//				fmt.format("XXXXXXXXXX    R2: %f\n", R2);
			}
			if(M > L1 && L1 > L2 && M > R1 && R1 > R2)
				return true;
			else
				return false;
		}
		else
		{
			return false;
		}
	}
	private static int FindIndexPriceTuPo(ANLStock cANLStock, int iCheckDay, int checkCnt, int minBofengQujian)
	{
		int iIndexTupo = -1;
		int checkQujian = checkCnt;
		int bofengQujian = minBofengQujian;
		if(iCheckDay < checkQujian + bofengQujian/2) return -1;
		if(iCheckDay > cANLStock.historyData.size() - bofengQujian/2) return -1;
		
		ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iCheckDay);
		ANLStockDayKData cCheckDayKDataPre = cANLStock.historyData.get(iCheckDay-1);
		// 半周期内没有高于当前值
		for(int iIndex = iCheckDay-1; iIndex > iCheckDay-bofengQujian/2; iIndex--)  
		{
			ANLStockDayKData cTmpCheck = cANLStock.historyData.get(iIndex);
			if(cTmpCheck.close > cCheckDayKData.close)
			{
				return -1;
			}
		}
		// 查找突破波峰
		float curBofengMax = 0.0f;
		for(int iIndex = iCheckDay-bofengQujian/2-1; iIndex > iCheckDay-checkQujian + bofengQujian/2 + 1; iIndex--)  
		{
			boolean bbCheckBoFeng = CheckBoFeng(cANLStock, iIndex, bofengQujian);
			if(bbCheckBoFeng)
			{
				// 更新最大值波峰
				if(cANLStock.historyData.get(iIndex).close > curBofengMax)
					curBofengMax = cANLStock.historyData.get(iIndex).close;
				
				// 检测到波峰
				if(cCheckDayKData.close >= curBofengMax
						&& cCheckDayKDataPre.close <= curBofengMax)
				{
					//突破波峰
					return iIndex;
				}
			}
		}
		return iIndexTupo;
	}
	
	/////////////////////////////////////////////////////////////
	// paramWeiQuShi
	
	private static int paramWeiQuShi(ANLStock cANLStock, int iCheckBegin, int iCheckDay)
	{
		boolean bTestOutput = false;
		String sTestDate = "";
		if(sTestDate.contains(cANLStock.historyData.get(iCheckDay).date.replace("-", "")))
		{
			bTestOutput = true;
			fmt.format("TEST ----------------------------------------\n");
		}
		int retWeiQuShi = 0;
		int iCheckBeginFix = iCheckBegin - (5 - (iCheckDay-iCheckBegin + 1)%5);
		int numdays = iCheckDay-iCheckBeginFix + 1;
		int indexP0 = iCheckBeginFix + (numdays/5)*0;
		int indexP1 = iCheckBeginFix + (numdays/5)*1;
		int indexP2 = iCheckBeginFix + (numdays/5)*2;
		int indexP3 = iCheckBeginFix + (numdays/5)*3;
		int indexP4 = iCheckBeginFix + (numdays/5)*4;
		int indexP5 = iCheckBeginFix + (numdays/5)*5;
		
		float aveP01 = 0.0f;
		float aveP01Cnt = 0;
		for(int iIndex = indexP0; iIndex < indexP1; iIndex++)  
		{
			aveP01 = aveP01 + cANLStock.historyData.get(iIndex).close;
			aveP01Cnt++;
		}
		aveP01 = aveP01/aveP01Cnt;
		
		float aveP12 = 0.0f;
		float aveP12Cnt = 0;
		for(int iIndex = indexP1; iIndex < indexP2; iIndex++)  
		{
			aveP12 = aveP12 + cANLStock.historyData.get(iIndex).close;
			aveP12Cnt++;
		}
		aveP12 = aveP12/aveP12Cnt;
		
		float aveP23 = 0.0f;
		float aveP23Cnt = 0;
		for(int iIndex = indexP2; iIndex < indexP3; iIndex++)  
		{
			aveP23 = aveP23 + cANLStock.historyData.get(iIndex).close;
			aveP23Cnt++;
		}
		aveP23 = aveP23/aveP23Cnt;
		
		float aveP34 = 0.0f;
		float aveP34Cnt = 0;
		for(int iIndex = indexP3; iIndex < indexP4; iIndex++)  
		{
			aveP34 = aveP34 + cANLStock.historyData.get(iIndex).close;
			aveP34Cnt++;
		}
		aveP34 = aveP34/aveP34Cnt;
		
		float aveP45 = 0.0f;
		float aveP45Cnt = 0;
		for(int iIndex = indexP4; iIndex < indexP5; iIndex++)  
		{
			aveP45 = aveP45 + cANLStock.historyData.get(iIndex).close;
			aveP45Cnt++;
		}
		aveP45 = aveP45/aveP45Cnt;
		
		// 找到最低点索引 计算左右斜率比
		float minprice = cANLStock.historyData.get(iCheckBegin).close;
		int minIndex = iCheckBegin;
		for(int iIndex = iCheckBegin; iIndex < indexP5; iIndex++)  
		{
			if(cANLStock.historyData.get(iIndex).close < minprice) 
			{
				minprice = cANLStock.historyData.get(iIndex).close;
				minIndex = iIndex;
			}
		}
		float zhendong = (cANLStock.historyData.get(iCheckBegin).close - minprice)/minprice;
		
		if(bTestOutput)
		{
			fmt.format("    numdays=%d\n",numdays);
			fmt.format("    indexP0=%d\n",indexP0);
			fmt.format("    indexP1=%d\n",indexP1);
			fmt.format("    indexP2=%d\n",indexP2);
			fmt.format("    indexP3=%d\n",indexP3);
			fmt.format("    indexP4=%d\n",indexP4);
			fmt.format("    indexP5=%d\n",indexP5);
			fmt.format("    minIndex=%d\n",minIndex);
			fmt.format("    zhendong=%f\n",zhendong);
			fmt.format("    aveP01=%f\n",aveP01);
			fmt.format("    aveP12=%f\n",aveP12);
			fmt.format("    aveP23=%f\n",aveP23);
			fmt.format("    aveP34=%f\n",aveP34);
			fmt.format("    aveP45=%f\n",aveP45);
		}
		
		if(aveP01 > aveP12) retWeiQuShi++;
		if(aveP01 > aveP23) retWeiQuShi++;
		if(aveP01 > aveP34) retWeiQuShi++;
		
		if(aveP12 > aveP23) retWeiQuShi++;
		if(aveP12 > aveP34) retWeiQuShi++;
		
		if(aveP45 > aveP34) retWeiQuShi++;
		if(aveP45 > aveP23) retWeiQuShi++;
			
		return retWeiQuShi;
	}
	
	private static boolean paramLiangTupo(ANLStock cANLStock, int iCheckBeginIndex, int iCheckDay) {
		boolean bLiangTuPo = false;
		
		int numdays = (iCheckDay-iCheckBeginIndex+1)-1;
		
		ANLStockDayKData checkDayKData = cANLStock.historyData.get(iCheckDay);
		// 量大于均值的两倍
		float aveVolume = 0;
		for(int iIndex = iCheckBeginIndex; iIndex <= iCheckDay-1; iIndex++)  
		{  
			aveVolume = aveVolume + cANLStock.historyData.get(iIndex).volume;
		}
		aveVolume = aveVolume/numdays;
		boolean check1 = (checkDayKData.volume/aveVolume) > 2;
		// 后半周期是最大量
		boolean check2 = true;
		int fromIndex2 = iCheckDay - numdays/2;
		for(int iIndex = fromIndex2; iIndex <= iCheckDay-1; iIndex++)  
		{  
			if(cANLStock.historyData.get(iIndex).volume > checkDayKData.volume)
			{
				check2 = false;
			}
		}
		
		if(check1)
		{
			bLiangTuPo = true;
		}
		
		return bLiangTuPo;
	}
	
	private static float paramGaoZhiInDay(ANLStockDayKData cANLStockDayKData) 
	{
		float midprice = (cANLStockDayKData.close + cANLStockDayKData.open)/2;
		int lenList = cANLStockDayKData.priceList.size();
		int biggerCount = 0;
		for(int iIndex = 0; iIndex < lenList; iIndex++)  
		{  
			float curPrice = cANLStockDayKData.priceList.get(iIndex);
			if(curPrice > midprice)
			{
				biggerCount = biggerCount + 1;
			}
		}
		float fBiggerCount = biggerCount;
		float retParam = fBiggerCount/lenList;
		return retParam;
	}

	private static class RetZhenDang
	{
		public RetZhenDang(float paramZhenfu, float paramFangCha, float paramCrosstimes)
		{
			zhenFu = paramZhenfu;
			fangCha = paramFangCha;
			crossTimes = paramCrosstimes;
		}
		public float zhenFu;
		public float fangCha;
		public float crossTimes;
	}
	private static float calFangCha(List<Float> valList)
	{
		float valSum = 0.0f;
		int lenList = valList.size();
		for(int iIndex = 0; iIndex < lenList; iIndex++)  
		{
			float val = valList.get(iIndex);
			valSum = valSum + val;
		}
		float aveVal = valSum/lenList;
		float fcSum = 0.0f;
		for(int iIndex = 0; iIndex < lenList; iIndex++)  
		{
			float val = valList.get(iIndex);
			fcSum = fcSum + (val - aveVal) * (val - aveVal);
		}
		float fangcha = fcSum/lenList;
		return fangcha;
	}
	private static RetZhenDang paramZhenDang(List<ANLStockDayKData> cListANLStockDayKData)
	{
		int indexBegin = 0;
		int indexEnd = cListANLStockDayKData.size()-1;
		
		float highPrice = cListANLStockDayKData.get(indexBegin).open;
		float lowPrice = cListANLStockDayKData.get(indexBegin).open;
		for(int iIndex = indexBegin; iIndex < indexEnd+1; iIndex++)  
		{  
			float highday = cListANLStockDayKData.get(iIndex).high;
			float lowday = cListANLStockDayKData.get(iIndex).low;
            if(highday > highPrice)
            {
            	highPrice = highday;
            }
            if(lowday < lowPrice)
            {
            	lowPrice = lowday;
            }
		}
		float curZhenfu = (highPrice - lowPrice)/lowPrice;

		List<Float> valList = new ArrayList<Float>();
		float firstActuralVal = 0.0f;
		for(int iIndex = indexBegin; iIndex < indexEnd+1; iIndex++)  
		{
			float midval = cListANLStockDayKData.get(iIndex).close;
			if(iIndex == indexBegin)
			{
				firstActuralVal = midval;
				valList.add(0.0f);
			}
			else
			{
				float calval = (midval - firstActuralVal)/firstActuralVal;
				valList.add(calval);
			}
		}
		float curfangcha = calFangCha(valList)*1000;
		
		float ave = (highPrice + lowPrice)/2;
		int crosstimes = 0;
		for(int iIndex = indexBegin; iIndex < indexEnd-1; iIndex++)  
		{
			if((cListANLStockDayKData.get(iIndex).high-ave)*(cListANLStockDayKData.get(iIndex).close-ave) < 0)
			{
				crosstimes = crosstimes + 1;
			}
		}
		return new RetZhenDang(curZhenfu,curfangcha,crosstimes);
	}
}
