/* Sharon Oh, 4/29/21, Pd 8
 * Client
 * Responsible for displaying GUI and controlling what appears on the GUI.
 */
import java.awt.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.net.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;

//Handles Client input (hoping for single-threaded)

public class TTTClient extends JFrame {


	private boolean myTurn;				// is it the user's turn
	private JLabel label;				// displays status info
	private Panel[][] panels;			// stores the x and o pics

	private String oppName;				// used for output purposes
	private String myPiece;				// either "x.jpg" or "o.jpg"
	private String oppPiece;
	private String myName;				// for debugging purposes

	private PrintWriter writer;			// used to write out to the server
	private Scanner reader;			//used to read from the server
	private Socket theSock;	
	private STATUS stat;				//WIN,LOSS,DRAW,GO

	public TTTClient(String ip, String name, int port){


		myName = name;
		this.setLayout(null);

		//create the board
		setSize(600,600);
		setTitle("Tic Tac Toe - "+name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//creates grid of tic tac toe board
		JPanel grid= new JPanel();
		grid.setBounds(0, 0, 600, 500);
		grid.setLayout(new GridLayout(3,3,5,5));
		grid.setBackground(Color.black);
		add(grid);

		//creates panel for label to be in
		JPanel labelPanel=new JPanel();
		labelPanel.setBounds(0, 500, 600, 100);
		labelPanel.setBackground(new Color(83,199,237));
		add(labelPanel);

		//creates label to output whos turn it is and who won
		label=new JLabel();
		labelPanel.add(label);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Comic Sans", Font.ITALIC, 18));
		label.setBounds(250, 570, 100, 30);


		panels=new Panel[3][3];

		//creates panels in a matrix to represent tic tac toe board
		for(int i = 0; i < 3; i ++){

			for(int k = 0; k < 3; k ++){

				panels[i][k] = new Panel(i,k);
				panels[i][k].setBackground(Color.white);
				grid.add(panels[i][k]);
			}

		}

		try{

			theSock = new Socket(ip,port);
			writer = new PrintWriter(theSock.getOutputStream());
			reader = new Scanner(new InputStreamReader(theSock.getInputStream()));
		}catch(IOException e){

			System.out.println("Problem setting up client socket");
			System.exit(-1);
		}

		//sends the server the player's name
		writeMessage(name);

		String[] message = reader.nextLine().split(",");

		//gets Player's Name, receives from server
		// if they go first and if they are x or o
		STATUS stat = getStatus(message[0]);
		oppName = message[1];

		if(stat==STATUS.GO){
			label.setText("It's your turn");
			myTurn = true;
			myPiece = "X.jpeg";
			oppPiece = "O.jpeg";
		}
		else{
			label.setText("It's "+ oppName+"'s turn");
			myTurn = false;
			myPiece = "O.jpeg";
			oppPiece = "X.jpeg";
		}

		Thread t = new Thread(new ReadHandler());
		t.start();
		setVisible(true);
	}

	// converts a string value to it's enum counterpart
	private STATUS getStatus(String value){
		return STATUS.values()[Integer.parseInt(value)];
	}

	//sends a message go the server
	private void writeMessage(String message){
		writer.println(message);
		writer.flush();
	}

	//updates the JLabel with the provided message
	private void updateLabel( String message){

		try {
			SwingUtilities.invokeAndWait(
					new Runnable(){

						public void run(){
							label.setText(message);
						}
					}
					);
		} catch (Exception e) {

			e.printStackTrace();
		} 
	}


	class Panel extends JPanel implements MouseListener{

		private BufferedImage image;
		private int w,h;		//used for drawing the pic
		private boolean empty;

		private int row;		// the row of the TTT matrix element
		private int col;		// the col of the TTT matrix element

		public Panel(int r, int c){
			row = r;
			col = c;

			empty = true;
			this.addMouseListener(this);
		}


		//changes the panel to a given file pic
		private void changePic(String fname){

			try {

				image = ImageIO.read(new File(fname));
				w = image.getWidth();
				h = image.getHeight();

			} catch (IOException ioe) {
				System.out.println(ioe);
				System.exit(0);
			}


		}

		//only handle an event when clicked
		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		//If it's the user's turn and the cell is free
		//Sends the selection to the server
		//updates the pic and label
		public void mouseClicked(MouseEvent e) {

			if(myTurn && empty) {
				
				String toSend = row+","+col;
				
				myTurn = false;
				this.empty=false;
				
				this.changePic(myPiece);
				
				updateLabel("It's "+ oppName+"'s turn");
				
				//sends message of coordinates to the server
				writeMessage(toSend);


			}
			
			repaint();
			
		}

		public Dimension getPreferredSize() {

			return new Dimension(w,h);
		}

		//this will draw the image
		//repaint calls this method (when it's ready)
		public void paintComponent(Graphics g){
			super.paintComponent(g);

			if(!empty)
				g.drawImage(image,0,0,this);
		}

	}

	//Handles reading messages from the server
	public class ReadHandler implements Runnable{

		public void run(){

			String message;
			stat = STATUS.GO;


			while(stat== STATUS.GO){
				
				//gets a message from the server
				if(reader.hasNextLine()) {
					
					message = reader.nextLine();
					
					String[] splitMessage = message.split(",");
					
					int firstCoord = Integer.parseInt(splitMessage[1]);
					int secondCoord = Integer.parseInt(splitMessage[2]);
					
					//If the game is still going on
					//updates the panel that the opponent selected
					//updates label and myTurn variable
					stat = getStatus(splitMessage[0]);
					
					updateLabel("It's Your Turn");
					
					panels[firstCoord][secondCoord].empty = false;
					panels[firstCoord][secondCoord].changePic(oppPiece);
					
					myTurn = true;
				}					

			}

			//depending if the status (if the player won,lost, or draw)
			if(stat.equals(STATUS.WON)) {
				updateLabel("You Won!");
			}
			else if(stat.equals(STATUS.LOST)) {
				updateLabel("You Lost!");
			}
			else if(stat.equals(STATUS.DRAW)) {
				updateLabel("Draw!");
			}

			try{
				reader.close();
				theSock.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return;
		}

	}

	public static void main(String[] args){
		String name = JOptionPane.showInputDialog(null,"What is your name?");
		new TTTClient("192.168.1.166",  name, 4242);
	}
}
