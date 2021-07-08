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

public class SpriteEditBox extends Sprite {
    static String clipboard = "";

    Font font;
    FontMetrics fontMetrics;
    StringBuffer text;
    String textToString;
    boolean selected;
    int value;
    boolean calcOffsets;
    boolean centered;
    int xOffset;
    int yOffset;
    Color unselText;
    Color unselBG;
    Color selText;
    Color selBG;
    static final int MARGIN = 4;

    public SpriteEditBox(Stage stage, int x, int y, int width, int height, String text, boolean centered) {
        super(stage, x, y, width, height);
        font = new Font("Helvetica", 0, 12);
        selected = true;
        calcOffsets = true;
        unselText = Color.black;
        unselBG = Color.white;
        selText = Color.white;
        selBG = Color.black;
        this.text = new StringBuffer(text);
        textToString = text;
        this.centered = centered;
    }

    public void setValue(String text) {
        this.text = new StringBuffer(text);
        textToString = text;
        selected = true;
        calcOffsets = true;
        markDirty();
    }

    public String getValue() {
        return text.toString();
    }

    public void addKey(char key) {
        if(selected) {
            selected = false;
            text.setLength(0);
        }
        if(key == '\b') {
            if(text.length() > 0)
                text.setLength(text.length() - 1);
        } else if(key >= ' ' && key <= '~') {
            text.append(Character.toUpperCase(key));
        }
        textToString = text.toString();
        if(fontMetrics != null && fontMetrics.stringWidth(textToString) > super.getWidth() - 8) {
            text.setLength(text.length() - 1);
            textToString = text.toString();
        }
        if(centered)
            calcOffsets = true;
        markDirty();
    }

    public void copyToClipboard() {
        clipboard = text.toString();
    }

    public void pasteFromClipboard() {
        text.setLength(0);
        text.append(clipboard);
        textToString = text.toString();
        markDirty();
    }

    public void cutToClipboard() {
        clipboard = text.toString();
        text.setLength(0);
        text.append("NOP");
        textToString = text.toString();
        markDirty();
    }

    public boolean drawAll(Graphics g) {
        markClean();
        if(calcOffsets) {
            if(fontMetrics == null)
                fontMetrics = g.getFontMetrics(font);
            yOffset = super.getHeight() - (super.getHeight() - (fontMetrics.getAscent() - (1 + font.getSize() / 12))) / 2;
            if(centered)
                xOffset = (super.getWidth() - fontMetrics.stringWidth(textToString)) / 2;
            else
                xOffset = 4;
            calcOffsets = false;
        }
        g.setColor(selected ? selBG : unselBG);
        g.fillRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());
        g.setColor(selected ? selText : unselText);
        g.setFont(font);
        g.drawString(textToString, super.getX() + xOffset, super.getY() + yOffset);
        return super.dirty;
    }

    public String toString() {
        return getClass().getName() + "[" + textToString + "]";
    }

}
