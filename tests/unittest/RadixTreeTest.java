package unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.radixtree.RadixTree;

public class RadixTreeTest {
	
	@Test
	public void radixTreeEmptySizeShouldBeOne() {

		RadixTree mRadixTree = new RadixTree();

	 	assertEquals("The size of the radix tree when created is 1", 1, mRadixTree.countNodes());
	   

	 }
	
	@Test
	public void radixTreeBasicCountCheck() {

		RadixTree mRadixTree = new RadixTree();
	   
		mRadixTree.insertWord("helloWorld");
		assertEquals("Added 1 word to radix tree", 2, mRadixTree.countNodes());
		
		mRadixTree.insertWord("test");
		assertEquals("Added 2 word to radix tree", 3, mRadixTree.countNodes());
		
		assertTrue("Search word added in radix tree", mRadixTree.findWord("test"));
		assertTrue("Search word added in radix tree", mRadixTree.findWord("helloWorld"));
		
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("tes"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("tested"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("hello"));

	 }
	
	@Test
	public void radixTreeEmptyStringShouldReturnFalse() {
		
		RadixTree mRadixTree = new RadixTree();
		assertFalse("Search empty word in radix tree", mRadixTree.findWord(""));
		assertFalse("Search non-existing word in radix tree", mRadixTree.findWord(" "));
		mRadixTree.insertWord("helloWorld");
		assertFalse("Search empty word in radix tree", mRadixTree.findWord(""));
	}
	
	@Test
	public void radixTreeInsertSecondCase() {

		RadixTree mRadixTree = new RadixTree();
	   
		mRadixTree.insertWord("test");
		assertEquals("Added 1 word to radix tree", 2, mRadixTree.countNodes());
		
		mRadixTree.insertWord("team");
		assertEquals("Added 2 word to radix tree with common prefix", 4, mRadixTree.countNodes());
		
		assertTrue("Search word added to radix tree", mRadixTree.findWord("test"));
		assertTrue("Search word added to radix tree", mRadixTree.findWord("team"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("te"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("tea"));
	}
	
	@Test
	public void radixTreeInsertSecondCaseInverted() {
		RadixTree mRadixTree = new RadixTree();
		   
		mRadixTree.insertWord("team");
		assertEquals("Added 1 word to radix tree", 2, mRadixTree.countNodes());
		
		mRadixTree.insertWord("test");
		assertEquals("Added 2 word to radix tree with common prefix", 4, mRadixTree.countNodes());
		
		assertTrue("Search word added to radix tree", mRadixTree.findWord("test"));
		assertTrue("Search word added to radix tree", mRadixTree.findWord("team"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("te"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("tea"));
	 }
	
	@Test
	public void radixTreeInsertThirdCase() {

		RadixTree mRadixTree = new RadixTree();
	   
		mRadixTree.insertWord("slower");
		assertEquals("Added 1 word to radix tree", 2, mRadixTree.countNodes());
		
		mRadixTree.insertWord("slow");
		assertEquals("Added a word contained in the key", 3, mRadixTree.countNodes());
		
		assertTrue("Search word added to radix tree", mRadixTree.findWord("slow"));
		assertTrue("Search word added to radix tree", mRadixTree.findWord("slower"));
		
		mRadixTree = new RadixTree();
		   
		mRadixTree.insertWord("slow");
		assertEquals("Added 1 word to radix tree", 2, mRadixTree.countNodes());
		
		mRadixTree.insertWord("slower");
		assertEquals("Added a word contained in the key", 3, mRadixTree.countNodes());
		
		assertTrue("Search word added to radix tree", mRadixTree.findWord("slow"));
		assertTrue("Search word added to radix tree", mRadixTree.findWord("slower"));

	 }
	
	@Test
	public void radixTreeInsertFourthCase() {

		RadixTree mRadixTree = new RadixTree();
	   
		mRadixTree.insertWord("test");
		assertEquals("Added 1 word to radix tree", 2, mRadixTree.countNodes());
		
		mRadixTree.insertWord("tested");
		assertEquals("Added a word with key contained in it", 3, mRadixTree.countNodes());
		
		assertTrue("Search word added to radix tree", mRadixTree.findWord("test"));
		assertTrue("Search word added to radix tree", mRadixTree.findWord("tested"));
		assertFalse("Search word not added to radix tree", mRadixTree.findWord("teste"));

	 }
	
	@Test
	public void radixTreeGetAllWords() {
		RadixTree mRadixTree = new RadixTree();
		
		mRadixTree.insertWord("test");
		mRadixTree.insertWord("tested");
		assertEquals("The number of results is good", 2, mRadixTree.getAllWordsWithPrefix("tes").size());
		assertTrue("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tes").contains("test"));
		mRadixTree.insertWord("tes");
		assertTrue("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tes").contains("tes"));
		
		mRadixTree.insertWord("tan");
		mRadixTree.insertWord("tef");
		assertTrue("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tef").contains("tef"));
		assertFalse("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tes").contains("tef"));
		assertTrue("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tan").contains("tan"));
		assertTrue("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("t").contains("tan"));
		assertFalse("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tes").contains("tan"));
		assertFalse("Has correct string with prefix", mRadixTree.getAllWordsWithPrefix("tes").contains("te"));
		
	}
	
