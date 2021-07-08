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

public class SpriteIR extends SpriteComponent {

    TextBox operatorBox;
    TextBox operandBox;
    Font font;

    public SpriteIR(Stage stage, int x, int y, int width, int height) {
        super(stage, "IR", x, y, width, height);
        font = new Font("Helvetica", 0, 12);
        addNode("DAT", width / 2, 0);
        addNode("DEC", width / 4, height);
        addNode("ADD", (3 * width) / 4, height);
        operatorBox = new TextBox(stage, x, y, width / 2, height, "", "", font, false, true);
        operandBox = new TextBox(stage, x + width / 2, y, width / 2, height, "", "", font, false, true);
    }

    public void setValue(int instruction) {
        int opcodeInt = CPUTools.top8Bits(instruction);
        int operandInt = CPUTools.bottom8Bits(instruction);
        String opcodeString = CPUTools.int16ToOpcode(instruction);
        String operandString = CPUTools.int16ToOperand(instruction);
        operatorBox.setText(opcodeString, CPUTools.intToBinString(opcodeInt, 8));
        operandBox.setText(operandString, CPUTools.intToBinString(operandInt, 8));
        markDirty();
    }

    public void flashOperator() {
        operatorBox.flashBG();
        markDirty();
    }

    public void flashOperand() {
        operandBox.flashBG();
        markDirty();
    }

    public void clear() {
        operatorBox.setText("", "");
        operandBox.setText("", "");
        markDirty();
    }

    public boolean drawAll(Graphics g) {
        markClean();
        super.dirty |= operandBox.drawAll(g);
        super.dirty |= operatorBox.drawAll(g);
        g.setColor(Color.black);
        g.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());
        if(super.dirtyPartner != null)
            super.dirtyPartner.markDirty();
        return super.dirty;
    }

}
