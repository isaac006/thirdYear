/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package piquet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * all get methods assumes the hand is legal and sorted
 * 
 * @author isaac006
 */
public class Rules
{
  // settings variables
  private int     carteBlanche,
                  lastMoveScore;
  private boolean elderMustSwap,
                  youngerMustSwap;
  
  // getters
  public int getCarteBlancheScore() { return carteBlanche; }
  public int getLastMoveScore()     { return lastMoveScore; }
  
  public boolean computerMustSwap(boolean humanIsElder)
  {
    return humanIsElder ? youngerMustSwap : elderMustSwap;
  }
  
  public boolean humanMustSwap(boolean humanIsElder)
  {
    return !humanIsElder ? youngerMustSwap : elderMustSwap;
  }
  
  
  // constructor
  public Rules()
  {
    carteBlanche = 10;
    lastMoveScore = 1;
    elderMustSwap = true;
    youngerMustSwap = false;
  }
  
  public static boolean isCarteBlanche(Card hand[])
  {
    for (Card card:hand)
      if (card.isFaceCard())
        return false;
    return true;
  }
  
  // this method counts the number of cards in one suit and stores the value in
  // an array, it has its own method because it is used multiple times in different
  // methods and Computer.java also can use it
  public static int[] getSuitCount(Card hand[])
  {
    int suitCount[] = new int[4];
    for (Card card:hand)
      suitCount[card.getSuit().ordinal()]++;
    return suitCount;
  }
  
  public static Card[][] seperateBySuit(Card hand[])
  {
    ArrayList<ArrayList<Card>> splited = new ArrayList<ArrayList<Card>>();
    Card suitArray[][] = new Card[4][];
    
    for (int i = 0; i < 4; i++)
      splited.add(new ArrayList<Card>());
    
    for (Card card:hand)
      splited.get(card.getSuit().ordinal()).add(card);
    
    for (int i = 0; i < 4; i++)
      splited.get(i).toArray(suitArray[i] = new Card[splited.get(i).size()]);
    
    return suitArray;
  }
  
  /**
   * This method returns an int representing the point in this hand, for example,
   * a 4 means this hand has a point of 4. A 0 is returned if this card has no point.
   * A point means the most card in one suit.
   * 
   * @param hand
   * @return int representing the point in this hand
   */
  public static int getPoint(Card hand[])
  { 
    int suitCount[]  = getSuitCount(hand),
        highestPoint = 3;
    
    for (int i = 0; i < 4; i++)
      if (suitCount[i] > highestPoint)
        highestPoint = suitCount[i];
    
    return highestPoint > 3 ? highestPoint : 0;
  }
  
//  public static int pointResponse(int opponentPoint, Card hand[])
//  {
//    return getPoint(hand) - opponentPoint;
//  }
  
  /**
   * When both players have the same point, the elder must call out the value of
   * his hand and compare with the younger. The values are: ace = 11, face cards = 10
   * and face value for the rest.
   * 
   * @param hand
   * @return the value of the highest suit
   */
  public static int getPointValue(Card hand[])
  {
    int suitCount[]      = getSuitCount(hand),
        suitValue[]      = new int[4],
        highestPointSuit = 0,
        value;
    
    for (Card card:hand)
    {
      if (card.getRank().compareTo(Card.Rank.Jack) < 0)//ordinal() < 4) // i.e. the rank is 7 to 10
        value = card.getRank().ordinal() + 7;
      else if (card.getRank().compareTo(Card.Rank.Ace) < 0) // i.e. the rank is J, Q or K
        value = 10;
      else // i.e. this card is an ace
        value = 11;
      suitValue[card.getSuit().ordinal()] += value;
    }
    
    for (int i = 0; i < 4; i++)
      if (suitValue[i] > suitValue[highestPointSuit])
        highestPointSuit = i;
    
    if (suitCount[highestPointSuit] <= 3) return 0;
    else return suitValue[highestPointSuit];
  }
  
  /**
   * A sequence is a consecutive run of cards longer than 3 and in the same suit.
   * A hand can contain more than 1 sequence therefore an array of ints is returned.
   * 
   * @param hand
   * @return an array of ints representing the number of  consecutive cards in a suit
   * in descending order
   */
  public static int[] getSequences(Card hand[])
  {
    Card allSequences[][] = getAllSequences(hand);
    int allSequencesCount[] = new int[allSequences.length],
        result[] = new int[allSequences.length],
        index = 0;
    
    for(int i = 0; i < allSequences.length; i++)
      allSequencesCount[i] = allSequences[i].length;
    Arrays.sort(allSequencesCount);
    
    for (int i = allSequences.length-1; i >= 0; i--)
      result[index++] = allSequencesCount[i];

    return result;
  }
  
  public static String getSequenceName(int number)
  {
    switch(number)
    {
      case 3: return "Tierce";
      case 4: return "Quarte";
      case 5: return "Quinte";
      case 6: return "Sixième";
      case 7: return "Septième";
      case 8: return "Huitième";
      default: throw new IllegalArgumentException("Wrong number for getSequenceName("+number+")");
    }
  }
  
