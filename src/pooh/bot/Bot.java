package pooh.bot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.event.AWTEventListener;



public class Bot {

	public static int TOP_LEFT_X;
	public static int TOP_LEFT_Y;
	public static boolean coordsSet = false;

	public static void main(String[] args) throws AWTException, InterruptedException {
		
		Toolkit.getDefaultToolkit().addAWTEventListener(
				new Listener(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
		JFrame frame = new JFrame();
		frame.setVisible(true);
		
		int i = 0;
		int x = 0;
		int y = 0;
		int color = 0;
		int WHITE = 250;

		int HEIGHT_OF_AREA = 250;
		int WIDTH_OF_AREA = 160;

		// decrease HR_ZONE for faster pitches
		// increase it for slower pitches
		// 120 for 1st pitcher
		// 110 for 2nd pitcher
		// 90 for 3rd pitcher
		// 105 for 5th pitcher
		int HR_ZONE = 120;

		int BASELINE_Y = 200;
		
		// adjust this for each pitcher
		int PITCHES = 10;
		int hits = 0;
		
		boolean ballvisible = true;
		boolean pitched = false;
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice devices[] = ge.getScreenDevices();
		Robot robot = new Robot(devices[devices.length-1]);
		
		SobelEdgeProcessor sobel = new SobelEdgeProcessor();
		
		while (!coordsSet)
		{
			// this loop will run forever, regardless of whether or not mouse has been clicked
			// unless this print statement runs:
			// I will have to see why that is!
			System.out.println(coordsSet);
		}
		
		System.out.println("X is now " + TOP_LEFT_X);
		System.out.println("Y is now " + TOP_LEFT_Y);
		
		Rectangle screenRect = new Rectangle(Bot.TOP_LEFT_X, TOP_LEFT_Y,
				WIDTH_OF_AREA, HEIGHT_OF_AREA);
		
		while (hits < PITCHES) {
			
			BufferedImage screen = robot.createScreenCapture(screenRect);

			BufferedImage edge = sobel.cloneImageGray(screen);
			
			outsideloop:
			for (x = 0; x < screenRect.width; x++) {
				for (y = 0; y < screenRect.height; y++) {
					
					color = screen.getRGB(x, y);
					
					// red component is shifted 16 bits
					// green component is shifted 8 bits
					// blue component is simply and'ed
					if (((color & 0xFF) > WHITE) && ((color >> 8 & 0xFF) > WHITE)
							&& ((color >> 16 & 0xFF) > WHITE)) 
					{

						// keep Y centered so that Pooh only moves left to
						// right.

							robot.mouseMove(TOP_LEFT_X + x, TOP_LEFT_Y + BASELINE_Y);
							
							if(!ballvisible)
								ballvisible = true;
							
							i++;

							// ball is at point where we should swing
							if (y >= HR_ZONE) {
								robot.mousePress(InputEvent.BUTTON1_MASK);
							}


						// Uncomment below for image generation of homerun zone
						// - i.e. where bot tracks ball
						
						if (i % 100 == 0) {
							File outputfile = new File("edge" + i / 100
									+ ".png");
							try {
								ImageIO.write(edge, "png", outputfile);

							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					}
					else if ((closeTo((color & 0xFF), 255)) && (closeTo((color >> 8 & 0xFF), 90))
							&& (closeTo((color >> 16 & 0xFF), 25)))
					{
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						System.out.println("Strike!");
						robot.mouseMove(WIDTH_OF_AREA/2 + TOP_LEFT_X, TOP_LEFT_Y + BASELINE_Y);
						allThreadSleep();
						hits++;
						
						break outsideloop;
					}
					else if ((closeTo((color & 0xFF), 83)) && (closeTo((color >> 8 & 0xFF), 130))
							&& (closeTo((color >> 16 & 0xFF), 255)))
					{
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						System.out.println("Home run!");
						robot.mouseMove(WIDTH_OF_AREA/2 + TOP_LEFT_X, TOP_LEFT_Y + BASELINE_Y);
						allThreadSleep();
						hits++;
						
						break outsideloop;
					}
					else if ((closeTo((color & 0xFF), 57)) && (closeTo((color >> 8 & 0xFF), 175))
							&& (closeTo((color >> 16 & 0xFF), 255)))
					{
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						System.out.println("Hit!");
						robot.mouseMove(WIDTH_OF_AREA/2 + TOP_LEFT_X, TOP_LEFT_Y + BASELINE_Y);
						allThreadSleep();
						hits++;
						
						break outsideloop;
					}
					
					//end screen
					else if ((closeTo((color & 0xFF), 107)) && (closeTo((color >> 8 & 0xFF), 213))
							&& (closeTo((color >> 16 & 0xFF), 250)))
					{
						System.exit(1);
					}
					else
					{

						if(ballvisible)
						{
							ballvisible = false;
							//System.out.println("Ball has become invisible!");	
						}
					}
				}
			}
		}
	}
	
	public static boolean closeTo(int num1, int num2)
	{
		if((num1 <= num2 + 1) && (num1 >= num2 - 1))
			return true;
		else
			return false;
				
	}
	

	public static void allThreadSleep() throws InterruptedException
	{
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for (Thread th : threadArray) {
			th.sleep(400);
		}
				
	}
	
	static class Listener extends Bot implements AWTEventListener {
		static boolean enabled = true;
        public void eventDispatched(AWTEvent event) {
        	
        	// event ID for losing focus is 1005
        	// which is what happens upon first mouse click
        	if(event.getID() == 1005)
        	{
        		if(enabled == true)
        		{	
        			// detection box is 240 pixels to the right of top left X
        			// detection box is 90 pixels down from top left Y
        			TOP_LEFT_X = MouseInfo.getPointerInfo().getLocation().x + 240;
        			TOP_LEFT_Y = MouseInfo.getPointerInfo().getLocation().y + 90;
        			enabled = false;
        			coordsSet = true;
        		}
        	}
        }
    }
}