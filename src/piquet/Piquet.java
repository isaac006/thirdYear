/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package piquet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Timer;

/**
 *
 * @author mbax9yc4
 */
public class Piquet extends Component implements MouseListener, MouseMotionListener, ComponentListener, ActionListener
{
  
  //GUI related objects
  private static int             cardWidth,
                                 cardHeight,
                                 mouseOverHuman  = -1,
                                 temp,
                                 talonX, talonY, // coordinate for the talon
                                 flipCounter     = 0, // for the flip animation
                                 fontSize        = 56;
  private static boolean         hintNeeded      = false,
                                 showTalon       = false;
  private static Point           computerCard    = new Point(),
                                 humanCard       = new Point();
  private static String          currentFont     = "AirCut",
                                 fonts[];
  private static Font            font;
  private static JFrame          gui             = new JFrame("Piquet");
  private static JMenuBar        menuBar         = new JMenuBar();
  private static JMenu           gameMenu        = new JMenu("Game"),
                                 settings        = new JMenu("Settings"),
                                 fontMenu        = new JMenu();
  private static JMenuItem       newGame         = new JMenuItem("Start a new game"),
                                 repaint         = new JMenuItem("Manual Repaint"), // for debugging
                                 nextState       = new JMenuItem("Go to next State"), // for debgging
                                 quit            = new JMenuItem("Quit");
  private static Cursor          defaultC        = new Cursor(Cursor.DEFAULT_CURSOR),
                                 handC           = new Cursor(Cursor.HAND_CURSOR);
  private static Color           backgroundColor = new Color(0,99,0);
  private static BufferedImage   backgroundImage;
  private static Card            back            = new Card("back.png");
  private static List<JMenuItem> fontItems       = new ArrayList<>();
  private static Timer           timer;
  private static MenuScroller    menuScroller;
  private static Piquet          piquetGui;
  
  // animation states
  private static enum CBState // for Carte Blanche
  {
    ShowMessage,  // allow time for player to read the Carte Blanche message and get ready
    ShowHand,    // show Carte Blanche hand to opponent
    Looking,     // opponent is looking at hand to check
    HideHand;    // hide hand from opponent
    
    // method to cycle through states
    private CBState next()
    {
      return values()[(ordinal()+1)%values().length];
    }
  }
  private static CBState cbState = CBState.ShowMessage;
  
  // objects used throughout the program
  private static Game game;

  public Piquet()
  {
    setUpMenuBar();
    timer = new Timer(5000, this);
    addMouseListener(this);
    addMouseMotionListener(this);
    addComponentListener(this);
  }
  
  public static void main(String[] args)
  {    
    // load background image
    try {
      backgroundImage = ImageIO.read(new File("felt_01.png"));
    } catch (IOException ex) {
      Logger.getLogger(Piquet.class.getName()).log(Level.SEVERE, null, ex);
    } 
    
    fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    game = new Game(back);
    piquetGui = new Piquet();
    gui.getContentPane().setBackground(backgroundColor);
    gui.add(piquetGui);
    gui.setJMenuBar(menuBar);
    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gui.pack();
    gui.setMinimumSize(new Dimension(320, 240));
    
    centerWindow(gui);
    gui.setVisible(true);
  }
  
  private void setUpNewGame()
  {
    hintNeeded = false; // reset hints
    showTalon = false; // reset talon
    cbState = CBState.ShowMessage; // reset cbState
    if (timer.isRunning()) timer.stop(); // stop any running timers

    // reset the card's starting location
    talonX = (getWidth() - cardWidth * 11) / 2;
    talonY = (getHeight() - cardHeight) / 2;
    for (Card card:game.getDeck())
      card.setStartingLocation(talonX, talonY);

    // new game
    game.newGame();
//    flipComputerHandQuick(false);
//    flipTalonQuick(false);
    // reset all elements
    updateElements();
  }
  
  private void setUpMenuBar()
  {
    fontMenu.setText("Fonts (Current: " + currentFont + ")");
    
    for (int i = 0; i < fonts.length; i++)
    {
      fontItems.add(new JMenuItem(fonts[i]));
      fontItems.get(i).addActionListener(this);
      fontItems.get(i).addMouseListener(this);
      fontMenu.add(fontItems.get(i));
    }
    
    menuScroller = new MenuScroller(fontMenu, 30, 15);
    
    newGame.addActionListener(this);
    repaint.addActionListener(this);
    nextState.addActionListener(this);
    quit.addActionListener(this);
    
    gameMenu.add(newGame);
    gameMenu.addSeparator();
    gameMenu.add(repaint);
    gameMenu.add(nextState);
    gameMenu.addSeparator();
    gameMenu.add(quit);
    
    settings.add(fontMenu);
    
    menuBar.add(gameMenu);
    menuBar.add(settings);
  }
  
