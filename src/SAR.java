/** Class: SAR.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *  Purpose - The main GUI class. Shows the mission screen and handles keyboard commands, etc.
 *  This design of this simulation is inspired by a game called "Wumpus World."
 */
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
//import java.util.Scanner;
//import java.util.Timer;
//import java.util.TimerTask;
import javax.swing.*;



@SuppressWarnings("serial")
public class SAR extends JFrame {
	public static final int ROWS = 6;
	public static final int COLS = 6;

	public static final int CELL_SIZE = 100; // cell width and height (square)
	public static final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas's dimensions
	public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
//	public static final int GRID_WIDTH = 2;                   // Grid-line's width
//	public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2; // Grid-line's half-width

	// Use an enumeration to represent the various states of the mission
	public enum GameState {
		PLAYING, DRAW, H1_WON, H2_WON
	}

	protected GameState currentState;  // the current game state. See above enum for the possible game states.

	protected Player currentPlayer;  // the current player. Could be either h1 or h2. See below.
	protected Player h1;    // hunter 1
	protected String p1Name;
	protected Player h2;    // hunter 2
	protected String p2Name;

	protected Board board, boardPerceivedByAI; // Mission maps. Note: boardPerceivedByAI is used by robot AI for navigation.
	protected DrawCanvas canvas; // Drawing canvas (JPanel) for the mission board
	protected JLabel statusBar;  // Status Bar
	protected JLabel titleBar;  // Title Bar
	protected JTextArea instructions; // instructions on rules of the mission
	protected JScrollPane instructionsSP;	//this will contain the above JTextArea
	protected JButton btnNext;			// Button used for tutorials only
	protected JLabel pageNo;			//Label used for tutorials only
	protected int totalPagesOfTutorial;	//Total no. of pages of the tutorial content. Used for tutorials only
	protected int currentPage;		//used for tutorials only
	protected Box box;				// Box layout that will contain instructions and the button
	protected String[] tutorialStrArr;	//An array of instruction Strings for the tutorial mode
	protected int tutorialInstrIndex;		//An index to keep track of which instruction String to display
	protected boolean[] tutorialEnableBtnArr;	//An array of boolean values to enable or disable buttons at specific stages of the tutorial instructions.
	protected String[][] tutorialStatusCheckerArr;	//Array of String[] arrays to trigger various events during a tutorial.

	protected KeyAdapter keyAdapter;			//Keyboard listener

	protected DrawRoom[][] squares = new DrawRoom[ROWS][COLS]; //rooms
	protected ImageIcon imageH1[] = new ImageIcon[4];//4 images for robot hunter 1
	protected ImageIcon imageH2[] = new ImageIcon[4];//4 images for robot hunter 2
	protected int currentImageH1; //image hunter 1 currently using
	protected int currentImageH2; //image hunter 2 currently using

	private Tutorial tutorial;
	private PracticeDrillHuman practiceDrillHuman;
	private PracticeDrillAI practiceDrillAI;
	private FinalMission finalMission;

	private int experimentType;		//1 - easy/bad AI, 2 - easy/good AI, 3 - hard/bad AI, 4 - hard/good AI

	protected String options;	//A String containing some options to be provided by the end-user
//	protected java.util.Timer timer;
	protected String controlMode;	//Human manual, AI, or both modes enabled
	protected int resultOfShooting; 	//In the event the robot fires its decontaminant shot, this will track whether
									//the shot was on target (1), or whether it missed (-1), or whether the shot couldn't be fired due to being out of ammo (0).

	//Core stats to be saved for the experiment
	protected int numOfMoves;				//Total number of moves the robot took in a mission
	protected int numOfTimesAITriggered;	//Total no. of times the human subject triggered the robot AI during the final mission.
	protected boolean missionIsSuccess;
	private static String code = "";		//This is the code that will contain core stats re: the experiment

