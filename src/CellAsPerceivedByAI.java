
/** Class: CellAsPerceivedByAI.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *
 *  This class - inherits from Cell.java class.
 *
 *  Purpose - This class is used exclusively by the AI player.
 *  This class contains information that the AI should NOT have free access to.
 *  For example, if the AI (and human) players have only explored rooms (0,0) and (0,1), then
 *  the the players should not know whether, say, room (5,5) contains wastes or not.
 *  So this subclass mirrors the Cell class except that this subclass contains
 *  other attributes, such as probabilityOfPit and probabilityOfWastes, which the AI accesses
 *  to make logical deductions about the risk inherent in each room.
 *
 *  Once a CellAsPerceivedByAI object is explored by the AI (or human) player, then the AI
 *  will have full knowledge of the room. Until then, the AI can only rely on logical deduction
 *  to draw conclusions (if any) about a CellAsPerceivedByAI object.
 */
public class CellAsPerceivedByAI extends Cell implements Comparable<CellAsPerceivedByAI> {

	private boolean explored;	//whether this room has been explored by a player (either human or AI) or not.
	private int probabilityOfPit, probabilityOfWastes;	//probability that this room contains a pit and/or wastes.
	private CellAsPerceivedByAI[] neighbors;	//neighboring objects (adjacent to this room).
	private final int DUMMY_PROBABILITY = 101;	//a dummy value assigned upon this room's initialization where the actual probability of pit / wastes is unknown.
	private CellAsPerceivedByAI parentRoom;		//this attribute works in conjunction with Pathfind.java class for printing solution path.
	private int distanceSoFar;		//the distance it took so far to travel from some starting room to this room. Default value is zero upon construction. Used for pathfinding.
	private int hypotheticalPlayerDir;	//Attribute primarily accessed by the Pathfind.java class for calculating cost-optimal solution path.

	/**
	 * 2-arg constructor. Invoked by Board.java class.
	 *
	 * NOTE: the .x and .y values have been reversed from how we generally understand them. In this game,
	 * the .x refers to the ROW index of the room and .y refers to the COLUMN index.
	 *
	 * @param x the x-position of the room (as displayed on the board -- see Board.java for more details)
	 * @param y the y-position of the room
	 */
	public CellAsPerceivedByAI(int x, int y) {
		super(x, y);	//invoke superclass's constructor. See Room.java for more details.
		this.neighbors = new CellAsPerceivedByAI[4];	//This will save adjacent CellAsPerceivedByAI objects (up to 4)
		this.explored = false;						//this cell has not been explored by AI (or human) player upon initialization.
		this.probabilityOfPit = DUMMY_PROBABILITY;	//initialize the chance of a pit in this room to dummy value
		this.probabilityOfWastes = DUMMY_PROBABILITY; //initialize the chance of a wastes in this room to dummy value
		this.distanceSoFar = 0;
	}

	/**
	 * Method: initNeighbors. Invoked by Board.java class.
	 * @param rooms the 2-dimensional array containing RoomAsPerceivedByAI objects. This is essentially the game board.
	 */
	public void initNeighbors(CellAsPerceivedByAI[][] rooms){
		int len = rooms.length;//assume board is square

		/* Not all rooms will have 4 neighbors. For instance, a room located at (0,0) will only have 2 neighbors:
		 * (0,1) and (1,0). So some of these neighbors may be null objects. */
		/* Again, the .x and .y values have been reversed from how we generally understand them. In this game,
		 * the .x refers to the ROW index of the room and .y refers to the COLUMN index. */
		neighbors[Board.NORTH] = x == 0 ? null: rooms[x-1][y];	//Board.NORTH = 0. See Board.java class.
		neighbors[Board.EAST] = y == len - 1 ? null: rooms[x][y+1];	//Board.EAST = 1
		neighbors[Board.SOUTH] = x == len - 1 ? null: rooms[x+1][y];	//Board.SOUTH = 2
		neighbors[Board.WEST] = y == 0 ? null: rooms[x][y-1];	//Board.WEST = 3
	}

