package model;

public class Card {
	
//	private int card; //number of card in sorted deck, 0-51 from A of Spades to K of Diamonds
//	private int value; //value of card for counting 1-10
//	private char name; //name of card face, A, 2-9, T, J, Q, or K
//	private char suit; //suit: S, H, C, D
	
	private static char[] valueArray = new char[] {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K'};
    private static char[] suitArray = new char[] { 'S', 'H', 'C', 'D' };
	
	/*public Card(int i){
		card = i;
		value = getValue(i);
		name = getName(i);
		suit = getSuit(i);
	}*/

	public static int getValue(int i) {
		int temp = i%13; //gets position in suit 0-12
		temp++;
		if(temp >= 10)
			return 10;
		else
			return temp;
	}

	public static char getName(int i) {
		int temp = i%13; //gets position in suit 0-12
		return valueArray[temp];
	}

	public static char getSuit(int i) {
		int temp = (int)(i/13);
		return suitArray[temp];
	}
	
	public static String toString(int i){
		String out = "";
		char n = getName(i);
		if(n == 'A')
			out = "Ace";
		else if(n == 'T')
			out = "10";
		else if(n == 'J')
			out = "Jack";
		else if(n == 'Q')
			out = "Queen";
		else if(n == 'K')
			out = "King";
		else
			out += String.valueOf(n);
		
		out += " of ";
		char s = getSuit(i);
		if(s == 'S')
			out += "Spades";
		else if(s == 'H')
			out += "Hearts";
		else if(s == 'C')
			out += "Clubs";
		else if(s == 'D')
			out += "Diamonds";
		else
			out += String.valueOf(s);
		
		return out;
	}
	

//	public int getCard() {
//		return card;
//	}
//
//	public int getValue() {
//		return value;
//	}
//
//	public char getName() {
//		return name;
//	}
//
//	public char getSuit() {
//		return suit;
//	}

}
