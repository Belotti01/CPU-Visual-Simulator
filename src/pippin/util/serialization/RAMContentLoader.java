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
package pippin.util.serialization;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.io.*;


public class RAMContentLoader {

    private static final String FILE_EXTENSION = ".pippin";
    private static final String DEFAULT_FILE_NAME = "MyProgram";

    private static final FileFilter pippinFileFilter = new FileFilter() {
        public String getDescription() {
            return "PIPPIN Program (*" + FILE_EXTENSION + ")";
        }
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(FILE_EXTENSION);
        }
    };

    private final JFrame owner;

    private RAMContent lastSave;
    private File lastSaveFile;



    public RAMContentLoader(JFrame owner){
        lastSave = null;
        lastSaveFile = null;
        this.owner = owner;
    }



    public boolean canSave(RAMContent content) {
        return lastSave != null && lastSaveFile != null && !lastSave.equals(content);
    }


    public void save(RAMContent content) {
        if (content == null) {
            throw new IllegalArgumentException("null ram content");
        }
        if (!canSave(content)) {
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(lastSaveFile))) {
            out.writeObject(content);
            lastSave = content;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong while saving your program.", "Ops!", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void saveAs(RAMContent content) {
        if (content == null) {
            throw new IllegalArgumentException("null ram content");
        }
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(pippinFileFilter);
        fc.setFileFilter(pippinFileFilter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Save your program as:");
        if (canSave(content)) {
            fc.setCurrentDirectory(lastSaveFile.getParentFile());
            fc.setSelectedFile(lastSaveFile);
        } else {
            fc.setSelectedFile(new File(fc.getCurrentDirectory(), DEFAULT_FILE_NAME));
        }

        int returnStatus = fc.showSaveDialog(owner);
        File returnFile = fc.getSelectedFile();

        if (returnFile == null || returnStatus != JFileChooser.APPROVE_OPTION) {
            return;
        }
        if (returnFile.getPath().endsWith(FILE_EXTENSION)) {
            returnFile = new File(returnFile.getPath().replaceAll("(\\.pippin)+$", FILE_EXTENSION));
        } else {
            returnFile = new File(returnFile.getPath() + FILE_EXTENSION);
        }

        lastSaveFile = returnFile;
        lastSave = content;

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(lastSaveFile))) {
            out.writeObject(content);
            lastSave = content;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong while saving your program.", "Ops!", JOptionPane.ERROR_MESSAGE);
        }
    }


    public RAMContent load() {
        RAMContent content = null;
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(pippinFileFilter);
        fc.setFileFilter(pippinFileFilter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Select your program");

        int returnStatus = fc.showOpenDialog(owner);

        if (returnStatus == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null) {
            lastSaveFile = fc.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(lastSaveFile))) {
                content = (RAMContent) in.readObject();
                lastSave = content;
            } catch (IOException | ClassNotFoundException e) {
                lastSaveFile = null;
                lastSave = null;
                JOptionPane.showMessageDialog(null, "Something went wrong while reading your file", "Ops!", JOptionPane.ERROR_MESSAGE);
            }
        }
        return content;
    }

}
