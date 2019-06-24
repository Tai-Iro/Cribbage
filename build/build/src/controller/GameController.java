package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import application.AI_Agent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
//import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import model.Card;
import model.Context;
import javafx.event.ActionEvent;

public class GameController {

    @FXML private FlowPane aiHandPane;
    @FXML private Label aiHandLabel;
    @FXML private ImageView aiCard0;
    @FXML private ImageView aiCard1;
    @FXML private ImageView aiCard2;
    @FXML private ImageView aiCard3;
    @FXML private ImageView aiCard4;
    @FXML private ImageView aiCard5;
    
    @FXML private FlowPane userHandPane;
    @FXML private ImageView userCard0;
    @FXML private ImageView userCard1;
    @FXML private ImageView userCard2;
    @FXML private ImageView userCard3;
    @FXML private ImageView userCard4;
    @FXML private ImageView userCard5;
    
    @FXML private AnchorPane middlePane;
    
    @FXML private AnchorPane scorePane;
    @FXML private Label userScoreLabel;
    @FXML private Label aiScoreLabel;
    
    @FXML private Label deckLabel;
    @FXML private ImageView deckImg;
    
    @FXML private AnchorPane controlsPane;
    @FXML private Button sortBtn;
    @FXML private Button shuffleBtn;
    @FXML private Button clearLogBtn;
    @FXML private Button acceptBtn;
    @FXML private Button tutorialBtn;
    
    @FXML private Label logLabel;
    @FXML private TextArea logTextArea;
    
    @FXML private MenuItem newGameBtn;
    @FXML private MenuItem saveBtn, loadBtn;
    @FXML private MenuItem exitBtn;
    @FXML private MenuItem tutorialMenu, aboutBtn;
    @FXML private MenuItem debugBtn;
    
    private boolean debug;// = false;

    private int aiScore, userScore;
    public ArrayList<Integer> deck;// = new ArrayList(Arrays.asList(IntStream.rangeClosed(0, 51).toArray()));
    public boolean dealer = true;//true = player, false = dealer
    public ArrayList<Integer> pHand, aiHand, crib;
    private ImageView[] aiCards;// = new ImageView[]{aiCard0, aiCard1, aiCard2, aiCard3, aiCard4, aiCard5};
    private ImageView[] userCards;// = new ImageView[]{userCard0, userCard1, userCard2, userCard3, userCard4, userCard5};
    private ArrayList<Integer> selected;
    Random random = new Random();
    
    public static int cutCard;
    public static int state;//the game state: 0=deal&making crib, 1=counting, 2=calculate points, 3=crib
    private int count;
    private ArrayList<Integer> stack;
    public static ArrayList<Integer> revealed;
    private boolean go = false;
    
    @FXML
    public void initialize() {
    	aiCards = new ImageView[]{aiCard0, aiCard1, aiCard2, aiCard3, aiCard4, aiCard5};
        userCards = new ImageView[]{userCard0, userCard1, userCard2, userCard3, userCard4, userCard5};
    	exitBtn.setOnAction(event -> {
			System.exit(0);
		});
    	debug = true;
    	/*debugBtn.setOnAction(event -> {
			debug = !debug;
			log("Debug set to: " + debug);
		});*/
    	
    	newGame();
    	//log(deck.toString());
    }
    
    @FXML
    private void newGame(){
    	clearLog();
    	log("Starting Game");
    	resetDeck();
    	aiScore = 0;
    	userScore = 0;
    	
    	pHand = new ArrayList<Integer>();
    	aiHand = new ArrayList<Integer>();
    	crib = new ArrayList<Integer>();
    	selected = new ArrayList<Integer>();
    	revealed = new ArrayList<Integer>();
    	
    	count = 0;
    	stack = new ArrayList<Integer>();
    	
    	random = new Random();
        dealer = random.nextBoolean();
        
    	updateScores();
    	cleanUp();
    	startHand();
    }
    
    private void resetDeck(){
    	List<Integer> range = IntStream.rangeClosed(0, 51).boxed().collect(Collectors.toList());
    	deck = new ArrayList<Integer>(range);
    	Collections.shuffle(deck);
    }
    private void updateScores(){
    	userScoreLabel.setText(""+userScore);
    	aiScoreLabel.setText(""+aiScore);
    }
    
