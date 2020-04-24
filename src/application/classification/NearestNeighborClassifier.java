package application.classification;

import application.helpers.FileIoHandler;
import application.containers.MutableInt;
import application.containers.TextDocument;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Třída, představující klasifikátor využívající metodu k nejbližších sousedů
 * pro automatické určování třídy textových dokumentů.
 *
 * @author Petr Kozler
 */
public class NearestNeighborClassifier extends AClassifier {

    /**
     * seznam přepravek s váženými četnostmi slov v trénovacích dokumentech
     */
    protected List<WeightedDocument> weightedDocuments;

    /**
     * mapa slov ve slovníku s počty dokumentů, ve kterých se vyskytují
     */
    protected Map<String, MutableInt> documentWithWordCounts;

    /**
     * číslo k určující počet nejbližších sousedů pro určení třídy
     */
    protected int nearestNeighborCount;

    /**
     * Třída, uchovávající vážené četnosti slov v dokumentu používané pro
     * výpočet podobnosti dokumentů při klasifikaci metodou k nejbližších
     * sousedů.
     *
     * @author Petr Kozler
     */
    protected class WeightedDocument implements Serializable {

        /**
         * třída dokumentu
         */
        public final String CLASS;

        /**
         * četnosti jednotlivých slov
         */
        public final Map<String, MutableInt> WORD_COUNTS;

        /**
         * vážené četnosti jednotlivých slov
         */
        private final Map<String, Double> WEIGHTED_WORD_COUNTS = new HashMap<>();

        /**
         * Vytvoří novou přepravku pro vážené četnosti slov bez specifikace
         * třídy příslušného dokumentu.
         *
         * @param wordCounts absolutní četnosti slov
         */
        public WeightedDocument(Map<String, MutableInt> wordCounts) {
            this(null, wordCounts);
        }

        /**
         * Vytvoří novou přepravku pro vážené četnosti slov se specifikací třídy
         * příslušného dokumentu.
         *
         * @param CLASS třída dokumentu
         * @param wordCounts absolutní četnosti slov
         */
        public WeightedDocument(String CLASS, Map<String, MutableInt> wordCounts) {
            this.CLASS = CLASS;
            this.WORD_COUNTS = wordCounts == null ? new HashMap<>() : wordCounts;
        }

        /**
         * Vypočítá a uloží váženou četnost slova.
         *
         * @param word slovo
         * @param documentTotalCount celkový počet dokumentů
         * @param documentContainingWordCount počet dokumentů, které slovo
         * obsahují
         */
        public void putWeightedWordCount(String word, int documentTotalCount, int documentContainingWordCount) {
            MutableInt count = WORD_COUNTS.get(word);

            if (count == null) {
                return;
            }

            double weightedCount = count.getDecimal() * log2(
                    (double) documentTotalCount / (double) documentContainingWordCount);
            WEIGHTED_WORD_COUNTS.put(word, weightedCount);
        }

        /**
         * Vypočítá podobnost dokumentu s dokumentem z trénovací množiny.
         *
         * @param trainingDocument dokument z trénovací množiny
         * @return podobnost dokumentů
         */
        public double getSimilarity(WeightedDocument trainingDocument) {
            double similarity = 0;

            for (Map.Entry<String, Double> weightedWordCount : WEIGHTED_WORD_COUNTS.entrySet()) {
                Double trainingDocumentWeightedWordCount
                        = trainingDocument.WEIGHTED_WORD_COUNTS.get(weightedWordCount.getKey());

                if (trainingDocumentWeightedWordCount == null) {
                    continue;
                }

                similarity += (weightedWordCount.getValue() * trainingDocumentWeightedWordCount);
            }

            double norm = getNorm();
            double trainingDocumentNorm = trainingDocument.getNorm();

            return similarity / (norm * trainingDocumentNorm);
        }

        /**
         * Vypočítá normu jako euklidovskou vzdálenost vážených četností slov.
         *
         * @return norma
         */
        private double getNorm() {
            double norm = 0;

            for (Double weightedCount : WEIGHTED_WORD_COUNTS.values()) {
                norm += (weightedCount * weightedCount);
            }

            return Math.sqrt(norm);
        }

        /**
         * Vypočítá binární logaritmus.
         *
         * @param n argument
         * @return binární logaritmus
         */
        private double log2(double n) {
            if (n <= 0) {
                throw new IllegalArgumentException();
            }

            return Math.log(n) / Math.log(2);
        }

    }

