package application.parameterisation;

import application.helpers.PosTagger;

/**
 * Třída, představující čítač slov, který v průběhu počítání využívá tzv.
 * PoS tagger za účelem rozlišení slov v textu na slova významová (nesoucí
 * užitečnou informaci z hlediska klasifikace) a slova nevýznamová (stop-slova),
 * jejichž výskyty jsou ignorovány a nejsou tak součástí popisů dokumentů.
 *
 * @author Petr Kozler
 */
public class PosTaggingWordCounter extends AWordCounter {
    
    /**
     * objekt pro označování stop slov
     */
    protected PosTagger posTagger;

    /**
     * Vrátí upravený tvar slova pro zařazení do mapy slov a četností.
     * Úprava zahrnuje odstraňování stop-slov.
     * 
     * @param word slovo
     * @return upravený tvar slova nebo null, pokud bylo slovo odstraněno jako nevýznamné
     */
    protected String getRelevantWord(String word) {
        // vynechání stop-slov (předložky, spojky atd.)
        if (posTagger.isStopWord(word)) {
            return null;
        }
        
        return word;
    }
    
    /**
     * Vytvoří čítač slov s PoS taggingem pro detekci stop-slov.
     * 
     * @param posTagger objekt pro detekci stop-slov
     * @return čítač slov
     */
    public static AWordCounter CreateWordCounter(PosTagger posTagger) {
        PosTaggingWordCounter wordCounter = new PosTaggingWordCounter();
        wordCounter.posTagger = posTagger;
        
        return wordCounter;
    }
    
}