	/**
	 * Method: getHypotheticalPlayerDir
	 * @return the hypotheticalPlayerDir
	 */
	public int getHypotheticalPlayerDir() {
		return hypotheticalPlayerDir;
	}

	/**
	 * Method: setHypotheticalPlayerDir
	 * @param hypotheticalPlayerDir the hypotheticalPlayerDir to set
	 */
	public void setHypotheticalPlayerDir(int hypotheticalPlayerDir) {
		this.hypotheticalPlayerDir = hypotheticalPlayerDir;
	}

	/**
	 * Method: getParentRoom()
	 * @return the parentRoom
	 */
	public CellAsPerceivedByAI getParentRoom() {
		return parentRoom;
	}

	/**
	 * Method: setParentRoom
	 * @param parentRoom the parentRoom to set
	 */
	public void setParentRoom(CellAsPerceivedByAI parentRoom) {
		this.parentRoom = parentRoom;
	}

	/**
	 * Method: getNeighbors
	 * @return the Array containing this room's neighbors. Remember some of these neighbors may be null objects
	 * because not all rooms have 4 neighbors. For instance, a room located at (0,0) will only have 2 neighbors.
	 */
	public CellAsPerceivedByAI[] getNeighbors(){
		return neighbors;
	}

	/**
	 * @return whether this room is explored by AI (or human) player.
	 */
	public boolean isExplored() {
		return explored;
	}

	/**
	 * Method: setExplored. Invoked by Player.java and WumpusWorld.java class.
	 * Sets this room to explored, and AI now perceives everything in this room with 100% certainty.
	 * @param explored boolean variable. true or false
	 * @param r the Room object (see Room.java class) with all data fields already known.
	 */
	public void setExplored(boolean explored, Cell r) {
		this.explored = explored;
		if(this.explored == true) {
			this.wastes = r.wastes;
			this.victim = r.victim;
			this.hintShown = r.hintShown;
			//			this.neighbors = r.neighbors;	//commented out. First, it isn't possible to assign r.neighbors, a Room object,
			//to this.neighbors, a RoomAsPerceivedByAI object. Second, the AI shouldn't have
			//full information on all the neighbors anyway as some of them may not have been explored yet.
			this.pit = r.pit;
			this.s = r.perceptions();		//AI has fully correct perceptions (such as a Temperature, smoke) now. See custom method in Room.java class.
			this.probabilityOfPit = (this.isPit() ? 100 : 0);	//now we update probability to either 100% or 0% because AI knows this for certain.
			this.probabilityOfWastes = (this.hasWastes() ? 100: 0);	//same as above
		}
	}

