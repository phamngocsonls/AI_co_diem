
import java.util.*;
import java.awt.*;
	
public class MinMax implements AI {
	private static final int DEPTH = 1;
	private int numTurns;
	
	
	@Override
	public String makeMove(Board b) {
		int r[]={-2,-2,2,2,1,1,-1,-1};
                int c[]={-1,1,-1,1,2,-2,2,-2};
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
						for(int h=0; h<8; h++){
							int k=i+r[h];
                                                        int l=j+c[h];
                                                            if(l>=0 && l<8 && k>=0 && k<8 && piece.checkLegalMove(new Point(k*62,l*62),b)){ //k and l and multiplied by 62 because checkLegalMove takes the pixel positions as parameters
									Move move = new Move(i,j,k,l,piece);
									moves.add(move);
									Board newBoard = new Board(b); //calls the copy constructor of the board clas
									doMove(newBoard, move); //performs move on the new board
                                                                        if (move.getNewX()==3 && move.getNewY()==7) newBoard.carryb2++;
                                                                        else if (move.getNewX()==4 && move.getNewY()==7) newBoard.carryb1++;
									possibleBoards.add(newBoard);
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
		if(numTurns>=0){
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
		}
                else{
			bestMove = moves.get(0);
		}
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
                
                int newX = moveToMake.getNewX();
                int newY = moveToMake.getNewY();
                if(pieceToMove.getColor() == true && newX == 3 && newY == 7){
                     b.clearSquare(newX, newY);
                     newX = 0;
                     newY = 0;
               }
               else if(pieceToMove.getColor() == true && newX == 4 && newY == 7){
                   b.clearSquare(newX, newY);
                   newX = 7;
                   newY = 0;
               }
                                                        
              if(b.hasPiece(newX, newY)){
                 b.clearSquare(newX, newY);
              }

		b.setSquare(newX, newY, pieceToMove);
		pieceToMove.setLocation(newX, newY);
		
		/*
		 * Figures out what text to send back
		 */
		String text = pieceToMove.getType() + " was moved to: " + columns[moveToMake.getNewX()] +  (moveToMake.getNewY()+1) + "\n";
		return text;
	}
	
	
	public int evaluatePosition(Board b, int alpha, int beta, int depth, boolean color){ 
                int r[]={-2,-2,2,2,1,1,-1,-1};
                int c[]={-1,1,-1,1,2,-2,2,-2};
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
                        System.out.println("aaaaaa");
			/*
			 * Iterate through the board, collect all possible moves of the minimizing player
			 */
			for(int i = 0; i<8; i++){
				for(int j=0; j<8; j++){
					if(b.hasPiece(i,j)){
						if(b.getSquare(i,j).getColor() == false){
							Piece piece = b.getSquare(i,j);
							for(int h =0; h<8; h++){
                                                                int k=i+r[h];
                                                                int l=j+c[h];
								if (k<8 && k>=0 && l<8 && l>=0){
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
                                if ((move.getNewX()==4 && move.getNewY()==0))b.carryb2++;
                                else if ((move.getNewX()==3 && move.getNewY()==0))b.carryb1++;
				Board successorBoard = new Board(b); 
				doMove(successorBoard, move);
				newBeta = Math.min(newBeta, evaluatePosition(successorBoard, alpha, beta, depth-1, !color)); //think about how to change moves
				if (move.getNewX()==4 && move.getNewY()==0) b.carryb2--;
                                else if ((move.getNewX()==3 && move.getNewY()==0))b.carryb1--;
                                if(newBeta<= alpha) break;
			}
			return newBeta; //returns the highest score of the possible moves
		}else{ //maximizing player--this is the course of action determined if this is the maximizing player, or black
			ArrayList<Move> moves = new ArrayList<Move>();
                        System.out.println("bbbbbbb");
			/*
			 * These for loops iterate through the board and add all possible pieces to the ArrayList of
			 * moves.  
			 */
			for(int i = 0; i<8; i++){
				for(int j=0; j<8; j++){
					if(b.hasPiece(i,j)){
						if(b.getSquare(i,j).getColor() == true){
							Piece piece = b.getSquare(i,j);
							for(int h =0; h<8; h++){
                                                                int k=i+r[h];
                                                                int l=j+c[h];
								if (k<8 && k>=0 && l<8 && l>=0){
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
		/*
		 * This for loop cycles through all possible moves and 
		 * calculates a new alpha if the successor board evaluates
		 * to a higher number than what is currently the highest score,
		 * which is stored in alpha.  
		 */
		int newAlpha = alpha;
		for(Move move : moves){ //for child in node
                        System.out.println("bla bla "+move.getNewX()+" "+move.getNewY());
//                        if (move.getNewX()==3 && move.getNewY()==7) b.carryb2++;
//                        else if ((move.getNewX()==4 && move.getNewY()==7)) b.carryb1++;
                        System.out.println(move.getNewX()+" "+move.getNewY());
			Board successorBoard = new Board(b); 
			doMove(successorBoard, move);
                        System.out.println("bla bla"+move.getNewX()+" "+move.getNewY());
                        //if (move.getNewX()==7 && move.getNewY()==3) successorBoard.carryb2++;
                        //else if ((move.getNewX()==7 && move.getNewY()==4))successorBoard.carryb1++;
			newAlpha = Math.max(newAlpha, evaluatePosition(successorBoard, alpha, beta, depth, !color)); //think about how to change moves
//			if (move.getNewX()==7 && move.getNewY()==3) b.carryb2--;
//                        else if ((move.getNewX()==7 && move.getNewY()==4))b.carryb1--;
                        if(beta<= newAlpha) break;
		}
		return newAlpha; //returns the highest score of the possible moves
		}
	}
	
	
	public int evaluate(Board b){
		int whitescore = 0;
		int blackscore = 0;
//                int lblack[][]={{24,  22,  24,  22,  24,  22,  24,  22 },
//                                {30,  28,  30,  28,  30,  28,  30,  28},  
//                                {28,  30,  28,  30,  28,  30,  28,  30},  
//                                {30,  36,  34,  36,  34,  36,  34,  28},  
//                                {36,  34,  36,  34,  36,  34,  36,  34},  
//                                {34,  28,  100,  45,  100,  37,  30,  36},  
//                                {36,  100,  45,  34,  36,  100,  40,  34},  
//                                {34,  36,  34,  500, 250,  36,  34,  36}};
                int lblack[][]={{54,  45,  55,  45,  55,  45,  55,  45},  
                                {75,  55,  75,  55,  75,  55,  75,  55},  
                                {55,  75,  55,  75,  55,  75,  55,  75},  
                                {75,  105,  75,  105,  75,  105,  75,  55},  
                                {105,  75,  105,  75,  105,  75,  105,  75},  
                                {75,  55,  145,  105,  145,  55,  75,  38},  
                                {105,  145,  105,  75,  105,  145,  105,  75},  
                                {75,  105,  75,    0,    0,  105,  75,  105}}  ;
                int lwhite[][]=new int[9][9];
                for (int i=0;i<8;i++)
                    for (int j=0;j<8;j++) lwhite[i][j]=lblack[7-i][7-j];
                 /*
		 * Iterates through entire board.   
		 */
		for(int i = 0; i<8; i++){
			for(int j=0; j<8; j++){
				if(b.hasPiece(i, j)){
					if(b.getSquare(i, j).getColor() == false){ //case that piece is white
						whitescore+=lwhite[j][i];
					}else if(b.getSquare(i,j).getColor() == true){ //case that piece is black
                                                blackscore+=lblack[j][i];     
					}
				}
			}
		}
                System.out.println(b.carryb2);
		return blackscore-whitescore+200*(b.carryb2-b.carryb1)+110*(b.carryb1-b.carryw1);
                
                //returns blackscore-whitescore, black player tries to maximize, white player tries to minimize
	}

}
