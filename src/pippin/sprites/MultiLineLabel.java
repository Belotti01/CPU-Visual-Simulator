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
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class MultiLineLabel extends Canvas {

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    protected String[] lines;
    protected int num_lines;
    protected int margin_width;
    protected int margin_height;
    protected int line_height;
    protected int line_ascent;
    protected int[] line_widths;
    protected int max_width;
    protected int alignment;

    protected void newLabel(String label) {
        StringTokenizer t = new StringTokenizer(label, "\n");
        num_lines = t.countTokens();
        lines = new String[num_lines];
        line_widths = new int[num_lines];
        for(int i = 0; i < num_lines; i++)
            lines[i] = t.nextToken();

    }

    protected void measure() {
        FontMetrics fm = getFontMetrics(getFont());
        if(fm == null)
            return;
        line_height = fm.getHeight();
        line_ascent = fm.getAscent();
        max_width = 0;
        for(int i = 0; i < num_lines; i++) {
            line_widths[i] = fm.stringWidth(lines[i]);
            if(line_widths[i] > max_width)
                max_width = line_widths[i];
        }
    }

    public MultiLineLabel(String label, int margin_width, int margin_height, int alignment) {
        newLabel(label);
        this.margin_width = margin_width;
        this.margin_height = margin_height;
        this.alignment = alignment;
    }

    public MultiLineLabel(String label, int margin_width, int margin_height) {
        this(label, margin_width, margin_height, 0);
    }

    public MultiLineLabel(String label, int alignment) {
        this(label, 10, 10, alignment);
    }

    public MultiLineLabel(String label) {
        this(label, 10, 10, 0);
    }

    public void setLabel(String label) {
        newLabel(label);
        measure();
        repaint();
    }

    public void setFont(Font f) {
        super.setFont(f);
        measure();
        repaint();
    }

    public void setForeground(Color c) {
        super.setForeground(c);
        repaint();
    }

    public void setAlignment(int a) {
        alignment = a;
        repaint();
    }

    public void setMarginWidth(int mw) {
        margin_width = mw;
        repaint();
    }

    public void setMarginHeight(int mh) {
        margin_height = mh;
        repaint();
    }

    public int getAlignment() {
        return alignment;
    }

    public int getMarginWidth() {
        return margin_width;
    }

    public int getMarginHeight() {
        return margin_height;
    }

    public void addNotify() {
        super.addNotify();
        measure();
    }

    public Dimension preferredSize() {
        return new Dimension(max_width + 2 * margin_width, num_lines * line_height + 2 * margin_height);
    }

    public Dimension minimumSize() {
        return new Dimension(max_width, num_lines * line_height);
    }

    public void paint(Graphics g) {
        @SuppressWarnings("deprecation")
		Dimension d = size();
        int y = line_ascent + (d.height - num_lines * line_height) / 2;
        for(int i = 0; i < num_lines;) {
            int x;
            switch(alignment) {
                case 0: // '\0'
                    x = margin_width;
                    break;

                case 1: // '\001'
                default:
                    x = (d.width - line_widths[i]) / 2;
                    break;

                case 2: // '\002'
                    x = d.width - margin_width - line_widths[i];
                    break;
            }
            g.drawString(lines[i], x, y);
            i++;
            y += line_height;
        }
    }
}
