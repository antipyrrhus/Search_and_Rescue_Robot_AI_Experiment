
import java.util.ArrayList;
//import java.util.LinkedList;
import java.util.PriorityQueue;
//import java.util.Queue;
import java.util.Stack;

/** Class: Pathfind.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *
 *  This class - The Pathfind class.
 *  Purpose - Used by the SAR class to perform basic breadth-first pathfinding for the AI player.
 *  Outside of this class, cost analysis is performed (when player changes directions by turning right or left,
 *  it takes up a whole turn) to find the best solution path. This isn't perfectly optimal, but mostly good enough.
 *
 *  UPDATE: Now uses Uniform-Cost Search instead of BFS. UCS takes into account the cost of the Player's
 *  changing directions (whereas BFS did not take said cost into account, and thus another method outside
 *  of this class had to calculate the cost of changing directions AFTER the fact -- thereby sometimes resulting
 *  in non-optimal solution path).
 *
 *  So now, the solution path calculated by the UCS method in this class is ALWAYS guaranteed to be cost-optimal.
 */
public class Pathfind {
	/* Declare an ArrayList of RoomAsPerceivedByAI objects that have been visited by the AI. */
	ArrayList<CellAsPerceivedByAI> visitedArr;

	/* Declare an ArrayList of RoomAsPerceivedByAI objects that form the solution path. */
	ArrayList<CellAsPerceivedByAI> solutionAL;

	/**
	 * Method: ucs
	 * Performs a Uniform-Cost Search from the given room to the destination room, given a board consisting of rooms and
	 * given the Player object's direction he is currently facing.
	 *
	 * @param rootRoomAI the given starting room.
	 * @param destinationRoomAI the given destination room.
	 * @param boardAI the given board
	 * @param playerDirection the direction that the Player object is currently facing.
	 * @return an ArrayList consisting of the cost-optimal solution path from the current room to the destination.
	 */
	public ArrayList<CellAsPerceivedByAI> ucs(CellAsPerceivedByAI rootRoomAI, CellAsPerceivedByAI destinationRoomAI, Board boardAI, int playerDirection) {

		/* Prior to starting this method, reset the distances assigned to each RoomAsPerceivedByAI object to zero.
		 * This reset is necessary because, depending on where the Player is and what direction he is facing, the
		 * distanceSoFar attribute of the RoomAsPerceivedByAI class will be different.
		 * So it's always safest to reset the attribute to zero for every room before we start this ucs algorithm. */
		for(int i = 0; i < Board.ROWS; i++) {
			for(int j = 0; j < Board.COLS; j++) {
				boardAI.getRoomAI(i, j).setDistanceSoFar(0);
			}
		}

		/* Reset the ArrayLists keeping track of the visited rooms as well as the solution path. */
		visitedArr = new ArrayList<>();
		solutionAL = new ArrayList<>();

		/* UCS uses PriorityQueue data structure.
		 * (First In First Out, auto-sorted from least to greatest cost. See the compareTo() method
		 * in RoomAsPerceivedByAI class for more details.
		 */
		PriorityQueue<CellAsPerceivedByAI> q = new PriorityQueue<>();

		/* Set the hypotheticalPlayerDir attribute for this room. This is important when
		 * calculating the cost of changing player's directions as part of the overall optimal cost calculation. */
		rootRoomAI.setHypotheticalPlayerDir(playerDirection);

		/* Begin by adding the starting room to the queue and setting that room to visited.
		 * Print the root room and destination room to the console. */
		q.add(rootRoomAI);
		visitedArr.add(rootRoomAI);
//		System.out.printf("********************************\nStarting node: %s, Goal node: %s\nStarting Search...\n\n",
//				rootRoomAI.getXY(), destinationRoomAI.getXY());

		/* Declare a variable to keep track of the current room in the priority queue (FIFO, auto-sorted).
		 * This variable will be continuously updated in the while loop below. */
		CellAsPerceivedByAI r;

		//Declare a child and begin by setting it to the starting room
		CellAsPerceivedByAI child = rootRoomAI;

		/* Continue the while loop as long as queue has stuff in it, AND as long as the goal room has not been reached. */
		while(!child.getXY().equals(destinationRoomAI.getXY()) && !q.isEmpty())
		{
			/* The very first time this loop runs, r will equal the starting root room
			 * since the root room is the only one in the priority queue at the start. In other iterations, r will equal
			 * whatever room is at the very front of the queue */
			r = q.peek();
//			System.out.println("r = " + r.getXY() + ", and child = " + child.getXY());

			/* use method call to get the LEAST-COST child neighboring the current room.
			 * If there is no such room, null will be returned.
			 * NOTE: In the event that a child that would otherwise be returned happens to be a duplicate (previously been visited),
			 * then this method will check to see whether this child is "better" (as in, has lesser cost than the one previously visited).
			 * */
			child = this.getChildNodeUCS(r, destinationRoomAI);

			/* If the returned child value is null, just remove the current node from the queue. */
			if ( child == null ) {
				q.remove();
//				System.out.printf("Node %s removed from priority queue...", r.getXY());		//for user-friendliness
			}

			/* Otherwise, add the child to the visited array. Then add the child to the priority queue, replacing any duplicate.
			 * Finally, set r as the parent of the returned child. This is useful later when we backtrack from
			 * the goal room to the root room, in order to print a complete solution key. */
			else {
				this.visitedArr.add(child);
//				System.out.printf("Checking path from %s to %s...", r.getXY(), child.getXY());	//for user-friendliness
				if(q.contains(child)) q.remove(child);	//Remove any old duplicate before adding child. This is necessary to trigger Priority Queue's auto-sorting.
				q.add(child);
				child.setParentRoom(r);
			}
			//End if/else

			/* Use custom method to print the current queue (optional). Then re-assign child node to whatever is next in the queue.
			 * Then get ready for the next iteration. */
//			printQ(q);	//optional, just for testing
			child = q.peek();
		}
		//End while

		/* Let end-user know if an optimal cost path was found from root to goal room. Display the total cost required.
		 * Then, print and return the solution key using custom method call. */
		if(child.getXY().equals(destinationRoomAI.getXY())) {
//			System.out.printf("\nOptimal-cost path to goal node found!\n"
//					+ "Cost from Room %s to Room %s is: %s.\n", rootRoomAI.getXY(), destinationRoomAI.getXY(), destinationRoomAI.getDistanceSoFar());
			return printSolution(rootRoomAI, destinationRoomAI);
		}
		else {
//			System.out.println("Goal node not reached.");
			return null;
		}
	}

