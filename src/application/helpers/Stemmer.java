package application.helpers;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Třída, sloužící ke stemmingu, tj. odstraňování předpon, přípon a koncovek
 * za účelem nalezení kořene slova (stemu) při počítání četností slov
 * v textových dokumentech, což obvykle zvyšuje spolehlivost klasifikátoru.
 * 
 * @author Petr Kozler
 */
public class Stemmer implements Serializable {
    
    /**
     * seznam předpon
     */
    private final Set<String> WORD_PREFIXES;
    
    /**
     * seznam přípon
     */
    private final Set<String> WORD_SUFIXES;
    
    /**
     * seznam koncovek
     */
    private final Set<String> WORD_ENDINGS;
    
    /**
     * Vytvoří objekt pro stemming.
     * 
     * @param wordPrefixLines
     * @param wordSufixLines
     * @param wordEndingLines
     * @throws IOException 
     */
    public Stemmer(
            List<String> wordPrefixLines, List<String> wordSufixLines, List<String> wordEndingLines)
            throws IOException {
        WORD_PREFIXES = new HashSet<>(wordPrefixLines);
        WORD_SUFIXES = new HashSet<>(wordSufixLines);
        WORD_ENDINGS = new HashSet<>(wordEndingLines);
    }
    
    /**
     * Vrátí kořen slova.
     * 
     * @param word slovo
     * @return kořen slova
     */
    public String getWordStem(String word) {
        // odstranění koncovky
        for (String ending : WORD_ENDINGS) {
            if (word.endsWith(ending)) {
                word = word.substring(0, word.length() - ending.length());
                
                break;
            }
        }
        
        // odstranění přípony
        for (String sufix : WORD_SUFIXES) {
            if (word.endsWith(sufix)) {
                word = word.substring(0, word.length() - sufix.length());
                
                break;
            }
        }
        
        // odstranění koncovky
        for (String prefix : WORD_PREFIXES) {
            if (word.startsWith(prefix)) {
                word = word.substring(prefix.length() - 1);
                
                break;
            }
        }
        
        return word;
    }
    
}
