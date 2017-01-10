package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.fw.tranengine_lite.ANLStock;
import stormstock.fw.tranengine_lite.ANLStockDayKData;
import stormstock.fw.tranengine_lite.ANLDataProvider;
import stormstock.fw.tranengine_lite.ANLLog;
import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataWebStockAllList.StockItem;
import stormstock.ori.stockdata.DataWebStockDayDetail.DayDetailItem;
import stormstock.ori.stockdata.DataWebStockDayK.DayKData;

/*
 * ��ȡ�ù�Ʊ��Ϣ���ݵķ�ʽ
 */
public class RunGetDataSample {
	public static void main(String[] args) {
		ANLLog.outputConsole("Main Begin\n\n");
		// ��Ʊȫ�б�������й�Ʊid
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLLog.outputConsole(stockId + "\n");
		}
		// ���һֻ��Ʊ������k����
		ANLStock cANLStock = ANLDataProvider.getANLStock("600020");
		for(int j = 0; j < cANLStock.historyData.size(); j++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
			ANLLog.outputConsole("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
            // ���һ�콻�׵���ϸ����
            if(j == cANLStock.historyData.size()-1)
            {
            	cANLDayKData.LoadDetail();
            	for(int k = 0; k < cANLDayKData.detailDataList.size(); k++)  
            	{
            		ANLLog.outputConsole("    %s %.2f\n", 
            				cANLDayKData.detailDataList.get(k).time,
            				cANLDayKData.detailDataList.get(k).price);
            	}
            }
        } 
		ANLLog.outputConsole("\n\nMain End");
	}
}
