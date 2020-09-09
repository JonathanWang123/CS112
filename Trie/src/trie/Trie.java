package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	private static int commonPrefixIndex(String a, String b) {
		int count = -1;
		int minLength = Math.min(a.length(), b.length());
	    for (int i = 0; i < minLength; i++) {
	        if (a.charAt(i) != b.charAt(i)) {
	            break;
	        }
	        count++;
	    }
	    return count;
	}
	private static TrieNode findPlace(TrieNode start, String word,String[] allWords) {
		TrieNode compareNode = start;
		TrieNode prev = compareNode;
		for(compareNode = start;compareNode!=null;compareNode = compareNode.sibling) {
			int commonPrefix = commonPrefixIndex(allWords[compareNode.substr.wordIndex].substring(compareNode.substr.startIndex,compareNode.substr.endIndex+1),word.substring(compareNode.substr.startIndex));
		    
			if(commonPrefix != -1) {
				if(compareNode.firstChild == null) {
					Indexes newChildIndex = new Indexes(compareNode.substr.wordIndex,(short)(compareNode.substr.startIndex+commonPrefix+1),compareNode.substr.endIndex);
					if(compareNode.substr.startIndex>=compareNode.substr.endIndex) {
						newChildIndex = new Indexes(compareNode.substr.wordIndex,(short)(compareNode.substr.startIndex+commonPrefix),compareNode.substr.endIndex);
					}
					TrieNode newChild = new TrieNode(newChildIndex,null,null);
					
					Indexes replacementIndex = new Indexes(compareNode.substr.wordIndex,(short)(compareNode.substr.startIndex),(short)(compareNode.substr.startIndex+commonPrefix));
					compareNode.substr = replacementIndex;
					compareNode.firstChild = newChild;
					return newChild;
				} else{
					String prefix = allWords[compareNode.substr.wordIndex].substring(compareNode.substr.startIndex,compareNode.substr.endIndex+1);
					if(word.substring(compareNode.substr.startIndex).indexOf(prefix) == 0) {
						return findPlace(compareNode.firstChild,word,allWords);
					} else {
						Indexes newChildIndex = new Indexes(compareNode.substr.wordIndex,(short)(compareNode.substr.startIndex+commonPrefix+1),compareNode.substr.endIndex);
						if(compareNode.substr.startIndex>=compareNode.substr.endIndex) {
							newChildIndex = new Indexes(compareNode.substr.wordIndex,(short)(compareNode.substr.startIndex+commonPrefix),compareNode.substr.endIndex);
						}
						TrieNode newChild = new TrieNode(newChildIndex,compareNode.firstChild,null);
						
						Indexes replacementIndex = new Indexes(compareNode.substr.wordIndex,(short)(compareNode.substr.startIndex),(short)(compareNode.substr.startIndex+commonPrefix));
						compareNode.substr = replacementIndex;
						compareNode.firstChild = newChild;
						return newChild;
					}
				}
				
			}
			prev = compareNode;
		}
		return prev;
	}
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null,null,null);
		
		Indexes firstWordIndex = new Indexes(0,(short)0,(short)(allWords[0].length()-1));
		TrieNode firstWord = new TrieNode(firstWordIndex,null,null);
		root.firstChild = firstWord;

		for(int i = 1; i < allWords.length; i++) {
			TrieNode newNode = findPlace(firstWord,allWords[i],allWords);
			Indexes newWordIndex = new Indexes(i,newNode.substr.startIndex,(short)(allWords[i].length()-1));
			TrieNode addNode = new TrieNode(newWordIndex,null,null);
			newNode.sibling = addNode;
		}

		return root;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		for(TrieNode pos = root.firstChild; pos!= null; pos = pos.sibling) {
			if(pos.firstChild == null && isPrefixIn(pos,prefix,allWords)) {
				list.add(pos);
			} else if(pos.firstChild != null && isPrefixIn(pos,prefix,allWords)) {
				ArrayList<TrieNode> hold = (completionList(pos,allWords,prefix));
				for(TrieNode n: hold) {
					list.add(n);
				}
			}
		}
		return list;
	}
	private static boolean isPrefixIn(TrieNode n, String prefix, String[]allWords) {
		String hold = allWords[n.substr.wordIndex].substring(0,n.substr.endIndex+1);
		if(hold.length() < prefix.length()) {
			if(hold.equals(prefix.substring(0,hold.length()))){
				return true;
			}
		}
		if(hold.indexOf(prefix) == 0) {
			return true;
		} else {
			return false;
		}
	}
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
