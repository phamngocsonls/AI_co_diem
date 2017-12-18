
import java.util.*;
import java.awt.*;
	
public class MinMax implements AI {
	private static final int DEPTH = 1;
	private int numTurns;
	
	
	@Override
	public String makeMove(Board b) {
		
		Move bestMove; //keeps track of the best possible move AI has available
		int bestMoveScore; //score of that best move
		
		ArrayList<Board> possibleBoards = new ArrayList<Board>(); //keeps track of the possible boards (boards with the possible moves made on them)
		ArrayList<Move> moves  = new ArrayList<Move>(); //keeps track of all possible moves 
		
		/*
		 * iterates through board, generates all possible moves and saves them in moves
		 */
		for(int i = 0; i<8; i++){
			for(int j=0; j<8; j++){
				if(b.hasPiece(i,j)){
					Piece piece = b.getSquare(i,j);
					if(piece.getColor() == true){
						for(int k=0; k<8; k++){
							for(int l=0; l<8; l++){
								if(piece.checkLegalMove(new Point(k*62,l*62),b)){ //k and l and multiplied by 62 because checkLegalMove takes the pixel positions as parameters
									Move move = new Move(i,j,k,l,piece);
									moves.add(move);
									Board newBoard = new Board(b); //calls the copy constructor of the board clas
									doMove(newBoard, move); //performs move on the new board
									possibleBoards.add(newBoard);
								}
							}
						}
					}
				}
			}
		}
		//initializes bestMove to the first move in the 
		bestMove = moves.get(0);
		bestMoveScore = evaluatePosition(possibleBoards.get(0), Integer.MIN_VALUE, Integer.MAX_VALUE, DEPTH, false);
		
		//call evaluateposition on each move
		//keep track of the move with the best score
		if(numTurns>0){
			for(int i = 1; i<possibleBoards.size(); i++){
				System.out.println("Evaluating move: " + moves.get(i).toString());
				/*
				 * calls evaluatePosition on each possible board and if the score is higher than previous,
				 * reset the bestMove
				 */
				int j = evaluatePosition(possibleBoards.get(i), Integer.MIN_VALUE, Integer.MAX_VALUE, DEPTH, false);
				if(j >= bestMoveScore){
					bestMove = moves.get(i);
					bestMoveScore = j;
				}
	
			}
		}else{
			Random generator = new Random();
			int index = generator.nextInt(moves.size());
			bestMove = moves.get(index);
		}
		System.out.println(bestMove.toString());
		numTurns++;
		return doMove(b, bestMove); //doMove performs the move on the original board and returns a string of that move

		
	}
	
	
	public String doMove(Board b, Move moveToMake){
		final String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"}; //used to print the row as a letter instead of a number
		Piece pieceToMove = moveToMake.getPiece();
		
		/*
		 * Check if castling took place
		 */
		
		//clear square and reset the new square with the piece to be moved
		b.clearSquare(moveToMake.getOldX(), moveToMake.getOldY());
		b.setSquare(moveToMake.getNewX(), moveToMake.getNewY(), pieceToMove);
		pieceToMove.setLocation(moveToMake.getNewX(), moveToMake.getNewY());
		
		/*
		 * Figures out what text to send back
		 */
		String text = pieceToMove.getType() + " was moved to: " + columns[moveToMake.getNewX()] +  (moveToMake.getNewY()+1) + "\n";
		return text;
	}
	
	
	public int evaluatePosition(Board b, int alpha, int beta, int depth, boolean color){ 
		System.out.println("Begin evaluating position: depth-" + depth + "for- "+ color);
		/*
		 * Base case: when depth is decremented to 0, evaluatePosition simply returns the result
		 * of the evaluate function
		 */
		if(depth == 0){
			int evaluation = evaluate(b);
			System.out.println("Evaluated to: " + evaluation);
			return evaluation;
		}
		
		if(color == false){ //minimizing player--sequence of events that occurs
			ArrayList<Move> moves = new ArrayList<Move>(); //this arraylist keeps track of possible moves from the given position
			/*
			 * Iterate through the board, collect all possible moves of the minimizing player
			 */
			for(int i = 0; i<8; i++){
				for(int j=0; j<8; j++){
					if(b.hasPiece(i,j)){
						if(b.getSquare(i,j).getColor() == color){
							Piece piece = b.getSquare(i,j);
							for(int k =0; k<8; k++){
								for(int l=0; l<8; l++){
									Point p = new Point(k*62,l*62);
									if(piece.checkLegalMove(p, b)){
										moves.add(new Move(i,j,k,l,piece)); //adds moves to the arraylist as they are calculated
									}
									
								}
							}
						}
					}
				}
			}
			
			int newBeta = beta;
			for(Move move : moves){ //for child in node
				System.out.println("Move to be evaluated: " + move.toString());
				Board successorBoard = new Board(b); 
				doMove(successorBoard, move);
				newBeta = Math.min(newBeta, evaluatePosition(successorBoard, alpha, beta, depth -1, !color)); //think about how to change moves
				if(newBeta<= alpha) break;
			}
			return newBeta; //returns the highest score of the possible moves
		}else{ //maximizing player--this is the course of action determined if this is the maximizing player, or black
			ArrayList<Move> moves = new ArrayList<Move>();
			/*
			 * These for loops iterate through the board and add all possible pieces to the ArrayList of
			 * moves.  
			 */
			for(int i = 0; i<8; i++){
				for(int j=0; j<8; j++){
					if(b.hasPiece(i,j)){
						if(b.getSquare(i,j).getColor() == true){
							Piece piece = b.getSquare(i,j);
							for(int k =0; k<8; k++){
								for(int l=0; l<8; l++){
									Point p = new Point(k*62,l*62);
									if(piece.checkLegalMove(p, b)){
										moves.add(new Move(i,j,k,l,piece)); //Check this statement 
									}
									
								}
							}
						}
					}
				}
		}
		/*
		 * This for loop cycles through all possible moves and 
		 * calculates a new alpha if the successor board evaluates
		 * to a higher number than what is currently the highest score,
		 * which is stored in alpha.  
		 */
		int newAlpha = alpha;
		for(Move move : moves){ //for child in node
			System.out.println("Move to be evaluated: " + move.toString());
			Board successorBoard = new Board(b); 
			doMove(successorBoard, move);
			newAlpha = Math.max(newAlpha, evaluatePosition(successorBoard, alpha, beta, depth -1, !color)); //think about how to change moves
			if(beta<= newAlpha) break;
		}
		return newAlpha; //returns the highest score of the possible moves
		}
	}
	
	
	public int evaluate(Board b){
		int whitescore = 0;
		int blackscore = 0;

		/*
		 * Iterates through entire board.   
		 */
		for(int i = 0; i<8; i++){
			for(int j=0; j<8; j++){
				if(b.hasPiece(i, j)){
					if(b.getSquare(i, j).getColor() == false){ //case that piece is white
						if(b.getSquare(i,j).getType() == "Queen"){
							whitescore += 2;
						}else if(b.getSquare(i,j).getType() == "Knight"){
							whitescore += 1;
						}else if(b.getSquare(i,j).getType() == "King"){
							whitescore += 10000000;
						}
					}else if(b.getSquare(i,j).getColor() == true){ //case that piece is black
                                                 if(b.getSquare(i,j).getType() == "Knight"){
							whitescore += 1;
						}
					}
				}
			}
		}
		return blackscore-whitescore; //returns blackscore-whitescore, black player tries to maximize, white player tries to minimize
	}

}