  public static Card[][] getAllSequences(Card hand[])
  {
    int consecutiveCount;
    Card suitArray[][] = seperateBySuit(hand);
    Card.Rank lastRank;
    ArrayList<Card[]> allSequencesList = new ArrayList<>();
    
    for (int i = 0; i < 4; i++)
    {
      if (suitArray[i].length > 2)
      {
        /* the first card cannot be ace since there has to be at least
         * 3 cards in this suit and ace will be at the end of the array */
        lastRank = suitArray[i][0].getRank();
        // reset, this is the first consecutive card
        consecutiveCount = 0;
        // check if cards are consecutive
        for(int j = 1; j < suitArray[i].length; j++)
        {
          // if this card is the last in the suit
          if (j == suitArray[i].length-1)
          {
            // if this card is also consecutive
            if (Card.Rank.values()[lastRank.ordinal()+1].equals(suitArray[i][j].getRank()))
            {
              consecutiveCount++;
              if (consecutiveCount > 2)
                allSequencesList.add(Arrays.copyOfRange(suitArray[i], j-consecutiveCount, j+1));
            }
            else consecutiveCount = 0; // need to reset the counter
          }
          // this card is the consecutive next of the last
          else if (suitArray[i][j].getRank().equals(Card.Rank.values()[lastRank.ordinal()+1]))
          {
            consecutiveCount++;
            lastRank = suitArray[i][j].getRank();
          }
          else // this is not the consecutive next
          {
            // save this sequenec if it is longer than 2
            if (consecutiveCount > 2)
              allSequencesList.add(Arrays.copyOfRange(suitArray[i], j-1-consecutiveCount, j));
            consecutiveCount = 0; // need to reset the counter
            lastRank = suitArray[i][j].getRank(); // reset lastRank too
          }
        }
      }
    }
    
    return allSequencesList.toArray(new Card[allSequencesList.size()][]);
  }
  
  public static Card.Rank getSequenceHighest(Card hand[])
  {
    Card allSequences[][] = getAllSequences(hand);
    
    // if there are no sequences
    if (allSequences.length == 0) return null;
    
    int longestSequence = 0, longestIndex = 0;
    
    for(int i = 0; i < allSequences.length-1; i++)
      // if this is the longest sequence
      if (allSequences[i].length > longestSequence)
      {
        longestSequence = allSequences[i].length;
        longestIndex = i;
      }
      // else if this sequence is the same length as the longest
      else if (allSequences[i].length == longestSequence)
        // if this sequence is higher then the other
        if (allSequences[i][allSequences[i].length-1].getRank().compareTo(allSequences[longestIndex][allSequences[longestIndex].length-1].getRank()) > 0)
        {
          longestSequence = allSequences[i].length;
          longestIndex = i;
        }
    
    return allSequences[longestIndex][allSequences[longestIndex].length-1].getRank();
  }
  
  public static String getSequenceHighestName(int number, Card.Rank rank)
  {
    return getSequenceName(number) + " to " + rank;
  }
  
  /**
   *
   * @param hand
   * @return int array of size 5, each int represent the number of cards (if larger than 2)
   * in a rank from ten to ace
   */
  public static int[] getAllSets(Card hand[])
  {
    int allSets[] = new int[5];
    for (Card card:hand)
      // if this card is higher than 9
      if (card.getRank().compareTo(Card.Rank.Nine) > 0)
        allSets[card.getRank().ordinal()-3]++;
    for (int i = 0; i < 5; i++)
      if (allSets[i] < 3)
        allSets[i] = 0;
    return allSets;
  }
  
  // returns either 0, 3 or 4 representing no set, set of 3 and set of 4
  public static int getSet(Card hand[])
  {
    int allSets[] = getAllSets(hand);
    int result = 0;
    for (int i = 0; i < allSets.length; i++)
      if(allSets[i] > result)
        result = allSets[i];
    return result;
  }
  
  public static Card.Rank getSetRank(Card hand[])
  {
    int allSets[] = getAllSets(hand);
    int set = 0, rank = -1;
    for (int i = 0; i < allSets.length; i++)
      if(allSets[i] >= set && allSets[i] != 0)
      {
        set = allSets[i];
        rank = i;
      }
    return (rank != -1) ? Card.Rank.values()[rank+3] : null;
  }
  
  public static String getSetHighestName(int setSize, int rank)
  {
    if (setSize < 3 || setSize > 4 || rank < 0 || rank > 4)
      throw new IllegalArgumentException("Wrong arguments for getSetName(" + setSize + ", " + rank + ")");
    return (setSize==3?"Trios":"Quatorzes") + " of " + Card.Rank.values()[rank+3];
  }
  
//  private static void checkLegalHand(Card hand[])
//  {
//    if (hand.length != 12)
//      throw new IllegalArgumentException("Illegal Hand of length " + hand.length);
//  }
}
