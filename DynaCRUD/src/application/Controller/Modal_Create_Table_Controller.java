package application.Controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import application.Model.Schema_Table_DAO_Imp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Modal_Create_Table_Controller implements Initializable{

    private Connection conexao;
    private String schema;
    private Stage modalStage;
    private Popup popup;
    private Runnable onCloseCallback;

    public void setConstructor(Connection conn, Stage modal, String schema, Runnable onCloseCallback) {
        this.conexao = conn;
        this.modalStage = modal;
        this.schema = schema;
        this.onCloseCallback = onCloseCallback;
    }
    
    @FXML // fx:id="rootPane"
    private AnchorPane rootPane;
    
    @FXML // fx:id="campoTable"
    private TextField campoTable;
    
    @FXML // fx:id="campoAtributo"
    private TextArea campoAtributo;
    
    @FXML
    private void createTable(ActionEvent event) {
    	String nomeTable = campoTable.getText().trim();
    	String atributo =  campoAtributo.getText().trim();
    	
        if (nomeTable.isEmpty()) {
            showToast("O nome da tabela não pode estar vazio.");
            return;
        }

        // Validação adicional do nome do schema
        if (!nomeTable.matches("[a-zA-Z0-9_]+")) {
            showToast("Nome da tabela inválido. Apenas letras, números e underscores são permitidos.");
            return;
        }
        
        // Validação de atributo
        if (atributo.isEmpty()) {
            showToast("O atributo não pode estar vazio.");
            return;
        }
        
    	Schema_Table_DAO_Imp table = new Schema_Table_DAO_Imp();
    	table.createTable(conexao, schema, nomeTable, atributo)
    		.thenRun(() -> {
	            Platform.runLater(() -> {
	                if (modalStage != null) {
	                    modalStage.close();  // Fecha o modal
	                    closePopup();
	
	                    if (onCloseCallback != null) {
	                        onCloseCallback.run();  // Executa o callback
	                    }
	                }
	            });
	        })
	        .exceptionally(ex -> {
	            ex.printStackTrace(); 
	            showToast("Error ao criar tabela");
	            return null;
	        });
    }
    
    private void showToast(String mensagem) {
        Popup popup = new Popup();
        Label label = new Label(mensagem);
        label.setStyle("-fx-background-color: darkgray; -fx-text-fill: red; -fx-padding: 10px;");
        popup.getContent().add(label);
        
        // Exibe o popup
        popup.show(rootPane.getScene().getWindow());
        
        // Fechar o popup após 3 segundos
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> popup.hide())
        );
        timeline.play();
    }
    
    private void closePopup() {
        if (popup != null) {
            popup.hide();  // Fecha o Popup
        }
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
