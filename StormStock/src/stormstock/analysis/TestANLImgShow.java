package stormstock.analysis;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLImgShow.CurvePoint;

public class TestANLImgShow {
	public static Formatter fmt = new Formatter(System.out);
	
	public static void test_writeLogicCurve_shangzheng()
	{
		ANLImgShow cANLImgShow = new ANLImgShow(1600,900,"test_writeLogicCurve_shangzheng.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999", "2014-01-01", "2016-01-01");
		for(int j = 0; j < cANLStock.historyData.size(); j++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
            fmt.format("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
            PoiList.add(new CurvePoint(j,cANLDayKData.open));
        } 
		cANLImgShow.writeLogicCurve(PoiList, 1);
		cANLImgShow.GenerateImage();
	}
	
	public static void test_writeLogicCurve()
	{
		ANLImgShow cANLImgShow = new ANLImgShow(1600,900,"test_writeLogicCurve.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		PoiList.add(new CurvePoint(10.0f,110.1f));
		PoiList.add(new CurvePoint(20.1f,180.2f));
		PoiList.add(new CurvePoint(30.2f,290.3f));
		PoiList.add(new CurvePoint(40.3f,560.4f));
		PoiList.add(new CurvePoint(50.4f,300.2f));
		PoiList.add(new CurvePoint(60.5f,-22.1f));
		PoiList.add(new CurvePoint(70.6f,-110.3f));
		PoiList.add(new CurvePoint(80.7f,0.0f));
		PoiList.add(new CurvePoint(90.8f,110.1f));
		PoiList.add(new CurvePoint(100.9f,400.2f));
		PoiList.add(new CurvePoint(110.0f,110.9f));
		cANLImgShow.writeLogicCurve(PoiList, 1);
		cANLImgShow.GenerateImage();
	}
	
	public static void test_writeUnitCurve()
	{
		ANLImgShow cANLImgShow = new ANLImgShow(1600,900,"test_writeUnitCurve.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		PoiList.add(new CurvePoint(0.0f,0.1f));
		PoiList.add(new CurvePoint(0.1f,0.2f));
		PoiList.add(new CurvePoint(0.2f,0.3f));
		PoiList.add(new CurvePoint(0.3f,0.4f));
		PoiList.add(new CurvePoint(0.4f,0.2f));
		PoiList.add(new CurvePoint(0.5f,-0.1f));
		PoiList.add(new CurvePoint(0.6f,-0.3f));
		PoiList.add(new CurvePoint(0.7f,0.0f));
		PoiList.add(new CurvePoint(0.8f,0.1f));
		PoiList.add(new CurvePoint(0.9f,0.2f));
		PoiList.add(new CurvePoint(1.0f,0.9f));
		cANLImgShow.writeUnitCurve(PoiList, 1);
		cANLImgShow.GenerateImage();
	}
	
	public static void test_writeImagePixelCurve()
	{
		ANLImgShow cANLImgShow = new ANLImgShow(1600,900,"test_writeImagePixelCurve.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		PoiList.add(new CurvePoint(0.0f,200.1f));
		PoiList.add(new CurvePoint(100.0f,250.1f));
		PoiList.add(new CurvePoint(200.0f,350.1f));
		PoiList.add(new CurvePoint(300.0f,630.1f));
		PoiList.add(new CurvePoint(400.0f,740.1f));
		PoiList.add(new CurvePoint(500.0f,245.1f));
		PoiList.add(new CurvePoint(600.0f,147.1f));
		PoiList.add(new CurvePoint(700.0f,158.1f));
		PoiList.add(new CurvePoint(800.0f,190.1f));
		PoiList.add(new CurvePoint(900.0f,288.1f));
		PoiList.add(new CurvePoint(1000.0f,328.1f));
		PoiList.add(new CurvePoint(1100.0f,520.2f));
		cANLImgShow.writeImagePixelCurve(PoiList, 1);
		cANLImgShow.GenerateImage();
	}
	
	public static void main(String[] args) {
		test_writeImagePixelCurve();
		test_writeUnitCurve();
		test_writeLogicCurve();
		test_writeLogicCurve_shangzheng();
	}
}