	/**
	 * 3-arg constructor. Sets up the mission and GUI components.
	 * @param options A String containing the mission options as set in SARMain.java class
	 * 				  (or, alternatively, as set by the main method in this class).
	 * @param p1Name  Robot Player 1's name
	 * @param p2Name  Robot Player 2's name
	 */
	public SAR(String options, String p1Name, String p2Name) {
		this.options = options;
//		System.out.println("options:" + this.options);
		this.p1Name = p1Name;
		this.p2Name = p2Name;

		if (this.options.contains("#")) {
			int index = this.options.indexOf("#");
			this.experimentType = Integer.parseInt(options.substring(index + 1, index + 2));
			System.out.println("Experiment type is? " + this.experimentType);
		}

		canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
		canvas.setFocusable(true);
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		canvas.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		//draw rooms
		canvas.setLayout(new GridLayout(ROWS,COLS));
		for (int i = 0; i < ROWS; i++)
			for (int j = 0; j < COLS; j++){
				squares[i][j] = new DrawRoom();
				squares[i][j].setBackground(Color.WHITE);
				squares[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				canvas.add(squares[i][j]);
			}

		// Setup the title bar (JLabel)
		titleBar = new JLabel(new ImageIcon("images/title.png"));
		titleBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		// Setup the status bar (JLabel) to display status message during mission
		statusBar = new JLabel("  ");
		statusBar.setFont(new Font("Serif", Font.BOLD, 24));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		instructions = new JTextArea();
		instructions.setFont(new Font("Serif", Font.ITALIC, 15));
		instructions.setFocusable(false);
		instructions.setEditable(false);
		instructions.setWrapStyleWord(true);
		instructions.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
		instructionsSP = new JScrollPane(instructions);

		//This button is used for tutorial only, and disabled by default
		btnNext = new JButton("Next");
		this.setBtnNextVisible(false);

		//Initialize tutorial objects and stat variables
		this.nullifyTutorialObjectsAndStats();

		//A label (used in tutorial only) to let the user know which page of the tutorial they're on
		//Disabled by default
		this.pageNo = new JLabel("Page __ of __");
		this.pageNo.setVisible(false);

		//Put the above button into a horizontal box
		Box boxHoriz = Box.createHorizontalBox();
		boxHoriz.add(btnNext);
		boxHoriz.add(Box.createHorizontalStrut(10));
		boxHoriz.add(pageNo);

		//Now create a vertical box
		box = Box.createVerticalBox();
		box.add(instructionsSP);	//Add  the instruction text
		box.add(boxHoriz);			//Add the horizontal box containing the button above

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.setBackground(Color.WHITE);
		cp.add(titleBar, BorderLayout.NORTH);
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(box, BorderLayout.EAST);
		cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

		this.setPreferredSize(new Dimension(1600, 800));

//		Toolkit tk = Toolkit.getDefaultToolkit();
//	    int xSize = ((int) tk.getScreenSize().getWidth());
//	    int ySize = ((int) tk.getScreenSize().getHeight());
//	    this.setPreferredSize(new Dimension(xSize, ySize));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();  // pack all the components in this JFrame
		setTitle("Search & Rescue");
		setVisible(true);  // show this JFrame


		if (this.options.contains("T")) {	//tutorial mode
			this.initTutorial();	//enable this later once debugging complete
//			this.initFinalMission();
		} else {							//non-tutorial mode

			// Setup the instruction area (JTextArea)
			setInstrText(InstructionMsg.DEFAULT);

			(new StartMissionPopupThread()).start();	//popup GUI to start mission

			//custom method to initialize board and players
			this.initMission();
		}
		//end if (this.options.contains("T")) / else
	}

	protected void enableInstructionTextCopy(boolean b) {
		instructions.setFocusable(b);
	}

	/**
	 * Method: recordStats
	 *         Saves core stats during various stages of the experiment
	 * @param o
	 */
	protected void recordStats(Object o) {
		if (o instanceof Tutorial) {
			//Just record the experiment type for the tutorial mode. No need to record success or fail here
			SAR.code += this.experimentType;
		} else if (o instanceof PracticeDrillHuman) {
			SAR.code += "PDH" + String.valueOf(this.numOfMoves) + (currentState == GameState.H1_WON ? "S" : "F");
		} else if (o instanceof PracticeDrillAI) {
			SAR.code += "A" + String.valueOf(this.numOfMoves) + (currentState == GameState.H1_WON ? "S" : "F");
		} else if (o instanceof FinalMission) {
			SAR.code += "M" + String.valueOf(this.numOfMoves) + "T" + String.valueOf(this.numOfTimesAITriggered) + (currentState == GameState.H1_WON ? "S" : "F");
		}
	}

	/**
	 * Method: getExperimentType
	 * @return 1, 2, 3, or 4, where:
	 *         1 - easy/bad AI, 2 - easy/good AI, 3 - hard/bad AI, 4 - hard/good AI
	 */
	protected int getExperimentType() {
		return this.experimentType;
	}
	/**
	 * Method: getCode
	 * @return the codestring that contains stats re: the experiment
	 */
	protected String getCode() {
		return SAR.code;
	}

	/**
	 * Method: nullifyTutorialObjects
	 * Resets tutorial objects and stats.
	 */
	protected void nullifyTutorialObjectsAndStats() {
		this.tutorial = null;
		this.practiceDrillHuman = null;
		this.practiceDrillAI = null;
		this.finalMission = null;
		this.numOfMoves = 0;
		this.numOfTimesAITriggered = 0;
	}

	/**
	 * Method: instrSetFont
	 * @param f
	 */
	protected void instrSetFont(Font f) {
		this.instructions.setFont(f);
	}

	/**
	 * Method: initTutorial
	 *
	 */
	protected void initTutorial() {
		this.nullifyTutorialObjectsAndStats();
		tutorial = new Tutorial(SAR.this);
		this.setBtnNextActionListener(tutorial);
	}

	/**
	 *
	 */
	protected void initDrillHuman() {
		this.nullifyTutorialObjectsAndStats();
		practiceDrillHuman = new PracticeDrillHuman(this);
		this.setBtnNextActionListener(practiceDrillHuman);
	}

	/**
	 * Method: initDrillAI
	 *
	 */
	protected void initDrillAI() {
		this.nullifyTutorialObjectsAndStats();
		practiceDrillAI = new PracticeDrillAI(this);
		this.setBtnNextActionListener(practiceDrillAI);
	}

	/**
	 * Method: initFinalMission
	 *
	 */
	protected void initFinalMission() {
		this.nullifyTutorialObjectsAndStats();
		finalMission = new FinalMission(this);
		this.setBtnNextActionListener(finalMission);
	}

	/**
	 * Method: setPageNo
	 * @param currentPage
	 */
	protected void setPageNo(int currentPage) {
		this.pageNo.setText("Step " + currentPage + " of " + this.totalPagesOfTutorial);
	}

	/**
	 * Method: setPageNoVisible
	 */
	protected void setPageNoVisible(boolean b) {
		this.pageNo.setVisible(b);
	}

	/**
	 * Method: setBtnNextVisible
	 *
	 */
	protected void setBtnNextVisible(boolean b) {
		this.btnNext.setVisible(b);
	}

	protected void enableBtnNext(boolean b) {
		this.btnNext.setEnabled(b);
	}

	protected void setInstrText(String s) {
		this.instructions.setText(s);
	}

	/**
	 * Method: setBtnNextActionListener
	 * @param o
	 */
	protected void setBtnNextActionListener(Object o) {
		//Reset any existing listeners
		for (ActionListener al : this.btnNext.getActionListeners()) {
			this.btnNext.removeActionListener(al);
		}
		//Set button listeners depending on which object is in the parameter
		if (o instanceof Tutorial) {
			this.btnNext.addActionListener(e-> ((Tutorial) o).showNextInstruction());
		} else if (o instanceof PracticeDrillHuman) {
			this.btnNext.addActionListener(e -> ((PracticeDrillHuman)o).showNextInstruction());
		} else if (o instanceof PracticeDrillAI){
			this.btnNext.addActionListener(e -> ((PracticeDrillAI)o).showNextInstruction());
		} else if (o instanceof FinalMission) {
			this.btnNext.addActionListener(e -> ((FinalMission)o).showNextInstruction());
		}
	}

	/**
	 * Method: setInstrRowCol
	 * @param row
	 * @param col
	 */
	protected void setInstrRowCol(int row, int col) {
		instructions.setRows(row);
		instructions.setColumns(col);
	}

	/**
	 * Method: removeKeyListeners
	 * @param b
	 */
	protected void removeKeyListeners(boolean b) {
		if (b == true) {
			canvas.removeKeyListeners();
//			System.out.println("key listeners removed");
		} else {
			canvas.removeKeyListeners(); //first reset all listeners before adding them (this is to prevent duplicate key listeners
			canvas.addKeyListeners();
//			System.out.println("key listeners added");
		}
	}

	/**
	 * Method: initNumOfMoves
	 * Resets the counter that tracks the no. of moves the robot has made so far
	 */
	protected void initNumOfMoves() {
		this.numOfMoves = 0;
	}

	/**
	 * Method: initMission
	 * Initialize the mission map contents and status.
	 * */
	public void initMission() {
		initNumOfMoves();	//reset counter
		//Begin by creating a random board.
		/* 1st parameter: is the cell at (0,0) always empty?
		 * 2nd parameter: will this board be accessed by the AI?
		 * See the constructor comments in Board.java for more details. */

		board = new Board(this.options.toUpperCase().contains("00") ? true : false, false);

		//TODO OPTIONAL: instead of spawning a random board, we can customize
		//our own board! Just un-comment any of the below boards or paste your own!

		//Wastes to the right AND to the south! Watch how the AI would react.
		//try one-player AI version (aggressive or not, doesn't matter)
//		board = new Board(new String[][]
//				{{"", "P", "W", "", "", ""},
//				 {"W", "", "", "", "G", ""},
//				 {"", "", "", "", "P", ""},
//				 {"", "", "", "", "", ""},
//				 {"", "", "", "", "", ""},
//				 {"", "", "P", "", "", ""},
//				});
		//What's the most efficient way for the AI to find the victim?
		//Note there are no wastes or fire pits in this scenario.
//			board = new Board(new String[][]
//				{{" ",    " ",    " ",    " ",    " ",    " "  },
//				{" ",    " ",    " ",    " ",    " ",    " "  },
//				{" ",    " ",    " ",    " ",    " ",    " "  },
//				{" ",    " ",    "G",    " ",    " ",    " "  },
//				{" ",    " ",    " ",    " ",    " ",    " "  },
//				{" ",    " ",    " ",    " ",    " ",    " "  }
//				});

		//Impossible one. Useful for explaining probability during presentation.
//						board = new Board(new String[][]
//								{{"", "", "P", "", "", ""},
//								{"", "P", "", "", "", ""},
//								{"P", "", "", "", "", ""},
//								{"W", "", "", "", "", ""},
//								{"", "", "", "W", "", ""},
//								{"", "G", "", "", "", ""},
//								});

		/* Very tough board, but the AI is up to the task. */
//		board = new Board(new String[][]
//				{{"", "", "", "", "", ""},
//				 {"", "", "", "", "", ""},
//				 {"", "P", "P", "", "", ""},
//				 {"", "", "", "", "", ""},
//				 {"W", "", "P", "", "", ""},
//				 {"G", "", "", "W", "", ""},
//				});

		//tough tough tough. The victim is surrounded by a wall of fire!
		//try it with 2 AI's, starting in random cell. One of them might die!
//		board = new Board(new String[][]
//				{{"", "P", "", "G", "", ""},
//				 {"", "", "", "", "P", "W"},
//				 {"", "", "", "", "", ""},
//				 {"", "W", "", "", "", ""},
//				 {"", "", "", "", "", "P"},
//				 {"", "", "", "", "", ""},
//				});

		//with aggressiveModeOn set to true, the AI will disinfect both wastes.
		//if set to false, the AI will disinfect only one.
		//Either way, AI gets the victim.
		//				board = new Board(new String[][]
		//						{{"", "", "", "", "", ""},
		//						{"", "", "", "P", "", ""},
		//						{"", "W", "", "", "", ""},
		//						{"", "W", "", "G", "", "P"},
		//						{"", "", "", "", "", ""},
		//						{"P", "", "", "", "", ""},
		//						});


		//RARE situation (one-player mode) where setting aggressiveModeOn to true will result in death.
		//Non-aggressive mode will get the victim. Start at cell (0,0).
//		board = new Board(new String[][]
//				{{"", "", "", "P", "G", "P"},
//				{"", "", "P", "W", "W", ""},
//				{"", "", "", "", "", ""},
//				{"", "", "", "", "", ""},
//				{"", "", "", "P", "P", ""},
//				{"P", "", "", "", "", ""},
//				});


		//Very rare instance. if both players are AI and start at cell (0,0),
		//they will BOTH die. However, note that it's not the AI's fault.
		//The AI still managed to MINIMIZE risk. They just got unlucky.
//		board = new Board(new String[][]
//				{{"", "", "", "", "P", ""},
//				{"", "", "", "", "", ""},
//				{"", "W", "P", "", "", ""},
//				{"W", "", "", "", "G", ""},
//				{"P", "", "", "", "", ""},
//				{"", "", "", "", "", ""},
//				});

		/* Use custom method in Board class to print the board to the console and save it to a text file.
		 * See the method comments in Board.java class for more details. */
		board.printBoard();

		/* Initialize the cell in which the players will start the mission. */
		Cell startRoom;
		CellAsPerceivedByAI startRoomAI;
		Cell[] startRooms = this.setStartRoomAndBoardAI(this.options.toUpperCase().contains("00"));
		startRoom = startRooms[0];
		startRoomAI = (CellAsPerceivedByAI)startRooms[1];

		this.createPlayers(startRoom, startRoomAI, this.p1Name, this.p2Name,
				this.options.toUpperCase().contains("A1"), this.options.toUpperCase().contains("A2"), this.options.toUpperCase().contains("S"),
				this.options.toUpperCase().contains("H") ? "H" : this.options.toUpperCase().contains("R") ? "R" : "B");	//create players

		this.hideAllPics();
		greyOutCell(startRoomAI.getX(), startRoomAI.getY());
		repaint();
	}

	/**
	 * Method: greyOutCell
	 * Greys out indicated cell (means it's been explored)
	 * @param x
	 * @param y
	 */
	protected void greyOutCell(int x, int y) {
		squares[x][y].setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * Method: hideAllPics
	 * Hides pictures in all rooms. Usually invoked when a game is being initialized
	 */
	protected void hideAllPics() {
		for (int i = 0; i < ROWS; i++) {  //hide pictures in all rooms
			for (int j = 0; j < COLS; j++){
				squares[i][j].setBackground(Color.WHITE);
				squares[i][j].hidePics();
			}
		}
	}

	protected Cell[] setStartRoomAndBoardAI(boolean startRoomIs00) {
		Cell startRoom;
		if (startRoomIs00) {
			startRoom = board.getRoom(0, 0);
		} else {
			startRoom = board.getRoom((int)(Math.random() * ROWS), (int)(Math.random() * COLS));

			/*If players start on a random cell, that cell might already have
			 * a pit or victim or wastes in it. Meaning the mission is over before it even begins...which we don't want.
			 * So this while-loop is designed to ensure that the random starting cell is safe AND
			 * that it doesn't have the victim in it. */
			while(startRoom.hasWastes() || startRoom.isPit() || startRoom.hasVictim()) {
				startRoom = board.getRoom((int)(Math.random() * ROWS), (int)(Math.random() * COLS));
			}
		}
		/* Now create a board with incomplete information that will be perceived / accessed by the AI
		 * who will use deductive logic to navigate the board and avoid dangers.
		 * 1st parameter: is cell at (0,0) always empty?
		 * 2nd parameter: will this board be accessed / perceived by the AI? */
		boardPerceivedByAI = new Board(startRoomIs00, true);

		startRoom.setHints();	//custom method to display information about this room on the GUI.

		CellAsPerceivedByAI startRoomAI = boardPerceivedByAI.getRoomAI(startRoom.getX(), startRoom.getY());	//startRoomAI is the same location as startRoom
		startRoomAI.setExplored(true, startRoom);	//Custom method to set this cell as having been explored by the robot.

		/* Every time a room / cell is explored, the AI will use the custom method below to logically deduce and assign
		 * the probability of monsters and pits in every neighboring room. See the CellAsPerceivedByAI.java class for more details. */
		startRoomAI.assignProbabilityToNeighbors(this.board, this.boardPerceivedByAI);
		return new Cell[]{startRoom, startRoomAI};
	}

	/**
	 * Method: createPlayers
	 * @param startRoom the room where the players will start the mission.
	 * @param startRoomAI the room where the players will start the mission. This is a CellAsPerceivedByAI object and will be accessed by AI player
	 * @param p1Name player 1 name
	 * @param p2Name player 2 name
	 * @param aggressivep1 whether p1 is set to aggressive mode
	 * @param aggressivep2 whether p2 is set to aggressive mode
	 * @param singlePlayer whether this is a single player game
	 * @param controlMode  "H" for human manual control only, "R" for robot AI mode only, or "B" for both options enabled
	 */
	public void createPlayers(Cell startRoom, CellAsPerceivedByAI startRoomAI,
							  String p1Name, String p2Name,
							  boolean aggressivep1, boolean aggressivep2, boolean singlePlayer,
							  String controlMode) {

		/* Create two players. Each player can be either a human or AI. */
		h1 = new Player(startRoom, startRoomAI, p1Name);
		h1.setAI(true);	//either or both players can be an AI.
		h1.setAggressiveModeOn(aggressivep1);//this can be toggled on or off. See comments in the Player class beginning with "DESIGN DECISION".
		h2 = new Player(startRoom, startRoomAI, p2Name);
		h2.setAI(true);	//either or both players can be an AI.
		h2.setAggressiveModeOn(aggressivep2);//this can be toggled on or off. See comments in the Player class beginning with "DESIGN DECISION".

		/* OPTIONAL: if you want a 1-player mission, have the other player quit right away.
		 * This gets rid of player's graphics from being shown on the GUI. */
		if(singlePlayer) {
			h2.setOutOfGame(true);
		}
		this.controlMode = controlMode;
		currentPlayer = h1;       //h1 plays first by default. Can be changed to h2 if you want
		currentState = GameState.PLAYING; // mission state: ready to start
		currentImageH1 = 0;
		currentImageH2 = 0;
	}

	/**
	 * Method: updateGame
	 * Updates the mission board and GUI after a player makes a move.
	 * @param currentPlayer h1 or h2. Player h1 is human. h2 might be an AI.
	 * @param command A char. Valid commands are: 'F', 'L', 'R', 'G', 'S', 'Q', or ' ' (space bar).
	 * @param level the current level of the recursion (only relevant when we're running AI mode, which recursively calls updateGame())
	 * @return whether the command was valid. For example, 'X' is not a valid command char.
	 */
	public boolean updateGame(Player currentPlayer, char command, int level) {
		int cpl = currentPlayer.getCurrentRoom().getLocation(); //get current player location
		boolean validKeyTyped = false;	//initialize sentinel value to be returned.


		//NOTE: Also see DrawCanvas class that allows players to use arrow keys to move the robot.
		//		For example, the UP arrow key is mapped to 'F'.
		switch(Character.toUpperCase(command)){
		/* 'F' moves a player forward. So this makes the player in the current room GUI invisible
		 * before moving him to the next room. */
		case 'F':
			if(currentPlayer == h1)
				squares[cpl/ROWS][cpl%COLS].pics[2][0].setVisible(false);
			else
				squares[cpl/ROWS][cpl%COLS].pics[2][2].setVisible(false);
			currentPlayer.forward(this.boardPerceivedByAI); 	//custom method
			validKeyTyped = true;
			break;
			/* 'L' turns a character to the left. */
		case 'L':
			currentPlayer.turnLeft();
			if(currentPlayer == h1){
				if (currentImageH1 == 0) //rotate picture
					currentImageH1 = 3;
				else
					currentImageH1 --;
			}
			else{
				if (currentImageH2 == 0) //rotate picture
					currentImageH2 = 3;
				else
					currentImageH2 --;
			}
			validKeyTyped = true;
			break;
		case 'R':
			currentPlayer.turnRight();
			if(currentPlayer == h1){
				if (currentImageH1 == 3) //rotate picture
					currentImageH1 = 0;
				else
					currentImageH1 ++;
			}
			else{
				if (currentImageH2 == 3) //rotate picture
					currentImageH2 = 0;
				else
					currentImageH2 ++;
			}
			validKeyTyped = true;
			break;
		case 'G':
			if(currentPlayer.giveAid()){
				squares[cpl/ROWS][cpl%COLS].pics[1][2].setVisible(true); //show victim aid icon
			}
			validKeyTyped = true;
			break;
		case 'S':
			resultOfShooting = currentPlayer.shoot();
			if(currentPlayer.isWasteKiller()){
				(new DecontaminantHitPopupThread()).start();
				squares[cpl/ROWS][cpl%COLS].pics[1][1].setVisible(true); //show disinfected icon
				currentPlayer.resetWasteKiller();
			} else if (resultOfShooting == -1) {
				(new DecontaminantMissedPopupThread()).start();
			} else if (resultOfShooting == 0) {
				JOptionPane.showMessageDialog(null, "Cannot use decontaminant (Shots remaining : " + currentPlayer.getNumOfShotsLeft() + ")", "", JOptionPane.PLAIN_MESSAGE);
			}
			validKeyTyped = true;
			break;
		case 'Q':
			currentPlayer.quit();
			validKeyTyped = true;
			break;
		case ' ':
			//pressing spacebar activates AI as long as the current player has been initialized as an AI player.
			if(currentPlayer.isAI()) {
//				System.out.printf("========================================\nSPACEBAR PRESSED. Activating AI for %s...\n"
//						+ "========================================\n",
//						currentPlayer.getName());
				validKeyTyped = true;
				/* See the Player.java class for the custom method getAction(). It returns a command char.
				 * This is the heart of AI's algorithm. */
				int percentRandom = 0;	//initialize the % of time the AI robot will act randomly
				//If the options were set before the mission to allow random acts by robot a certain % of the time,
				//extract that percent value now and execute updateGame() method with it as parameter
				if (SAR.this.options.indexOf("%") != -1) {
					percentRandom = Integer.parseInt(this.options.substring(this.options.indexOf(" ") + 1, this.options.indexOf("%")));
//					System.out.printf("Random action chance: %s%%\n", percentRandom);
				}
				this.updateGame(currentPlayer, currentPlayer.getAction(this.board, this.boardPerceivedByAI, percentRandom), 1);
				this.numOfTimesAITriggered++;
			}
			break;

		case 'O':	//Options window
			this.openOptionsWindow();	//custom method
			break;
		}
		//end switch(Character.toUpperCase(command)){

		/* Now that the player has made a move, we'll go thru each RoomAsPerceivedByAI object on the Board and,
		 * for each explored room aka cell, update the perceptions and probabilities by invoking setExplored() method
		 * and the assignProbabilityToNeighbors from the CellAsPerceivedByAI class. This updates AI's overall
		 * perceptions of which rooms have which types of risks. */
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				CellAsPerceivedByAI tempR = this.boardPerceivedByAI.getRoomAI(i, j);
				/* For every room that has been explored, refresh the perceptions via the setExplored() method
				 * and refresh the AI's probability calculation of neighboring rooms' risks. */
				if(tempR.isExplored()) {
					this.greyOutCell(i, j);
//					squares[i][j].setBackground(Color.LIGHT_GRAY);	//gray out explored room
					tempR.setExplored(true, this.board.getRoom(i, j));
					tempR.assignProbabilityToNeighbors(this.board, this.boardPerceivedByAI);
				}
			}
		}
		//end for i

		/* Now the AI will make some manual adjustments to the probability of UNEXPLORED rooms.
		 * Go thru the whole board again, locate unexplored rooms, then invoke
		 * custom method modifyProbabilityIfSurroundedByDanger(). See the comments accompanying
		 * this custom method for more details. */
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				CellAsPerceivedByAI tempR = this.boardPerceivedByAI.getRoomAI(i, j);
				if(!tempR.isExplored()) {
					tempR.modifyProbabilityIfSurroundedByDanger(board, boardPerceivedByAI);
				}
			}
		}
		//end for i

		if (hasWon(currentPlayer)) {  // check for mission success
			currentState = (currentPlayer == h1) ? GameState.H1_WON : GameState.H2_WON;
			if (level > 0) return validKeyTyped;	//If the recursion level is not at the root level, we skip the below steps for now

			(new PopupThread()).start();	//Popup message game over, needs to run in new thread for thread safety
			if (isTutorialMode()) tutorial.checkTutorialStatus(String.valueOf(command));	//If this was a tutorial mode, update tutorial status
			else if (this.isPracticeMissionHumanMode()) practiceDrillHuman.checkStatus(String.valueOf(command));
			else if (this.isPracticeMissionAIMode()) practiceDrillAI.checkStatus(String.valueOf(command));
			else if (this.isFinalMissionMode()) finalMission.checkStatus(String.valueOf(command));
		} else if (isDraw()) {  // if "draw", it means neither player 1 nor 2 (if in 2-player mode) has found the victim and are both dead. Mission fail
			currentState = GameState.DRAW;
			if (level > 0) return validKeyTyped;	//If the recursion level is not at the root level, we skip the below steps for now
			if (this.isTutorialMode()) tutorial.checkTutorialStatus(String.valueOf(command));	//If this was a tutorial mode, update tutorial status
			else (new PopupThread()).start();	//Popup message game over, needs to run in new thread for thread safety
			if (this.isPracticeMissionHumanMode()) practiceDrillHuman.checkStatus(String.valueOf(command));
			else if (this.isPracticeMissionAIMode()) practiceDrillAI.checkStatus(String.valueOf(command));
			else if (this.isFinalMissionMode()) finalMission.checkStatus(String.valueOf(command));
		}
		// Otherwise, no change to current state (still GameState.PLAYING).
		return validKeyTyped;
	}
	//end public boolean updateGame

	/**
	 * Method: isTutorialMode
	 * @return true if the current game is part of the tutorial mode, false otherwise.
	 */
	public boolean isTutorialMode() {
		return (this.tutorial != null);
	}

	public boolean isPracticeMissionHumanMode() {
		return (this.practiceDrillHuman != null);
	}

	public boolean isPracticeMissionAIMode() {
		return (this.practiceDrillAI != null);
	}

	public boolean isFinalMissionMode() {
		return (this.finalMission != null);
	}

	/**
	 * Method: isTutorialOrMissionMode
	 * @return true if the current game is either part of the tutorial or mission mode (e.g. part of the experiment).
	 *         Note: this differs from the other methods above in that this one will return true for
	 *         ANY AND ALL of the following modes: Tutorial, PracticeMissionHuman, PracticeMissionAI, FinalMission.
	 */
	public boolean isTutorialOrMissionMode() {
		return options.contains("T");
	}

	/**
	 * Method: openOptionsWindow
	 * Opens the mission options menu GUI, and closes / destroys this GUI.
	 */
	public void openOptionsWindow() {
		this.dispose();	//built-in method to destroy this GUI.
		new SARMain().setVisible(true);
	}

	/** Return true if it is a draw (i.e., both players died) */
	public boolean isDraw() {
		if (!h1.isAlive() && !h2.isAlive())
			return true;
		return false;
	}

	/** Return true if the player has located and aided victim */
	public boolean hasWon(Player currentPlayer) {
		return currentPlayer.hasVictim();
	}

	/**
	 * Inner class to place images into a room on the GUI
	 */
	class DrawRoom extends JPanel { //place images in a room
		public JLabel[][] pics = new JLabel[3][3]; //picture array

		public DrawRoom() {

			try {
				imageH1[0] = changeImageSize("images/hunter10.png");
				imageH1[1] = changeImageSize("images/hunter11.png");
				imageH1[2] = changeImageSize("images/hunter12.png");
				imageH1[3] = changeImageSize("images/hunter13.png");
				imageH2[0] = changeImageSize("images/hunter20.jpg");
				imageH2[1] = changeImageSize("images/hunter21.jpg");
				imageH2[2] = changeImageSize("images/hunter22.jpg");
				imageH2[3] = changeImageSize("images/hunter23.jpg");

				this.setLayout(new GridLayout(3,3));

				pics[0][0] = new JLabel(changeImageSize("images/temperature.png"));
				pics[0][1] = new JLabel(changeImageSize("images/stench.png"));
				pics[0][2] = new JLabel(changeImageSize("images/victim.png"));
				pics[1][0] = new JLabel(changeImageSize("images/heat.png"));
				pics[1][1] = new JLabel(changeImageSize("images/disinfect.png"));
				pics[1][2] = new JLabel(changeImageSize("images/success.png"));
				pics[2][0] = new JLabel(imageH1[currentImageH1]);
				pics[2][1] = new JLabel(changeImageSize("images/wastes.png"));
				pics[2][2] = new JLabel(imageH2[currentImageH2]);

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++){
						pics[i][j].setVisible(false);
						add(pics[i][j]);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		public void showPics(String s){ //show the pictures in the room
			String[] symbols = s.split("[ ]");
			for (int i = 0; i < symbols.length; i++){
				//System.out.println(symbols[i]);
				switch (symbols[i]){
				case "B": pics[0][0].setVisible(true); break;
				case "S": pics[0][1].setVisible(true); break;
				case "G": pics[0][2].setVisible(true); break;
				case "P": pics[1][0].setVisible(true); break;
				case "R": pics[1][1].setVisible(true); break;
				case "D": pics[1][2].setVisible(true); break;
				case "H1":
					pics[2][0].setIcon(imageH1[currentImageH1]);
					if(!h1.isOutOfGame()) pics[2][0].setVisible(true);
					break;
				case "W": pics[2][1].setVisible(true); break;
				case "H2":
					pics[2][2].setIcon(imageH2[currentImageH2]);
					if(!h2.isOutOfGame()) pics[2][2].setVisible(true);
					break;
				}
			}
		}

		public void hidePics(){ //hide all the pictures in a room
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					pics[i][j].setVisible(false);
		}
	}
	//end class DrawRoom extends JPanel


	public ImageIcon changeImageSize(String fileName) throws IOException{ //change the image size to be fitted for the room
//		ImageIcon myIcon = new ImageIcon(fileName);
//		BufferedImage img = ImageIO.read(getClass().getResource(fileName));
//		Image img = myIcon.getImage();

		//Using getResource() allows it to run in runnable .jar form
		URL url = SAR.class.getClassLoader().getResource(fileName);
		ImageIcon icon = new ImageIcon(url);
		Image img = icon.getImage();
		Image newImg = img.getScaledInstance(CELL_SIZE/3, CELL_SIZE/3, java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newImg);
		return newIcon;
	}

	/**
	 *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
	 */
	class DrawCanvas extends JPanel {
		public DrawCanvas(){
			addKeyListeners();
		}

		public void removeKeyListeners() {
			removeKeyListener(keyAdapter);
		}

		public void addKeyListeners() {
			//get key input. Among others, this maps arrow keys to char commands to be interpreted by the GUI.
//			System.out.println("add key listeners invoked!");
			keyAdapter = new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent e){
					char command;
					if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
//						System.out.println("FORWARD");
						command = 'F';
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
//						System.out.println("TURN RIGHT");
						command = 'R';
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT){
//						System.out.println("TURN LEFT");
						command = 'L';
					//Let's disable 'F', 'R' and 'L' keys because they're captured by the arrow keys above
					} else if (e.getKeyCode() == KeyEvent.VK_F || e.getKeyCode() == KeyEvent.VK_R || e.getKeyCode() == KeyEvent.VK_L) {
						command = '-';	//this signifies an invalid command.
					} else {
						command = Character.toUpperCase(e.getKeyChar());
					}


					if (currentState == GameState.PLAYING) {
						//If this is tutorial mode, check status first to see if this action should be allowed at all.
						//If not approved, end this method immediately
						if (SAR.this.isTutorialMode()) {
							boolean actionApproved = tutorial.checkTutorialActionApproved(command);
							if (!actionApproved) return;
						} else if (SAR.this.isPracticeMissionHumanMode()) {
							boolean actionApproved = practiceDrillHuman.checkMissionActionApproved(command);
							if (!actionApproved) return;
						} else if (SAR.this.isPracticeMissionAIMode()) {
							boolean actionApproved = practiceDrillAI.checkMissionActionApproved(command);
							if (!actionApproved) return;
						} else if (SAR.this.isFinalMissionMode()){
							if (!finalMission.checkMissionActionApproved(command)) return;
						}

						//Depending on whether the control mode is "H" for manual control only, "R" for automated control only,
						//or "B" for both modes enabled, certain commands will be disabled / enabled accordingly.
						//For instance, in mode "R", only the spacebar command will be recognized.
						if ( ("H".contains(SAR.this.controlMode) && "FRLGSQO" .contains(String.valueOf(command))) ||
							 ("R".contains(SAR.this.controlMode) &&       " " .contains(String.valueOf(command))) ||
							 ("B".contains(SAR.this.controlMode) && "FRLGSQO ".contains(String.valueOf(command))) ) {
							boolean validKeyTyped = updateGame(currentPlayer, command, 0); // invoke update method with the given command
							// Switch player (in the event that we have a 2-player mission)
							if(validKeyTyped) {
								SAR.this.numOfMoves++;
								Player nextPlayer = (currentPlayer == h1 ? h2 : h1);
								if (nextPlayer.isAlive())
									currentPlayer = nextPlayer;
//								System.out.printf("Total no. of moves so far: %s\n", numOfMoves);
							}
						}
						//end nested if

						//If we're in tutorial mode, we need to check to see
						//if user has completed what the tutorial asked them to do
						if (SAR.this.isTutorialMode()) {
							tutorial.checkTutorialStatus(String.valueOf(command));
						}
					} else if (Character.toUpperCase(command) == 'A') {       // this command can be used when mission is over to restart mission
						if (SAR.this.isTutorialMode()) {
							initTutorial();	//If this was a tutorial mode, restart the same tutorial
//							tutorial = new Tutorial(SAR.this);
							return;
						}
						else initMission();									//otherwise, initialize mission again
					} else if(Character.toUpperCase(command) == 'O' && !SAR.this.isTutorialOrMissionMode()) {	// open options window as long as this isn't' tutorial mode
						openOptionsWindow();	//custom method
					}
					// Refresh the drawing canvas
					repaint();  // Call-back paintComponent().

				}
				//end public void keyPressed
			};
			//end keyAdapter initialization
			addKeyListener(keyAdapter);
		}

		@Override
		public void paintComponent(Graphics g) {  // invoke via repaint()
			super.paintComponent(g);    // fill background
			setBackground(Color.WHITE); // set its background color

			int pl1 = h1.getCurrentRoom().getLocation(); //player 1 location
			int pl2 = h2.getCurrentRoom().getLocation(); //player 2 location

			for(int i = 0; i < ROWS; i++)
				for (int j = 0; j < COLS; j++){
					if (board.rooms[i][j].isShown()){
						String s = board.rooms[i][j].perceptions(); //status
						if ((i == (pl1/ROWS)) && (j == (pl1%COLS)))
							s += "H1 ";
						if ((i == (pl2/ROWS)) && (j == (pl2%COLS)))
							s += "H2 ";
						squares[i][j].showPics(s);
					}
				}

			// Print status-bar message
			if (currentState == GameState.PLAYING) {
				statusBar.setForeground(Color.BLUE);
				if (currentPlayer == h1) {
					String lastActionTaken = h1.getLastActionTaken();
					statusBar.setText(h1.getName() + "'s Turn. Control mode: " + (controlMode.equals("H") ? "Manual" : controlMode.equals("R") ? "AI" : "Manual/AI") + ". " +
									  "Last action taken: " + lastActionTaken + (lastActionTaken.equals("S") ? (resultOfShooting == -1 ? " (Missed)" : resultOfShooting == 0 ? " (Out of ammo)" : " (On target!)") : "") +
									  ". Decontaminant shots left: " + h1.getNumOfShotsLeft());
				} else {
					String lastActionTaken = h2.getLastActionTaken();
					statusBar.setText(h2.getName() + "'s Turn. Control mode: " + (controlMode.equals("H") ? "Manual" : controlMode.equals("R") ? "AI" : "Manual/AI") + ". " +
							  "Last action taken: " + lastActionTaken + (lastActionTaken.equals("S") ? (resultOfShooting == -1 ? " (Missed)" : resultOfShooting == 0 ? " (Out of ammo)" : " (On target!)") : "") +
							  ". Decontaminant shots left: " + h2.getNumOfShotsLeft());
				}
			} else {
				//Set status bar message in addition to popup
				if (currentState == GameState.DRAW) {
					statusBar.setForeground(Color.RED);
					statusBar.setText("Mission Failed.");
				} else if (currentState == GameState.H1_WON) {
					statusBar.setForeground(Color.BLUE);
					statusBar.setText(h1.getName() + " has successfully completed the mission!");
				} else if (currentState == GameState.H2_WON) {
					statusBar.setForeground(Color.BLUE);
					statusBar.setText(h2.getName() + " has successfully completed the mission!");
				}
			}
		}
		//end public void paintComponent
	}
	//end class DrawCanvas extends JPanel

	/**
	 * Popup "game over" GUI. Runs in separate thread for thread safety
	 */
	class PopupThread extends Thread {
		public void run() {
			if (currentState == GameState.DRAW) {
				JOptionPane.showMessageDialog(null, "The robot has been destroyed.", "Mission Failed", JOptionPane.PLAIN_MESSAGE);
			} else if (currentState == GameState.H1_WON) {
				JOptionPane.showMessageDialog(null, h1.getName() + " has successfully completed the mission.", "Mission Complete", JOptionPane.PLAIN_MESSAGE);
			} else if (currentState == GameState.H2_WON) {
				JOptionPane.showMessageDialog(null, h2.getName() + " has successfully completed the mission.", "Mission Complete", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	/**
	 * "Begin Mission" popup GUI. Runs in separate thread for thread safety
	 */
	class StartMissionPopupThread extends Thread {
		public void run() {
			JOptionPane.showMessageDialog(null, "Get ready to begin the mission.", "", JOptionPane.PLAIN_MESSAGE);
		}
	}

	class DecontaminantMissedPopupThread extends Thread {
		public void run() {
			JOptionPane.showMessageDialog(null, "Missed!\nShots remaining: " + currentPlayer.getNumOfShotsLeft(), "", JOptionPane.PLAIN_MESSAGE);
		}
	}

	class DecontaminantHitPopupThread extends Thread {
		public void run() {
			JOptionPane.showMessageDialog(null, "Shot on target!\nShots remaining: " + currentPlayer.getNumOfShotsLeft(), "", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/** The entry main() method.
	 *  UPDATE: Recommended that you use SARMain.java's options GUI to run the mission instead of running it here. */
	public static void main(String[] args) {
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
//				Scanner sc = new Scanner(System.in);
//				System.out.println("Enter options in a single String. Options are as follows:\n"
//						+ "======================================================================================\n"
//						+ "S : Single Player Mode (default mode is 2-player mode)\n"
//						+ "T : Enables a special Tutorial Mode (and disables all the other settings)\n"
//						+ "00 : Player(s) always start at room (0,0) (default starting room is chosen at random)\n"
//						+ "A1: Aggressive Mode is ON for Player 1 (default is OFF)\n"
//						+ "A2: Aggressive Mode is ON for Player 2 (default is OFF)\n"
//						+ "(0% - 100%) : the % of time that the AI robot will act randomly for one time-step\n"
//						+ "H, R, or B: Human manual control only, Robot AI only, or Both\n"
//						+ "======================================================================================\n"
//						+ "For example, if you want a single-player mission, random starting room, and aggressive mode,\n"
//						+ "where the robot acts randomly 10% of the time, and AI mode only is enabled,\n"
//						+ "then type the following: S A1 10% R");
//				String options = sc.nextLine();
//				sc.close();
//				System.out.println("Now loading mission. Please wait...");
//				new SAR(options, "Robot 1", "Robot 2");

				//Tutorial / Experiment mode.
				new SAR("T00 0%B", "Robot", "N/A");
			}
		});
	}
}