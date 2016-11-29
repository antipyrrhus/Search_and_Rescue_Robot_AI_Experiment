import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

//import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Class: Intro.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *  Course:
 *  Written / Updated: Nov 26, 2016
 *
 *  This Class - Provides introductory information about the experiment scenario to the human subject.
 */
public class Intro extends JFrame {
	private static final long serialVersionUID = 1L;
	protected static int WIDTH, HEIGHT;
	protected JTextArea instructions; // instruction text
	protected JScrollPane instructionsSP;	//this will contain the above JTextArea
	protected JButton btnNext;			// Button
	protected JLabel pageNo;			//Label
	protected ImageIcon nissa;		// stock image of the victim
	protected JLabel picLabel;		//contains the above stock image
	protected int totalPagesOfIntro;
	protected int currentPage;
	protected int introIndex;
	protected String[] introStrArr;
	protected Timer timer;


	/**
	 * constructor
	 */
	public Intro() {
		this.introStrArr = InstructionMsg.INTRO;	//get the instruction String array from the static class InstructionMsg
		this.totalPagesOfIntro = this.introStrArr.length;
		this.introIndex = -1;

		//text area
		this.instructions = new JTextArea();
		this.instrSetFont(new Font("Serif", Font.BOLD, 17));
		instructions.setFocusable(false);
		instructions.setWrapStyleWord(true);
		instructions.setBackground(Color.LIGHT_GRAY);
		instructions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		instructionsSP = new JScrollPane(instructions);

		//Free stock image from https://pixabay.com/en/woman-glasses-business-woman-1254454/
		try {
			this.nissa = this.changeImageSize("images/nissa.jpg", 256, 384);	//changeImageSize() is a custom method
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		picLabel = new JLabel(nissa);

		//Next button and page no.
		btnNext = new JButton("Next");
		setBtnNextActionListener();	//custom method
		pageNo = new JLabel();
		currentPage = 0;

		//this pane will contain the button & page no.
		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.LIGHT_GRAY);
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(btnNext);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(pageNo);

		//add all components to the main pane
		//note: the picture of the woman is not added initially. will be shown later. See showNextInstruction() method.
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.setBackground(Color.WHITE);
		cp.add(instructionsSP, BorderLayout.CENTER);
		cp.add(buttonPane, BorderLayout.SOUTH);

		this.setPreferredSize(new Dimension(1000, 800));	//set default size

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();  // pack all the components in this JFrame
		setTitle("Search & Rescue -- Introduction");
		setVisible(true);  // show this JFrame

		this.showNextInstruction();	//Begin showing the instruction text.
	}

	/**
	 * Method: changeImageSize
	 * @param fileName image file to be resized
	 * @param width desired width
	 * @param height desired height
	 * @return resized image icon
	 */
	public ImageIcon changeImageSize(String fileName, int width, int height) throws IOException{ //change the image size to be fitted for the room
//		BufferedImage img = ImageIO.read(getClass().getResource(fileName));
//		Image newImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
//		ImageIcon newIcon = new ImageIcon(newImg);
//		return newIcon;

		URL url = Intro.class.getClassLoader().getResource(fileName);
		ImageIcon icon = new ImageIcon(url);
		Image img = icon.getImage();
		Image newImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newImg);
		return newIcon;
	}

	/**
	 * Method: instrSetFont
	 *         Sets the font style of the instruction text pane
	 * @param f
	 */
	protected void instrSetFont(Font f) {
		this.instructions.setFont(f);
	}

	/**
	 * Method: setPageNo
	 * @param currentPage
	 */
	protected void setPageNo(int currentPage) {
		this.pageNo.setText("Step " + currentPage + " of " + this.totalPagesOfIntro);
	}

	/**
	 * Method: setInstrText
	 * @param s
	 */
	protected void setInstrText(String s) {
		this.instructions.setText(s);
	}

	/**
	 * Method: setBtnNextActionListener
	 * @param o
	 */
	protected void setBtnNextActionListener() {
		//Reset any existing listeners
		for (ActionListener al : this.btnNext.getActionListeners()) {
			this.btnNext.removeActionListener(al);
		}

		this.btnNext.addActionListener(e -> showNextInstruction());
	}

	/**
	 * Method: showNextInstruction
	 */
	protected void showNextInstruction() {
		//Base case. If the intro section is over,
		//then we will go on to the tutorial section.
		if (introIndex == this.introStrArr.length - 1) {
			this.btnNext.setEnabled(false);
			this.setInstrText("Loading Tutorial. Please wait...");
			timer.start();		//short timer to allow the gui to update
			return;
		}

		timer = new Timer(1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				//TODO edit competence of robot here
//				SAR ww = new SAR("T00 0%B", "Robot", "N/A");		//initialize instance of SAR class with parameters set to tutorial mode
				SAR ww = new SAR("T00 25%B", "Robot", "N/A");		//this mode is the same as above except robot acts randomly 25% of the time
				while(!ww.isVisible()) {}	//wait for the tutorial GUI to load
				dispose();					//once GUI loads, close / destroy this Intro GUI. dispose() is a built-in method.
			}
		});

		introIndex++;
		this.setInstrText(this.introStrArr[introIndex]);
		this.currentPage++;
		this.setPageNo(currentPage);

		//Show the woman's image on the last page of the intro only.
		//For a better visual presentation, we temporarily swap the position of the picture
		//with the position of the instruction text.
		if (introIndex == this.introStrArr.length - 1) {
			this.getContentPane().remove(instructionsSP);
			this.getContentPane().add(picLabel, BorderLayout.CENTER);
			this.getContentPane().add(instructionsSP, BorderLayout.NORTH);
		} else {
			this.getContentPane().remove(picLabel);
			this.getContentPane().remove(instructionsSP);
			this.getContentPane().add(instructionsSP, BorderLayout.CENTER);
		}
	}

	/**
	 * A separate thread for an information popup for thread safety and user friendliness
	 */
	class PleaseWaitPopupThread extends Thread {
		public void run() {
			JOptionPane.showMessageDialog(null, "Please wait...", "Loading Tutorial", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/** Main method. Runs the Intro. */
	public static void main(String[] args) {
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Intro();
			}
		});
	}
}
