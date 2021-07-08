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

public abstract class Sprite {

    public Stage stage;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean dirty;
    Sprite dirtyPartner;



    public Sprite(Stage stage, Point loc) {
        dirty = true;
        this.stage = stage;
        setX(loc.x);
        setY(loc.y);
    }

    public Sprite(Stage stage, int x, int y, int width, int height) {
        dirty = true;
        this.stage = stage;
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }



    public void setDirtyPartner(Sprite dirtyPartner) {
        this.dirtyPartner = dirtyPartner;
    }

    public void addStage(int theChannel) {
        stage.addSprite(this, theChannel);
    }

    public synchronized boolean inside(int x, int y) {
        return x >= this.getX() && y >= this.getY() && x < this.getX() + getWidth() && y < this.getY() + getHeight();
    }

    public void move(int x, int y) {
        this.setX(x);
        this.setY(y);
        markDirty();
    }

    public void move(Point p) {
        move(p.x, p.y);
    }

    public void reshape(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
        markDirty();
    }

    public void reshape(Rectangle rect) {
        reshape(rect.x, rect.y, rect.width, rect.height);
    }

    public /*synchronized*/ abstract boolean drawAll(Graphics g);

    public synchronized boolean drawNew(Graphics g) {
        if (dirty) {
            return drawAll(g);
        } else {
            return false;
        }
    }

    public final void markDirty() {
        if (stage == null) {
            System.out.println("Sprite.markDirty: stage is null with sprite " + this);
        } else {
            stage.dirtySprite(this);
        }
        dirty = true;
    }

    public final boolean isDirty() {
        return dirty;
    }

    public final void markClean() {
        dirty = false;
    }

    public abstract String toString();

    public void dispose() {
    }

    @SuppressWarnings("deprecation")
	public void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
