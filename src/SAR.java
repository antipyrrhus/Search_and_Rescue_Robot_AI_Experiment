/** Class: SAR.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *  Purpose - The main GUI class. Shows the mission screen and handles keyboard commands, etc.
 *  This design of this simulation is inspired by a game called "Wumpus World."
 */
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
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

	private GameState currentState;  // the current game state. See above enum for the possible game states.

	private Player currentPlayer;  // the current player. Could be either h1 or h2. See below.
	protected Player h1;    // hunter 1
	protected String p1Name;
	protected Player h2;    // hunter 2
	protected String p2Name;

	private Board board, boardPerceivedByAI; // Mission maps. Note: boardPerceivedByAI is used by robot AI for navigation.
	private DrawCanvas canvas; // Drawing canvas (JPanel) for the mission board
	private JLabel statusBar;  // Status Bar
	private JLabel titleBar;  // Title Bar
	private JTextArea instructions; // instructions on rules of the mission
	private JScrollPane instructionsSP;	//this will contain the above JTextArea
	private JButton btnNext;			// Button used for tutorials only
	private Box box;				// Box layout that will contain instructions and the button
	private String[] tutorialStrArr;	//An array of instruction Strings for the tutorial mode
	private int tutorialInstrIndex;		//An index to keep track of which instruction String to display
	private boolean[] tutorialEnableBtnArr;	//An array of boolean values to enable or disable buttons at specific stages of the tutorial instructions.
	private String[][] tutorialStatusCheckerArr;	//Array of String[] arrays to trigger various events during a tutorial.

	private KeyAdapter keyAdapter;			//Keyboard listener

	private DrawRoom[][] squares = new DrawRoom[ROWS][COLS]; //rooms
	private ImageIcon imageH1[] = new ImageIcon[4];//4 images for robot hunter 1
	private ImageIcon imageH2[] = new ImageIcon[4];//4 images for robot hunter 2
	private int currentImageH1; //image hunter 1 currently using
	private int currentImageH2; //image hunter 2 currently using

	private String options;	//A String containing some options to be provided by the end-user
