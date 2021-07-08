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

import pippin.stage.Stage;
import pippin.util.ColorTools;

public class SpriteRect extends Sprite {

    private Color bgColor;
    private Color[] bgColors;
    boolean framed;
    long time;


    public SpriteRect(Stage stage, int x, int y, int width, int height) {
        super(stage, new Point(0, 0));
        bgColors = ColorTools.interpArrayHSB(Color.green, new Color(224, 224, 224), 10);
        framed = false;
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        framed = true;
    }

    public SpriteRect(Stage stage, Color bgColor, Node horiz, Node vert) {
        super(stage, new Point(0, 0));
        bgColors = ColorTools.interpArrayHSB(Color.green, new Color(224, 224, 224), 10);
        framed = false;
        this.setBgColor(bgColor);
        int xCenter = horiz.location().x;
        int yCenter = vert.location().y;
        setX(xCenter - 3);
        setY(yCenter - 3);
        setWidth(7);
        setHeight(7);
        markDirty();
    }

    public boolean inside(int x, int y) {
        return false;
    }

    public void mouseHandle(@SuppressWarnings("deprecation") Event event1) {
    }

    public void flash() {
        time = System.currentTimeMillis();
        markDirty();
    }

    public boolean drawAll(Graphics g) {
        markClean();
        g.setPaintMode();
        if (getBgColor() != null) {
            g.setColor(getBgColor());
        } else {
            super.dirty |= ColorTools.setColor(g, bgColors, time);
        }
        g.fillRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());
        if(framed) {
            g.setColor(Color.black);
            g.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());
        }
        if(super.dirtyPartner != null) {
            super.dirtyPartner.drawAll(g);
        }
        return super.dirty;
    }

    public String toString() {
        return getClass().getName() + "[]";
    }

	public Color getBgColor() {
		return bgColor;
	}

	// removes the ability of flashing the BG
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

    // DOESN'T remove the ability of flashing the BG
	public void setBgColors(Color idle, Color animated) {
        bgColors = ColorTools.interpArrayHSB(animated, idle, 10);
    }

}
