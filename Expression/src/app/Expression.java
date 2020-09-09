package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	StringTokenizer tokenize = new StringTokenizer(expr,delims);
    	while(tokenize.hasMoreTokens()) {
    		String temp = tokenize.nextToken();
    		if(temp.matches("^[a-zA-Z]*$")) {
	    		if(expr.indexOf(temp)+temp.length() < expr.length() && expr.charAt(expr.indexOf(temp)+temp.length()) == '[') {
	    			boolean check = true;
	    			for(int i = 0; i < arrays.size(); i++) {
	    				if(arrays.get(i).name.equals(temp)) {
	    					check = false;
	    				}
	    			}
	    			if(check) {
	    				Array add = new Array(temp);
	    				arrays.add(add);
	    			}
	    		} else {
	    			boolean check = true;
	    			for(int i = 0; i < vars.size(); i++) {
	    				if(vars.get(i).name.equals(temp)) {
	    					check = false;
	    				}
	    			}
	    			if(check) {
	    				Variable add = new Variable(temp);
	    				vars.add(add);
	    			}
	    		}
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr = expr.replaceAll("\\s","");
    	ArrayList<String> expression = new ArrayList<String>();
    	Stack<Float> values = new Stack<Float>();
    	Stack<String> operators = new Stack<String>();
    	
    	int temp = 0;
    	int count = 0;
    	for(int i = 0; i < expr.length(); i++) {
    		if(expr.charAt(i) == '[') {
    			for(int j = i; j < expr.length(); j++) {
    				if(expr.charAt(j) == '[') {
    					count++;
    				}
    				if(expr.charAt(j) == ']') {
    					count--;
    				}
    				if(expr.charAt(j) == ']' && count==0) {
    					i = j;
    					break;
    				}
    			}
    		}
    		if(expr.charAt(i) == '+' || expr.charAt(i) == '-' || expr.charAt(i) == '*' || expr.charAt(i) == '/' || expr.charAt(i) == '(' || expr.charAt(i) == ')') {
    			if(temp == i) {
    				expression.add(expr.substring(i,i+1));
    				temp = i+1;
    			} else {
	    			expression.add(expr.substring(temp,i));
	    			expression.add(expr.substring(i,i+1));
	    			temp = i+1;
    			}
    		}       		
    	}
    	if(temp!=expr.length()) {
    		expression.add(expr.substring(temp));
    	}
    	
    	for(int i = 0; i < expression.size(); i++) {
    		if(expression.get(i).matches("^[0-9]*$") || expression.get(i).matches("^\\d*\\.\\d+|\\d+\\.\\d*$")) {
    			values.push(Float.parseFloat(expression.get(i)));
    		} else if(expression.get(i).matches("^[a-zA-Z]*$")) {
    				//Variable

    				for(int j = 0; j< vars.size(); j++) {
    					if(vars.get(j).name.equals(expression.get(i))) {
    						values.push((float)vars.get(j).value);
    	
    					}
    				}
    		} else if(expression.get(i).indexOf('[') != -1){
    				//Array
    				String arrayName = expression.get(i).substring(0,expression.get(i).indexOf("["));
    				int finalIndex = 0;
    	    		for(int j = 0; j < expression.get(i).length(); j++) {
    	    			if(expression.get(i).charAt(j) == '[') {
    	    				count++;
    	    			}
    	    			if(expression.get(i).charAt(j) == ']') {
    	    				count--;
    	    			}
    	   				if(expression.get(i).charAt(j) == ']' && count==0) {
    	   					finalIndex = j;
    	   					break;
    	   				}
        			}
    				String arrayIndex = expression.get(i).substring(expression.get(i).indexOf("[")+1,finalIndex);
    				for(int j = 0; j < arrays.size(); j++) {
    					if(arrays.get(j).name.equals(arrayName)) {
    						values.push((float)arrays.get(j).values[(int)evaluate(arrayIndex,vars,arrays)]);
    					}
    				}
    		} else if(expression.get(i).equals("(")) {
    			operators.push(expression.get(i));
    		} else if(expression.get(i).equals(")")) {
    			while(!operators.peek().equals("(")) {
    				values.push(performOperation(operators.pop(),values.pop(),values.pop()));
    			}
    			operators.pop();
    		} else if(expression.get(i).equals("+") || expression.get(i).equals("-") || expression.get(i).equals("*") || expression.get(i).equals("/")) {
    			while(!operators.isEmpty() && pemdas(expression.get(i), operators.peek())) {
    				values.push(performOperation(operators.pop(),values.pop(),values.pop()));
    			}
    			operators.push(expression.get(i));
    		}
    	}
    	while(!operators.isEmpty()) {
    		values.push(performOperation(operators.pop(),values.pop(),values.pop()));
    	}
    	return values.pop();
    }
    private static boolean pemdas(String a, String b) {
    	if(b.equals("(")|| b.equals(")")) return false;
    	else if((a.equals("*")||a.equals("/")) && (b.equals("+")|| b.equals("-"))) return false;
    	else return true;
    }
    private static float performOperation(String operator, float a, float b) {
    	if(operator.equals("+")) return b+a;
    	else if(operator.equals("-")) return b-a;
    	else if(operator.equals("*")) return b*a;
    	else if(operator.equals("/")) return b/a;
    	else return 0;
    }
}