//	private java.util.Timer timer;
	private String controlMode;	//Human manual, AI, or both modes enabled
	private int numOfMoves;		//Total number of moves the robot took in a mission
	private int resultOfShooting; 	//In the event the robot fires its decontaminant shot, this will track whether
									//the shot was on target (1), or whether it missed (-1), or whether the shot couldn't be fired due to being out of ammo (0).

	/**
	 * 3-arg constructor. Sets up the mission and GUI components.
	 * @param options A String containing the mission options as set in SARMain.java class
	 * 				  (or, alternatively, as set by the main method in this class).
	 * @param p1Name  Robot Player 1's name
	 * @param p2Name  Robot Player 2's name
	 */
	public SAR(String options, String p1Name, String p2Name) {
		this.options = options;
		this.p1Name = p1Name;
		this.p2Name = p2Name;

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
		titleBar = new JLabel(new ImageIcon("title.png"));
		titleBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		// Setup the status bar (JLabel) to display status message during mission
		statusBar = new JLabel("  ");
		statusBar.setFont(new Font("Serif", Font.BOLD, 24));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		instructions = new JTextArea();
		instructions.setFont(new Font("Serif", Font.ITALIC, 15));
		instructions.setFocusable(false);
		instructions.setWrapStyleWord(true);
		instructions.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
		instructionsSP = new JScrollPane(instructions);

		//This button is used for tutorial only, and disabled by default
		btnNext = new JButton("Next");
		btnNext.setVisible(false);
		btnNext.addActionListener(e -> {
			showNextInstruction();
		});
//		btnPrev = new JButton("Previous");
//		btnPrev.setVisible(false);
//		btnPrev.addActionListener(e -> {
//			showPrevInstruction();
//		});

		//Put the above button into a horizontal box
		Box boxHoriz = Box.createHorizontalBox();
		boxHoriz.add(btnNext);

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
			this.initTutorial();
		} else {							//non-tutorial mode

			// Setup the instruction area (JTextArea)
			instructions.setText(
					"Mission goal: Locate the displaced victim.\n\n" +
							"Robot moves:\n" +
							"- Arrow keys to move forward, turn left, or turn right.\n" +
							"- 'S' to shoot decontaminant materials from an adjacent grid (the robot can only use this option twice).\n" +
							"- 'G' to give emergency first aid to the victim once located (mission success).\n" +
							"- 'A' to attempt another mission (only available once current mission is terminated in success or failure).\n" +
							"- 'Q' to quit / abort the current mission\n" +
							"At any time, press SPACEBAR to enable AI mode for a single turn.\n\n" +
							"Pressing O brings up the Options Menu.\n\n" +
							"Robot Sensors can detect the following environmental cues: \n"+
							"1. Heightened temperature reading (indicated by a thermometer icon) in grids\n" +
							"   adjacent to that containing a fire pit. This must be avoided at all costs.\n"+
							"2. Poisonous stench in grids adjacent to that containing radioactive wastes.\n" +
							"   They may either be avoided or be eliminated via shooting anti-radioactive materials towards it.\n" +
							"3. Once the robot locates the victim, the victim's icon will be displayed.\n\n" +
							"The environment contains:\n"+
							"1. One dislocated victim,\n"+
							"2. Two grids that contain radioactive wastes,\n" +
							"3. Estimated 10% of the grids may contain fire pits.\n\n"+
							"Equipment: " +
							"The robot is equipped with two shots of anti-radioactive decontaminants.\n\n" +
							"Other Notes:\n"+
							"1. The robot is destroyed if it is in the square with a fire pit.\n"+
							"2. The robot is destroyed if it is in the square with active radioactive wastes.\n"+
							"3. Shooting decontaminants toward a direction the robot is facing\n"+
							"   will disable radioactive wastes (if any) in that adjacent grid, and\n"+
							"   an icon (signifying disinfection) will be displayed."
					);

			(new StartMissionPopupThread()).start();	//popup GUI to start mission

			//custom method to initialize board and players
			this.initMission();
		}
		//end if (this.options.contains("T")) / else
	}

	/**
	 * Method: showNextInstruction
	 * Used in tutorial mode only. Shows the next set of instructions.
	 */
	private void showNextInstruction() {
		//Base case. If the mission ended in success (2nd-to last element in the tutorialStrArr array)
		//then we will go on to the next stage of the tutorial instead of showing the last element in the array,
		//which is a mission failed message.
		if (tutorialInstrIndex == tutorialStrArr.length - 2) {
			//TODO
			return;
		}

		//Another base case to prevent out of index error
		if (tutorialInstrIndex + 1 >= tutorialStrArr.length) {
			tutorialInstrIndex = tutorialStrArr.length - 1;
			return;
		}

		tutorialInstrIndex++;
		System.out.println(tutorialInstrIndex);
		instructions.setText(tutorialStrArr[tutorialInstrIndex]);
		btnNext.setEnabled(this.tutorialEnableBtnArr[tutorialInstrIndex]);
		removeKeyListeners(btnNext.isEnabled());
	}

	/**
	 * Used in tutorial mode only. Shows mission failed message and allows user to restart tutorial
	 */
	private void showMissionFailureInstruction() {
		tutorialInstrIndex = tutorialStrArr.length - 1;
		instructions.setText(tutorialStrArr[tutorialInstrIndex]);
		btnNext.setEnabled(false);	//disable next button since game over
		removeKeyListeners(false);	//do not remove (aka, add back) key listeners to allow user to restart
	}

