import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

public class Bot {

	public static void main(String[] args) throws AWTException, InterruptedException {
		int i = 0;
		int x = 0;
		int y = 0;
		int color = 0;
		int WHITE = 250;

		int TOP_LEFT_X = 870;
		int TOP_LEFT_Y = 530;
		int HEIGHT_OF_AREA = 250;
		int WIDTH_OF_AREA = 160;

		//decrease HR_ZONE for faster pitches
		//increase it for slower pitches
		//120 for 1st pitcher
		//110 for 2nd pitcher
		//90 for 3rd pitcher
		
		//105 for 5th pitcher
		
		int HR_ZONE = 55;
		int SAFE_ZONE = 20;

		int BASELINE_Y = 200;
		
		// adjust this for each pitcher
		int PITCHES = 30;
		int hits = 0;
		boolean ballvisible = false;
		
		boolean once = false;
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice devices[] = ge.getScreenDevices();

		Robot robot = new Robot(devices[1]);
		Rectangle screenRect = new Rectangle(TOP_LEFT_X, TOP_LEFT_Y,
				WIDTH_OF_AREA, HEIGHT_OF_AREA);
		
		while (hits < PITCHES) {
			
			BufferedImage screen = robot.createScreenCapture(screenRect);
			outsideloop:
			for (x = 0; x < screenRect.width; x=x+1) {
				for (y = 0; y < screenRect.height; y=y+1) {
					
					color = screen.getRGB(x, y);
					
					//red component is shifted 16 bits
					//green component is shifted 8 bits
					//blue component is simply and'ed
					if (((color & 0xFF) > WHITE) && ((color >> 8 & 0xFF) > WHITE)
							&& ((color >> 16 & 0xFF) > WHITE)) 
					{
						
						// keep Y centered so that Pooh only moves left to
						// right.
						if(!once)
						{
							robot.mouseMove(x + TOP_LEFT_X, TOP_LEFT_Y + BASELINE_Y);
							i++;

							if (y >= HR_ZONE) {
								if (!once) {					
									once = true;
								}
								robot.mousePress(InputEvent.BUTTON1_MASK);
								ballvisible = false;
							}
							
							if(!ballvisible)
								ballvisible = true;
							
						}
						if(once)
						{
							if (y < SAFE_ZONE) {
								//robot.mouseRelease(InputEvent.BUTTON1_MASK);
								once = false;
							}
						}

						// Uncomment below for image generation of homerun zone
						// - i.e. where bot tracks ball

						if (i % 100 == 0) {
							File outputfile = new File("saved" + i / 100
									+ ".png");
							try {
								ImageIO.write(screen, "png", outputfile);

							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					}
					else if ((closeTo((color & 0xFF), 255)) && (closeTo((color >> 8 & 0xFF), 90))
							&& (closeTo((color >> 16 & 0xFF), 25)))
					{
						once = false;
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
						once = false;
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
						once = false;
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
						if(once)
							once = false;
						if(ballvisible)
						{
							
								
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
	
	// it turns out robot.delay can be used instead
	// of sleeping all threads.
	// also, look into autodelay, which is a
	// component of robot instances
	// robot.delay(500);
	public static void allThreadSleep() throws InterruptedException
	{
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for (Thread th : threadArray) {
			//System.out.println("Thread " + th.getId() + " sleeping.");
			th.sleep(400);
		}
				
	}
}