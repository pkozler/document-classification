package application.helpers;

import application.classification.AClassifier;
import application.classification.NaiveBayesClassifier;
import application.classification.NearestNeighborClassifier;
import application.classification.RandomSelectionClassifier;
import application.parameterisation.AWordCounter;
import application.parameterisation.PosTaggingWordCounter;
import application.parameterisation.SimpleWordCounter;
import application.parameterisation.StemDetectingWordCounter;
import java.io.IOException;
import java.util.List;

/**
 * Třída, sloužící ke zpracování argumentů příkazové řádky.
 * 
 * @author Petr Kozler
 */
public class CmdArg {
    
    /**
     * cesty k dokumentům z trénovací množiny
     */
    private List<String> trainingSetPath;
    
    /**
     * cesty k dokumentům z testovací množiny
     */
    private List<String> testSetPath;
    
    /**
     * objekt pro vytvoření příznaků
     */
    private AWordCounter wordCounter;
    
    /**
     * objekt pro klasifikaci dokumentů
     */
    private AClassifier classifier;
    
    /**
     * cesta k souboru pro uložení klasifikačního modelu
     */
    private final String classificationModelPath;
    
    /**
     * Vytvoří objekt pro zpracování argumentů příkazové řádky
     * při spuštění programu v režimu vytváření klasifikačního modelu.
     * 
     * @param trainingSet cesta k adresáři s dokumenty z trénovací množiny
     * @param testSet cesta k adresáři s dokumenty z testovací množiny
     * @param paramAlgorithmIdStr číslo algoritmu pro vytvoření příznaků
     * @param recognizeAlgorithmIdStr číslo algoritmu pro klasifikaci dokumentů
     * @param classifyModelName cesta k souboru pro uložení klasifikačního modelu
     * @throws IOException
     */
    public CmdArg(String trainingSet, String testSet, String
            paramAlgorithmIdStr, String recognizeAlgorithmIdStr, String classifyModelName)
            throws IOException
    {
        FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        trainingSetPath = fileIoHandler.getFilesInDirectory(trainingSet.trim());
        testSetPath = fileIoHandler.getFilesInDirectory(testSet.trim());
        wordCounter = selectWordCounter(paramAlgorithmIdStr.trim());
        classifier = selectClassifier(recognizeAlgorithmIdStr.trim());
        classificationModelPath = classifyModelName.trim();
    }
    
    /**
     * Vytvoří objekt pro zpracování argumentů příkazové řádky
     * při spuštění programu v režimu ručního zadávání textu.
     * 
     * @param classifyModelName cesta k souboru pro uložení klasifikačního modelu
     */
    public CmdArg(String classifyModelName) {
        classificationModelPath = classifyModelName.trim();
    }

    /**
     * Vrátí cesty k dokumentům z trénovací množiny.
     * 
     * @return cesty k dokumentům z trénovací množiny
     */
    public List<String> getTrainingSetPath() {
        return trainingSetPath;
    }

    /**
     * Vrátí cesty k dokumentům z testovací množiny.
     * 
     * @return cesty k dokumentům z testovací množiny
     */
    public List<String> getTestSetPath() {
        return testSetPath;
    }

    /**
     * Vrátí objekt pro vytvoření příznaků.
     * 
     * @return objekt pro vytvoření příznaků
     */
    public AWordCounter getParamAlgorithm() {
        return wordCounter;
    }

    /**
     * Vrátí objekt pro klasifikaci dokumentů.
     * 
     * @return objekt pro klasifikaci dokumentů
     */
    public AClassifier getRecognizeAlgorithm() {
        return classifier;
    }
    
    /**
     * Vrátí cestu k souboru pro uložení klasifikačního modelu.
     * 
     * @return cesta k souboru pro uložení klasifikačního modelu
     */
    public String getClassificationModelPath() {
        return classificationModelPath;
    }
    
    /**
     * Vybere algoritmus pro určování počtů slov (které tvoří příznaky dokumentu)
     * podle zadaného parametru příkazového řádku.
     * 
     * @param key identifikátor algoritmu pro vytváření příznaků dokumentu
     * @return čítač slov
     * @throws IOException 
     */
    private AWordCounter selectWordCounter(String key) throws IOException {
        FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        
        if (keyEquals(key, "o", "only-counting")) {
            return SimpleWordCounter.CreateWordCounter();
        }
        
        if (keyEquals(key, "p", "pos-tagging")) {
            PosTagger posTagger = new PosTagger(fileIoHandler.readStopWordFile());
            
            return PosTaggingWordCounter.CreateWordCounter(posTagger);
        }
        
        if (keyEquals(key, "s", "stemming")) {
            PosTagger posTagger = new PosTagger(fileIoHandler.readStopWordFile());
            Stemmer stemmer = new Stemmer(fileIoHandler.readWordPrefixFile(),
                fileIoHandler.readWordSufixFile(), fileIoHandler.readWordEndingFile());
            
            return StemDetectingWordCounter.CreateWordCounter(posTagger, stemmer);
        }
        
        return null;
    }
    
    /**
     * Vybere algoritmus klasifikace podle zadaného parametru příkazového řádku.
     * 
     * @param key identifikátor algoritmu pro klasifikaci
     * @return klasifikátor
     */
    private AClassifier selectClassifier(String key) {
        if (keyEquals(key, "b", "naive-bayes")) {
            return new NaiveBayesClassifier();
        }
        
        if (keyEquals(key, "n", "nearest-neighbor")) {
            return new NearestNeighborClassifier();
        }
        
        if (keyEquals(key, "r", "random")) {
            return new RandomSelectionClassifier();
        }
        
        return null;
    }
    
    /**
     * Určí, zda zadaný identifikátor algoritmu odpovídá jedné z možných podob
     * (krátké identifikátor s pomlčkou před, nebo dlouhý s dvěma pomlčkami).
     * 
     * @param key identifikátor algoritmu zadaný příkazem
     * @param shortKey krátký tvar podporovaného identifikátoru
     * @param longKey dlouhý tvar podporovaného identifikátoru
     * @return true, pokud identifikátor odpovídá
     */
    private boolean keyEquals(String key, String shortKey, String longKey) {
        return (("-" + shortKey).equals(key) || ("--" + longKey).equals(key));
    }
    
}
