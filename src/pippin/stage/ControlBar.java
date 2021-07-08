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

import pippin.sprites.ImageButton;
import pippin.util.documentation.DocumentationWindow;
import pippin.util.serialization.RAMContent;
import pippin.util.serialization.RAMContentLoader;
import pippin.util.ImageLoader;

import java.awt.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;

public class ControlBar extends Panel {

	private final ImageButton resetButton;
	private final ImageButton stopButton;
	private final ImageButton stepButton;
	private final ImageButton playButton;
	private final JSlider slider;
	private final ImageButton symbolicBinaryButton;
	private final ImageButton animateButton;
	private final ImageButton clearAllButton;
	private final ImageButton openButton;
	private final ImageButton saveButton;
	private final ImageButton saveAsButton;
	private final ImageButton docsButton;
	private final DocumentationWindow documentationWindow;
	private final NEWCPUSim ncs;

	private int nextTime;
	private boolean animate;



	public ControlBar(NEWCPUSim ncs) {
		this.ncs = ncs;

		setLayout(new FlowLayout());
		setBackground(new Color(100, 100, 100));

		resetButton = new ImageButton("reset", ImageLoader.getImage("reset_on"), ImageLoader.getImage("reset_off"), null);
		resetButton.reshape(0, 299, 41, 51);
		add(resetButton);

		stopButton = new ImageButton("stop", ImageLoader.getImage("stop_on"), ImageLoader.getImage("stop_off"), null);
		stopButton.reshape(41, 299, 41, 51);
		add(stopButton);

		stepButton = new ImageButton("step", ImageLoader.getImage("step_on"), ImageLoader.getImage("step_off"), null);
		stepButton.reshape(82, 299, 41, 51);
		add(stepButton);

		playButton = new ImageButton("play", ImageLoader.getImage("play_on"), ImageLoader.getImage("play_off"), null);
		playButton.reshape(123, 299, 41, 51);
		add(playButton);

		slider = new JSlider(JSlider.HORIZONTAL, 10, 300, 155);
		slider.setPreferredSize(new Dimension(80, 50));
		slider.setBackground(new Color(100, 100, 100));
		slider.addChangeListener(event -> nextTime = 315 - slider.getValue() );
		slider.setUI(new BasicSliderUI(slider) {
			@Override
			public void paintFocus(Graphics g) {}
		});
		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		JLabel slow = new JLabel("Slow");
		slow.setForeground(Color.white);
		slow.setFont(new Font("Helvetica", Font.PLAIN, 12));
		JLabel fast = new JLabel("Fast");
		fast.setFont(new Font("Helvetica", Font.PLAIN, 12));
		fast.setForeground(Color.white);
		labels.put(10, slow);
		labels.put(300, fast);
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		add(slider);

		nextTime = 150;

		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new GridLayout(2, 1));

		symbolicBinaryButton = new ImageButton("symbolic", "binary", ImageLoader.getImage("binary"),
				ImageLoader.getImage("symbolic"), null);
		symbolicBinaryButton.reshape(204, 299, 80, 25);
		jPanel2.add(symbolicBinaryButton);

		animateButton = new ImageButton("animateOn", "animateOff", ImageLoader.getImage("animate_off"),
				ImageLoader.getImage("animate_on"), null);
		animateButton.reshape(204, 324, 80, 25);
		jPanel2.add(animateButton);
		animate = true;

		add(jPanel2);

		clearAllButton = new ImageButton("clearAll", ImageLoader.getImage("clearall_on"), ImageLoader.getImage("clearall_off"),
				null);
		clearAllButton.reshape(308, 299, 80, 50);
		add(clearAllButton);

		openButton = new ImageButton("open", ImageLoader.getImage("open_on"), ImageLoader.getImage("open_off"), null);
		openButton.reshape(398, 299, 60, 50);
		add(openButton);

		saveButton = new ImageButton("save", ImageLoader.getImage("save_on"), ImageLoader.getImage("save_off"),
				ImageLoader.getImage("save_disabled"));
		saveButton.reshape(460, 299, 60, 50);
		saveButton.enable(ncs.getRamContentLoader().canSave(ncs.getCpu().getSpriteIRAM().getContent()));
		add(saveButton);

		saveAsButton = new ImageButton("saveAs", ImageLoader.getImage("saveas_on"), ImageLoader.getImage("saveas_off"), null);
		saveAsButton.reshape(520, 299, 80, 50);
		add(saveAsButton);

		docsButton = new ImageButton("docs", ImageLoader.getImage("info_on"), ImageLoader.getImage("info_off"), null);
		docsButton.reshape(600, 299, 50, 50);
		add(docsButton);

		documentationWindow = new DocumentationWindow();

		ncs.getCpu().getSpriteIRAM().addContentChangeListener(e -> {
			saveButton.enable(ncs.getRamContentLoader().canSave((RAMContent)e.getNewValue()));
		});

		setVisible(true);
	}



	public boolean isAnimating() {
		return animate;
	}


	public int getNextTime() {
		return nextTime;
	}


	@Override
	@SuppressWarnings("deprecation")
	public boolean action(Event evt, Object what) {
		CPU cpu = ncs.getCpu();
		RAMContent ramContent = cpu.getSpriteIRAM().getContent();
		RAMContentLoader ramContentLoader = ncs.getRamContentLoader();

		if (evt.target == resetButton) {
			cpu.reset();
			return true;
		} else if (evt.target == stopButton) {
			cpu.stop();
			return true;
		} else if (evt.target == stepButton) {
			cpu.step();
			return true;
		} else if (evt.target == playButton) {
			cpu.play();
			return true;
		} else if (evt.target == symbolicBinaryButton) {
			cpu.changeMode(symbolicBinaryButton.getState() ? NEWCPUSim.BINARY_MODE : NEWCPUSim.SYMBOLIC_MODE);
			return true;
		} else if (evt.target == animateButton) {
			animate = !animate;
			if (!animate) {
				slider.setValue(300);
				slider.setEnabled(false);
			} else {
				slider.setValue(155);
				slider.setEnabled(true);
			}
			return true;
		} else if (evt.target == clearAllButton) {
			cpu.clear();
			return true;
		} else if (evt.target == openButton) {
			cpu.reset();
			if (cpu.editWrite()) {
				ramContent = ramContentLoader.load();
				symbolicBinaryButton.setState(false); // set symbolic mode
				cpu.getSpriteIRAM().setContent(ramContent);
				cpu.updateStage();
			}
			saveButton.enable(ramContentLoader.canSave(ramContent));
			return true;
		} else if (evt.target == saveButton) {
			cpu.reset();
			if (cpu.editWrite()) {
				ramContentLoader.save(ramContent);
				cpu.updateStage();
			}
			saveButton.enable(ramContentLoader.canSave(ramContent));
			return true;
		} else if (evt.target == saveAsButton) {
			cpu.reset();
			if (cpu.editWrite()) {
				ramContentLoader.saveAs(ramContent);
				cpu.updateStage();
			}
			saveButton.enable(ramContentLoader.canSave(ramContent));
			return true;
		} else if (evt.target == docsButton) {
			documentationWindow.setVisible(true);
			return true;
		} else {
			return false;
		}
	}

}