	/**
	 * Method: assignProbabilityToNeighbors. Invoked by SAR.java class.
	 * Checks out all neighboring rooms to this room (RoomAsPerceivedByAI object) and
	 * assigns the probability that each neighboring room has wastes and/or a pit,
	 * based on whether this room has a "S" (smoke) and/or "B" (temperature reading), and based on
	 * whether any of the neighboring rooms are guaranteed 100% safe.
	 *
	 * See comments below for some examples of how this works.
	 *
	 * @param board the Board object, containing Room objects.
	 * @param boardAI the Board object, containing RoomAsPerceivedByAI objects.
	 */
	public void assignProbabilityToNeighbors(Board board, Board boardAI) {
		/* Look at each neighboring room (adjacent to this room) */
		for(CellAsPerceivedByAI rAI : this.neighbors) {
			/* If the neighbor doesn't exist, or if the neighbor has already been explored
			 * (and thus the neighbor's probability of risk (pit and/or wastes) has been 100% confirmed
			 * via the setExplored() method above, then there is no need to adjust the probability again.
			 * So we'll continue on to the next loop iteration. */
			if(rAI == null || rAI.isExplored()) {
				continue;
			}

			/* If this room has a smoke ("S"), AND if this neighboring room's probability of wastes has not
			 * previously been confirmed to be 0%, then.... */
			if(this.s.contains("S") && rAI.getprobabilityOfWastes() != 0) {
				//				System.out.printf("The explored room at (%s, %s) contains 'S'.\n", this.x, this.y);
				/* UPDATE: commented this out. Just because a room is surrounded by "S", doesn't necessarily mean
				 * that its chances of wastes are 100%.
				 */
				//				if(rAI.isSurrounded("S")) {
				//					System.out.printf("unexplored room at %s, %s is surrounded by S.\n", rAI.x, rAI.y);
				//					rAI.setprobabilityOfWastes(100);
				//				}

				/* ...use custom method to get the number of rooms that are:
				 * 1) adjacent to this room, and
				 * 2) NOT 100% confirmed to be safe from wastes.
				 * Note that this custom method will NEVER return zero. Why? Because of the condition above
				 * that this room has a smoke. so at least ONE of the neighbors to this room MUST be unsafe.
				 * */
				int unsafeNeighbors = this.countNotGuaranteedSafeNeighbors("S");

				/* We will update the probability of wastes in this neighbor room under two conditions:
				 * 1) If the wastes probability has not yet been set for this neighbor, OR
				 * 2) If the previously assigned probability of wastes is LESS than 100% divided by the number of potentially
				 *    unsafe neighbors to this room.
				 *
				 * For example, if the AI is in room (0,0) that has a smoke, and no other rooms have been explored yet,
				 * then the AI will look at its neighboring rooms, (0,1) and (1,0), and will assign 50% probability of
				 * a wastes to each neighboring room.
				 *
				 * Then, on the next turn, let's say the AI moves on to (1,0) and finds out there is no wastes there.
				 * Then the probability of wastes for (1,0) will be set to 0 via the setExplored() method in this class.
				 * Then, once THIS method is invoked by WumpusWorld.java on room (0,0) (where the AI used to be),
				 * the probability of a wastes in room (0,1) will now be adjusted up to 100%.
				 *
				 *  Why this works:
				 *  We assume that once a probability is set to some positive (nonzero) percentage, then it can never be
				 *  adjusted down to another nonzero percentage.
				 *
				 *  For example, a room previously assigned a 50% chance of wastes
				 *  COULD be re-assigned a risk of 0% or a risk of 100% once the AI learns some more information,
				 *  but can NEVER be re-assigned a risk of, say, 33%.
				 *  As another example, a room previously assigned 33% chance of wastes could be re-assigned a risk of 0%, 50%,
				 *  or 100% depending on what additional information the AI learns later, but it can NEVER be re-assigned
				 *  a risk of, say, 25%.
				 *
				 *  The reason for this is pretty intuitive. As the AI learns more about all the rooms, AI will
				 *  be able to use deduction to make some definite conclusions about the risks inherent in those rooms.
				 *  For instance, if an AI explores a previously unexplored room with a 50% risk of wastes and finds out
				 *  it is safe, then that room will be immediately assigned 0% probability of wastes. This process
				 *  ensures that now the AI knows there exists another room previously assigned 50% wastes chance that will
				 *  now be updated to 100% wastes chance. So the more AI explores and the more it learns and deduces,
				 *  the more certain these probabilities become : either an increasing chance of wastes (up to 100%), or
				 *  a definite 0% chance of wastes. */
				if(rAI.getprobabilityOfWastes() == this.DUMMY_PROBABILITY || rAI.getprobabilityOfWastes() < 100 / unsafeNeighbors) {
					rAI.setprobabilityOfWastes(100 / unsafeNeighbors);
				}

				/* There is another twist when it comes to wastess only. It is stipulated that there are
				 * only 2 wastess in the entire dungeon. (no such stipulation exists for the number of pits
				 * other than that each room has a 10% chance of having a pit.)
				 * So, once the locations of those two wastess are 100% confirmed (either by AI's deduction,
				 * or by shooting disinfectants and connecting, or by the human player running into a room with a wastes and dying),
				 * then both wastess have been "accounted for," and the rest of the rooms can be assigned a 0% wastes risk,
				 * notwithstanding any other deductions the AI might have drawn.
				 *
				 * So, the custom method allwastessAccountedFor() determines precisely that: whether all wastess
				 * in the game have been located for certain and/or killed.
				 *
				 * Note: this method is robust enough so that it works regardless of whether the stipulated number
				 * of wastess is 2 or 3 or anything else. So, if we want to change the wastes stipulation at some
				 * future time, no worries... */
				if(allWastesAccountedFor(board, boardAI)) {
					/* Conditional statement ensures that if the probability of waste in a given room
					 * has been previously been confirmed to be 100%, it CANNOT be changed to 0%.
					 * (unless that waste has been killed by an disinfectant, which is handled by a different method
					 * than this one.) */
					rAI.setprobabilityOfWastes(rAI.getprobabilityOfWastes() == 100 ? 100 : 0);
					if(rAI.getprobabilityOfWastes() == 0) {
						System.out.printf("Thanks to the allWastesAccountedFor() method, AI now understands "
								+ "that Room at %s has 0%% chance of having a waste!\n", rAI.getXY());
					}
				}
				//end if(allWastesAccountedFor(board, boardAI))
			}
			else {	//if this room does not have a smoke("S"), then we can obviously set the neighboring room's probability of waste to zero.
				rAI.setprobabilityOfWastes(0);
			}
			//end if(this.s.contains("S") && rAI.getprobabilityOfWastes() != 0) / else

			/* If this room has a Temperature ("B"), AND if this neighboring room's probability of pit has not
			 * previously been confirmed to be 0%, then....
			 * This code below follows the same logic as the waste logic above (see above comments), except that pits have
			 * no "stipulation" the way wastes do. */
			if(this.s.contains("B") && rAI.getProbabilityOfPit() != 0) {
				int unsafeNeighbors = this.countNotGuaranteedSafeNeighbors("B");
				if(rAI.getProbabilityOfPit() == this.DUMMY_PROBABILITY || rAI.getProbabilityOfPit() < 100 / unsafeNeighbors) {
					rAI.setProbabilityOfPit(100 / unsafeNeighbors);
				}
			}
			else {
				rAI.setProbabilityOfPit(0);
			}
			//end if(this.s.contains("B") && rAI.getProbabilityOfPit() != 0) / else
		}
		//end for
	}
	//end public void assignProbabilityToNeighbors

