package com.example.radixtree;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Radix tree implementation for fast search suggestion.
 * 
 * It seems that for TreeMap, the function ceilingKey(key)
 * only works for API >= 8...
 * 
 *
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RadixTree implements Serializable{
	
	private Node root;
	
	public RadixTree() {
		root = new Node();
	}
	
	public void insertWord(String word) {
		root.insertWord(word);
	}
	
	public int countNodes() {
		return root.countNodes();
	}
	
	public Boolean findWord(String word) {
		return root.findWord(word);
	}
	
	public List<String> getAllWordsWithPrefix(String prefix) {
		return root.getAllWordsWithPrefix(prefix);
	}
	
	class Node implements Serializable {
		
		private TreeMap<String,Node> outEdges;
		private Boolean containWord;
		
		private Node() {
			outEdges = new TreeMap<String,Node>();
			containWord = false;
		};
		
		public List<String> getAllWordsWithPrefix(String prefix) {
			
			List<String> res = new ArrayList<String>();
			
			/**
			 * We start by the ceiling string because we have the prefix
			 * and iterate all strings with prefix. If there exist a ceiling 
			 * string that contains the prefix, all the strings contained
			 * in the subtree need to appear in the answer.
			 */
			
			String key = this.outEdges.ceilingKey(prefix);
			
			if (key != null) {
			
				for(Map.Entry<String,Node> entry : this.outEdges.tailMap(key).entrySet()) {
				  if (!entry.getKey().contains(prefix)) break;
				  
				  List<String> tmpList = entry.getValue().getAllWordsWithPrefix("");
				  for (int i=0; i<tmpList.size(); i++) res.add(entry.getKey() + tmpList.get(i));
				}
				
			}
			else if (prefix.equals("")) {
				for(Map.Entry<String,Node> entry : this.outEdges.entrySet()) {
					  
					 List<String> tmpList = entry.getValue().getAllWordsWithPrefix("");
					 for (int i=0; i<tmpList.size(); i++) res.add(entry.getKey() + tmpList.get(i));
				}
					
			}
			
			/**
			 * Maybe the prefix does not appear completely in the edge, then
			 * we search it in the floor string.
			 * Example: 
			 * 		prefix: start
			 * 		edge: st
			 */
			
			key = this.outEdges.floorKey(prefix);
			
			if (key != null && !key.contains(prefix)) {
				int len = 0; //Length of the largest common prefix.
				for (int i=0; i<Math.min(key.length(), prefix.length()); i++) {
					if (key.charAt(i) == prefix.charAt(i)) {
						len++;
					}
					else break;
				}
				
				if (len == key.length()) {
					Node node1 = this.outEdges.get(key);
					
					String newPrefix = prefix.substring(len, prefix.length());
					List<String> tmpList = node1.getAllWordsWithPrefix(newPrefix);
					for (int i=0; i<tmpList.size(); i++) res.add(key + tmpList.get(i));
				}
			}
			
			if (this.getContainWord()) res.add("");
			
			return res;
			
		}

		public Boolean findWord(String word) {
			
			if (word.equals("")) {
				return this.getContainWord();
			}
			else {
			
				String key = this.outEdges.ceilingKey(word);
				
				if (key != null) {
					/**
					 * If there is a match in outEdges, the word has
					 * to be a prefix of the key or the key a prefix 
					 * of the word:
					 *  -	Word is a prefix of the key. If its equal return 
					 *  	the search of that node, else return false. We are searching an exact match.
					 *  -	The key is a prefix of the word. Continue searching.
					 *  
					 *  All other cases are false.
					 */
					
					int len = 0; //Length of the largest common prefix.
					for (int i=0; i<Math.min(key.length(), word.length()); i++) {
						if (key.charAt(i) == word.charAt(i)) {
							len++;
						}
						else break;
					}
					
					if (len == key.length() && len == word.length()) {
						Node node1 = this.outEdges.get(key);
						
						return node1.findWord("");
					}
					else if (len == key.length() && len < word.length()) {
						String newWord = "";
						if (len < word.length()) {
							newWord = word.substring(len, word.length());
						}
						
						Node node1 = this.outEdges.get(key);
						
						return node1.findWord(newWord);
						
					}
					
				}

				key = this.outEdges.floorKey(word);
					
				if (key != null) {
					/**
					 * The same as before with floor key
					 */
					int len = 0; //Length of the largest common prefix.
					for (int i=0; i<Math.min(key.length(), word.length()); i++) {
						if (key.charAt(i) == word.charAt(i)) {
							len++;
						}
						else break;
					}
					
					if (len == key.length() && len == word.length()) {
						Node node1 = this.outEdges.get(key);
							
						return node1.findWord("");
					}
					else if (len == key.length() && len < word.length()) {
						String newWord = "";
						if (len < word.length()) {
							newWord = word.substring(len, word.length());
						}
						
						Node node1 = this.outEdges.get(key);
						
						return node1.findWord(newWord);
						
					}
					else return false;
				}
			}
			
			return false;

		}


		public int countNodes() {
			if (this.outEdges.size()==0) return 1;
			else {
				
				int numberNodes = 1;
				
				for(Map.Entry<String,Node> entry : this.outEdges.entrySet()) {
				  String key = entry.getKey();
				  Node node = entry.getValue();

				  numberNodes += node.countNodes();
				}
				
				return numberNodes;
			}
		}

		public void insertWord(String word) {
						
			String key = this.outEdges.ceilingKey(word);
			
			Boolean ceilingNull = false;
			if (key == null) ceilingNull = true;
						
			if (key != null) {
				
				/**
				 * There four are basic cases:
				 * 	- 	There is no string with any common prefix to word.
				 * 		In this case we create a new edge with word and add a node 
				 * 		to the end of this edge. If outEdges is empty we have to 
				 * 		create also an empty edge.
				 * 
				 * 		Example:
				 * 			
				 * 			root -(test)--> Node1
				 * 
				 * 			when added "slow" transforms into
				 * 
				 * 			root -(test)--> Node1
				 * 				 -(slow)--> newNode1
				 * 
				 * 		This case is treated outside this conditional. We use
				 * 		flag ceilingNull.
				 * 	
				 * 		
				 *  -	The key and the word have a common prefix but its length is 
				 *  	smaller than both. In this case we have to substitute the
				 *  	edge for the prefix intersection and create two new edges 
				 *  	from a new node. One of the nodes will be equal to the node
				 *  	the previous edge was pointing to. The other node is new.
				 *  	
				 *  	Example:
				 *  		
				 *  		root -(test)--> Node1
				 *  
				 *  		when added "team" transforms into
				 *  
				 *  		root -(te)--> NewNode1 -(st)--> Node
				 *  							   -(am)--> NewNode2
				 *  
				 *  -	The word is contained in the key. We have to divide the key
				 *  	and insert a new node.
				 *  
				 *  	Example:
				 *  		
				 *  		root -(tested)--> Node1
				 *  
				 *  		when added "tested" transforms into
				 *  
				 *  		root -(test)--> Node1 -(ed)--> newNode1
				 *  
				 *  -	The key is contained in word. In this case we have to insert 
				 *  	in the node pointed by the edge the word without the key prefix.
				 *  
				 *  	Example:
				 *  		
				 *  		root -(test)--> Node1
				 *  
				 *  		when added "tested" transforms into
				 *  
				 *  		root -(test)--> Node1 -(ed)--> newNode1
				 *  
				 *  -	The two strings are equal. In this case we do nothing.
				 */
				
				
				int len = 0; //Length of the largest common prefix.
				for (int i=0; i<Math.min(key.length(), word.length()); i++) {
					if (key.charAt(i) == word.charAt(i)) {
						len++;
					}
					else break;
				}
				
				
				//No common prefix
				if (len==0) {
					
					ceilingNull = true;
					
				} 
				else if ((len < key.length() && len < word.length())) {
					

					//Find the new values for the new edges we are going to create.
					String commonPrefix = key.substring(0, len);
					String newKey = "";
					if (len < key.length()) {
						newKey = key.substring(len, key.length());
					}
					
					String newWord = "";
					if (len < word.length()) {
						newWord = word.substring(len, word.length());
					}
					
					
					Node node1 = this.outEdges.get(key);
					
					Node newNode1 = new Node();
					Node newNode2 = new Node();
					
					//Erase the previous edge. The pointer to the node is stored in
					//Node1 and will be used later.
					
					this.eraseEdge(key);
										
					this.addEdge(commonPrefix, newNode1);

					newNode1.addEdge(newKey, node1);
					newNode1.addEdge(newWord, newNode2);
					newNode2.setContainWord(true);
					

				}
				else if (len < key.length() && len == word.length()) {
					
					//Find the new values for the new edges we are going to create.
					String commonPrefix = key.substring(0, len);
					String newKey = "";
					if (len < key.length()) {
						newKey = key.substring(len, key.length());
					}
					
					Node node1 = this.outEdges.get(key);
					
					Node newNode1 = new Node();
					
					//Erase the previous edge. The pointer to the node is stored in
					//Node1 and will be used later.
					
					this.eraseEdge(key);
					
					this.addEdge(commonPrefix, newNode1);

					newNode1.addEdge(newKey, node1);

					newNode1.setContainWord(true);
				}
				else if (len == key.length() && len < word.length()) {
					
					String newWord = "";
					if (len < word.length()) {
						newWord = word.substring(len, word.length());
					}
					
					Node node1 = this.outEdges.get(key);
					node1.insertWord(newWord);
					
				}
				
			}

			/**
			 * Same as before with floor key
			 */
			key = this.outEdges.floorKey(word);
			
			boolean floorNull = false;
			
			if (key == null) floorNull = true;
								
			if (key != null) {
				
				int len = 0; //Length of the largest common prefix.
				for (int i=0; i<Math.min(key.length(), word.length()); i++) {
					if (key.charAt(i) == word.charAt(i)) {
						len++;
					}
					else break;
				}
				
				//No common prefix
				if (len==0) {
					
					floorNull = true;
				} 
				else if ((len < key.length() && len < word.length())) {
					
					//Find the new values for the new edges we are going to create.
					String commonPrefix = key.substring(0, len);
					String newKey = "";
					if (len < key.length()) {
						newKey = key.substring(len, key.length());
					}
					
					String newWord = "";
					if (len < word.length()) {
						newWord = word.substring(len, word.length());
					}
					
					
					Node node1 = this.outEdges.get(key);
					
					Node newNode1 = new Node();
					Node newNode2 = new Node();
					
					//Erase the previous edge. The pointer to the node is stored in
					//Node1 and will be used later.
					
					this.eraseEdge(key);
					
					this.addEdge(commonPrefix, newNode1);
					newNode1.addEdge(newKey, node1);
					newNode1.addEdge(newWord, newNode2);
					newNode2.setContainWord(true);
					
				}
				else if (len < key.length() && len == word.length()) {
					
					//Find the new values for the new edges we are going to create.
					String commonPrefix = key.substring(0, len);
					String newKey = "";
					if (len < key.length()) {
						newKey = key.substring(len, key.length());
					}
					
					Node node1 = this.outEdges.get(key);
					
					Node newNode1 = new Node();
					
					//Erase the previous edge. The pointer to the node is stored in
					//Node1 and will be used later.
					
					this.eraseEdge(key);
					
					this.addEdge(commonPrefix, newNode1);

					newNode1.addEdge(newKey, node1);

					newNode1.setContainWord(true);
				}
				else if (len == key.length() && len < word.length()) {
					
					String newWord = "";
					if (len < word.length()) {
						newWord = word.substring(len, word.length());
					}
					
					Node node1 = this.outEdges.get(key);
					node1.insertWord(newWord);
					
				}
			}

				 /**
				  * If there are no edges with this information we create a new one
				  */
				if (ceilingNull && floorNull) {
					Node newNode1 = new Node();
					this.outEdges.put(word, newNode1);
					newNode1.setContainWord(true);
				}

		}

		private void setContainWord(boolean b) {
			this.containWord = b;
		}
		
		private Boolean getContainWord() {
			return this.containWord;
		}

		/**
		 * Add a new edge to the node.
		 * @param key
		 * @param node Pointer to the node
		 */
		private void addEdge(String key, Node node) {

			outEdges.put(key, node);
			
		}

		/**
		 * Remove the key form outgoing edges. 
		 * The node pointer still exists, only removes the key.
		 * @param key
		 */
		private void eraseEdge(String key) {
			
			outEdges.remove(key);
			
		}
	}
	
}
