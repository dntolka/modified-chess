import java.util.ArrayList;

public class Minimax_template
{
    private World w;

    public Minimax_template(World w){
        this.w = w;
    }

	public int minimax(int height, long start, String[][] board, ArrayList<String> availableMoves, int color, int depth, boolean maximizingPlayer, int score) 
    {
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
					eval = minimax(height, start, tmpBoard, avMoves, 1, depth + 1, false, score);

				} else {

					avMoves = w.tmpSelectAction(0, tmpBoard);
					eval = minimax(height, start, tmpBoard, avMoves, 0, depth + 1, false, score);

				}

				if (eval > currEval) {
					currEval = eval;
					maxIndex = count;
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
					eval = minimax(height, start, tmpBoard, avMoves, 1, depth + 1, true, score);

				} else {
				
					avMoves = w.tmpSelectAction(0, tmpBoard);
					eval = minimax(height, start, tmpBoard, avMoves, 0, depth + 1, true, score);

				}

				currEval = Math.min(currEval,eval);
					
			}
		}
			return currEval;
	}

	// A utility function to find Log n in base 2
	public int log2(int n)
	{
		return (n==1)? 0 : 1 + log2(n/2);
	}

}