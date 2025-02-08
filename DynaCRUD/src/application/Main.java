package application;

import java.sql.Connection;
import java.util.Map;

import application.Controller.CRUD_GUI_Controller;
import application.Controller.ConexaoGUI_Controller;
import application.Controller.Modal_Create_Record_Controller;
import application.Controller.Modal_Create_Schema_Controller;
import application.Controller.Modal_Create_Table_Controller;
import application.Controller.Modal_Update_Record_Controller;
import application.Util.Conexao;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class Main extends Application {
	
	private static Main instance;
	
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();  // Cria a instância se ainda não foi criada
        }
        return instance;
    }
	
	@Override
	public void start(Stage stage) {
		try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/View/ConexaoGUI.fxml"));
	        Parent root = loader.load();
	        stage.setTitle("DynaCRUD");	
	        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/crud.png")));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			
			if (stage.isShowing()) {
			    Platform.runLater(() -> centralizarJanela(stage));
			} else {
				stage.setOnShown(event -> {
			        centralizarJanela(stage); // Centraliza a janela após mostrar
				});
			}
			
			stage.show();
			
	        // Passa o Stage para o Controller
	        ConexaoGUI_Controller conn_gui_controller = loader.getController();
	        conn_gui_controller.setStage(stage); // Agora o controller tem acesso ao Stage
						
		} catch(Exception e) {
			exibirErro(e);  // Exibe a mensagem de erro
		}
	}
	
	/**
     * @param stage
	 * @param conexao
	 * Método para fazer a transição de telas
     */
	public void openDataBaseWindow(Stage stage, Conexao conexao, String campoUSER, String campoIP, String campoPORTA) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/View/CRUD_GUI.fxml"));
            Parent root = loader.load();
            
            // Passando o objeto 'conexao' para o controlador
            CRUD_GUI_Controller crud_gui_controller = loader.getController();
            crud_gui_controller.setConstructor(conexao, stage, campoUSER, campoIP, campoPORTA);  // Passa a instância de Conexao para o controlador
            
	        stage.setTitle("DynaCRUD");
	        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/crud.png")));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			
			if (stage.isShowing()) {
			    Platform.runLater(() -> centralizarJanela(stage));
			} else {
				stage.setOnShown(event -> {
			        centralizarJanela(stage); // Centraliza a janela após mostrar
				});
			}
			
			stage.show();
				        
	        // Adiciona o listener para o evento de fechamento
	        stage.setOnCloseRequest(event -> {
	            conexao.desconectar();
	        });
	        
		} catch(Exception e) {
			exibirErro(e);  // Exibe a mensagem de erro
		}
    }
	
	
	public void openModalCreateSchema(Connection conn, Runnable onCloseCallback) {
	    try {
	        // Carrega o arquivo FXML do modal
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/View/Modal_Create_Schema.fxml"));
	        Parent root = loader.load();  // Carrega o FXML primeiro
	     	        
	        Stage modal = new Stage();
	        Scene modalScene = new Scene(root);
	        
	        // Obtém o controlador do FXML após o carregamento
	        Modal_Create_Schema_Controller controller = loader.getController();
	        controller.setConstructor(conn, modal, onCloseCallback);  // Passa a conexão ao controlador
	        
	        
	        modal.setTitle("Criar schema");
	        modal.getIcons().add(new Image(getClass().getResourceAsStream("/resources/crud.png")));
	        
	        // Configura que a janela é modal
	        modal.initModality(Modality.APPLICATION_MODAL);

	        // Define a cena com o conteúdo carregado
	        modal.setScene(modalScene);
	        
	        // Centraliza ao exibir
	        modal.setOnShown(event -> centralizarJanela(modal));

	        // Centraliza e exibe o modal
	        modal.showAndWait();
	     	        
	    } catch (Exception e) {
	        exibirErro(e);  // Exibe erros
	    }
	}
	
	public void openModalCreateTable(Connection conn, String schema,Runnable onCloseCallback) {
	    try {
	        // Carrega o arquivo FXML do modal
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/View/Modal_Create_Table.fxml"));
	        Parent root = loader.load();  // Carrega o FXML primeiro
	     	        
	        Stage modal = new Stage();
	        Scene modalScene = new Scene(root);
	        
	        // Obtém o controlador do FXML após o carregamento
	        Modal_Create_Table_Controller controller = loader.getController();
	        controller.setConstructor(conn, modal, schema, onCloseCallback);  // Passa a conexão ao controlador
	        
	        modal.setTitle("Criar tabela");
	        modal.getIcons().add(new Image(getClass().getResourceAsStream("/resources/crud.png")));
	        
	        // Configura que a janela é modal
	        modal.initModality(Modality.APPLICATION_MODAL);

	        // Define a cena com o conteúdo carregado
	        modal.setScene(modalScene);
	        
	        // Centraliza ao exibir
	        modal.setOnShown(event -> centralizarJanela(modal));

	        // Centraliza e exibe o modal
	        modal.showAndWait();
	     	        
	    } catch (Exception e) {
	        exibirErro(e);  // Exibe erros
	    }
	}
	
	public void openModalCreateRecord(Connection conn, String schema, String table, Runnable onCloseCallback) {
	    try {
	        // Carrega o arquivo FXML do modal
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/View/Modal_Create_Record.fxml"));
	        Parent root = loader.load();  // Carrega o FXML primeiro
	     	        
	        Stage modal = new Stage();
	        Scene modalScene = new Scene(root);
	        
	        // Obtém o controlador do FXML após o carregamento
	        Modal_Create_Record_Controller controller = loader.getController();
	        controller.setConstructor(conn, modal, schema, table, onCloseCallback);  // Passa a conexão ao controlador
	        
	        modal.setTitle("Criar registro na tabela " + table);
	        modal.getIcons().add(new Image(getClass().getResourceAsStream("/resources/crud.png")));

	        // Configura que a janela é modal
	        modal.initModality(Modality.APPLICATION_MODAL);

	        // Define a cena com o conteúdo carregado
	        modal.setScene(modalScene);
	        
	        // Centraliza ao exibir
	        modal.setOnShown(event -> centralizarJanela(modal));

	        // Centraliza e exibe o modal
	        modal.showAndWait();
	     	        
	    } catch (Exception e) {
	        exibirErro(e);  // Exibe erros
	    }
	}
	
	public void openModalUpdateRecord(Connection conn, String schema, String table, Map<String, Object> selectedItem, Runnable onCloseCallback) {
	    try {
	        // Carrega o arquivo FXML do modal
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/View/Modal_Update_Record.fxml"));
	        Parent root = loader.load();  // Carrega o FXML primeiro
	     	        
	        Stage modal = new Stage();
	        Scene modalScene = new Scene(root);
	        
	        // Obtém o controlador do FXML após o carregamento
	        Modal_Update_Record_Controller controller = loader.getController();
	        controller.setConstructor(conn, modal, schema, table, selectedItem, onCloseCallback);  // Passa a conexão ao controlador
	        
	        modal.setTitle("Atualizar registro na tabela " + table);
	        modal.getIcons().add(new Image(getClass().getResourceAsStream("/resources/crud.png")));

	        // Configura que a janela é modal
	        modal.initModality(Modality.APPLICATION_MODAL);

	        // Define a cena com o conteúdo carregado
	        modal.setScene(modalScene);
	        
	        // Centraliza ao exibir
	        modal.setOnShown(event -> centralizarJanela(modal));

	        // Centraliza e exibe o modal
	        modal.showAndWait();
	     	        
	    } catch (Exception e) {
	        exibirErro(e);  // Exibe erros
	    }
	}
	
	private void centralizarJanela(Stage stage) {
	    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	    
	    double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
	    double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;
	    
	    stage.setX(centerX);
	    stage.setY(centerY);
	}
    
    private void exibirErro(Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro ao iniciar o aplicativo:");
        alert.setContentText(e.getMessage());  // Mostra a mensagem do erro
        alert.showAndWait();
        System.out.println("Ocorreu um erro ao iniciar o aplicativo:\n");
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
