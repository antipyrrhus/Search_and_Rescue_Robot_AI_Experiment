import java.util.ArrayList;
/** Class: Player.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *
 *  This class - the Player class.
 *  Purpose - Player for the SAR.java game class. Contains a logical deduction algorithm
 *  to traverse the cells in the event that this player is an AI.
 */
public class Player
{
	private String name;
	private Cell currentRoom;	//the room the player is in right now. This room contains all the information.

	/* the room the player is in right now, AS PERCEIVED BY THE ROBOT. Some information may be hidden.
	 * Used for AI logical deduction algorithm. See CellAsPerceivedByAI.java class for more details. */
	private CellAsPerceivedByAI currentRoomAI;
	private int dir;	//the direction this robot is currently facing. 0 = Board.NORTH, 1 = Board.EAST, 2 = Board.SOUTH, 3 = Board.WEST
	private boolean alive;	//whether the robot player is alive or dead. Accessed by SAR.java to determine whether to freeze  player's graphics on the GUI.
	private boolean outOfGame;	//whether this player is participating in the game or not. Accessed by SAR.java to determine whether to erase the player's graphics on the GUI.
	private int disinfectant;	//how many disinfectants the player has
	private boolean victim;	//whether this player has found the victim or not.
	private boolean wastesKiller;	//whether this player killed a wastes.
	private boolean isAI;		//whether this player is an AI player or not.
	private boolean aggressiveModeOn;	//whether this player (assumed to be AI) is eager to use disinfectants or whether he prefers to conserve them.
	private String lastActionTaken;

	private Pathfind pf = new Pathfind();	//Pathfind.java class. Used for AI pathfinding.

	/**
	 * 3-arg constructor.
	 * @param start The room in which the player will start the game. Contains open and complete information that is often unknown to the AI.
	 * @param start2 The room in which the player will start the game...as perceived by the player AI. Used for AI logic / deductions.
	 * @param name The name of this player.
	 */
	public Player (Cell start, CellAsPerceivedByAI start2, String name)
	{
		this.name = name;
		currentRoom = start;
		currentRoomAI = start2;

		/* Custom method to set this room as having been explored. Once set to explored, the AI
		 * perceives everything in this room correctly. */
		currentRoomAI.setExplored(true, currentRoom);

		dir = Board.EAST;	//By default, every player starts out facing the east direction
		alive = true;	//Every player starts out alive...duh
		disinfectant = 2; //Each player has 2 disinfectants
		victim = false;	//Initially, the player has not found the victim. Getting to the victim is the goal of the mission.
		wastesKiller = false;	//Initially, the player hasn't disinfected any chemical wastes.
		aggressiveModeOn = false;	//By default, the player will conserve disinfectants instead of being aggressive.
		lastActionTaken = "";
	}

	/**
	 * Overloaded 3-arg constructor.
	 * @param start The room in which the player will start the game. Contains complete information that is often unknown to the AI.
	 * @param start2 The room in which the player will start the game...as perceived by the player AI. Used for AI logic / deductions.
	 * @param name The name of this player.
	 * @param aggressiveModeOn boolean value. If set to true and this player is AI, then it will use disinfectants liberally (albeit intelligently).
	 */
	public Player(Cell start, CellAsPerceivedByAI start2, String name, boolean aggressiveModeOn) {
		this(start, start2, name);	//invoke 3-arg constructor
		this.aggressiveModeOn = aggressiveModeOn;
	}

	/**
	 * Method: forward
	 * Moves the player forward one step into the next room. If there is no room in the direction the player is facing, does nothing.
	 * @param boardPerceivedByAI The Board object, consisting of RoomsAsPerceivedByAI. Used to update AI's perceptions.
	 * @return true if the player was able to move forward, false otherwise.
	 */
	public boolean forward(Board boardPerceivedByAI){

		lastActionTaken = "F";

		/* Get the adjacent Room (in accordance with the direction the player is currently facing.
		 * For example, if the player is facing Board.NORTH (equivalent to 0. See Board.java class for more details),
		 * then the code below is equivalent to the following:
		 * Room next = currentRoom.getNeighbors()[0];
		 * where currentRoom.getNeighbors() is a custom method that returns an array composed of 4 elements,
		 * where 0th element = the north room, 1st element = east room, etc..
		 *
		 * Note that some of these Room elements may be null. For example, if the current room is located in (0, 0),
		 * then there is no room to the north or to the west. So the 0th and 3rd element will be null. */
		Cell next = currentRoom.getNeighbors()[dir];
		if(next == null){	//if the adjacent room is null, then don't move forward and just return false
			return false;
		}
		//Otherwise...move to the next room
		currentRoom = next;
		currentRoom.setHints();  	//display the perceptions (Stench, Breeze, etc.) to the GUI.

		/* Get the current room as perceived by the AI (in the event this player is AI).
		 * Uses custom Board method getRoomAI() to get the correct row and col index of the
		 * room which the player just moved into.
		 * Remember that currentRoomAI is a RoomAsPerceivedByAI.java object, a subclass of Room.java.
		 * This subclass deliberately hides information about a given room from the AI (provided that
		 * the AI hasn't explored it), so that the AI must use logic to deduce the risk of the room.
		 * Once a room of this subclass is explored, though, the AI has full knowledge of the room. */
		currentRoomAI = boardPerceivedByAI.getRoomAI(currentRoom.getX(), currentRoom.getY());
		currentRoomAI.setExplored(true, currentRoom);	//Set this room to explored. The AI now has full knowledge of this Room.

		/* If this room has a wastes or a pit, then the player is dead. */
		if (currentRoom.hasWastes() || currentRoom.isPit())	alive = false;

		return true;	//return true to indicate that the player successfully moved into the room
	}