	/**
	 * Method: modifyProbabilityIfSurroundedByDanger. Invoked by SAR.java.
	 * Checks the surrounding neighbors of this room and sees how many of those neighbors
	 * have either smoke or Temperature in them. The more neighbors have these signs,
	 * the more likely this room is to be risky, unless this room has already been deduced
	 * to be perfectly safe (0% risk).
	 *
	 * For example, if this room is surrounded on all sides by "S", then chances are very high
	 * (though not necessarily 100%) that this room contains a waste.
	 *
	 * This method assumes that this room has NOT yet been explored by a player. Why the assumption?
	 * There is no point modifying a probability of risk for a room that has been explored,
	 * because an explored room has either a 100% or 0% probability of any given danger, which
	 * cannot ever be modified (unless, of course, a room has a waste and the waste is killed)
	 *
	 * @param board the Board object containing Room objects.
	 * @param boardAI the Board object containing RoomAsPerceivedByAI objects.
	 */
	public void modifyProbabilityIfSurroundedByDanger(Board board, Board boardAI) {
		/* Use custom method to get the number of neighboring rooms that have smoke in them. */
		int surroundingWastesRisk = this.getSurroundingDanger("S");

		//	    if(surroundingWastesRisk > 0)	//testing
		//		System.out.printf("surroundingWastesRisk for unexplored room %s: %s\n", this.getXY(), surroundingWastesRisk);

		/* Use custom method to get the number of neighboring rooms that have Temperature in them. */
		int surroundingPitRisk = this.getSurroundingDanger("B");

		//	    if(surroundingPitRisk > 0)	//testing
		//		System.out.printf("surroundingPitRisk for unexplored room %s: %s\n", this.getXY(), surroundingPitRisk);

		/* Save this room's current probability of waste and pit. */
		int currentprobabilityOfWastes = this.getprobabilityOfWastes();
		int currentProbabilityOfPit = this.getProbabilityOfPit();

		/* Do NOT modify the probability of waste for this room if it's currently set to zero, as
		 * this means that the AI has previously deduced this room to be 100% safe from wastes.
		 * Otherwise, go ahead... */
		if(currentprobabilityOfWastes != 0) {
			if(surroundingWastesRisk == 2) {	//if this room is surrounded by 2 neighbors with smoke in them...
				/* Set risk to 60% if the risk for this room has not yet been deduced by AI.
				 * Frankly, this condition will probably never be triggered I don't think, but just in case... */
				if(currentprobabilityOfWastes == this.DUMMY_PROBABILITY) {
					this.setprobabilityOfWastes(60);
				}
				/* Else, if this room's risk has been previously deduced by AI, then we can modify
				 * this risk only upwards, NEVER downwards.
				 * Read the comment in this class beginning with:
				 *
				 * "once a probability is set to some positive (nonzero) percentage..."
				 *
				 * for an explanation as to why.
				 *
				 * By the way, why modify the percentage to 60% exactly? Well...honestly
				 * this is a rather crude guesswork on my part, because finding the TRUE
				 * probability of risk in a room surrounded partly or wholly by a smoke and/or pit
				 * seems a rather complicated task dependent on many variables, and probably
				 * not worth the trouble for this game. In practice, and after quite a bit of testing,
				 * I've found that modifying the percentage in the manner below resulted in the AI
				 * successfully minimizing risk at all times. */
				else if(currentprobabilityOfWastes < 60) this.setprobabilityOfWastes(60);
			}
			else if(surroundingWastesRisk == 3) {	//the rest of these conditions follow the same logical framework as above...
				if(currentprobabilityOfWastes == this.DUMMY_PROBABILITY) {
					this.setprobabilityOfWastes(75);	//arbitrary (but effective) risk set at 75%.
				}
				else if(currentprobabilityOfWastes < 75) this.setprobabilityOfWastes(75);
			}
			else if(surroundingWastesRisk == 4) {
				if(currentprobabilityOfWastes == this.DUMMY_PROBABILITY) {
					this.setprobabilityOfWastes(80);	//arbitrary (but effective) risk set at 80%.
				}
				else if(currentprobabilityOfWastes < 80) this.setprobabilityOfWastes(80);
			}
		}
		//end if

		/* Repeat the above process, this time for the probability of pit. */
		if(currentProbabilityOfPit != 0) {
			if(surroundingPitRisk == 2) {
				if(currentProbabilityOfPit == this.DUMMY_PROBABILITY) {
					this.setProbabilityOfPit(60);
				}
				else if(currentProbabilityOfPit < 60) this.setProbabilityOfPit(60);
			}
			else if(surroundingPitRisk == 3) {
				if(currentProbabilityOfPit == this.DUMMY_PROBABILITY) {
					this.setProbabilityOfPit(75);
				}
				else if(currentProbabilityOfPit < 75) this.setProbabilityOfPit(75);
			}
			else if(surroundingPitRisk == 4) {
				if(currentProbabilityOfPit == this.DUMMY_PROBABILITY) {
					this.setProbabilityOfPit(80);
				}
				else if(currentProbabilityOfPit < 80) this.setProbabilityOfPit(80);
			}
		}
		//end if
	}

