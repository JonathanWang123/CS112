package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String s, String d) {
		if(s.equals(d)) {
			return null;
		}
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
	    boolean[] isVisited = new boolean[g.members.length]; 
	    ArrayList<String> pathList = new ArrayList<>(); 
	      
	    //add source to path[] 
	    pathList.add(s); 
	      
	    //Call recursive utility 
	    findAllPathsUtil(list,g,s, d, isVisited, pathList); 
	    
	    if(list.size()==0) {
	    	return new ArrayList<String>();
	    }
	    int index = 0;
	    int minLen = Integer.MAX_VALUE;
	    for(int i=0; i<list.size();i++) {
	    	if(list.get(i).size()<minLen) {
	    		index = i;
	    		minLen = list.get(i).size();
	    	}
	    }
	    return list.get(index);
	}
	private static void findAllPathsUtil(ArrayList<ArrayList<String>> list,Graph g, String u, String d, boolean[] isVisited, ArrayList<String> localPathList) { 
			// Mark the current node 
				int index = 0;
			for(int i=0;i<g.members.length;i++) {
				if(g.members[i].name.equals(u)) {
					index = i;
				}
			}
			isVisited[index] = true; 
	
			if (u.equals(d)){ 
				ArrayList<String> temp= new ArrayList<String>();
				for (String s: localPathList) {
					temp.add(s);
				}
				list.add(temp);
				// if match found then no need to traverse more till depth 
				isVisited[index]= false; 
				return; 
			} 
	
			// Recur for all the vertices 
			// adjacent to current vertex 
			Friend f = g.members[index].first;
			while(f!=null) {
				if (!isVisited[f.fnum]) 
				{ 
					// store current node in path[] 
					localPathList.add(g.members[f.fnum].name); 
					findAllPathsUtil(list,g,g.members[f.fnum].name, d, isVisited, localPathList); 
					// remove current node in path[] 
					localPathList.remove(g.members[f.fnum].name);  
				}
				f=f.next;
			}
			// Mark the current node 
			isVisited[index] = false; 
		}

	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
		ArrayList<String> usedNames = new ArrayList<String>();
		for(int i = 0; i<g.members.length;i++) {
			if(g.members[i].school != null && g.members[i].school.equals(school) && !isInStringArray(g.members[i].name,usedNames)) {
				ArrayList<String> clique = new ArrayList<String>();
				clique.add(g.members[i].name);
				usedNames.add(g.members[i].name);
				visitFriends(g.members[i].first,usedNames,clique,g,school);
				ans.add(clique);
			}
		}
		
		return ans;
		
	}
	public static boolean isInStringArray(String s, ArrayList<String> a) {
		for (String str: a) {
			if(str.equals(s)) {
				return true;
			}
		}
		return false;
	}
	public static void visitFriends(Friend f, ArrayList<String> used, ArrayList<String> clique, Graph g, String school) {
		int count = 0;
		while(f!=null) {
			if(g.members[f.fnum].school != null && g.members[f.fnum].school.equals(school) && !isInStringArray(g.members[f.fnum].name,used)) {
				clique.add(g.members[f.fnum].name);
				used.add(g.members[f.fnum].name);
				visitFriends(g.members[f.fnum].first,used,clique,g,school);
				count++;
			}
			f=f.next;
		}
		if(count==0) {
			return;
		}
	}	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		ArrayList<String> ans = new ArrayList<String>();
		for(int i=0; i<g.members.length;i++) {
			for(int j=0; j<g.members.length;j++) {
				if(i!=j) {
					ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
				    boolean[] isVisited = new boolean[g.members.length]; 
				    ArrayList<String> pathList = new ArrayList<>(); 
				      
				    //add source to path[] 
				    pathList.add(g.members[i].name); 
				      
				    //Call recursive utility 
				    findAllPathsUtil(list,g,g.members[i].name, g.members[j].name, isVisited, pathList); 
				    for(int k=0;k<list.size();k++) {
				    	list.get(k).remove(0);
				    	list.get(k).remove(list.get(k).size()-1);
				    }
				    if(list.size()>0) {
					    ArrayList<String> common = list.get(0);
					    for(int k=1;k<list.size();k++) {
					    	common.retainAll(list.get(k));
					    }
					    for(String s:common) {
					    	ans.add(s);
					    }
				    }
				}
			}
		}
		ArrayList<String> finalAns = new ArrayList<String>();
		for(String s:ans) {
			if(!finalAns.contains(s)) {
				finalAns.add(s);
			}
		}
		return finalAns;
	}
}

