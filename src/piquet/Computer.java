/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package piquet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Isaac Cheng
 */
public class Computer {
  // variables
  private Card[]        myHand,
                        available = new Card[20], // a list of cards that could be in the himan's hand
                        possibleHumanHand = new Card[12],
                        possibleTalon, // talon size is unknown if computer is second player
                        deck;
  private boolean       humanIsElder;
  private Rules         rules;
  private List<Integer> toSwapList;
  private List<Card>    swappedList = new ArrayList<>(), // list of cards the computer decided to swap, not going to be in human's hand
                        blacklist = new ArrayList<>(), // a list of cards we know the human player will not have
                        peepList = new ArrayList<>(); // a list  of cards we know are from the talon and if the human player swaps we know they will have it
  
  // getters
  public Card[] getHand()      { return myHand; }
  public List getSwappedList() { return swappedList; }
  public List getToSwapList()  { return toSwapList; }
  public int getSwapSize()     { return toSwapList.size(); }
  public int getPeepSize()     { return peepList.size(); }
  
  // constructor
  public Computer(Card[] hand, Card[] aDeck, Rules theRules)
  {
    if (hand.length != 12 || aDeck.length != 32)
      throw new IllegalArgumentException("Wrong input! Hand length: " + hand.length + ", deck length: " + deck.length);
    
    rules = theRules;
    myHand = hand;
    deck = aDeck.clone();
    Arrays.sort(deck);
    
    blacklist.addAll(Arrays.asList(myHand));
    
    int index = 0;
    List handList = Arrays.asList(myHand);
    for (int i = 0; i < deck.length; i++)
      if (!handList.contains(deck[i]))
        available[index++] = deck[i];
  }
  
  public void setHumanIsElder(boolean humanElder)
  {
    humanIsElder = humanElder;
  }
  
  // if the human player has CarteBlanche they will show their hand to prove it
  public void humanCarteBlanche(Card[] humanHand)
  {
    if (!Rules.isCarteBlanche(humanHand))
      throw new IllegalArgumentException("Human Hand is not Carte Blanche");
    
    // take note of their hand
    possibleHumanHand = humanHand.clone();
  }
  
  // to peep at the remaining cards in the talon
  public void peep(Card[] toPeep)
  {
    peepList.addAll(Arrays.asList(toPeep));
  }
  
  // to see how many cards the human player decided to swap, if we peeped before we then know which cards they must have
  // if we are elder we woud have peeped and have a list
  public void humanSwapped(int humanSwapped)
  {
    // all this can only happen if we are elder
    if (!humanIsElder)
    {
      // if human swapped more then we  peeped we set it to the amount of cards we peeped
      humanSwapped = humanSwapped > peepList.size() ? peepList.size() : humanSwapped;
      // we will blacklist the cards in peepList which are not swapped
      blacklist.addAll(peepList.subList(humanSwapped-1, peepList.size()));
      // we only want the subset of cards which are swapped by the human in peepList
      peepList = peepList.subList(0, humanSwapped);
    }
  }
  
  // method to select which cards to swap and returns the index of the card in the hand, -1 if swapping less than the max allowed 5 cards
  // talon size represent the remaining cards in the talon, the maximum amount we can swap or peak
  public void calculateSwapWithTalon(int talonSize, boolean mustSwap)
  {
    /* according to wiki:
          * Players discard low cards (nine or lower) even if this means getting rid of four or more of one suit. 
          * This diminishes the chances of winning the point round, but this round is the lowest scoring one. 
          * Getting rid of these lower cards to get straights of five or more is very beneficial and will increase one's score greatly.
          */
    toSwapList = new ArrayList<>();
    
    // can only swap up to 5 cards
    if (talonSize > 5) talonSize = 5;
    
    // index the cards so can find their original index after sorting
    for (int i = 0; i < myHand.length; i++)
      myHand[i].setIndex(i);
    
    Card[] sorted = Card.sortByRankThenSuitValue(myHand);
    for (int i = 0; i < talonSize; i++)
    {
      // if this card is less than 10
      if (sorted[i].getRank().compareTo(Card.Rank.Ten) < 0)
        // add it to the toSwap list
        toSwapList.add(sorted[i].getIndex());
      // else this card is 10 or higher, which means no more low cards
      else break;
    }
    
    // if there are no cards to swap and we must swap at least one
    if (mustSwap && toSwapList.isEmpty())
      // select the lowest valued card to swap
      toSwapList.add(sorted[0].getIndex());
    
    // sort the result by their index to conceal relation to their rank
    Collections.sort(toSwapList);
  }
  
  public void swapWithTalon(Card[] talon)
  {
    // swap with talon and save swapped card into swapped array
    for (int i = 0; i < toSwapList.size(); i++)
    {
      myHand[toSwapList.get(i)].setNotDealt();
      swappedList.add(myHand[toSwapList.get(i)]);
      myHand[toSwapList.get(i)] = talon[i];
      myHand[toSwapList.get(i)].setDealt();
      // adds newly swapped cards into blacklist
      blacklist.add(talon[i]);
    }
  }
  
  public void sortMyHand()
  {
    Card.sort(myHand);
  }
}
