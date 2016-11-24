package stormstock.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import stormstock.analysis.ANLStockDayKData.DetailData;
import stormstock.data.DataEngine.ExKData;

public class ANLImgShow {
	public static class CurvePoint
	{
		public CurvePoint(){ m_x = 0.0f; m_y = 0.0f;m_name = "";}
		public CurvePoint(float x, float y) { m_x = x; m_y = y;m_name = "";}
		public CurvePoint(float x, float y, String name) { m_x = x; m_y = y; m_name = name;}
		public float m_x;
		public float m_y;
		public String m_name;
	}
	
	public void GenerateImage()
	{   
        try {
        	File file = new File(m_fileName);
			ImageIO.write(m_bi, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	// 描画任意数值曲线， 次曲线将映射到图片上
	public void writeLogicCurve(List<CurvePoint> LogicPoiList, int index)
	{
		float beginx = LogicPoiList.get(0).m_x;
		float beginy = LogicPoiList.get(0).m_y;
		float min_logic_x =  1000000.0f;
		float min_logic_y =  1000000.0f;
		float max_logic_x = -1000000.0f;
		float max_logic_y = -1000000.0f;
		for(int i = 0; i < LogicPoiList.size(); i++)  
        {  
			CurvePoint cPoi = LogicPoiList.get(i); 
			if(cPoi.m_x <= min_logic_x) min_logic_x = cPoi.m_x;
			if(cPoi.m_y <= min_logic_y) min_logic_y = cPoi.m_y;
			if(cPoi.m_x >= max_logic_x) max_logic_x = cPoi.m_x;
			if(cPoi.m_y >= max_logic_y) max_logic_y = cPoi.m_y;
        }
		float logic_unit_max_width = max_logic_x - min_logic_x;
		float logic_unit_max_hight = (max_logic_y - min_logic_y);
		
		List<CurvePoint> poiUnitList = new ArrayList<CurvePoint>();
		int AutoWriteTextSpan = LogicPoiList.size()/5;
		for(int i = 0; i < LogicPoiList.size(); i++)  
        {  
			CurvePoint cPoi = LogicPoiList.get(i); 
			float unitX = (cPoi.m_x - beginx)/logic_unit_max_width;
			float unitY = (cPoi.m_y - beginy)/logic_unit_max_hight;
			String textstr = "";
			if(cPoi.m_name != "")
			{
				textstr = cPoi.m_name;
			}
			else
			{
				float val = cPoi.m_y;
				float rate = (cPoi.m_y - beginy)/beginy*100;
				if(i == 0 || i == LogicPoiList.size() -1 
						|| 0 == Float.compare(max_logic_y, val) || 0 == Float.compare(min_logic_y, val)
						|| (i%AutoWriteTextSpan == 0 && i > 0 & i < LogicPoiList.size() - AutoWriteTextSpan/2) )
				{
					textstr = String.format("(%.2f, %.2f%%)", val, rate);
					poiUnitList.add(new CurvePoint(unitX, unitY, textstr));
				}
			}
			poiUnitList.add(new CurvePoint(unitX, unitY, textstr));
        }
		
		writeUnitCurve(poiUnitList, index);
	}
	
    // 按单位1描画，图片左侧中点为(0,0)，右上角为 (0,1)，逻辑数值无单位
	public void writeUnitCurve(List<CurvePoint> poiList, int index)
	{
		List<CurvePoint> poiPixList = new ArrayList<CurvePoint>();
		for(int i = 0; i < poiList.size(); i++)  
        {  
			CurvePoint cPoi = poiList.get(i);
			int newX = (int)(m_padding_x + cPoi.m_x*m_unitWidth);
			int newY = (int)(m_padding_y +  m_unitHight - cPoi.m_y*m_unitHight);
			// ANLLog.outputConsole("%.2f,%.2f ", cPoi.m_x, cPoi.m_y);
			// ANLLog.outputConsole("New %d,%d \n", newX, newY);
			poiPixList.add(new CurvePoint(newX, newY, cPoi.m_name));
        }
		writeImagePixelCurve(poiPixList, index);
	}
	
	// 按实际图片像素描画曲线，图片左上为（0.0），右下角为图片最大长宽，单位是像素
	public void writeImagePixelCurve(List<CurvePoint> poiList, int index)
	{
		if(index == 0) m_g2.setPaint(Color.BLACK);
		if(index == 1) m_g2.setPaint(Color.RED);
		if(index == 2) m_g2.setPaint(Color.GREEN);
		if(index == 3) m_g2.setPaint(Color.YELLOW);
		for(int i = 0; i < poiList.size()-1; i++)  
        {  
			CurvePoint cPoiBegin = poiList.get(i); 
			CurvePoint cPoiEnd = poiList.get(i+1); 
			int BeginX = (int)cPoiBegin.m_x;
			int BeginY = (int)cPoiBegin.m_y;
			int EndX = (int)cPoiEnd.m_x;
			int EndY = (int)cPoiEnd.m_y;
			m_g2.drawLine(BeginX, BeginY, EndX, EndY);
			if (cPoiBegin.m_name != "")
			{
				m_g2.drawString(cPoiBegin.m_name, (int)BeginX - 10, (int)BeginY - 5);
				m_g2.fillOval((int)BeginX, (int)BeginY, 8, 8);
			}
        }
	}
	
	public ANLImgShow(int width, int high, String fileName)
	{
		m_widthPix = width;
		m_hightPix = high;
		m_fileName = fileName;
		m_bi = new BufferedImage(m_widthPix, m_hightPix, BufferedImage.TYPE_INT_RGB);
		m_g2 = (Graphics2D)m_bi.getGraphics();   
		m_g2.setBackground(Color.WHITE);   
		m_g2.clearRect(0, 0, m_widthPix, m_hightPix);   
		m_g2.setPaint(Color.BLACK);   
        Font font = new Font(Font.MONOSPACED,Font.BOLD, 15);   
        m_g2.setFont(font);
        m_g2.setStroke(new BasicStroke(1.0f)); // 线粗细
        
    	m_padding_x = (int)(m_widthPix * 0.1f);
    	m_padding_y = (int)(m_hightPix * 0.05f);
    	m_unitWidth = (int)(m_widthPix - 2*m_padding_x);
    	m_unitHight = (int)((m_hightPix - 2*m_padding_y)/2.0f);
    	
    	// 描画坐标系
    	List<CurvePoint> PoiList_linex = new ArrayList<CurvePoint>();
    	PoiList_linex.add(new CurvePoint(0.0f,0.0f));
    	PoiList_linex.add(new CurvePoint(1.0f,0.0f));
    	writeUnitCurve(PoiList_linex, 0);
    	List<CurvePoint> PoiList_liney = new ArrayList<CurvePoint>();
    	PoiList_liney.add(new CurvePoint(0.0f,1.0f));
    	PoiList_liney.add(new CurvePoint(0.0f,-1.0f));
    	writeUnitCurve(PoiList_liney, 0);

//        String s = "test";
//        FontRenderContext context = m_g2.getFontRenderContext();
//        Rectangle2D bounds = font.getStringBounds(s, context);   
//        double x = (m_widthPix - bounds.getWidth()) / 2;   
//        double y = (m_hightPix - bounds.getHeight()) / 2;   
//        double ascent = -bounds.getY();   
//        double baseY = y + ascent;   
//        m_g2.drawString(s, (int)x, (int)baseY);   
	}

    //坐标系参数
	private int m_padding_x;
	private int m_padding_y;
	private int m_unitWidth;
	private int m_unitHight;

	// 图像参数
	private BufferedImage m_bi;
	private Graphics2D m_g2;
	private int m_widthPix;
	private int m_hightPix;
	private String m_fileName;
}