    public void startHand(){
    	log("Starting Hand");
    	state = 0;
    	log("It's "+ (dealer?"your":"AI's") + " turn to deal!");
    	//log(deck.toString());
    	//deal
    	for(int i = 0; i < 12; i++)
    	{
    		if(dealer ^ (i%2)==0){
    			//give card to player
    			pHand.add(deck.get(i));
    		}
    		else{
    			//give to ai
    			aiHand.add(deck.get(i));
    		}
    	}
    	Collections.sort(aiHand, Context.getInstance().getC());
    	//log(pHand.toString());
    	//log(aiHand.toString());
    	updateHandImages();
    	log("Choose 2 cards to put into the crib, then click 'Accept'. " +
    			(!dealer?"It's the opponent's crib, so choose carefully!":"It's your crib! Give yourself some points!"));
    }
    
    
    //after each round.
    public void cleanUp(){
    	pHand = new ArrayList<Integer>();//.clear();
    	aiHand = new ArrayList<Integer>();//.clear();
    	crib = new ArrayList<Integer>();//.clear();
    	selected = new ArrayList<Integer>();//.clear();
    	stack = new ArrayList<Integer>();//.clear();
    	revealed = new ArrayList<Integer>();
    	
    	count = 0;
    	cutCard = -1;

    	resetDeck();
    	
    	dealer = !dealer;
    	
    	Image temp = new Image("img/back.png");
    	for(ImageView i : userCards){
    		i.setImage(temp);
    		i.setOpacity(1);
        	i.setTranslateY(0);
        	i.translateYProperty();
    	}
    	for(ImageView i : aiCards){
    		i.setImage(temp);
    		i.setOpacity(1);
    	}
    	deckImg.setImage(temp);
    }
    
    
    public void updateHandImages(){
    	int x = 0;
    	if(pHand.size() == 4){
    		x = 1;
    		userCards[0].setVisible(false);//(true);
    		userCards[5].setVisible(false);
    		aiCards[0].setVisible(false);
    		aiCards[5].setVisible(false);
    	}
    	else{
    		userCards[0].setVisible(true);
    		userCards[5].setVisible(true);
    		aiCards[0].setVisible(true);
    		aiCards[5].setVisible(true);
    	}
    	for(int i = 0; i < pHand.size(); i++){
    		String path = "img/cards/" + pHand.get(i).toString() + ".png";
    		//System.out.println(path);
    		userCards[i+x].setImage(new Image(path));
    	}
    	//aiCards
    	if(debug){
    		for(int i = 0; i < aiHand.size(); i++){
        		String path = "img/cards/" + aiHand.get(i).toString() + ".png";
        		aiCards[i+x].setImage(new Image(path));
        	}
    	}
    	else{
    		Image temp = new Image("img/back.png");
        	for(ImageView i : aiCards){
        		i.setImage(temp);
        		i.setOpacity(1);
        	}
    	}
    	for(int i = 0; i < revealed.size(); i++){
    		String path = "img/cards/" + revealed.get(i).toString() + ".png";
    		aiCards[i+x].setImage(new Image(path));
    	}
    }
    @FXML
    void cardClick(MouseEvent e) {
    	String id = ((ImageView)e.getSource()).getId();//userCard0, userCard1, etc.
        int k = Character.getNumericValue(id.charAt(id.length()-1));//or, subtract by char '0'
        log(k+" was clicked");
        if(!selected.contains(k) && selected.size()<2 && state == 0){// || (state == 1))){
        	selected.add(k);
        	((ImageView)e.getSource()).setOpacity(.7);
        	((ImageView)e.getSource()).setTranslateY(-20);
        	((ImageView)e.getSource()).translateYProperty();
        	return;
        }
        else{
        	if(state != 1){
	        	selected.remove(Integer.valueOf(k));
	        	((ImageView)e.getSource()).setOpacity(1);
	        	((ImageView)e.getSource()).setTranslateY(0);
	        	((ImageView)e.getSource()).translateYProperty();
	        	return;
        	}
        }
        
        //if state == 1, counting phase
        if(state == 1){
        	int card = pHand.get(k-1);
        	if(!selected.contains(card) && checkValid(card)){ //selected.contains(k)
            	selected.add(card);//selected.add(k);
            	((ImageView)e.getSource()).setOpacity(.7);
            	((ImageView)e.getSource()).setTranslateY(-20);
            	((ImageView)e.getSource()).translateYProperty();
            	System.out.println("k: "+k+", card: "+card+", value"+Card.getValue(card));
            	
            	addCount(card, true);
            }
        }
    }
    
