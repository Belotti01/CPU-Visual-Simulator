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

import pippin.stage.Stage;
import pippin.util.ColorTools;

import java.awt.*;
import java.util.Arrays;

public class MultiLineTextBox extends SpriteComponent {

    int[] xOffset;
    int yOffset;
    long textTime;
    long bgTime;
    boolean calcOffsets;
    boolean dirty;
    boolean frame;
    boolean centered;
    String[] lines;
    Color[] textColors;
    Color[] bgColors;
    Color borderColor;
    Font font;



    public MultiLineTextBox(Stage stage, String name, int x, int y, int width, int height, String...lines) {
        super(stage, name, x, y, width, height);
        calcOffsets = true;
        dirty = true;
        font = new Font("Helvetica", 0, 12);
        frame = true;
        centered = true;
        xOffset = new int[lines.length];
        yOffset = 4;
        setTextColors(Color.red, Color.black);
        setBGColors(Color.green, new Color(224, 224, 224));
        setBorderColor(Color.black);
        this.lines = lines;
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
        FontMetrics fm = g.getFontMetrics(font);
        if (calcOffsets) {
            if (centered) {
                for (int i = 0; i < lines.length; i++) {
                    xOffset[i] = (width - fm.stringWidth(lines[i])) / 2;
                }
            } else {
                Arrays.fill(xOffset, 5);
            }
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
        int currentY = y + fm.getAscent();
        for (int i = 0; i < lines.length; i++) {
            g.drawString(lines[i], x + xOffset[i], currentY + yOffset);
            currentY += fm.getHeight();
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