	/**
	 * Method: turnLeft
	 * Turns the player to the left direction.
	 */
	public void turnLeft(){
		lastActionTaken = "L";
		dir = (dir == 0 ? 3 : dir - 1);	//Remember: 0 indicates Board.NORTH, 1 = Board.EAST, 2 = Board.SOUTH, 3 = Board.WEST.
	}

	/**
	 * Method: turnRight
	 * Turns the player to the right direction.
	 */
	public void turnRight(){
		lastActionTaken = "R";
		dir = (dir == 3 ? 0 : dir + 1);
	}

	/**
	 * Method: getCurrentRoom
	 * @return the Room this player is currently in.
	 */
	public Cell getCurrentRoom(){
		return currentRoom;
	}

	/**
	 * Method: hasVictim
	 * @return true if player has obtained victim, false otherwise.
	 */
	public boolean hasVictim(){
		return victim;
	}

	/**
	 * Method: giveAid
	 * @return attempts to obtain victim from the current room, and returns true if successful. If there is no victim in the room, returns false.
	 */
	public boolean giveAid(){
		lastActionTaken = "G";
		victim = currentRoom.giveAid();
		return victim;
	}

	/**
	 * Method: isWasteKiller
	 * @return true if this player has killed a wastes, false otherwise.
	 */
	public boolean isWasteKiller(){
		return wastesKiller;
	}

	/**
	 * Method: resetWasteKiller
	 * Sets wastesKiller attribute to false.
	 */
	public void resetWasteKiller(){
		wastesKiller = false;
	}

	/**
	 * Method: isAlive
	 * @return true if the player is alive, false otherwise
	 */
	public boolean isAlive(){
		return alive;
	}

	/**
	 * Method: isOutOfGame
	 * This attribute determines whether the player's graphics will be completely hidden from the game board GUI.
	 * @return whether or not this player is participating in this game.
	 */
	public boolean isOutOfGame() {
		return outOfGame;
	}

	/**
	 * Method: setOutOfGame
	 * Sets whether this player is participating in this game.
	 * This attribute determines whether the player's graphics will be completely hidden from the game board GUI.
	 * @param outOfGame the outOfGame to set
	 */
	public void setOutOfGame(boolean outOfGame) {
		this.outOfGame = outOfGame;
		if(outOfGame == true) this.alive = false;
	}

	/**
	 * Method: hasDisinfectant
	 * @return true if player has at least one disinfectant, false otherwise.
	 */
	public boolean hasDisinfectant(){
		if (disinfectant > 0) return true;
		return false;
	}

	public int getNumOfShotsLeft() {
		return disinfectant;
	}

	/**
	 * Method: shoot
	 * Shoots the player's disinfectant in the direction the player is facing. If there is a wastes anywhere
	 * in the straight-line trajectory the disinfectant will travel, even if it's several rooms down the row or column,
	 * then that wastes will be killed.
	 *
	 * For example, if a player in room (0,0) shoots disinfectant while facing Board.EAST, then this method will check
	 * room (0,1), (0,2), and so on, until it either finds a room with a wastes and kills it, or it
	 * runs out of rooms to check.
	 */
	public int shoot(){
		lastActionTaken = "S";
		if(disinfectant == 0) return 0; //BASE CASE: if player has no disinfectant, nothing happens. Immediately return and exit this method.

		disinfectant --;	//If we get this far, player has disinfectants left. So decrement disinfectant count
//		Cell r = currentRoom;	//identify the current room the player is in
		Cell neighbor = this.currentRoom.getNeighbors()[dir];	//get adjacent room in the direction the robot is facing

		//check if the neighbor room is valid, and whether it's infected. If so, disinfect it and return
		if (neighbor != null && neighbor.hasWastes()) {
			neighbor.killWastes();
			this.wastesKiller = true;
			return 1;
		}

		//Neighbor room wasn't valid or it wasn't infected. We just wasted an ammo.
		return -1;

		/* Do a loop, going thru all the rooms in the direction the robot is facing,
		 * and check if the room has a wastes and if so, kill it and break out of this method. */
//		while(r != null){
//			if(r.hasWastes()){
//				r.killWastes();
//				wastesKiller = true;
//				return;
//			}
//			r = r.getNeighbors()[dir];	//go to the next room in the direction the player is facing.
//		}
	}

	/**
	 * Method: quit
	 * This player will commit seppuku and quit the game.
	 */
	public void quit(){
		alive = false;
	}

	/**
	 * Method: getDirectionString
	 * @return the String version of the direction the player is currently facing. For example, if this.dir == 1, then
	 * this method will return the corresponding String "EAST". Notice that Board.DIRS is a static String[] array.
	 */
	public String getDirectionString(){
		return Board.DIRS[dir];
	}

	/**
	 * Method: isAI
	 * @return whether this player is an AI.
	 */
	public boolean isAI() {
		return isAI;
	}

