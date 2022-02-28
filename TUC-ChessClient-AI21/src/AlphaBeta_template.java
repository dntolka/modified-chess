import java.util.ArrayList;

public class AlphaBeta_template
{
    private World w;
	private Zobrist zob;	

    public AlphaBeta_template(World w, Zobrist z){
        this.w = w;
		this.zob = z;
    }

	public int alpha_beta_prun(int height, long start, String[][] board, ArrayList<String> availableMoves, int color, int depth, int alpha, int beta, boolean maximizingPlayer, int score) 
    {
	/*	/////////////////////////////////////////////////////////////
		Transposition curr = zob.retrieveTransposition(board);

		if( (curr != null) ){

			if( ((curr.validity & 0x2) != 0) && (curr.lowerBound >= beta) ){
				if(curr.lowerDepth >= depth){
					System.out.println("lower "+curr.lowerBound);
					return curr.lowerBound;
				}
			} 
				
			if( (curr.validity & 0x2) != 0 )
				if(curr.lowerDepth >= depth)
					alpha = Math.max(alpha, curr.lowerBound); //max
		
			if( ((curr.validity & 0x4) != 0) && (curr.upperBound <= alpha))
				if(curr.upperDepth >= depth){
					System.out.println("upper "+curr.upperBound);
					return curr.upperBound;
				}
					
			
			if( (curr.validity & 0x4) != 0 )
				if(curr.upperDepth >= depth)
					beta = Math.min(beta, curr.upperBound); //min
		}
		////////////////////////////////////////////////////////////  */
		
		long end = System.currentTimeMillis();
		float sec = (end - start) / 1000F; 

		if (depth >=2*height || sec>=8) {
			
			return w.evaluate(board, score);
		}

		int tmp_score = score;
		int currEval =0;

		if (maximizingPlayer) {
			
			currEval = Integer.MIN_VALUE;
			int maxIndex = currEval;

			if (availableMoves.size() == 0) {
				currEval = Integer.MAX_VALUE;
				maxIndex = 0;
			}

			int count=-1;
			for (String move : availableMoves) {
				
				count++;
				score = tmp_score;
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));

				String present = Character.toString(board[x2][y2].charAt(0));
				if (present.equals("P")) {
					score++;
				}

				String[][] tmpBoard = w.tmpMakeMove(board, x1, y1, x2, y2);
				if (w.isRemoved) {
					score++;
					w.isRemoved = false;
				}

				ArrayList<String> avMoves = null;
				int eval;

				if (color == 0) {
	
					avMoves = w.tmpSelectAction(1, tmpBoard);
					eval = alpha_beta_prun(height ,start, tmpBoard, avMoves, 1, depth + 1, alpha, beta, false, score);

				} else {

					avMoves = w.tmpSelectAction(0, tmpBoard);
					eval = alpha_beta_prun(height ,start, tmpBoard, avMoves, 0, depth + 1, alpha, beta, false, score);

				}

				if (eval > currEval) {
					currEval = eval;
					maxIndex = count;
				}

				alpha = Math.max(alpha, eval);

				if(alpha >= beta){
					break;
				}

			}

			if (depth == 0) {
				return maxIndex;
			} 

		} else { // Minimizing Player
			
			currEval = Integer.MAX_VALUE;

			if (availableMoves.size() == 0) {
				currEval = Integer.MIN_VALUE;
			}

			for (String move : availableMoves) {
				
				score = tmp_score;
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));

				String present = Character.toString(board[x2][y2].charAt(0));
				if (present.equals("P")) {
					score--;
				}

				String[][] tmpBoard = w.tmpMakeMove(board, x1, y1, x2, y2);
				if (w.isRemoved) {
					score--;
					w.isRemoved = false;
				}

				ArrayList<String> avMoves = null;
				int eval;
				if (color == 0) {
				
					avMoves = w.tmpSelectAction(1, tmpBoard);
					eval = alpha_beta_prun(height ,start, tmpBoard, avMoves, 1, depth + 1, alpha, beta, true, score);

				} else {
				
					avMoves = w.tmpSelectAction(0, tmpBoard);
					eval = alpha_beta_prun(height ,start, tmpBoard, avMoves, 0, depth + 1, alpha, beta, true, score);

				}

				currEval = Math.min(currEval,eval);
				beta = Math.min(beta,eval);

				if(beta <= alpha){
					break;
				}
					
			}
		}
		
		/*	////////////////////////////////////////////////////////////////////
			if(currEval <= alpha)
				zob.saveUpper(board, currEval, depth);
			if( (currEval > alpha) && (currEval < beta) )
				zob.saveTransposition(board, currEval, currEval, depth);
			if ( currEval >= beta)
				zob.saveLower(board, currEval, depth);
			//////////////////////////////////////////////////////////////////// */

			return currEval;
	}

	// A utility function to find Log n in base 2
	public int log2(int n)
	{
		return (n==1)? 0 : 1 + log2(n/2);
	}

}