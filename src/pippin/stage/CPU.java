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

import pippin.exceptions.AbortedException;
import pippin.sprites.*;
import static pippin.util.CPUTools.*;
import pippin.util.Global;

import javax.swing.*;

public class CPU extends Panel implements Runnable {

    private boolean isReset;
    private boolean isHalted;
    private boolean gotFocus;
    private boolean isStepping;
    private boolean isRunning;
    private int steps;
    private int barWidth;
    private int editAddress;
    private Stage stage;
    private Scrollbar scrollBar;
    private NEWCPUSim ncs;
    private Global mode;
    private Color bgColor;
    private Thread thread;
    private SpriteIR spriteIR;
    private MultiLineTextBox spriteDEC;
    private SpriteMUX spriteMUX;
    private SpriteALU spriteALU;
    private SpriteBox spriteACC;
    private SpriteBox spriteINC;
    private SpriteBox spritePC;
    private SpriteIRAM spriteIRAM;
    private SpriteRect spriteABUS;
    private SpriteSW spriteSW;
    private SpriteRAM editRAM;
    private SpriteRect cpuContainer;
    private SpriteEditBox editBox;



    public CPU(Frame frame) {
        mode = new Global("mode", NEWCPUSim.SYMBOLIC_MODE);
        bgColor = Color.lightGray;
        isRunning = true;
        isReset = false;
        isHalted = false;
        gotFocus = false;
        isStepping = false;
        ncs = (NEWCPUSim) frame;
        setLayout(null);
        setBackground(bgColor);
        scrollBar = new Scrollbar(Scrollbar.VERTICAL);
        frame.add(scrollBar);
        barWidth = scrollBar.getPreferredSize().width;
        frame.remove(scrollBar);
        scrollBar = new Scrollbar(Scrollbar.VERTICAL);
        add(scrollBar);
        scrollBar.setBounds(getSize().width - 27, 30, barWidth, 258);
        buildStage();
        internalReset();
        thread = new Thread(this, "EventHandler");
        thread.start();
    }



    public synchronized void clear() {
        reset();
        for (int i = SpriteIRAM.FIRST_ADDRESS; i <= SpriteIRAM.LAST_ADDRESS; i += SpriteIRAM.WORD_LENGTH) {
            spriteIRAM.setValue(i, "NOP");
            spriteIRAM.setLabel(i, "", true);
        }
    }


    public synchronized void reset() {
        if (editWrite()) {
            steps = 0;
            isReset = true;
            while (isStepping) {
                stage.aborting(true);
                try {
                    wait();
                } catch(InterruptedException _ex) {
                    //NO-OP
                }
            }
            stage.aborting(false);
            notifyAll();
        }
    }


    public synchronized void stop() {
        if (editWrite()) {
            steps = 0;
            while (isStepping) {
                stage.aborting(true);
                try {
                    wait();
                } catch (InterruptedException _ex) {
                    //NO-OP
                }
            }
            stage.aborting(false);
            notifyAll();
        }
    }


    public synchronized void step() {
        isHalted = false;
        if (editWrite()) {
            steps++;
            notifyAll();
        }
    }


    public synchronized void play() {
        isHalted = false;
        if (editWrite()) {
            steps = 1000;
            notifyAll();
        }
    }


    public synchronized void changeMode(int mode) {
        editWrite();
        this.mode.setInt(mode);
        if (editBox != null) {
            editBox.setValue(editRAM.getStringValue(editAddress));
        }
        repaint();
    }


    @Override
    public void run() {
        boolean step;
        while (isRunning) {
            synchronized (this) {
                if (!isReset && steps == 0) {
                    isStepping = false;
                    notifyAll();
                    try {
                        wait();
                    } catch (InterruptedException _ex) {
                        //NO-OP
                    }
                    isStepping = true;
                }
                if (steps > 0) {
                    step = true;
                    steps--;
                } else {
                    step = false;
                }
            }
            if (isReset) {
                steps = 0;
                internalReset();
                isReset = false;
            } else if (!internalDone()) {
                if (step) {
                    step = false;
                    internalStep();
                }
            } else {
                steps = 0;
            }
        }
    }


