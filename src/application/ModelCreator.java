package application;

import application.classification.AClassifier;
import application.containers.ClassificationModel;
import application.helpers.FileIoHandler;
import application.containers.MutableInt;
import application.parameterisation.AWordCounter;
import application.containers.TextDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Třída, sloužící k načítání dokumentů z trénovací množiny
 * a vytváření klasifikačního modelu.
 * 
 * @author Petr Kozler
 */
public class ModelCreator {
    
    /**
     * množina všech nalezených tříd, do kterých patří některé z trénovacích dokumentů
     */
    private final Set<String> CLASSES = new HashSet<>();
    
    /**
     * množina všech nalezených slov ve všech trénovacích dokumentech
     */
    private final Set<String> VOCABULARY = new HashSet<>();
    
    /**
     * mapa přiřazující načtené dokumenty k odpovídajícím třídám
     */
    private final Map<String, List<TextDocument>> DOCUMENTS = new HashMap<>();
    
    /**
     * objekt pro manipulaci se soubory
     */
    private final FileIoHandler FILE_IO_HANDLER;
    
    /**
     * čítač slov v dokumentech
     */
    private final AWordCounter WORD_COUNTER;
    
    /**
     * klasifikátor dokumentů
     */
    private final AClassifier CLASSIFIER;
    
    /**
     * Vytvoří objekt pro načítání dokumentů, vytváření a testování
     * klasifikačního modelu.
     * 
     * @param wordCounter čítač slov v dokumentech
     * @param classifier klasifikátor dokumentů
     * @throws IOException 
     */
    public ModelCreator(AWordCounter wordCounter, AClassifier classifier)
            throws IOException {
        FILE_IO_HANDLER = FileIoHandler.getInstance();
        WORD_COUNTER = wordCounter;
        CLASSIFIER = classifier;
    }
    
    /**
     * Vytvoří klasifikační model.
     * 
     * @param trainingSetFiles soubory trénovací množiny
     * @param testSetFiles soubory testovací množiny
     * @return klasifikační model
     * @throws IOException 
     */
    public ClassificationModel createClassificationModel(List<String> trainingSetFiles, List<String> testSetFiles)
            throws IOException {
        // načtení vstupních dat
        initializeClassifier(trainingSetFiles);
        
        // trénování a vyhodnocování
        CLASSIFIER.train(DOCUMENTS);
        evaluateClassifier(testSetFiles);
        
        ClassificationModel classificationModel = new ClassificationModel();
        classificationModel.setModelComponents(WORD_COUNTER, CLASSIFIER);
        
        return classificationModel;
    }
    
    /**
     * Spustí načítání dokumentů v trénovací množině.
     * 
     * @param trainingSetFiles seznam souborů trénovací množiny
     * @throws IOException
     */
    private void initializeClassifier(List<String> trainingSetFiles)
            throws IOException {
        System.out.println("Spuštěno načítání dokumentů pro trénování klasifikátoru.\n");
        
        for (String path : trainingSetFiles) {
            // načtení příznaků dokumentu
            TextDocument document = loadDocumentInfo(path);
            System.out.println(document.NAME);
            addDocumentInfoToLists(document);
        }
        
        System.out.println("==================================================\n"
                + "Počty dokumentů ve třídách:\n---");
        
        final MutableInt totalDocumentCount = new MutableInt();
        DOCUMENTS.entrySet().stream().forEach((documentCount) -> {
            String classDesc = FILE_IO_HANDLER.getClassDescription(documentCount.getKey());
            int docCount = documentCount.getValue().size();
            
            System.out.println(classDesc + ": " + docCount);
            totalDocumentCount.add(docCount);
        });
        
        System.out.println("---\nCelkem dokumentů: " + totalDocumentCount);
        System.out.println("Celkem tříd: " + CLASSES.size());
        System.out.println("Celkem slov: " + VOCABULARY.size());
        System.out.println("--- HOTOVO ---");
        
        List<String> classes = new ArrayList<>(CLASSES);
        Collections.sort(classes);
        List<String> vocabulary = new ArrayList<>(VOCABULARY);
        Collections.sort(vocabulary);
        
        Map<String, List<TextDocument>> documents = new HashMap<>();
        
        DOCUMENTS.entrySet().stream().forEach((documentList) -> {
            List<TextDocument> readOnlyDocumentList
                    = Collections.unmodifiableList(documentList.getValue());
            documents.put(documentList.getKey(), readOnlyDocumentList);
        });
        
        Collections.unmodifiableMap(documents);
        
        CLASSIFIER.setLists(Collections.unmodifiableList(classes),
                Collections.unmodifiableList(vocabulary));
    }
    
