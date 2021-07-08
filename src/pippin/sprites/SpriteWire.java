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

import pippin.stage.NEWCPUSim;
import pippin.exceptions.AbortedException;
import pippin.stage.Stage;
import pippin.util.ColorTools;

public class SpriteWire extends Sprite {


    Node start;
    Node end;
    int[] xList;
    int[] yList;
    long[] timeList;
    int count;
    boolean horizontal;
    static final int SEG_LENGTH = 15;
    static final int COLOR_COUNT = 10;
    static final int COLOR_TIME = 100;
    static final int SEG_TIME = 100;
    Color[] colors;
    NEWCPUSim newcpuSim;


    public SpriteWire(Stage stage, Node start, Node end, NEWCPUSim newcpuSim, String busType) {
        super(stage, new Point(0, 0));
        this.newcpuSim = newcpuSim;
        this.start = start;
        start.addWire(this);
        this.end = end;
        end.addWire(this);
        setColor(busType);
        Point startPoint = start.location();
        Point endPoint = end.location();
        float distX = endPoint.x - startPoint.x;
        float distY = endPoint.y - startPoint.y;
        float dist = (float)Math.sqrt(distX * distX + distY * distY);
        count = (int)(dist / 15F);
        if (count == 0) {
            count = 1;
        }
        horizontal = distX != 0.0F;
        float x = startPoint.x;
        float y = startPoint.y;
        float diffX = distX / (float)count;
        float diffY = distY / (float)count;
        xList = new int[count + 1];
        yList = new int[count + 1];
        timeList = new long[count];
        int i;
        for (i = 0; i < count; i++) {
            xList[i] = (int)x;
            yList[i] = (int)y;
            timeList[i] = 0L;
            x += diffX;
            y += diffY;
        }

        xList[i] = (int)x;
        yList[i] = (int)y;
    }

    public SpriteWire(Stage stage, Node start, Node end, NEWCPUSim newcpuSim) {
        this(stage, start, end, newcpuSim, "");
    }

    public void setColor(String busType) {
        Color color;
        switch(busType) {
            case "address":
                color = Color.yellow;
                break;
            case "control":
                color = Color.pink;
                break;
            case "data":
                color = Color.cyan;
                colors = ColorTools.interpArrayHSB(Color.yellow, color, 10);
                return;
            case "addressExternal":
                color = Color.orange;
                break;
            case "controlExternal":
                color = Color.red;
                break;
            case "dataExternal":
                color = Color.blue;
                break;
            default:
                color = Color.black;
        }
        colors = ColorTools.interpArrayHSB(Color.green, color, 10);
    }

    public void addStage(int theChannel) {
        super.addStage(theChannel);
        super.stage.addNode(start);
        super.stage.addNode(end);
    }

    public boolean inside(int x, int y) {
        return false;
    }

    public void mouseHandle(@SuppressWarnings("deprecation") Event event1) {
    }

    public boolean animateFrom(Node goal) throws AbortedException {
        Node node = start;
        boolean wasStart = true;
        for (int i = 0; i < 2; i++) {
            if (node.animateFrom(goal)) {
                long nextTime = System.currentTimeMillis();
                for (int j = 0; j < count; j++) {
                    if (super.stage.aborting()) {
                        throw new AbortedException();
                    }
                    timeList[wasStart ? j : count - 1 - j] = nextTime;
                    markDirty();
                    //nextTime += 300L; // regulate animation speed
                    nextTime += newcpuSim.getNextTime();
                    long waitTime = nextTime - System.currentTimeMillis();
                    if (waitTime > 0L) {
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException _ex) {
                            //NO-OP
                        }
                    }
                }
                return true;
            }
            node = end;
            wasStart = false;
        }

        return false;
    }

    public boolean drawAll(Graphics g) {
        boolean wasDirty = super.dirty;
        markClean();
        g.setPaintMode();
        if(wasDirty) {
            int startX = xList[0];
            int startY = yList[0];
            System.currentTimeMillis();
            for(int i = 0; i < timeList.length; i++) {
                int endX = xList[i + 1];
                int endY = yList[i + 1];
                super.dirty |= ColorTools.setColor(g, colors, timeList[i]);
                if(horizontal) {
                    g.drawLine(startX, startY - 1, endX, endY - 1);
                    g.drawLine(startX, startY, endX, endY);
                    g.drawLine(startX, startY + 1, endX, endY + 1);
                } else {
                    g.drawLine(startX - 1, startY, endX - 1, endY);
                    g.drawLine(startX, startY, endX, endY);
                    g.drawLine(startX + 1, startY, endX + 1, endY);
                }
                startX = endX;
                startY = endY;
            }
        } else {
            g.setColor(colors[colors.length - 1]);
            int startX = xList[0];
            int startY = yList[0];
            int endX = xList[timeList.length - 1];
            int endY = yList[timeList.length - 1];
            if(horizontal) {
                g.drawLine(startX, startY - 1, endX, endY - 1);
                g.drawLine(startX, startY, endX, endY);
                g.drawLine(startX, startY + 1, endX, endY + 1);
            } else {
                g.drawLine(startX - 1, startY, endX - 1, endY);
                g.drawLine(startX, startY, endX, endY);
                g.drawLine(startX + 1, startY, endX + 1, endY);
            }
        }
        if(super.dirtyPartner != null)
            super.dirtyPartner.drawAll(g);
        return super.dirty;
    }

    public synchronized void dispose() {
        if(start != null) {
            start = null;
            end = null;
        }
    }

    public void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public String toString() {
        return getClass().getName() + "[start=" + start + ",end=" + end + "]";
    }

}