	/**
	 * Method: setAI
	 * Sets this player to AI mode or not, depending on the parameter.
	 * @param isAI the isAI to set
	 */
	public void setAI(boolean isAI) {
		this.isAI = isAI;
	}

	/**
	 * Method: isAggressiveModeOn
	 * @return whether this player is on aggressive mode or not. Aggressive means that the AI player will be more
	 * likely to use disinfectants when there are possible wastess in adjacent rooms, and that the player will actively
	 * SEEK OUT wastess to kill.
	 */
	public boolean isAggressiveModeOn() {
		return aggressiveModeOn;
	}

	/**
	 * Method: setAggressiveModeOn
	 * See the comments in this class beginning with "DESIGN DECISION" for more details on what this is is about.
	 * @param aggressiveModeOn the aggressiveModeOn to set
	 */
	public void setAggressiveModeOn(boolean aggressiveModeOn) {
		this.aggressiveModeOn = aggressiveModeOn;
	}

	/**
	 * Method: getName
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method: setName
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getLastActionTaken() {
		return this.lastActionTaken;
	}

	/**
	 * Method: getAction
	 * Given a Board object and assuming that this Player is an AI, considers and returns AI's next action.
	 * @param board the given Board object consisting of Room objects. See Board.java and Room.java for more details.
	 * @param boardPerceivedByAI Board object consisting of RoomAsPerceivedByAI objects. See RoomAsPerceivedByAI.java for more details.
	 * @param percentRandom an int indicating the % of time the AI robot will act randomly
	 * @return one of the following: 'R' (turn Right), 'L' (turn Left), 'F' (move Forward), 'G' (Give aid to victim once found), 'S' (Shoot disinfectant).
	 */
	public char getAction(Board board, Board boardPerceivedByAI, int percentRandom) {
		/* BASE CASE where percentRandom > 0. In this case return a random action some of the time. */
		if (percentRandom > 0) {
			char[] actions = {'R', 'L', 'F', 'S'};
			if ( (int)(Math.floor(Math.random() * 100)) <= percentRandom ) {
//				System.out.println("AI robot acts randomly....");
				int index = new java.util.Random().nextInt(actions.length);	//choose a random index from the actions array
				while ( (actions[index] == 'S' && !this.hasDisinfectant()) ) { //don't take action 'S' randomly if out of decontaminant
					index = new java.util.Random().nextInt(actions.length);	//choose another random index
				}
//				System.out.printf("Random action chosen is %s\n", actions[index]);
				return actions[index];
			}
		}
		/* BASE CASE where AI's current room happens to have the victim in it. Just give first aid by returning 'G'. */
		if(currentRoom.hasVictim()) return 'G';

		/* BASE CASE where the location of the victim is known to the AI, but the victim is not in the current room
		 * (so the AI can't just use 'G' to pick it up right then and there).
		 * This situation is pretty rare, but it may occur if the human player discovers the victim first,
		 * but doesn't pick it up for some reason. Or, perhaps the human player moved onto a room that
		 * has both a wastes AND a victim inside it, thus uncovering the victim but dying instantly. In this case,
		 * the AI wants to find a safe path to the victim ASAP. */
		outerloop:	//define outerloop.
			for(int i = 0; i < Board.ROWS; i++) {
				for(int j = 0; j < Board.COLS; j++) {
					/* If the AI knows that a given room has victim... */
					if(boardPerceivedByAI.getRoomAI(i, j).hasVictim()) {
//						System.out.println(this.name + " can see the location of the victim! Trying to find a safe path to the victim...");
						/* ...then use custom method (in Pathfind.java) to do a UCS search and see if
						 * a SAFE way to the victim can be found. If so, return the ArrayList containing the cost-optimal solution path,
						 * and then use the .get(1) command to get the room contained in index 1 of that ArrayList.
						 * Essentially, this command first confirms whether a safe path to the victim exists, then
						 * identifies the adjacent room to which the AI must move in its path to the victim. */
						ArrayList<CellAsPerceivedByAI> safePathAL = pf.ucs(currentRoomAI, boardPerceivedByAI.getRoomAI(i, j), boardPerceivedByAI, this.dir);
						if(safePathAL == null) {
//							System.out.println("No safe path to the victim is found at this time. The victim will have to wait a bit longer.");
							break outerloop;	//if there is no safe path to the victim, then break off this inner AND outer loop.
						}
						CellAsPerceivedByAI nextDestination = safePathAL.get(1);	//This is the room the AI must move on to next.
//						System.out.println("Safe path to the victim found! the Next room the AI will move on to is: " + nextDestination.getXY()); //testing

						/* Now use custom method to return the key command the AI must use in order to move on to the next room.
						 * If the key command is 'F' (forward) and the next room happens to have a wastes in it
						 * (NOTE: this will only occur if the next room IS the destination room that has the victim
						 * in it, AND if the room happens to have both victim and a wastes together.
						 * Excepting this situation, the path to the victim will be 100% safe.),
						 * then return 'S' (shoot) instead of 'F' provided the AI player has an disinfectant. */
						char tempMove = computeMove(nextDestination);
						if(tempMove == 'F' && nextDestination.hasWastes() && this.hasDisinfectant()) {
//							System.out.println("The AI SHOOTS the disinfectant!");
							return 'S';
						}
						return tempMove;
					}
					//end if(boardPerceivedByAI.getRoomAI(i, j).hasVictim())
				}
				//end for j
			}
		//end for i (aka outerloop)

		/* BASE CASE where AI player has found 100% safe unexplored room(s).
		 * Use custom method getLeastRiskyRooms() to get an arraylist of RoomAsPerceivedByAI objects that are
		 * least risky. If there exist two or more such rooms with identically low risks, then get all such rooms. */
		ArrayList<CellAsPerceivedByAI> leastRiskyRoomsAL = this.getLeastRiskyRooms(board, boardPerceivedByAI);
		/* Custom method to see if the 0th element is 100% safe. Since all elements in this array have
		 * identically low risks, if one element is 100% safe, then all elements are equally safe. */
		if(leastRiskyRoomsAL.get(0).isGuaranteedSafe()) {
			//			System.out.println("There exist unexplored room(s) that are 100% safe! Finding the closest such room and moving to it...");
			/* Uses two custom methods here.
			 *
			 * getNearestAdjRoom(): this method first identifies the room that takes the AI
			 * the fewest turns to get to, then identifies the solution path to that room, then
			 * returns the room (which is part of that solution path) that is adjacent to the AI's current room.
			 *
			 *  computeMove(): this method takes the room (adjacent to the current room) from above,
			 *  and returns the key command the AI will issue in order to turn towards that room
			 *  (if not already facing that direction) or move forward into the room. */
			return this.computeMove(this.getNearestAdjRoom(leastRiskyRoomsAL, boardPerceivedByAI));
		}

		/* BASE CASE where AI player is standing right next to a room that possibly contains a wastes.
		 * Aim and shoot the disinfectant if chance of wastes is 50% or more AND if AI player has at least one disinfectant left. */
		if(this.hasDisinfectant()) {
//			System.out.println("Now checking to see if adjacent rooms might have wastess in them (Remember AI has disinfectant(s))...");
			char move = ' ';	//initialize a possible move to be returned
			CellAsPerceivedByAI neighborWithBestChanceOfWastes = null;	//initialize the room that has the greatest chance of wastes
			int bestChanceOfWastes = -1;	//initialize the probability that a room has wastes in it
			int mostEffectiveCost = Integer.MAX_VALUE;	//initialize the number of turns it will take to change direction to face the wastes

			/* Go thru every room adjacent to the one the AI is in. */
			for(CellAsPerceivedByAI neighbor : this.currentRoomAI.getNeighbors()) {

				/* If one or more valid neighboring room(s) have 50% or more chance of a wastes,
				 * then find out which room has the best chance of having a wastes.
				 * We obviously want to shoot the disinfectant toward the room that has the greatest chance
				 * (preferably 100%) of having a wastes inside it. */
				if(neighbor != null && neighbor.getprobabilityOfWastes() >= 50) {
					if(neighbor.getprobabilityOfWastes() > bestChanceOfWastes) {
						bestChanceOfWastes = neighbor.getprobabilityOfWastes();
						neighborWithBestChanceOfWastes = neighbor;
						ArrayList<CellAsPerceivedByAI> tempAL = new ArrayList<>();
						tempAL.add(this.currentRoomAI); tempAL.add(neighbor);
						mostEffectiveCost = this.getTotalCost(tempAL);
					}
					/* If two or more rooms have an equal chance of having a Wastes,
					 * then we want to shoot the disinfectant towards the most conveniently-placed Wastes,
					 * preferably without having to change player's directions at all (changing directions
					 * takes up valuable turns). So we compute which room requires the fewest turns
					 * for the player to position his direction correctly to aim his disinfectant.
					 *
					 * For example, if the player is in room (0,0) while facing Board.EAST, and
					 * the neighboring rooms (1,0) and (0,1) each have 50% chance of having a Wastes,
					 * then the player should choose the room to the east because he wouldn't have to
					 * change directions at all in order to shoot the disinfectant. */
					else if(neighbor.getprobabilityOfWastes() == bestChanceOfWastes) {
						//compute total cost of moving to this neighbor...efficiency matters.
						ArrayList<CellAsPerceivedByAI> tempAL = new ArrayList<>();	//create new arraylist for this purpose
						tempAL.add(this.currentRoomAI); tempAL.add(neighbor);	//add the current room and the neighboring room
						/* Use custom method getTotalCost() to get the total no. of turns needed to
						 * change player's direction toward the neighboring room's direction */
						int tempTotalCost = this.getTotalCost(tempAL);
						if(tempTotalCost < mostEffectiveCost) {
							mostEffectiveCost = tempTotalCost;
							neighborWithBestChanceOfWastes = neighbor;
						}
					}
					//end if(neighbor.getprobabilityOfWastes() > bestChanceOfWastes) / else if...
				}
				//end if(neighbor != null && neighbor.getprobabilityOfWastes() >= 50)
			}
			//end for(RoomAsPerceivedByAI neighbor : this.currentRoomAI.getNeighbors())

			if(neighborWithBestChanceOfWastes != null) {
//				System.out.printf("%s has %s%% chance of wastes! Aiming disinfectant...",
//						neighborWithBestChanceOfWastes.getXY(), neighborWithBestChanceOfWastes.getprobabilityOfWastes());
				/* Before we try to shoot the Wastes, we want to make sure we're facing the right direction.
				 * Use custom method to return the key command the AI would use if it were to move into this room.
				 * If this method returns 'F' (forward), then we know the AI is facing the direction of the possible Wastes.
				 * So we'll shoot by returning 'S'.
				 * Otherwise, if the method returns 'L' or 'R' (left or right), then we'll return the same. */
				char tempMove = computeMove(neighborWithBestChanceOfWastes);
				if(tempMove == 'F') {
					/* Since we'll shoot in this direction, we can now safely say that there is no Wastes
					 * in this neighboring room. Either the Wastes is in the room and will be shot / killed, or
					 * the Wastes was never in the room to begin with. Either way, the AI now knows for certain
					 * that the probability of a Wastes in this room is zero. */
					neighborWithBestChanceOfWastes.setprobabilityOfWastes(0);
//					System.out.println("The AI SHOOTS the disinfectant!");
					return 'S';
				}
				else move = tempMove;
			}

			if(move != ' ') return move;	//If we found a valid move above, then return it.
			else {
//				System.out.printf("None of the adjacent rooms have 50%% or more chance of containing a Wastes. %s will save disinfectants for now.\n",
//					this.name);
			}
		}
		//end if

		/* If none of the base cases above applies, then we'll just move to the least risky unexplored room.
		 * Note that the variable leastRiskyRoomsAL was previously computed above. If we got this far in this method,
		 * it means that NONE of the remaining unexplored rooms are 100% safe, and leastRiskyRoomsAL merely holds
		 * a list of Rooms that have all been computed to pose the LEAST, equal, nonzero risk.
		 *
		 * So now we will identify, out of these elements in leastRiskyRoomsAL, the room that will take the AI
		 * the fewest number of turns to get to. Since it should be one of AI's goals to explore as many unexplored rooms
		 * in as few turns as possible, we want to save time spent exploring.
		 *
		 * Remember that time spent changing player's directions plays a huge factor in computing the overall cost of exploration,
		 * and the below custom method, getNearestAdjRoom(), takes account of this fact.
		 * Be sure to read the javadoc for the method getNearestAdjRoom() for more details. */
		CellAsPerceivedByAI tempDestination = this.getNearestAdjRoom(leastRiskyRoomsAL, boardPerceivedByAI);
		char tempMove = computeMove(tempDestination);	//custom method to get the key command the AI will use to get to the next room

		/* This is a final check to try to minimize risk of AI's death. If AI would have moved forward
		 * but there is a nonzero chance of a Wastes in the adjacent room, then try to shoot an disinfectant if possible.
		 * Granted, this condition will rarely be triggered given all of the base cases above, but this
		 * situation does come up sometimes, and having this condition in here just in case can mean the
		 * difference between life and death. */
		if(tempMove == 'F' && tempDestination.getprobabilityOfWastes() > 0 && this.hasDisinfectant()) {
//			System.out.printf("\nBefore moving forward, %s senses a possible Wastes...and decides to SHOOT the disinfectant!\n", this.name);
			tempDestination.setprobabilityOfWastes(0);
			return 'S';
		}
		return tempMove;	//Now we're really out of base cases and conditions. Time to return the move.
	}

