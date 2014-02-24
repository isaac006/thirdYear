/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piquet;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author isaac006
 */

public class Card implements Comparable<Card>, Cloneable
{
  // descrition of a card in general
  public static enum Suit{Clubs, Diamonds, Spades, Hearts}
  public static enum Rank{Seven, Eight, Nine, Ten, Jack, Queen, King, Ace}
  
  // details of this card
  private final Suit suit;
  private final Rank rank;
  private final boolean isFaceCard;
  
  // gui related
  private BufferedImage image, // image at original dimensions for rescaling
                        scaledImage, // scaled to fit the window
                        finalImage; // the image that is displayed on screen
  private Point point = new Point(), // the point this card want to be in
                current = new Point(); // this is the current point the card is drawn at
  private String imagePath = "cardImages/normal/";
  private boolean moving = false, // turn when card is moving in an animation
                  flipped = true, // true means the back is showing
                  isDealt = false; // true if the card has been dealt and is not moving
  private Card back; // the back side of this card
  private float flipCounter = 0.0f; // to keep track of the flip progress
  
  // for sorting use
  private static enum SortMode
  {
    SuitThenRank,     // sort by suit, if suit is the same then sort by rank, the default sort mode
    RankThenSuit,     // sort by rank, if rank is same then sort by suit
    RankThenSuitValue // sort by rank, if rank is same then sort by the value of Rules.seperateBySuit ( toSort )[ suit ]
                      // if still the same then sort by the suit count
  }
  private static SortMode sortMode = SortMode.SuitThenRank;
  private static Card[][] seperatedBySuit; // used when in RankThenSuitCount mode
  
  // for the computer player
  private int index = 0;
  
  // for the back face card which has no value
  public Card(String path)
  {
    suit = null;
    rank = null;
    this.back = this; // possible recursive loop here
    isFaceCard = false;
    try {
      image = ImageIO.read(new File(imagePath + path));
    } catch (IOException ex) {
      Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
    }
    scaledImage = image;
    finalImage = scaledImage;
  }
  
  // for normal cards
  public Card(Suit suit, Rank rank, Card back)
  {
    this.suit = suit;
    this.rank = rank;
    this.back = back;
    // this card is a face card if the rank is Jack, Queen, King or Ace
    isFaceCard = (rank.compareTo(Rank.Ten) > 0)?true:false;
    try {
      image = ImageIO.read(new File(imagePath + toString().toLowerCase().replace(' ', '_') + ".png"));
    } catch (IOException ex) {
      Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
    }
    scaledImage = image;
    finalImage = back.scaledImage;
  }
  
  public Card(Card toClone)
  {
    suit        = toClone.suit;
    rank        = toClone.rank;
    isFaceCard  = toClone.isFaceCard;
    image       = toClone.image;
    scaledImage = toClone.scaledImage;
    finalImage  = toClone.finalImage;
    point       = new Point(toClone.point);
    current     = new Point(toClone.current);
    imagePath   = toClone.imagePath;
    moving      = toClone.moving;
    flipped     = toClone.flipped;
    isDealt     = toClone.isDealt;
    back        = toClone.back;
    flipCounter = toClone.flipCounter;
  }
  
  // getters
  public BufferedImage getFrontImage() { return scaledImage; }
  public BufferedImage getBackImage()  { return back.scaledImage; }
  public BufferedImage getImage()      {  return finalImage; }
  public boolean isFaceCard()          { return isFaceCard; }
  public boolean isMoving()            { return moving; }
  public boolean isDealt()             { return isDealt; }
  public boolean flipped()             { return flipped; }
  public Point getPoint()              { return current; }
  public Card getBack()                { return back; }
  public Suit getSuit()                { return suit; }
  public Rank getRank()                { return rank; }
  public int getIndex()                { return index; }
  
