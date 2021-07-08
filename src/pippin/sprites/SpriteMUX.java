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

public class SpriteMUX extends SpriteComponent {

    static {
        lineColors = ColorTools.interpArrayHSB(Color.green, new Color(128, 0, 0), 10);
        bgColors = ColorTools.interpArrayHSB(Color.green, new Color(224, 224, 224), 10);
    }

    long lineTime;
    long bgTime;
    static final int COLOR_COUNT = 10;
    static final int COLOR_TIME = 100;
    static Color[] lineColors;
    static Color[] bgColors;
    boolean calcOffsets;
    static final String label = "MUX";
    int xOffset;
    int yOffset;
    Font font;
    Polygon border;
    static final float SLOPE = 2F;
    static final int INPUT_DIST = 15;
    boolean showLine;
    boolean rightNode;



    public SpriteMUX(Stage stage, int x, int y, int width, int height) {
        super(stage, "MUX", x, y, width, height);
        calcOffsets = true;
        font = new Font("Helvetica", 0, 12);
        showLine = false;
        addNode("ADD", 15, 0);
        addNode("DAT", width - 15, 0);
        addNode("ALU", width / 2, height);
        addNode("DEC", (int)((float)height / 2.0F / 2.0F), height / 2);
        border = new Polygon();
        border.addPoint(x, y);
        border.addPoint(x + width, y);
        border.addPoint((x + width) - (int)((float)height / 2.0F), y + height);
        border.addPoint(x + (int)((float)height / 2.0F), y + height);
        border.addPoint(x, y);
    }

    public void set(boolean rightNode) {
        this.rightNode = rightNode;
        showLine = true;
        markDirty();
    }

    public void clear() {
        showLine = false;
        markDirty();
    }

    public void flashLine() {
        lineTime = System.currentTimeMillis();
        markDirty();
    }

    public void flashBG() {
        bgTime = System.currentTimeMillis();
        markDirty();
    }

    public boolean drawAll(Graphics g) {
        markClean();
        if(calcOffsets) {
            FontMetrics fm = g.getFontMetrics(font);
            xOffset = (super.getWidth() - fm.stringWidth("MUX")) / 2;
            yOffset = super.getHeight() - (super.getHeight() - (fm.getAscent() - (1 + font.getSize() / 12))) / 2;
            calcOffsets = false;
        }
        super.dirty |= ColorTools.setColor(g, bgColors, bgTime);
        g.fillPolygon(border);
        g.setColor(Color.black);
        g.drawPolygon(border);
        g.setColor(Color.black);
        g.setFont(font);
        g.drawString("MUX", super.getX() + xOffset, super.getY() + yOffset);
        if(super.dirtyPartner != null)
            super.dirtyPartner.markDirty();
        return super.dirty;
    }

}
