package application.classification;

import application.containers.TextDocument;
import application.containers.MutableInt;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Třída, představující naivní Bayesův klasifikátor pro automatické
 * určování třídy textových dokumentů.
 * 
 * @author Petr Kozler
 */
public class NaiveBayesClassifier extends AClassifier {

    /**
     * relativní četnosti dokumentů pro jednotlivé třídy
     */
    protected double[] relativeClassFrequencies;
    
    /**
     * relativní četnosti jednotlivých slov pro jednotlivé třídy
     */
    protected double[][] relativeWordByClassFrequencies;

    /**
     * Natrénuje naivní Bayesův klasifikátor.
     * 
     * @param documents seznamy dokumentů a příslušných tříd
     */
    public void train(Map<String, List<TextDocument>> documents) {
        //FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        System.out.println("==================================================\n"
                + "Spuštěno trénování naivního Bayesova klasifikátoru.\n");
        
        relativeClassFrequencies = calculateRelativeClassFrequencies(documents);
        
        System.out.println("--- Relativní četnosti dokumentů pro jednotlivé třídy ---");
        /*for (int i = 0; i < classes.size(); i++) {
            System.out.println(fileIoHandler.getClassDescription(classes.get(i))
                    + ": " + relativeClassFrequencies[i]);
        }*/

        relativeWordByClassFrequencies = calculateRelativeWordByClassFrequencies(documents);
        
