package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import controller.GameController;
import model.Card;
import model.Context;

public class AI_Agent {
	
	private static ArrayList<Integer> ranks;
	private static ArrayList<Integer> values;
	private static int score;
	
	public static ArrayList<Integer> chosen;
	
	public static int calcPoints(ArrayList<Integer> hand){
		Collections.sort(hand, Context.getInstance().getC());
		/*ArrayList<Integer> */ranks = new ArrayList<Integer>(hand);
    	//Collections.copy(ranks, stack);
    	for(int j = 0; j < ranks.size(); j++)
    	{ ranks.set(j, hand.get(j)%13); }
    	
    	//ArrayList<Integer> 
    	values = new ArrayList<Integer>(hand);
    	for(int j = 0; j < values.size(); j++)
    	{ values.set(j, Card.getValue(hand.get(j))); }
    	
    	//count 15's
    	/*int num = 0;
    	for(int i = 0; i < hand.size(); i++){
    	}*/
    	score = 0;
    	count15(0,0);
    	//System.out.println(score);
    	
    	//check for pairs
    	for(int i = 0; i< ranks.size(); i++){
    		//int matches = 0;
    		for(int j = i+1; j<ranks.size(); j++){
    			if(ranks.get(i).equals(ranks.get(j))){
    				//matches++;
    				score += 2;
    			}
    		}
    		//System.out.println(matches + "matches");
    	}
    	System.out.println("Score from pairs: " + score);
    	
    	Collections.sort(ranks);
    	int[] arr = new int[ranks.size()];
		for (int i = 0; i < arr.length; i++) {
		    arr[i] = ranks.get(i);
		}
    	int runs = checkRuns(arr, 0, 1);
    	System.out.println("Score from runs: " + runs);
    	score += runs;
    	
    	//check for right jack
    	int c = GameController.cutCard;
    	if(c != -1)
    	{
    		if(hand.contains(Integer.valueOf(c))){
    			hand.removeIf(n -> (n == c));
    		}
    		for(int i = 0; i < hand.size(); i++){
    			if(Card.getSuit(c) == Card.getSuit(hand.get(i)) && Card.getName(hand.get(i)) == 'J'){
    				System.out.println("Right Jack.");
    				score +=1;
    				i = hand.size();
    			}
    		}
    	}
    	
    	//search for flush
    	char suit = Card.getSuit(hand.get(0));
    	int counter=1;
    	for(int i = 1; i < hand.size(); i++){
			if(suit == Card.getSuit(hand.get(i))){
				counter++;
			}
		}
    	if(counter == 4){
    		if(Card.getSuit(c) == suit)
    			counter = 5;
    		if(counter == 5 || (counter == 4 && GameController.state != 3)){
    			System.out.println("Score from flush: " + counter);
    			score += counter;
    		}
    	}
    	
		return score;
	}
	/* Recursively find all combinations that add up to fifteen.
	 */
	static void count15(int j, int total)
	{
	    for ( ; j < values.size() ; ++j) {
	        int subtotal = total + values.get(j);
	        if (subtotal == 15)
	            score += 2;
	        else if (subtotal < 15)
	        	count15(j + 1, subtotal);
	    }
	}
	//recursively find runs
	static int checkRuns(int[] arr, int j, int len){
		if(j+1 < arr.length){
			if(/*(j+1 < arr.length) &&*/ arr[j]+1 == arr[j+1]){
				return checkRuns(arr, j+1, len+1);
			}
			else if(arr[j] == arr[j+1]){
				return checkRuns(arr, j+1, len)*2;
			}
			else {//if(j+1 < arr.length){
				len = 1;
				return checkRuns(arr, j+1, len);
			}
		}
			/*else
				return len;*/
		if(len >= 3)
			return len;
		else
			return 0;
	}
	public static int checkRuns2(int[] arr, int j, int len){
		if(j+1 < arr.length && arr[j]+1 == arr[j+1]){
			return checkRuns(arr, j+1, len+1);
		}
			/*else
				return len;*/
		if(len >= 3)
			return len;
		else
			return 0;
	}
	
	public static int pickCrib(ArrayList<Integer> hand){
		chosen = new ArrayList<Integer>();
		int max = 0;
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for(int i = 0; i < hand.size(); i++){
			ArrayList<Integer> temp = new ArrayList<Integer>(hand);
			Collections.copy(temp, hand);
			temp.remove(i);
			for(int j = 0; j < temp.size(); j++){
				ArrayList<Integer> temp2 = new ArrayList<Integer>(temp);
				Collections.copy(temp2, temp);
				temp2.remove(j);
				int p = calcPoints(temp2);
				if(p>max){
					max = p;
					arr.add( hand.get(i));
					arr.add( temp.get(j));
				}
			}
		}
		if(arr.isEmpty() || arr.get(0).equals(null)){
			Random r = new Random();
			int p = r.nextInt(hand.size()+1);
			chosen.add( hand.get(p));
			hand.remove(p);
			p = r.nextInt(hand.size()+1);
			chosen.add( hand.get(p));
			hand.remove(p);
		}
		else{
			chosen.add( arr.get(0));
			chosen.add( arr.get(1));
		}
		return max;
	}
	
	//return card it picks, or -1 for a go
	public static int pickCount(ArrayList<Integer> hand, ArrayList<Integer> stack, int count){
		for(Integer i : hand){
			if(!GameController.revealed.contains(i) && checkValidCount(i, count)){
				return i;
			}
		}
		return -1;
	}
	
	public static boolean checkValidCount(int i, int count){
    	int val = Card.getValue(i);
    	return (val + count) <= 31;
    }
}
