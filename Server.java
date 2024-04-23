/* Sharon Oh, 4/29/21, Pd 8
 * Server
 * This controls whether the game is still in play, and communicating between the players.
 */
import java.net.*;
import java.util.*;
import java.io.*;

public class Server {

	private char[][] board;					// the board
	private int turns;						// the total number of turns taken
	private char[] marks ={'X','O'};		// X is always the first player
	private Scanner[] readers;
	private PrintWriter[] writers;

	public Server(){
		board = new char[3][3];
		readers = new Scanner[2];
		writers = new PrintWriter[2];
	}

	public void go(){

		//Set up the server socket, print IP and port info 

		try {
			ServerSocket server = new ServerSocket(4242);

			System.out.println(InetAddress.getLocalHost().getHostAddress());

			//Accept two players and set up the readers and writers
			Socket player1 = server.accept();
			Socket player2 = server.accept();

			readers = new Scanner[2];
			readers[0] = new Scanner(new InputStreamReader(player1.getInputStream()));
			readers[1] = new Scanner(new InputStreamReader(player2.getInputStream()));

			writers = new PrintWriter[2];
			writers[0] = new PrintWriter(player1.getOutputStream());
			writers[1] = new PrintWriter(player2.getOutputStream());

			//Randomly determine who goes first
			if((int)(Math.random()*2) ==1){
				PrintWriter temp = writers[0];
				writers[0] = writers[1];
				writers[1] = temp;
				Scanner hold = readers[0];
				readers[0]=readers[1];
				readers[1]=hold;

			}


			String names[] = new String[2];

			//receives the players name from both clients
			if(readers[0].hasNextLine()) {	
				names[0] = readers[0].nextLine();
			}

			if(readers[1].hasNextLine()) {
				names[1] = readers[1].nextLine();
			}

			//sends message with which player is starting and the other player's name
			writeMessage(writers[0],STATUS.GO,names[1]);
			writeMessage(writers[1],STATUS.DRAW,names[0]);

			String move="";
			STATUS stat = STATUS.GO;
			do{

				//Get move from next player
				//figure out if the game should still go on
				if(readers[turns%2].hasNextLine()) {

					move = readers[turns%2].nextLine();

					//splits message to determine the coordinates
					String[] splitMessage = move.split(",");
					int firstCoord = Integer.parseInt(splitMessage[0]);
					int secondCoord = Integer.parseInt(splitMessage[1]);

					board[firstCoord][secondCoord]= marks[turns%2];
					stat = gameOver(marks[turns%2]);

					//send STATUS and opposing players move
					writeMessage(writers[(turns+1)%2],stat, move);
					System.out.println("here");

					turns++;
				}
			}while(stat == STATUS.GO);

			//send final message to player

			STATUS firstPlayer = gameOver(marks[0]);
			STATUS secondPlayer = gameOver(marks[1]);

			if(firstPlayer==STATUS.WON) {
				writeMessage(writers[0],STATUS.WON,"You Won!");
				writeMessage(writers[1],STATUS.LOST,"You Lost :(");
			}
			else if(secondPlayer==STATUS.LOST) {
				writeMessage(writers[0],STATUS.LOST,"You Lost");
				writeMessage(writers[1],STATUS.WON,"You Won!");
			}
			else {
				writeMessage(writers[0],STATUS.DRAW,"Draw!");
				writeMessage(writers[1],STATUS.DRAW,"Draw!");		
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//writes a socket with status, followed by a message. All seperated by commas.
	private void writeMessage(PrintWriter writer,STATUS stat, String message){
		writer.println(stat.ordinal()+","+message);
		writer.flush();
	}

	//determines if the game is over
	//sends back Lose or Draw if it is over
	//sends back GO if there is no winner
	private STATUS gameOver(char mark){

		//checks every possible combination or number of turns to see if the game is over
		if(board[0][0] == mark && board[0][1] == mark && board[0][2] == mark) {
			return STATUS.LOST;
		}
		else if(board[1][0] == mark && board[1][1] == mark && board[1][2] == mark){
			return STATUS.LOST;
		}
		else if(board[2][0] == mark && board[2][1] == mark && board[2][2] == mark){
			return STATUS.LOST;
		}
		else if(board[0][0] == mark && board[1][0] == mark && board[2][0] == mark){
			return STATUS.LOST;
		}
		else if(board[0][1] == mark && board[1][1] == mark && board[2][1] == mark){
			return STATUS.LOST;
		}
		else if(board[0][2] == mark && board[1][2] == mark && board[2][2] == mark){
			return STATUS.LOST;
		}
		else if(board[0][0] == mark && board[1][1] == mark && board[2][2] == mark){
			return STATUS.LOST;
		}
		else if(board[0][2] == mark && board[1][1] == mark && board[0][2] == mark){
			return STATUS.LOST;
		}
		else if(turns>=9) {
			return STATUS.DRAW;
		}
		return STATUS.GO;
	}
	public static void main(String[] args){
		new Server().go();
	}
}
