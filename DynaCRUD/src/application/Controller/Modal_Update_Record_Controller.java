package application.Controller;

import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import application.Model.ColumnMetadata;
import application.Model.RecordDAO_Imp;
import application.Service.DatabaseMetadataService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Modal_Update_Record_Controller implements Initializable{
	
    private Connection conexao;
    private String schema;
    private String table;
    private Stage modalStage;
    private Popup popup;
    private Runnable onCloseCallback;
    private Map<String, TextField> fields = new HashMap<>();
    private Map<String, Object> selectedItem = new HashMap<>();
    
    public void setConstructor(Connection conn, Stage modal, String schema, String table, Map<String, Object> selectedItem, Runnable onCloseCallback) {
        this.conexao = conn;
        this.modalStage = modal;
        this.schema = schema;
        this.table = table;
        this.selectedItem = selectedItem;
        this.onCloseCallback = onCloseCallback;
        consulta();
	}
    
    @FXML // fx:id="rootPane"
    private AnchorPane rootPane;
    
    @FXML // fx:id="scrollPane"
    private ScrollPane scrollPane;
    
    @FXML // fx:id="borderPane"
    private BorderPane borderPane;
    
    @FXML
    public void updateRecord(){
        RecordDAO_Imp record = new RecordDAO_Imp();
        // Chamando o método assíncrono e aguardando a execução
        record.updateRecord(conexao, schema, table, fields, selectedItem)
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
                // Código a ser executado em caso de erro
            	showToast("Erro ao inserir o registro");
                System.err.println("Erro ao inserir o registro: " + ex.getMessage());
                return null;
            });
    }
    
    private void consulta() {
    	GridPane gridPane = new GridPane();
    	gridPane.setHgap(1);
    	gridPane.setVgap(15);
    	
    	for (int i = 0; i < 4; i++) {
	    	ColumnConstraints columnConstraints = new ColumnConstraints();
	    	columnConstraints.setMinWidth(100);  // Largura mínima
	    	columnConstraints.setPrefWidth(200); // Largura preferida
	    	columnConstraints.setHgrow(Priority.ALWAYS); // Expansão proporcional
	        gridPane.getColumnConstraints().add(columnConstraints);
    	}
        
        DatabaseMetadataService service = new DatabaseMetadataService();
        
        // Obtém as colunas da tabela de forma assíncrona
        service.getColumns(conexao, schema, table)
		       .thenAccept(columns -> {
		    	   Platform.runLater(() -> {
			            int row = 0;
			            for (ColumnMetadata column : columns) {
			                // Define o título da coluna, incluindo o tipo, tamanho e se é chave primária
			                StringBuilder columnTitle = new StringBuilder("[" + column.getType());

			                // Adiciona o tamanho da coluna se for diferente de zero
			                if (column.getSize() != 0) {
			                    columnTitle.append("(").append(column.getSize()).append(")");
			                }

			                // Adiciona a informação de chave primária ao título, se for aplicável
			                if ("PRIMARY KEY".equalsIgnoreCase(column.getPrimaryKey())) {
			                    columnTitle.append("(PK)");
			                }

			                // Fecha os colchetes e adiciona o nome da coluna
			                columnTitle.append("] ").append(column.getName());
			                
			                Label label = new Label(columnTitle.toString() + ":");
			                TextField textField = new TextField();
			                
			                // Verifica se há valor correspondente no registro selecionado
			                Object value = selectedItem != null ? selectedItem.get(column.getName()) : null;
			                textField.setText(value != null ? value.toString() : "");
			
			                fields.put(column.getName(), textField);
			
			                gridPane.add(label, 1, row);
			                gridPane.add(textField, 2, row);
			                row++;
			            }
			            
			            borderPane.setCenter(gridPane);
			            scrollPane.setContent(borderPane);
		                scrollPane.setFitToWidth(true);
		                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
			            
		                // Obtém a largura e altura da janela
		                double windowWidth = rootPane.getWidth();
		                double windowHeight = rootPane.getHeight();

		                // Ajuste de proporção do ScrollPane
		                scrollPane.setPrefWidth(windowWidth);  
		                scrollPane.setPrefHeight(windowHeight);
		    	   });
			    })
		        .exceptionally(ex -> {
		            ex.printStackTrace(); 
		            showToast("Error ao criar registro");
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