	/**
	 * Method: getNearestAdjRoom
	 * Takes the given leastRiskyRoomsAL, which contains RoomAsPerceivedByAI elements that have been determined
	 * by the AI to be the "least risky unexplored rooms" (see method getLeastRiskyRooms() for more details),
	 * and computes the solution path for each of these rooms.
	 *
	 * Afterwards, this method computes the total cost (including the number of direction changes the AI will
	 * have to make) of making it to each of these rooms.
	 *
	 * Afterwards, this method identifies the sole "least risky unexplored room" that provides the most
	 * cost-effective way of getting there.
	 *
	 * Finally, this method looks at the solution path for getting to this room, and returns the 1st-index element
	 * (aka second element) in the solution path, effectively returning the room adjacent to the current room which
	 * will form part of the solution path.
	 *
	 * @param leastRiskyRoomsAL the ArrayList of RoomAsPerceivedByAI objects.
	 * @param boardPerceivedByAI the Board.java object containing all RoomAsPerceivedByAI objects (every room in the board,
	 * as perceived by the AI)
	 *
	 * @return the adjacent room that is part of the solution path to allow the AI to get to the least risky room
	 * in the shortest number of turns.
	 */
	public CellAsPerceivedByAI getNearestAdjRoom(ArrayList<CellAsPerceivedByAI> leastRiskyRoomsAL, Board boardPerceivedByAI) {
		int leastCost = Integer.MAX_VALUE;	//initialize the number of turns required for the AI to get to the least risky rooms
		CellAsPerceivedByAI nextPath = null;	//initialize the value to be returned
//		System.out.println("\nNow going through all the least risky unexplored room(s) and trying to find most efficient path...");
		/* Go thru each element in the ArrayList */
		for(CellAsPerceivedByAI r : leastRiskyRoomsAL){
			/* Use Pathfind.java's custom method to get an arraylist containing the cost-optimal solution path from the
			 * current room to each of the elements in the leastRiskyRoomsAL ArrayList. */
			ArrayList<CellAsPerceivedByAI> tempSolutionPath = pf.ucs(currentRoomAI, r, boardPerceivedByAI, this.dir);
			if(tempSolutionPath != null) {	//If a solution path exists...

				/* Use custom method to get the total cost of moving there. UPDATE: no longer necessary. See below. */
//				int tempCost = getTotalCost(tempSolutionPath);

				/* Thanks to the UCS algorithm in the Pathfind class, the total cost of moving to room r
				 * is saved in that room's distanceSoFar attribute. So just use the getter method. */
				int tempCost = r.getDistanceSoFar();

//				System.out.printf("Total cost to move from %s to %s : %s\n", currentRoomAI.getXY(), r.getXY(), tempCost);	//testing
				if(tempCost < leastCost) {
//					System.out.printf("Woot! The above total cost is less than the previously saved leastCost! "
//							+ "the destination is now set to %s.\n", r.getXY());
					leastCost = tempCost;	//update least cost variable
					nextPath = tempSolutionPath.get(1);	//get the 1st-index element of the solution path
//					System.out.printf("The adjacent room %s will move to is now set to %s\n", this.name, nextPath.getXY());
				}
			}
			//end if(tempSolutionPath != null)
		}
		//end for
		return nextPath;
	}