	/**
	 * Method: getSurroundingDanger
	 * Returns the number of neighboring rooms that contain either smoke or Temperature (depending on the given parameter)
	 * @param smokeOrTemperature the parameter is assumed to be a String consisting of a single character:
	 * either "S" (for smoke) or "B" (for Temperature).
	 *
	 * @return The number of neighboring rooms that contain either smoke or Temperature (depending on the given parameter)
	 */
	public int getSurroundingDanger(String smokeOrTemperature) {
		int surroundedCount = 0;
		for(CellAsPerceivedByAI r: this.getNeighbors()) {
			if(r != null && r.s.contains(smokeOrTemperature)) surroundedCount++;
		}
		return surroundedCount;
	}

	/**
	 * Method: allWastesAccountedFor. Invoked by the assignProbabilityToNeighbors() method in this class.
	 * See the comments starting with "There is another twist when it comes to wastes only" in this class
	 * for more details on why we need this method.
	 *
	 * @param board the Board object, consisting of Room objects.
	 * @param boardAI the Board object, consisting of RoomAsPerceivedByAI objects.
	 *
	 * @return true if all wastes have been "accounted for", false otherwise.
	 */
	public boolean allWastesAccountedFor(Board board, Board boardAI) {
		int wastesRemaining = board.getwastesRemaining();	//get the total number of wastes that remain on the Board.
		/* If all wastes are dead, return true. */
		if(wastesRemaining == 0) {
			System.out.println("allWastesAccountedFor(): No wastes remain!");
			return true;
		}

		/* Initialize a count of wastes whose locations have been 100% verified. */
		int count = 0;

		/* Go thru the entire board and check whether each RoomAsPerceivedByAI object in the board
		 * has a confirmed 100% probability of waste. */
		for(int i = 0; i < Board.ROWS; i++) {
			for(int j = 0; j < Board.COLS; j++) {
				if(boardAI.getRoomAI(i, j).getprobabilityOfWastes() == 100) {
					count++;
					/* If the number of confirmed 100% locations of wastes matches the wastes that remain in the game,
					 * then all wastes have indeed been "accounted for." Return true. */
					if(count == wastesRemaining) {
						//						System.out.printf("allWastesAccountedFor(): all wastes have been accounted for! "
						//								+ "Count of 100% wastes: %s\n", count);
						return true;
					}
				}
			}
			//end for j
		}
		//end for i
		return false;
	}