  // showBack is true means we want the back to be shown,
  // if it is already shown nothing needs to be done
  // return true if card is still flipping
  public boolean flip(boolean showBack)
  {
    if (showBack == flipped)
      return moving = false;
    
    BufferedImage frontImage, backImage;
    if (!flipped)
    {
      frontImage = scaledImage;
      backImage = back.scaledImage;
    }
    else
    {
      frontImage = back.scaledImage;
      backImage = scaledImage;
    }
    
    if (flipCounter < 1)
      finalImage = ImageTransform.rescaleX(flipCounter, frontImage);
    else if (flipCounter < 2)
      finalImage = ImageTransform.rescaleX(2-flipCounter, backImage);
    else
      finalImage = flipped?scaledImage:back.scaledImage;
    
//    System.out.println("FLIPPING " + flipCounter);
    
    flipCounter += 0.1;
    if (flipCounter >= 2.3)
    {
      flipped = showBack;
      moving = false;
      flipCounter = 0.0f;
    }
    else moving = true;
    return moving;
  }
  
  // flip without animation
  public void flipQuick(boolean showBack)
  {
    flipped = showBack;
    finalImage = flipped?back.scaledImage:scaledImage;
  }
  
  public void setDealt()
  {
    isDealt = true;
  }
  
  public void setNotDealt()
  {
    isDealt = false;
  }
  
  // return true if animation is still going
  public boolean move()
  {
    if (!current.equals(point))
    {
      double offsetX = (point.x - current.x) * 0.25;
      double offsetY = (point.y - current.y) * 0.25;
      if (Math.abs(offsetX) <= 0.5 && Math.abs(offsetY) <= 0.5)
      {
        current.setLocation(point);
//        System.out.println(toString() + " STOPPED...");
      }
      else
      {
        current.setLocation(current.x + offsetX, current.y + offsetY);
//        System.out.println(toString() + " moving... x: " + offsetX + " y: " + offsetY);
        moving = true;
      }
    }
    else
    {
      current.setLocation(point);
      moving = false;
    }
    return moving;
  }
  
  public void setLocation(Point p)
  {
    point.setLocation(p);
  }
  
  public void setLocation(double x, double y)
  {
    moving = true;
    point.setLocation(x, y);
  }
  
  public void setStartingLocation(double x, double y)
  {
    point.setLocation(x, y);
    current.setLocation(point);
  }
  
  public void setStartingLocation(Point p)
  {
    point.setLocation(p);
    current.setLocation(point);
  }
  
  public void setIndex(int newIndex)
  {
    index = newIndex;
  }
  
  public String suitToStringShort()
  {
    switch(suit)
    {
      case Clubs:    return "♣";
      case Diamonds: return "♦";
      case Hearts:   return "♥";
      case Spades:   return "♠";
      default: throw new IllegalArgumentException("This will never happen");
    }
  }
  
  public String rankToStringShort()
  {
    switch(rank)
    {
      case Seven: return "7";
      case Eight: return "8";
      case Nine:  return "9";
      case Ten:   return "10";
      case Jack:  return "J";
      case Queen: return "Q";
      case King:  return "K";
      case Ace:   return "A";
      default: throw new IllegalArgumentException("This will never happen");
    }
  }
  
  @Override
  public String toString()
  {
    return rank + " of " + suit;
  }
  
  public String toStringShort()
  {
    return rankToStringShort() + suitToStringShort();
  }
  
  public static Card[] createPiquetDeck(Card back)
  {
    Card deck[] = new Card[32];
    int position = 0;
    for (Suit suit:Suit.values())
      for (Rank rank:Rank.values())
        deck[position++] = new Card(suit, rank, back);
    return deck;
  }
  
  public static void shuffle(Card deck[])
  {
    Random random = new Random();
    for (int i = deck.length-1; i > 0; i--)
    {
      int toSwap = random.nextInt(i+1);
      Card card = deck[toSwap];
      deck[toSwap] = deck[i];
      deck[i] = card;
    }
  }
  
