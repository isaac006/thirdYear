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
 * this class contains all the methods required to play the game
 * @author isaac006
 */
public class Game
{ 
  private static Card          deck[],         // the piquet deck
                               humanHand[],    // player's hand
                               talon[],        // the talon
                               clickedCard,    // the card the user selected
                               cutResult[];    // the 2 cards to specify who is elder
  private static boolean       humanIsElder = false,
                               humanTurn = false,
                               humanHandSorted = false,
                               cutEqual; // true if recut is needed
  private static int           humanScore = 0,
                               computerScore = 0,
                               round = 1,
                               roundsPerGame = 6; // 6 rounds in a game
  private static List<Integer> cardsToSwapList; // a list of indexes of the cards to swap
  private static List<Card>    humanSwappedList; // list of cards the human decided to swap
  private static Computer      computer;
  private static Rules         rules = new Rules();
                     
  private static State state = State.NotPlaying;
  
  // an enum representing the state of a game
  // for communicating with gui
  public static enum State
  {
    NotPlaying,                  // before game starts or after game over
    Cut,                         // decide which player is elder and which is younger
    ReadyDeal,                   // state where the cards are animated to flip and go back to the talon before dealing
    Deal,                        // deal the cards
    ElderCarteBlanche,           // elder check for carte blanche
    ElderSelectSwap,             // elder selects his cards to swap
    ElderSwapTo,                 // elder moves its swapped card to another area
    ElderSwapFrom,               // elder gets new cards from talon
    ElderPeep,                   // elder looks at the remining cards in talon
    YoungerCarteBlanche,         // younger then checks for carte blanche
    YoungerSelectSwap,           // younger selects his cards to swap
    YoungerSwapTo,               // then swaps
    YoungerSwapFrom,             //
    YoungerPeep,                 // younger peeps
    ElderDeclarePoint,           // the 3 declation stage, elder first
    YoungerReplyPoint,           //
    ElderCalloutPoint,           // only needed if they have the same point
    YoungerReplyCalloutPoint,    //
    ElderDeclareSequence,        //
    YoungerReplySequence,        //
    ElderCalloutSequence,        // only needed if they have the same sequence length
    YoungerReplyCalloutSequence, //
    ElderDeclareSet,             //
    YoungerReplySet,             //
    ElderCalloutSet,             // only needed if they have the same set size
    YoungerReplyCalloutSet,      //
    ElderMove,                   // start of the game
    YoungerMove,                 //
    GameOver;                    // last stage
    
    // method to cycle through states
    private State next()
    {
      return values()[(ordinal()+1)%values().length];
    }
  }
  
  // getters for private objects
  public Card[] getDeck()              { return deck; }
  public Card[] getHumanHand()         { return humanHand; }
  public Card[] getComputerHand()      { return computer.getHand(); }
  public Card[] getTalon()             { return talon; }
  public Card getClickedCard()         { return clickedCard; }
  public boolean isHumanElder()        { return humanIsElder; }
  public boolean isHumanTurn()         { return humanTurn; }
  public boolean isHumanHandSorted()   { return humanHandSorted; }
  public boolean isCutEqual()          { return cutEqual; }
  public int getHumanScore()           { return humanScore; }
  public int getCopmuterScore()        { return computerScore; }
  public List getHumanToSwapList()     { return cardsToSwapList; }
  public List getHumanSwappedList()    { return humanSwappedList; }
  public List getComputerToSwapList()  { return computer.getToSwapList(); }
  public List getComputerSwappedList() { return computer.getSwappedList(); }
  public State getState()              { return state; }
  public Computer getComputer()        { return computer; }
  public Rules getRules()              { return rules; }
  
  public Card[] getElderHand()
  {
    return humanIsElder?humanHand:getComputerHand();
  }
  
  public Card[] getYoungerHand()
  {
    return humanIsElder?getComputerHand():humanHand;
  }
  
  public Card getCutResult(boolean isHuman)
  {
    return isHuman?cutResult[0]:cutResult[1];
  }
  
  public Card[] getCutResult()
  {
    return cutResult;
  }
  
  // constructor
  public Game(Card back)
  {
    deck = Card.createPiquetDeck(back);
  }
  