	/**
	 * Method: computeMove
	 * Given a RoomAsPerceivedByAI that is adjacent to the AI's current room, computes the key command necessary
	 * to turn towards to that room, or to move forward if AI is already facing towards the direction of that room.
	 * @param nextDestination the given RoomAsPerceivedByAI object that is adjacent to the AI's current room
	 * @return 'R', 'L', or 'F' (right, left, or forward)
	 */
	public char computeMove(CellAsPerceivedByAI nextDestination) {
		/* Oops. the .x and .y values have been reversed from how we generally understand them. In this game,
		 * the .x refers to the ROW index of the room and .y refers to the COLUMN index. */
		if(currentRoom.y == nextDestination.y) {	//if the current room and neighboring room are in the same COLUMN...
			if(currentRoom.x < nextDestination.x) {	//...and if the neighboring room is located to the south of current room...
				if(this.dir == Board.SOUTH) return 'F';	//move forward if player is already facing south.
				if(this.dir == Board.WEST) return 'L';	//turn left if player is facing west.
				else return 'R';						//turn right otherwise. The rest of this method works via same logic.
			}
			else {	//else, if the neighboring room is located to the north of current room...etc. you know the rest
				if(this.dir == Board.NORTH) return 'F';
				if(this.dir == Board.EAST) return 'L';
				else return 'R';
			}
		}
		else {
			if(currentRoom.y < nextDestination.y){
				if(this.dir == Board.EAST) return 'F';
				if(this.dir == Board.SOUTH) return 'L';
				else return 'R';
			}
			else {
				if(this.dir == Board.WEST) return 'F';
				if(this.dir == Board.NORTH) return 'L';
				else return 'R';
			}
		}
		//end outer if
	}


