package application.containers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Třída, představující textový dokument, pro který jsou počítány četnosti
 * obsažených slov za účelem trénování klasifikátoru pro pozdější klasifikaci
 * dokumentů.
 *
 * @author Petr Kozler
 */
public class TextDocument implements Serializable {

    /**
     * název dokumentu
     */
    public final String NAME;

    /**
     * celkový počet slov
     */
    private int totalWordCount;
    
    /**
     * četnosti jednotlivých slov
     */
    private Map<String, MutableInt> wordCounts;

    /**
     * Vytvoří objekt představující textový dokument s nespecifikovaným názvem
     * (určeno k použití při manuálním zadávání textu ke klasifikaci).
     */
    public TextDocument() {
        this(null);
    }

    /**
     * Vytvoří objekt představující textový dokument se zadaným názvem (určeno k
     * použití při trénování a testování klasifikátoru).
     *
     * @param name název dokumentu
     */
    public TextDocument(String name) {
        NAME = name;
    }

    /**
     * Vrátí celkový počet slov.
     * 
     * @return celkový počet slov
     */
    public int getTotalWordCount() {
        return totalWordCount;
    }

    /**
     * Vrátí četnosti jednotlivých slov.
     * 
     * @return četnosti jednotlivých slov
     */
    public Map<String, MutableInt> getWordCounts() {
        return wordCounts;
    }

    /**
     * Nastaví zjištěné četnosti jednotlivých slov a určí celkový počet slov.
     * 
     * @param wordCounts četnosti jednotlivých slov
     */
    public void setWordCounts(Map<String, MutableInt> wordCounts) {
        this.wordCounts = wordCounts == null ? new HashMap<>() : wordCounts;
        totalWordCount = 0;
        
        for (MutableInt i : this.wordCounts.values()) {
            if (i != null) {
                totalWordCount += i.get();
            }
        }
    }

    /**
     * Vrátí textovou reprezentaci zjištěných četností pro jednotlivá slova.
     *
     * @return textová reprezentace četností slov
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(NAME);
        sb.append('\n');

        wordCounts.entrySet().stream().forEach((wordCount) -> {
            sb.append(wordCount.getKey()).append(": ")
                    .append(wordCount.getValue()).append('\n');
        });

        return sb.toString();
    }

}