  // randomly picks 2 card from deck, first for player, sencond for computer
  public static Card[] cut(Card deck[])
  {
    Card[] temp = deck;
    Card.shuffle(temp);
    Card[] result = new Card[2];
    result[0] = new Card(temp[0]);
    result[1] = new Card(temp[1]);
    return result;
  }
  
  @Override
  public int compareTo(Card other)
  {
    switch(sortMode)
    {
      case SuitThenRank:
        if (this.suit.equals(other.suit))
          return this.rank.compareTo(other.rank);
        else return this.suit.compareTo(other.suit);
      case RankThenSuit:
        if (this.rank.equals(other.rank))
          return this.suit.compareTo(other.suit);
        else return this.rank.compareTo(other.rank);
      case RankThenSuitValue:
        if (seperatedBySuit == null)
          throw new IllegalArgumentException("Entered RankThenSuitCount Mode but suitCount == null");
        // if the rank is the same
        if (this.rank.equals(other.rank))
        {
          // if the value of their suit is the same, whichever suit has more cards is smaller
          // since reaching the same value with less cards must mean those cards have higher value
          if (getValue(getSuitCountAbove(seperatedBySuit[this.suit.ordinal()], this)) == getValue(getSuitCountAbove(seperatedBySuit[other.suit.ordinal()], other)))
            return getSuitCountAbove(seperatedBySuit[other.suit.ordinal()], other).length - getSuitCountAbove(seperatedBySuit[this.suit.ordinal()], this).length;
          // else compare their values
          else return getValue(getSuitCountAbove(seperatedBySuit[this.suit.ordinal()], this)) - getValue(getSuitCountAbove(seperatedBySuit[other.suit.ordinal()], other));
        }
        else return this.rank.compareTo(other.rank);
      default:
        throw new IllegalArgumentException("SortMode state unknown: " + sortMode);
    }
  }
  
  private static int getValue(Card[] cards)
  {
    int value = 0;
    // 1 for the lowest rank, 8 for the highest
    for (Card card:cards)
      value += card.rank.ordinal() + 1; // 1 added so value will be at least 1 if there is at least 1 card in cards
    return value;
  }
  
  // only return those that has a higher rank
  private static Card[] getSuitCountAbove(Card[] suit, Card above)
  {
    // calculate result size
    int size = 0;
    for (Card card:suit)
      if (card.rank.compareTo(above.rank) > 0)
        size++;
    Card[] result = new Card[size];
    
    // fill up array
    int index = 0;
    for (Card card:suit)
      if (card.rank.compareTo(above.rank) > 0)
        result[index++] = card;
    
    return result;
  }
  
  // sort the array by Rank then SuitValue, returns a new array and keep the old untouched
  public static Card[] sortByRankThenSuitValue(Card[] toSort)
  {
    seperatedBySuit = Rules.seperateBySuit(toSort);
    sortMode = SortMode.RankThenSuitValue;
    Card[] newToSort = toSort.clone();
    Arrays.sort(newToSort);
    return newToSort;
  }
  
  // sort the cards in the default way
  public static void sort(Card[] toSort)
  {
    sortMode = SortMode.SuitThenRank;
    Arrays.sort(toSort);
  }
  
  public int compareRank(Card other)
  {
    return this.rank.compareTo(other.rank);
  }
  
  public void resize(int width, int height)
  {
    // to avoid exception when resizing
    if (width <= 0) width = 2;
    if (height <= 0) height = 2;
    
    scaledImage = ImageTransform.resizeImage(image, width, height);
    finalImage = flipped?back.scaledImage:scaledImage;
  }
  
  public BufferedImage transform(double scalingFactor)
  {
    return ImageTransform.rescaleX(scalingFactor, scaledImage);
  }
  
  public BufferedImage transformBack(double scalingFactor)
  {
    return ImageTransform.rescaleX(scalingFactor, back.scaledImage);
  }
}
