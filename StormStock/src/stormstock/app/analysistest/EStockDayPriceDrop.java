package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 
 * @author wudi
 * ��鵱ǰλ���Ƿ��Ƕ��ڼ������ȵ�
 */

public class EStockDayPriceDrop {

	public static class ResultCheckPriceDrop
	{
		public ResultCheckPriceDrop()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public float maxDropRate;
	}
	// ���iCheck�Ƿ�������ڼ���
	public ResultCheckPriceDrop checkPriceDrop(List<StockDay> list, int iCheck)
	{
		ResultCheckPriceDrop cResultCheck = new ResultCheckPriceDrop();
		
		// ����ٽ���
		int iBegin = iCheck-10;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return cResultCheck;
		}
		int indexHigh = StockUtils.indexHigh(list, iBegin, iEnd);
		StockDay cStockDayHigh = list.get(indexHigh);
		float fStockDayHigh_high = cStockDayHigh.high();
		float fStockDayHigh_close = cStockDayHigh.close();
		int indexLow = StockUtils.indexLow(list, iBegin, iEnd);
		StockDay cStockDayLow = list.get(indexLow);
		float fStockDayLow_low = cStockDayLow.low();
		float fStockDayLow_close = cStockDayLow.close();
		//BLog.output("TEST", " %d %d \n", indexHigh, indexLow);
		
		// ��͵�����ߵ���� 
		if(0 <= indexHigh && indexHigh < indexLow)
		{
		}
		else
		{
			return cResultCheck;
		}
		
		// ������
		float MaxDropRate = (fStockDayLow_close-fStockDayHigh_close)/fStockDayHigh_close;
		if(MaxDropRate < -0.10)
		{
			cResultCheck.maxDropRate = MaxDropRate;
		}
		else
		{
			return cResultCheck;
		}
		
		// ��ǰ���ֽ���Ϊ��͵�
		if(iEnd - indexLow <= 4 && iEnd - indexLow > 0)
		{
		}
		else
		{
			return cResultCheck;
		}
		
		// ��ǰ�ռ۸��ܷ�������
		StockDay cCurStockDay = list.get(iCheck);
		float fCurStockDay_close =  cCurStockDay.close();
		float CurPriceRate = (fCurStockDay_close - fStockDayLow_close)/fStockDayLow_close;
		CurPriceRate = Math.abs(CurPriceRate);
		if(CurPriceRate <= 0.01f)
		{
		}
		else
		{
			return cResultCheck;
		}
		

		cResultCheck.bCheck = true;
		return cResultCheck;
	}
	// �ָ������iCheck�Ƿ�������ڼ������ų������Ը��ţ�
	public ResultCheckPriceDrop checkPriceDrop_sep(List<StockDay> list, int iCheck)
	{
		ResultCheckPriceDrop cResultCheckPriceDrop = new ResultCheckPriceDrop();
		
		ResultCheckPriceDrop cResultCheckPriceDropSingle = checkPriceDrop(list, iCheck);
		if(cResultCheckPriceDropSingle.bCheck) // ��������iCheck����CheckPoint
		{
			int iCheckOKLast = -1;
			int iBegin = iCheck - 20;
			int iEnd = iCheck;
			for(int i=iBegin; i<=iEnd; i++)
			{
				ResultCheckPriceDrop cResultCheckPriceDropBefore = checkPriceDrop(list, i);
				if(cResultCheckPriceDropBefore.bCheck)
				{
					iCheckOKLast = i;
					cResultCheckPriceDrop.maxDropRate = cResultCheckPriceDropBefore.maxDropRate;
					i=i+10;
				}
			}
			
			if(iCheckOKLast == iCheck)
			{
				cResultCheckPriceDrop.bCheck = true;
			}
		}

		return cResultCheckPriceDrop;
	}
	
	/*
	 * ********************************************************************
	 * Test
	 * ********************************************************************
	 */
	public static void main(String[] args)
	{
		BLog.output("TEST", "Main Begin\n");
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "600439"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2011-01-01", "2012-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayPriceDrop cEStockDayPriceDrop = new EStockDayPriceDrop();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			ResultCheckPriceDrop cResultCheckPriceDrop = cEStockDayPriceDrop.checkPriceDrop(list, i);
			if (cResultCheckPriceDrop.bCheck)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "D");
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceDrop.jpg");
}
