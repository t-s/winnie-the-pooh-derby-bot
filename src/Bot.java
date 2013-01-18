import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

public class Bot {

	public static void main(String[] args) throws AWTException, InterruptedException 
	{	
		int i = 0;
		int x = 0;
		int y = 0;
		int color = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		int j = 0;
		boolean once = false;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice devices[] = ge.getScreenDevices();

	    Robot robot = new Robot(devices[1]); 
	    //robot.mouseMove(950, 700); 
	    robot.mouseMove(1200,800);
	    Rectangle screenRect = new Rectangle(874,550,149,250);
	    System.out.println(InputEvent.BUTTON1_MASK);
	    while(i < 100000)
	    {
	    	BufferedImage img = robot.createScreenCapture(screenRect);
	    	for(x = 0; x < 149; ++x)
	    	{
	    		for(y = 0; y < 250; ++y)
	    		{
	    			color = img.getRGB(x,y);
						
	    			if ((((color)&0xFF) > 240) && (((color>>8)&0xFF) > 240) && (((color>>16)&0xFF) > 240))
	    			{
	    				robot.mouseMove(x+874, 550+150);
	    				i++;
	    				if(y > 85)
	    				{
	    					robot.mousePress(InputEvent.BUTTON1_MASK);
	    					
	    					if(!once)
	    					{
	    						Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
	    						Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
	    						for (Thread th : threadArray)
	    						{
	    							System.out.println("Thread " + th.getId() + " sleeping.");
	    							th.sleep(500);
	    						}
	    						once = true;
	    						robot.mouseMove(874+ 74, 550+150);
	    					}
	    				}
	    				if(y < 50)
	    				{
	    					robot.mouseRelease(InputEvent.BUTTON1_MASK);
	    					once = false;
	    				}
	    				//File outputfile = new File("saved" + i + ".png");
	    		    	//try {
	    				//	ImageIO.write(img, "png", outputfile);
	    					
	    				//} catch (IOException e) {
	    				//	e.printStackTrace();
	    				//}
	    			}		
	    		}
	    	}
	    }	    	
	 }
	
}