	/**
	 * Method: getChildNodeUCS
	 *
	 * Gets the LEAST-COST child that is adjacent to the current room. If there is no such child, null will be returned.
	 *
	 * NOTE: In the event that a child that would otherwise be returned happens to be a duplicate (previously been visited),
	 * then this method will check to see whether this child is "better" (as in, has lesser cost than the one previously visited).
	 * This method will only return this child if it is:
	 * 1) the least-cost child, AND 2) either not a duplicate or a "better" duplicate.
	 *
	 * @param r the given room.
	 * @return the least-cost neighboring child subject to the conditions above.
	 */
	public CellAsPerceivedByAI getChildNodeUCS(CellAsPerceivedByAI r, CellAsPerceivedByAI destinationRoomAI) {

		CellAsPerceivedByAI ret = null;		//initialize the node to return.

		/* Initialize to dummy value. if we do end up returning a room, then we'll need to set / adjust
		 * that room's distanceSoFar data field beforehand. */
		int distanceToSet = -1;

		/* Now we'll go thru each of the given room's neighbors. There are 4 neighbors, and some of them may be null.
		 * (For instance, a room located at (0,0) has only two neighbors -- (1,0) and (0,1).
		 * So we'll set index = 0 and get the index of the first neighbor that is NOT null. */
		int index = 0;
		for(int i = index; i < r.getNeighbors().length; i++) {
			if(r.getNeighbors()[i] != null) {
				index = i;
				break;
			}
		}

		/* Now that we have found the first NON-NULL neighbor, let's make some temporary variables to save important info...
		 * Begin by getting 1) this RoomAsPerceivedByAI neighbor object, 2) distance from r, and
		 * 3) combined distance from starting room up to and including this neighbor. */
		CellAsPerceivedByAI prevNeighbor = r.getNeighbors()[index];
		int[] distAndNeighborsDirection = computeCost(r, prevNeighbor);	//computeCost() is a custom method in this class.
		int prevDist = distAndNeighborsDirection[0];
		int prevDistSoFar = prevDist + r.getDistanceSoFar();

		/* We will also need to keep track of the Player's direction depending on which room he is in.
		 * So we'll set this direction for this neighboring room. */
		prevNeighbor.setHypotheticalPlayerDir(distAndNeighborsDirection[1]);

		/* This above neighbor potentially qualifies as a candidate to be returned if it meets the following conditions:
		 * 1) the room has not been previously visited, or the room has been previously visited (as in, a duplicate)
		 *    but is "better", i.e. less overall distance cost;
		 * AND
		 * 2) The room is either the destination room, OR the room is guaranteed to be safe. */
		if(
				( !this.visitedArr.contains(prevNeighbor) || prevDistSoFar < prevNeighbor.getDistanceSoFar() ) &&
				( prevNeighbor.getXY().equals(destinationRoomAI.getXY()) || prevNeighbor.isGuaranteedSafe() )
				) {
			ret = prevNeighbor;
			distanceToSet = prevDistSoFar;
		}

		/* Now go thru the rest of the neighbors, starting from index + 1, and compare it to the previous neighbor,
		 * and pick out the best one to return. */
		for (int i = index + 1; i < r.getNeighbors().length; i++) {
			/* Begin by getting 1) the current neighbor room, 2) its distance from r, and
			 * 3) combined distance from starting room up to and including this neighbor. */
			CellAsPerceivedByAI currNeighbor = r.getNeighbors()[i];
			if(currNeighbor == null) continue;	//if this neighbor is null, then just go on to the next iteration
			distAndNeighborsDirection = computeCost(r, currNeighbor); 	//custom method call
			int currDist = distAndNeighborsDirection[0];
			int currDistSoFar = currDist + r.getDistanceSoFar();

			/* This current neighbor potentially qualifies as a candidate to be returned if it meets the following conditions:
			 * 1) Either there is no current candidate to be returned or the current combined distance cost is lower than
			 *    the previously selected return candidate,
			 * AND
			 * 2) the current neighbor is not a duplicate or is a "better" duplicate,
			 * AND
			 * 3) the current neighbor is either the destination room or is guaranteed safe. */
			if(     (ret == null || currDistSoFar < distanceToSet) &&
					(!this.visitedArr.contains(currNeighbor) || currDistSoFar < currNeighbor.getDistanceSoFar()) &&
					( currNeighbor.getXY().equals(destinationRoomAI.getXY()) || currNeighbor.isGuaranteedSafe() )
					) {
				ret = currNeighbor;
				distanceToSet = currDistSoFar;

				/* We will also need to keep track of the Player's direction depending on which room he is in.
				 * So we'll set this direction for this neighboring room. */
				currNeighbor.setHypotheticalPlayerDir(distAndNeighborsDirection[1]);
			}
			//end if
		}
		//End for i

		/* Once the above loop is over, in the event the selected return candidate is a duplicate, we will
		 * remove the previous duplicate from the visited array. The return candidate will then later be re-added
		 * to the visited array outside of this method. */
		if(visitedArr.contains(ret)) visitedArr.remove(ret);

		/* If we do have a return candidate, now is the time to set its distance traveled so far. This information is crucial
		 * for the UCS algorithm to work properly in computing costs along the way. */
		if(ret != null) ret.setDistanceSoFar(distanceToSet);

		return(ret);	//return the room
	}
	//End public RoomAsPerceivedByAI getChildNodeUCS

