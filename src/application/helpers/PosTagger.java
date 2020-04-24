package application.helpers;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Třída, sloužící jako PoS (part of speech) Tagger, který označuje
 * nevýznamná, tzv. stop-slova (předložky, spojky), která nemají
 * pozitivní vliv na spolehlivost klasifikátoru.
 * 
 * @author Petr Kozler
 */
public class PosTagger implements Serializable {
    
    /**
     * seznam stop-slov
     */
    private final Set<String> STOP_WORDS = new HashSet<>();
    
    /**
     * Vytvoří objekt pro odstraňování stop-slov.
     * 
     * @param stopWordLines seznam stop-slov
     * @throws IOException 
     */
    public PosTagger(List<String> stopWordLines) throws IOException {
        stopWordLines.stream().forEach((word) -> {
            STOP_WORDS.add(word.trim().toLowerCase());
        });
    }
    
    /**
     * Určí, zda zadané slovo patří mezi stop-slova.
     * 
     * @param word slovo
     * @return true, pokud je zadané slovo stop-slovem, jinak false
     */
    public boolean isStopWord(String word) {
        return STOP_WORDS.contains(word);
    }
    
}
