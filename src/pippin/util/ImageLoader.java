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

package pippin.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

	private ImageLoader() {

	}


	public static Image getImage(String name) {
		Image image = null;
		try {
			URL imageURL = ImageLoader.class.getClassLoader().getResource("pippin/resources/images/" + name + ".png");
			image = ImageIO.read(imageURL);
			image.getHeight(null);
		} catch (IOException | IllegalArgumentException e) {
			System.err.println(name);
			e.printStackTrace();
		}
		return image;
	}

}