    public boolean checkValid(int i){
    	int val = Card.getValue(i);
    	return ((val + count) <= 31);// && !selected.contains(i);
    }
    
    public void agentCount(){
    	if(!go){
    		int play = AI_Agent.pickCount(aiHand, stack, count);
    		if(play == -1){
    			go = true;
    			String out = "There's no card the AI can play, so they call 'GO'.";
    			if(selected.size() < 4){
    				log(out + " It's your turn again.");
        			playerGo();
    			}
    			else{
    				log(out + " Since you are out of cards, the count is reset, and the AI goes again.");
    				count = 0;
    				resetStack();
    				agentCount();
    			}
    			//count = 0;
    		}
    		else{
    			addCount(play, false);
    			revealed.add(play);
        		updateHandImages();
    		}
    	}
    	else{
    		int play = AI_Agent.pickCount(aiHand, stack, count);
    		if(play == -1){
    			go = false;
    			String out = "There's no card the AI can play, so 'GO' is ended.";
    			if(count != 31 && count != 0){
    				out += " The AI gets 1 extra point for 'GO'.";
    				userScore += 1;
    				updateScores();
    			}
    			out+="\n The count is now reset,";
    			count = 0;
				resetStack();
				
    			if(selected.size() < 4){
    				log(out + " and it's your turn again.");
    			}
    			else{
    				log(out + " but since you are out of cards it's the AI's turn again.");
    				agentCount();
    			}
    		}
    		else{
    			addCount(play, false);
    			revealed.add(play);
        		updateHandImages();
    		}
    	}
    }
    public void agentGo(){
    	go = true;
    	agentCount();
    }
    public void playerGo(){
    	acceptBtn.setDisable(true);
    	String out = "";
    	if(count == 31 || count == 0){
			out = "You reached 31. Count is reset, and play is passed to AI.";
			count = 0;
			go = false;
			acceptBtn.setDisable(false);
			resetStack();
			log(out);
			return;
		}
    	boolean test=false;
    	for(int i : pHand){
			if(!selected.contains(i) && checkValid(i)){
				test=true;
			}
		}
		if(test){
			out+= "The count is "+count+". Choose a card to play.";
		}
		else{
			out+="No valid card you can play. Count is reset, and play is passed to the AI.";
			if(count != 31 && count != 0){
				out += " You still get 1 extra point for 'GO'.";
				userScore += 1;
				updateScores();
			}
			count = 0;
			go = false;
			acceptBtn.setDisable(false);
			resetStack();
		}
		log(out);
    }
    public void addCount(int i, boolean p){
    	stack.add(i);
    	count += Card.getValue(i);
    	
    	String out = (p?"You ":"The AI ") + "played the " + Card.toString(i) + ".";
    	out += "\nThe count is now " + count + ".\n";
    	
    	//check for points
    	
    	if(stack.size()>=2){
	    	ArrayList<Integer> ranks = new ArrayList<Integer>(stack);
	    	Collections.copy(ranks, stack);
	    	for(int j = 0; j < ranks.size(); j++){
	    		ranks.set(j, ranks.get(j)%13);
	    	}
	    	Collections.reverse(ranks);
	    	//log(stack.toString());
	    	//log(ranks.toString());
	    	if(stack.size()>=3){//Check for runs
		    	boolean straight = false;
		    	int len = 0;
		    	//Old checking for runs loop
		    	/*for(int k = 3; k < ranks.size(); k++){//ranks.size()-1; k > 2; k--){
		    		List<Integer> temp = ranks.subList(0, k);
		    		Collections.sort(temp);
		    		boolean b = true;
		    		for(int m = 0; (m < temp.size() && b == true); m++){
		    			if(m > 0 && temp.get(m).intValue() != (temp.get(m-1).intValue() + 1))
		    				b = false;
		    			if(m < temp.size()-1 && temp.get(m).intValue() != (temp.get(m+1).intValue() - 1))
		    				b = false;
		    		}
		    		if(b == true){
		    			straight = true;
		    			len = k+1;
		    			k = ranks.size();//-1;//quickly exit loop
		    		}
		    	}*/
		    	//Newer checking for runs loop
		    	boolean b = true;
		    	int length = 0;
	    		int k = 3;
		    	while(b){
		    		List<Integer> temp = ranks.subList(0, k);
		    		Collections.sort(temp);
		    		int[] arr = new int[temp.size()];
		    		for (int w = 0; w < arr.length; w++) {
		    		    arr[w] = temp.get(w);
		    		}
		    		int g = AI_Agent.checkRuns2(arr, 0, 1);
		    		if(g>length)
		    			length = g;
		    		else
		    			b=false;
		    		
		    		if(k+1 < ranks.size()-1)
		    			k++;
		    		else
		    			b=false;
		    	}
		    	if(length > 0){
		    		len = length;
		    		straight = true;
		    	}
		    	if(straight){
		    		out += "A run of " + len + " means " + len + " points for " + (p?"you!":"the AI!") + "\n";
		    		if(p)
		    			userScore += len;
		    		else
		    			aiScore += len;
		    	}
	    	}
	    	//check for pairs
	    	if(ranks.get(0).equals(ranks.get(1))){
	    		//pair
	    		String s = "pair worth 2 points!";
	    		int z = 2;
	    		if(ranks.size() >= 3 && ranks.get(0).equals(ranks.get(2))){
	    			s = "triplet worth 6 points! Wow!";
		    		z = 6;
		    		if(ranks.size() >= 4 && ranks.get(0).equals(ranks.get(3))){
		    			s = "double pair! That's worth a whopping 12 points! Amazing!";
			    		z = 12;
		    		}
	    		}
	    		out += (p?"You":"The AI") + " got a " + s + "\n";
	    		if(p)
	    			userScore += z;
	    		else
	    			aiScore += z;
	    	}
	    	
	    	
	    	//Simple points for 15 or 31
	    	if(count == 15){
	    		out += "A count of 15 means 2 points for " + (p?"you!":"the AI!") + "\n";
	    		if(p)
	    			userScore += 2;
	    		else
	    			aiScore += 2;
	    	}
	    	else if(count == 31){
	    		out += "A count of 31 means 2 points for " + (p?"you!":"the AI!") + "\n";
	    		if(p)
	    			userScore += 2;
	    		else
	    			aiScore += 2;
	    		out += "\nThe count is also reset to 0.\n";
	    		count = 0;
	    		resetStack();//stack.clear();
	    	}
    	}
    	if(selected.size() >= 4 && revealed.size()>=4){
    		out+= "\n" + (p?"You":"The AI") + "played the last card!"
    				+(count!=0?" 1 extra point for last card.":"")
    				+"\nNow moving to calculating points in hand."+(!dealer?"You":"The AI")+" are first.";
    		if(count==0){
    			if(p)
	    			userScore += 1;
	    		else
	    			aiScore += 1;
    		}
    		state = 3;
    		go = false;
        	updateScores();
        	log(out);
        	return;
    	}
    	else if (selected.size() >= 4 && revealed.size() < 4){
    		out+="\nThe count is now " + count + ", and you are out of cards to play. The AI goes again.";
    		go = false;
        	updateScores();
        	log(out);
    		agentCount();
    	}
    	else if(selected.size() < 4 && revealed.size()>=4){
    		out+="\nThe count is now " + count + ", and the AI is out of cards to play. Play again.";
    		go = false;
        	updateScores();
        	log(out);
    	}
    	updateScores();
    	log(out);
    	if(!go){
    		if(p)
    			agentCount();
    		else{
    			out = "It's your turn, and the count is now " + count
    					+ ". Choose a card to reveal that won't cause the count to go over 31,"
    					+ " or click 'Accept' if you can't.";
    			log(out);
    		}
    	}
    	else{
    		if(p){
    			playerGo();
    		}
    		else{
    			agentGo();
    		}
    	}
    	//System.out.println(out);
    }
    
