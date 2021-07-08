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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import pippin.stage.NEWCPUSim;
import pippin.util.serialization.RAMContent;
import pippin.util.CPUTools;
import pippin.stage.Stage;

public class SpriteIRAM extends SpriteRAM {

    public static final int WIDTH = 295;
    public static final int HEIGHT = 258;
    public static final int FIRST_ADDRESS = 0;
    public static final int LAST_ADDRESS = 126;
    public static final int WORD_LENGTH = 2;

    private static final Color OLD_ADDRESSBOX_ORRIBLE_PINK = new Color(255, 204, 204); //commemorative variable

    private int scroll;
    private boolean isEditingLabel; // used outside the class to understand if the edited TextBox is a datBox or a labelBox

    private final PropertyChangeSupport contentChangeSupport;
    private final Scrollbar scrollBar;
    private final boolean[] showAsVariable; // determines if the value inside a cell should be displayed as a number or as an instruction
    private final TextBox[] labelBoxes;
    private final Map<String, Integer> labelsMapping;



    public SpriteIRAM(Stage stage, int x, int y, Scrollbar scrollBar) {
        super(stage, "IRAM", x, y, WIDTH, HEIGHT);
        contentChangeSupport = new PropertyChangeSupport(this);
        this.scrollBar = scrollBar;
        super.data = new int[64];
        super.addBoxes = new TextBox[64];
        super.datBoxes = new TextBox[64];
        labelBoxes = new TextBox[64];
        showAsVariable = new boolean[64];
        labelsMapping = new HashMap<>();
        isEditingLabel = false;

        addNode("DAT", WIDTH - (int)((float)WIDTH * 0.3333333F), 0);
        addNode("ADD", -5, 50);

        for (int i = 0; i < 64; i++) {
            super.addBoxes[i] = new TextBox(stage, 0, 0, 1, 1, Integer.toString(i * 2, 10), CPUTools.intToBinString(i * 2, 8), super.font, true, true, Color.orange);
            super.datBoxes[i] = new TextBox(stage, 0, 0, 1, 1, "", "", super.font, true, false);
            labelBoxes[i] = new TextBox(stage, 0, 0, 1, 1, "", "", super.font, true, false, Color.lightGray);
            labelBoxes[i].setBorderColor(Color.gray);
            labelBoxes[i].setTextColors(Color.red, Color.gray);
            setValue(indexToAddress(i), "NOP");
        }

        updateScroll();
    }



    @Override
    public String getStringValue(int address) {
        int index = addressToIndex(address);
        if (super.stage.getMode() == NEWCPUSim.SYMBOLIC_MODE) {
            if (showAsVariable[index]) {
                return datBoxes[index].symText;
            } else {
                return datBoxes[index].symText + "\t" + datBoxes[index].symTabText;
            }
        } else {
            return datBoxes[index].binText;
        }
    }


    @Override
    public String checkValue(int address, String value) {
        value = value.replaceAll("\\s+", "\t");
        if (super.stage.getMode() ==  NEWCPUSim.SYMBOLIC_MODE) {
            if (value.matches("-?\\d+")) { //if è una variabile
                int dataValue = Integer.parseInt(value);
                if (dataValue > 127 || dataValue < -128) {
                    return "Data exceed 8-bit range (127/-128)";
                }
            } else {
                String operand = CPUTools.secondPart(value);
                if (labelsMapping.containsKey(operand)) {
                    value = CPUTools.firstPart(value) + "\t" + labelsMapping.get(operand);
                }
                CPUTools.instructionToInt16(value);
            }
        } else {
            String stripValue = stripWhite(value);
            int iValue;
            try {
                iValue = Integer.valueOf(stripValue, 2);
            } catch (NumberFormatException _ex) {
                return "not a legal binary number";
            }
            CPUTools.int16ToInstruction(iValue);
        }
        return CPUTools.error();
    }


    @Override
    public String checkValue(int address, int value) {
        String error = CPUTools.int16ToInstruction(value);
        if (error != null) {
            return null;
        } else {
            return CPUTools.error();
        }
    }