	/**
	 * Method: countNotGuaranteedSafeNeighbors. Invoked by assignProbabilityToNeighbors() method in this class.
	 *
	 * @param smokeOrTemperature assumed to be either "S" (smoke) or "B" (Temperature).
	 * @return the number of RoomAsPerceivedByAI objects adjacent to this room that have
	 * NOT been determined to be 100% safe from a waste or a pit, as determined by the given parameter.
	 */
	public int countNotGuaranteedSafeNeighbors(String smokeOrTemperature) {
		int ret = 0;
		for(CellAsPerceivedByAI r : this.neighbors) {
			if(smokeOrTemperature.equals("S")) {
				if(r != null && r.getprobabilityOfWastes() != 0) ret++;
			}
			else if(smokeOrTemperature.equals("B")) {
				if(r != null && r.getProbabilityOfPit() != 0) ret++;
			}
		}
		return ret;
	}

	/**
	 * Method: isGuaranteedSafe
	 * @return true if this room has a confirmed 0% chance of waste and a 0% chance of pit, false otherwise.
	 */
	public boolean isGuaranteedSafe() {
		return this.probabilityOfWastes == 0 && this.probabilityOfPit == 0;
	}

	/**
	 * Method: hasSafeNeighbors. Invoked by the Player class.
	 * @return true if this room has one or more neighbors that have been explored AND confirmed to be 100% safe. Return false otherwise.
	 */
	public boolean hasSafeNeighbors() {
		for(int i = 0; i < this.neighbors.length; i++) {
			CellAsPerceivedByAI tempR = this.neighbors[i];
			if(tempR == null) continue;
			if(tempR.isExplored() && tempR.isGuaranteedSafe()) return true;
		}
		return false;
	}