//	private void showPrevInstruction() {
//		tutorialInstrIndex = Math.max(0, tutorialInstrIndex - 1);
//		System.out.println(tutorialInstrIndex);
//		instructions.setText(tutorialStrArr[tutorialInstrIndex]);
//		btnNext.setEnabled(this.tutorialEnableBtnArr[tutorialInstrIndex]);
//		removeKeyListeners(btnNext.isEnabled());
//	}

	private void removeKeyListeners(boolean b) {
		if (b == true) {
			canvas.removeKeyListeners();
			System.out.println("key listeners removed");
		} else {
			canvas.removeKeyListeners(); //first reset all listeners before adding them (this is to prevent duplicate key listeners
			canvas.addKeyListeners();
			System.out.println("key listeners added");
		}
	}

	/**
	 * Method: initTutorial
	 * Initializes tutorial session
	 */
	public void initTutorial() {
		btnNext.setVisible(true);	//This button is only used in tutorial mode. Enable it
//		btnPrev.setVisible(true);
		removeKeyListeners(true);	//temporarily disable keyboard command listeners

		this.instructions.setRows(3);
		this.instructions.setColumns(50);

		this.tutorialStrArr = new String[]{
				"Before beginning the actual mission, you should learn how to control the robot.\n"
			  + "Let's begin the tutorial and complete a practice mission together.",

				"To the left, you see the map of the area the robot will need to traverse\n"
			  + "in search of the victim.",

			    "The robot is represented by the circular-shaped object.",

			    "The robot is currently facing EAST, as indicated by the symbol >.",

			    "The starting area is shaded grey, meaning that the robot has explored it.\n"
			  + "As the robot explores further, more sections of the map will be shaded grey.",

			    "You can use the LEFT, and RIGHT arrow keys respectively\n"
			  + "to rotate the robot left or right.\n"
			  + "(IMPORTANT: the LEFT and RIGHT keys do not actually move the robot, but only\n"
			  + "rotate the robot in place. The UP and DOWN arrow keys do nothing.)\n\n"
			  + "Pressing the SHIFT key moves the robot in the direction it is currently facing.\n"
			  + "(For example, if the robot were facing SOUTH, then pressing SHIFT will move it\n"
			  + "towards the south.)\n\n"
			  + "Right now, the robot is facing EAST. So, pressing SHIFT now will move it\n"
			  + "one step to the eastern direction.\n\n"
			  + "Press the SHIFT key now to move the robot one step to the east.\n"
			  + "(Once you do, the 'Next' button below will be enabled, and you can continue.)",

			    "A thermometer icon here means the robot has picked up high temperature reading.\n"
			  + "This indicates that one or more adjacent grid(s) contain a fire pit.\n"
			  + "(There is also a smoke icon here, but we'll learn about this later.)",

			    "Fire pits are dangerous and will destroy the robot.\n"
			  + "Hence, they should always be avoided.",

			    "So in this case, there may be a fire pit to the robot's right or to the south.\n"
			  + "It's also possible that there are fire pits in both places!\n"
			  + "We would not wish to take the risk of falling into a fire pit if we can avoid it,\n"
			  + "so let's head back the way we came and explore the southern direction instead.\n\n"
			  + "Move the robot back to the starting grid, then move it one step south.\n"
			  + "(Remember, RIGHT and LEFT arrow keys to rotate, and SHIFT to move.)\n"
			  + "Do this now, then press Next to continue.",

			    "Now let's learn about what the smoke icon means.\n"
			  + "It means that the robot's sensors have picked up noxious chemicals nearby, and\n"
			  + "one or more adjacent grid(s) are infected with radioactive chemicals.",

			    "A grid with radioactive chemicals will also destroy the robot.\n"
			  + "But this time, the robot has a choice.\n\n"
			  + "It can either avoid a grid with radioactive chemicals, or disinfect them\n"
			  + "by shooting a decontaminant spray towards that direction.",

			    "The robot is equipped with two shots of the decontaminant spray, so\n"
			  + "we should be judicious about when to use them.\n\n"
			  + "If the robot uses up the decontaminants too early, it may be in trouble later\n"
			  + "when it really needs them.",

			    "To use the decontaminants, we can the robot face the direction of the grid\n"
			  + "which is believed to be infected with radioactive chemicals.",

			    "Since we don't know whether the grid to the south or east is infected,\n"
			  + "we should simply make a guess in this case.\n"
			  + "We'll try using the decontaminant on the neighboring grid to the south.",

			    "Since the robot is currently facing south, we don't have to rotate the robot.\n"
			  + "Press the S key to shoot the decontaminant spray towards the south.\n"
			  + "Do this now, close the popup window that follows, then press Next to continue.",

			    "Nothing happened. The shot missed.\n"
			  + "This means the southern neighboring grid did not contain radioactive chemicals.\n"
			  + "Therefore, the eastern neighboring grid must be the infected one.",

			    "By the way, if you observe the status bar message at the bottom, you will see\n"
			  + "that the 'Decontaminant shots left' have decreased from 2 to 1.\n\n"
			  + "The robot has one decontaminant shot remaining. While we could use this on\n"
			  + "the eastern grid now, we might prefer to save it, since we know\n"
			  + "for certain that the southern grid is safe, and could explore there instead.",

			    "Let's move the robot one step further to the south.\n"
			  + "Do this now, then press Next to continue.",

			    "There is another heightened temperature reading here, meaning fire pits are nearby.\n"
			  + "Since fire pits must be avoided, and we would prefer not to take that risk,\n"
			  + "it may be safer to go back one step north and decontaminate the chemicals which\n"
			  + "we know resides in the grid at the second row and second column.\n"
			  + "Go ahead and move north, then press Next to continue.",

			  	"Now have the robot rotate to the east by pressing the RIGHT arrow key,\n"
			  + "then press Next to continue.",

			  	"OK. Go ahead and press S now to fire the decontaminant shot, then press Next.",

			    "Good. the icon that appeared signals that the shot was a success.\n"
			  + "Unfortunately, the robot has no more decontaminants left.\n\n"
			  + "Let's keep exploring by moving the robot now to the right.\n"
			  + "Then press Next to continue.",

			    "You are on your own for the remainder of this practice mission.\n"
			  + "The key commands at your disposal are:\n\n"
			  + "- SHIFT to move the robot toward the direction it is currently facing,\n"
			  + "- LEFT and RIGHT arrow keys to rotate the robot in-place,\n"
			  + "- S to shoot decontaminant spray towards an adjacent grid in the direction\n"
			  + "  the robot is currently facing (provided the robot has shots left),\n"
			  + "- G to give aid to the victim once found.\n\n"
			  + "Remember to avoid grids containing fire pits at all costs.\n"
			  + "Either avoid or decontaminate grids containing radioactive wastes if possible.\n"
			  + "(since the robot is out of decontaminants now, it must avoid these grids.)\n\n"
			  + "Continue exploring the area on your own, using your best judgment.\n"
			  + "The person in need of rescue is somewhere in this area.\n"
			  + "Once the person is discovered, the Next button will be enabled.",

			    "You have located the victim! Well done.\n"
			  + "Press G to give emergency aid and finish the mission successfully.",

			  	"The robot has been destroyed, and the mission is a failure.\n"
			  + "Fortunately, this was a practice mission. Press A to re-start now."

		};

		//Buttons will be disabled or enabled during various points of the tutorial, as follows.
		this.tutorialEnableBtnArr = new boolean[]{
				true,		//Before beginning the actual mission,...
				true,		//To the left, you see the map...
				true,		//The robot is represented by the circular-shaped ...
				true,		//The robot is currently facing EAST...
				true,		//The starting area is shaded grey....
				false,		//...Move the robot to row 1, column 2 now...
				true,		//A thermometer icon here means ...
				true,		//Fire pits are dangerous...
				false,		//Move the robot back to the starting grid, then move it one step south...
				true,		//There is a smoke icon here...
				true,		//A grid with radioactive chemicals...
				true,		//The robot is equipped with two shots...
				true,		//To use the decontaminant,...
				true,		//Since we don't know whether ...
				false,		//Press the S key ...
				true,		//Nothing happened..
				true,		//If you observe the status bar message ...
				false,		//Let's move the robot to the south...
				false,		//safer to go back one step north...
				false,		//Now have the robot face east...
				false,		//Go ahead and press S now...
				false,		//moving the robot now to the right...
				false,		//Continue exploring...
				false,		//Press G to give emergency aid...
				false,		//Press A to re-start now...
		};

		//This array of arrays will keep track of various trigger events that must occur during a tutorial.
		this.tutorialStatusCheckerArr = new String[][]{
												{null, null, null},
												{null, null, null},
												{null, null, null},
												{null, null, null},
												{null, null, null},
												{"M", "0", "1"},
												{null, null, null},
												{null, null, null},
												{"M", "1", "0"},
												{null, null, null},
												{null, null, null},
												{null, null, null},
												{null, null, null},
												{null, null, null},
												{"S", "3", "0"},
												{null, null, null},
												{null, null, null},
												{"M", "2", "0"},
												{"M", "1", "0"},
												{"R", null, null},
												{"S", "1", "1"},
												{"M", "1", "1"},
												{"M", "3", "4"},
												{"G", "3", "4"},
												{"A", null, null},
												};


		this.tutorialInstrIndex = -1;
		this.instructions.setFont(instructions.getFont().deriveFont(18f));
		this.showNextInstruction();

		board = new Board(new String[][]
				{{"", "", "P", "", "", ""},
				{"", "W", "", "", "", ""},
				{"", "", "", "", "", ""},
				{"P", "", "P", "", "G", ""},
				{"P", "", "", "", "", ""},
				{"", "", "W", "", "", ""},
				});
		boardPerceivedByAI = new Board(true, true);

		Cell startRoom = board.getRoom(0, 0);
		startRoom.setHints();	//custom method to display information about this room on the GUI.
		CellAsPerceivedByAI startRoomAI = boardPerceivedByAI.getRoomAI(startRoom.getX(), startRoom.getY());	//startRoomAI is the same location as startRoom
		startRoomAI.setExplored(true, startRoom);	//Custom method to set this cell as having been explored by the robot.

		/* Every time a room / cell is explored, the AI will use the custom method below to logically deduce and assign
		 * the probability of monsters and pits in every neighboring room. See the CellAsPerceivedByAI.java class for more details. */
		startRoomAI.assignProbabilityToNeighbors(this.board, this.boardPerceivedByAI);

		/* Create two players. Each player can be either a human or AI. */
		h1 = new Player(startRoom, startRoomAI, this.p1Name);
		h1.setAI(true);	//either or both players can be an AI.
		h1.setAggressiveModeOn(false);  //We'll set aggressive mode to OFF for this tutorial
		h2 = new Player(startRoom, startRoomAI, this.p2Name);
		h2.setAI(true);	//either or both players can be an AI.
		h2.setAggressiveModeOn(false);
		h2.setOutOfGame(true);	//Player 2 will not be involved for the tutorial

		//Manual control, Robot AI automation, or both options enabled
		this.controlMode = "B";		//both human manual control and robot AI control enabled for the tutorial

		currentPlayer = h1;
		currentState = GameState.PLAYING; // mission state: ready to start
		currentImageH1 = 0;
		currentImageH2 = 0;
		for (int i = 0; i < ROWS; i++) {  //hide pictures in all rooms
			for (int j = 0; j < COLS; j++){
				squares[i][j].setBackground(Color.WHITE);
				squares[i][j].hidePics();
			}
		}
		squares[startRoomAI.getX()][startRoomAI.getY()].setBackground(Color.LIGHT_GRAY); //Gray out starting room to indicate it's explored
		repaint();
	}

	/**
	 * Method: checkTutorialActionApproved
	 * This is only relevant in tutorial mode.
	 * Manages the tutorial's overall sequence depending on user input and the current progress of the tutorial stage.
	 * @param command the given action that the user taking the tutorial tries to take.
	 * @return true if the action is in accordance with what the tutorial instructs, false otherwise.
	 */
	public boolean checkTutorialActionApproved(char command) {
		//First check which instruction is currently showing in the tutorial
		//(e.g. is the user asked to move the robot to a certain location? If so,
		//this will check to see if the user has moved the robot to that location, and then
		//enable the "Next" button)

//		String commandStr = String.valueOf(command);

		//Base case. The the status checker says there's nothing to do. So any action is automatically not approved.
		String actionReqd = this.tutorialStatusCheckerArr[tutorialInstrIndex][0];
		if (actionReqd == null) return false;

		//Now figure out what type of action is required and act accordingly
		switch(actionReqd) {
		case "M":	//Move to a specified spot. Only move command will be approved (forward, left, or right)
			return (command == 'F' || command == 'L' || command == 'R');
		case "S":	//Shoot towards a specified spot. ON
			return (command == 'S');
		case "R":	//rotate to the right
			return (command == 'R');
		case "G":	//Give aid while in a specific spot
			return (command == 'G');
		case "A":	//Restart tutorial
			return (command == 'A');
		}

		return false;	//placeholder
	}

	/**
	 * Only used in tutorial mode, after the user has taken some approved action.
	 * (see above method for more details on that)
	 * This will move the tutorial forward by triggering subsequent action, e.g.
	 * re-enabling "next" button to allow the user to proceed to the next part of the tutorial
	 */
	public void checkTutorialStatus(String command) {
		//TODO
		//Base case: if robot has been destroyed, then display game over message and
		//allow user to restart tutorial
		if (currentState == GameState.DRAW) {	//this means robot has been killed
			this.showMissionFailureInstruction();
			return;
		}

		//First, check what the current stage of the tutorial's instructions were.
		String[] actionRequested = tutorialStatusCheckerArr[tutorialInstrIndex];
		String actionTakenByUser = command;

		System.out.println("actionRequested: " + actionRequested[0]);
		System.out.println("actionTakenByUser: " + actionTakenByUser);

		//Another base case: the action requested doesn't equal the action the user took In this case return immediately
		//(only exception is if the action requested is "M" (Move) and the user action was F, R or L. In this case, don't return immediately.)
		if (actionRequested[0].equals("M") && !"FLR".contains(actionTakenByUser)) return;
		if (!actionRequested[0].equals("M") && !actionRequested[0].equals(actionTakenByUser)) return;

		System.out.println("Beginning switch statement...");
		switch(actionRequested[0]) {
		case "M":
			//Will re-enable next button if the user has moved to a specific location
			int[] locRequired = {Integer.parseInt(actionRequested[1]), Integer.parseInt(actionRequested[2])};
			int locRow = locRequired[0];
			int locCol = locRequired[1];
			DrawRoom cellReqd = SAR.this.squares[locRow][locCol];
			System.out.println(cellReqd);
			repaint();
			SwingUtilities.invokeLater(new Runnable()
		    {
		      public void run()
		      {
		    	  if (cellReqd.pics[2][0].isVisible()) {
		    		  btnNext.setEnabled(true);
		    		  SAR.this.removeKeyListeners(true); //remove key listeners
		    	  }
		      }
		    });
			break;
		case "S":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			btnNext.setEnabled(true);
  		  	SAR.this.removeKeyListeners(true); //remove key listeners
			break;
		case "R":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			btnNext.setEnabled(true);
  		  	SAR.this.removeKeyListeners(true); //remove key listeners
			break;
		case "G":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			btnNext.setEnabled(true);
  		  	SAR.this.removeKeyListeners(true); //remove key listeners
			break;
		case "A":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			btnNext.setEnabled(true);
  		  	SAR.this.removeKeyListeners(true); //remove key listeners
			break;
		}
	}

	/**
	 * Method: initMission
	 * Initialize the mission map contents and status.
	 * */
	public void initMission() {
		/* 1st parameter: is the cell at (0,0) always empty?
		 * 2nd parameter: will this board be accessed by the AI?
		 * See the constructor comments in Board.java for more details. */
//		board = new Board(this.options.toUpperCase().contains("00") ? true : false, false);
		this.numOfMoves = 0;

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
		board = new Board(new String[][]
				{{"", "", "", "P", "G", "P"},
				{"", "", "P", "W", "W", ""},
				{"", "", "", "", "", ""},
				{"", "", "", "", "", ""},
				{"", "", "", "P", "P", ""},
				{"P", "", "", "", "", ""},
				});


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

		/* If end-user typed a string containing "00", the starting cell is always (0,0).*/
		if(this.options.toUpperCase().contains("00")) {
			startRoom = board.getRoom(0, 0);

			/* Now create a board with incomplete information that will be perceived / accessed by the AI
			 * who will use deductive logic to navigate the board and avoid dangers.
			 * 1st parameter: is cell at (0,0) always empty?
			 * 2nd parameter: will this board be accessed / perceived by the AI? */
			boardPerceivedByAI = new Board(true, true);
		}
		else {
			//The players start the mission at a RANDOM position on the board
			startRoom = board.getRoom((int)(Math.random() * ROWS), (int)(Math.random() * COLS));

			/*If players start on a random cell, that cell might already have
			 * a pit or victim or wastes in it. Meaning the mission is over before it even begins...which we don't want.
			 * So this while-loop is designed to ensure that the random starting cell is safe AND
			 * that it doesn't have the victim in it. */
			while(startRoom.hasWastes() || startRoom.isPit() || startRoom.hasVictim()) {
				startRoom = board.getRoom((int)(Math.random() * ROWS), (int)(Math.random() * COLS));
			}

			/* Create a board with incomplete information that will be perceived / accessed by the AI player
			 * who will use deductive logic to navigate the board and avoid dangers.
			 * 1st parameter: is cell at (0,0) always empty?
			 * 2nd parameter: will this board be accessed / perceived by the AI? */
			boardPerceivedByAI = new Board(false, true);
		}

		startRoom.setHints();	//custom method to display information about this room on the GUI.

		CellAsPerceivedByAI startRoomAI = boardPerceivedByAI.getRoomAI(startRoom.getX(), startRoom.getY());	//startRoomAI is the same location as startRoom
		startRoomAI.setExplored(true, startRoom);	//Custom method to set this cell as having been explored by the robot.

		/* Every time a room / cell is explored, the AI will use the custom method below to logically deduce and assign
		 * the probability of monsters and pits in every neighboring room. See the CellAsPerceivedByAI.java class for more details. */
		startRoomAI.assignProbabilityToNeighbors(this.board, this.boardPerceivedByAI);

		this.createPlayers(startRoom, startRoomAI);	//create players

		for (int i = 0; i < ROWS; i++) {  //hide pictures in all rooms
			for (int j = 0; j < COLS; j++){
				squares[i][j].setBackground(Color.WHITE);
				squares[i][j].hidePics();
			}
		}
		squares[startRoomAI.getX()][startRoomAI.getY()].setBackground(Color.LIGHT_GRAY); //Gray out starting room to indicate it's explored
		repaint();
	}


	/**
	 * Method: createPlayers
	 * Creates two players, h1 and h2.
	 * @param startRoom the room where the players will start the mission.
	 * @param startRoomAI the room where the players will start the mission. This is a CellAsPerceivedByAI object and will be accessed by AI player
	 */
	public void createPlayers(Cell startRoom, CellAsPerceivedByAI startRoomAI) {

		/* Create two players. Each player can be either a human or AI. */
		h1 = new Player(startRoom, startRoomAI, this.p1Name);
		h1.setAI(true);	//either or both players can be an AI.
		h1.setAggressiveModeOn(this.options.toUpperCase().contains("A1") ? true : false);//this can be toggled on or off. See comments in the Player class beginning with "DESIGN DECISION".
		h2 = new Player(startRoom, startRoomAI, this.p2Name);
		h2.setAI(true);	//either or both players can be an AI.
		h2.setAggressiveModeOn(this.options.toUpperCase().contains("A2") ? true : false);//this can be toggled on or off. See comments in the Player class beginning with "DESIGN DECISION".

		/* OPTIONAL: if you want a 1-player mission, have the other player quit right away.
		 * This gets rid of player's graphics from being shown on the GUI. */
		if(this.options.toUpperCase().contains("S")) {
			h2.setOutOfGame(true);
		}

		//Manual control, Robot AI automation, or both options enabled

		if (this.options.toUpperCase().contains("H")) {
			this.controlMode = "H";		//human manual control only
		} else if (this.options.toUpperCase().contains("R")) {
			this.controlMode = "R";		//robot only
		} else {		// options contains "B"
			this.controlMode = "B";		//both modes enabled
		}

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
	 * @return whether the command was valid. For example, 'X' is not a valid command char.
	 */
	public boolean updateGame(Player currentPlayer, char command) {
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
			} else {
				(new DecontaminantMissedPopupThread()).start();
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
				System.out.printf("========================================\nSPACEBAR PRESSED. Activating AI for %s...\n"
						+ "========================================\n",
						currentPlayer.getName());
				validKeyTyped = true;
				/* See the Player.java class for the custom method getAction(). It returns a command char.
				 * This is the heart of AI's algorithm. */
				int percentRandom = 0;	//initialize the % of time the AI robot will act randomly
				//If the options were set before the mission to allow random acts by robot a certain % of the time,
				//extract that percent value now and execute updateGame() method with it as parameter
				if (this.options.toUpperCase().contains("%%")) {
					percentRandom = Integer.parseInt(this.options.substring(this.options.indexOf(" ") + 1, this.options.indexOf("%")));
					System.out.printf("Random action chance: %s%%", percentRandom);
				}
				this.updateGame(currentPlayer, currentPlayer.getAction(this.board, this.boardPerceivedByAI, percentRandom));
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
					squares[i][j].setBackground(Color.LIGHT_GRAY);	//gray out explored room
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
			(new PopupThread()).start();	//Popup message game over, needs to run in new thread for thread safety
			if (options.contains("T")) this.checkTutorialStatus(String.valueOf(command));	//If this was a tutorial mode, update tutorial status
		} else if (isDraw()) {  // if "draw", it means neither player 1 nor 2 (if in 2-player mode) has found the victim and are both dead. Mission fail
			currentState = GameState.DRAW;
			if (options.contains("T")) this.checkTutorialStatus(String.valueOf(command));	//If this was a tutorial mode, update tutorial status
			else (new PopupThread()).start();	//Popup message game over, needs to run in new thread for thread safety
		}
		// Otherwise, no change to current state (still GameState.PLAYING).
		return validKeyTyped;
	}
	//end public boolean updateGame

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

			imageH1[0] = changeImageSize("hunter10.png");
			imageH1[1] = changeImageSize("hunter11.png");
			imageH1[2] = changeImageSize("hunter12.png");
			imageH1[3] = changeImageSize("hunter13.png");
			imageH2[0] = changeImageSize("hunter20.jpg");
			imageH2[1] = changeImageSize("hunter21.jpg");
			imageH2[2] = changeImageSize("hunter22.jpg");
			imageH2[3] = changeImageSize("hunter23.jpg");

			this.setLayout(new GridLayout(3,3));

			pics[0][0] = new JLabel(changeImageSize("temperature.png"));
			pics[0][1] = new JLabel(changeImageSize("stench.png"));
			pics[0][2] = new JLabel(changeImageSize("victim.png"));
			pics[1][0] = new JLabel(changeImageSize("heat.png"));
			pics[1][1] = new JLabel(changeImageSize("disinfect.png"));
			pics[1][2] = new JLabel(changeImageSize("success.png"));
			pics[2][0] = new JLabel(imageH1[currentImageH1]);
			pics[2][1] = new JLabel(changeImageSize("wastes.png"));
			pics[2][2] = new JLabel(imageH2[currentImageH2]);

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++){
					pics[i][j].setVisible(false);
					add(pics[i][j]);
				}
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


	public ImageIcon changeImageSize(String fileName){ //change the image size to be fitted for the room
		ImageIcon myIcon = new ImageIcon(fileName);
		Image img = myIcon.getImage();
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
			System.out.println("add key listeners invoked!");
			keyAdapter = new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent e){
					char command;
					if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
						System.out.println("FORWARD");
						command = 'F';
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						System.out.println("TURN RIGHT");
						command = 'R';
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT){
						System.out.println("TURN LEFT");
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
						if (SAR.this.options.contains("T")) {
							boolean actionApproved = SAR.this.checkTutorialActionApproved(command);
							if (!actionApproved) return;
						}

						//Depending on whether the control mode is "H" for manual control only, "R" for automated control only,
						//or "B" for both modes enabled, certain commands will be disabled / enabled accordingly.
						//For instance, in mode "R", only the spacebar command will be recognized.
						if ( ("H".contains(SAR.this.controlMode) && "FRLGSQO" .contains(String.valueOf(command))) ||
							 ("R".contains(SAR.this.controlMode) &&       " " .contains(String.valueOf(command))) ||
							 ("B".contains(SAR.this.controlMode) && "FRLGSQO ".contains(String.valueOf(command))) ) {
							boolean validKeyTyped = updateGame(currentPlayer, command); // invoke update method with the given command
							// Switch player (in the event that we have a 2-player mission)
							if(validKeyTyped) {
								SAR.this.numOfMoves++;
								Player nextPlayer = (currentPlayer == h1 ? h2 : h1);
								if (nextPlayer.isAlive())
									currentPlayer = nextPlayer;
								System.out.printf("Total no. of moves so far: %s\n", numOfMoves);
							}
						}
						//end nested if

						//If we're in tutorial mode, we need to check to see
						//if user has completed what the tutorial asked them to do
						if (SAR.this.options.contains("T")) {
							checkTutorialStatus(String.valueOf(command));
						}
					} else if (Character.toUpperCase(command) == 'A') {       // this command can be used when mission is over to restart mission
						if (SAR.this.options.contains("T")) {
							initTutorial();	//If this was a tutorial mode, restart the same tutorial
							return;
						}
						else initMission();									//otherwise, initialize mission again
					} else if(Character.toUpperCase(command) == 'O' && !options.contains("T")) {	// open options window as long as this isn't' tutorial mode
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
					statusBar.setForeground(Color.RED);
					statusBar.setText(h1.getName() + " has successfully completed the mission!");
				} else if (currentState == GameState.H2_WON) {
					statusBar.setForeground(Color.RED);
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
			JOptionPane.showMessageDialog(null, "Missed!", "", JOptionPane.PLAIN_MESSAGE);
		}
	}

	class DecontaminantHitPopupThread extends Thread {
		public void run() {
			JOptionPane.showMessageDialog(null, "Shot on target!", "", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/** The entry main() method.
	 *  UPDATE: Recommended that you use SARMain.java's options GUI to run the mission instead of running it here. */
	public static void main(String[] args) {
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Scanner sc = new Scanner(System.in);
				System.out.println("Enter options in a single String. Options are as follows:\n"
						+ "======================================================================================\n"
						+ "S : Single Player Mode (default mode is 2-player mode)\n"
						+ "T : Enables a special Tutorial Mode (and disables all the other settings)\n"
						+ "00 : Player(s) always start at room (0,0) (default starting room is chosen at random)\n"
						+ "A1: Aggressive Mode is ON for Player 1 (default is OFF)\n"
						+ "A2: Aggressive Mode is ON for Player 2 (default is OFF)\n"
						+ "(0% - 100%) : the % of time that the AI robot will act randomly for one time-step\n"
						+ "H, R, or B: Human manual control only, Robot AI only, or Both\n"
						+ "======================================================================================\n"
						+ "For example, if you want a single-player mission, random starting room, and aggressive mode,\n"
						+ "where the robot acts randomly 10% of the time, and AI mode only is enabled,\n"
						+ "then type the following: S A1 10% R");
				String options = sc.nextLine();
				sc.close();
				System.out.println("Now loading mission. Please wait...");
				new SAR(options, "Robot 1", "Robot 2");
			}
		});
	}
}