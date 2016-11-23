package stormstock.analysis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ANLImgShow {
	public static void test()
	{
		int width = 1600;   
        int height = 900;   

        File file = new File("image.jpg");   

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
        Graphics2D g2 = (Graphics2D)bi.getGraphics();   
        g2.setBackground(Color.WHITE);   
        g2.clearRect(0, 0, width, height);   
        g2.setPaint(Color.RED);   
        Font font = new Font(Font.MONOSPACED,Font.BOLD, 100);   
        g2.setFont(font);
           
        String s = "test";
        FontRenderContext context = g2.getFontRenderContext();
        
        Rectangle2D bounds = font.getStringBounds(s, context);   
        double x = (width - bounds.getWidth()) / 2;   
        double y = (height - bounds.getHeight()) / 2;   
        double ascent = -bounds.getY();   
        double baseY = y + ascent;   
        g2.drawString(s, (int)x, (int)baseY);   
        
        g2.drawLine(0, 0, 1600, 900);
           
        try {
			ImageIO.write(bi, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
}
