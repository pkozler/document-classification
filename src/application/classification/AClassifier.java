package application.classification;

import application.containers.TextDocument;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstraktní třída, představující obecnou komponentu klasifikačního modelu,
 * implementující algoritmus pro automatickou klasifikaci textových dokumentů
 * na základě informací získaných při procesu trénování. Jedná se tedy
 * o klasifikátor využívající metodu učení s učitelem.
 * 
 * @author Petr Kozler
 */
public abstract class AClassifier implements Serializable {
    
    /**
     * seznam všech nalezených tříd, do kterých patří některé z trénovacích dokumentů
     */
    protected List<String> classes;
    
    /**
     * seznam všech nalezených slov ve všech trénovacích dokumentech
     */
    protected List<String> vocabulary;
    
    /**
     * Inicializuje seznamy tříd a slov pro klasifikační model.
     * 
     * @param classes seznam všech nalezených tříd
     * @param vocabulary seznam všech nalezených slov
     */
    public void setLists(List<String> classes, List<String> vocabulary) {
        this.classes = new ArrayList(classes);
        this.vocabulary = new ArrayList(vocabulary);
    }
    
    /**
     * Spustí trénování klasifikačního modelu daty z dokumentů z trénovací množiny.
     * 
     * @param documents seznamy dokumentů a příslušných tříd
     */
    public abstract void train(Map<String, List<TextDocument>> documents);
    
    /**
     * Klasifikuje dokument se zadanými daty.
     * 
     * @param document dokument ke klasifikaci
     * @return třída dokumentu
     */
    public abstract String classify(TextDocument document);
    
}
