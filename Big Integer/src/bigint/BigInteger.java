
package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
	throws IllegalArgumentException {
		String str = integer.trim();
		BigInteger parsed = new BigInteger();
		parsed.negative = str.charAt(0) == '-';
		if(str.charAt(0) == '-' || str.charAt(0) == '+') {
			str = str.substring(1);
		}
		for(int i = 0; i < str.length(); i++) {
			if(str.length() != 1 && str.charAt(i) == '0') {
				str = str.substring(1);
				i--;
			}
			else{
				break;
			}
		}
		for(int i = 0; i < str.length(); i++) { 
			if(Character.isDigit(str.charAt(i)) == false){
				throw new IllegalArgumentException();
			}
		}
		parsed.numDigits = str.length();
		if(str.equals("0")) {
			return new BigInteger();
		}
		DigitNode tail = new DigitNode(Character.getNumericValue(str.charAt(0)),null);
		str = str.substring(1);
		DigitNode temp;
		for(int i = 0; i < str.length(); i++) {
			temp = tail;
			tail = new DigitNode(Character.getNumericValue(str.charAt(i)),temp);
		}
		parsed.front = tail;
		return parsed;
	}
	
	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {
		if(first.front ==null && second.front == null) {
			return new BigInteger();
		}
		if(first.negative == second.negative) {
			DigitNode sum = new DigitNode(0,null);
			DigitNode temp1;
			int length;
			if(first.numDigits >= second.numDigits) {
				length = first.numDigits;
			} else {
				length = second.numDigits;
			}
			for(int i = 0; i<length ;i++) {
				temp1 = sum;
				sum = new DigitNode(0,temp1); //Make sum a list of zeros
			}
			int carry;
			int ab;
			DigitNode a = first.front;
			DigitNode b = second.front;
			DigitNode temp2 = sum;
			for(int i = 0; i<length; i++) {
					ab = (a.digit + b.digit)%10;
					carry = (temp2.digit + a.digit + b.digit)/10;
					temp2.digit += ab;
					temp2.digit %= 10;
					temp2.next.digit = carry;
					if(b.next == null && a.next != null) {
						a = a.next;
						temp2 = temp2.next;
						for(int j = i+1; j<length;j++) {
							carry = (temp2.digit + a.digit)/10;
							temp2.digit += a.digit;
							temp2.digit = temp2.digit%10;
							temp2.next.digit = carry;
							a = a.next;
							DigitNode trackZero = temp2;
							temp2 = temp2.next;
							if(temp2.next==null && temp2.digit == 0) {
								trackZero.next = null;
							}
						}
						break;
					} else if(a.next == null && b.next != null) {
						b = b.next;
						temp2 = temp2.next;
						for(int j = i+1; j<length;j++) {
							carry = (temp2.digit + b.digit)/10;
							temp2.digit += b.digit;
							temp2.digit = temp2.digit%10;
							temp2.next.digit = carry;
							b = b.next;
							DigitNode trackZero = temp2;
							temp2 = temp2.next;
							if(temp2.next == null && temp2.digit == 0) {
								trackZero.next = null;
							}
						}
						break;
					} else {
					a = a.next;
					b = b.next;
					DigitNode trackZero = temp2;
					temp2 = temp2.next;
					if(temp2.next == null && temp2.digit == 0) {
						trackZero.next = null;
					}
					}
			}
			BigInteger summation = new BigInteger();
			summation.front = sum;
			summation.negative = first.negative;
			int count = 0;
			for(DigitNode temp = summation.front; temp != null; count++) {
				temp = temp.next;
			}
			summation.numDigits = count;
			return summation;
		} else {
			BigInteger ans = new BigInteger();
			if(isEqual(first,second)) {
				return new BigInteger();
			} else if(absIsGreater(first,second)) {
				ans.negative = first.negative;
				DigitNode one = first.front;
				DigitNode two = appendZero(second.front,first.numDigits);
				DigitNode sum = appendZero(new DigitNode(0,null),first.numDigits);
				DigitNode temp = sum;
				int carry = 0;
				for(int i = 0; i < first.numDigits; i++) {
					if(one.digit == 0 && two.digit !=0) {
						one.digit = (10 - carry);
						carry = 1;
					} else {
						one.digit = one.digit - carry;
						carry = 0;
					}
					if(one.digit > two.digit) {
						temp.digit = one.digit - two.digit;
					} else if(one.digit < two.digit) {
						temp.digit = ((10+one.digit) - (two.digit))%10;
						carry += 1;
					} else {
						temp.digit = 0;
					}
					one = one.next;
					two = two.next;
					temp = temp.next;
				}
				
				temp = sum; 
				DigitNode trackZero = temp;
				while(temp.next != null) {
					trackZero = temp;
					temp = temp.next;
					if(temp.digit == 0 && temp.next == null) {
						trackZero.next = null;
					}
				}
				ans.front = trailingZero(sum);
				int count = 0;
				for(DigitNode temp2 = ans.front; temp2 != null; count++) {
					temp2 = temp2.next;
				}
				ans.numDigits = count;
				
				return ans;
			} else {
				ans.negative = second.negative;
				DigitNode one = second.front;
				DigitNode two = appendZero(first.front,second.numDigits);
				DigitNode sum = appendZero(new DigitNode(0,null),second.numDigits);
				DigitNode temp = sum;
				int carry = 0;
				for(int i = 0; i < second.numDigits; i++) {
					if(one.digit == 0 && two.digit !=0) {
						one.digit = (10 - carry);
						carry = 1;
					} else {
						one.digit = one.digit - carry;
						carry = 0;
					}
					if(one.digit > two.digit) {
						temp.digit = one.digit - two.digit;
					} else if(one.digit < two.digit) {
						temp.digit = ((10+one.digit) - (two.digit))%10;
						carry += 1;
					} else {
						temp.digit = 0;
					}
					one = one.next;
					two = two.next;
					temp = temp.next;
				}
				
				temp = sum; 
				DigitNode trackZero = temp;
				while(temp.next != null) {
					trackZero = temp;
					temp = temp.next;
					if(temp.digit == 0 && temp.next == null) {
						trackZero.next = null;
					}
				}
				ans.front = trailingZero(sum);
				int count = 0;
				for(DigitNode temp2 = ans.front; temp2 != null; count++) {
					temp2 = temp2.next;
				}
				ans.numDigits = count;
				
				return ans;
			}
		}
	}
	private static DigitNode appendZero(DigitNode head, int desiredLen) {
		DigitNode a = head;
		DigitNode b = new DigitNode(0,null);
		DigitNode temp = new DigitNode(0,null);
		if(desiredLen > 0) {
			for(int i = 0; i<desiredLen;i++) {
				temp = b;
				b = new DigitNode(0,temp); //Make newHead a list of zeros
			}
			DigitNode newHead = temp;
			for(int i = 0; i < desiredLen; i++) {
				if(a != null && temp != null) {
					temp.digit = a.digit;
					a = a.next;
					temp = temp.next;
				}
			}
			return newHead;
		} else {
			return head;
		}
	}
	private static DigitNode reverseList(DigitNode n) {
		int count = 0;
		DigitNode temp = n;
		while(temp != null) {
			count ++;
			temp = temp.next;
		}
		DigitNode head = appendZero(new DigitNode(0,null), count);
		DigitNode headTemp = head;
		temp = n;
		for(int i = 0; i<count; i++) {
			headTemp.digit = temp.digit;
			headTemp = headTemp.next;
			temp = temp.next;
		}
		DigitNode curr = head;
		DigitNode next = null;
		DigitNode prev = null;
		while(curr != null) {
			next = curr.next;
			curr.next = prev;
			prev = curr;
			curr = next;
		}
		head = prev;
		return head;
	}
	private static Boolean absIsGreater(BigInteger first, BigInteger second) {
		if(first.numDigits > second.numDigits) {
			return true;
		} else if(first.numDigits < second.numDigits) {
			return false;
		} else {
			DigitNode one = reverseList(first.front);
			DigitNode two = reverseList(second.front);
			for(int i = 0; i < first.numDigits; i++) {
				if(one.digit > two.digit) {
					return true;
				} else if(one.digit < two.digit) {
					return false;
				} else {
					one = one.next;
					two = two.next;
				}
			}
		}
		return false;
	}
	private static Boolean isEqual(BigInteger first, BigInteger second) {
		if(first.numDigits != second.numDigits) {
			return false;
		}else {
			DigitNode one = (first.front);
			DigitNode two = (second.front);
			for(int i = 0; i < first.numDigits; i++) {
				if(one.digit != two.digit) {
					return false;
				} 
				one = one.next;
				two = two.next;
			}
		}
		return true;
	}
	private static DigitNode trailingZero(DigitNode node) {
		DigitNode prev = new DigitNode(0,null);
		for(DigitNode temp = node; temp!=null;temp = temp.next) {
			if(temp.next == null && temp.digit == 0) {
				prev.next = null;
				trailingZero(node);
			}
			prev = temp;
		}
		return node;
	}
	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {
				BigInteger ans = new BigInteger();
				if(first.front == null||second.front == null){
					return new BigInteger();
				}
				ans.negative = !(first.negative == second.negative);
				int maxLength = first.numDigits + second.numDigits;
				DigitNode product = appendZero(new DigitNode(0,null),maxLength);
				ans.front = product;
				ans.numDigits = maxLength;
				
				DigitNode one = first.front;
				for(int i = 0; i < first.numDigits; i++) {
				DigitNode subProduct = appendZero(new DigitNode(0,null),second.numDigits+1+i);
				DigitNode temp = subProduct;
					for(int startingZeros = 0; startingZeros < i; startingZeros++) {
						temp = temp.next;
					}
					DigitNode two = second.front;
					for(int j = 0; j<second.numDigits; j++) {	
						temp.digit += (one.digit*two.digit);
						temp.next.digit = temp.digit/10;
						temp.digit = temp.digit%10;
						temp = temp.next;
						two = two.next;
					}
					BigInteger mult = new BigInteger();
					mult.numDigits = second.numDigits+1+i;
					mult.negative = ans.negative;
					mult.front = subProduct;
					ans = add(ans, mult);
					one = one.next;
					}
				DigitNode temp3 = ans.front;
				DigitNode trackZero = temp3;
				while(temp3.next != null) {
					trackZero = temp3;
					temp3 = temp3.next;
					if(temp3.digit == 0 && temp3.next == null) {
						trackZero.next = null;
					}
				}
				int count = 0;
				for(DigitNode temp4 = ans.front; temp4 != null; count++) {
					temp4 = temp4.next;
				}
				ans.numDigits = count;
				return ans;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
}

