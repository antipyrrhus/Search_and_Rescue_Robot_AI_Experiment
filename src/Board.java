
/** Class: Board.java
 *  @author Yury Park
 *  Course: HRI
 *
 *  This class - Board class. Composed of Cell or CellAsPerceivedByAI objects.
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Board
{
	/* Static ints to indicate directions, along with corresponding String[] array.
	 * Accessed primarily by Player and Cell / CellAsPerceivedByAI class. */
	public static int NORTH = 0;
	public static int EAST = 1;
	public static int SOUTH = 2;
	public static int WEST = 3;
	public static String[] DIRS = {"NORTH", "EAST", "SOUTH", "WEST"};

	public static final int ROWS = 6;  // ROWS by COLS cells
	public static final int COLS = 6;
	public static final int PITP = 10; //10% of rooms have pits
	public static final int WASTES = 2; //2 WASTES

	/* A Board may be composed of Cell objects or CellAsPerceivedByAI objects. The latter is used by the AI
	 * for logical deductions. See the corresponding class files for more info. */
	public Cell[][] rooms;
	public CellAsPerceivedByAI[][] roomsAI;
	private int[] randomOrdering;	//Used to spawn a Board with randomly placed monsters, pits and gold.

	/**
	 * 2-arg constructor.
	 *
	 * If the 1st given boolean parameter is true, the room at (0,0) will ALWAYS be empty.
	 * Used in the event that players ALWAYS start the game in room (0,0).
	 *
	 * If the 2nd given boolean parameter is true, then creates
	 * a Board object consisting of CellAsPerceivedByAI objects. This Board is accessed by the AI
	 * who draws logical inferences / deductions based on incomplete information --
	 * note that, unlike the other constructors above, this one does NOT contain any information
	 * on which rooms contain pits, monsters or gold.
	 *
	 * If the 2nd given boolean parameter is false, then creates
	 * a normal Board object consisting of Cell.java objects. This board contains
	 * all information about every room whether it's been explored by the player(s) or not.
	 * This board is NOT accessed by the AI.
	 *
	 * See CellAsPerceivedByAI.java class file for more details
	 * @param roomAt_0_0_is_always_empty 1st boolean parameter. Sets whether room at (0,0) is always empty.
	 * @param perceivedByAI 2nd boolean parameter. If set to true, creates a board composed of RoomAsPerceivedByAI objects.
	 */
	public Board(boolean roomAt_0_0_is_always_empty, boolean perceivedByAI)
	{
		/* Create a board as perceived by the AI. */
		if(perceivedByAI == true) {
			roomsAI = new CellAsPerceivedByAI[ROWS][COLS];

			for(int i = 0; i<ROWS; i++)
				for(int j = 0; j<COLS; j++)
					roomsAI[i][j] = new CellAsPerceivedByAI(i,j);
			for(int i = 0; i<ROWS; i++)
				for(int j = 0; j<COLS; j++)
					roomsAI[i][j].initNeighbors(roomsAI);
		}
		/* Otherwise, create a normal board. */
		else {
			rooms = new Cell[ROWS][COLS];

			for(int i = 0; i<ROWS; i++)
				for(int j = 0; j<COLS; j++)
					rooms[i][j] = new Cell(i,j);
			for(int i = 0; i<ROWS; i++)
				for(int j = 0; j<COLS; j++)
					rooms[i][j].initNeighbors(rooms);

			//create random ordering.
			Random randomGenerator = new Random();
			randomOrdering = new int[ROWS*COLS - 1];
			for(int i = 0; i< randomOrdering.length; i++) {
				if(roomAt_0_0_is_always_empty) randomOrdering[i] = i + 1;
				else randomOrdering[i] = i;
			}

			for(int i = 0; i < randomOrdering.length; i++){//shuffle
				int t = randomOrdering[i];
				int n = randomGenerator.nextInt(randomOrdering.length);
				randomOrdering[i] = randomOrdering[n];
				randomOrdering[n] = t;
			}

			//set pits, Wumpus, and gold
			int numPits = ROWS*COLS*PITP/100;
			for(int i = 0; i < numPits; i++)
				randomRoom(i).makePit();	//custom method to get a random room then make pit
			randomRoom(numPits).spawnWastes();
			randomRoom(numPits + 1).spawnWastes();
			randomRoom(numPits + 2).placeVictim();
		}
	}

	/**
	 * 1-arg constructor. Used for test scenarios. Instead of spawning a random board,
	 * creates a board in accordance with the given parameter. This is good for debugging, etc.
	 * because you can test different strategies on the same exact board and retry multiple times.
	 *
	 * @param boardLayout a String[][] array that looks kind of like the following:
	 * {{"",     "",     "",     "",     "",     ""   },
	 *  {"",     "",     "",     "W,",   "",     ""   },
	 *  {"P",    "",     "",     "P",    "",     ""   },
	 *  {"",     "P",    "P",    "",     "",     ""   },
	 *  {"",     "",     "",     "",     "WPG",  ""   },
	 *  {"",     "",     "",     "",     "",     "P"  }
	 * }
	 *
	 * where W stands for Wumpus (monster), P stands for Pit, and G stands for Gold.
	 */
	public Board(String[][] boardLayout) {
		rooms = new Cell[ROWS][COLS];

		for(int i = 0; i<ROWS; i++)
			for(int j = 0; j<COLS; j++)
				rooms[i][j] = new Cell(i,j);
		for(int i = 0; i<ROWS; i++) {
			for(int j = 0; j<COLS; j++) {
				rooms[i][j].initNeighbors(rooms);

				/* Set pits, monsters and gold in accordance with the given String[][] parameter. */
				String tempS = boardLayout[i][j];
				if(tempS.contains("P")) rooms[i][j].makePit();
				if(tempS.contains("W")) rooms[i][j].spawnWastes();
				if(tempS.contains("G")) rooms[i][j].placeVictim();
			}
		}
	}

	/**
	 * Method: randomRoom. Invoked by the no-arg constructor in this class.
	 * @param i a given integer denoting an index
	 * @return a random Room object
	 */
	private Cell randomRoom(int i){
		int rnumber = randomOrdering[i];
		int x = rnumber/ROWS;
		int y = rnumber%ROWS;
		return rooms[x][y];
	}

	/**
	 * Method: getRoom
	 * @param x the row index of the room.
	 * @param y the col index of the room.
	 * @return the Room object at the given row and col.
	 */
	public Cell getRoom(int x, int y){
		return rooms[x][y];
	}

	/**
	 * Method: getRoomAI
	 * @param x the row index of the room.
	 * @param y the col index of the room.
	 * @return the RoomAsPerceivedByAI object at the given row and col.
	 */
	public CellAsPerceivedByAI getRoomAI(int x, int y) {
		return roomsAI[x][y];
	}

	/**
	 * Method: getMonstersRemaining
	 * @return the number of alive monsters on this Board.
	 */
	public int getwastesRemaining() {
		int ret = 0;
		for(int i = 0; i < rooms.length; i++) {
			for(int j = 0; j < rooms[i].length; j++) {
				if(rooms[i][j].hasWastes()) ret++;
			}
		}
		//end for
		return ret;
	}

	/**
	 * Method: printBoard
	 * Prints this Board to the console, AND saves the board layout to a text file.
	 * This is good for debugging, as the contents of the text file can be copy/pasted directly
	 * onto WumpusWorld.java, and be used to construct an identical Board via this one-arg constructor:
	 *
	 * public Board(String[][] boardLayout)
	 *
	 * An example of a board printout looks like this:
	 *
	 * board = new Board(new String[][]
	 * {{"", "", "", "G", "", ""},
	 *  {"", "", "", "W", "", ""},
	 *  {"", "", "", "", "", ""},
	 *  {"W", "", "", "P", "", ""},
	 *  {"P", "", "", "", "", ""},
	 *  {"", "", "", "", "P", ""},
	 * });
	 */
	public void printBoard() {
		try {
			/* Create a new PrintWriter. Note that the parameter "true" in
			 *
			 * new FileWriter("board.txt", true)
			 *
			 * indicates that new information will be APPENDED to the end of the file,
			 * as opposed to the file being overwritten. */
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("board.txt", true)));
			pw.print("board = new Board(new String[][]\n{{\"");
//			System.out.print("board = new Board(new String[][]\n{{\"");
			for(int i = 0; i < rooms.length; i++) {
				for(int j = 0; j < rooms[i].length; j++) {
					if(rooms[i][j].hasWastes()) {
						pw.print("W");
//						System.out.print("W");
					}
					if(rooms[i][j].hasVictim()) {
						pw.print("G");
//						System.out.print("G");
					}
					if(rooms[i][j].isPit()) {
						pw.print("P");
//						System.out.print("P");
					}
					if(j < rooms[i].length - 1) {
						pw.print("\", \"");
//						System.out.print("\", \"");
					}
				}
				pw.println("\"},");
//				System.out.println("\"},");
				if(i < rooms.length - 1) {
					pw.print(" {\"");
//					System.out.print(" {\"");
				}
			}
			pw.println("});");
//			System.out.println("});");
			pw.close();
		}
		catch(IOException ioe) {
//			System.out.println("Error occurred while trying to write to file!");
		}
	}
}