	@Test
	public void radixTreeBiggerTest() {
		RadixTree mRadixTree = new RadixTree();
		
		mRadixTree.insertWord("Linkin Park");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		mRadixTree.insertWord("Like");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.findWord("Like"));
		mRadixTree.insertWord("Lus");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.findWord("Like"));
		assertTrue("Found word", mRadixTree.findWord("Lus"));
		mRadixTree.insertWord("Led Zeppelin");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.findWord("Like"));
		assertTrue("Found word", mRadixTree.findWord("Lus"));
		assertTrue("Found word", mRadixTree.findWord("Led Zeppelin"));
		mRadixTree.insertWord("Los Ramones");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.findWord("Like"));
		assertTrue("Found word", mRadixTree.findWord("Lus"));
		assertTrue("Found word", mRadixTree.findWord("Led Zeppelin"));
		assertTrue("Found word", mRadixTree.findWord("Los Ramones"));
		mRadixTree.insertWord("Like Girls");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.findWord("Like"));
		assertTrue("Found word", mRadixTree.findWord("Lus"));
		assertTrue("Found word", mRadixTree.findWord("Led Zeppelin"));
		assertTrue("Found word", mRadixTree.findWord("Los Ramones"));
		assertTrue("Found word", mRadixTree.findWord("Like Girls"));
		mRadixTree.insertWord("Line up");
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.findWord("Like"));
		assertTrue("Found word", mRadixTree.findWord("Lus"));
		assertTrue("Found word", mRadixTree.findWord("Led Zeppelin"));
		assertTrue("Found word", mRadixTree.findWord("Los Ramones"));
		assertTrue("Found word", mRadixTree.findWord("Like Girls"));
		assertTrue("Found word", mRadixTree.findWord("Line up"));
		mRadixTree.insertWord("Green");
		mRadixTree.insertWord("A Line");

		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("L").contains("Linkin Park"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("L").contains("Like"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("L").contains("Lus"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("L").contains("Los Ramones"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("L").contains("Led Zeppelin"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("L").contains("Like Girls"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Li").contains("Linkin Park"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Li").contains("Like"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Lu").contains("Lus"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Los").contains("Los Ramones"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Led").contains("Led Zeppelin"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Lik").contains("Like Girls"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Like").contains("Like Girls"));
		assertTrue("Found word", mRadixTree.findWord("Linkin Park"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Lin").contains("Linkin Park"));
		assertTrue("Found word", mRadixTree.getAllWordsWithPrefix("Link").contains("Linkin Park"));
	}
}
