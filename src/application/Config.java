package application;

/**
 * Knihovní třída, definující konfigurační konstanty programu.
 * 
 * @author Petr Kozler
 */
public final class Config {
    
    /**
     * název spustitelného archivu
     */
    public static final String EXECUTABLE_NAME = "DocumentClassification.jar";
    
    /**
     * FXML dokument definující GUI programu v režimu klasifikace ručně zadaného textu
     */
    public static final String WINDOW_FXML_FILE = "Window.fxml";
    
    /**
     * textový soubor se seznamem klíčových slov a odpovídajících popisů tříd dokumentů
     */
    public static final String DOCUMENT_CLASS_FILE = "data/document_classes.csv";
    
    /**
     * textový soubor se seznamem stop-slov (nevýznamných slov v textu)
     */
    public static final String STOP_WORD_FILE = "data/stop_words.csv";
    
    /**
     * textový soubor se seznamem předpon slov
     */
    public static final String WORD_PREFIX_FILE = "data/word_prefixes.csv";
    
    /**
     * textový soubor se seznamem přípon slov
     */
    public static final String WORD_SUFIX_FILE = "data/word_sufixes.csv";
    
    /**
     * textový soubor se seznamem koncovek slov
     */
    public static final String WORD_ENDING_FILE = "data/word_endings.csv";
    
    /**
     * přípona binárních souborů pro uchování klasifikačního modelu
     */
    public static final String CLASSIFY_MODEL_EXTENSION = ".model";
    
    /**
     * regulární výraz popisující oddělovače klíčových slov tříd v názvech dokumentů
     */
    public static final String DOCUMENT_CLASS_SEPARATOR = "(_|\\.)";
    
    /**
     * znak oddělující klíčová slova a popisy tříd
     */
    public static final String CLASS_DESC_SEPARATOR = ":";
    
    /**
     * regulární výraz popisující nevýznamové znaky v textu
     */
    public static final String WORD_NOT_ALPHA_REGEX = "[^0123456789aábcčdďeéěfghiíjklmnňoópqrřsštťuúůvwxyýzž]+";
    
    /**
     * Zamezuje vytvoření instance.
     */
    private Config() {
        // žádný kód
    }
    
}
