/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package piquet;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import piquet.Card.*;

/**
 *
 * @author isaac006
 */
public class RulesTest {
  
  // every card in a piquet deck.
  private static Card c7, c8, c9, c10, cJ, cQ, cK, cA,
                      d7, d8, d9, d10, dJ, dQ, dK, dA,
                      s7, s8, s9, s10, sJ, sQ, sK, sA,
                      h7, h8, h9, h10, hJ, hQ, hK, hA;
  public RulesTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    // initilise all the cards
    c7  = new Card(Suit.Clubs, Rank.Seven, null);
    c8  = new Card(Suit.Clubs, Rank.Eight, null);
    c9  = new Card(Suit.Clubs, Rank.Nine, null);
    c10 = new Card(Suit.Clubs, Rank.Ten, null);
    cJ  = new Card(Suit.Clubs, Rank.Jack, null);
    cQ  = new Card(Suit.Clubs, Rank.Queen, null);
    cK  = new Card(Suit.Clubs, Rank.King, null);
    cA  = new Card(Suit.Clubs, Rank.Ace, null);
    
    d7  = new Card(Suit.Diamonds, Rank.Seven, null);
    d8  = new Card(Suit.Diamonds, Rank.Eight, null);
    d9  = new Card(Suit.Diamonds, Rank.Nine, null);
    d10 = new Card(Suit.Diamonds, Rank.Ten, null);
    dJ  = new Card(Suit.Diamonds, Rank.Jack, null);
    dQ  = new Card(Suit.Diamonds, Rank.Queen, null);
    dK  = new Card(Suit.Diamonds, Rank.King, null);
    dA  = new Card(Suit.Diamonds, Rank.Ace, null);
    
    s7  = new Card(Suit.Spades, Rank.Seven, null);
    s8  = new Card(Suit.Spades, Rank.Eight, null);
    s9  = new Card(Suit.Spades, Rank.Nine, null);
    s10 = new Card(Suit.Spades, Rank.Ten, null);
    sJ  = new Card(Suit.Spades, Rank.Jack, null);
    sQ  = new Card(Suit.Spades, Rank.Queen, null);
    sK  = new Card(Suit.Spades, Rank.King, null);
    sA  = new Card(Suit.Spades, Rank.Ace, null);
    
