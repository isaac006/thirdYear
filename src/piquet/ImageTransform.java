/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piquet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author isaac006
 */
public class ImageTransform
{
  public static BufferedImage resizeImage(BufferedImage img, int width, int height)
  {
    int imgWidth = img.getWidth();
    int imgHeight = img.getHeight();
    if (imgWidth*height < imgHeight*width) width = imgWidth*height/imgHeight;
    else height = imgHeight*width/imgWidth;
    
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = newImage.createGraphics();
    
    try
    {
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setBackground(new Color(0,0,0,0));
      g2d.drawImage(img, 0, 0, width, height, null);
    }
    finally
    {
        g2d.dispose();
    }
    return newImage;
  }
  
  public static BufferedImage rescaleX(double scaling, BufferedImage input)
  {
    AffineTransform tx = AffineTransform.getScaleInstance(1 - scaling, 1);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    BufferedImage finalImg = op.filter(input, null);
    tx = AffineTransform.getTranslateInstance(input.getWidth()*scaling*0.5, 0);
    op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return op.filter(finalImg, null);
  }
}
