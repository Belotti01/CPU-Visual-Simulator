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

public class SpriteSW {

    private final SpriteBox zeroFlag;
    private final SpriteBox negativeFlag;
    private final SpriteBox unusedBits;

    private boolean isZero;
    private boolean isNegative;



    public SpriteSW(Stage stage, int x, int y, int width, int height) {
        zeroFlag = new SpriteBox(stage, "zeroFlag", x, y, width/2, height, "1", "1", true);
        negativeFlag = new SpriteBox(stage, "negativeFlag", x+width/2, y, width/2, height, "0", "0", true);
        unusedBits = new SpriteBox(stage, "statusWordUnusedBits", x+width, y, 57, height, "- - - - - -", "- - - - - -", true);
        setIsZero(true);
        setIsNegative(false);
    }

    public int getX() {
        return zeroFlag.getX();
    }

    public int getY() {
        return zeroFlag.getY();
    }

    public int getWidth() {
        return zeroFlag.getWidth() + negativeFlag.getWidth() + unusedBits.getWidth();
    }

    public int getHeight() {
        return zeroFlag.getHeight();
    }


    public void setIsZero(boolean isZero) {
        if (this.isZero != isZero) {
            this.isZero = isZero;
            String text = isZero ? "1" : "0";
            zeroFlag.setText(text, text);
            flashIsZero();
        }
    }

    public void setIsNegative(boolean isNegative) {
        if (this.isNegative != isNegative) {
            this.isNegative = isNegative;
            String text = isNegative ? "1" : "0";
            negativeFlag.setText(text, text);
            flashIsNegative();
        }
    }

    public boolean isZero() {
        return isZero;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void flashIsZero() {
        zeroFlag.flashBG();
    }

    public void flashIsNegative() {
        negativeFlag.flashBG();
    }

    public void addStage(int theChannel) {
        zeroFlag.addStage(theChannel);
        negativeFlag.addStage(theChannel);
        unusedBits.addStage(theChannel);
    }

}
