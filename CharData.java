/** Represents a character data object. 
 *  A character data object has a char value,
 *  a counter, and two probability fields. */
public class CharData {

	// a character
	public char chr;

	// a counter
	public int count; 

	// a probability (number between 0 and 1)
	public double p;    

	// a commulative probability (number between 0 and 1)
	public double cp;

	/** Creates and initializes a character data object. */
	public CharData(char chr) {
		this.chr = chr;
		this.count = 1;
		this.p = 0;
		this.cp = 0;
	}

	/** Checks if the character of this CharData object equals the given character. */
	public boolean equals(char chr) {
		return this.chr == chr;
	}
	
	/** Returns a textual representation of this CharData object. */
	public String toString() {
		return "(" + chr + " " + count + " " + p + " " + cp + ")";
	}
}