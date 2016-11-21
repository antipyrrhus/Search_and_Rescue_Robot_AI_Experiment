
import javax.swing.Timer;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import javax.swing.JTextField;

/** Class: SARMain.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course: HRI
 *
 *  Purpose - This is an OPTIONAL class with a basic user-friendly GUI that allows the
 *  end-user to set some options before starting the mission.
 *
 *  It is recommended that you run this class instead of SAR.java which, although
 *  it does have its own main class, requires the end user to input options
 *  into the console rather than on a GUI.
 *
 *  Little to no comments are provided for this class because the GUI is pretty self-explanatory
 *  (except maybe for the Timer class...)
 */
public class SARMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JComboBox<String> comboBox, comboBox_1;
	private JLabel lblStartingGame;

	private Timer timer;

	private String singlePlayerMode;
	private String startRoomIs_Always_0_0;
	private String aggressiveModeP1;
	private String aggressiveModeP2;
	private String humanOrRobotOption;
	private JTextField txtRobot1;
	private JTextField txtRobot2;
	private JTextField aiRandomPercentTxt;
	private JRadioButton rdbtnplayerMode, rdbtnplayerMode_1, tutorialBtn,
						 rdbtnStartingRoomIs, rdbtnRandomStartingRoom,
						 humanOnlyBtn, robotOnlyBtn, humanAndRobotBtn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SARMain frame = new SARMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SARMain() {
		/* These four variables work seamlessly with SAR.java class. */
		this.singlePlayerMode = "S";
		this.startRoomIs_Always_0_0 = "00";
		this.aggressiveModeP1 = "";
		this.aggressiveModeP2 = "";
		this.humanOrRobotOption = "H";

		setTitle("Options");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 458, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		rdbtnplayerMode = new JRadioButton("1-Player Mode");
		rdbtnplayerMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				singlePlayerMode = "S";
				//Enable other buttons if disabled
				SARMain.this.enableOrDisableBtns(true);
			}
		});
		rdbtnplayerMode.setSelected(true);
		buttonGroup.add(rdbtnplayerMode);
		rdbtnplayerMode.setBounds(43, 7, 109, 23);
		contentPane.add(rdbtnplayerMode);

		rdbtnplayerMode_1 = new JRadioButton("2-Player Mode");
		rdbtnplayerMode_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				singlePlayerMode = "";
				//Enable other buttons if disabled
				SARMain.this.enableOrDisableBtns(true);
			}
		});
		buttonGroup.add(rdbtnplayerMode_1);
		rdbtnplayerMode_1.setBounds(170, 7, 109, 23);
		contentPane.add(rdbtnplayerMode_1);

		tutorialBtn = new JRadioButton("Tutorial Mode");
		tutorialBtn.addActionListener(e -> {
			singlePlayerMode = "T";
			//Disable other buttons if enabled
			SARMain.this.enableOrDisableBtns(false);
		});
		buttonGroup.add(tutorialBtn);
		tutorialBtn.setBounds(300, 7, 109, 23);
		contentPane.add(tutorialBtn);

		rdbtnStartingRoomIs = new JRadioButton("Starting Room is (0,0)");
		rdbtnStartingRoomIs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startRoomIs_Always_0_0 = "00";
			}
		});
		rdbtnStartingRoomIs.setSelected(true);
		buttonGroup_1.add(rdbtnStartingRoomIs);
		rdbtnStartingRoomIs.setBounds(43, 47, 157, 23);
		contentPane.add(rdbtnStartingRoomIs);

		rdbtnRandomStartingRoom = new JRadioButton("Starting Room is Random");
		rdbtnRandomStartingRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startRoomIs_Always_0_0 = "";
			}
		});
		buttonGroup_1.add(rdbtnRandomStartingRoom);
		rdbtnRandomStartingRoom.setBounds(241, 47, 172, 23);
		contentPane.add(rdbtnRandomStartingRoom);

		//Manual, AI, or both
		JLabel manualVsAILabel = new JLabel("Choose manual or AI mode, or both:");
		manualVsAILabel.setBounds(43, 250, 300, 20);
		contentPane.add(manualVsAILabel);
		ButtonGroup manualVsAIBtnGrp = new ButtonGroup();
		humanOnlyBtn = new JRadioButton("Manual Control Only");
		humanOnlyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				humanOrRobotOption = "H";
			}
		});
		manualVsAIBtnGrp.add(humanOnlyBtn);
		humanOnlyBtn.setBounds(43, 270, 200, 20);
		contentPane.add(humanOnlyBtn);

		robotOnlyBtn = new JRadioButton("Automated Robot AI Only");
		robotOnlyBtn.addActionListener(e -> {
			humanOrRobotOption = "R";
		});
		manualVsAIBtnGrp.add(robotOnlyBtn);
		robotOnlyBtn.setBounds(43, 290, 200, 20);
		contentPane.add(robotOnlyBtn);

		humanAndRobotBtn = new JRadioButton("Both Manual and AI Enabled");
		humanAndRobotBtn.addActionListener(e -> {
			humanOrRobotOption = "B";
		});
		manualVsAIBtnGrp.add(humanAndRobotBtn);
		humanAndRobotBtn.setBounds(43, 310, 200, 20);
		contentPane.add(humanAndRobotBtn);

		humanOnlyBtn.setSelected(true);

		JButton btnStartGame = new JButton("Start");
		/* Once the "Start" button is pressed, set the "Starting mission. Please wait..." label to visible,
		 * then start a very short timer (1 millisecs) to allow this label to show up on the GUI.
		 * This is necessary because the GUI runs in a single thread, and since we're going to wait
		 * for the actual Game GUI to load before getting rid of this game options GUI,
		 * the lblStartingGame will NOT update to become visible unless we allow it to become visible
		 * either via a short timer or by running the process in a separate thread (multithreading).
		 * We'll just go with the former method for simplicity. */
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblStartingGame.setVisible(true);
				timer.start();
			}
		});
		btnStartGame.setBounds(161, 340, 109, 23);

		contentPane.add(btnStartGame);

		/* After a very short pause via the timer (see comment directly above), we will invoke the SAR class,
		 * and will continuously loop (doing nothing) while waiting for the main mission GUI to show up.
		 * Afterwards, we will disable this options GUI and make it invisible. */
		timer = new Timer(1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();

				/* Run class SAR via constructor invocation with all the options as a single String parameter.
				 * See SAR.java class for more details on its constructor. */
				String optionString = "";
				if (SARMain.this.tutorialBtn.isSelected()) {
					optionString = "T 00 0% B";	//Begin the game with the tutorial option if tutorial button selected
				} else {
					optionString = singlePlayerMode + " " + startRoomIs_Always_0_0 + " " + aggressiveModeP1 + " " +
							  aggressiveModeP2 + " " + aiRandomPercentTxt.getText() + "%" + " " + humanOrRobotOption;
				}

				System.out.println(optionString);	//Testing
				SAR ww = new SAR(optionString, txtRobot1.getText(), txtRobot2.getText());

				while(!ww.isVisible()) {}	//wait for the main GUI to load while doing nothing...
				dispose();					//once main GUI loads, close / destroy this options GUI. dispose() is a built-in method.
			}
		});

		JLabel lblPlayerAggressive = new JLabel("Player 1 Aggressive Mode:");
		lblPlayerAggressive.setBounds(53, 98, 157, 14);
		contentPane.add(lblPlayerAggressive);

		JLabel lblPlayerAggressive_1 = new JLabel("Player 2 Aggressive Mode:");
		lblPlayerAggressive_1.setBounds(256, 98, 157, 14);
		contentPane.add(lblPlayerAggressive_1);

		comboBox = new JComboBox<>();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(comboBox.getSelectedItem().toString().equals("OFF")) {
					aggressiveModeP1 = "";
				}
				else {
					aggressiveModeP1 = "A1";
				}
			}
		});
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"OFF", "ON"}));
		comboBox.setBounds(85, 130, 62, 20);
		contentPane.add(comboBox);

		comboBox_1 = new JComboBox<>();
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(comboBox_1.getSelectedItem().toString().equals("OFF")) {
					aggressiveModeP2 = "";
				}
				else {
					aggressiveModeP2 = "A2";
				}
			}
		});
		comboBox_1.setModel(new DefaultComboBoxModel<String>(new String[] {"OFF", "ON"}));
		comboBox_1.setBounds(289, 130, 62, 20);
		contentPane.add(comboBox_1);

		lblStartingGame = new JLabel("Please wait...");
		lblStartingGame.setForeground(Color.RED);
		lblStartingGame.setBounds(250, 312, 190, 14);
		lblStartingGame.setVisible(false);
		contentPane.add(lblStartingGame);

		JLabel lblPlayersName = new JLabel("Player 1's Name:");
		lblPlayersName.setBounds(53, 161, 109, 14);
		contentPane.add(lblPlayersName);

		JLabel lblPlayersName_1 = new JLabel("Player 2's Name:");
		lblPlayersName_1.setBounds(256, 161, 109, 14);
		contentPane.add(lblPlayersName_1);

		txtRobot1 = new JTextField();
		txtRobot1.setText("Rescue Robot");
		txtRobot1.setBounds(43, 186, 132, 20);
		contentPane.add(txtRobot1);
		txtRobot1.setColumns(10);

		txtRobot2 = new JTextField();
		txtRobot2.setText("N/A");
		txtRobot2.setBounds(241, 186, 132, 20);
		contentPane.add(txtRobot2);
		txtRobot2.setColumns(10);

		JLabel aiRandomPercentLbl = new JLabel("Frequency (%) of AI acting randomly:");
		aiRandomPercentLbl.setBounds(43, 216, 220, 20);
		contentPane.add(aiRandomPercentLbl);

		this.aiRandomPercentTxt = new JTextField();
		this.aiRandomPercentTxt.setText("0");
		this.aiRandomPercentTxt.setBounds(260, 216, 50, 20);
		this.contentPane.add(this.aiRandomPercentTxt);
	}

	private void enableOrDisableBtns(boolean enable) {
		comboBox.setEnabled(enable);
		comboBox_1.setEnabled(enable);
		txtRobot1.setEnabled(enable);
		txtRobot2.setEnabled(enable);
		aiRandomPercentTxt.setEnabled(enable);
		rdbtnStartingRoomIs.setEnabled(enable);
		rdbtnRandomStartingRoom.setEnabled(enable);
		humanOnlyBtn.setEnabled(enable);
		robotOnlyBtn.setEnabled(enable);
		humanAndRobotBtn.setEnabled(enable);
	}
}
