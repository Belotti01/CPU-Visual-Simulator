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

public class SpriteBox extends SpriteRAM {
    TextBox box;
    Font font;
    String symText;
    String binText;
    int value;
    private boolean isPC;


    public SpriteBox(Stage stage, String name, int x, int y, int width, int height, String symText, String binText, boolean framed) {
        super(stage, name, x, y, width, height);
        font = new Font("Helvetica", 0, 12);
        setPC(false);
        box = new TextBox(stage, x, y, width, height, symText, binText, font, framed, true);
    }

    public SpriteBox(Stage stage, String name, int x, int y, int width, int height, String symText, String binText) {
        super(stage, name, x, y, width, height);
        font = new Font("Helvetica", 0, 12);
        setPC(false);
        box = new TextBox(stage, x, y, width, height, symText, binText, font, true, true);
    }

    public SpriteBox(Stage stage, String name, int x, int y, int width, int height, int value) {
        super(stage, name, x, y, width, height);
        font = new Font("Helvetica", 0, 12);
        setPC(false);
        symText = Integer.toString(value, 10);
        binText = CPUTools.intToBinString(value, 8);
        box = new TextBox(stage, x, y, width, height, symText, binText, font, true, true);
    }

    public void setText(String symText, String binText) {
        this.symText = symText;
        this.binText = binText;
        box.setText(symText, binText);
        markDirty();
    }

    public String checkValue(int address, String value) {
        int base = super.stage.getMode() != 1 ? 2 : 10;
        try {
            int result = CPUTools.stringToSEx8(value, base);
            return checkValue(address, result);
        } catch(NumberFormatException _ex) {
            return "not a legal " + (base != 10 ? "binary" : "decimal") + " number";
        }
    }

    public String checkValue(int address, int value) {
        int base = super.stage.getMode() != 1 ? 2 : 10;
        if(value < (isPC() ? '\0' : -128) || value > 127)
            return "out of range: must be from " + (base != 10 ? isPC() ? "00000000" : "10000000" : isPC() ? "0" : "-128") + " to " + (base != 10 ? isPC() ? "01111110" : "01111111" : isPC() ? "126" : "127");
        if(isPC() && value % 2 == 1)
            return "value must be even";
        else
            return null;
    }

    public void setValue(int value) {
        this.value = CPUTools.intToSEx8(value);
        String symText = CPUTools.sEx8ToDecString(this.value);
        String binText = CPUTools.sEx8ToBinString(this.value);
        setText(symText, binText);
        markDirty();
    }

    public void setValue(int address, int value) {
        setValue(value);
    }

    public void setValue(int address, String value) {
        try {
            int base = super.stage.getMode() != 1 ? 2 : 10;
            int result = CPUTools.stringToSEx8(value, base);
            setValue(address, result);
        } catch(NumberFormatException _ex) {
            //NO-OP
        }
    }

    public int getValue() {
        return value;
    }

    public int getValue(int address) {
        return getValue();
    }

    public String getStringValue(int address) {
        String result;
        if(super.stage.getMode() == 1)
            result = CPUTools.sEx8ToDecString(value);
        else
            result = CPUTools.sEx8ToBinString(value);
        return result;
    }

    public int nextAddress(int address) {
        return 0;
    }

    public int prevAddress(int address) {
        return 0;
    }

    public boolean showAddress(int addres) {
        return false;
    }

    public void flashAddress(int i) {
    }

    public void flashData(int i) {
    }

    public int clickToAddress(int x, int y) {
        return 0;
    }

    public Rectangle addressToRect(int address) {
        return new Rectangle(super.getX(), super.getY(), super.getWidth(), super.getHeight());
    }

    public void flashText() {
        box.flashText();
        markDirty();
    }

    public void flashBG() {
        box.flashBG();
        markDirty();
    }

    public boolean drawAll(Graphics g) {
        markClean();
        super.dirty |= box.drawAll(g);
        if(super.dirtyPartner != null)
            super.dirtyPartner.markDirty();
        return super.dirty;
    }

    protected int addressToIndex(int address) {
        return 0;
    }

    protected int indexToAddress(int index) {
        return 0;
    }

	public boolean isPC() {
		return isPC;
	}

	public void setPC(boolean isPC) {
		this.isPC = isPC;
	}

}
