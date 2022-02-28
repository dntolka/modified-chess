import java.util.ArrayList;
import java.util.Random;

public class MCTS {

    int rolloutDepth;
    int Depth;
    double mctsConstant;
    World w;

    public MCTS(int rolloutDepth, int Depth, double mctsConstant, World w ) {
        this.rolloutDepth = rolloutDepth;
        this.Depth=Depth;
        this.mctsConstant = mctsConstant;
        this.w=w;
    }

    public int algorithm(String[][] board, ArrayList<String> availableMoves, int color, int score, int totalVst, int depth) 
    {   
        //-------------------------Initialize variables----------------------------------
        ArrayList<Double> uctValue = new ArrayList<Double>();
        ArrayList<Integer>	prizesList = new ArrayList<Integer>();
        ArrayList<Boolean> isStateChecked = new ArrayList<Boolean>();
        ArrayList<Integer>	evaluationList = new ArrayList<Integer>();
        ArrayList<String[][]> boardList = new ArrayList<String[][]>();
        ArrayList<Integer> ni_board = new ArrayList<Integer>();
        int Np = totalVst;
        String[][] bestBoard = null;
        int breakFlag = 0;
        int maxi=0;
        for (int i = 0; i < availableMoves.size(); i++) {
            isStateChecked.add(i, false);
            prizesList.add(i,0);
            ni_board.add(i, 0);
            evaluationList.add(i,0);
            uctValue.add(i,0.0);
        }
    //-----------------------------------------------------------------------------------
        do {
            int i=0;
			for (String move : availableMoves) {
				double maxuctValue = 0;
                
                if (isStateChecked.get(i) == true) { // leaf state
                	i++;
                	continue; 
                }else{
                    // Do Temp Move
                	int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
    				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
    				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
    				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));

    				String present = Character.toString(board[x2][y2].charAt(0));
    				if (present.equals("P"))
    					score++;
    				
                    String[][] tempBoard = w.tmpMakeMove(board, x1, y1, x2, y2);
                    boardList.add(i, tempBoard); 
                    if (w.isRemoved) {
                        score++;
                        w.isRemoved = false;
                    }
                    prizesList.set(i,score);
                    
                    // 1] SELECTION : choose a node by calculating UCT equation
                    int vi  = w.evaluate(tempBoard, score);
                    Double lnNp = Math.log(Np);
                    
                    // Calculate UCT value
                    if (ni_board.get(i) == 0) {     //division with zero , ni = 0
                        uctValue.set(i, Double.MAX_VALUE);
                        
                    }else {
                        if(Np == 0) 
                            uctValue.set(i, Double.MAX_VALUE);
                        else 
                            uctValue.set(i, vi + this.mctsConstant * (Math.sqrt(lnNp / ni_board.get(i))));	}
                    
                    
                    // if UCT value is better
                    if (uctValue.get(i) > maxuctValue) {
                        maxuctValue = uctValue.get(i);
                        bestBoard = tempBoard;
                        maxi = i;
                    }
                    // 2] EXPANSION 
                    boardList.set(maxi, bestBoard);
                    ni_board.set(maxi, ni_board.get(maxi) + 1);
        
                    // 3] SIMULATION : Roll-out
                    if(color == 0) 
                        evaluationList.set(maxi, rollOutFunction(tempBoard, 1, prizesList.get(maxi), this.rolloutDepth, 
                        					w.tmpSelectAction(1, tempBoard)));
                    else 
                        evaluationList.set(maxi, rollOutFunction(tempBoard, 0, prizesList.get(maxi), this.rolloutDepth, 
                        					w.tmpSelectAction(0, tempBoard)));
                    isStateChecked.set(maxi, true);
                    
                    breakFlag++;
                    Np++;
                    i++;
                }
            }
            
            //checking if all states are complete
            if (breakFlag == availableMoves.size()) {
                break;
            }
        }while(true);
        
        // 4] BACK-PROPAGATION : calculate evaluation and call algorithm again 
        int maxEval =Integer.MIN_VALUE;
        int minEval = Integer.MAX_VALUE;
        int currMaxEval = Integer.MIN_VALUE;
        int currMinEval = Integer.MAX_VALUE;
        int besti = -1;
        
        for(int i=0; i<availableMoves.size(); i++) {  
            if(depth == 0) {  
                if(color == w.myColor) { 
                    if(evaluationList.get(i) > maxEval ) {
                        maxEval = evaluationList.get(i);
                        besti = i;
                        }
                }else {
                    if(minEval > evaluationList.get(i)) {
                        minEval = evaluationList.get(i);
                        besti = i;
                        }
                }
            }else {
                if(color == w.myColor) {   
                    currMaxEval = algorithm(boardList.get(i), w.tmpSelectAction(1, boardList.get(i)),
                    						0, prizesList.get(i), Np, depth-1);
                
                    if(currMaxEval > maxEval) {
                    	besti = i;
                        maxEval = currMaxEval;
                    }

                }else {
                    currMinEval = algorithm(boardList.get(i), w.tmpSelectAction(0, boardList.get(i)),
                    						1, prizesList.get(i), Np, depth-1);             
                    if(currMinEval < minEval) {
                    	besti = i;
                        minEval = currMinEval;
                    }
                }	
            }
        }
        if(depth == this.Depth)            
            return besti; //return which move to play in the end error if <0
        else { 
            if(color ==  w.myColor) 
                return maxEval;
            else 
                return minEval;           
        }
    }

    private int rollOutFunction(String[][] board, int color, int score, int rdepth, ArrayList<String> availableMoves) {
        int rollOutValue = -1;
        
        if (rdepth == 0 || availableMoves.size() <= 0) {
            rollOutValue = w.evaluate(board, score);
            return rollOutValue; // Return the current evaluation
        }

        // Do random roll-out
        Random rand = new Random();
        Integer randomRollOut = rand.nextInt(availableMoves.size());

        // Move
        int x1 = Integer.parseInt(Character.toString(availableMoves.get(randomRollOut).charAt(0)));
        int y1 = Integer.parseInt(Character.toString(availableMoves.get(randomRollOut).charAt(1)));
        int x2 = Integer.parseInt(Character.toString(availableMoves.get(randomRollOut).charAt(2)));
        int y2 = Integer.parseInt(Character.toString(availableMoves.get(randomRollOut).charAt(3)));

        String present = Character.toString(board[x2][y2].charAt(0));
        if (present.equals("P"))
            score++;

        // temp move
        String[][] tmpBoard = w.tmpMakeMove(board, x1, y1, x2, y2);

        if (w.isRemoved) {
            score++;
            w.isRemoved = false;
        }
       //call roll-out with the next moves
        if(color == 0) 
            rollOutValue = rollOutFunction(tmpBoard, 1, score, rdepth-1, w.tmpSelectAction(1, tmpBoard));
        else 
            rollOutValue = rollOutFunction(tmpBoard, 0, score, rdepth-1,w.tmpSelectAction(0, tmpBoard));
        
        
        return rollOutValue;
    }
}