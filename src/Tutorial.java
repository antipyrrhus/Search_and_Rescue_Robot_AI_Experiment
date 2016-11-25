import javax.swing.SwingUtilities;

/** Class: Tutorial.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course:
 *  Written / Updated: Nov 22, 2016
 *
 *  This Class - the Tutorial class. Used for tutorial mode.
 *  Purpose -
 */
public class Tutorial {
	private SAR sar;
	private String[] tutorialStrArr;
	private int totalPagesOfTutorial, currentPage;
	private int tutorialInstrIndex;
	private boolean[] tutorialEnableBtnArr;
	private String[][] tutorialStatusCheckerArr;

	/**
	 * Constructor
	 * @param sar a SAR.java object
	 */
	public Tutorial(SAR sar) {
		this.sar = sar;
		tutorialStrArr = InstructionMsg.TUTORIAL;

		//Note: why length - 1? Because the tutorialStrArr contains two String elements that
		//signify the end of the tutorial: 1) mission success, and 2) mission failure, retry.
		//Only one of the above, not both, will be shown.
		this.totalPagesOfTutorial = this.tutorialStrArr.length - 1;
		this.currentPage = 0;

		sar.initNumOfMoves();		//set counter to zero (this counts the no. of moves the robot has taken thus far)

		//make next button and page no. information visible
		sar.setBtnNextVisible(true);
		sar.setPageNoVisible(true);

		sar.removeKeyListeners(true);	//temporarily disable keyboard command listeners
		sar.setInstrRowCol(3, 50);	//set the dimension of the instruction text area

		//Buttons will be disabled or enabled during various points of the tutorial, as follows.
		tutorialEnableBtnArr = new boolean[]{
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
		tutorialStatusCheckerArr = new String[][]{
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
		sar.instrSetFont(sar.instructions.getFont().deriveFont(18f));

		//Create custom board for tutorial.
		sar.board = new Board(new String[][]
				{{"", "", "P", "", "", ""},
				{"", "W", "", "", "", ""},
				{"", "", "", "", "", ""},
				{"P", "", "P", "", "G", ""},
				{"P", "", "", "", "", ""},
				{"", "", "W", "", "", ""},
				});

		Cell startRoom;
		CellAsPerceivedByAI startRoomAI;
		Cell[] startRooms = sar.setStartRoomAndBoardAI(true);
		startRoom = startRooms[0];
		startRoomAI = (CellAsPerceivedByAI)startRooms[1];

		sar.createPlayers(startRoom, startRoomAI, sar.p1Name, sar.p2Name, false, false, true, "H");

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
		//Base case. If the mission ended in success (2nd-to last element in the tutorialStrArr array)
		//then we will go on to the next stage of the tutorial instead of showing the last element in the array
		//(which is a mission failed message).
		if (tutorialInstrIndex == tutorialStrArr.length - 2) {
			sar.recordStats(this);
			sar.initDrillHuman();
			return;
		}

		//Another base case to prevent out of index error
		if (tutorialInstrIndex + 1 >= tutorialStrArr.length) {
			tutorialInstrIndex = tutorialStrArr.length - 1;
			return;
		}

		tutorialInstrIndex++;
		sar.setInstrText(tutorialStrArr[tutorialInstrIndex]);
		sar.enableBtnNext(this.tutorialEnableBtnArr[tutorialInstrIndex]);
		this.removeKeyListeners(sar.btnNext.isEnabled());
		this.currentPage++;
		this.setPageNo(currentPage);
	}

	protected void setPageNo(int currentPage) {
		sar.pageNo.setText("Step " + currentPage + " of " + this.totalPagesOfTutorial);
	}

	/**
	 * Used in tutorial mode only. Shows mission failed message and allows user to restart tutorial
	 */
	protected void showMissionFailureInstruction() {
		tutorialInstrIndex = tutorialStrArr.length - 1;
		sar.setInstrText(tutorialStrArr[tutorialInstrIndex]);
		sar.enableBtnNext(false);	//disable next button since game over
		this.removeKeyListeners(false);	//do not remove (aka, add back) key listeners to allow user to restart
		this.currentPage = totalPagesOfTutorial;
		setPageNo(currentPage);
	}

	protected void removeKeyListeners(boolean b) {
		sar.removeKeyListeners(b);
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
		//Base case: if robot has been destroyed, then display game over message and
		//allow user to restart tutorial
		if (sar.currentState == SAR.GameState.DRAW) {	//this means robot has been killed
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
			SAR.DrawRoom cellReqd = sar.squares[locRow][locCol];
			System.out.println(cellReqd);
			sar.repaint();
			SwingUtilities.invokeLater(new Runnable()
		    {
		      public void run()
		      {
		    	  if (cellReqd.pics[2][0].isVisible()) {
		    		  sar.enableBtnNext(true);
		    		  removeKeyListeners(true); //remove key listeners
		    	  }
		      }
		    });
			break;
		case "S":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			sar.enableBtnNext(true);
  		  	removeKeyListeners(true); //remove key listeners
			break;
		case "R":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			sar.enableBtnNext(true);
  		  	removeKeyListeners(true); //remove key listeners
			break;
		case "G":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			sar.enableBtnNext(true);
  		  	removeKeyListeners(true); //remove key listeners
			break;
		case "A":
			//Immediately remove key listeners and enable buttons (the tutorial is designed so that the robot WILL be facing the correct direction
			//and be in the correct location when the user is instructed to use this command.)
			sar.enableBtnNext(true);
  		  	removeKeyListeners(true); //remove key listeners
			break;
		}
	}
}