  @Override
  public void paint(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;
    
    // set background
    g2d.drawImage(backgroundImage, 0, 0, null);
    
    // if moving = true, it means animations are still going therefore need repaint
    boolean moving = false;
    
    // draw the hands if the cards are dealt
    if (game.cardsDealt()) 
      if (drawHands(g2d))
        moving = true;
    
    // draw the swapped cards if cards are swapped
    if (game.humanSwapped())
    {
      updateHumanSwappedPos();
      if (flipMove1By1(g2d, game.getHumanSwappedList(), false))
        moving = true;
    }
    if (game.computerSwapped())
    {
      updateComputerSwappedPos();
      if (flipMove1By1(g2d, game.getComputerSwappedList(), true))
        moving = true;
    }
    
    // draw the talon id the game has started
    if (game.isPlaying())
    {
      drawScore(g2d);
      if (drawTalon(g2d))
        moving = true;
    }
    
    switch(game.getState())
    {
      case Cut:
        // for the human's card
        updateHumanCardPos();
        game.getCutResult(true).setLocation(humanCard);
        if (game.getCutResult(true).move() || game.getCutResult(true).flip(false))
          moving = true;
        else game.getCutResult(true).setDealt();
        drawCard(g2d, game.getCutResult(true));
        
        // for the computer's card
        updateComputerCardPos();
        game.getCutResult(false).setLocation(computerCard);
        if (game.getCutResult(false).move() || game.getCutResult(false).flip(false))
          moving = true;
        else game.getCutResult(false).setDealt();
        drawCard(g2d, game.getCutResult(false));
        
        // if animation is complete draw hints
        if (game.getCutResult(false).isDealt() && game.getCutResult(true).isDealt())
        {
          drawHint(g2d, game.isCutEqual()?"Cut Equal":"AI: " + (game.isHumanElder()?"Younger":"Elder"), 0);
          drawHint(g2d, game.isCutEqual()?"Cut Equal":"You: " + (game.isHumanElder()?"Elder":"Younger"), getHeight() - cardHeight);
          // set up timer for hints as to what to click to continue
          if (!hintNeeded)
          {
            timer = new Timer(5000, this);
            timer.start();
          }
          else
            drawHint(g2d, "Please click the talon to continue", (getHeight()-cardHeight)/2);
        }
        break;
        
      case ReadyDeal:
        // player card
        game.getCutResult(true).setNotDealt();
        game.getCutResult(true).setLocation(game.getTalon()[0].getPoint());
        if (game.getCutResult(true).flip(true) || game.getCutResult(true).move())
          moving = true;
        drawCard(g2d, game.getCutResult(true)); 
        
        // computer card
        game.getCutResult(false).setNotDealt();
        game.getCutResult(false).setLocation(game.getTalon()[0].getPoint());
        if (game.getCutResult(false).flip(true) || game.getCutResult(false).move())
          moving = true;
        drawCard(g2d, game.getCutResult(false));
        if (!moving)
        {
          game.nextState();
          repaint();
        }
        break;
        
      case Deal:
        // animate the cards and flip them
        if (dealHands(g2d) || flipHumanHand(false)) moving = true;
        
        // sort player's cards
        if (!moving) // if cards dealt
        {
          if (!game.isHumanHandSorted()) // if cards not sorted
            game.sortHumanHand(); // sort the cards
          else game.nextState(); // else ready for next stage
          repaint();
        }
        break;
          
      case ElderCarteBlanche:
      case YoungerCarteBlanche:
        boolean isElderCarteBlanche = game.getState() == Game.State.ElderCarteBlanche;
        if (Rules.isCarteBlanche(isElderCarteBlanche?game.getElderHand():game.getYoungerHand()))
        {
          if (game.isHumanElder() ^ !isElderCarteBlanche) // xor
            // if human is elder and elder has CarteBlanche
            switch(cbState)
            {
              case ShowMessage:
                drawHint(g2d, "Carte Blanche", getHeight()-cardHeight*2);
                timer = new Timer(1000, this);
                timer.start();
                break;
                
              case ShowHand:
                drawHint(g2d, "The AI will take a look", getHeight()-cardHeight*2);
                timer = new Timer(1000, this);
                timer.start();
                break;
                
              case Looking:
                drawHint(g2d, "It saw your cards", getHeight()-cardHeight*2);
                timer = new Timer(1000, this);
                timer.start();
                break;
                
              case HideHand:
                cbState = cbState.next(); // loop back to first state to reset
                game.nextState();
                repaint();
                break;
            }
          else
          {
            switch(cbState)
            {
              case ShowMessage:
                drawHint(g2d, "Carte Blanche", cardHeight);
                timer = new Timer(1000, this);
                timer.start();
                break;
                
              case ShowHand:
                if (flipComputerHand(false))
                {
                  drawHint(g2d, "Showing to prove...", cardHeight);
                  moving = true;
                }
                else
                {
                  cbState = cbState.next();
                  repaint();
                }
                break;
                
              case Looking:
                drawHint(g2d, "Please Check...", cardHeight);
                timer.start();
                break;
                
              case HideHand:
                if (flipComputerHand(true))
                {
                  drawHint(g2d, "OK, that's enough time...", cardHeight);
                  moving = true;
                }
                else
                {
                  cbState = cbState.next(); // loop back to first state to reset
                  game.nextState();
                  repaint();
                }
                break;
            }
          }
        }
        break;
        
      case ElderSelectSwap:
      case YoungerSelectSwap:
        // display a nessage for the human player
        if (game.isHumanSelectSwap()) // if human is swapping
          drawHint(g2d, "You can swap " + (game.humanMustSwap() ?  1 : 0) + " to " + (game.getTalon().length > 5 ? 5 : game.getTalon().length) + " cards", getHeight()-cardHeight*2);
        break;
        
      case ElderSwapTo:
      case YoungerSwapTo:
        boolean isElderSwapTo = game.getState() == Game.State.ElderSwapTo;
        if (!isElderSwapTo ^ game.isHumanElder()) // if human is swapping
        {
          drawHint(g2d, "You swapped " + game.getHumanSwappedList().size() + " cards", getHeight() - cardHeight * 2);
          updateHumanSwappedPos();
          if (move1By1(g2d, game.getHumanSwappedList())) moving = true;
        }
        else
        {
          drawHint(g2d, "AI swaps " + game.getComputerSwappedList().size() + " cards", cardHeight);
          updateComputerSwappedPos();
          if (move1By1(g2d, game.getComputerSwappedList())) moving = true;
        }
        
        if (!moving)
        {
          game.nextState();
          repaint();
        }
        break;
        
      case ElderSwapFrom:
      case YoungerSwapFrom:
        boolean isElderSwapFrom = game.getState() == Game.State.ElderSwapFrom;
        if (!isElderSwapFrom ^ game.isHumanElder()) // if human is swapping
        {
          drawHint(g2d, "You swapped " + game.getHumanSwappedList().size() + " cards", getHeight() - cardHeight * 2);
          updateHumanSwappedPos();
          if (move1By1(g2d, game.getHumanSwappedList())) moving = true;
          if (move1By1(g2d, game.getHumanHand())) moving = true;
          else if (flipAll(g2d, game.getHumanHand(), false)) moving = true;
          if (!moving)
          {
            game.sortHumanHand();
            repaint();
          }
        }
        else
        {
          drawHint(g2d, "AI swaps " + game.getComputerSwappedList().size() + " cards", cardHeight);
          updateComputerSwappedPos();
          if (flipMove1By1(g2d, game.getComputerSwappedList(), true)) moving = true;
          if (moveFlip1By1(g2d, game.getComputerHand(), true)) moving = true;
          else 
          {
            game.getComputer().sortMyHand();
            updateComputerHandPosQuick();
          }
        }
        
        if (!moving)
        {
          game.nextState();
          repaint();
        }
        break;
        
      case ElderPeep:
      case YoungerPeep:
        boolean isElderPeep = game.getState() == Game.State.ElderPeep;
        if (game.isHumanElder() ^ !isElderPeep) // human peeps
        {
          drawHint(g2d, "Mouse over talon to peep", cardHeight);
          drawHint(g2d, "Click anywhere to continue", getHeight()-cardHeight*2);
        }
        
        break;
        
    }
    
    // if the animation is not over
    if (moving)
    {
//      trySleep(10);
      repaint();
    }
  }
  
