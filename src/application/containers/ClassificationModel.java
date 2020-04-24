package application.containers;

import application.classification.AClassifier;
import application.parameterisation.AWordCounter;
import java.io.Serializable;

/**
 * Třída, představující klasifikační model.
 * Obsahuje instance třídy implementující vybraný algoritmus parametrizace
 * dokumentů a třídy představující vybraný klasifikátor dokumentů.
 * 
 * @author Petr Kozler
 */
public class ClassificationModel implements Serializable {
    
    /**
     * čítač slov v dokumentech
     */
    private AWordCounter wordCounter;
    
    /**
     * klasifikátor dokumentů
     */
    private AClassifier classifier;
    
    /**
     * Nastaví komponenty klasifikačního modelu.
     * 
     * @param wordCounter čítač slov v dokumentech
     * @param classifier klasifikátor dokumentů
     */
    public void setModelComponents(AWordCounter wordCounter, AClassifier classifier) {
        this.wordCounter = wordCounter;
        this.classifier = classifier;
    }

    /**
     * Vrátí čítač slov v dokumentech.
     * 
     * @return čítač slov v dokumentech
     */
    public AWordCounter getWordCounter() {
        return wordCounter;
    }

    /**
     * Vrátí klasifikátor dokumentů.
     * 
     * @return klasifikátor dokumentů
     */
    public AClassifier getClassifier() {
        return classifier;
    }
    
}