    h7  = new Card(Suit.Hearts, Rank.Seven, null);
    h8  = new Card(Suit.Hearts, Rank.Eight, null);
    h9  = new Card(Suit.Hearts, Rank.Nine, null);
    h10 = new Card(Suit.Hearts, Rank.Ten, null);
    hJ  = new Card(Suit.Hearts, Rank.Jack, null);
    hQ  = new Card(Suit.Hearts, Rank.Queen, null);
    hK  = new Card(Suit.Hearts, Rank.King, null);
    hA  = new Card(Suit.Hearts, Rank.Ace, null);
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of isCarteBlanche method, of class Rules.
   */
  @Test
  public void testIsCarteBlanche1() {
    System.out.println("isCarteBlanche1");
    Card[] hand = {c7, c8, c10, d8, d9, s7, s8, s9, s10, h7, h9, h10};
    Arrays.sort(hand);
    boolean expResult = true;
    boolean result = Rules.isCarteBlanche(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testIsCarteBlanche2() {
    System.out.println("isCarteBlanche2");
    Card[] hand = {c7, c8, cQ, d8, d9, s7, s8, s9, s10, h7, h9, h10};
    Arrays.sort(hand);
    boolean expResult = false;
    boolean result = Rules.isCarteBlanche(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testIsCarteBlanche3() {
    System.out.println("isCarteBlanche3");
    Card[] hand = {c7, cJ, cQ, dJ, dK, sJ, sQ, sK, sA, hQ, hK, hA};
    Arrays.sort(hand);
    boolean expResult = false;
    boolean result = Rules.isCarteBlanche(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testIsCarteBlanche4() {
    System.out.println("isCarteBlanche4");
    Card[] hand = {cJ, cQ, cA, dJ, dK, sJ, sQ, sK, sA, hQ, hK, hA};
    Arrays.sort(hand);
    boolean expResult = false;
    boolean result = Rules.isCarteBlanche(hand);
    assertEquals(expResult, result);
  }

  /**
   * Test of seperateBySuit method, of class Rules.
   */
  @Test
  public void testSeperateBySuit1() {
    System.out.println("seperateBySuit1");
    Card[] hand = {cJ, cQ, cA, dJ, dK, sJ, sQ, sK, sA, hQ, hK, hA};
    Arrays.sort(hand);
    Card[][] expResult = {{cJ, cQ, cA}, {dJ, dK}, {sJ, sQ, sK, sA}, {hQ, hK, hA}};
    Card[][] result = Rules.seperateBySuit(hand);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testSeperateBySuit2() {
    System.out.println("seperateBySuit2");
    Card[] hand = {d7, d8, d9, d10, dJ, dQ, dK, dA, s8, s10, sK, hK};
    Arrays.sort(hand);
    Card[][] expResult = {{}, {d7, d8, d9, d10, dJ, dQ, dK, dA}, {s8, s10, sK}, {hK}};
    Card[][] result = Rules.seperateBySuit(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testSeperateBySuit3() {
    System.out.println("seperateBySuit3");
    Card[] hand = {c7, c8, c9, c10, cJ, cQ, cK, cA, d8, d10, dK, dA};
    Arrays.sort(hand);
    Card[][] expResult = {{c7, c8, c9, c10, cJ, cQ, cK, cA}, {d8, d10, dK, dA}, {}, {}};
    Card[][] result = Rules.seperateBySuit(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testSeperateBySuit4() {
    System.out.println("seperateBySuit4");
    Card[] hand = {c7, c8, cJ, cK, s8, s10, sK, sA, h8, h9, hQ, hA};
    Arrays.sort(hand);
    Card[][] expResult = {{c7, c8, cJ, cK}, {}, {s8, s10, sK, sA}, {h8, h9, hQ, hA}};
    Card[][] result = Rules.seperateBySuit(hand);
    assertArrayEquals(expResult, result);
  }

  /**
   * Test of getPoint method, of class Rules.
   */
  @Test
  public void testGetPoint1() {
    System.out.println("getPoint1");
    Card[] hand = {c7, c8, cJ, cK, s8, s10, sK, sA, h8, h9, hQ, hA};
    Arrays.sort(hand);
    int expResult = 4;
    int result = Rules.getPoint(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetPoint2() {
    System.out.println("getPoint2");
    Card[] hand = {c7, c8, c9, c10, cJ, cQ, cK, cA, d8, d10, dK, dA};
    Arrays.sort(hand);
    int expResult = 8;
    int result = Rules.getPoint(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetPoint3() {
    System.out.println("getPoint3");
    Card[] hand = {c7, c8, c10, d8, d9, d10, s8, s9, s10, h7, h9, h10};
    Arrays.sort(hand);
    int expResult = 0;
    int result = Rules.getPoint(hand);
    assertEquals(expResult, result);
  }

  /**
   * Test of getPointValue method, of class Rules.
   */
  @Test
  public void testGetPointValue1() {
    System.out.println("getPointValue1");
    Card[] hand = {c7, c8, cJ, cK, s8, s10, sK, sA, h7, h8, h9, h10};
    Arrays.sort(hand);
    int expResult = 39;
    int result = Rules.getPointValue(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetPointValue2() {
    System.out.println("getPointValue2");
    Card[] hand = {c7, c8, c9, c10, cJ, cQ, cK, cA, d8, d10, dK, dA};
    Arrays.sort(hand);
    int expResult = 75;
    int result = Rules.getPointValue(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetPointValue3() {
    System.out.println("getPointValue3");
    Card[] hand = {c7, c8, c9, d8, d10, dK, s8, s9, sK, h7, h9, hA};
    Arrays.sort(hand);
    int expResult = 0;
    int result = Rules.getPointValue(hand);
    assertEquals(expResult, result);
  }

  /**
   * Test of getSequences method, of class Rules.
   */
  @Test
  public void testGetSequences1() {
    System.out.println("getSequences1");
    Card[] hand = {c7, c8, c9, c10, d10, dK, s8, s9, sK, h7, h9, hA};
    Arrays.sort(hand);
    int[] expResult = {4};
    int[] result = Rules.getSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetSequences2() {
    System.out.println("getSequences2");
    Card[] hand = {d7, d8, d10, dJ, dQ, dK, dA, s9, s10, sJ, sQ, hA};
    Arrays.sort(hand);
    int[] expResult = {5, 4};
    int[] result = Rules.getSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetSequences3() {
    System.out.println("getSequences3");
    Card[] hand = {c7, c8, c10, dJ, dQ, dK, s9, s10, sJ, h7, h9, hA};
    Arrays.sort(hand);
    int[] expResult = {};
    int[] result = Rules.getSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetSequences4() {
    System.out.println("getSequences4");
    Card[] hand = {c7, c8, c9, c10, dJ, dQ, dK, dA, s9, s10, sJ, sQ};
    Arrays.sort(hand);
    int[] expResult = {4, 4, 4};
    int[] result = Rules.getSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetSequences5() {
    System.out.println("getSequences5");
    Card[] hand = {c7, c8, c9, c10, d10, dJ, dQ, dK, dA, s9, s10, sJ};
    Arrays.sort(hand);
    int[] expResult = {5, 4};
    int[] result = Rules.getSequences(hand);
    assertArrayEquals(expResult, result);
  }

  /**
   * Test of getAllSequences method, of class Rules.
   */
  @Test
  public void testGetAllSequences1() {
    System.out.println("getAllSequences1");
    Card[] hand = {c7, c8, c9, c10, d10, dK, s8, s9, sK, h7, h9, hA};
    Arrays.sort(hand);
    Card[][] expResult = {{c7, c8, c9, c10}};
    Card[][] result = Rules.getAllSequences(hand);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetAllSequences2() {
    System.out.println("getAllSequences2");
    Card[] hand = {c7, c8, c10, cJ, cQ, cK, s8, s9, sK, h7, h9, hA};
    Arrays.sort(hand);
    Card[][] expResult = {{c10, cJ, cQ, cK}};
    Card[][] result = Rules.getAllSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetAllSequences3() {
    System.out.println("getAllSequences3");
    Card[] hand = {d7, d8, d10, dJ, dQ, dK, dA, s9, s10, sJ, sQ, hA};
    Arrays.sort(hand);
    Card[][] expResult = {{d10, dJ, dQ, dK, dA}, {s9, s10, sJ, sQ}};
    Card[][] result = Rules.getAllSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetAllSequences4() {
    System.out.println("getAllSequences4");
    Card[] hand = {c7, c8, c10, dJ, dQ, dK, s9, s10, sJ, h7, h9, hA};
    Arrays.sort(hand);
    Card[][] expResult = {};
    Card[][] result = Rules.getAllSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetAllSequences5() {
    System.out.println("getAllSequences5");
    Card[] hand = {c7, c8, c9, c10, dJ, dQ, dK, dA, s9, s10, sJ, sQ};
    Arrays.sort(hand);
    Card[][] expResult = {{c7, c8, c9, c10}, {dJ, dQ, dK, dA}, {s9, s10, sJ, sQ}};
    Card[][] result = Rules.getAllSequences(hand);
    assertArrayEquals(expResult, result);
  }
  
  /**
   * Test of getSequenceHighest method, of class Rules.
   */
  @Test
  public void testGetSequenceHighest1() {
    System.out.println("getSequenceHighest1");
    Card[] hand = {c7, c8, c9, c10, d10, dK, s8, s9, sK, h7, h9, hA};
    Arrays.sort(hand);
    Rank expResult = Rank.Ten;
    Rank result = Rules.getSequenceHighest(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSequenceHighest2() {
    System.out.println("getSequenceHighest2");
    Card[] hand = {c7, c8, c9, c10, dJ, dQ, dK, dA, s9, s10, sJ, sQ};
    Arrays.sort(hand);
    Rank expResult = Rank.Ace;
    Rank result = Rules.getSequenceHighest(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSequenceHighest3() {
    System.out.println("getSequenceHighest3");
    Card[] hand = {c7, c8, c10, dJ, dQ, dK, s9, s10, sJ, h7, h9, hA};
    Arrays.sort(hand);
    Rank expResult = null;
    Rank result = Rules.getSequenceHighest(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSequenceHighest4() {
    System.out.println("getSequenceHighest4");
    Card[] hand = {d7, d8, d9, d10, dJ, dQ, dK, s10, sJ, sQ, sK, sA};
    Arrays.sort(hand);
    Rank expResult = Rank.King;
    Rank result = Rules.getSequenceHighest(hand);
    assertEquals(expResult, result);
  }

  /**
   * Test of getAllSets method, of class Rules.
   */
  @Test
  public void testGetAllSets1() {
    System.out.println("getAllSets1");
    Card[] hand = {d7, d8, d9, d10, dJ, dQ, dK, s10, sJ, sQ, sK, sA};
    Arrays.sort(hand);
    int[] expResult = {0, 0, 0, 0, 0};
    int[] result = Rules.getAllSets(hand);
    assertArrayEquals(expResult, result);
  }
  
  @Test
  public void testGetAllSets2() {
    System.out.println("getAllSets2");
    Card[] hand = {c10, d8, d9, d10, dJ, dQ, dK, s10, sJ, sQ, sK, sA};
    Arrays.sort(hand);
    int[] expResult = {3, 0, 0, 0, 0};
    int[] result = Rules.getAllSets(hand);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetAllSets3() {
    System.out.println("getAllSets3");
    Card[] hand = {cJ, cQ, cK, cA, sJ, sQ, sK, sA, hJ, hQ, hK, hA};
    Arrays.sort(hand);
    int[] expResult = {0, 3, 3, 3, 3};
    int[] result = Rules.getAllSets(hand);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetAllSets4() {
    System.out.println("getAllSets4");
    Card[] hand = {cJ, cQ, cK, cA, dK, sJ, sQ, sK, sA, hJ, hQ, hK};
    Arrays.sort(hand);
    int[] expResult = {0, 3, 3, 4, 0};
    int[] result = Rules.getAllSets(hand);
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testGetAllSets5() {
    System.out.println("getAllSets5");
    Card[] hand = {c7, c8, c9, d7, d8, d9, s7, s8, s9, h7, h8, h9};
    Arrays.sort(hand);
    int[] expResult = {0, 0, 0, 0, 0};
    int[] result = Rules.getAllSets(hand);
    assertArrayEquals(expResult, result);
  }
  
  /**
   * Test of getSet method, of class Rules.
   */
  @Test
  public void testGetSet1() {
    System.out.println("getSet1");
    Card[] hand = {cJ, cQ, cK, cA, dK, sJ, sQ, sK, sA, hJ, hQ, hK};
    Arrays.sort(hand);
    int expResult = 4;
    int result = Rules.getSet(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSet2() {
    System.out.println("getSet2");
    Card[] hand = {cJ, cQ, cK, cA, sJ, sQ, sK, sA, hJ, hQ, hK, hA};
    Arrays.sort(hand);
    int expResult = 3;
    int result = Rules.getSet(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSet3() {
    System.out.println("getSet3");
    Card[] hand = {c10, d8, d9, d10, dJ, dQ, dK, s10, sJ, sQ, sK, sA};
    Arrays.sort(hand);
    int expResult = 3;
    int result = Rules.getSet(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSet4() {
    System.out.println("getSet4");
    Card[] hand = {d7, d8, d9, d10, dJ, dQ, dK, s10, sJ, sQ, sK, sA};
    Arrays.sort(hand);
    int expResult = 0;
    int result = Rules.getSet(hand);
    assertEquals(expResult, result);
  }
  
  /**
   * Test of getSetRank method, of class Rules.
   */
  @Test
  public void testGetSetRank1() {
    System.out.println("getSetRank1");
    Card[] hand = {d7, d8, d9, d10, dJ, dQ, dK, s10, sJ, sQ, sK, sA};
    Arrays.sort(hand);
    Rank expResult = null;
    Rank result = Rules.getSetRank(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSetRank2() {
    System.out.println("getSetRank2");
    Card[] hand = {cJ, cQ, cK, cA, dK, sJ, sQ, sK, sA, hJ, hQ, hK};
    Arrays.sort(hand);
    Rank expResult = Rank.King;
    Rank result = Rules.getSetRank(hand);
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetSetRank3() {
    System.out.println("getSetRank3");
    Card[] hand = {cJ, cQ, cK, cA, sJ, sQ, sK, sA, hJ, hQ, hK, hA};
    Arrays.sort(hand);
    Rank expResult = Rank.Ace;
    Rank result = Rules.getSetRank(hand);
    assertEquals(expResult, result);
  }
}
