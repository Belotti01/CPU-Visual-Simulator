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

import pippin.util.CPUTools;
import pippin.stage.Stage;

public class SpriteALU extends SpriteComponent {

    TextBox leftArgBox;
    TextBox rightArgBox;
    SpriteLabel operator;
    Font argFont;
    Polygon border;
    Color bgColor;
    static final int ARG_HEIGHT = 16;
    static final int ARG_VSPACE = 5;
    static final int OP_WIDTH = 15;
    static final int OP_HEIGHT = 15;
    static final float SLOPE = 3F;
    static final int V_WIDTH = 16;
    static final int INPUT_DIST = 35;


    public SpriteALU(Stage stage, int x, int y, int width, int height) {
        super(stage, "ALU", x, y, width, height);
        argFont = new Font("Helvetica", 0, 12);
        bgColor = new Color(224, 224, 224);
        addNode("DAT", 35, 0);
        addNode("MUX", width - 35, 0);
        addNode("DEC", (int)((float)(height / 2) / 3F), height / 2);
        addNode("ACC", width / 2, height);
        int veeHeight = 24;
        border = new Polygon();
        border.addPoint(x, y);
        border.addPoint(x + (width - 16) / 2, y);
        border.addPoint(x + width / 2, y + veeHeight);
        border.addPoint(x + (width + 16) / 2, y);
        border.addPoint(x + width, y);
        border.addPoint((x + width) - (int)((float)height / 3F), y + height);
        border.addPoint(x + (int)((float)height / 3F), y + height);
        border.addPoint(x, y);
        int argWidth = (width - 16) / 2 - 5;
        int argHSpace = 7;
        leftArgBox = new TextBox(stage, x + argHSpace, y + 5, argWidth, 16, "", "", argFont, false, true);
        rightArgBox = new TextBox(stage, (x + width) - (argWidth + argHSpace), y + 5, argWidth, 16, "", "", argFont, false, true);
        operator = new SpriteLabel(stage, "", x + (width - 15) / 2 +4, y + ((veeHeight + height) - 15) / 2, 15, 15, 1, 5);
        operator.addStage(1);
        operator.setFont(new Font("Helvetica", Font.BOLD, 15));
        operator.bgColor = new Color(224, 224, 224);
    }

    public void setLeftArg(int value) {
        value &= 0xff;
        int extendedValue;
        if ((value & 0x80) != 0) {
            extendedValue = value | 0xffffff00;
        } else {
            extendedValue = value;
        }
        leftArgBox.setText(Integer.toString(extendedValue, 10), CPUTools.intToBinString(value, 8));
        leftArgBox.flashBG();
        markDirty();
    }

    public void setRightArg(int value) {
        value &= 0xff;
        int extendedValue;
        if((value & 0x80) != 0)
            extendedValue = value | 0xffffff00;
        else
            extendedValue = value;
        rightArgBox.setText(Integer.toString(extendedValue, 10), CPUTools.intToBinString(value, 8));
        rightArgBox.flashBG();
        markDirty();
    }

    public void setOperation(char op) {
        operator.text = Character.toString(op);
        operator.markDirty();

        markDirty();
    }

    public void clear() {
        leftArgBox.setText("", "");
        rightArgBox.setText("", "");
        operator.text = " ";
        operator.markDirty();
        markDirty();
    }

    public boolean drawAll(Graphics g) {
        markClean();
        g.setColor(bgColor);
        g.fillPolygon(border);
        super.dirty |= leftArgBox.drawAll(g);
        super.dirty |= rightArgBox.drawAll(g);
        super.dirty |= operator.drawAll(g);
        g.setColor(Color.black);
        g.drawPolygon(border);
        if (super.dirtyPartner != null) {
            super.dirtyPartner.markDirty();
        }
        return super.dirty;
    }

    public boolean drawNew(Graphics g) {
        markClean();
        super.dirty |= leftArgBox.drawNew(g);
        super.dirty |= rightArgBox.drawNew(g);
        super.dirty |= operator.drawNew(g);
        g.setColor(Color.black);
        g.drawPolygon(border);
        if (super.dirtyPartner != null) {
            super.dirtyPartner.markDirty();
        }
        return super.dirty;
    }

}
