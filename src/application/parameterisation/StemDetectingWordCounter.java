package application.parameterisation;

import application.helpers.PosTagger;
import application.helpers.Stemmer;

/**
 * Třída, představující čítač slov, který v průběhu počítání využívá jednak
 * PoS tagger pro detekci nevýznamových slov, a jednak tzv. stemmer, umožňující
 * redukovat slova na jejich základy (stemy) odstraněním pro klasifikaci
 * nevýznamných částí (jako jsou předpony nebo přípony, popř. koncovky),
 * přičemž výskyt rozdílných slov majících stejný základ je vyhodnocován
 * jako vícenásobný výskyt jednoho a téhož slova.
 * 
 * @author Petr Kozler
 */
public class StemDetectingWordCounter extends AWordCounter {
    
    /**
     * objekt pro označování stop slov
     */
    protected PosTagger posTagger;
    
    /**
     * objekt pro nalezení kořenů slov
     */
    protected Stemmer stemmer;

    /**
     * Vrátí upravený tvar slova pro zařazení do mapy slov a četností.
     * Úprava zahrnuje odstraňování stop-slov nalezení stemu (základu) slova.
     * 
     * @param word slovo
     * @return upravený tvar slova nebo null, pokud bylo slovo odstraněno jako nevýznamné
     */
    protected String getRelevantWord(String word) {
        // vynechání stop-slov (předložky, spojky atd.)
        if (posTagger.isStopWord(word)) {
            return null;
        }

        // nalezení kořene slova
        word = stemmer.getWordStem(word);
        
        if (word.isEmpty()) {
            return null;
        }

        return word;
    }
    
    /**
     * Vytvoří čítač slov s PoS taggingem a stemmingem.
     * 
     * @param posTagger objekt pro detekci stop-slov
     * @param stemmer objekt pro hledání stemů slov
     * @return čítač slov
     */
    public static AWordCounter CreateWordCounter(PosTagger posTagger, Stemmer stemmer) {
        StemDetectingWordCounter wordCounter = new StemDetectingWordCounter();
        wordCounter.posTagger = posTagger;
        wordCounter.stemmer = stemmer;
        
        return wordCounter;
    }
    
}
