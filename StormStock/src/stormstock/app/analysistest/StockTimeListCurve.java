package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BImageCurve;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StockTimeListCurve {
	public StockTimeListCurve(String fileName)
	{
		m_poiList = new ArrayList<CurvePoint>();
		m_imageCurve = new BImageCurve(1600,900,fileName);
	}
	
	public void clear()
	{
		m_poiList.clear();
		m_imageCurve.clear();
	}
	
	public void setCurve(List<StockTime> list)
	{
		for(int i = 0; i < list.size(); i++)  
        {  
			StockTime cStockTime = list.get(i);
			
			CurvePoint cCurvePoint = new CurvePoint();
			cCurvePoint.m_x = i;
			cCurvePoint.m_y = cStockTime.price;
			m_poiList.add(cCurvePoint);
        }
	}
	public void markCurveIndex(int index, String name)
	{
		for(int i = 0; i < m_poiList.size(); i++)  
        {  
			if (i == index)
			{
				m_poiList.get(i).m_marked = true;
				m_poiList.get(i).m_name = name;
			}
        }
	}
	
	public void generateImage()
	{
		m_imageCurve.writeLogicCurve(m_poiList, 1);
		m_imageCurve.GenerateImage();
	}
	
	private List<CurvePoint> m_poiList;
	private BImageCurve m_imageCurve;
}
