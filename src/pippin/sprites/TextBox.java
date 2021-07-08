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

public class TextBox {

    int x;
    int y;
    int width;
    int height;
    int symXOffset;
    int binXOffset;
    int yOffset;
    long textTime;
    long bgTime;
    boolean calcOffsets;
    boolean dirty;
    boolean frame;
    boolean centered;
    String symText;
    String binText;
    String symTabText;
    String binTabText;
    Color[] textColors;
    Color[] bgColors;
    Color borderColor;
    Font font;
    Stage stage;

    public TextBox(Stage stage, int x, int y, int width, int height, String symText, String binText, Font font, boolean frame, boolean centered) {
        calcOffsets = true;
        dirty = true;
        this.stage = stage;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.font = font;
        this.frame = frame;
        this.centered = centered;
        setText(symText, binText);
        setTextColors(Color.red, Color.black);
        setBGColors(Color.green, new Color(224, 224, 224));
        setBorderColor(Color.black);
    }

    public TextBox(Stage stage, int x, int y, int width, int height, String symText, String binText, Font font, boolean frame, boolean centered, Color bgColor) {
        this(stage, x, y, width, height, symText, binText, font, frame, centered);
        setBGColors(Color.GREEN, bgColor);
    }

    public void setTextColors(Color activeColor, Color idleColor) {
        textColors = ColorTools.interpArrayRGB(activeColor, idleColor, 10);
    }

    public void setTextColors(Color[] textColors) {
        this.textColors = textColors;
    }

    public void setBGColors(Color activeColor, Color idleColor) {
        bgColors = ColorTools.interpArrayRGB(activeColor, idleColor, 10);
    }

    public void setBGColors(Color[] bgColors) {
        this.bgColors = bgColors;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void dumpColors() {
        for(int i = 0; i < bgColors.length; i++)
            System.out.println("Color " + i + " = " + bgColors[i]);

    }

    public void reshape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        calcOffsets = true;
        dirty = true;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isInside(int x, int y) {
        return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
    }

    public void setText(String symText, String binText) {
        int tabPos = symText.indexOf('\t');
        if(tabPos == -1) {
            this.symText = symText;
            symTabText = "";
        } else {
            this.symText = symText.substring(0, tabPos);
            symTabText = symText.substring(tabPos + 1);
        }
        tabPos = binText.indexOf('\t');
        if(tabPos == -1) {
            this.binText = binText;
            binTabText = "";
        } else {
            this.binText = binText.substring(0, tabPos);
            binTabText = binText.substring(tabPos + 1);
        }
        calcOffsets = true;
        dirty = true;
    }

    public void flashText() {
        textTime = System.currentTimeMillis();
        dirty = true;
    }

    public void flashBG() {
        bgTime = System.currentTimeMillis();
        dirty = true;
    }

    public boolean drawAll(Graphics g) {
        dirty = false;
        if (calcOffsets) {
            FontMetrics fm = g.getFontMetrics(font);
            if (centered) {
                symXOffset = (width - fm.stringWidth(symText)) / 2;
                binXOffset = (width - fm.stringWidth(binText)) / 2;
            } else {
                symXOffset = 5;
                binXOffset = 5;
            }
            yOffset = height - (height - (fm.getAscent() - (1 + font.getSize() / 12))) / 2;
            calcOffsets = false;
        }
        dirty |= ColorTools.setColor(g, bgColors, bgTime);
        g.fillRect(x, y, width, height);
        if (frame) {
            g.setColor(borderColor);
            g.drawRect(x, y, width, height);
        }
        dirty |= ColorTools.setColor(g, textColors, textTime);
        g.setFont(font);
        if (stage.getMode() == 1) {
            g.drawString(symText, x + symXOffset, y + yOffset);
            g.drawString(symTabText, x + 40, y + yOffset);
        } else {
            g.drawString(binText, x + binXOffset, y + yOffset);
            g.drawString(binTabText, x + 40, y + yOffset);
        }
        return dirty;
    }

    public boolean drawNew(Graphics g) {
        if (dirty)
            return drawAll(g);
        else
            return false;
    }

    public String toString() {
        return getClass().getName() + "[x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }

}
