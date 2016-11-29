
/** Class: Cell.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *
 *  This class - the Cell class.
 *  Purpose - Contains all information about the Room object. This class will NOT be accessed by the AI
 *  during the game. See CellAsPerceivedByAI.java class for more info.
 */
public class Cell
{
	public static final int ROWS = 6;
	protected int x;
	protected int y;
	protected Cell[] neighbors;	//array of rooms adjacent to this room.
	protected boolean pit;	//whether this room contains a pit
	protected boolean wastes;	//whether this room contains a wastes
	protected boolean victim;	//whether this room has the victim
	protected boolean hintShown;	//whether certain graphics regarding a room will be shown on the GUI.
	protected String s;	//perceptions

	/**
	 * 2-arg constructor.
	 * * NOTE: the .x and .y values have been reversed from how we generally understand them. In this game,
	 * the .x refers to the ROW index of the room and .y refers to the COLUMN index.
	 *
	 * @param x the x-position of the room (as displayed on the board -- see Board.java for more details)
	 * @param y the y-position of the room
	 */
	public Cell (int x, int y)
	{
		this.x = x;
		this.y = y;
		this.pit = false;
		this.wastes = false;
		this.victim = false;
		this.neighbors = new Cell[4];
		this.hintShown = false;
		this.s = "";
	}

	public void makePit(){
		this.pit = true;
	}

	public boolean isPit(){
		return pit;
	}

	public void spawnWastes(){
		this.wastes = true;
	}

	public void killWastes(){
		this.wastes = false;
	}

	public boolean hasWastes(){
		return this.wastes;
	}

	public void placeVictim(){
		this.victim = true;
	}

	public boolean giveAid(){
		if(this.victim == false) return false;
		this.victim = false;
		return true;
	}

	public boolean hasVictim(){
		return this.victim;
	}

	public void initNeighbors(Cell[][] rooms){
		int len = rooms.length;//assume board is square
		neighbors[Board.NORTH] = (x==0 ? null : rooms[x-1][y]);		//Board.NORTH = 0
		neighbors[Board.EAST] =  (y==len-1 ? null:rooms[x][y+1]);	//Board.EAST = 1
		neighbors[Board.SOUTH] = (x==len-1 ? null:rooms[x+1][y]);	//Board.SOUTH = 2
		neighbors[Board.WEST] =  (y==0 ? null:rooms[x][y-1]);		//Board.WEST = 3
	}

	public Cell[] getNeighbors(){
		return neighbors;
	}

	/**
	 * Method: perceptions
	 * @return a String containing all the perceptions in this room. For instance, if a room has a breeze and a stench,
	 * returns "B S ".
	 */
	public String perceptions(){
		s = "";
		boolean pitAir = false;
		boolean stenchAir = false;
		/* See if any neighboring rooms have a wastes or a pit. */
		for (int i = 0; i < neighbors.length; i++)
		{
			if(neighbors[i]!=null){
				if(neighbors[i].hasWastes())
					stenchAir = true;
				if(neighbors[i].isPit()){
					pitAir = true;
				}
			}
		}
		/* Concatenate characters as necessary to form the String to be returned.*/
		if (pitAir)
			s += "B ";
		if (stenchAir)
			s += "S ";
		if (hasWastes())
			s += "W ";
		if (isPit())
			s += "P ";
		if (hasVictim())
			s += "G ";
		return s;
	}

	/**
	 * Method: setHints
	 * Setter. Information on this room will now be displayed on the Board GUI.
	 */
	public void setHints(){
		hintShown = true;
	}

	/**
	 * Method: isShown
	 * Getter.
	 * @return whether information on this room is displayed on the Board GUI.
	 */
	public boolean isShown(){
		return hintShown;
	}

	public int getLocation(){
		return (ROWS * x) + y;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Method: toString
	 */
	@Override
	public String toString(){
		return String.format("Room at (%s, %s). Pit: %s; Monster: %s; Victim: %s; Perceptions: %s",
				this.x, this.y, this.pit, this.wastes, this.victim, this.perceptions());
	}
}