    @FXML
    void accept(ActionEvent event) {
    	if(state == 0 && selected.size()==2){
    		Collections.sort(selected);
    		for(int i : selected){
    			crib.add(pHand.get(i));
    			pHand.set(i, -1);
    			userCards[i].setOpacity(1);
    			userCards[i].setTranslateY(0);
    			userCards[i].translateYProperty();
    		}
    		pHand.removeIf(n -> (n == -1));
    		System.out.println(pHand.toString());
    		selected = new ArrayList<Integer>();//.clear();
    		//add cards from ai's hand
    		

    		log(aiHand.toString());
    		AI_Agent.pickCrib(aiHand);
    		Collections.sort(AI_Agent.chosen);
    		for(Integer i : AI_Agent.chosen){
    			crib.add(i);
    			aiHand.remove(i);
    		}
    		
    		
    		log(pHand.toString());
    		log(aiHand.toString());
    		updateHandImages();
    		log("Crib is made. Moving on to counting phase.");
    		state = 1;
    		count = 0;
    		stack.clear();
    		
    		//choose cut card
    		int i = random.nextInt(51-12) + 12;//first 12 are in the hands
    		cutCard = deck.get(i);
    		deckImg.setImage(new Image("img/cards/" + cutCard + ".png"));
    		if(Card.getName(cutCard) == 'J'){
    			log((dealer?"You":"The AI") + " (the dealer) gets 2 points for flipping a Jack.");
    			if(dealer)
	    			userScore += 2;
	    		else
	    			aiScore += 2;
    		}
    		

    		Collections.sort(pHand, Context.getInstance().getC());
    		updateHandImages();
    		
    		log("Now starting the counting phase.");
    		if(dealer){
    			agentCount();
    		}
    		else{
    			log("Play a card to start the count!");
    		}
    	}
		//For testing
		System.out.println(AI_Agent.calcPoints(pHand));
		if(state==1){
			//counting phase
			boolean test = false;
			String out = "";
			for(int i : pHand){
				if(!selected.contains(i) && checkValid(i)){
					test=true;
				}
			}
			if(test){
				out+= "There's still a card you can play.";
				log(out);
			}
			else{
				//if(selected.size())
				out+="No valid card you can play. You call 'GO'.";
				if(revealed.size() < 4){
					out +=  "Play is passed to the AI.";
					go = true;
					log(out);
					agentGo();
				}
				else{
					out += " Since the AI is out of cards, count is reset and you get to go again.";
					count = 0;
					go = false;
					resetStack();
					log(out);
				}
			}
		}
    }
    
    public void resetStack(){
    	stack = new ArrayList<Integer>();
    }
    @FXML
    private void clearLog() {
    	logTextArea.clear();
    }

    @FXML
    void howTo(ActionEvent event) {

    	log(deck.size()+"");
    }

    @FXML
    void shuffleHand(ActionEvent event) {
    	log("Shuffling Hand");
    	Collections.shuffle(pHand);
    	updateHandImages();
    }

    @FXML
    void sortHand(ActionEvent event) {
    	log("Sorting Hand");
    	Collections.sort(pHand, Context.getInstance().getC());
    	updateHandImages();
    }
    @FXML
    void debugToggle(ActionEvent event) {
		debug = !debug;
		updateHandImages();
		log("Debug set to: " + debug);
    }
    
    public void log(String input){
    	logTextArea.appendText("---------------------------\n");
    	logTextArea.appendText(input);
    	logTextArea.appendText("\n");
    }

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<Integer> getAiHand() {
		return aiHand;
	}

	public int getState() {
		return state;
	}

	public ArrayList<Integer> getStack() {
		return stack;
	}
}
