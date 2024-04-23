/* Sharon Oh, 5/22/20, Pd 4
 * Tic Tac Toe
 * This is an interactive GUI which allows two people to play Tic Tac Toe and click 
 * on windows on the GUI in order to play. Starting player is randomly chosen and 
 * game could result in a win for a player or tie. 
 */
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Oh_TicTacToe extends JFrame {

	private PicPanel[][] allPanels;
	private ArrayList<BufferedImage> images;
	private String[] playerNames;
	private JLabel bottomLabel;
	private int turns;
	private boolean gameOver;
	private int[][] board;

	public Oh_TicTacToe() {

		setSize(450,550);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Tic Tac Toe");
		setBackground(Color.blue);

		images = new ArrayList<BufferedImage>();

		//checks and opens the files for the X and O
		try {
			images.add(ImageIO.read(new File("x.png")));
			images.add(ImageIO.read(new File("o.png")));

		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not read in the pic");
			System.exit(0);
		}

		//represents the Tic Tac Toe grid 
		PicPanel grid = new PicPanel();
		grid.setLayout(new GridLayout(3,3,5,5));
		grid.setBackground(Color.black);
		grid.setBounds(0,0,450,440);

		allPanels = new PicPanel[3][3];

		//initalizes all the panels inside of the Tic Tac Toe grid
		for(int row =0; row<3; row++) {

			for(int col=0; col<3; col++) {

				allPanels[row][col] = new PicPanel();
				grid.add(allPanels[row][col]);

			}
		}

		gameOver = false;

		board = new int[3][3];

		String[] pNames = new String[2];

		//recieves the player names through JOptionPane
		for(int i=0; i<2; i++) {
			pNames[i] = JOptionPane.showInputDialog(null, "Enter Player Name: ");
		}

		playerNames = new String[2];

		//randomly chooses the starting player
		turns = (int)(Math.random()*2);
		playerNames[0] = pNames[turns%2];
		playerNames[1] = pNames[(turns+3)%2];


		//creates the JLabel at the bottom of the board that shows player names and their symbols
		JLabel player1 = new JLabel(playerNames[0]+" (X)");
		JLabel player2 = new JLabel(playerNames[1]+" (O)");
		player1.setBounds(20,445,100,20);
		player2.setBounds(350,445,100,20);

		//bottomLabel which represents whose turn it is and when the game ends, who won or if it resulted in a tie
		bottomLabel = new JLabel(pNames[turns%2]+"'s Turn");
		bottomLabel.setBounds(200,500,150,30);

		add(grid);
		add(player1);
		add(player2);
		add(bottomLabel);

		setVisible(true);
	}

	class PicPanel extends JPanel implements MouseListener{

		private BufferedImage image;
		private boolean hasPhoto;

		public PicPanel() {
			setBackground(Color.white);
			this.addMouseListener(this);
			hasPhoto=false;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if(image!=null){
				int x = (this.getWidth() - image.getWidth(null)) / 2;
				int y = (this.getHeight() - image.getHeight(null))/ 2;
				g.drawImage(image, x, y, null);
			}
		}

		//returns if there is a X or O in this square
		public boolean getHasPhoto() {
			return hasPhoto;
		}

		//called whenever the user clicks in a space
		public void mouseClicked(MouseEvent e) {

			//adds a symbol if there is no photo in the first place and if the game hasnt ended
			if(!gameOver && hasPhoto == false) {
				image = images.get(turns%2);
				this.repaint();
				hasPhoto = true;

				//this is for later to check if all the panels are occupied
				int checkPanels = 0;

				//changes board matrix according to the new photo added above
				//by finding the change in the allPanels matrix
				for(int row= 0; row<3; row++) {

					for(int col =0; col<3;col++) {


						if(allPanels[row][col].getHasPhoto()) {

							checkPanels++;

							if(board[row][col]==0)
								board[row][col]=(turns%2)+1;
						}

					}
				}

				//checks all ways of winning for example diagonally, horizontally, and vertically or a tie 
				//which would be all the spaces on the board are filled
				if(board[0][0]==(turns%2)+1 && board[1][1]==(turns%2)+1 && board[2][2]==(turns%2)+1){
					gameOver=true;
					System.out.println(1);
				}
				else if(board[0][2]==(turns%2)+1 && board[1][1]==(turns%2)+1 && board[2][0]==(turns%2)+1){
					gameOver = true;
					System.out.println(2);

				}
				else if(board[0][0]==(turns%2)+1 && board[1][0]==(turns%2)+1 && board[2][0]==(turns%2)+1){
					gameOver = true;			
					System.out.println(3);

				}
				else if(board[0][1]==(turns%2)+1 && board[1][1]==(turns%2)+1 && board[2][1]==(turns%2)+1){
					gameOver = true;
					System.out.println(4);

				}
				else if(board[0][2]==(turns%2)+1 && board[1][2]==(turns%2)+1 && board[2][2]==(turns%2)+1){
					gameOver = true;
					System.out.println(5);

				}
				else if(board[0][0]==(turns%2)+1 && board[0][1]==(turns%2)+1 && board[0][2]==(turns%2)+1){
					gameOver = true;
					System.out.println(6);

				}
				else if(board[1][0]==(turns%2)+1 && board[1][1]==(turns%2)+1 && board[1][2]==(turns%2)+1){
					gameOver = true;
					System.out.println(7);

				}
				else if(board[2][0]==(turns%2)+1 && board[2][1]==(turns%2)+1 && board[2][2]==(turns%2)+1){
					gameOver = true;
					System.out.println(8);

				}
				else if(checkPanels == 9) {
					gameOver = true;
				}

				//checks if the game is continuing or someone has won/there is a tie and changes bottomLabel accordingly
				if(gameOver == true && checkPanels == 9) {
					bottomLabel.setText("Tie!");
				}
				else if(gameOver==true) {
					bottomLabel.setText(playerNames[turns%2]+" Wins!");
				}
				else {
					turns++;
					bottomLabel.setText(playerNames[turns%2]+"'s Turn");
				}
			}

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}
	}

	public static void main(String []args) {
		new Oh_TicTacToe();
	}

}