    @Override
    public void setValue(int address, String value) {
        if (super.stage.getMode() ==  NEWCPUSim.SYMBOLIC_MODE) {
            if (value.matches("-?\\d{1,3}")) { // if it's a variable
                showAsVariable[addressToIndex(address)] = true;
                setValue(address, CPUTools.joinBits(Integer.parseInt(value), 0b00000000));
            } else {
                getShowAsVariable()[addressToIndex(address)] = false;
                if (labelsMapping.containsKey(CPUTools.secondPart(value))) {
                    int index = addressToIndex(address);
                    String valueWithAddress = CPUTools.firstPart(value) + "\t" + labelsMapping.get(CPUTools.secondPart(value));
                    int int16Value = CPUTools.instructionToInt16(valueWithAddress);
                    String binString = int16ToBin(int16Value);
                    super.data[index] = int16Value;
                    super.datBoxes[index].setText(value, binString);
                    markDirty();
                } else {
                    setValue(address, CPUTools.instructionToInt16(value));
                }
            }
        } else {
            value = stripWhite(value);
            int iValue;
            try {
                iValue = Integer.valueOf(value, 2);
            } catch (NumberFormatException _ex) {
                return;
            }
            setValue(address, iValue);
        }
        contentChangeSupport.firePropertyChange("content", null, getContent());
    }


    @Override
    public void setValue(int address, int value) {
        String symString;
        String binString;
        int index = addressToIndex(address);
        super.data[index] = value;
        if (getShowAsVariable()[index]) {
            symString = Integer.toString(CPUTools.top8Bits(value));
        } else {
            symString = CPUTools.int16ToInstruction(value);
        }
        binString = int16ToBin(super.data[index]);
        assert symString != null;
        super.datBoxes[index].setText(symString, binString);
        markDirty();
    }

    @Override
    public int nextAddress(int address) {
        if (address < LAST_ADDRESS) {
            return address + 2;
        } else {
            return FIRST_ADDRESS;
        }
    }


    @Override
    public int prevAddress(int address) {
        if (address > FIRST_ADDRESS) {
            return address - 2;
        } else {
            return LAST_ADDRESS;
        }
    }


    @Override
    public void flashAddress(int address) {
        showAddress(address);
        super.flashAddress(address);
    }


    @Override
    public void flashData(int address) {
        showAddress(address);
        super.flashData(address);
    }


    public void updateScroll() {
        int addressWidth = 66;
        int dataWidth = 132;
        int labelWidth = 95;
        int labelAddBorder = super.getX() + labelWidth;
        int addDatBorder = labelAddBorder + addressWidth;
        int top = super.getY();
        int left = super.getX();

        for (int i = 0; i < 64; i++) {
            if (i >= scroll && i < scroll + 16) {
                int bottom = super.getY() + (((i + 1) - scroll) * super.getHeight()) / 16;
                labelBoxes[i].reshape(left - 10, top, labelWidth -10, 17);
                super.addBoxes[i].reshape(labelAddBorder, top, addressWidth, 17);
                super.datBoxes[i].reshape(addDatBorder, top, dataWidth, 17);
                top = bottom - 1;
            } else {
                labelBoxes[i].reshape(-10, -10, 5, 5);
                super.addBoxes[i].reshape(-10, -10, 5, 5);
                super.datBoxes[i].reshape(-10, -10, 5, 5);
            }
        }

        toScroll();
        markDirty();
    }