    /**
     * Třída, uchovávající vypočítanou hodnotu podobnosti a třídu dokumentu z
     * trénovací množiny pro použití při určení pravděpodobnosti příslušnosti
     * dokumentu k dané třídě při klasifikaci metodou k nejbližších sousedů.
     *
     * @author Petr Kozler
     */
    protected class DocumentSimilarity implements Comparable<DocumentSimilarity> {

        /**
         * třída dokumentu
         */
        public final String CLASS;

        /**
         * podobnost dokumentu
         */
        public final double VALUE;

        /**
         * Vytvoří novou přepravku pro uchování třídy a podobnosti dokumentu.
         *
         * @param clazz třída dokumentu
         * @param value podobnost dokumentu
         */
        public DocumentSimilarity(String clazz, double value) {
            CLASS = clazz;
            VALUE = value;
        }

        /**
         * Porovná hodnoty podobností pro sestupné řazení v seznamu.
         *
         * @param o přepravka s hodnotou podobnosti
         * @return výsledek porovnání
         */
        @Override
        public int compareTo(DocumentSimilarity o) {
            return Double.compare(o.VALUE, VALUE);
        }

    }

    /**
     * Natrénuje klasifikátor k-NN (k nejbližších sousedů).
     *
     * @param documents seznamy dokumentů a příslušných tříd
     */
    @Override
    public void train(Map<String, List<TextDocument>> documents) {
        FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        System.out.println("==================================================\n"
                + "Spuštěno trénování klasifikátoru podle nejbližšího souseda.\n");
        weightedDocuments = getWeightedDocuments(documents);
        documentWithWordCounts = calculateTrainingWeightedWordCounts();
        nearestNeighborCount = classes.size();
    }

    /**
     * Klasifikuje textový dokument metodou nejbližšího souseda.
     *
     * @param document dokument ke klasifikaci
     * @return třída dokumentu
     */
    @Override
    public String classify(TextDocument document) {
        WeightedDocument weightedDocument = calculateWeightedWordCounts(document.getWordCounts());
        List<DocumentSimilarity> similarities = calculateSimilarities(weightedDocument);

        /*for (int i = 0; i < similarities.size(); i++) {
            System.out.println(similarities.get(i).CLASS + ": " + similarities.get(i).VALUE);
        }*/
        
        Map<String, Double> confidences = getConfidences(similarities);

        String maxConfidenceClass = null;
        double maxConfidence = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Double> confidence : confidences.entrySet()) {
            // nastavení případné nové třídy s větší pravděpodobností
            double currentConfidence = confidence.getValue();

            if (currentConfidence > maxConfidence) {
                maxConfidence = currentConfidence;
                maxConfidenceClass = confidence.getKey();
            }
        }

