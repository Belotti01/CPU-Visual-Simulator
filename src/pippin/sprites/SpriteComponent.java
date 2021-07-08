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

import java.awt.*;
import java.util.Vector;

import pippin.stage.Stage;

// Sprite connected to nodes
public class SpriteComponent extends Sprite {

    String name;
    Vector<Node> nodes;

    public SpriteComponent(Stage stage, String name, int x, int y, int width, int height) {
        super(stage, new Point(x, y));
        nodes = new Vector<>(4);
        setWidth(width);
        setHeight(height);
        this.name = name;
    }

    public void addNode(String nodeName, int dX, int dY) {
        Node node = new Node(name + ":" + nodeName, super.getX() + dX, super.getY() + dY);
        nodes.addElement(node);
    }

    public void addStage(int theChannel) {
        super.addStage(theChannel);
        for (Node node : nodes) {
            super.stage.addNode(node);
        }
    }

    public boolean drawAll(Graphics g) {
        markClean();
        if (super.dirtyPartner != null) {
            super.dirtyPartner.markDirty();
        }
        return false;
    }

    public void dispose() {
    }

    public void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public String toString() {
        return getClass().getName() + "[width=" + super.getWidth() + ",height=" + super.getHeight() + "]";
    }

}
