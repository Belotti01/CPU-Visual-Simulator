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
package pippin.util;
import java.awt.*;

public class ColorTools {

    static final int COLOR_TIME = 100;

    private ColorTools() { }

    public static Color interpRGB(Color start, Color end, float progress) {
        float startRed = (float)start.getRed() / 255F;
        float startGreen = (float)start.getGreen() / 255F;
        float startBlue = (float)start.getBlue() / 255F;
        float endRed = (float)end.getRed() / 255F;
        float endGreen = (float)end.getGreen() / 255F;
        float endBlue = (float)end.getBlue() / 255F;
        return new Color(interpolate(startRed, endRed, progress, false), interpolate(startGreen, endGreen, progress, false), interpolate(startBlue, endBlue, progress, false));
    }

    public static Color[] interpArrayRGB(Color start, Color end, int count) {
        Color[] result = new Color[count];
        float startRed = (float)start.getRed() / 255F;
        float startGreen = (float)start.getGreen() / 255F;
        float startBlue = (float)start.getBlue() / 255F;
        float endRed = (float)end.getRed() / 255F;
        float endGreen = (float)end.getGreen() / 255F;
        float endBlue = (float)end.getBlue() / 255F;
        for (int i = 0; i < count; i++) {
            float progress = (float)i / (float)(count - 1);
            result[i] = new Color(interpolate(startRed, endRed, progress, false), interpolate(startGreen, endGreen, progress, false), interpolate(startBlue, endBlue, progress, false));
        }
        return result;
    }

    public static Color[] interpArrayHSB(Color start, Color end, int count) {
        Color[] result = new Color[count];
        float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
        float[] endHSB = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);
        if (startHSB[2] == 0.0F) {
            startHSB[0] = endHSB[0];
            startHSB[1] = endHSB[1];
        } else if (endHSB[2] == 0.0F) {
            endHSB[0] = startHSB[0];
            endHSB[1] = startHSB[1];
        } else if (startHSB[1] == 0.0F) {
            startHSB[0] = endHSB[0];
        } else if (endHSB[1] == 0.0F) {
            endHSB[0] = startHSB[0];
        }
        for (int i = 0; i < count; i++) {
            float progress = (float)i / (float)(count - 1);
            float hue = interpolate(startHSB[0], endHSB[0], progress, true);
            if (hue >= 1.0F) {
                hue = 0.0F;
            }
            float sat = interpolate(startHSB[1], endHSB[1], progress, false);
            float bri = interpolate(startHSB[2], endHSB[2], progress, false);
            result[i] = Color.getHSBColor(hue, sat, bri);
        }
        return result;
    }

    public static boolean setColor(Graphics g, Color[] colors, long time) {
        long index = (System.currentTimeMillis() - time) / 100L;
        if (index >= (long)(colors.length - 1)) {
            g.setColor(colors[colors.length - 1]);
            return false;
        } else {
            g.setColor(colors[(int)index]);
            return true;
        }
    }

    private static float interpolate(float start, float end, float progress, boolean wrap) {
        if (start + 0.5F > end && end + 0.5F > start) {
            wrap = false;
        }
        if (wrap) {
            if (start > end) {
                end++;
            } else {
                start++;
            }
        }
        float result = start + (end - start) * progress;
        if (result > 1.0F) {
            result--;
        }
        return result;
    }

}