	/**
	 * Method: computeCost. Given a current room and the next (adjacent) room, computes the cost of the Player to move there,
	 * and also returns the direction the Player will be facing once he reaches the next room.
	 *
	 * @param curr current room
	 * @param nextDestination the adjacent room to move to
	 *
	 * @return an int[] array consisting of two integers:
	 * 1) cost of moving to the next room, and
	 * 2) the direction the Player will be facing once he gets to the next room.
	 */
	public int[] computeCost(CellAsPerceivedByAI curr, CellAsPerceivedByAI nextDestination) {
		int ret = 0;	//Initialize the cost to return to zero
		int direction = curr.getHypotheticalPlayerDir();	//get the direction the Player in the current room is facing.
		int neighborDirection = -1;	//initialize the direction the Player will be facing in the next room to a dummy value.

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
				neighborDirection = Board.SOUTH;	//update the direction (notice this does not change the Player class's global attribute (this.dir))
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
				neighborDirection = Board.NORTH;
			}
		}
		else { //else, if current and next rooms are in the same row...you know the rest.
			if(curr.y < nextDestination.y){
				if(direction == Board.EAST) ret++;
				else if(direction == Board.SOUTH) ret += 2;
				else if(direction == Board.NORTH) ret += 2;
				else ret += 3;
				neighborDirection = Board.EAST;
			}
			else {
				if(direction == Board.WEST) ret++;
				else if(direction == Board.NORTH) ret += 2;
				else if(direction == Board.SOUTH)ret += 2;
				else ret += 3;
				neighborDirection = Board.WEST;
			}
		}
		return new int[]{ret, neighborDirection};	//return {cost, neighbor's direction}
	}

	/**
	 * Method: printQ
	 * Prints the queue to the console, to help the end-user keep track.
	 * @param q the queue to print
	 */
	public void printQ(PriorityQueue<CellAsPerceivedByAI> q) {

		/* Create a stack to temporarily save the contents of the queue as it is being emptied out */
		Stack<CellAsPerceivedByAI> s = new Stack<>();

//		System.out.print("Current Queue is: [ ");

		/* Empty out the queue into the Stack, and include the following info:
		 * 1) the room's location, AND
		 * 2) the distance traveled thus far (for UCS) */
		while(!q.isEmpty()) {
//			CellAsPerceivedByAI tmp = q.peek();
//			System.out.print(tmp.getXY() + "(" + tmp.getDistanceSoFar() + ") ");
			s.push(q.remove());
		}
//		System.out.println("]");

		/* We need to restore the queue. Add the nodes back in from the Stack. */
		while(!s.isEmpty()) {
			q.add(s.pop());
		}
	}
	//End public void printQ

	/**
	 * Method: bfs
	 * UPDATE: NO LONGER USED. ucs() is used instead.
	 *
	 * Performs a breadth-first search and finds a solution path from the starting room to the destination room.
	 *
	 * @param rootRoomAI The starting room. This is a RoomAsPerceivedByAI object.
	 * @param destinationRoomAI The destination room. This is a RoomAsPerceivedByAI object.
	 * @param boardAI The game board consisting of all RoomAsPerceivedByAI objects.
	 * @return an ArrayList of RoomAsPerceivedByAI objects that forms the solution path.
	 */
