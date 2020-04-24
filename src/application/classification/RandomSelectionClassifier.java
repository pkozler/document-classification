package application.classification;

import application.containers.TextDocument;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Třída, představující "klasifikátor" pro triviální určování třídy textových
 * dokumentů náhodným výběrem - slouží pro účely porovnání a vyhodnocení
 * úspěšnosti klasifikace u ostatních klasifikátorů.
 * 
 * @author Petr Kozler
 */
public class RandomSelectionClassifier extends AClassifier {

    /**
     * generátor náhodných čísel
     */
    protected Random random;
    
    /**
     * Vytvoří generátor náhodných čísel pro náhodný výběr tříd dokumentů.
     * 
     * @param documents seznamy dokumentů a příslušných tříd
     */
    public void train(Map<String, List<TextDocument>> documents) {
        random = new Random(documents.hashCode());
    }

    /**
     * Klasifikuje textový dokument metodou náhodného výběru.
     * 
     * @param document dokument ke klasifikaci
     * @return třída dokumentu
     */
    @Override
    public String classify(TextDocument document) {
        return classes.get(random.nextInt(classes.size()));
    }
    
}
