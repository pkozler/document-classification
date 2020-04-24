package application.parameterisation;

/**
 * Třída, představující jednoduchý čítač slov, nerozlišující významovost slov
 * ani jejich různé tvary.
 *
 * @author Petr Kozler
 */
public class SimpleWordCounter extends AWordCounter {
    
    /**
     * Vrátí upravený tvar slova pro zařazení do mapy slov a četností.
     * 
     * @param word slovo
     * @return upravený tvar slova nebo null, pokud bylo slovo odstraněno jako nevýznamné
     */
    protected String getRelevantWord(String word) {
        return word;
    }
    
    /**
     * Vytvoří jednoduchý čítač slov.
     * 
     * @return čítač slov
     */
    public static AWordCounter CreateWordCounter() {
        return new SimpleWordCounter();
    }
    
}
