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
package pippin.stage;
import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.Vector;

import pippin.sprites.Node;
import pippin.sprites.Sprite;
import pippin.util.Global;

@SuppressWarnings("serial")
public class Stage extends FFCanvas implements Runnable {

    private final Vector<Vector<Sprite>> sprites;
    private final Vector<Node> nodes;
    private final Color bgColor;
    public Global mode;
    int lastMode;
    boolean aborting;
    boolean running;
    boolean dirty;



    public Stage(Color bg, Dimension stageSize, Global mode) {
        sprites = new Vector<>();
        lastMode = -1;
        aborting = false;
        nodes = new Vector<>(30);
        running = true;
        dirty = false;
        this.mode = mode;
        setBackground(bg);
        bgColor = bg;
        setSize(stageSize);
        new Thread(this, "StageRepainter").start();
    }



    public void run() {
        while(running) {
            if(dirty)
                update(null);
            try {
                Thread.sleep(10L);
            } catch(InterruptedException _ex) {
                // NO-OP
            }
        }
    }

    public void startRepaint() {
        dirty = true;
    }

    public int getMode() {
        return mode.getInt();
    }

    public void setMode(int mode) {
        this.mode.setInt(mode);
    }

    public void aborting(boolean aborting) {
        this.aborting = aborting;
    }

    public boolean aborting() {
        return aborting;
    }

    public Dimension minimumSize() {
        return getSize();
    }

    public Dimension preferredSize() {
        return getSize();
    }

    public synchronized void addSprite(Sprite theSprite, int theChannel) {
        if(theSprite.stage == null) {
            System.out.println("Stage.addSprite with sprite.stage == null!");
        }
        if(sprites.size() <= theChannel) {
            sprites.setSize(theChannel + 1);
        }
        Vector<Sprite> vector;
        if(sprites.elementAt(theChannel) == null) {
            sprites.setElementAt(vector = new Vector<>(), theChannel);
        } else {
            vector = sprites.elementAt(theChannel);
        }
        if(vector.contains(theSprite)) {
            System.out.println("Stage.addSprite: the sprite is already there!");
        }
        vector.addElement(theSprite);
        startRepaint();
    }

    public void removeSprite(Sprite theSprite) {
        for(Vector<Sprite> v : sprites) {
            if(v != null && v.removeElement(theSprite)) {
                markDirty();
                return;
            }
        }
        System.err.println("Stage.removeSprite: couldn't find sprite " + theSprite);
    }

    public void removeSprites(int theChannel) {
        if(theChannel < sprites.size()) {
            sprites.setElementAt(null, theChannel);
            markDirty();
        }
    }

    public void removeAllSprites() {
        for(int theChannel = 0; theChannel < sprites.size(); theChannel++) {
            removeSprites(theChannel);
        }
    }

    public synchronized void addNode(Node node) {
        nodes.addElement(node);
    }

    public Node nodeByName(String name) {
        for(Node node : nodes) {
            if(node.name().equals(name)) {
                return node;
            }
        }
        System.err.println("Node.getNode: couldn't find node \"" + name + "\"");
        for(Node node : nodes) {
            System.err.println(" (item in list: " + node + ")");
        }
        return null;
    }

    public Sprite locateSprite(int x, int y) {
        for(Vector<Sprite> v : sprites) {
            if(v != null) {
                for(Sprite s : v) {
                    if(s.inside(x, y)) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    public void dirtySprite(Sprite sprite) {
        startRepaint();
    }

    public void markDirty() {
        lastMode = -1;
        startRepaint();
    }

    public void paintAll(Graphics g) {
        boolean redraw = false;
        System.currentTimeMillis();
        g.setColor(bgColor);
        g.setPaintMode();
        Dimension d = getSize();
        g.fillRect(0, 0, d.width, d.height);
        synchronized(this) {
            for(Vector<Sprite> v : sprites) {
                if (v != null) {
                    for (Sprite s : v) {
                        redraw |= s.drawAll(g);
                    }
                }
            }
        }
        if(!redraw) {
            dirty = false;
        }
    }

    public void paintNew(Graphics g) {
        boolean redraw = false;
        boolean newMode = getMode() != lastMode;
        lastMode = getMode();
        System.currentTimeMillis();
        synchronized(this) {
            try {
                for (Vector<Sprite> v : sprites) {
                    if (v != null) {
                        for (Sprite s : v) {
                            if (newMode) {
                                redraw |= s.drawAll(g);
                            } else {
                                boolean thisRedraw = s.drawNew(g);
                                redraw |= thisRedraw;
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException e) {
                dirty = true;
            }
        }
        if(!redraw) {
            dirty = false;
        }
    }

}
