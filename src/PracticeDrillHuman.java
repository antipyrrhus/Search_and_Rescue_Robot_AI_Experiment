import java.awt.Color;

/** Class: PracticeDrillHuman.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course:
 *  Written / Updated: Nov 22, 2016
 *
 *  This Class -
 *  Purpose -
 */
public class PracticeDrillHuman {
	private SAR sar;
	private String[] drillInstructionArr;
	private int totalPagesOfInstr, currentPage;
	private int drillInstrIndex;
	private boolean[] enableBtnArr;

	public PracticeDrillHuman(SAR sar) {
		this.sar = sar;
		drillInstructionArr = InstructionMsg.DRILL_HUMAN;

		this.totalPagesOfInstr = this.drillInstructionArr.length;
		this.currentPage = 0;

		sar.btnNext.setVisible(true);	//This button is only used in tutorial mode. Enable it
		sar.pageNo.setVisible(true);
		sar.btnNext.addActionListener(e -> {
			showNextInstruction();
		});

		this.removeKeyListeners(true);	//temporarily disable keyboard command listeners

		sar.instructions.setRows(3);
		sar.instructions.setColumns(50);

		//Buttons will be disabled or enabled during various points of the drill, as follows.
		enableBtnArr = new boolean[]{
				true,
				false
		};


		this.drillInstrIndex = -1;
		sar.instructions.setFont(sar.instructions.getFont().deriveFont(18f));
		this.showNextInstruction();

		sar.board = new Board(new String[][]
				{{"", "", "", "", "", ""},
				 {"", "", "", "", "", ""},
				 {"", "P", "P", "", "", ""},
				 {"", "", "", "", "", ""},
				 {"W", "", "P", "", "", ""},
				 {"G", "", "", "W", "", ""},
				});
		sar.boardPerceivedByAI = new Board(true, true);

		Cell startRoom = sar.board.getRoom(0, 0);
		startRoom.setHints();	//custom method to display information about this room on the GUI.
		CellAsPerceivedByAI startRoomAI = sar.boardPerceivedByAI.getRoomAI(startRoom.getX(), startRoom.getY());	//startRoomAI is the same location as startRoom
		startRoomAI.setExplored(true, startRoom);	//Custom method to set this cell as having been explored by the robot.

		/* Every time a room / cell is explored, the AI will use the custom method below to logically deduce and assign
		 * the probability of monsters and pits in every neighboring room. See the CellAsPerceivedByAI.java class for more details. */
		startRoomAI.assignProbabilityToNeighbors(sar.board, sar.boardPerceivedByAI);

		/* Create two players. Each player can be either a human or AI. */
		sar.h1 = new Player(startRoom, startRoomAI, sar.p1Name);
		sar.h1.setAI(true);	//either or both players can be an AI.
		sar.h1.setAggressiveModeOn(false);  //We'll set aggressive mode to OFF for this tutorial
		sar.h2 = new Player(startRoom, startRoomAI, sar.p2Name);
		sar.h2.setAI(true);	//either or both players can be an AI.
		sar.h2.setAggressiveModeOn(false);
		sar.h2.setOutOfGame(true);	//Player 2 will not be involved for the tutorial

		//Manual control, Robot AI automation, or both options enabled
		sar.controlMode = "H";		//both human manual control and robot AI control enabled for the tutorial

		sar.currentPlayer = sar.h1;
		sar.currentState = SAR.GameState.PLAYING; // mission state: ready to start
		sar.currentImageH1 = 0;
		sar.currentImageH2 = 0;
		for (int i = 0; i < SAR.ROWS; i++) {  //hide pictures in all rooms
			for (int j = 0; j < SAR.COLS; j++){
				sar.squares[i][j].setBackground(Color.WHITE);
				sar.squares[i][j].hidePics();
			}
		}
		sar.squares[startRoomAI.getX()][startRoomAI.getY()].setBackground(Color.LIGHT_GRAY); //Gray out starting room to indicate it's explored
		sar.repaint();
	}


	/**
	 * Method: showNextInstruction
	 * Used in tutorial mode only. Shows the next set of instructions.
	 */
	protected void showNextInstruction() {
		//Base case. If the mission is over (success or failure), show the next part.
		if (drillInstrIndex == drillInstructionArr.length - 1) {
			//TODO show next part of the drill
			return;
		}

		//Another base case to prevent out of index error
		if (drillInstrIndex + 1 >= drillInstructionArr.length) {
			drillInstrIndex = drillInstructionArr.length - 1;
			return;
		}

		drillInstrIndex++;
//		System.out.println(drillInstrIndex);
		sar.instructions.setText(drillInstructionArr[drillInstrIndex]);
		sar.btnNext.setEnabled(this.enableBtnArr[drillInstrIndex]);
		this.removeKeyListeners(sar.btnNext.isEnabled());
		this.currentPage++;
		this.setPageNo(currentPage);
	}

	protected void setPageNo(int currentPage) {
		sar.pageNo.setText("Step " + currentPage + " of " + this.totalPagesOfInstr);
	}

	protected void removeKeyListeners(boolean b) {
		if (b == true) {
			sar.canvas.removeKeyListeners();
			System.out.println("key listeners removed");
		} else {
			sar.canvas.removeKeyListeners(); //first reset all listeners before adding them (this is to prevent duplicate key listeners
			sar.canvas.addKeyListeners();
			System.out.println("key listeners added");
		}
	}


	/**
	 * Method: checkTutorialActionApproved
	 * This is only relevant in tutorial mode.
	 * Manages the tutorial's overall sequence depending on user input and the current progress of the tutorial stage.
	 * @param command the given action that the user taking the tutorial tries to take.
	 * @return true if the action is in accordance with what the tutorial instructs, false otherwise.
	 */
	public boolean checkMissionActionApproved(char command) {
		//only moving Forward, rotate Left or Right, Shoot decontaminant, and Give aid buttons are valid for the drill
		if (command == 'F' || command == 'L' || command == 'R' || command == 'S' || command == 'G') {
			return true;
		}
		return false;
	}
}
