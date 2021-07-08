/*
	Copyright notice

	Cengage Learning Inc. Reproduced by permission. http://www.cengage.com/permissions

	This simulator can be used exclusively for non-commercial, educational activities.
	This CPU Visual Simulator was derived (modified and extended - see credits) in 2021
	from the original PIPPIN Applet (© 1998 PWS Publishing Company), with permission from
	Cengage Learning Inc. Other deletions, additions, edits, changes to, or derivatives of
	the Cengage Material can only be made with Cengage’s express written approval, email sufficing.

	Credits

	Original PIPPIN applet:
	Prof. Stuart Hirshfield and Prof. Rick Decker (designers)
	CPU Visual Simulator extension, carried out with an Open Pedagogy / OER-enabled process:

	Renato Cortinovis, PhD - project coordinator
	Nicola Preda - porting to a Java application, initial new functionalities, and documentation
	Jonathan Cancelli - implementation of the great majority of new functionalities
	Alessandro Belotti - implementation of visualizations and animations
	Davide Riva - initial refactoring
	MariaPia Cavarretta, Giordano Cortinovis, Giovanni Ingargiola, Alessandro Suru, Piero Andrea Sileo and others - documentation.
*/
package pippin.stage;
import javax.swing.*;

import pippin.util.ImageLoader;
import pippin.util.serialization.RAMContentLoader;

import java.awt.*;


public class NEWCPUSim extends JFrame  {

	public static final int WINDOW_HEIGHT = 399;
	public static final int WINDOW_WIDTH = 745;
	public static final int CONTROLBAR_HEIGHT = 60;
	public static final int CPU_HEIGHT = 305;

	public static final int SYMBOLIC_MODE = 1;
	public static final int BINARY_MODE = 2;

	private final CPU cpu;
	private final ControlBar controlBar;
	private final RAMContentLoader ramContentLoader;



	public NEWCPUSim() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBackground(new Color(102, 102, 102));
		setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setLayout(null);
		setIconImage(ImageLoader.getImage("icon"));
		setLocationRelativeTo(null);

		cpu = new CPU(this);
		cpu.setBounds(0,0, WINDOW_WIDTH, CPU_HEIGHT);
		add(cpu);

		ramContentLoader = new RAMContentLoader(this);

		controlBar = new ControlBar(this);
		controlBar.setBounds(0, CPU_HEIGHT, WINDOW_WIDTH, CONTROLBAR_HEIGHT);
		add(controlBar);
	}



	public boolean isAnimating() {
		return controlBar.isAnimating();
	}


	public int delayValue() {
		return 500 - (300 - controlBar.getNextTime());
	}


	public int getNextTime() {
		return controlBar.getNextTime();
	}


	public CPU getCpu() {
		return cpu;
	}

	public RAMContentLoader getRamContentLoader() {
		return ramContentLoader;
	}

}