	/**
	 * Method: getLeastRiskyRooms
	 * Given the Board object (consisting of Room.java objects) as well as another Board object (consisting of
	 * RoomAsPerceivedByAI.java objects), computes and returns an ArrayList of unexplored rooms that are LEAST risky
	 * to the AI.
	 *
	 * @param board given Board. Consists of Room objects. All information is open to players, including information
	 * on unexplored rooms. The AI does not use this information (otherwise that'd be cheating)
	 *
	 * @param boardPerceivedByAI given Board. Consists of RoomAsPerceivedByAI objects.
	 * Not all information is open to players -- namely information on unexplored rooms are hidden.
	 * The AI relies on the incomplete information provided by this board to make logical deductions
	 * about which rooms are the least risky.
	 *
	 * @return ArrayList of RoomAsPerceivedByAI objects determined by the AI to be least risky.
	 */
	public ArrayList<CellAsPerceivedByAI> getLeastRiskyRooms(Board board, Board boardPerceivedByAI) {
		/* Initialize an arraylist to hold unexplored rooms. This will be eventually returned by this method. */
		ArrayList<CellAsPerceivedByAI> unexploredRoomsAL = new ArrayList<>();
		/* Initialize the risk levels for Wastes and pit. Notice that there exists
		 * a WastesGreatestRisk variable here, but not a corresponding pitGreatestRisk variable.
		 * This will be explained later in this method... */
		int wastesLeastRisk = 101, wastesGreatestRisk = -1, pitLeastRisk = 101;

		/* Also initialize a var to keep track of least COMBINED risk (used in the event that a room
		 * might have BOTH a wastes AND a pit). For instance, if a room has a 50% chance of wastes
		 * and 50% chance of pit, the total COMBINED risk is 75%, aka 0.75. */
		double leastCombinedRisk = 1.0;	//aka 100%

		/* Go thru every RoomAsPerceivedByAI in the board. */
		for(int i = 0; i < Board.ROWS; i++) {
			for(int j = 0; j < Board.COLS; j++) {
				CellAsPerceivedByAI tempR = boardPerceivedByAI.getRoomAI(i, j);	//temporarily save this room at location i, j

				/* If this room has not been explored, AND if this room has one or more safe neighbors
				 * (aka has one or more adjacent rooms guaranteed to be safe), then
				 * we have identified an unexplored room that is possible for the AI to travel to safely.
				 * (NOTE: This says nothing, however, about whether the unexplored room ITSELF is safe.)
				 *
				 * We do not consider other unexplored rooms that may or may not be safe to travel to.
				 *
				 * For example, if the player has explored rooms at (0,0) and (0,1), then the only
				 * unexplored rooms that will be identified as per above are: (1,0), (1,1), and (0,2).
				 *  */
				if(!tempR.isExplored() && tempR.hasSafeNeighbors()) {
//					System.out.printf("\nFound unexplored room at %s:\n%s\nIt's possible for %s to navigate to this room safely. "
//							, tempR.getXY(), tempR, this.name);

					/* Get the probability that this room has a wastes, and/or that this room has a pit.
					 * Remember that tempR is a RoomAsPerceivedByAI object, which does NOT contain complete information
					 * about the room (unless it's been explored by a player). So we have to work with probabilities.
					 * To this end, also remember that the RoomAsPerceivedByAI class has a custom method called
					 * assignProbabilityToNeighbors(), in which the AI uses logic to deduce
					 * the risk in each unexplored room that is adjacent to an explored room. */
					int wastesRisk = tempR.getprobabilityOfWastes();
					int pitRisk = tempR.getProbabilityOfPit();

					/* The combined risk calculation formula is as follows:
					 *
					 * (P * M) + (!P * M) + (P * !M)
					 *
					 * where P = probability that the room has a pit, expressed in decimal form (between 0 and 1.0)
					 * where M = probability that the room has a wastes, expressed in decimal form (between 0 and 1.0)
					 * and !P and !M respectively refer to the probability that the room does NOT have a pit or a wastes. */
					double combinedRisk = (pitRisk / 100.0 * wastesRisk / 100.0)
							+ ((100 - pitRisk) / 100.0 * wastesRisk / 100.0)
							+ (pitRisk / 100.0 * (100 - wastesRisk) / 100.0);
//					System.out.printf("Combined risk is %.2f%%.\n", combinedRisk * 100);

					if(combinedRisk == 0) {
//						System.out.println("This room is guaranteed 100% SAFE!");
					}

					/* DESIGN DECISION: After much consideration, I decided to break up the definition of "least risky" into
					 * two categories.
					 * ===================================================================================================
					 * ==FIRST CATEGORY== The AI player has no disinfectants.
					 * In this case, both pits and wastess pose an equal danger to the AI. So the AI must try to avoid
					 * both at all costs. So the AI will just consider the combined risk of wastes + pit.
					 * For example, if a room has a 50% chance of wastes and a 50% chance of pit, then
					 * the combined risk is 75%. We want to minimize this combined risk.
					 *
					 * ===================================================================================================
					 * ==SECOND CATEGORY== The AI player has one or more disinfectants.
					 * In this case, the risk of wastess does not pose as great a threat as the risk of pits, because
					 * the AI can shoot disinfectants at wastess.
					 *
					 * Therefore, the AI will avoid pits first and foremost and, as a secondary priority,
					 * the AI will either actively seek out rooms with possible wastess in them (aka go "wastes hunting"),
					 * or will try to avoid those rooms -- depending on whether AI's aggression mode is
					 * set to true or false (attribute aggressiveModeOn).
					 * Do you remember the variable wastesGreatestRisk, and how I said the reason for this variable
					 * will be "explained later"? Well here it is: when the aggression mode is on, the AI will
					 * actually PRIORITIZE seeking out wastess over seeking out 100% safe unexplored rooms!
					 *
					 * So, to sum up:
					 * If AI's aggressiveModeOn is set to false, the AI will try to find 100% safe unexplored rooms first,
					 * and if there exist no 100% safe unexplored rooms, only then will the AI seek out possible wastes rooms
					 * while always trying to avoid pits. While this mode conserves disinfectants and is sometimes safer than the
					 * aggressive mode, the downside is that on average it takes longer for the AI to find the victim because
					 * the AI is reluctant to "shoot through" possible wastes rooms unless it ABSOLUTELY has to, thereby
					 * resulting in more backtracking and moving "around" wastess.
					 *
					 * On the other hand, if AI's aggressiveModeOn is set to true, the AI will actively seek out
					 * possible wastes rooms (while always avoiding pits), EVEN IF there exist other 100% safe
					 * unexplored rooms. Killing wastess is the AI's priority in this case. Trust me, this aggression mode
					 * might sound very foolhardy, but in practice it works out very well, and is often more efficient
					 * than the non-aggressive mode. The only real downside is that sometimes the AI in this mode
					 * runs out of disinfectants quite early in the game, thus SLIGHTLY increasing its risk of death overall.
					 * ===================================================================================================
					 * */
					if(!this.hasDisinfectant()) {	//AI has no disinfectants
//						System.out.printf("%s doesn't have disinfectants left. Trying to find least overall combined risk (wastes + pit)...\n",
//								this.name);
						if(combinedRisk < leastCombinedRisk) {
							leastCombinedRisk = combinedRisk;
							unexploredRoomsAL.clear();	//clear the ArrayList of any previous rooms, because we just found a better (less risky) room!
							unexploredRoomsAL.add(tempR);	//and add the room
						}
						else if(combinedRisk == leastCombinedRisk) {
							unexploredRoomsAL.add(tempR);
						}
					}
					/* ==SECOND CATEGORY== */
					else {	//AI has disinfectant(s).
//						System.out.println(this.name + " has disinfectant(s) left. " + (this.aggressiveModeOn == true ? "Time to go wastes hunting!" :
//								"Will try to conserve disinfectants and use them only if absolutely necessary."));
						/* Even though AI has disinfectants, it still must avoid pits at all costs. */
						if(pitRisk < pitLeastRisk) {
							pitLeastRisk = pitRisk;
							wastesLeastRisk = wastesRisk;
							wastesGreatestRisk = wastesRisk;
							unexploredRoomsAL.clear();
							unexploredRoomsAL.add(tempR);
						}
						/* If a room has an equally low chance of having a pit... then AI's action depends on whether
						 * its aggressiveModeOn attribute is set to true or false. */
						else if(pitRisk == pitLeastRisk) {
							if(aggressiveModeOn) {	//Aggression mode on! AI will go wastes hunting.
								if(wastesRisk > wastesGreatestRisk) {	//seek out rooms that most likely have wastess!
									wastesGreatestRisk = wastesRisk;
									unexploredRoomsAL.clear();
									unexploredRoomsAL.add(tempR);
								}
								else if(wastesRisk == wastesGreatestRisk) {
									unexploredRoomsAL.add(tempR);
								}
							}
							else {	//Aggression mode off. AI will conserve disinfectants if possible.
								if(wastesRisk < wastesLeastRisk) {	//seek out rooms that are least likely to have wastess.
									wastesLeastRisk = wastesRisk;
									unexploredRoomsAL.clear();
									unexploredRoomsAL.add(tempR);
								}
								else if(wastesRisk == wastesLeastRisk) {
									unexploredRoomsAL.add(tempR);
								}
							}
							//end if(aggressiveModeOn)/else
						}
						//end if(pitRisk < pitLeastRisk) / else if(pitRisk == pitLeastRisk)
					}
					//end if / else
				}
				//end if(!tempR.isExplored() && tempR.getSafeNeighbors() != null)
			}
			//end for j
		}
		//end for i

//		System.out.printf("\n**Least risky unexplored room(s) as determined by %s**\n", this.name);	//print to console for testing
//		for(CellAsPerceivedByAI r : unexploredRoomsAL) {
//			System.out.println(r);
//		}
//		System.out.println();	//add an extra line for formatting
		return unexploredRoomsAL;	//return the ArrayList of "least risky unexplored rooms"
	}

