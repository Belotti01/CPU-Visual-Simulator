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
package pippin.sprites;

public class ImageButtonGroup {

	public ImageButtonGroup() {
	}

	public ImageButton getCurrent() {
		return currentChoice;
	}

	public synchronized void setCurrent(ImageButton button) {
		if (button != null && button.group != this)
			return;
		if (button != currentChoice) {
			ImageButton oldChoice = currentChoice;
			currentChoice = button;
			if (oldChoice != null)
				oldChoice.setStateInternal(false);
			if (button != null)
				button.setStateInternal(true);
		}
	}

	public String toString() {
		if (currentChoice != null)
			return getClass().getName() + "[current=" + currentChoice.labelOn + "]";
		else
			return getClass().getName() + "[current=null]";
	}

	ImageButton currentChoice;
}
