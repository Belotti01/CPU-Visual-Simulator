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

import pippin.exceptions.AbortedException;

public class Node {

    private Vector<SpriteWire> wires;
    private final String name;
    boolean propagating;
    int x;
    private int y;



    public Node(String name, int x, int y) {
        this.wires = new Vector<>(5);
        this.propagating = false;
        this.name = name;
        this.x = x;
        this.setY(y);
    }



    public Point location() {
        return new Point(x, getY());
    }

    public String name() {
        return name;
    }

    public void addWire(SpriteWire wire) {
        wires.addElement(wire);
    }

    public boolean animateFrom(Node node) throws AbortedException {
        try {
            if (propagating) {
                return false;
            }
            if (node == this) {
                return true;
            }
            propagating = true;
            for (SpriteWire wire : wires) {
                if(wire.animateFrom(node)) {
                    propagating = false;
                    return true;
                }
            }
            propagating = false;
            return false;
        } catch(AbortedException e) {
            propagating = false;
            throw e;
        }
    }


    public synchronized void dispose() {
        if(wires != null) {
            @SuppressWarnings("unchecked")
			Vector<SpriteWire> tempWires = (Vector<SpriteWire>)wires.clone();
            for(int i = 0; i < tempWires.size(); i++) {
                tempWires.elementAt(i).dispose();
            }
            wires.removeAllElements();
            wires = null;
        }
    }


    @SuppressWarnings("deprecation")
	public void finalize() throws Throwable {
        dispose();
        super.finalize();
    }


    public String toString() {
        return getClass().getName() + "[name=" + name + ", wires.length=" + wires.size() + ", x=" + x + ", y=" + getY() + "]";
    }



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}

}