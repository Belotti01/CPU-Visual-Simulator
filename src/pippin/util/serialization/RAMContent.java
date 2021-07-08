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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RAMContent implements Serializable {

	private final Map<String, Integer> labelsMappings;
	private final String[] instructions;   



	public RAMContent(Map<String, Integer> labelsMappings, String[] instructions) {
		this.labelsMappings = new HashMap<>(labelsMappings);
		this.instructions = Arrays.copyOf(instructions, instructions.length);
	}


	public Map<String, Integer> getLabelsMappings() {
		return labelsMappings;
	}

	public String[] getInstructions() {
		return instructions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RAMContent that = (RAMContent) o;
		return labelsMappings.equals(that.labelsMappings) && Arrays.equals(instructions, that.instructions);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(labelsMappings);
		result = 31 * result + Arrays.hashCode(instructions);
		return result;
	}
}