	/**
	 * Method: getTotalCost
	 * Given a "solution path" ArrayList consisting of RoomAsPerceivedByAI objects, sorted in the order from
	 * the current room to the destination room, this method computes the total turns needed for the AI to get
	 * to the destination.
	 *
	 * For example, if this AI player is currently facing EAST and the solution path consists of {(0,0), (1,0), (1,1), (1,2)}
	 * (where (0,0) is the current room, (1,2) is the destination room), and the other rooms form part of the solution path)
	 * then the total turns needed to get to the destination is 5. (turn south, forward, turn east, forward, forward)
	 *
	 * @param solutionPath the given ArrayList of solution path from current room to destination
	 * @return the number of turns needed for the AI to get to the destination.
	 */
	public int getTotalCost(ArrayList<CellAsPerceivedByAI> solutionPath) {
		int ret = 0;
		int direction = this.dir;	//the current direction this AI is facing.

		/* Iterate through the solutionPath, starting with index 1. */
		for(int i = 1; i < solutionPath.size(); i++) {
			CellAsPerceivedByAI curr = solutionPath.get(i - 1);	//get the room in the previous index and assign to current
			CellAsPerceivedByAI nextDestination = solutionPath.get(i);	//get the room in this index and assign to nextDestination

			/* As mentioned before in the comments associated with computeMove() method,
			 * the .x and .y values have been reversed from how we generally understand them. In this game,
			 * the .x refers to the ROW index of the room and .y refers to the COLUMN index. */
			if(curr.y == nextDestination.y) {	//if current and next rooms are in the same column...
				if(curr.x < nextDestination.x) {	//..and if the next room is located to the south of the current room..
					if(direction == Board.SOUTH) ret++;	//..and if player is currently facing south, it just takes 1 turn to move to the next room
					else if(direction == Board.WEST) {	//...if player is facing west or east, it takes 2 turns...
						ret += 2;
					}
					else if(direction == Board.EAST) {
						ret += 2;
					}
					else {		//if player is facing north, it takes 3 turns.
						ret += 3;
					}
					direction = Board.SOUTH;	//update the direction (notice this does not change this class's global attribute (this.dir))
				}
				else {	//else if the next room is located to the north of the current room...you know the rest.
					if(direction == Board.NORTH) ret++;
					else if(direction == Board.EAST) {
						ret += 2;
					}
					else if(direction == Board.WEST) {
						ret += 2;
					}
					else {
						ret += 3;
					}
					direction = Board.NORTH;
				}
			}
			else { //else, if current and next rooms are in the same row...you know the rest.
				if(curr.y < nextDestination.y){
					if(direction == Board.EAST) ret++;
					else if(direction == Board.SOUTH) ret += 2;
					else if(direction == Board.NORTH) ret += 2;
					else ret += 3;
					direction = Board.EAST;
				}
				else {
					if(direction == Board.WEST) ret++;
					else if(direction == Board.NORTH) ret += 2;
					else if(direction == Board.SOUTH)ret += 2;
					else ret += 3;
					direction = Board.WEST;
				}
			}
			//end outer if
		}
		return ret;	//return the total number of turns needed to get to the destination.
	}
}