  // when user wants to start a new round
  public void newRound()
  {
    // reset cardsToSwapList
    cardsToSwapList = new ArrayList<>();
    
    // reset deck
    for (Card card:deck)
    {
      card.flipQuick(true);
      card.setNotDealt();
      card.setIndex(0);
    }
    Card[] sortedDeck = deck;
    Arrays.sort(sortedDeck);
    Card.shuffle(deck);
    // human hand == first 12 cards
    // not sorted for animation
    humanHandSorted = false;
    humanHand = Arrays.copyOfRange(deck, 0, 12);
    // computer hand == next 12 cards
    computer = new Computer(Arrays.copyOfRange(deck, 12, 24), sortedDeck, rules);
    // talon == rest 8 cards
    talon = Arrays.copyOfRange(deck, 24, 32);
    // start the game
    cut();
  }
  
  public void newGame()
  {
    newRound();
    // reset scores
    humanScore = 0;
    computerScore = 0;
    // reset round
    round = 1;
  }
  
  public void cut()
  {
    cutResult = Card.cut(deck);
    // if the 2 cards have the same rank, this round doesnt count
    if (getCutResult(true).getRank().equals(getCutResult(false).getRank()))
      cutEqual = true;
    else
    {
      cutEqual = false;
      // if human got a higher card then he is the older and starts first
      humanIsElder = getCutResult(true).getRank().compareTo(getCutResult(false).getRank()) > 0;
      computer.setHumanIsElder(humanIsElder);
      System.out.println((humanIsElder?"Human":"Computer") + " is Elder.");
    }
    state = State.Cut;
  }
  
  public boolean isPlaying()
  {
    return state != State.NotPlaying;
  }
  
  public boolean hintsAllowed()
  {
    return state == State.Cut;
  }
  
  public boolean inCarteBlanche()
  {
    return state == State.ElderCarteBlanche || state == State.YoungerCarteBlanche;
  }
  
  public boolean humanMustSwap()
  {
    return rules.humanMustSwap(humanIsElder);
  }
  
  public boolean isHumanSelectSwap()
  {
    return humanIsElder && state == State.ElderSelectSwap ||
           !humanIsElder && state == State.YoungerSelectSwap;
  }
  
  public boolean isComputerSwap()
  {
    if (!humanIsElder) return state == State.ElderSwapTo || state == State.ElderSwapFrom;
    else return state == State.YoungerSwapTo || state == State.YoungerSwapFrom;
  }
  
  public boolean isHumanSwap()
  {
    if (humanIsElder) return state == State.ElderSwapTo || state == State.ElderSwapFrom;
    else return state == State.YoungerSwapTo || state == State.YoungerSwapFrom;
  }
  
  public boolean computerSwapped()
  {
    return state.compareTo(!humanIsElder ? State.ElderSwapFrom : State.YoungerSwapFrom) > 0;
  }
  
  public boolean humanSwapped()
  {
    return state.compareTo(humanIsElder ? State.ElderSwapFrom : State.YoungerSwapFrom) > 0;
  }
  
  public boolean userCanClickHand()
  {
    if (humanIsElder)
      return state == State.ElderSelectSwap || state == State.ElderMove;
    else
      return state == State.YoungerSelectSwap || state == State.YoungerMove;
  }
  
  public boolean userCanClickTalon()
  {
    if (humanIsElder)
      return state == State.ElderSelectSwap || state == State.Cut;
    else
      return state == State.YoungerSelectSwap || state == State.Cut;
  }
  
