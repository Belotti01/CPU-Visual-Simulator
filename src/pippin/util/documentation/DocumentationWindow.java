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
package pippin.util.documentation;

import pippin.util.ImageLoader;

import javax.swing.*;
import java.io.IOException;

public class DocumentationWindow extends JFrame {

    private JEditorPane editorPane;
    private JScrollPane scrollPane;

    public DocumentationWindow() {
        setTitle("Documentation");
        setSize(976, 900);
        setLocationRelativeTo(null);
        setIconImage(ImageLoader.getImage("icon"));
        try {
            editorPane = new JEditorPane(getClass().getClassLoader().getResource("pippin/resources/manual/Manual.html"));
            editorPane.setEditable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrollPane = new JScrollPane(editorPane);
        setContentPane(scrollPane);
    }
}
