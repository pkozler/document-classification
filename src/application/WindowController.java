package application;

import application.classification.AClassifier;
import application.containers.ClassificationModel;
import application.containers.TextDocument;
import application.helpers.FileIoHandler;
import application.parameterisation.AWordCounter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Třída, sloužící jako kontroler uživatelského rozhraní zobrazeného
 * při spuštění programu v režimu ručního zadávání textu.
 * 
 * @author Petr Kozler
 */
public class WindowController implements Initializable {
    
    /**
     * textové pole pro zobrazení názvu načteného klasifikačního modelu
     */
    @FXML
    private TextField classificationModelTextField;
    
    /**
     * textové pole pro zadávání textu ke klasifikaci
     */
    @FXML
    private TextArea testDocumentTextArea;
    
    /**
     * textové pole pro zobrazení zjištěné třídy dokumentu
     */
    @FXML
    private TextField documentClassTextField;
    
    /**
     * načtený čítač slov v textu
     */
    private AWordCounter wordCounter;
    
    /**
     * načtený klasifikátor textu
     */
    private AClassifier classifier;
    
    /**
     * Spustí klasifikaci ručně zadaného textu po stisknutí tlačítka.
     * 
     * @param event objekt události
     * @throws IOException 
     */
    @FXML
    private void handleClassifyButtonAction(ActionEvent event) throws IOException {
        String text = testDocumentTextArea.getText();
        TextDocument document = new TextDocument();
        document.setWordCounts(wordCounter.countWords(text));
        String documentClass = classifier.classify(document);
        FileIoHandler classDescription = FileIoHandler.getInstance();
        documentClassTextField.setText(classDescription.getClassDescription(documentClass));
    }

    /**
     * Nastaví načtený klasifikační model.
     * 
     * @param classificationModel klasifikační model
     * @param name název modelu
     */
    public void setClassificationModel(ClassificationModel classificationModel, String name) {
        this.classifier = classificationModel.getClassifier();
        this.wordCounter = classificationModel.getWordCounter();
        classificationModelTextField.setText(name);
    }
    
    /**
     * Slouží k provedení inicializace při zobrazení okna.
     * 
     * @param url URL
     * @param rb RB
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // žádný kód
    }    
    
}