  public boolean cardsDealt()
  {
    return state.compareTo(State.Deal) > 0;
  }
  private boolean talonCopied = false;
  public void nextState()
  {
    state = state.next();
    System.out.println("Current state = " + state);
    switch(state)
    {
      case Deal:
        if (cutEqual) cut();
        break;
        
      case ElderCarteBlanche:
      case YoungerCarteBlanche:
        boolean isElderCarteBlanche = state == State.ElderCarteBlanche;
        if (Rules.isCarteBlanche(isElderCarteBlanche?getElderHand():getYoungerHand()))
        {
          if (humanIsElder ^ !isElderCarteBlanche)
          {
            humanScore += rules.getCarteBlancheScore();
            computer.humanCarteBlanche(humanHand); // show your cards to the computer
          }
          else // computer has Carte Blanche
              computerScore += rules.getCarteBlancheScore();
        }
        // if there is no Carte Blanche, go to next state
        else nextState();
        break;
        
      case ElderSelectSwap:
      case YoungerSelectSwap:
        boolean isElderSelectSwap = state == State.ElderSelectSwap;
        if (humanIsElder ^ isElderSelectSwap) // computer is swapping
        {
          computer.calculateSwapWithTalon(talon.length, rules.computerMustSwap(humanIsElder));
          nextState();
        }
        break;
        
      case ElderSwapTo:
      case YoungerSwapTo:
        boolean isElderSwapTo = state == State.ElderSwapTo;
        if (humanIsElder ^ !isElderSwapTo) // human swaps with talon
        {
          // swap with talon and save swapped cards into array
          Collections.sort(cardsToSwapList);
          humanSwappedList = new ArrayList<>();
          for (int i = 0; i < cardsToSwapList.size(); i++)
          {
            humanHand[cardsToSwapList.get(i)].setNotDealt();
            humanSwappedList.add(humanHand[cardsToSwapList.get(i)]);
            humanHand[cardsToSwapList.get(i)] = talon[i];
            humanHand[cardsToSwapList.get(i)].setDealt();
          }
        }
        else // computer swaps with talon
          // swap with talon
          computer.swapWithTalon(Arrays.copyOfRange(talon, 0, computer.getToSwapList().size()));
        break;
        
      case ElderSwapFrom:
      case YoungerSwapFrom:
        boolean isElderSwapFrom = state == State.ElderSwapFrom;
        if (humanIsElder ^ !isElderSwapFrom) // human swaps with talon
        {
          for (int i = 0; i < cardsToSwapList.size(); i++)
            humanHand[cardsToSwapList.get(i)].setNotDealt();
          // remove swapped from talon
          talon = Arrays.copyOfRange(talon, cardsToSwapList.size(), talon.length);
        }
        else // computer swaps with talon
        {
          for (int i = 0; i < computer.getSwappedList().size(); i++)
            computer.getHand()[(int)computer.getToSwapList().get(i)].setNotDealt();
          // remove swapped from talon
          talon = Arrays.copyOfRange(talon, computer.getToSwapList().size(), talon.length);
        }
        break;
        
      case ElderPeep:
      case YoungerPeep:
        boolean isElderPeep = state == State.ElderPeep;
        if (!humanIsElder ^ !isElderPeep) // computer peeps
        {
          int peepAmount = 5-computer.getSwapSize();
          if (talon.length < peepAmount) peepAmount = talon.length;
          Card [] toPeep = Arrays.copyOfRange(talon, 0, peepAmount);
          for (Card card:toPeep)
            System.out.println("Computer peep: " + card);
          computer.peep(toPeep);
        }
        else
          System.out.println("Human peep");
        break;
    }
  }
  
  public void sortHumanHand()
  {
    Arrays.sort(humanHand);
//    Card.sortByRankThenSuitValue(humanHand);
    humanHandSorted = true;
  }
  
  public void userClickedHand(int index)
  {
    if (isHumanSelectSwap())
    {
      // what if user clicks again to deselect this card?
      int indexOf = cardsToSwapList.indexOf(index);
      int maxToSwap = talon.length > 5 ? 5 : talon.length;
      if (indexOf != -1) // if arraylist contains this number
      {
        System.out.println("ArrayList contains this number " + index + " in index " + indexOf);
        cardsToSwapList.remove(indexOf);
      }
      // if user has not reached the limit of the cards available to swap, add card into list
      else if (cardsToSwapList.size() < maxToSwap)
        cardsToSwapList.add(index);
    }
    
    clickedCard = humanHand[index];
//    humanHand[index] = humanHand[humanHand.length-1];
//    humanHand[humanHand.length-1] = clickedCard;
//    // copy all cards except the last one
//    Arrays.sort(humanHand = Arrays.copyOfRange(humanHand, 0, humanHand.length-1));
    System.out.println("Card clicked: " + clickedCard);
  }
}
