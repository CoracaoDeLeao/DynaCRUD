package application.Controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import application.Util.Conexao;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConexaoGUI_Controller implements Initializable {
	
	private Stage stage;
	
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML // fx:id="AnchorPane"
    private AnchorPane AnchorPane; 

    @FXML // fx:id="LabelIP"
    private Label LabelIP;
    
    @FXML // fx:id="btnConectar"
    private Button btnConectar;
    
    @FXML // fx:id="fieldUSER"
    private TextField fieldUSER;
    
    @FXML // fx:id="fieldSENHA"
    private PasswordField fieldSENHA;
    
    @FXML // fx:id="fieldIP"
    private TextField fieldIP;
    
    @FXML // fx:id="fieldPORTA"
    private TextField fieldPORTA;
    
    @FXML
    private void conectar(ActionEvent event) {
    	String campoUSER = fieldUSER.getText();
    	String campoSENHA = fieldSENHA.getText();
    	String campoIP = fieldIP.getText();
    	String campoPORTA = fieldPORTA.getText();
    	
    	// Obtém a instância do DAO com os dados fornecidos pela interface
    	Conexao conn = Conexao.getInstance(campoIP, campoPORTA, campoUSER, campoSENHA);
    	
    	// Conexão assíncrona
        Task<Boolean> conectarTask = new Task<>() {
            @Override
            protected Boolean call() {
                conn.conectar();
                return conn.isConectado();
            }
        };
    	
    	 // Quando a tarefa for concluída, mude a interface do usuário
        conectarTask.setOnSucceeded(workerStateEvent -> {
            boolean conectado = conectarTask.getValue();
            if (conectado) {
                Main main = Main.getInstance();
                main.openDataBaseWindow(stage, conn, campoUSER, campoIP, campoPORTA);
            } else {
            	// Exibir um alerta em caso de falha de conexão
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro de Conexão");
                alert.setHeaderText("Falha ao conectar ao banco de dados.");
                alert.setContentText("Verifique as configurações e tente novamente.");
                alert.showAndWait();
			}
        });
        
        // Em caso de erro
        conectarTask.setOnFailed(workerStateEvent -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro de Conexão");
            alert.setHeaderText("Ocorreu um erro durante a conexão.");
            alert.setContentText(conectarTask.getException().getMessage());
            alert.showAndWait();
        });
    	
    	// Executa a tarefa em uma nova thread
        new Thread(conectarTask).start();
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}

