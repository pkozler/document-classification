package application.helpers;

import application.classification.AClassifier;
import application.Config;
import application.containers.ClassificationModel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Třída jedináčka, který slouží k manipulaci se soubory a uchovává
 * mapu klíčových slov jednotlivých tříd dokumentů a odpovídajících popisů.
 * 
 * @author Petr Kozler
 */
public final class FileIoHandler {
    
    /**
     * instance jedináčka pro manipulaci se soubory
     */
    private static FileIoHandler instance;
    
    /**
     * mapa zkratek tříd dokumentů a odpovídajících názvů
     */
    private final Map<String, String> CLASS_DESCRIPTIONS = new HashMap<>();
    
    /**
     * Vrátí instanci jedináčka pro manipulaci se soubory.
     * 
     * @return jedináček pro manipulaci se soubory
     */
    public static FileIoHandler getInstance() {
        if (instance == null) {
            try {
                instance = new FileIoHandler();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return instance;
    }
    
    /**
     * Inicializuje jedináčka a zamezuje vytvoření další instance.
     * 
     * @throws IOException 
     */
    private FileIoHandler() throws IOException {
        List<String> lines = Files.readAllLines(
                Paths.get(Config.DOCUMENT_CLASS_FILE));
        
        lines.stream().forEach((line) -> {
            String[] parts = line.split(Config.CLASS_DESC_SEPARATOR);
            CLASS_DESCRIPTIONS.put(parts[0], parts[1]);
        });
    }
    
    /**
     * Načte soubor se seznamem předpon slov v dokumentech.
     * 
     * @return seznam předpon
     * @throws IOException 
     */
    public List<String> readWordPrefixFile() throws IOException {
        List<String> wordPrefixLines = Files.readAllLines(
                Paths.get(Config.WORD_PREFIX_FILE));
        
        return wordPrefixLines;
    }
    
    /**
     * Načte soubor se seznamem přípon slov v dokumentech.
     * 
     * @return seznam přípon
     * @throws IOException 
     */
    public List<String> readWordSufixFile() throws IOException {
        List<String> wordSufixLines = Files.readAllLines(
                Paths.get(Config.WORD_SUFIX_FILE));
        
        return wordSufixLines;
    }
    
    /**
     * Načte soubor se seznamem koncovek slov v dokumentech.
     * 
     * @return seznam koncovek
     * @throws IOException 
     */
    public List<String> readWordEndingFile() throws IOException {
        List<String> wordEndingLines = Files.readAllLines(
                Paths.get(Config.WORD_ENDING_FILE));
        
        return wordEndingLines;
    }
    
    /**
     * Načte soubor se seznamem stop-slov v dokumentech.
     * 
     * @return seznam stop-slov
     * @throws IOException 
     */
    public List<String> readStopWordFile() throws IOException {
        List<String> stopWordLines = Files.readAllLines(
                Paths.get(Config.STOP_WORD_FILE));
        
        return stopWordLines;
    }
    
    /**
     * Načte seznam názvů souborů v adresáři na zadané cestě.
     * 
     * @param path cesta k adresáři
     * @return seznam názvů souborů
     * @throws IOException 
     */
    public List<String> getFilesInDirectory(String path)
            throws IOException {
        List<String> fileNames = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(Paths.get(
                path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    fileNames.add(filePath.toString());
                }
            });
        }
        
        return fileNames;
    }
    
    /**
     * Získá jméno souboru ze zadané cesty.
     * 
     * @param path cesta k souboru
     * @return jméno souboru
     * @throws IOException 
     */
    public String getFileNameFromPath(String path) throws IOException {
        return Paths.get(path).getFileName().toString();
    }
    
    /**
     * Načte obsah textového dokumentu na zadané cestě.
     * 
     * @param path cesta k souboru
     * @return obsah souboru
     * @throws IOException 
     */
    public String loadTextDocument(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
    
    /**
     * Načte klasifikační model ze souboru na zadané cestě.
     * 
     * @param path cesta k souboru
     * @return klasifikační model
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public ClassificationModel loadClassificationModel(String path)
            throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(
                path + Config.CLASSIFY_MODEL_EXTENSION);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            System.out.println("==================================================\n"
                + "Spuštěno načítání klasifikačního modelu ze souboru: " + path + "\n");
            ClassificationModel model = (ClassificationModel) ois.readObject();
            System.out.println("--- HOTOVO ---");
            
            return model;
        }
    }
    
    /**
     * Uloží klasifikační model do souboru na zadané cestě.
     * 
     * @param classificationModel klasifikační model
     * @param path cesta k souboru
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void saveClassificationModel(ClassificationModel classificationModel, String path)
            throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(
                path + Config.CLASSIFY_MODEL_EXTENSION);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            System.out.println("==================================================\n"
                + "Spuštěno ukládání klasifikačního modelu do souboru: " + path + "\n");
            oos.writeObject(classificationModel);
            System.out.println("--- HOTOVO ---");
        }
    }
    
    /**
     * Vrátí popis třídy podle zadaného klíčového slova.
     * 
     * @param key klíčové slovo
     * @return popis třídy
     */
    public String getClassDescription(String key) {
        return CLASS_DESCRIPTIONS.get(key);
    }
    
}
