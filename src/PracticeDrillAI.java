/** Class: PracticeDrillAI.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *  Written / Updated: Nov 23, 2016
 *
 *  This Class - Practice Drill class (AI mode only). Used as part of the tutorial.
 */
public class PracticeDrillAI {
	private SAR sar;
	private String[] drillInstructionArr;
	private int totalPagesOfInstr, currentPage;
	private int drillInstrIndex;
	private boolean[] enableBtnArr;

	/**
	 * Constructor
	 * @param sar a SAR class object
	 */
	public PracticeDrillAI(SAR sar) {
		this.sar = sar;
		drillInstructionArr = InstructionMsg.DRILL_AI;

		this.totalPagesOfInstr = this.drillInstructionArr.length;
		this.currentPage = 0;

		sar.initNumOfMoves();
		sar.setBtnNextVisible(true);
		sar.setPageNoVisible(true);

		sar.removeKeyListeners(true);	//temporarily disable keyboard command listeners
		sar.setInstrRowCol(3, 50);	//set the dimension of the instruction text area

		//Buttons will be disabled or enabled during various points of the drill, as follows.
		enableBtnArr = new boolean[]{
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				false,
				true
		};

		this.drillInstrIndex = -1;
		sar.instrSetFont(sar.instructions.getFont().deriveFont(18f));

		sar.board = new Board(new String[][]
				{{"", "", "", "", "", ""},
				 {"", "", "", "", "", ""},
				 {"", "P", "P", "", "", ""},
				 {"", "", "", "", "", ""},
				 {"W", "", "P", "", "", ""},
				 {"G", "", "", "W", "", ""},
				});

		Cell startRoom;
		CellAsPerceivedByAI startRoomAI;
		Cell[] startRooms = sar.setStartRoomAndBoardAI(true);
		startRoom = startRooms[0];
		startRoomAI = (CellAsPerceivedByAI)startRooms[1];

		sar.createPlayers(startRoom, startRoomAI, sar.p1Name, sar.p2Name, false, false, true, "R");	//"R" means Robot AI only is enabled (no manual control)
		sar.hideAllPics();
		sar.greyOutCell(startRoomAI.getX(), startRoomAI.getY());	//gray out start room to indicate it's been explored
		sar.repaint();
		showNextInstruction();
	}


	/**
	 * Method: showNextInstruction
	 * Used in tutorial mode only. Shows the next set of instructions.
	 */
	protected void showNextInstruction() {
		//Base case. If the mission is over (success or failure), show the next part.
		if (drillInstrIndex == drillInstructionArr.length - 1) {
			sar.recordStats(this);
			sar.initFinalMission();
			return;
		}

		drillInstrIndex++;
		sar.setInstrText(drillInstructionArr[drillInstrIndex]);
		sar.enableBtnNext(this.enableBtnArr[drillInstrIndex]);
		this.removeKeyListeners(sar.btnNext.isEnabled());
		this.currentPage++;
		this.setPageNo(currentPage);
	}

	protected void setPageNo(int currentPage) {
		sar.pageNo.setText("Step " + currentPage + " of " + this.totalPagesOfInstr);
	}

	protected void removeKeyListeners(boolean b) {
		sar.removeKeyListeners(b);
	}


	/**
	 * Method: checkMissionActionApproved
	 * This is only relevant in tutorial mode.
	 * Manages the tutorial's overall sequence depending on user input and the current progress of the tutorial stage.
	 * @param command the given action that the user taking the tutorial tries to take.
	 * @return true if the action is in accordance with what the tutorial instructs, false otherwise.
	 */
	public boolean checkMissionActionApproved(char command) {
		//only spacebar is allowed for this drill
		if (command == ' ') return true;
		return false;
	}

	public void checkStatus(String command) {
		//If mission is over (success or fail, doesn't matter), then show next instruction. Otherwise do nothing
		if (sar.currentState == SAR.GameState.DRAW || sar.currentState == SAR.GameState.H1_WON) {
			showNextInstruction();
		}
	}
	//end public void checkStatus
}