    @SuppressWarnings("deprecation")
	public boolean handleEvent(Event evt) {
        if (evt.id == Event.SCROLL_ABSOLUTE || evt.id == Event.SCROLL_LINE_DOWN || evt.id == Event.SCROLL_LINE_UP || evt.id == Event.SCROLL_PAGE_DOWN || evt.id == Event.SCROLL_PAGE_UP) {
            getSpriteIRAM().fromScroll();
            if (editRAM == getSpriteIRAM()) {
                Rectangle rect = editRAM.addressToRect(editAddress);
                rect.grow(-1, -1);
                editBox.reshape(rect);
            }
            return true;
        } else {
            return super.handleEvent(evt);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean gotFocus(Event evt, Object what) {
        gotFocus = true;
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean lostFocus(Event evt, Object what) {
        gotFocus = false;
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean mouseDown(Event evt, int x, int y) {
        if (isStepping) {
            return false;
        }
        if (!gotFocus) {
            requestFocus();
        }
        boolean isEditingLabel = false;
        if (editWrite()) {
            if (spriteIRAM.inside(x, y)) {
                editRAM = spriteIRAM;
                spriteIRAM.setIsEditingLabel(x, y);
                isEditingLabel = spriteIRAM.isEditingLabel();
            } else if (spritePC.inside(x, y)) {
                editRAM = spritePC;
            } else if (spriteACC.inside(x, y)) {
                editRAM = spriteACC;
            } else {
                return true;
            }
            editAddress = editRAM.clickToAddress(x, y);
            if (editAddress == -1) {
                editRAM = null;
                return true;
            }
            Rectangle rect = isEditingLabel ? spriteIRAM.addressToLabelRect(editAddress) : editRAM.addressToRect(editAddress);
            rect.grow(-1, -1);
            String text = isEditingLabel ? spriteIRAM.getLabelStringValue(editAddress) : editRAM.getStringValue(editAddress).replaceAll("\\t", " ");
            editBox = new SpriteEditBox(stage, rect.x, rect.y, rect.width, rect.height, text, editRAM != spriteIRAM);
            editBox.addStage(3);
        }
        return true;
    }


    @SuppressWarnings("deprecation")
	public boolean keyDown(Event evt, int key) {
        if (editRAM == null) {
            return false;
        }
        if (key == Event.TAB || key == 13 || key == Event.ENTER || key == Event.UP || key == Event.DOWN) {
            if (spriteIRAM.isEditingLabel()) {
                return false;
            }
            int oldAddress = editAddress;
            SpriteRAM oldEditRAM = editRAM;
            if (!editWrite()) {
                return true;
            }
            editRAM = oldEditRAM;
            if (evt.shiftDown() && key == Event.UP) {
                editAddress = editRAM.nextAddress(oldAddress);
            } else if (evt.shiftDown() || key == Event.UP) {
                editAddress = editRAM.prevAddress(oldAddress);
            } else {
                editAddress = editRAM.nextAddress(oldAddress);
            }
            editRAM.showAddress(editAddress);
            Rectangle rect = editRAM.addressToRect(editAddress);
            rect.grow(-1, -1);
            String text = editRAM.getStringValue(editAddress);
            editBox = new SpriteEditBox(stage, rect.x, rect.y, rect.width, rect.height, text, editRAM != spriteIRAM);
            editBox.addStage(3);
            return true;
        }

        if (editRAM.showAddress(editAddress)) {
            Rectangle rect = editRAM.addressToRect(editAddress);
            rect.grow(-1, -1);
            editBox.reshape(rect);
        }

        if (evt.controlDown() && key == 3) {         //Ctrl + C
            editBox.copyToClipboard();
            return true;
        }else if (evt.controlDown() && key == 22) {  //Ctrl + V
            editBox.pasteFromClipboard();
            return true;
        }else if (evt.controlDown() && key == 24) {  //Ctrl + X
            editBox.cutToClipboard();
            return true;
        }

        editBox.addKey((char)key);
        if (editRAM == spriteIRAM && !spriteIRAM.isEditingLabel() && evt.controlDown()) {
            if (key == 19) { //s
                spriteIRAM.shiftDownFromAddress(editAddress);
            } else if (key == 23) { //w
                spriteIRAM.shiftUpFromAddress(editAddress);
            } else {
                return true;
            }
            stage.removeSprite(editBox);
            editBox.dispose();
            editRAM = null;
            editBox = null;
            return true;
        }
        return true;
    }


    public boolean editWrite() {
        if (editRAM == null) {
            return true;
        }
        String value = editBox.getValue();
        if (value == null) {
            value = "";
        }
        String error = spriteIRAM.isEditingLabel() ? spriteIRAM.checkLabel(editAddress, value) : editRAM.checkValue(editAddress, value);
        if (error != null) {
            if (!value.isEmpty()) {
                String title;
                if (editRAM == spriteIRAM) {
                    if (spriteIRAM.isEditingLabel()) {
                        title = "Error writing label";
                    } else {
                        title = "Error writing instruction";
                    }
                } else if (editRAM == spritePC) {
                    title = "Error writing Program Counter";
                } else if (editRAM == spriteACC) {
                    title = "Error writing Accumulator";
                } else {
                    title = "Error writing value";
                }
                String message = "Error setting the value to \"" + value + "\":\n" + error + ".";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            }
            if (spriteIRAM.isEditingLabel()) {
                editBox.setValue(spriteIRAM.getLabelStringValue(editAddress));
            } else {
                editBox.setValue(editRAM.getStringValue(editAddress).replaceAll("\\t", " "));
            }
            return false;
        } else {
            stage.removeSprite(editBox);
            editBox.dispose();
            if (spriteIRAM.isEditingLabel()) {
                spriteIRAM.setLabel(editAddress, value, true);
            } else {
                editRAM.setValue(editAddress, value.replaceAll("\\s+", "\t"));
            }
            editRAM = null;
            editBox = null;
            return true;
        }
    }


    public void repaint() {
        stage.repaint();
        super.repaint();
    }


    public void updateStage() {
        stage.update(null);
    }


    private void buildStage() {
        stage = new Stage(bgColor, new Dimension(NEWCPUSim.WINDOW_WIDTH, NEWCPUSim.CPU_HEIGHT), mode);
        stage.reshape(0, 0, NEWCPUSim.WINDOW_WIDTH, NEWCPUSim.CPU_HEIGHT);
        add(stage);

        spriteIR = new SpriteIR(stage, 35, 30, 140, 20);
        spriteIR.addStage(2);

        makeLabel("IR", 35, 30, 140, 20, 1);

        spriteDEC = new MultiLineTextBox(stage, "DEC", 35, 80, 84, 40, "Control Unit /", "Decoder");
        spriteDEC.addNode("IR", 35, 0);
        spriteDEC.addNode("ALU", 35, spriteDEC.getHeight()+1);
        spriteDEC.addNode("MUX", spriteDEC.getWidth()+1, 30);
        spriteDEC.addStage(2);

        spriteMUX = new SpriteMUX(stage, 185, 100, 60, 20);
        spriteMUX.addStage(2);

        spriteALU = new SpriteALU(stage, 90, 170, 160, 50);
        spriteALU.addStage(2);

        makeLabel("ALU", 90, 170, 160, 50, 2);

        spriteACC = new SpriteBox(stage, "ACC", 135, 250, 70, 20, 0);
        spriteACC.addNode("ALU", 35, 0);
        spriteACC.addNode("DAT", 35, 21);
        spriteACC.addStage(2);

        makeLabel("ACC", 135, 250, 70, 20, 1);

        spriteINC = new SpriteBox(stage, "INC", 300, 100, 30, 20, "+2", "+10");
        spriteINC.addNode("ADD", 15, -1);
        spriteINC.addNode("PC", 15, 21);
        spriteINC.addStage(2);

        spritePC = new SpriteBox(stage, "PC", 280, 145, 70, 20, 0);
        spritePC.addNode("INC", 35, -1);
        spritePC.addNode("ADD", 35, 21);
        spritePC.setPC(true);
        spritePC.addStage(2);

        makeLabel("PC", 280, 145, 70, 20, 1);

        spriteIRAM = new SpriteIRAM(stage, 395, 30,  scrollBar);
        spriteIRAM.addStage(2);

        spriteABUS = new SpriteRect(stage, 390 + 95, 30, 5, 259);
        spriteABUS.addStage(2);
        spriteABUS.setBgColors(Color.orange, Color.green);

        spriteSW = new SpriteSW(stage,240, 250, 36, 20);
        spriteSW.addStage(2);

        makeLabel("Z", 245, 290, 18, 20, 1);
        makeLabel("N", 263, 290, 18, 20, 1);

        makeLabel("SW", 240, 250, 70, 20, 1);

        //CPU outer outline & Label
        final int labelOffsetX = 45;
        final int labelOffsetY = 25;
        cpuContainer = new SpriteRect(stage, 5, 3, spritePC.getX() + spritePC.getWidth(), 292); // 10 5 375 285
        cpuContainer.setBgColor(new Color(0, 0, 0, 0));
        cpuContainer.addStage(0);
        makeContainerLabel("CPU", cpuContainer.getX() + cpuContainer.getWidth() - labelOffsetX, cpuContainer.getY() + labelOffsetY + 20, 5, 5, 1);

        makeContainerLabel("RAM", ncs.getWidth() - 25 - labelOffsetX - 30, cpuContainer.getY() + labelOffsetY, 5, 5, 1);

        makeNode("ADD4", 265, 180);
        makeNode("DAT3", 15, 140);
        makeNode("DAT1", "ACC:DAT", 285);
        makeNode("DAT6", "IR:DAT", 10);
        makeNode("DAT2", "DAT3", "DAT1");
        makeNode("DAT4", "ALU:DAT", "DAT3");
        makeNode("DAT5", "DAT3", "DAT6");
        makeNode("DAT7", "MUX:DAT", "DAT6");
        makeNode("DAT7:OUT", cpuContainer.getX() + cpuContainer.getWidth(), "DAT6");
        makeNode("DAT8", "IRAM:DAT", "DAT6");
        makeNode("ADD1", "IR:ADD", "IRAM:ADD");
        makeNode("ADD2", "MUX:ADD", "IRAM:ADD");
        makeNode("ADD3", "ADD4", "IRAM:ADD");
        makeNode("ADD5", "PC:ADD", "ADD4");
        makeNode("ADD6", "INC:ADD", "IRAM:ADD");
        makeNode("DEC1", "DEC:ALU", "ALU:DEC");

        //DEC -> IRAM
        int decoderNodesX = spriteDEC.getX() + spriteDEC.getWidth() / 4;
        int decToRamTurnY = (int)((spriteACC.getY() + spriteACC.getHeight()) * 1.5f - spriteALU.getY()) - 5; //5 offset to avoid overlap with ACC label
        makeNode("DEC2", decoderNodesX, spriteDEC.getY() + spriteDEC.getHeight()+1);
        makeNode("DEC3", decoderNodesX, stage.nodeByName("DAT3").getY() - 4);
        makeNode("DEC4", decoderNodesX, stage.nodeByName("DAT3").getY() + 4);
        makeNode("DEC5", decoderNodesX, decToRamTurnY);
        makeNode("DEC6", spriteACC.getX() + spriteACC.getWidth() / 2 - 4, decToRamTurnY);
        makeNode("DEC7", spriteACC.getX() + spriteACC.getWidth() / 2 + 4, decToRamTurnY);
        makeNode("DEC7:OUT", cpuContainer.getX() + cpuContainer.getWidth(), decToRamTurnY);
        makeNode("DEC8", spriteABUS.getX()-1, decToRamTurnY);

        //IRAM -> ADD
        makeNode("ADD8", spriteABUS.getX()-1, "ADD6");
        makeNode("ADD7", cpuContainer.getX() + cpuContainer.getWidth(), "ADD6");
        makeWire("ADD6", "ADD7", "address");
        makeWire("ADD7", "ADD8", "addressExternal");

        //ALU -> SW
        makeNode("ALU:SW1", spriteALU.getX() + spriteALU.getWidth() - 7, "ALU:DEC");
        makeNode("ALU:SW2", spriteSW.getX() + spriteSW.getWidth() / 2, "ALU:DEC");
        makeNode("ALU:SW3", "ALU:SW2", stage.nodeByName("DEC7").getY() - 4);
        makeNode("ALU:SW4", "ALU:SW2", stage.nodeByName("DEC7").getY() + 4);
        makeNode("ALU:SW5", "ALU:SW2", spriteSW.getY() - 1);
        makeWire("ALU:SW1", "ALU:SW2", "data");
        makeWire("ALU:SW2", "ALU:SW3", "data");
        makeWire("ALU:SW4", "ALU:SW5", "data");
        
        makeWire("DEC2", "DEC3", "control");
        makeWire("DEC4", "DEC5", "control");
        makeWire("DEC5", "DEC6", "control");
        makeWire("DEC7", "DEC7:OUT", "control");
        makeWire("DEC7:OUT", "DEC8", "controlExternal");


        makeWire("ACC:DAT", "DAT1", "data");
        makeWire("DAT1", "DAT2", "data");
        makeWire("DAT2", "DAT3", "data");

        SpriteWire tempA = makeWire("DAT3", "DAT4", "data");

        makeWire("DAT4", "ALU:DAT", "data");
        makeWire("DAT3", "DAT5", "data");
        makeWire("DAT5", "DAT6", "data");
        makeWire("DAT6", "IR:DAT", "data");
        makeWire("DAT6", "DAT7", "data");

        SpriteWire tempC = makeWire("DAT7", "MUX:DAT", "data");

        makeWire("DAT7", "DAT7:OUT", "data");
        makeWire("DAT7:OUT", "DAT8", "dataExternal");
        makeWire("DAT8", "IRAM:DAT", "dataExternal");
        makeWire("IR:ADD", "ADD1", "data");
        makeWire("ADD1", "ADD2", "data");
        makeWire("ADD2", "MUX:ADD", "data");

        SpriteRect temp2 = makeRect(bgColor, "DAT7", "ADD3");
        SpriteWire tempD = makeWire("ADD2", "ADD3", "address");
        tempC.setDirtyPartner(temp2);
        temp2.setDirtyPartner(tempD);

        makeWire("ADD3", "ADD4", "address");
        makeWire("ADD4", "ADD5", "address");
        makeWire("ADD5", "PC:ADD", "address");
        makeWire("ADD3", "ADD6", "address");
        makeWire("ADD6", "INC:ADD", "address");
        makeWire("IR:DEC", "DEC:IR", "data");

        SpriteRect temp1 = makeRect(bgColor, "DEC1", "DAT3");
        SpriteWire tempB = makeWire("DEC:ALU", "DEC1", "control");
        tempA.setDirtyPartner(temp1);
        temp1.setDirtyPartner(tempB);

        makeWire("DEC1", "ALU:DEC", "control");
        makeWire("ALU:ACC", "ACC:ALU", "data");
        makeWire("DEC:MUX", "MUX:DEC", "control");
        makeWire("MUX:ALU", "ALU:MUX", "data");
        makeWire("INC:PC", "PC:INC", "address");
    }


    private void internalStep() {
        boolean jumping = false;
        boolean divideByZero = false;
        boolean opcodeIsInvalid = false;
        try {
            int opcode = top8Bits(getIRAM(getPC()));
            int operand = bottom8Bits(getIRAM(getPC()));
            boolean direct = opcode < 16;
            if (!direct) {
                opcode -= 16;
            }
            spriteIR.clear();
            spriteALU.clear();
            spriteMUX.clear();

            // flash address bus
            spritePC.flashBG();
            delay(ncs.delayValue());
            if (ncs.isAnimating()) {
                flashWire("PC:ADD", "ADD8");
                spriteABUS.flash();
            }
            delay(ncs.delayValue() / 2);
            spriteIRAM.showAddress(getPC());
            spriteIRAM.flashAddress(getPC());
            delay(ncs.delayValue());

            // flash control bus
            if (ncs.isAnimating()) {
                flashWire("DEC2", "DEC3");
                flashWire("DEC4", "DEC6");
                flashWire("DEC7", "DEC8");
            }

            // flash data and load instruction
            spriteIRAM.flashData(getPC());
            delay(ncs.delayValue());
            if (ncs.isAnimating()) flashWire("IRAM:DAT", "IR:DAT");
            spriteIR.setValue(getIRAM(getPC()));
            spriteIR.flashOperator();
            spriteIR.flashOperand();
            delay(ncs.delayValue());
            if (ncs.isAnimating()) spriteIR.flashOperator();
            if (!direct && !opcodeTakesImmediate(opcode)) {
                opcodeIsInvalid = true;
                throw new AbortedException();
            }

            switch (opcode) {
                case ADD :
                case SUB :
                case MUL :
                case DIV :
                case LOD :
                case AND :
                case NOT :
                case CMP :
                    char operation = opcodeToOperation(opcode);
                    int argument = (direct) ? top8Bits(getIRAM(operand)) : intToSEx8(operand);
                    int result = 0;

                    switch (operation) {
                        case '+' :
                            result = getACC() + argument;
                            break;
                        case '-' :
                            result = getACC() - argument;
                            break;
                        case 'x' :
                            result = getACC() * argument;
                            break;
                        case '=' :
                            result = argument;
                            break;
                        case '&' :
                            result = getACC() & argument;
                            break;
                        case ':' :
                            break;
                        case '!' :
                            result = ~argument;
                            break;
                        case '/' :
                            if (argument == 0) {
                                divideByZero = true;
                            } else {
                                result = getACC() / argument;
                            }
                            break;
                    }

                    // opcode goes to the decoder and decoder tells mux if direct or immediate
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                        flashWire("DEC:MUX", "MUX:DEC");
                    }
                    spriteMUX.set(direct);

                    // decoder tells alu the operation
                    if (ncs.isAnimating()) {
                        spriteMUX.flashBG();
                        delay(ncs.delayValue());
                        flashWire("DEC:ALU", "ALU:DEC");
                    }
                    spriteALU.setOperation(operation);
                    delay(ncs.delayValue());

                    // loads first operand from accumulator
                    if (operation != '=' && operation != '!') {
                        if (ncs.isAnimating()) {
                            spriteACC.flashBG();
                            delay(ncs.delayValue());
                            flashWire("ACC:DAT", "ALU:DAT");
                        }
                        spriteALU.setLeftArg(getACC());
                        delay(ncs.delayValue());
                    }

                    // if operand is direct loads operand from ram else send the operand to alu
                    if (ncs.isAnimating()) {
                        spriteIR.flashOperand();
                        delay(ncs.delayValue());
                        if (direct) {
                            flashWire("IR:ADD", "ADD8");
                            spriteABUS.flash();
                            delay(ncs.delayValue() / 2);
                            spriteIRAM.flashAddress(operand);
                            spriteDEC.flashBG();
                            flashWire("DEC2", "DEC3");
                            flashWire("DEC4", "DEC6");
                            flashWire("DEC7", "DEC8");
                            delay(ncs.delayValue());
                            spriteIRAM.flashData(operand);
                            delay(ncs.delayValue());
                            flashWire("IRAM:DAT", "MUX:DAT");
                        } else {
                            spriteIR.flashOperand();
                            flashWire("IR:ADD", "MUX:ADD");
                        }
                    }
                    spriteMUX.flashLine();
                    if (ncs.isAnimating()) flashWire("MUX:ALU", "ALU:MUX");
                    spriteALU.setRightArg(argument);
                    delay(ncs.delayValue());

                    // show the relative operation
                    if (divideByZero) {
                        throw new AbortedException();
                    } else {
                        if (operation != ':') {
                            if (ncs.isAnimating()) flashWire("ALU:ACC", "ACC:ALU");
                            setACC(result);
                            spriteACC.flashBG();
                            delay(ncs.delayValue());
                        }
                        if (operation != '=') {
                            if (operation == ':') {
                                if (ncs.isAnimating()) {
                                    flashWire("ALU:SW1", "ALU:SW3");
                                    flashWire("ALU:SW4", "ALU:SW5");
                                }
                                spriteSW.setIsZero(argument == getACC());
                                spriteSW.setIsNegative(argument > getACC());
                            } else {
                                if (ncs.isAnimating()) {
                                    flashWire("ALU:SW1", "ALU:SW3");
                                    flashWire("ALU:SW4", "ALU:SW5");
                                }
                                spriteSW.setIsZero(getACC() == 0);
                                spriteSW.setIsNegative(getACC() < 0);
                            }
                            delay(ncs.delayValue());
                        }
                    }
                    break;
                case STO :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                        flashWire("IR:ADD", "ADD8");
                        spriteABUS.flash();
                        delay(ncs.delayValue() / 2);
                        spriteIRAM.flashAddress(operand);
                        delay(ncs.delayValue());
                        spriteACC.flashBG();
                        delay(ncs.delayValue());
                        flashWire("ACC:DAT", "IRAM:DAT");
                        delay(ncs.delayValue() / 2);
                        flashWire("DEC2", "DEC3");
                        flashWire("DEC4", "DEC6");
                        flashWire("DEC7", "DEC8");
                    }
                    spriteIRAM.getShowAsVariable()[getSpriteIRAM().addressToIndex(operand)] = true;
                    setIRAM(operand, joinBits(getACC(), 0b00000000));
                    spriteIRAM.flashData(operand);
                    delay(ncs.delayValue());
                    break;
                case JMP :
                    jumping = true;
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                        spriteIR.flashOperand();
                        flashWire("IR:ADD", "PC:ADD");
                    }
                    setPC(operand);
                    spritePC.flashBG();
                    delay(ncs.delayValue());
                    break;
                case JZ :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                    }
                    spriteSW.flashIsZero();
                    delay(ncs.delayValue());
                    if (spriteSW.isZero()) {
                        jumping = true;
                        if (ncs.isAnimating()) {
                            spriteIR.flashOperand();
                            flashWire("IR:ADD", "PC:ADD");
                        }
                        setPC(operand);
                        spritePC.flashBG();
                        delay(ncs.delayValue());
                    }
                    break;
                case JNZ :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                    }
                    spriteSW.flashIsZero();
                    delay(ncs.delayValue());
                    if (!spriteSW.isZero()) {
                        jumping = true;
                        if (ncs.isAnimating()) {
                            spriteIR.flashOperand();
                            flashWire("IR:ADD", "PC:ADD");
                        }
                        setPC(operand);
                        spritePC.flashBG();
                        delay(ncs.delayValue());
                    }
                    break;
                case JN :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                    }
                    spriteSW.flashIsNegative();
                    delay(ncs.delayValue());
                    if (spriteSW.isNegative()) {
                        jumping = true;
                        if (ncs.isAnimating()) {
                            spriteIR.flashOperand();
                            flashWire("IR:ADD", "PC:ADD");
                        }
                        setPC(operand);
                        spritePC.flashBG();
                        if (ncs.isAnimating()) delay(ncs.delayValue());
                    }
                    break;
                case JNN :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                    }
                    spriteSW.flashIsNegative();
                    delay(ncs.delayValue());
                    if (!spriteSW.isNegative()) {
                        jumping = true;
                        if (ncs.isAnimating()) {
                            spriteIR.flashOperand();
                            flashWire("IR:ADD", "PC:ADD");
                        }
                        setPC(operand);
                        spritePC.flashBG();
                        delay(ncs.delayValue());
                    }
                    break;
                case NOP :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                    }
                    break;
                case HLT :
                    if (ncs.isAnimating()) {
                        flashWire("IR:DEC", "DEC:IR");
                        spriteDEC.flashBG();
                        delay(ncs.delayValue());
                    }
                    isHalted = true;
                    break;
                default :
                    opcodeIsInvalid = true;
                    throw new AbortedException();
            }
            if(getPC() < 126) {
                if (!jumping) {
                    if (ncs.isAnimating()) {
                        flashWire("PC:ADD", "INC:ADD");
                        spriteINC.flashBG();
                    }
                    delay(ncs.delayValue());
                    if (ncs.isAnimating()) flashWire("INC:PC", "PC:INC");
                    setPC(getPC() + 2);
                    spritePC.flashBG();
                    delay(ncs.delayValue());
                }
            } else {
                isHalted = true;
            }
        } catch (AbortedException _ex) {
            isHalted = true;
            String title;
            String message;
            if (divideByZero) {
                title = "Divide By Zero";
                message = "Error: ALU tried to divide by zero";
            } else if (opcodeIsInvalid) {
                title = "Invalid Opcode";
                message = "Error: invalid opcode found inside IR";
            } else {
                return;
            }
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }


    private void internalReset() {
        isHalted = false;
        setPC(0);
        setACC(0);
        spriteSW.setIsNegative(false);
        spriteSW.setIsZero(true);
        spriteALU.clear();
        spriteIR.clear();
        stage.update(null);
        spritePC.flashBG();
        spriteACC.flashBG();
    }


    private boolean internalDone() {
        return getPC() == 128 || isHalted;
    }


    private Node makeNode(String name, int x, int y) {
        Node node = new Node(name, x, y);
        stage.addNode(node);
        return node;
    }


    private Node makeNode(String name, String xName, int y) {
        Node xNode = stage.nodeByName(xName);
        return makeNode(name, xNode.location().x, y);
    }


    private Node makeNode(String name, String xName, String yName) {
        Node xNode = stage.nodeByName(xName);
        Node yNode = stage.nodeByName(yName);
        return makeNode(name, xNode.location().x, yNode.location().y);
    }

    private Node makeNode(String name, int x, String yName) {
        Node yNode = stage.nodeByName(yName);
        return makeNode(name, x, yNode.location().y);
    }


    private SpriteRect makeRect(Color color, String xName, String yName) {
        Node xNode = stage.nodeByName(xName);
        Node yNode = stage.nodeByName(yName);
        SpriteRect rect = new SpriteRect(stage, color, xNode, yNode);
        rect.addStage(1);
        return rect;
    }

    private SpriteWire makeWire(String start, String end, String busType) {
        Node startNode = stage.nodeByName(start);
        Node endNode = stage.nodeByName(end);
        SpriteWire wire = new SpriteWire(stage, startNode, endNode, ncs, busType);
        wire.addStage(0);
        return wire;
    }


    private SpriteLabel makeLabel(String text, int x, int y, int width, int height, int horPos) {
        int vertSpace = 4;
        SpriteLabel label = new SpriteLabel(stage, text, x, y - vertSpace, width, 0, horPos, 5);
        label.addStage(1);
        return label;
    }

    private SpriteLabel makeContainerLabel(String text, int x, int y, int width, int height, int horPos) {
        int vertSpace = 4;
        SpriteLabel label = new SpriteLabel(stage, text, x, y - vertSpace, width, 0, horPos, 5);
        label.addStage(5);
        label.setFont(new Font("Helvetica", Font.BOLD, 18));
        return label;
    }


    private void flashWire(String start, String end) throws AbortedException {
        Node startNode = stage.nodeByName(start);
        Node endNode = stage.nodeByName(end);
        endNode.animateFrom(startNode);
    }


    private int getIRAM(int address) {
        return getSpriteIRAM().getValue(address);
    }


    private void setIRAM(int address, int instruction) {
        getSpriteIRAM().setValue(address, instruction);
    }


    private int getACC() {
        return spriteACC.getValue();
    }


    private void setACC(int value) {
        spriteACC.setValue(value);
    }


    private int getPC() {
        return spritePC.getValue();
    }


    private void setPC(int value) {
        spritePC.setValue(value);
    }


    private void delay(int ms) throws AbortedException {
        if(stage.aborting()) {
            throw new AbortedException();
        }
        try {
            Thread.sleep(ms);
        } catch(InterruptedException _ex) {
            //NO-OP
        }
    }


	public SpriteIRAM getSpriteIRAM() {
		return spriteIRAM;
	}


	public void setSpriteIRAM(SpriteIRAM spriteIRAM) {
		this.spriteIRAM = spriteIRAM;
	}

}