//	public ArrayList<RoomAsPerceivedByAI> bfs(RoomAsPerceivedByAI rootRoomAI, RoomAsPerceivedByAI destinationRoomAI, Board boardAI) {
//		/* Initialize ArrayLists. */
//		visitedArr = new ArrayList<>();
//		solutionAL = new ArrayList<>();
//
//		/* Initialize a queue to keep track of rooms */
//		Queue<RoomAsPerceivedByAI> q = new LinkedList<>();
//
//		q.add(rootRoomAI);	//add the starting room to the queue
//		visitedArr.add(rootRoomAI);	//add the starting room to the ArrayList that keeps track of which rooms have been visited
//
//		//Declare n to keep track of the current room in the queue (FIFO)
//		RoomAsPerceivedByAI n;
//
//		//Declare a child node and begin by setting it to the starting room
//		RoomAsPerceivedByAI child = rootRoomAI;
//
//		/* Continue the while loop as long as queue has stuff in it, AND as long as the goal node has not been reached. */
//		while(!q.isEmpty() && !(child.x == destinationRoomAI.x && child.y == destinationRoomAI.y)) {
//			n = q.peek();
//			child = this.getSafeAndUnvisitedChildNode(n, destinationRoomAI);	//custom method
//			if(child == null) q.remove();
//			else {
//				visitedArr.add(child);
//				//				System.out.printf("Now checking from Room (%s, %s) to room (%s, %s)...\n", n.x, n.y, child.x, child.y);
//				q.add(child);
//				child.setParentRoom(n);	//custom method to set the parent of this room. Good for later printing the solution path.
//				//				System.out.println(q);
//			}
//			child = q.peek();	//set the child to the next node in the queue
//		}
//		//end while
//
//		/* If we found the solution path, then return the ArrayList containing the path.
//		 * Note that printSolution() is a custom method that prints out the solution path
//		 * to the console AND returns an ArrayList containing same. */
//		if(child != null && child.getXY().equals(destinationRoomAI.getXY())) {
//			return printSolution(rootRoomAI, destinationRoomAI);
//		}
//		else {	//else, if we didn't find a solution path, return null.
//			return null;
//		}
//	}

	/**
	 * Method: getSafeAndUnvisitedChildNode
	 * UPDATE: NO LONGER USED since bfs() is no longer used in this class. See ucs() instead.
	 *
	 * @param n the current room that is being considered.
	 * @param destinationRoomAI the room that is the ultimate destination.
	 * @return a neighboring room that is 100% safe and that isn't contained in the visited ArrayList.
	 * EXCEPTION: if the neighboring room happens to BE the ultimate destination room, THEN
	 * such neighboring room does NOT have to be 100% safe.
	 */
