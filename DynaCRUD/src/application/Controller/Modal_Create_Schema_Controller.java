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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Modal_Create_Schema_Controller implements Initializable {

    private Connection conexao;
    private Stage modalStage;
    private Popup popup;
    private Runnable onCloseCallback;

    public void setConstructor(Connection conn, Stage modal, Runnable onCloseCallback) {
        this.conexao = conn;
        this.modalStage = modal;
        this.onCloseCallback = onCloseCallback;
    }
    
    @FXML // fx:id="rootPane"
    private AnchorPane rootPane;
    
    @FXML // fx:id="campoSchema"
    private TextField campoSchema;
    
    @FXML
    private void createScheme(ActionEvent event) {
    	String nomeSchema = campoSchema.getText().trim();
    	
        if (nomeSchema.isEmpty()) {
            showToast("O nome do schema não pode estar vazio.");
            return;
        }

        // Validação adicional do nome do schema
        if (!nomeSchema.matches("[a-zA-Z0-9_]+")) {
            showToast("Nome do schema inválido. Apenas letras, números e underscores são permitidos.");
            return;
        }
        
    	Schema_Table_DAO_Imp schema = new Schema_Table_DAO_Imp();
    	schema.createSchema(conexao, nomeSchema)    	
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
	            showToast("Error ao criar schema");
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