    /**
     * Vrátí zkratku třídy podle názvu dokumentu.
     * 
     * @param name název dokumentu
     * @return zkratka třídy
     */
    private String getClassKeyWord(String name) {
        return name.split(Config.DOCUMENT_CLASS_SEPARATOR)[1];
    }
    
    /**
     * Přidá slova nalezená v dokumentu do slovníku a dokument přidá
     * do seznamu v mapě k odpovídající třídě.
     * 
     * @param document dokument
     */
    private void addDocumentInfoToLists(TextDocument document) {
        String documentClass = getClassKeyWord(document.NAME);
        VOCABULARY.addAll(document.getWordCounts().keySet());
        
        // přiřazení dokumentu k odpovídající třídě určené názvem souboru
        CLASSES.add(documentClass);
        
        if (DOCUMENTS.get(documentClass) == null) {
            DOCUMENTS.put(documentClass, new ArrayList<>());
        }
        
        List<TextDocument> documentList = DOCUMENTS.get(documentClass);
        documentList.add(document);
    }
    
    /**
     * Načte obsah dokumentu a spočítá četnosti slov.
     * 
     * @param path cesta k dokumentu
     * @return dokument
     * @throws IOException 
     */
    private TextDocument loadDocumentInfo(String path) throws IOException {
        // získání názvu souboru korpusu dokumentu a vytvoření přepravky
        String name = FILE_IO_HANDLER.getFileNameFromPath(path);
        TextDocument document = new TextDocument(name);
        String text = FILE_IO_HANDLER.loadTextDocument(path);
        // načtení textu, spočtení slov a doplnění nalezených slov do slovníku
        Map<String, MutableInt> wordCounts = WORD_COUNTER.countWords(text);
        document.setWordCounts(wordCounts);
        
        return document;
    }
    
    /**
     * Spustí vyhodnocování spolehlivosti klasifikátoru dokumenty testovací množiny.
     * 
     * @param testSetFiles seznam dokumentů testovací množiny
     * @throws IOException 
     */
    private void evaluateClassifier(List<String> testSetFiles)
            throws IOException {
        System.out.println("==================================================\n"
                + "Spuštěno testování klasifikace dokumentů:\n---");
        FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        
        int count = 0;
        int correctCount = 0;
        for (String path : testSetFiles) {
            // načtení testovacího dokumentu
            TextDocument document = loadDocumentInfo(path);
            String actualClass = getClassKeyWord(document.NAME);
            String detectedClass = CLASSIFIER.classify(document);
            FileIoHandler classDescription = FileIoHandler.getInstance();
            
            count++;
            System.out.println("Testovací dokument: " + document.NAME);
            System.out.println("Zjištěná třída: " + classDescription.getClassDescription(detectedClass));
            System.out.println("Skutečná třída: " + classDescription.getClassDescription(actualClass));
            
            // porovnání skutečné a zjištěné třídy
            boolean correct = actualClass.equals(detectedClass);
            
            if (correct) {
                correctCount++;
            }
            
            System.out.println("Hodnocení: " + (correct ? "OK" : "FAIL") + "\n---");
        }
        
        // výpočet spolehlivosti
        float success = ((float) correctCount / (float) count) * 100;
        System.out.println("Správně klasifikováno: " + correctCount + " z " + count + " dokumentů");
        System.out.println("Úspěšnost klasifikace: " + success + "%");
        System.out.println("--- HOTOVO ---");
    }
    
}
