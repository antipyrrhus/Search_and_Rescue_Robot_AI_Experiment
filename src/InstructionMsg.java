/** Class: InstructionMsg.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *  Written / Updated: Nov 22, 2016
 *
 *  This Class - A repository for various instruction messages used in the game.
 */
public class InstructionMsg {

	public static String DEFAULT =
			"Robot moves:\n" +
			"- LEFT and RIGHT arrow keys to rotate left and right.\n" +
			"- 'S' to shoot decontaminants towards an adjacent grid which the robot is facing.\n" +
			"  (Note: the robot only has two rounds of shots, so use them judiciously)" +
			"- 'G' to give aid to the victim once located (successfully ends the mission).\n" +
			"- 'A' to attempt another mission (only available once current mission is terminated in success or failure).\n" +
			"- 'Q' to quit / abort the current mission\n" +
			"At any time, press SPACEBAR to enable AI mode for a single turn.\n\n" +
			"Pressing O brings up the Options Menu.\n\n" +
			"Robot Sensors can detect the following environmental cues: \n"+
			"1. Heightened temperature reading (indicated by a thermometer icon) in grids\n" +
			"   adjacent to that containing a fire pit. Fire pits must be avoided.\n"+
			"2. Smoke reading in grids adjacent to that containing radioactive wastes.\n" +
			"   Wastes must be either avoided or cleaned via shooting decontaminants.\n" +
			"3. Once the robot locates the victim, the victim's icon is displayed. Pressing G will end the mission.\n\n" +
			"The environment contains:\n"+
			"1. One dislocated victim,\n"+
			"2. Two grids that contain radioactive wastes,\n" +
			"3. Estimated 10% of the grids may contain fire pits.\n\n"+
			"Equipment: The robot is equipped with two shots of anti-radioactive decontaminants.\n\n" +
			"Other Notes:\n"+
			"1. The robot is destroyed if it is in the square with a fire pit.\n"+
			"2. The robot is destroyed if it is in the square with active radioactive wastes.\n"+
			"3. Shooting decontaminants toward a direction the robot is facing\n"+
			"   will disable radioactive wastes (if any) in the adjacent grid, and\n"+
			"   an icon (signifying disinfection) will be displayed.";

	public static String[] TUTORIAL = {
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

					    "You are on your own for the remainder of this practice mission. Remember:\n"
					  + "- SHIFT to move the robot toward the direction it is currently facing,\n"
					  + "- LEFT and RIGHT arrow keys to rotate the robot in-place,\n"
					  + "- S to shoot decontaminant spray towards an adjacent grid in the direction\n"
					  + "  the robot is currently facing (provided the robot has shots left),\n"
					  + "- G to give aid to the victim once found.\n\n"
					  + "- Avoid grids containing fire pits.\n"
					  + "- Either avoid or decontaminate grids containing radioactive wastes.\n"
					  + "  (since the robot is out of decontaminants now, it must avoid these grids.)\n\n"
					  + "Continue exploring the area on your own, using your best judgment.\n"
					  + "The person in need of rescue is somewhere in this area.\n"
					  + "Once the person is discovered, the Next button will be enabled.",

					    "You have located the victim! Well done.\n"
					  + "Press G to give emergency aid and finish the mission successfully.",

					  	"The robot has been destroyed, and the mission is a failure.\n"
					  + "Fortunately, this was a practice mission. Press A to start the tutorial again."
	};


	public static String[] DRILL_HUMAN = {
				"Now that you've successfully completed the tutorial,\n"
			  + "you will get one more practice drill on a different environment.\n"
			  + "The location of the victim, as well as that of fire pits\n"
			  + "and radioactive wastes, has been changed for this drill.\n\n"
			  + "You will be completely on your own this time, though\n"
			  + "the next page will provide helpful reminders on keyboard commands.\n\n"
			  + "You will get only one chance to complete the mission,\n"
			  + "which will end either once you locate the victim and press G to give aid,\n"
			  + "or once the robot is destroyed and the mission fails.\n\n"
			  + "Press Next when you are ready to begin.\n",

			    "Robot moves:\n" +
				"- LEFT and RIGHT arrow keys to rotate left and right.\n" +
				"- 'S' to shoot decontaminants towards an adjacent grid which the robot is facing.\n" +
				"  (Note: the robot only has two rounds of shots, so use them judiciously)" +
				"- 'G' to give aid to the victim once located (successfully ends the mission).\n\n" +
				"Robot Sensors can detect the following environmental cues: \n"+
				"1. Heightened temperature reading (indicated by a thermometer icon) in grids\n" +
				"   adjacent to that containing a fire pit. Fire pits must be avoided.\n"+
				"2. Smoke reading in grids adjacent to that containing radioactive wastes.\n" +
				"   Wastes must be either avoided or cleaned via shooting decontaminants.\n" +
				"3. Once the robot locates the victim, the victim's icon is displayed. Pressing G will end the mission.\n\n" +
				"The environment contains:\n"+
				"1. One dislocated victim,\n"+
				"2. Two grids that contain radioactive wastes,\n" +
				"3. Estimated 10% of the grids may contain fire pits.\n\n"+
				"Equipment: The robot is equipped with two shots of anti-radioactive decontaminants.\n\n" +
				"Other Notes:\n"+
				"1. The robot is destroyed if it is in the square with a fire pit.\n"+
				"2. The robot is destroyed if it is in the square with active radioactive wastes.\n"+
				"3. Shooting decontaminants toward a direction the robot is facing\n"+
				"   will disable radioactive wastes (if any) in the adjacent grid, and\n"+
				"   an icon (signifying disinfection) will be displayed."
	};
}
