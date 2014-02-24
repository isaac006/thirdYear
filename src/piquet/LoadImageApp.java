/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piquet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author isaac006
 */

public class LoadImageApp extends Component implements ComponentListener, ActionListener
{

    BufferedImage img, back, scaledImg, finalImg, scaledBack, backArray[] = new BufferedImage[50], finalImgArray[] = new BufferedImage[50];
    int offset, counter = 0;
    double scalingFactor = 0.0;
    Timer timer;

  @Override
  public void paint(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;
    
//    rescaleX(scalingFactor);
    BufferedImage toDraw;
    if (counter >= 150) toDraw = rescaleX(0.02*(199-counter), scaledBack);
    else if (counter >= 100) toDraw = rescaleX(0.02*(counter-100), scaledImg);
    else if (counter >= 50) toDraw = rescaleX(0.02*(99-counter), scaledImg);
    else toDraw = rescaleX(0.02*(counter), scaledBack);
    g2d.drawImage(toDraw, 0, 0, null);
  }
  
  public BufferedImage rescaleX(double scaling, BufferedImage input)
  {
    AffineTransform tx = AffineTransform.getScaleInstance(1 - scaling, 1);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    finalImg = op.filter(input, null);
    tx = AffineTransform.getTranslateInstance(input.getWidth()*scaling*0.5, 0);
    op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return finalImg = op.filter(finalImg, null);
  }

  public LoadImageApp()
  {
    f.addComponentListener(this);
    timer = new Timer(5, this);
    timer.setInitialDelay(1000);
    timer.start();
    
    try
    {
      img = ImageIO.read(new File("cardImages/normal/ace_of_spades.png"));
      back = ImageIO.read(new File("cardImages/normal/back.png"));
    }
    
    catch (IOException e) {}
    for (int i = 0; i < backArray.length; i++)
    {
      backArray[i] = rescaleX(0.02*i, back);
      finalImgArray[i] = rescaleX(0.02*i, img);
    }
    this.setBackground(Color.red);
  }

  public BufferedImage scaleImage(BufferedImage img, int width, int height)
  {
    int imgWidth = img.getWidth();
    int imgHeight = img.getHeight();
    if (imgWidth*height < imgHeight*width) width = imgWidth*height/imgHeight;
    else height = imgHeight*width/imgWidth;
    
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = newImage.createGraphics();
    
    try
    {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setBackground(new Color(0,0,0,0));
      g.drawImage(img, 0, 0, width, height, null);
    }
    finally
    {
        g.dispose();
    }
    return newImage;
}

    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(100,100);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
       }
    }
  public static JFrame f = new JFrame("Load Image Sample");
  public static void main(String[] args) {

    f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    f.getContentPane().setBackground(Color.red);
    f.add(new LoadImageApp());
    f.pack();
    f.setVisible(true);
    
  }

  @Override
  public void componentResized(ComponentEvent e)
  {
    scaledImg = scaleImage(img, f.getContentPane().getWidth(), f.getContentPane().getHeight());
    scaledBack = scaleImage(back, f.getContentPane().getWidth(), f.getContentPane().getHeight());
    for (int i = 0; i < backArray.length; i++)
    {
      backArray[i] = rescaleX(0.02*i, scaledBack);
      finalImgArray[i] = rescaleX(0.02*i, scaledImg);
    }
    repaint();
  }
  @Override
  public void componentMoved(ComponentEvent e) {}
  @Override
  public void componentShown(ComponentEvent e) {}
  @Override
  public void componentHidden(ComponentEvent e) {}

  private void swapSide()
  {
    if (smaller)
      scaledImg = scaleImage(back, f.getContentPane().getWidth(), f.getContentPane().getHeight());
    else
      scaledImg = scaleImage(img, f.getContentPane().getWidth(), f.getContentPane().getHeight());
    smaller = !smaller;
  }
  
  boolean smaller = true;
  @Override
  public void actionPerformed(ActionEvent e) {
//    if (smaller)
//      if (scalingFactor < 0.98)
//        scalingFactor += 0.02;
//      else swapSide();
//    else
//      if (scalingFactor > 0.02)
//        scalingFactor -= 0.02;
//      else swapSide();
    counter = (counter+1)%200;
    repaint();
  }
}