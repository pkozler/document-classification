 package application.parameterisation;

import application.Config;
import application.containers.MutableInt;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstraktní třída, představující obecnou komponentu klasifikačního modelu,
 * implementující algoritmus pro vytváření reprezentace obsahu dokumentů vhodnou
 * pro použití při klasifikaci. Společným základem těchto algoritmů je počítání
 * výskytů slov v textu, proto tato třída již obsahuje některé základní funkce,
 * které jsou využívány všemi potomky společně.
 * 
 * @author Petr Kozler
 */
public abstract class AWordCounter implements Serializable {
    
    /**
     * Spočítá četnosti slov v textu dokumentu.
     * 
     * @param text text dokumentu
     * @return mapa slov nalezených v dokumentu a jejich četností
     */
    public Map<String, MutableInt> countWords(String text) {
        Map<String, MutableInt> wordCounts = new HashMap<>();
        String[] words = splitToWords(text);
        
        for (String word : words) {
            word = word.toLowerCase();

            if (word.isEmpty()) {
                continue;
            }

            word = getRelevantWord(word);
            
            if (word == null) {
                continue;
            }
            
            MutableInt count = wordCounts.get(word);

            if (count == null) {
                wordCounts.put(word, new MutableInt(1));
            }
            else {
                count.inc();
            }
        }
        
        return wordCounts;
    }
    
    /**
     * Rozdělí textu dokumentu na slova.
     * 
     * @param text text dokumentu
     * @return pole slov
     */
    protected String[] splitToWords(String text) {
        return text.split(Config.WORD_NOT_ALPHA_REGEX);
    }
    
    /**
     * Vrátí upravený tvar slova pro zařazení do mapy slov a četností
     * v závislosti na zvoleném algoritmu vytváření příznaků dokumentu. 
     * 
     * @param word slovo
     * @return upravený tvar slova nebo null, pokud bylo slovo odstraněno jako nevýznamné
     */
    protected abstract String getRelevantWord(String word);
}
