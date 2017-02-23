package stormstock.fw.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class BImageCurve {

	public static class CurvePoint
	{
		public CurvePoint()
		{ 
			m_x = 0.0f; 
			m_y = 0.0f;
			m_name = "";
			m_marked = false;
		}
		public CurvePoint(float x, float y) 
		{ 
			m_x = x; 
			m_y = y;
			m_name = "";
			m_marked = false;
		}
		public CurvePoint(float x, float y, String name) 
		{ 
			m_x = x; 
			m_y = y; 
			m_name = name;
			m_marked = false;
		}
		public CurvePoint(float x, float y, String name, boolean marked) 
		{ 
			m_x = x; 
			m_y = y; 
			m_name = name;
			m_marked = marked;
		}
		public float m_x;
		public float m_y;
		public String m_name;
		public boolean m_marked;
	}
	
	public void clear()
	{
		m_g2.clearRect(0, 0, m_widthPix, m_hightPix);
		GenerateImage();
	}
	
	public void GenerateImage()
	{   
		// 绘制多条Y同比曲线
		writeMultiLogicCurveSameRatio();
		
        try {
        	File file = new File(m_fileName);
			ImageIO.write(m_bi, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	// 描画任意数值曲线， 此曲线将映射到图片上，多曲线Y同比例调整
	public void addLogicCurveSameRatio(List<CurvePoint> LogicPoiList, int index)
	{
		if(LogicPoiList.size() == 0) return;
		
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
		
		List<CurvePoint> poiCurList = new ArrayList<CurvePoint>();
		int AutoWriteTextSpan = LogicPoiList.size()/5;
		int iPreSpan = 0;
		boolean bHighTextOut = false;
		boolean bLowTextOut = false;
		for(int i = 0; i < LogicPoiList.size(); i++)  
        {  
			CurvePoint cPoi = LogicPoiList.get(i); 
			float curX = (cPoi.m_x - beginx)/logic_unit_max_width;
			float curY = (cPoi.m_y - beginy)/beginy;
			boolean marked = cPoi.m_marked;
			String textstr = "";
			if(cPoi.m_name != "")
			{
				textstr = cPoi.m_name;
			}
			else
			{
				float val = cPoi.m_y;
				float rate = (cPoi.m_y - beginy)/beginy*100;
				if(i == 0 || i == LogicPoiList.size() -1) // 头尾带文字
				{
					textstr = String.format("(%.2f, %.2f%%)", val, rate);
				}
				if(AutoWriteTextSpan > 0)
				{
					if((i%AutoWriteTextSpan == 0 && 
							i > 0 && i < LogicPoiList.size() - AutoWriteTextSpan/2) ) // 中间部分固定跨度带文字
					{
						textstr = String.format("(%.2f, %.2f%%)", val, rate);
						iPreSpan = i;
					}
					if(0 == Float.compare(max_logic_y, val) && !bHighTextOut) // 最高最低带文字
					{
						if( iPreSpan+AutoWriteTextSpan-i >=  AutoWriteTextSpan/4 
								&& LogicPoiList.size()-1-i >= AutoWriteTextSpan/4) // 避免与下一个文字过分重合
						{
							textstr = String.format("(%.2f, %.2f%%)", val, rate);
							bHighTextOut = true;
						}
					}
					if(0 == Float.compare(min_logic_y, val) && !bLowTextOut) // 最高最低带文字
					{
						if( iPreSpan+AutoWriteTextSpan-i >=  AutoWriteTextSpan/4 
								&& LogicPoiList.size()-1-i >= AutoWriteTextSpan/4) // 避免与下一个文字过分重合
						{
							textstr = String.format("(%.2f, %.2f%%)", val, rate);
							bLowTextOut = true;
						}
					}
				}
			}
			poiCurList.add(new CurvePoint(curX, curY, textstr, marked));
        }
		
		m_cMultiUnitCurveMap.put(index, poiCurList);
	}
	public void writeMultiLogicCurveSameRatio()
	{
		//找到最大y
		float fMaxY = 0.0f; 
		for (Map.Entry<Integer, List<CurvePoint>> entry : m_cMultiUnitCurveMap.entrySet()) {  
			
			List<CurvePoint> cCurPoiList = entry.getValue();
			for(int i = 0; i < cCurPoiList.size(); i++) 
			{
				CurvePoint cPoi = cCurPoiList.get(i);
				if(cPoi.m_y > fMaxY) fMaxY = cPoi.m_y;
				if(-cPoi.m_y > fMaxY) fMaxY = -cPoi.m_y;
			} 
		}  
		fMaxY = fMaxY*1.10f;
		
		// 高度同比化
		for (Map.Entry<Integer, List<CurvePoint>> entry : m_cMultiUnitCurveMap.entrySet()) {  
			Integer key = entry.getKey();
			List<CurvePoint> cCurPoiList = entry.getValue();
			for(int i = 0; i < cCurPoiList.size(); i++) 
			{
				CurvePoint cPoi = cCurPoiList.get(i);
				cPoi.m_y = cPoi.m_y / fMaxY;
			} 
			// 绘制单位曲线到图像
			writeUnitCurve(cCurPoiList, key);
		} 
	}
	
	// 描画任意数值曲线， 此曲线将映射到图片上，Y自比例调整，与其他曲线无关
	public void writeLogicCurve(List<CurvePoint> LogicPoiList, int index)
	{
		if(LogicPoiList.size() == 0) return;
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
		int iPreSpan = 0;
		for(int i = 0; i < LogicPoiList.size(); i++)  
        {  
			CurvePoint cPoi = LogicPoiList.get(i); 
			float unitX = (cPoi.m_x - beginx)/logic_unit_max_width;
			float unitY = (cPoi.m_y - beginy)/logic_unit_max_hight;
			boolean marked = cPoi.m_marked;
			String textstr = "";
			if(cPoi.m_name != "")
			{
				textstr = cPoi.m_name;
			}
			else
			{
				float val = cPoi.m_y;
				float rate = (cPoi.m_y - beginy)/beginy*100;
				if(i == 0 || i == LogicPoiList.size() -1) // 头尾带文字
				{
					textstr = String.format("(%.2f, %.2f%%)", val, rate);
				}
				if((i%AutoWriteTextSpan == 0 && 
						i > 0 && i < LogicPoiList.size() - AutoWriteTextSpan/2) ) // 中间部分固定跨度带文字
				{
					textstr = String.format("(%.2f, %.2f%%)", val, rate);
					iPreSpan = i;
				}
				if(0 == Float.compare(max_logic_y, val) || 0 == Float.compare(min_logic_y, val)) // 最高最低带文字
				{
					if( iPreSpan+AutoWriteTextSpan-i >=  AutoWriteTextSpan/4 
							&& LogicPoiList.size()-1-i >= AutoWriteTextSpan/4) // 避免与下一个文字过分重合
					{
						textstr = String.format("(%.2f, %.2f%%)", val, rate);
					}
				}
			}
			poiUnitList.add(new CurvePoint(unitX, unitY, textstr, marked));
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
			boolean marked = cPoi.m_marked;
			// ANLLog.outputConsole("%.2f,%.2f ", cPoi.m_x, cPoi.m_y);
			// ANLLog.outputConsole("New %d,%d \n", newX, newY);
			poiPixList.add(new CurvePoint(newX, newY, cPoi.m_name, marked));
        }
		writeImagePixelCurve(poiPixList, index);
	}
	
	// 按实际图片像素描画曲线，图片左上为（0.0），右下角为图片最大长宽，单位是像素
	public void writeImagePixelCurve(List<CurvePoint> poiList, int index)
	{
		// 绘制线段与文字
		if(index == 0) m_g2.setPaint(Color.BLACK);
		if(index == 1) m_g2.setPaint(Color.BLUE);
		if(index == 2) m_g2.setPaint(Color.GREEN);
		if(index == 3) m_g2.setPaint(Color.YELLOW);
		if(index == 4) m_g2.setPaint(Color.CYAN);
		if(index == 5) m_g2.setPaint(Color.PINK);
		if(index == 6) m_g2.setPaint(Color.ORANGE);
		if(index == 7) m_g2.setPaint(Color.GRAY);
		for(int i = 0; i < poiList.size(); i++)  
        {  
			// 绘制线段
			if(i <  poiList.size() - 1)
			{
				CurvePoint cPoiBegin = poiList.get(i); 
				CurvePoint cPoiEnd = poiList.get(i+1); 
				int BeginX = (int)cPoiBegin.m_x;
				int BeginY = (int)cPoiBegin.m_y;
				int EndX = (int)cPoiEnd.m_x;
				int EndY = (int)cPoiEnd.m_y;
				m_g2.drawLine(BeginX, BeginY, EndX, EndY);
			}
			// 绘制文字
			{
				CurvePoint cPoi = poiList.get(i); 
				if (cPoi.m_name != "")
				{
					m_g2.drawString(cPoi.m_name, (int)cPoi.m_x - 10, (int)cPoi.m_y - 5);
					m_g2.fillOval((int)cPoi.m_x-3, (int)cPoi.m_y-3, 6, 6);
				}
			}
        }
		
		// 绘制标记
		m_g2.setPaint(Color.RED);
		for(int i = 0; i < poiList.size(); i++)  
        {  
			CurvePoint cPoi = poiList.get(i); 
			if (cPoi.m_marked)
			{
				m_g2.fillOval((int)cPoi.m_x-4, (int)cPoi.m_y-4, 8, 8);
				
				int OriX = (int)cPoi.m_x;
				int OriY = (int)cPoi.m_y;
				
				int BeginX = OriX;
				int BeginY = OriY+8;
				int EndX = OriX;
				int EndY = OriY + 28;
				m_g2.drawLine(BeginX, BeginY, EndX, EndY);
				
				BeginX = OriX;
				BeginY = OriY+8;
				EndX = BeginX-5;
				EndY = BeginY+8;
				m_g2.drawLine(BeginX, BeginY, EndX, EndY);
				
				BeginX = OriX;
				BeginY = OriY+8;
				EndX = BeginX+5;
				EndY = BeginY+8;
				m_g2.drawLine(BeginX, BeginY, EndX, EndY);
			}
        }
	}
	
	public BImageCurve(int width, int high, String fileName)
	{
		m_cMultiUnitCurveMap = new HashMap<Integer, List<CurvePoint>>();
		
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
    	m_unitWidth = (int)(m_widthPix - 2.5*m_padding_x);
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
	
	// 同比例多曲线表，点记录的是Unit数据，最大Y为单位1
	private Map<Integer, List<CurvePoint>> m_cMultiUnitCurveMap; // <index, curvePointList>

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