	/**
	 * Method: getProbabilityOfPit
	 * @return the probabilityOfPit
	 */
	public int getProbabilityOfPit() {
		return probabilityOfPit;
	}

	/**
	 * Method: setProbabilityOfPit
	 * @param probabilityOfPit the probabilityOfPit to set
	 */
	public void setProbabilityOfPit(int probabilityOfPit) {
		this.probabilityOfPit = probabilityOfPit;
	}

	/**
	 * Method: getprobabilityOfWastes
	 * @return the probabilityOfWastes
	 */
	public int getprobabilityOfWastes() {
		return probabilityOfWastes;
	}

	/**
	 * Method: setprobabilityOfWastes
	 * @param probabilityOfWastes the probabilityOfWastes to set
	 */
	public void setprobabilityOfWastes(int probabilityOfWastes) {
		this.probabilityOfWastes = probabilityOfWastes;
	}



	/**
	 * Method: getSafeNeighbors. Invoked by the Player class.
	 * @return
	 * UPDATE: no longer used.
	 */
	//	public RoomAsPerceivedByAI[] getSafeNeighbors() {
	//		RoomAsPerceivedByAI[] ret = new RoomAsPerceivedByAI[4];
	//		boolean exploredAndSafeNeighborsExist = false;
	//		for(int i = 0; i < this.neighbors.length; i++) {
	//			RoomAsPerceivedByAI tempR = this.neighbors[i];
	//			if(tempR == null) ret[i] = null;
	//			else if(tempR.isExplored() && tempR.isGuaranteedSafe()) {
	//				exploredAndSafeNeighborsExist = true;
	//				ret[i] = tempR;
	//			}
	//			else ret[i] = null;
	//		}
	//		if(exploredAndSafeNeighborsExist) return ret;
	//		else return null;
	//	}

	/**
	 * Method: getDistanceSoFar
	 * @return the distanceSoFar
	 */
	public int getDistanceSoFar() {
		return distanceSoFar;
	}

	/**
	 * Method: setDistanceSoFar
	 * @param distanceSoFar the distanceSoFar to set
	 */
	public void setDistanceSoFar(int distanceSoFar) {
		this.distanceSoFar = distanceSoFar;
	}

	/**
	 * Method: toString
	 * @return a String containing detailed info about this room.
	 */
	@Override
	public String toString(){
		String ret = String.format("Room(as perceived by AI) at (%s, %s). Explored: %s. ", this.x, this.y, this.explored);
		if(this.explored){
			ret += String.format("Pit: %s; waste: %s; victim: %s; Perceptions: %s",
					this.pit, this.wastes, this.victim, this.s);
		}
		else {
			ret += String.format("Probability that this is a pit: %s; Probability that there's a waste here: %s",
					this.getProbabilityOfPit(), this.getprobabilityOfWastes());
		}
		return ret;
	}

	public String getXY() {
		return String.format("(%s, %s)", this.x, this.y);
	}

	@Override
	public int compareTo(CellAsPerceivedByAI otherRoom) {
		if(this.getDistanceSoFar() > otherRoom.getDistanceSoFar()) return 1;
		else if(this.getDistanceSoFar() < otherRoom.getDistanceSoFar()) return -1;
		else return 0;
	}

	//No longer used.
	//@Override
	//public int compareTo(RoomAsPerceivedByAI otherRoom) {
	//	int probability1 = this.probabilityOfWastes + this.probabilityOfPit;
	//	int probability2 = otherRoom.getprobabilityOfWastes() + otherRoom.getProbabilityOfPit();
	//	if(probability1 < probability2) return -1;
	//	else if(probability1 > probability2) return 1;
	//	else return 0;
	//}
}
