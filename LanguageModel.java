import java.util.HashMap;
import java.util.Random;

public class LanguageModel {


    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {

		String window = "";
        char c;
        In in = new In(fileName);
        for(int i=0;i<this.windowLength;i++){
            window += "" + in.readChar();
        }

        while (!in.isEmpty()) {
            c = in.readChar();
            List list;
            if(CharDataMap.get(window)==null){
                list = new List();
               
            } else{
                list = CharDataMap.get(window);
            }
            list.update(c);
            CharDataMap.put(window, list);
            window = window.substring(1)+c;
        }

        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
 
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {
        CharData[] arrList = probs.toArray();

        int totalCount = 0;
        for(CharData elem : arrList){
            totalCount+=elem.count;
        }
        double prob = 1.0/totalCount;
        ListIterator itr = probs.listIterator(0); 
        itr.current.cp.p=itr.current.cp.count*prob;
        itr.current.cp.cp=itr.current.cp.count*prob;
        while (itr.hasNext()) {
        if(itr.current.next!=null){
           itr.current.next.cp.p=itr.current.next.cp.count*prob;
           itr.current.next.cp.cp=itr.current.cp.cp+itr.current.next.cp.p;
           }
           itr.next();
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
        // double rand = Math.random();
        double rand = randomGenerator.nextDouble();
        ListIterator itr = probs.listIterator(0);
        while (itr.hasNext()) {
            if(rand<itr.current.cp.cp) return itr.current.cp.chr;
            if(itr.current.next!=null){
                itr.next();
            }
        }
        return itr.current.cp.chr;
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {

        StringBuilder str = new StringBuilder();
        if(initialText.length()<this.windowLength) return initialText;
        str.append(initialText);
        int startCount = initialText.length()- windowLength;
        String window = initialText.substring(startCount); 

        for(int i=0;i<textLength;i++){
            List list = CharDataMap.get(window);
            if(list==null) return str.toString();       
            char c = getRandomChar(list);
            str.append(c);
            window = window.substring(1)+c;
        }        
        return str.toString();
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {

        int windowLength = Integer.parseInt(args[0]);
       String initialText = args[1];
       int generatedTextLength = Integer.parseInt(args[2]);
       Boolean randomGeneration = args[3].equals("random");
       String fileName = args[4];
       // Create the LanguageModel object
       LanguageModel lm;
       if (randomGeneration)
           lm = new LanguageModel(windowLength);
       else
           lm = new LanguageModel(windowLength, 20);
       // Trains the model, creating the map.
       lm.train(fileName);
       // Generates text, and prints it.
       System.out.println(lm.generate(initialText, generatedTextLength));
    }
}