    @Override
    public boolean showAddress(int address) {
        int index = addressToIndex(address);
        if (index < scroll) {
            scroll = index;
            updateScroll();
            return true;
        }
        if (index >= scroll + 16) {
            scroll = (index - 16) + 1;
            updateScroll();
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int clickToAddress(int x, int y) {
        for (int i = 0; i < 64; i++) {
            if (datBoxes[i].isInside(x, y) || labelBoxes[i].isInside(x, y)) {
                return indexToAddress(i);
            }
        }
        return -1;
    }


    public void setIsEditingLabel(int x, int y) {
        for (int i = 0; i < 64; i++) {
            if (labelBoxes[i].isInside(x,y)) {
                isEditingLabel = true;
                return;
            }
        }
        isEditingLabel = false;
    }


    public String checkLabel(int address, String value) {
        if (value.length() > 9) {
            return "invalid label length (max 9 characters)";
        }
        if (!value.matches("(^[A-Z]{0,9}$)")) {
            return "this label can't be used";
        }
        if (labelsMapping.containsKey(value) && labelsMapping.get(value) != address) {
            return "this label is already in use";
        }
        return null;
    }


    public Rectangle addressToLabelRect(int address) {
        int index = addressToIndex(address);
        return labelBoxes[index].getRect();
    }


    public String getLabelStringValue(int address) {
        int index = addressToIndex(address);
        return labelBoxes[index].symText;
    }


    public void setLabel(int address, String newValue, boolean updateInstructions) {
        int index = addressToIndex(address);
        String oldValue = labelBoxes[index].symText;
        if (newValue.isEmpty()) {
            labelsMapping.remove(oldValue);
        } else {
            if (!oldValue.isEmpty()) {
                labelsMapping.remove(oldValue);
            }
            labelsMapping.put(newValue, address);
        }
        labelBoxes[index].setText(newValue, newValue);

        if (updateInstructions) {
            String newOperand = newValue.isEmpty() ? Integer.toString(address) : newValue;
            if (!oldValue.isEmpty()) {
                Stream.of(datBoxes).filter(box -> box.symTabText.contains(oldValue))
                        .forEach(box -> box.symTabText = box.symTabText.replaceAll(oldValue, newOperand));
            }
        }
        markDirty();
        contentChangeSupport.firePropertyChange("content", null, getContent());
    }


    public void fromScroll() {
        if (scrollBar != null) {
            int newScroll = scrollBar.getValue();
            if(newScroll != scroll) {
                scroll = newScroll;
                updateScroll();
            }
        }
    }


    public void toScroll() {
        if (scrollBar != null) {
            scrollBar.setValues(scroll, 16, 0, 56);
        }
    }


    // called when the shortcut ctrl+s is entered while editing an instruction inside the ram
    public void shiftDownFromAddress(int addressFrom) {
        if (addressFrom < FIRST_ADDRESS || addressFrom > LAST_ADDRESS) {
            return;
        }
        List<String> shiftedLabels = new LinkedList<>();
        for (int i = LAST_ADDRESS-2; i >= addressFrom; i-=WORD_LENGTH) {
            int index = addressToIndex(i);
            getShowAsVariable()[index+1] = getShowAsVariable()[index];
            data[index+1] = data[index];
            datBoxes[index+1].setText(datBoxes[index].symText + "\t" + datBoxes[index].symTabText, datBoxes[index].binText);
            String tmpLabel = labelBoxes[index].symText;
            if (!tmpLabel.equals("")) {
                shiftedLabels.add(tmpLabel);
            }
            setLabel(i, "", false);
            setLabel(i+2, tmpLabel, false);
        }
        if (super.stage.getMode() ==  NEWCPUSim.SYMBOLIC_MODE) {
            setValue(addressFrom, "NOP");
        } else {
            setValue(addressFrom, "00001110 00000000");
        }
        setLabel(addressFrom, "", false);
        // update the instructions with a label that was shifted as operand
        if (shiftedLabels.size() > 0) {
            for (int i = 0; i < 64; i++) {
                if (datBoxes[i].symTabText.matches("^#?\\d{1,3}$")) {
                    continue;
                }
                String stringInstruction = datBoxes[i].symText + "\t" + datBoxes[i].symTabText;
                for (String label : shiftedLabels) {
                    if (datBoxes[i].symTabText.matches(label)) {
                        setValue(indexToAddress(i), stringInstruction);
                        break;
                    }
                }
            }
        }
    }


    public void shiftUpFromAddress(int addressFrom) {
        if (addressFrom < FIRST_ADDRESS || addressFrom >= LAST_ADDRESS) {
            return;
        }
        List<String> shiftedLabels = new LinkedList<>();
        String deletedLabel = (!labelBoxes[addressToIndex(addressFrom)].symText.equals("")) ? labelBoxes[addressToIndex(addressFrom)].symText : null;
        for (int i = addressFrom+2; i <= LAST_ADDRESS; i += WORD_LENGTH) {
            int index = addressToIndex(i);
            showAsVariable[index-1] = showAsVariable[index];
            data[index-1] = data[index];
            datBoxes[index-1].setText(datBoxes[index].symText + "\t" + datBoxes[index].symTabText, datBoxes[index].binText);
            String tmpLabel = labelBoxes[index].symText;
            if (!tmpLabel.equals("") && !tmpLabel.equals(deletedLabel)) {
                shiftedLabels.add(tmpLabel);
            }
            setLabel(i, "", false);
            setLabel(i-2, tmpLabel, false);
        }
        setValue(LAST_ADDRESS, "NOP");
        // update the instructions with a label that was shifted as operand
        if (shiftedLabels.size() > 0) {
            for (int i = 0; i < 64; i++) {
                if (datBoxes[i].symTabText.matches("^#?\\d{1,3}$")) {
                    continue;
                }
                if (datBoxes[i].symTabText.equals(deletedLabel)) {
                    setValue(indexToAddress(i), datBoxes[i].symText + "\t" + addressFrom);
                    continue;
                }
                String stringInstruction = datBoxes[i].symText + "\t" + datBoxes[i].symTabText;
                for (String label : shiftedLabels) {
                    if (datBoxes[i].symTabText.matches(label)) {
                        setValue(indexToAddress(i), stringInstruction);
                        break;
                    }
                }
            }
        }
    }


    public void setContent(RAMContent content) {
        if (content == null) {
            return;
        }
        for (int address : labelsMapping.values().toArray(new Integer[0])) {
            setLabel(address, "", false);
        }
        content.getLabelsMappings().entrySet().forEach(entry -> setLabel(entry.getValue(), entry.getKey(), false));
        String[] instructions = content.getInstructions();
        for (int i = 0; i < instructions.length; i++) {
            setValue(indexToAddress(i), instructions[i]);
        }
        markDirty();
    }


    public RAMContent getContent() {
        try {
            String[] instructions = new String[datBoxes.length];
            for (int i = 0; i < instructions.length; i++) {
                if (showAsVariable[i]) {
                    instructions[i] = datBoxes[i].symText;
                } else {
                    instructions[i] = datBoxes[i].symText + "\t" + datBoxes[i].symTabText;
                }
            }
            return new RAMContent(labelsMapping, instructions);
        } catch (NullPointerException e) {
            return new RAMContent(new HashMap<>(), new String[0]); // this is for when the ram is getting initialized and some elements are still null
        }
    }


    @Override
    public boolean drawAll(Graphics g) {
        markClean();
        for (int i = scroll; i < scroll + 16; i++) {
            super.dirty |= labelBoxes[i].drawAll(g);
            super.dirty |= super.addBoxes[i].drawAll(g);
            super.dirty |= super.datBoxes[i].drawAll(g);
        }

        if (super.dirtyPartner != null) {
            super.dirtyPartner.markDirty();
        }
        return super.dirty;
    }


    @Override
	public int addressToIndex(int address) {
        return address / 2;
    }


    @Override
    public int indexToAddress(int index) {
        return index * 2;
    }


    private String int16ToBin(int value) {
        int top8 = CPUTools.top8Bits(value);
        int bottom8 = CPUTools.bottom8Bits(value);
        return CPUTools.sEx8ToBinString(top8) + "  " + CPUTools.intToBinString(bottom8, 8);
    }

    public void addContentChangeListener(PropertyChangeListener listener) {
        contentChangeSupport.addPropertyChangeListener(listener);
    }


    // removes all white spaces from a string
    private String stripWhite(String value) {
        return value.replaceAll("\\s+|\\t", "");
    }


	public boolean isEditingLabel() {
		return isEditingLabel;
	}


	public void setEditingLabel(boolean isEditingLabel) {
		this.isEditingLabel = isEditingLabel;
	}


	public boolean[] getShowAsVariable() {
		return showAsVariable;
	}



}
