package application;

import application.containers.ClassificationModel;
import application.helpers.FileIoHandler;
import application.helpers.CmdArg;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hlavní třída, prostřednictvím níž se spouští celá aplikace.
 * 
 * @author Petr Kozler
 */
public class Main extends Application {
    
    /**
     * objekt pro zpracování argumentů příkazového řádku
     */
    private static CmdArg cmdArg;
    
    /**
     * Zobrazí okno GUI při spuštění v režimu ručního zadávání textu.
     * 
     * @param stage okno
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        String path = cmdArg.getClassificationModelPath();
        ClassificationModel classificationModel = fileIoHandler.loadClassificationModel(path);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Config.WINDOW_FXML_FILE));
        Parent root = (Parent) fxmlLoader.load();
        WindowController controller = fxmlLoader.<WindowController>getController();
        controller.setClassificationModel(classificationModel, fileIoHandler.getFileNameFromPath(path));
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Klasifikace textu");
        stage.show();
    }
    
    /**
     * Načte trénovací data, natrénuje, otestuje a uloží
     * zvolený klasifikační model.
     * 
     * @throws IOException 
     */
    private static void create() throws IOException {
        FileIoHandler fileIoHandler = FileIoHandler.getInstance();
        
        ModelCreator modelCreator = new ModelCreator(cmdArg.getParamAlgorithm(), cmdArg.getRecognizeAlgorithm());
        
        ClassificationModel classificationModel = modelCreator.createClassificationModel(
                cmdArg.getTrainingSetPath(), cmdArg.getTestSetPath());
        
        fileIoHandler.saveClassificationModel(classificationModel, cmdArg.getClassificationModelPath());
        System.exit(0);
    }
    
    /**
     * Vypíše nápovědu ke spuštění programu.
     */
    private static void help() {
        System.out.printf("\n");
        System.out.printf("Program pro klasifikaci dokumentů (Verze 1.0)\n");
        System.out.printf("Semestrální práce z předmětu \"Umělá inteligence a rozpoznávání\" (KIV/UIR)\n");
        System.out.printf("Autor: Petr Kozler (A13B0359P), 2017\n");
        System.out.printf("\n");
        System.out.printf("Použití:   " + Config.EXECUTABLE_NAME +
                " [<trénovací množina> <testovací množina> <parametrizační algoritmus> <klasifikační algoritmus>] <název modelu>\n");
        System.out.printf("Popis parametrů:\n");
        System.out.printf("    <trénovací množina> ........... cesta k adresáři s dokumenty pro natrénování klasifikačního modelu\n");
        System.out.printf("    <testovací množina> ........... cesta k adresáři s dokumenty pro otestování klasifikačního modelu\n");
        System.out.printf("    <parametrizační algoritmus> ... identifikační číslo parametrizačního algoritmu (1 - 3)\n");
        System.out.printf("    <klasifikační algoritmus> ..... identifikační číslo klasifikačního algoritmu (1 - 3)\n");
        System.out.printf("    <název modelu> ................ název souboru (bez přípony) klasifikačního modelu - povinné\n");
        System.out.printf("\n");
        
        System.exit(0);
    }

    /**
     * Spustí aplikaci v režimu vytváření klasifikačního modelu
     * nebo ručního zadávání textu pomocí GUI, nebo vypíše nápovědu
     * ke spuštění programu v závislosti na počtu zadaných
     * argumentů příkazové řádky
     * 
     * @param args argumenty příkazové řádky
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args)
            throws IOException, FileNotFoundException, ClassNotFoundException {
        switch (args.length) {
            case 0:
                help();
                break;
            case 1:
                cmdArg = new CmdArg(args[0]);
                launch(args);
                break;
            case 5:
                cmdArg = new CmdArg(
                        args[0], args[1], args[2], args[3], args[4]);
                create();
                break;
            default:
                System.err.println("Neplatný počet argumentů.");
                help();
                break;
        }
    }
    
}
