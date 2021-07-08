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
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Global extends Observable {

    Object value;
    String name;

    public Global(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Global(String name, int value) {
        this(name, Integer.valueOf(value));
    }

    public Object getObject() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setString(String value) {
        setObject(value);
    }

    public void setInt(int value) {
        if(!(this.value instanceof Integer) || value != (Integer) this.value) {
            this.value = value;
            setChanged();
            notifyObservers();
        }
    }

    public void setObject(Object value) {
        if(!value.equals(this.value)) {
            this.value = value;
            setChanged();
            notifyObservers();
        }
    }

    public String getString() {
        if(value instanceof String)
            return (String)value;
        else
            return null;
    }

    public int getInt() {
        if(value instanceof Integer)
            return (Integer) value;
        else
            return 0;
    }

}