//	public RoomAsPerceivedByAI getSafeAndUnvisitedChildNode(RoomAsPerceivedByAI n, RoomAsPerceivedByAI destinationRoomAI) {
//		/* Go thru every neighboring room. Note that n.getNeighbors() is a custom method in the
//		 * RoomAsPerceivedByAI class.
//		 * For each neighboring room, if the following is true:
//		 * 1) The room is not null, AND
//		 * 2) the neighboring room is either the destination room or is 100% safe, AND
//		 * 3) the neighboring room is not contained in the visited ArrayList,
//		 * THEN
//		 * this method returns that neighboring room.
//		 *
//		 * If there is no neighboring room that meets the above criteria, returns null. */
//		for(RoomAsPerceivedByAI neighbor : n.getNeighbors()) {
//			if(neighbor != null) {
//				if( (neighbor.getXY().equals(destinationRoomAI.getXY()) || neighbor.isGuaranteedSafe())
//						&& !visitedArr.contains(neighbor))
//					return neighbor;
//			}
//		}
//		return null;
//	}

	/**
	 * Method: printSolution
	 * Prints the solution path and returns an ArrayList containing same.
	 * @param rootRoomAI the starting room.
	 * @param destinationRoomAI the destination room.
	 * @return ArrayList containing the solution path.
	 */
	public ArrayList<CellAsPerceivedByAI> printSolution(CellAsPerceivedByAI rootRoomAI, CellAsPerceivedByAI destinationRoomAI) {

		CellAsPerceivedByAI currRoom = destinationRoomAI;		//We'll backtrack from the destination room.
		Stack<CellAsPerceivedByAI> solution = new Stack<>();	//create a stack. We'll add the backtracked solution into this stack, then pop them back out in reverse.
		solution.push(currRoom);					//push the destination room into the stack

		/* Continue this loop until we have backtracked all the way to the starting room */
		while(currRoom != rootRoomAI) {
			currRoom = currRoom.getParentRoom();	//Each room can only have one parent. So it makes backtracking easy.
			solution.push(currRoom);
		}

		/* We're ready to print the solution. */
//		System.out.print("SOLUTION: ");
		currRoom = solution.pop();
//		System.out.printf("%s", currRoom.getXY());	//Remember a stack is LIFO (last in first out). So we begin by printing the starting room.
		solutionAL.add(currRoom);	//add this room to the ArrayList that will be returned.

		/* Continue this loop until the stack is all emptied out */
		while(!solution.isEmpty()) {
			currRoom = solution.pop();
			solutionAL.add(currRoom);	//add this room to the ArrayList that will be returned.
//			System.out.printf(" -> %s", currRoom.getXY());	//Keep popping out the rooms
		}
//		System.out.println();	//Add a new line for good measure
		return solutionAL;
	}
}
