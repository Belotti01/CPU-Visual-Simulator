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

public abstract class SpriteRAM extends SpriteComponent {

    int[] data;
    TextBox[] addBoxes;
    TextBox[] datBoxes;
    Font font;


    public SpriteRAM(Stage stage, String name, int x, int y, int width, int height) {
        super(stage, name, x, y, width, height);
        font = new Font("Helvetica", 0, 12);
    }

    public int getValue(int address) {
        int index = addressToIndex(address);
        return data[index];
    }

    public abstract String getStringValue(int i);

    public abstract String checkValue(int i, String s);

    public abstract String checkValue(int i, int j);

    public abstract void setValue(int i, String s);

    public abstract void setValue(int i, int j);

    public abstract int nextAddress(int i);

    public abstract int prevAddress(int i);

    public abstract boolean showAddress(int i);

    public void flashAddress(int address) {
        int index = addressToIndex(address);
        addBoxes[index].flashBG();
        markDirty();
    }

    public void flashData(int address) {
        int index = addressToIndex(address);
        datBoxes[index].flashBG();
        markDirty();
    }

    public int clickToAddress(int x, int y) {
        for (int i = 0; i < datBoxes.length; i++) {
            if (datBoxes[i].isInside(x, y)) {
                return indexToAddress(i);
            }
        }
        return -1;
    }

    public Rectangle addressToRect(int address) {
        int index = addressToIndex(address);
        return datBoxes[index].getRect();
    }

    protected abstract int addressToIndex(int i);

    protected abstract int indexToAddress(int i);

}