        // výběr třídy s největší pravděpodobností příslušnosti dokumentu
        return maxConfidenceClass;
    }

    /**
     * Vytvoří seznam přepravek pro uchování vážených četností slov v dokumentech.
     * 
     * @param documents mapa tříd a příslušných dokumentů uchovávajících absolutní četnosti
     * @return seznam přepravek uchovávajících vážené četnosti
     */
    protected List<WeightedDocument> getWeightedDocuments(Map<String, List<TextDocument>> documents) {
        List<WeightedDocument> weightedDocumentList = new ArrayList<>();

        documents.entrySet().stream().forEach((documentsByClass) -> {
            String documentClass = documentsByClass.getKey();

            // vytvoření přepravek pro dokumenty dané třídy a uložení do seznamu
            documentsByClass.getValue().stream().map((document) -> new WeightedDocument(
                    documentClass, document.getWordCounts())).forEach((weightedDocument) -> {
                        weightedDocumentList.add(weightedDocument);
                    });
        });

        return weightedDocumentList;
    }

    /**
     * Vypočítá vážené četnosti slov v jednotlivých dokumentech pro použití
     * při výpočtu podobnosti dokumentů a vrátí vytvořenou mapu slov
     * ve slovníku s celkovým počtem dokumentů, ve kterých se tato slova
     * vyskytují - ta je později znovu použita při výpočtu vážených slov
     * klasifikovaných dokumentů.
     * 
     * @return mapa slov s počtem dokumentů, kde se vyskytují
     */
    protected Map<String, MutableInt> calculateTrainingWeightedWordCounts() {
        int documentTotalCount = weightedDocuments.size();
        Map<String, MutableInt> documentCountMap = new HashMap<>();

        // inicializace mapy slov s počty dokumentů
        vocabulary.stream().forEach((word) -> {
            documentCountMap.put(word, new MutableInt());
        });

        // určení počtu dokumentů pro jednotlivá slova
        weightedDocuments.stream().forEach((weightedDocument) -> {
            weightedDocument.WORD_COUNTS.keySet().stream().forEach((word) -> {
                documentCountMap.get(word).inc();
            });
        });

        weightedDocuments.stream().forEach((weightedDocument) -> {
            // určení vážených četností pro každé slovo v každém dokumentu
            weightedDocument.WORD_COUNTS.keySet().stream().forEach((word) -> {
                weightedDocument.putWeightedWordCount(word,
                        documentTotalCount, documentCountMap.get(word).get());
            });
        });

        return documentCountMap;
    }

    /**
     * Vypočítá vážené četnosti slov pro klasifikovaný dokument.
     * 
     * @param wordCounts absolutní četnosti slov dokumentu
     * @return přepravka s váženými četnostmi slov
     */
    protected WeightedDocument calculateWeightedWordCounts(Map<String, MutableInt> wordCounts) {
        WeightedDocument weightedDocument = new WeightedDocument(wordCounts);
        int documentTotalCount = weightedDocuments.size();

        weightedDocument.WORD_COUNTS.keySet().stream().forEach((word) -> {
            MutableInt count = documentWithWordCounts.get(word);
            
            // určení vážené četnosti pro slovo, pokud je nalezeno ve slovníku
            if (count != null) {
                weightedDocument.putWeightedWordCount(word, documentTotalCount, count.get());
            }
        });

        return weightedDocument;
    }

    /**
     * Vypočítá podobnosti jednotlivých dokumentů z trénovací množiny
     * s právě klasifikovaným dokumentem pro určení nejpravděpodobnější
     * třídy, do které klasifikovaný dokument náleží.
     * 
     * @param document přepravka s váženými četnostmi slov v klasifikovaném dokumentu
     * @return seznam přepravek v podobnostmi a třídami trénovacích dokumentů
     */
    protected List<DocumentSimilarity> calculateSimilarities(WeightedDocument document) {
        List<DocumentSimilarity> similarities = new ArrayList<>(weightedDocuments.size());

        weightedDocuments.stream().map((trainingDocument) -> new DocumentSimilarity(
                trainingDocument.CLASS, document.getSimilarity(trainingDocument))).forEach((similarity) -> {
                    // uložení vypočtené podobnosti do seznamu
                    similarities.add(similarity);
        });

        Collections.sort(similarities);

        return similarities;
    }

    /**
     * Určí součet podobností prvních k dokumentů z trénovací množiny,
     * nejpodobnějších klasifikovanému dokumentu, které patří k určené třídě,
     * nebo celkový součet podobností prvních k dokumentů, pokud je jako třída
     * předána hodnota null.
     * 
     * @param similarities seznam přepravek podobností a tříd trénovacích dokumentů
     * @param k počet nejbližších sousedů (nejpodobnějších dokumentů) k určení třídy
     * @param documentClass třída dokumentů, pro kterou bude počítán součet podobností
     * @return součet podobností
     */
    protected double getSimilaritySum(List<DocumentSimilarity> similarities, int k, String documentClass) {
        double sum = 0;

        for (int i = 0; i < k; i++) {
            // přeskočení na další dokument, pokud třída není null a dokument do této třídy nepatří
            if (documentClass != null && !documentClass.equals(similarities.get(i).CLASS)) {
                continue;
            }

            double similarity = similarities.get(i).VALUE;

            // přičtení podobnosti dokumentu (pokud je hodnota platné číslo)
            if (!Double.isNaN(similarity)) {
                sum += similarities.get(i).VALUE;
            }
        }

        return sum;
    }

    /**
     * Vypočítá pravděpodobnosti příslušnosti klasifikovaného dokumentu k
     * jednotlivým třídám.
     * 
     * @param similarities podobnosti trénovacích dokumentů s testovacím dokumentem
     * @return pravděpodobnosti pro jednotlivé třídy
     */
    protected Map<String, Double> getConfidences(List<DocumentSimilarity> similarities) {
        Map<String, Double> confidences = new HashMap<>();
        int k = Math.min(nearestNeighborCount, similarities.size());
        // výpočet celkového součtu podobností pro k nejbližších sousedů
        double totalSimilaritySum = getSimilaritySum(similarities, k, null);

        classes.stream().forEach((documentClass) -> {
            // výpočet součtu podobností pro k nejbližších sousedů z dané třídy
            double similaritySumByClass = getSimilaritySum(similarities, k, documentClass);

            /*System.out.println("Pravděpodobnost třídy " + documentClass
            + ": " + similaritySumByClass / totalSimilaritySum);*/
            confidences.put(documentClass, similaritySumByClass / totalSimilaritySum);
        });

        return confidences;
    }

}