  private static void trySleep(int delay)
  {
    System.out.println("Sleeping " + delay + " ms...");
    try {
      Thread.sleep(delay);
    } catch (InterruptedException ex) {
      Logger.getLogger(Piquet.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  // draw a string with a white bubble in the centre of the window
  private void drawHint(Graphics2D g2d, String message, int y)
  {
    int stringWidth = getFontMetrics(font).stringWidth(message);
    int x = (getWidth() - stringWidth - cardWidth)/2;
    y += (int)(cardHeight*0.25);
    g2d.setColor(new Color(1f, 1f, 1f, 0.5f));
    g2d.fillRoundRect(x, y, stringWidth+cardWidth, (int)(cardHeight*0.5), cardWidth, cardWidth);
    g2d.setColor(Color.BLACK);
    g2d.setFont(font);
    g2d.drawString(message, x+(cardWidth/2), y+((int)(cardHeight*0.35)));
  }
  
  private void drawScore(Graphics2D g2d)
  {
    int xPos = (int) ((getWidth() - cardWidth * 12) / 2 + cardWidth * 0.25);
    int offset = ((getHeight() - cardHeight) / 2 - cardHeight) / 2;
    int humanYPos = (getHeight() + cardHeight) / 2 + offset + getFontMetrics(font).getAscent() / 2;
    int computerYPos = cardHeight + offset + getFontMetrics(font).getAscent() / 2;
    
    g2d.setColor(Color.BLACK);
    g2d.setFont(font);
    g2d.drawString("You: " + game.getHumanScore(), xPos, humanYPos);
    g2d.drawString("AI: " + game.getCopmuterScore(), xPos, computerYPos);
  }
  
  private boolean drawTalon(Graphics2D g2d)
  {
    boolean result = false;
    updateTalonPos();
    for (int i = game.getTalon().length-1; i >= 0; i--)
    {
      drawCard(g2d, game.getTalon()[i]);
      if (game.getTalon()[i].move()) result = true;
    }
    return result;
  }
  
  private boolean drawHands(Graphics2D g2d)
  {
    boolean result = false;
    // draw human hand
    updateHumanHandPos();
    for (int i = 0; i < game.getHumanHand().length; i++)
    {
      // if human swap and the card to draw is not in the swap list
      if (!game.isHumanSwap() || game.getHumanToSwapList().indexOf(i) == -1)
      {
        drawCard(g2d, game.getHumanHand()[i]);
        if (game.getHumanHand()[i].move()) result = true;
      }
    }
    
    // draw computer hand
    updateComputerHandPos();
    for (int i = 0; i < game.getComputerHand().length; i++)
    {
      // if computer swap and the card to draw is not in the swap list
      if (!game.isComputerSwap() || game.getComputerToSwapList().indexOf(i) == -1)
      {
        drawCard(g2d, game.getComputerHand()[i]);
        if (game.getComputerHand()[i].move()) result = true;
      }
    }
    
    return result;
  }
  
  // this method assume the pos of the list of cards has been updated
  private boolean flipMove1By1(Graphics2D g2d, List<Card> list, boolean showBack)
  {
    boolean result = false;
    for (int i = 0; i < list.size(); i++)
      drawCard(g2d, list.get(i));
    
    for (int i = 0; i < list.size(); i++)
      if (list.get(i).flip(showBack) || list.get(i).move())
      {
        result = true;
        if (!list.get(i).isDealt()) break;
      }
      else list.get(i).setDealt();
    
    return result;
  }
  
  private boolean flipMove1By1(Graphics2D g2d, Card[] array, boolean showBack)
  {
    return flipMove1By1(g2d, Arrays.asList(array), showBack);
  }
  
  private boolean moveFlip1By1(Graphics2D g2d, List<Card> list, boolean showBack)
  {
    boolean result = false;
    for (int i = 0; i < list.size(); i++)
      drawCard(g2d, list.get(i));
    
    for (int i = 0; i < list.size(); i++)
      if (list.get(i).move() || list.get(i).flip(showBack))
      {
        result = true;
        if (!list.get(i).isDealt()) break;
      }
      else list.get(i).setDealt();
    
    return result;
  }
  
  private boolean moveFlip1By1(Graphics2D g2d, Card[] array, boolean showBack)
  {
    return moveFlip1By1(g2d, Arrays.asList(array), showBack);
  }
  
  private boolean move1By1(Graphics2D g2d, List<Card> list)
  {
    boolean result = false;
    for (int i = 0; i < list.size(); i++)
      drawCard(g2d, list.get(i));
    
    for (int i = 0; i < list.size(); i++)
      if (list.get(i).move())
      {
        result = true;
        if (!list.get(i).isDealt()) break;
      }
      else list.get(i).setDealt();
    
    return result;
  }
  
  private boolean move1By1(Graphics2D g2d, Card[] array)
  {
    return move1By1(g2d, Arrays.asList(array));
  }
  
  private boolean flipAll(Graphics2D g2d, List<Card> list, boolean showBack)
  {
    boolean result = false;
    for (int i = 0; i < list.size(); i++)
      drawCard(g2d, list.get(i));
    
    for (int i = 0; i < list.size(); i++)
      if (list.get(i).flip(showBack))
        result = true;
      else list.get(i).setDealt();
    
    return result;
  }
  
  private boolean flipAll(Graphics2D g2d, Card[] array, boolean showBack)
  {
    return flipAll(g2d, Arrays.asList(array), showBack);
  }
  
  private boolean dealHands(Graphics2D g2d)
  {
    boolean result = false;
    
    // create a new array in dealing order
    Card[] ordered = new Card[24];
    for (int i = 0; i < 12; i+=2)
    {
      ordered[(i*2)]   = game.getElderHand()[i];
      ordered[(i*2)+1] = game.getElderHand()[i+1];
      ordered[(i*2)+2] = game.getYoungerHand()[i];
      ordered[(i*2)+3] = game.getYoungerHand()[i+1];
    }
    
    // deal both hands
    updateHumanHandPos();
    updateComputerHandPos();
    for (int i = 0; i < ordered.length; i+=2)
    {
      // animate first card
      drawCard(g2d, ordered[i]);
      if (ordered[i].move()) 
        result = true;
      else ordered[i].setDealt();
      
      // animate second card
      drawCard(g2d, ordered[i+1]);
      if (ordered[i+1].move()) 
        result = true;
      else ordered[i+1].setDealt();
      
      // break if those cards are still in motion
      if (result && !ordered[i].isDealt() && !ordered[i+1].isDealt()) break;
    }
    
    return result;
  }
  
  private boolean flipHumanHand(boolean showBack)
  {
    boolean result = false;
    for (int i = 0; i < game.getHumanHand().length; i++)
      if (game.getHumanHand()[i].flip(showBack))
        result = true;
    return result;
  }
  
  private void flipHumanHandQuick(boolean showBack)
  {
    for (int i = 0; i < game.getHumanHand().length; i++)
      game.getHumanHand()[i].flipQuick(showBack);
  }
  
  private boolean humanHandFlipped(boolean showBack)
  {
    for (int i = 0; i < game.getHumanHand().length; i++)
      if (game.getHumanHand()[i].flipped() != showBack)
        return false;
    return true;
  }
  
  private boolean flipComputerHand(boolean showBack)
  {
    boolean result = false;
    for (int i = 0; i < game.getComputerHand().length; i++)
      if (game.getComputerHand()[i].flip(showBack))
        result = true;
    return result;
  }
  
  private void flipComputerHandQuick(boolean showBack)
  {
    for (int i = 0; i < game.getComputerHand().length; i++)
      game.getComputerHand()[i].flipQuick(showBack);
  }
  
  private boolean computerHandFlipped(boolean showBack)
  {
    for (int i = 0; i < game.getComputerHand().length; i++)
      if (game.getComputerHand()[i].flipped() != showBack)
        return false;
    return true;
  }
  
  private boolean flipTalon(boolean showBack)
  {
    boolean result = false;
    for (int i = 0; i < game.getTalon().length; i++)
      if (game.getTalon()[i].flip(showBack))
        result = true;
    return result;
  }
  
  private void flipTalonQuick(boolean showBack)
  {
    for (int i = 0; i < game.getTalon().length; i++)
      game.getTalon()[i].flipQuick(showBack);
  }
  
  private boolean talonFlipped(boolean showBack)
  {
    for (int i = 0; i < game.getTalon().length; i++)
      if (game.getTalon()[i].flipped() != showBack)
        return false;
    return true;
  }
  
  private void drawCard(Graphics2D g2d, Card card)
  {
    g2d.drawImage(card.getImage(), card.getPoint().x, card.getPoint().y, null);
  }
  
  private void updateElements()
  {
    updateComputerCardPos();
    updateHumanCardPos();
    updateHumanHandPos();
    updateComputerHandPos();
    updateTalonPos();
  }
  
  private void updateCardDimension()
  {
    // calculate card size
    // default card width is 1/12 of the window width, so a full set will fit on one row on screen
    cardWidth = getWidth()/12;
    cardHeight = back.getImage().getHeight()*cardWidth/back.getImage().getWidth();
    
    // need to allow space for other parts of the game therefore height in between
    // must be at least 2.5 times cardHeight (which means cardHeight * 4.5 = height of window)
    if (cardHeight > getHeight()/4.5)
    {
      cardHeight = (int)(getHeight()/4.5);
      cardWidth = back.getImage().getWidth()*cardHeight/back.getImage().getHeight();
    }
    
    // update card size
    back.resize(cardWidth, cardHeight);
    for (Card card:game.getDeck())
      card.resize(cardWidth, cardHeight);
    // update clones too if game has started
    if (game.isPlaying()) // otherwise might cause null pointer exception as we havent cut yet
      for (Card card:game.getCutResult())
        card.resize(cardWidth, cardHeight);
  }
  
  private void updateHumanHandPos()
  {
    // offset for the start of the hand, since the hand is centered on screen
    // offset = the number of pixels on the left of the first card
    int offset = (getWidth() - cardWidth * game.getHumanHand().length) / 2;
    for (int i = 0; i < game.getHumanHand().length; i++)
    {
      boolean raised = mouseOverHuman == i || game.isHumanSelectSwap() && (game.getHumanToSwapList().indexOf(i) != -1);
      // since human hand is located at the bottom of the screen the default y coordinate is height of window - cardHeght
      // if user has mouse over that card it is raised 10% of its cardHeight
      game.getHumanHand()[i].setLocation(offset + cardWidth * i, getHeight() - cardHeight - (raised ? (int)(cardHeight*0.1):0));
    }
  }
  
  private void updateHumanSwappedPos()
  {
    int swappedX = (getWidth() - cardWidth * 8) / 2;
    int swappedY = getHeight()-(int)(cardHeight*2.2);
    for (int i = 0; i < game.getHumanSwappedList().size(); i++)
      ((Card)game.getHumanSwappedList().get(i)).setLocation(swappedX + cardWidth * i * 0.2, swappedY);
  }
  
  private void updateHumanCardPos()
  {
    // y coor = center of screen - half of cardWidth
    humanCard.setLocation((getWidth() - cardWidth)/2, getHeight()-(int)(cardHeight*2.2));
  }
  
  private void updateComputerHandPos()
  {
    // same reason for offset as in updateHumanHand()
    int offset = (getWidth() - cardWidth * game.getComputerHand().length) / 2;
    for (int i = 0; i < game.getComputerHand().length; i++)
      // computer hand is at the top therefore y coordinate is 0
      game.getComputerHand()[i].setLocation(offset + cardWidth * i, 0);
  }
  
  private void updateComputerHandPosQuick()
  {
    System.out.println("updateComputerHandPosQuick");
    // same reason for offset as in updateHumanHand()
    int offset = (getWidth() - cardWidth * game.getComputerHand().length) / 2;
    for (int i = 0; i < game.getComputerHand().length; i++)
      // computer hand is at the top therefore y coordinate is 0
      game.getComputerHand()[i].setStartingLocation(offset + cardWidth * i, 0);
  }
  
  private void updateComputerSwappedPos()
  {
    int swappedX = (getWidth() - cardWidth * 8) / 2;
    if (game.userCanClickTalon() && showTalon)
      swappedX += (game.getTalon().length - 1) * cardWidth * 0.45;
    int swappedY = (int) (cardHeight*1.2);
    for (int i = 0; i < game.getComputerSwappedList().size(); i++)
      ((Card)game.getComputerSwappedList().get(i)).setLocation(swappedX + cardWidth * i * 0.2, swappedY);
  }
  
  private void updateComputerCardPos()
  {
    computerCard.setLocation((getWidth()-cardWidth)/2, cardHeight*1.2);
  }
  
  private void updateTalonPos()
  {
    talonX = (getWidth() - cardWidth * 11) / 2;
    talonY = (getHeight() - cardHeight) / 2;
    if(showTalon)
      for (int i = 0; i < game.getTalon().length; i++)
        game.getTalon()[game.getTalon().length-1-i].setLocation(talonX + cardWidth * i * 0.5, talonY);
    else if (game.userCanClickTalon())
      for (int i = 0; i < game.getTalon().length; i++)
        game.getTalon()[game.getTalon().length-1-i].setLocation(talonX + cardWidth * i * 0.05, talonY);
    else
      for (int i = 0; i < game.getTalon().length; i++)
        game.getTalon()[game.getTalon().length-1-i].setLocation(talonX, talonY);
  }
  
  // update font size based on cardHeight
  // we want the font size to be 60% of cardHeight
  private void updateFontSize()
  {
    int target = (int) (cardHeight * 0.3);
    // the difference we are going to try every time
    int offset = 1;
    // a margin of error in case the target pixel cannot be reached, and to save the data to detect when this happens
    int margin = 1, last1 = 0, last2 = 0;
    // true if the font size went pass the target
    boolean targetPassed = false;
    // when this boolean flag is flipped we know we went pass the target
    font = new Font(currentFont, Font.PLAIN, fontSize);
    boolean lastRoundTooLarge = getFontMetrics(font).getAscent() > target;
//    System.out.println(target);
    // cannot set font size based on number of pixels, need to loop through each font size
    while (Math.abs(getFontMetrics(font).getAscent() - target) > margin) // allow a small margin of error
    {
      font = new Font(currentFont, Font.PLAIN, fontSize);
      if (getFontMetrics(font).getAscent() > target) // font too large
      {
//        System.out.println("Font too large " + fontSize + " " + getFontMetrics(font).getAscent());
        fontSize -= offset;
        if (!lastRoundTooLarge) targetPassed = true;
        lastRoundTooLarge = true;
      }
      else
      {
//        System.out.println("Font too small " + fontSize + " " + getFontMetrics(font).getAscent());
        fontSize += offset;
        if (lastRoundTooLarge) targetPassed = true;
        lastRoundTooLarge = false;
      }
      // allow binary tree search once target is passed, else increase offset to reach / pass target faster
      if (!targetPassed) offset *= 2;
      else if (offset > 1) offset /= 2;
      
      // check if target cannot be reached
      if (last2 == fontSize)
      {
        margin++;
//        System.out.println("New margin " + margin);
      }
      
      // save font size forchecking
      last2 = last1;
      last1 = fontSize;
    }
//    System.out.println("Final size " + fontSize + " " + getFontMetrics(font).getAscent());
  }
  
  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(1280,768);
  }
  
  private static void centerWindow(JFrame window)
  {
    // Get the size of the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
 
    // Determine the new location of the window
    int w = window.getSize().width;
    int h = window.getSize().height;
    int x = (dim.width-w)/2;
    int y = (dim.height-h)/2;
 
    // Move the window
    window.setLocation(x, y);
  }

  // a private method to test if the mouse is over any of the human hand cards,
  // if yes return the index of the card
  // else return -1
  private static int mouseOverHumanHand(int x, int y)
  {
    // if mouse is over any one of the cards
    for (int i = 0; i < game.getHumanHand().length; i++)
        if (x >= game.getHumanHand()[i].getPoint().x && x < game.getHumanHand()[i].getPoint().x + cardWidth &&
            y >= game.getHumanHand()[i].getPoint().y && y < game.getHumanHand()[i].getPoint().y + cardHeight + (mouseOverHuman == i?(int)(cardHeight*0.1):0))
          return i;
    // if not then return -1
    return -1;
  }
  
  // to test if the mouse is over the talon
  private static boolean mouseOverTalon(int x, int y)
  {
    return x >= game.getTalon()[game.getTalon().length-1].getPoint().x && x < game.getTalon()[0].getPoint().x + cardWidth && y >= game.getTalon()[game.getTalon().length-1].getPoint().y && y < game.getTalon()[0].getPoint().y + cardHeight;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) 
  {
    // if game is playing
    if (game.isPlaying())
    {
      if (game.userCanClickHand())
      {
        if (e.getButton() == MouseEvent.BUTTON1)
        // if clicked on humanHand and if so which card
          if ((temp = mouseOverHumanHand(e.getX(), e.getY())) != -1)
            // user clicked this card
            {
              game.userClickedHand(temp);
              mouseOverHuman = -1;
              repaint();
            }
      }
      // if clicked on talon
      if (game.userCanClickTalon() && mouseOverTalon(e.getX(), e.getY()))
      {
        game.nextState();
        if(timer.isRunning()) timer.stop();
        if (showTalon) showTalon = false;
        repaint();
      }

      // any mouse clicks will disable the hint
      if (hintNeeded)
      {
        hintNeeded = false;
        repaint();
      } 
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    Cursor cursor = defaultC;
    // if game has started
    if (game.isPlaying())
    {
      if (game.userCanClickHand())
      {
        // calculate which card the mouse is over
        if ((temp = mouseOverHumanHand(e.getX(), e.getY())) != -1)
        {
          cursor = handC;
//          System.out.println("Mouse over human hand " + temp);
        }
        if (mouseOverHuman != temp)
        {
          mouseOverHuman = temp;
          repaint();
        }
      }
      // if over on talon
      if (game.userCanClickTalon() && mouseOverTalon(e.getX(), e.getY()))
      {
//        System.out.println("Mouse over talon");
        cursor = handC;
        if (hintNeeded) hintNeeded = false; // reset any hint
        if (showTalon == false)
        {
          showTalon = true;
          repaint();
        }
      }
      else if (showTalon == true)
      {
        showTalon = false;
        repaint();
      }
    }
    setCursor(cursor);
  }
  
  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e)
  {
    if (e.getSource().getClass().equals(JMenuItem.class) && fontItems.contains(e.getSource()))
    {
      System.out.println("Mouse entered " + ((JMenuItem)e.getSource()).getText());
      font = new Font(((JMenuItem)e.getSource()).getText(), Font.PLAIN, fontSize);
      repaint();
    }
  }

  @Override
  public void mouseExited(MouseEvent e)
  {
    if (e.getSource().equals(piquetGui))
    {
      if (mouseOverHuman != -1)
      {
        mouseOverHuman = -1;
        repaint();
      }
    }
    else if (e.getSource().getClass().equals(JMenuItem.class) && fontItems.contains(e.getSource()))
    {
      System.out.println("Mouse exited " + ((JMenuItem)e.getSource()).getText());
      font = new Font(currentFont, Font.PLAIN, fontSize);
      repaint();
    }
  }

  @Override
  public void componentResized(ComponentEvent e)
  {
    updateCardDimension();
    updateFontSize();
    if (game.isPlaying())
      updateElements();
    
    talonX = (getWidth() - cardWidth * 11) / 2;
    talonY = (getHeight() - cardHeight) / 2;
    // only update those that hasnt been dealt and not in the talon
    if (!game.isPlaying())
    {
      for (Card card:game.getDeck())
        if (!card.isDealt())
          card.setStartingLocation(talonX, talonY);
    }
    else
    {
      for (Card card:game.getComputerHand())
        if (!card.isDealt())
          card.setStartingLocation(talonX, talonY);
      for (Card card:game.getHumanHand())
        if (!card.isDealt())
          card.setStartingLocation(talonX, talonY);
      
      // if in sort stage
      if (game.isHumanSwap())
        updateTalonPos();
    }
    repaint();
  }

  @Override
  public void componentMoved(ComponentEvent e) {}

  @Override
  public void componentShown(ComponentEvent e) {}

  @Override
  public void componentHidden(ComponentEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {}

  
//  int counter = 0;
//  @Override
//  public void repaint()
//  {
//    super.repaint();
//    System.out.println("REPINTED " + counter++);
//  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == quit)
      System.exit(0);
    // else if the source is a timer
    else if (e.getSource().getClass() == Timer.class)
    {
      // if this is not the timer I created (work around for a bug)
      if (e.getSource() != timer)
      {
        System.err.println("Unexpected timer ActionEvent: " + e.getSource() + ", STOPPING...");
        Timer unexpectedTimer = (Timer)e.getSource();
        unexpectedTimer.stop(); // stop this timer
        return; // stop here
      }
      // if in a state where hints are allowed
      if (game.hintsAllowed())
        hintNeeded = true;
      else if (game.inCarteBlanche())
        cbState = cbState.next();
      // all timer events will stop the timer and trigger a repaint
      timer.stop();
    }
    else if (e.getSource() == newGame)
    {
      setUpNewGame();
    }
    else if (e.getSource() == nextState)
    {
      if (game.isPlaying())
        game.nextState();
    }
    else if (e.getSource().getClass().equals(JMenuItem.class) && fontItems.contains(e.getSource()))
    {
      System.out.println("Clicked on a font");
      currentFont = ((JMenuItem)e.getSource()).getText();
      font = new Font(currentFont, Font.PLAIN, fontSize);
      updateFontSize();
      fontMenu.setText("Fonts (Current: " + currentFont + ")");
      repaint();
    }
    else // have not assigned an action to this event
      System.err.println("ActionEvent: " + e.getSource());
    
    repaint();
  }
}