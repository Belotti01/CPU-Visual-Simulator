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

public class SpriteLabel extends Sprite {

    Color textColor;
    Color bgColor;
    boolean calcOffsets;
    int xOffset;
    int yOffset;
    static final int LEFT = 1;
    static final int CENTER = 2;
    static final int RIGHT = 3;
    static final int TOP = 4;
    static final int BOTTOM = 5;
    int horPos;
    int vertPos;
    String text;
    private Font font;

    public SpriteLabel(Stage stage, String text, int x, int y, int width, int height, int horPos, int vertPos) {
        super(stage, x, y, width, height);
        textColor = Color.black;
        bgColor = Color.lightGray;
        calcOffsets = true;
        setFont(new Font("Helvetica", 0, 12));
        this.text = text;
        this.horPos = horPos;
        this.vertPos = vertPos;
    }

    public boolean drawAll(Graphics g) {
        markClean();
        g.setPaintMode();
        if(calcOffsets) {
            FontMetrics fm = g.getFontMetrics(getFont());
            if(horPos == 1)
                xOffset = 0;
            else if(horPos == 3)
                xOffset = super.getWidth() - fm.stringWidth(text);
            else
                xOffset = (super.getWidth() - fm.stringWidth(text)) / 2;
            if(vertPos == 4)
                yOffset = fm.getAscent() - (1 + getFont().getSize() / 12);
            else if(vertPos == 5)
                yOffset = super.getHeight();
            else
                yOffset = super.getHeight() - (super.getHeight() - (fm.getAscent() - (1 + getFont().getSize() / 12))) / 2;
            calcOffsets = false;
        }
        g.setColor(bgColor);
        g.fillRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());
        g.setColor(textColor);
        g.setFont(getFont());
        g.drawString(text, super.getX() + xOffset, super.getY() + yOffset);
        return super.dirty;
    }

    public String toString() {
        return getClass().getName() + "[]";
    }

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

}
