package pooh.bot;

import java.awt.image.*;

public class EdgeProcessor {

   private static int[][] sobelx = {{-1, 0, 1},{-2, 0, 2},{-1, 0, 1}};
   private static int[][] sobely = {{1, 2, 1},{0, 0, 0},{-1, -2, -1}};

   public static int lum(int r, int g, int b) {
       return (r*3 + b + g*4) >> 3;
   }

   public static int rgb_to_luminance(int rgb) {
       int r = (rgb & 0xff0000) >> 16;
       int g = (rgb & 0xff00) >> 8;
       int b = (rgb & 0xff);
       //System.out.println(r + ", " + g + ", " + b);
       return lum(r, g, b);
   }

   public static int level_to_greyscale(int level) {
       return (level << 16) | (level << 8) | level;
   }

   public static BufferedImage cloneImageGray(BufferedImage image) {
       return new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
   }

   public static BufferedImage sobelEdgeDetection(BufferedImage image) {
       int level = 0;
       BufferedImage ret = cloneImageGray(image);
       int width = image.getWidth();
       int height = image.getHeight();
       for (int x = 0; x < width; x++) {
           for (int y = 0; y < height; y++) {
               level = 255;
               if ((x > 0) && (x < (width - 1)) && (y > 0) && (y < (height - 1))) {
                   int sumX = 0;
                   int sumY = 0;
                   for (int i = -1; i < 2; i++) {
                       for (int j = -1; j < 2; j++) {
                           sumX += rgb_to_luminance(image.getRGB(x+i, y+j)) * sobelx[i+1][j+1];
                           sumY += rgb_to_luminance(image.getRGB(x+i, y+j)) * sobely[i+1][j+1];
                       }
                   }
                   level = Math.abs(sumX) + Math.abs(sumY);
                   if (level < 0) {
                       level = 0;
                   } else if (level > 255) {
                       level = 255;
                   }
                   level = 255 - level;
               }
               ret.setRGB(x, y, level);
           }
       }
       return ret;
   }
}

