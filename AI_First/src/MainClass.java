
import javax.swing.*;
import java.awt.*;

public class MainClass {
        public static int black_score = 0;
        public static int white_score = 0;
	/**
	 * @param args
	 * The main method creates the GUI in a JFrame and begins the program.  It also
	 * gets input from the user for what kind of game would like to be played.  
	 */
	public static void main(String[] args) {
		
		/*
		 * Get user input for game type
		 */
		Object[] options = {"Player vs. Player",
        "AI first", "Minimax"};
		JFrame f = new JFrame(); //JFrame for dialog box
		int n = JOptionPane.showOptionDialog(f, "What game mode would you like to play?","Choose a mode",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
		/*
		 * Create board and textArea
		 */
		Board b = new Board();
		JPanel textPanel = new JPanel(new BorderLayout()); //create JPanel that holds all the text
		JTextArea text = new JTextArea(5,20); //(5,20) are the (rows,cols) of the textarea
		text.setMinimumSize(new Dimension(200, 300)); //initial minimum size is set for the textarea
		text.setEditable(false);
		text.setText("Welcome to Chess: " + options[n] + " style.  Enjoy!");
		
		if(n == 0){ //case that it is player vs. player mode
			String playerOneName = JOptionPane.showInputDialog("Enter the white player's name");
			String playerTwoName = JOptionPane.showInputDialog("Enter the black player's name");
			JLabel turnLabel = new JLabel(playerOneName + "'s Turn");
			//create view and game
			Game g = new Game(n, playerOneName, playerTwoName, turnLabel); //initialize Game with mode collected from JOptionPane
			View view = new View(b, text,g);
			//create scrollpane
			JScrollPane scroller = new JScrollPane(text);
			scroller.setMinimumSize(new Dimension(200,200));
			//add text to textPanels
			textPanel.add(scroller, BorderLayout.CENTER);
			textPanel.add(turnLabel, BorderLayout.NORTH);
			
			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.add(view, BorderLayout.CENTER);
			frame.add(textPanel, BorderLayout.SOUTH);
			frame.pack();
			frame.setMinimumSize(new Dimension(520,630));  //initial size set for the GUI
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false); //frame is prevented from being resized
			frame.setVisible(true);
		}else{ //case that AI are playing
			JLabel turnLabel = new JLabel("Your Turn!"); //initializes the turn Label
			Game g = new Game(n, turnLabel);
			View view = new View(b, text,g);

			JScrollPane scroller = new JScrollPane(text);
			scroller.setMinimumSize(new Dimension(200,200));
			//add text to the textpanel
			textPanel.add(turnLabel, BorderLayout.NORTH);
			textPanel.add(scroller, BorderLayout.CENTER);
			
			
			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.add(view, BorderLayout.CENTER);
			frame.add(textPanel, BorderLayout.SOUTH);
			frame.pack();
			frame.setMinimumSize(new Dimension(520,630));  //initial size set for the GUI
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false); //frame is prevented from being resized
			frame.setVisible(true);
			
		}

	}

}
