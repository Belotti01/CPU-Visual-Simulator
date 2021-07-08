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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

@SuppressWarnings({ "serial", "deprecation" })
public class ImageButton extends Canvas {

	public ImageButton(int type, String labelOn, String labelOff, Image imageOn, Image imageOff, Image imageDisabled,
			ImageButtonGroup group) {
		enabled = true;
		imageWidth = -1;
		imageHeight = -1;
		state = false;
		pressed = false;
		this.type = type;
		this.labelOn = labelOn;
		this.labelOff = labelOff;
		this.imageOn = imageOn;
		this.imageOff = imageOff;
		this.imageDisabled = imageDisabled;
		this.group = group;
		imageWidth = imageOn.getWidth(this);
		imageHeight = imageOn.getHeight(this);
		if (imageWidth != -1 && imageHeight != -1)
			resize(imageWidth, imageHeight);
	}

	public ImageButton(String label, Image imageOn, Image imageOff, Image imageDisabled) {
		this(1, label, null, imageOn, imageOff, imageDisabled, null);
	}

	public ImageButton(String labelOn, String labelOff, Image imageOn, Image imageOff, Image imageDisabled) {
		this(2, labelOn, labelOff, imageOn, imageOff, imageDisabled, null);
	}

	public ImageButton(String label, Image imageOn, Image imageOff, Image imageDisabled, ImageButtonGroup group) {
		this(3, label, null, imageOn, imageOff, imageDisabled, group);
	}

	public void enable(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			super.enable(enabled);
			paint(null);
		}
	}

	public void move(int x, int y) {
		reshape(x, y, size().width, size().height);
	}

	public void resize(int width, int height) {
		Rectangle bounds = bounds();
		reshape(bounds.x, bounds.y, width, height);
	}

	public void resize(Dimension d) {
		Rectangle bounds = bounds();
		reshape(bounds.x, bounds.y, d.width, d.height);
	}

	public synchronized void reshape(int x, int y, int width, int height) {
		Rectangle bounds = bounds();
		boolean resized = bounds.width != width || bounds.height != height;
		if (resized || bounds.x != x || bounds.y != y) {
			super.reshape(x, y, width, height);
			paint(null);
		}
	}

	public Dimension preferredSize() {
		Rectangle bounds = bounds();
		return new Dimension(bounds.width, bounds.height);
	}

	public Dimension minimumSize() {
		return preferredSize();
	}

	public void setState(boolean state) {
		switch (type) {
		case 2: // '\002'
			setStateInternal(state);
			paint(null);
			return;

		case 3: // '\003'
			if (group != null) {
				if (state)
					group.setCurrent(this);
				else if (this.state)
					group.setCurrent(null);
				paint(null);
				return;
			}
			// fall through

		case 1: // '\001'
		default:
			return;
		}
	}

	public boolean getState() {
		return state;
	}

	void setStateInternal(boolean state) {
		this.state = state;
		paint(null);
	}

	public boolean mouseMove(Event evt, int x, int y) {
		if (!enabled)
			return false;
		if (type == 1)
			pressed = false;
		return true;
	}

	public boolean mouseDown(Event evt, int x, int y) {
		if (!enabled)
			return false;
		switch (type) {
		default:
			break;

		case 1: // '\001'
			pressed = true;
			setStateInternal(true);
			break;

		case 2: // '\002'
			setStateInternal(!state);
			postActionEvent(evt, state);
			break;

		case 3: // '\003'
			if (group != null && group.getCurrent() != this) {
				group.setCurrent(this);
				postActionEvent(evt, state);
			}
			break;
		}
		return true;
	}

	public boolean mouseDrag(Event evt, int x, int y) {
		if (!enabled)
			return false;
		if (type == 1 && pressed) {
			boolean newState = mouseEventInside(evt.x, evt.y);
			if (state != newState)
				setStateInternal(newState);
			return true;
		} else {
			return false;
		}
	}

	public boolean mouseExit(Event evt, int x, int y) {
		if (!enabled)
			return false;
		if (type == 1) {
			if (pressed && state)
				setStateInternal(false);
			return true;
		} else {
			return false;
		}
	}

	public boolean mouseEnter(Event evt, int x, int y) {
		if (!enabled)
			return false;
		if (type == 1) {
			if (pressed && !state)
				setStateInternal(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean mouseUp(Event evt, int x, int y) {
		if (!enabled)
			return false;
		if (type == 1) {
			setStateInternal(false);
			if (pressed && mouseEventInside(evt.x, evt.y))
				postActionEvent(evt, true);
			pressed = false;
			return true;
		} else {
			return false;
		}
	}

	public String getLabel() {
		if (type == 1)
			return labelOn;
		if (state)
			return labelOn;
		else
			return labelOff;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (g == null)
			g = getGraphics();
		if (g != null) {
			Dimension d = size();
			g.drawImage(enabled ? state ? imageOn : imageOff : imageDisabled, 0, 0, d.width, d.height, this);
		}
	}

	protected String paramString() {
		return super.paramString() + ",state=" + state + ",labelOn=" + labelOn + ",labelOff=" + labelOff + ",imageOn="
				+ imageOn + ",imageOff=" + imageOff + ",imageWidth=" + imageWidth + ",imageHeight=" + imageHeight
				+ ",group=" + group;
	}

	public boolean imageUpdate(Image image, int flags, int x, int y, int w, int h) {
		boolean paint = false;
		boolean gotDimension = false;
		if (imageWidth == -1 && (flags & 1) != 0) {
			imageWidth = w;
			gotDimension = true;
		}
		if (imageHeight == -1 && (flags & 2) != 0) {
			imageHeight = h;
			gotDimension = true;
		}
		if (gotDimension && imageWidth != -1 && imageHeight != -1) {
			resize(imageWidth, imageHeight);
			invalidate();
			if (getParent() != null)
				getParent().layout();
			paint = true;
		}
		if ((flags & 0x28) != 0)
			paint = true;
		if (paint)
			paint(null);
		return (flags & 0xa0) == 0;
	}

	private void postActionEvent(Event evt, boolean eventState) {
		Event event = new Event(this, evt.when, 1001, evt.x, evt.y, evt.key, evt.modifiers,
				eventState ? ((Object) (labelOn)) : ((Object) (labelOff)));
		if (getParent() != null)
			getParent().postEvent(event);
	}

	private boolean mouseEventInside(int x, int y) {
		Dimension d = size();
		return x >= 0 && x < d.width && y >= 0 && y < d.height;
	}

	int type;
	String labelOn;
	String labelOff;
	Image imageOn;
	Image imageOff;
	Image imageDisabled;
	boolean enabled;
	int imageWidth;
	int imageHeight;
	ImageButtonGroup group;
	public static final int MOMENTARY = 1;
	public static final int TOGGLE = 2;
	public static final int RADIO = 3;
	boolean state;
	boolean pressed;
}
