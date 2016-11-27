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

	public static String[] INTRO = {
			"Thank you for participating in our Human-Robot Interaction (HRI) experiment.\n\n"
		  + "In this experiment, you will control a robot avatar in the context of a simulated\n"
		  + "urban disaster recovery scenario, otherwise known as Urban Search and Rescue (USAR).\n\n"
		  + "For better visibility, we recommend that you maximize this window before continuing.\n\n"
		  + "Press the 'Next' button below for a description of the rescue scenario.",

		    "SCENARIO\n\n"
		  + "Be sure to read the following scenario carefully.\n\n"
		  + "A nuclear facility located in the outskirts of a U.S. territory has just experienced\n"
		  + "a catastrophic system failure, resulting in core melt accident (i.e. nuclear meltdown).\n"
		  + "As a result, the facility has been partially destroyed.\n\n"
		  + "Due to severe overheating and the venting of contaminated steam resulting in\n"
		  + "the presence of radioactive materials, it is unsafe for any person to remain within\n"
		  + "the facility's perimeters.\n\n"
		  + "The employees at the facility have largely managed to evacuate, but at present\n"
		  + "we know of one female by the name of Nissa Clark who is still inside.\n\n"
		  + "It is believed that she may be unconscious, injured, or trapped beneath a rubble,\n"
		  + "as her communication channels were abruptly cut short as she was requesting aid.\n\n"
		  + "While she is definitely inside the facility, her precise location is unknown.\n\n"
		  + "(Press Next to continue.)",

		    "SCENARIO (cont'd)\n\n"
		  + "Due to the extremely high level of danger associated with sending a human rescue squad,\n"
		  + "an experimental robot prototype has been dispatched to attempt to locate her whereabouts.\n\n"
		  + "Although the robot is equipped with a vision sensor, we expect it will be of little help\n"
		  + "in the facility, whose electrical powers have been shut down resulting in total darkness,\n"
		  + "which is further exacerbated by the presence of fire, smoke and radioactive materials.\n\n"
		  + "To help compensate for this problem, the robot has been equipped with temperature and\n"
		  + "smoke sensors to help deduce the location of nearby hazards.\n\n"
		  + "Further, to help decontaminate harmful radioactive materials, the robot has been given\n"
		  + "two bursts of decontaminant shots.\n\n"
		  + "(Press Next to continue.)",

		    "YOUR MISSION\n\n"
		  + "Your mission, as the robot's operator, is to ensure that the robot safely explores\n"
		  + "the nuclear facility while avoiding or decontaminating its hazardous areas,\n"
		  + "until the robot manages to locate the victim, Nissa Clark.\n\n"
		  + "You will first be given a tutorial along with practice drills in order to learn\n"
		  + "the robot's controls and the way it traverses the environment.\n\n"
		  + "After the tutorial and drills are complete, you and the robot will be sent out\n"
		  + "on the actual mission to rescue Nissa Clark.\n\n"
		  + "Press Next to view a short biographical information about Nissa Clark.",

		    "Name:\t\tNissa Clark\n"
		  + "Age:\t\t32\n"
		  + "Job Title:\t\tFacilities Manager\n"
		  + "Industry experience:\t9 years\n"
		  + "Education:\t\tB.A. Nuclear Engineering, Northeastern State University\n"
		  + "Marital Status:\tMarried with children\n"
		  + "Hobbies:\t\tReading, kayaking, music\n\n"
		  + "Press Next when ready to go on to the tutorial and practice missions."
	};

	public static String[] TUTORIAL = {
						"Before beginning the actual mission, you should learn how to control the robot.\n"
					  + "Let's begin the tutorial and complete a practice mission together.\n\n"
					  + "NOTE: We recommend that you maximize this window before continuing.\n\n"
					  + "Press the 'Next' button below when you are ready.",

						"To the left, you see the map of the area the robot will need to traverse\n"
					  + "in search of Nissa.",

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

					    "To use the decontaminants, we can have the robot face the direction of the grid\n"
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
					  + "Go ahead and move back north, then press Next to continue.",

					  	"Now have the robot rotate to its right by pressing the RIGHT arrow key,\n"
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
					  + "- G to give aid to Nissa once found.\n\n"
					  + "- Avoid grids containing fire pits.\n"
					  + "- Either avoid or decontaminate grids containing radioactive wastes.\n"
					  + "  (since the robot is out of decontaminants now, it must avoid these grids.)\n\n"
					  + "Continue exploring the area on your own, using your best judgment.\n"
					  + "Remember, Nissa is somewhere in this area.\n"
					  + "Once Nissa is discovered, the Next button will be enabled.",

					    "You have located Nissa! Well done.\n"
					  + "Press G to give emergency aid and finish the mission successfully,\n"
					  + "then press Next to continue.",

					  	"The robot has been destroyed, and the mission is a failure.\n"
					  + "Fortunately, this was a practice mission. Press A to start the tutorial again."
	};


	public static String[] DRILL_HUMAN = {
				"Now that you've successfully completed the tutorial,\n"
			  + "you will get one more practice drill on a different environment.\n"
			  + "The locations of Nissa, fire pits and radioactive wastes\n"
			  + "have all been changed for this drill.\n\n"
			  + "You will be completely on your own this time, though\n"
			  + "the next page will provide helpful reminders on keyboard commands.\n\n"
			  + "You will get only one chance to complete the mission,\n"
			  + "which will end either once you locate Nissa and press G to give aid,\n"
			  + "or once the robot is destroyed and the mission fails.\n\n"
			  + "Press Next when you are ready to begin.\n",

			    "Robot moves:\n" +
				"- LEFT and RIGHT arrow keys to rotate left and right.\n" +
			    "- SHIFT to move in the direction the robot is facing.\n" +
				"- 'S' to shoot decontaminants towards an adjacent grid which the robot is facing.\n" +
				"  (Note: the robot only has two rounds of shots, so use them judiciously)\n" +
				"- 'G' to give aid to Nissa once located (successfully ends the mission).\n\n" +
				"Robot Sensors can detect the following environmental cues: \n"+
				"1. Heightened temperature reading (indicated by a thermometer icon) in grids\n" +
				"   adjacent to that containing a fire pit. Fire pits must be avoided.\n"+
				"2. Smoke reading in grids adjacent to that containing radioactive wastes.\n" +
				"   Wastes must be either avoided or cleaned via shooting decontaminants.\n" +
				"3. Once the robot locates Nissa, her icon is displayed.\n"
			  + "   Pressing G will end the mission.",

				"The drill is over. Press NEXT to continue."
	};

	public static String[] DRILL_AI = {
			"For this next (and final) drill before the actual mission,\n"
		  + "you will observe the robot AI attempt the same drill you just attempted.",

		    "The robot is equipped with an AI mode that can reason about its\n"
		  + "immediate surroundings and, based on the temperature and smoke sensor readings,\n"
		  + "assess the risks of danger in adjacent areas, choosing its next move\n"
		  + "via an algorithm that attempts to minimize the risk of harm to the robot\n"
		  + "while continuing to explore new areas.",

		    "Essentially, the robot's AI has the same goal as you do:\n"
		  + "to locate Nissa as safely and quickly as possible.",

		    "The AI algorithm is still in an experimental phase, and we need you to\n"
		  + "help assess its performance.",

		    "Manual control of the robot has been disabled for this drill. Instead,\n"
		  + "a new control scheme (SPACEBAR) has been enabled that triggers the AI.",

		    "Tapping SPACEBAR will activate the AI for a single move. That is,\n"
		  + "the robot will autonomously make a decision as to what it should do next,\n"
		  + "and will either rotate, move in the direction it is facing, shoot decontaminants,\n"
		  + "or give aid to Nissa once found.",

		    "Starting on the next screen, the drill will begin.\n\n"
		  + "Once it does, you should keep tapping the SPACEBAR key at a slow and steady pace\n"
		  + "(e.g. once every few seconds or so).\n\n"
		  + "At the same time, you should be carefully observing the decisions\n"
		  + "that the robot's AI makes every step along the way, and assess\n"
		  + "whether you think the AI has made the right decision at each step,\n"
		  + "as well as whether you would have done anything differently.\n\n"
		  + "Press NEXT to begin.",

		    "Keep on tapping SPACEBAR at a slow and steady pace, and carefully observe\n"
		  + "the robot AI making its decisions each step.\n\n"
		  + "(NOTE: the robot has no more knowledge of the environment than you do, and\n"
		  + "depends solely on its temperature and smoke sensors to inform its decisions.\n"
		  + "As such, it is possible that the robot may fail this drill.)",

		    "The drill is over. Press NEXT to move on to the actual mission."
	};

	public static String[] FINAL_MISSION = {
		  "It is now time to begin the real mission.\n"
		+ "You (and the robot) have one chance only to locate and save Nissa.\n"
		+ "It is strongly advised that you refrain from taking unnecessary risks.",

		  "You have the full suite of controls at your disposal for this mission:\n\n"
		+ "- Manual mode (LEFT/RIGHT arrow keys to rotate, SHIFT to move, S to shoot,\n"
		+ "  G to give aid)\n"
	    + "- AI mode (SPACEBAR to trigger AI mode for a single move)",

	      "You may elect to manually control the robot for the entire mission, or\n"
	    + "you may press SPACEBAR to delegate the control task to the AI for one turn.\n"
	    + "(you can use SPACEBAR as many times as you wish)\n\n"
	    + "Whether you choose to control the robot manually, or rely entirely on the AI,\n"
	    + "or a combination of both, we will leave to your best judgment.\n\n"
	    + "Press NEXT when you are ready to begin.",

	      "As a reminder, here are the essential controls again:\n\n"
	    + "LEFT/RIGHT arrows to rotate the robot,\n"
	    + "SHIFT to move in the direction the robot is facing,\n"
	    + "S to shoot decontaminants (remember the robot only has two shots),\n"
	    + "G to give aid to Nissa once located (this ends the mission),\n"
	    + "SPACEBAR to enable AI for a single move.\n\n"
	    + "Good luck.",

	    "The mission is over. Please press NEXT to continue."
	};

	public static String FINAL_MESSAGE = "Thank you for completing the experiment.\n\n"
			                           + "IMPORTANT: In order to get credit for your participation,\n"
			                           + "please copy the code provided below and save it, as\n"
			                           + "you will be asked to provide the code at the end of the survey.\n\n"
			                           + "Once you have copied the code below, close this window\n"
			                           + "and return to the mturk survey.";
}