        System.out.println("--- Relativní četnosti jednotlivých slov pro jednotlivé třídy ---");
        /*for (int i = 0; i < classes.size(); i++) {
            System.out.println(fileIoHandler.getClassDescription(classes.get(i)));
            for (int j = 0; j < vocabulary.size(); j++) {
                System.out.println(vocabulary.get(j) + ": " + relativeWordByClassFrequencies[i][j]);
            }
            System.out.println("---");
        }*/
    }

    /**
     * Klasifikuje textový dokument metodou naivního Bayesova klasifikátoru.
     * 
     * @param document dokument ke klasifikaci
     * @return třída dokumentu
     */
    @Override
    public String classify(TextDocument document) {
        Map<String, Double> probabilities = getProbabilities(document.getWordCounts());

        String maxProbabilityClass = null;
        double maxProbability = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Double> probability : probabilities.entrySet()) {
            // nastavení případné nové třídy s větší pravděpodobností
            double currentProbability = probability.getValue();

            if (currentProbability > maxProbability) {
                maxProbability = currentProbability;
                maxProbabilityClass = probability.getKey();
            }
        }

        // výběr třídy s největší pravděpodobností příslušnosti dokumentu
        return maxProbabilityClass;
    }

    /**
     * Vypočítá relativní dokumentů pro jednotlivé třídy.
     * 
     * @param documents seznamy dokumentů a příslušných tříd
     * @return relativní četnosti dokumentů pro jednotlivé třídy
     */
    protected double[] calculateRelativeClassFrequencies(Map<String, List<TextDocument>> documents) {
        int totalDocumentCount = 0;

        // zjištění celkového počtu trénovacích dokumentů ve všech třídách
        for (Map.Entry<String, List<TextDocument>> documentsByClass : documents.entrySet()) {
            totalDocumentCount += documentsByClass.getValue().size();
        }
        
        double[] frequencies = new double[classes.size()];

        // vydělení počtů dokumentů pro jednotlivé třídy celkovým počtem pro určení relativních četností
        for (int i = 0; i < classes.size(); i++) {
            String documentClass = classes.get(i);
            MutableInt documentCount = new MutableInt(documents.get(documentClass).size());
            frequencies[i] = documentCount.getDecimal(totalDocumentCount);
        }

        return frequencies;
    }

    /**
     * Vypočítá relativní četnosti slov v dokumentech jednotlivých tříd.
     * 
     * @param documents seznamy dokumentů a příslušných tříd
     * @return relativní četnosti jednotlivých slov pro jednotlivé třídy
     */
    protected double[][] calculateRelativeWordByClassFrequencies(Map<String, List<TextDocument>> documents) {
        double[][] frequencies = new double[classes.size()][];

        // určení relativních četností slov v trénovacích dokumentech pro každou třídu zvlášť
        for (int i = 0; i < classes.size(); i++) {
            String documentClass = classes.get(i);
            //System.out.println("--- Absolutní četnosti slov pro třídu " + documentClass + " ---");
            frequencies[i] = calculateRelativeWordFrequencies(
                    documents.get(documentClass));
        }

        return frequencies;
    }

    /**
     * Vypočítá relativní četnosti slov v dokumentech dané třídy.
     * 
     * @param documentsInClass dokumenty třídy
     * @return relativní četnosti jednotlivých slov pro třídu
     */
    protected double[] calculateRelativeWordFrequencies(List<TextDocument> documentsInClass) {
        Map<String, MutableInt> wordCounts = new HashMap<>();

        // inicializace počtu výskytů v dokumentech dané třídy pro všechna slova ve slovníku (+1)
        vocabulary.stream().forEach((documentWord) -> {
            wordCounts.put(documentWord, new MutableInt(1));
        });

        int totalWordCount = 0;

        // suma z počtu výskytů každého slova (+1) přes všechny dokumenty dané třídy (a celková suma)
        for (TextDocument document : documentsInClass) {
            for (Map.Entry<String, MutableInt> wordCountsInDocument : document.getWordCounts().entrySet()) {
                int wordCountValue = wordCountsInDocument.getValue().get();
                String wordKey = wordCountsInDocument.getKey();
                wordCounts.get(wordKey).add(wordCountValue);
            }
            
            totalWordCount += (document.getTotalWordCount() + 1);
        }
        
        /*for (Map.Entry<String, MutableInt> wordCount : wordCounts.entrySet()) {
            System.out.println(wordCount.getKey() + ": " + wordCount.getValue());
        }*/
        
        //System.out.println("(celkem slov: " + totalWordCount + ")\n---");
        
        double[] frequencies = new double[vocabulary.size()];

        // vydělení počtů výskytů slov celkovým počtem v dané třídě pro určení relativních četností
        for (Map.Entry<String, MutableInt> wordCount : wordCounts.entrySet()) {
            int index = Collections.binarySearch(vocabulary, wordCount.getKey());
            frequencies[index] = wordCount.getValue().getDecimal(totalWordCount);
        }

        return frequencies;
    }

    /**
     * Vypočítá logaritmickou pravděpodobnost příslušnosti klasifikovaného
     * dokumentu k dané třídě jako součty logaritmů pravděpodobností pro třídu
     * a jednotlivá slova, které nahrazují součiny samotných pravděpodobností,
     * aby nedocházelo k podtečení pro výsledné malé číselné hodnoty.
     * 
     * @param documentClass třída dokumentu
     * @param wordCounts četnosti slov v textu dokumentu
     * @return pravděpodobnost pro třídu
     */
    protected double calculateClassProbability(String documentClass, Map<String, MutableInt> wordCounts) {
        // zjištění relativní četnosti výskytu dokumentů z dané třídy v trénovací množině
        int classIndex = Collections.binarySearch(classes, documentClass);
        double probability = Math.log(relativeClassFrequencies[classIndex]);
        double[] relativeWordFrequencies = relativeWordByClassFrequencies[classIndex];

        for (Map.Entry<String, MutableInt> wordCount : wordCounts.entrySet()) {
            int wordIndex = Collections.binarySearch(vocabulary, wordCount.getKey());

            // ignorování slov, která se nevyskytují ve slovníku sestaveném z trénovací množiny
            if (wordIndex < 0) {
                continue;
            }

            // zjištění relativní četnosti výskytu aktuálního slova z pole četností pro danou třídu
            double frequency = relativeWordFrequencies[wordIndex];

            int wordCountValue = wordCount.getValue().get();

            // vynásobení s relativní četností výskytů slova (n-krát pro n-násobné výskyty v textu)
            for (int i = 0; i < wordCountValue; i++) {
                probability += Math.log(frequency);
            }
        }

        return probability;
    }

    /**
     * Vypočítá pravděpodobnosti příslušnosti klasifikovaného dokumentu k
     * jednotlivým třídám.
     * 
     * @param wordCounts četnosti slov v textu dokumentu
     * @return pravděpodobnosti pro jednotlivé třídy
     */
    protected Map<String, Double> getProbabilities(Map<String, MutableInt> wordCounts) {
        Map<String, Double> probabilities = new HashMap<>();

        classes.stream().forEach((documentClass) -> {
            // určení logaritmu pravděpodobnosti příslušnosti dokumentu k dané třídě
            double probability = calculateClassProbability(documentClass, wordCounts);
            probabilities.put(documentClass, probability);
        });

        return probabilities;
    }

}
