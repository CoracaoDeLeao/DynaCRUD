package application.Controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import application.Service.DatabaseMetadataService;
import application.Util.Conexao;
import application.Main;
import application.Model.ColumnMetadata;
import application.Model.RecordDAO_Imp;
import application.Model.Schema_Table_DAO_Imp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CRUD_GUI_Controller implements Initializable {
	
    private Conexao conexao;
	private Stage stage;
    private Popup popup;

    public void setConstructor(Conexao conn, Stage stage, String campoUSER, String campoIP, String campoPORTA) {
        this.conexao = conn;
        this.stage = stage;
        labelIP.setText(campoIP);
        labelUser.setText(campoUSER);
        labelPort.setText(campoPORTA);
        schemas();
    }
    
	@FXML // fx:id="rootPane"
    private AnchorPane rootPane;
	
	@FXML // fx:id="labelIP"
    private Text labelIP;
	
	@FXML // fx:id="labelPort"
    private Text labelPort;
	
	@FXML // fx:id="labelUser"
    private Text labelUser;

	@FXML // fx:id="combobox"
    private ComboBox<String> comboBox;
    
    @FXML // fx:id="listView"
    private ListView<String> listView;
    
    @FXML // fx:id="tableView"
    private TableView<Map<String, Object>> tableView;
  
    // Método para limpar a seleção do ComboBox
    // TODO Implementar metodo para deletar schema
    @FXML
    private void clearComboBoxSelection(ActionEvent event) {
    	String schema = comboBox.getValue();
    	
    	if(schema == null || schema.equals("Schemas")) {
    		showToast("Selecione um schema válido");
    		return;
    	}
        
    	Schema_Table_DAO_Imp deleteSchema = new Schema_Table_DAO_Imp();
        deleteSchema.deleteSchema(conexao.getConexao(), schema, () -> {
        	comboBox.setValue(null); // Limpa a seleção
        	schemas();
    	});
	}
    
    // Método para limpar a seleção do ListView
    // TODO Implementar metodo para deletar tabela
    @FXML
    private void clearListViewSelection(ActionEvent event) {
    	String schema = comboBox.getValue();
    	String tabela = listView.getSelectionModel().getSelectedItem();
    	
    	if(schema == null || schema.equals("Schemas")) {
    		showToast("Selecione um schema válido");
    		return;
    	}
    	
    	if(tabela == null) {
    		showToast("Selecione uma tabela válida");
    		return;
    	}
    	
    	Schema_Table_DAO_Imp deleteTable = new Schema_Table_DAO_Imp();
        deleteTable.deleteTable(conexao.getConexao(), schema, tabela, () -> {
        	tables(schema);
        	listView.getSelectionModel().clearSelection(); // Limpa a seleção
        });
    }
    
    // Método para abir a janela da conexão
    @FXML
    public void openConexaoWindow(ActionEvent event) {
    	conexao.desconectar();
    	Main main = Main.getInstance();
    	main.start(stage);
    }
    
    // Método para abir o modal criar schema
    @FXML
    public void openModalCreateSchema(ActionEvent event) {
    	Main main = Main.getInstance();
    	main.openModalCreateSchema(conexao.getConexao(), () -> schemas());
    }
    
    // Método para abir o modal criar schema
    @FXML
    public void openModalCreateTable(ActionEvent event) {
    	String schema = comboBox.getValue();
    	
    	if(schema == null || schema.equals("Schemas")) {
    		showToast("Selecione um schema válido");
    		return;
    	}
    	
    	Main main = Main.getInstance();
    	main.openModalCreateTable(conexao.getConexao(), schema, () -> tables(schema));
    }
    
    // Método para abir o modal criar registro
    @FXML
    public void openModalCreateRecord(ActionEvent event) {
    	String schema = comboBox.getValue();
    	String tabela = listView.getSelectionModel().getSelectedItem();
    	
    	if(schema == null || schema.equals("Schemas")) {
    		showToast("Selecione um schema válido");
    		return;
    	}
    	
    	if(tabela == null) {
    		showToast("Selecione uma tabela válida");
    		return;
    	}
    	
    	Main main = Main.getInstance();
    	main.openModalCreateRecord(conexao.getConexao(), schema, tabela, () -> records(schema, tabela));
    }
    
    // Método para abir o modal atualizar registro
    @FXML
    public void openModalUpdateRecord(ActionEvent event){
    	String schema = comboBox.getValue();
    	String tabela = listView.getSelectionModel().getSelectedItem();
    	
    	if(schema == null || schema.equals("Schemas")) {
    		showToast("Selecione um schema válido");
    		return;
    	}
    	
    	if(tabela == null) {
    		showToast("Selecione uma tabela válida");
    		return;
    	}
    	
        Map<String, Object> selectedItem = tableView.getSelectionModel().getSelectedItem();
        
        if (selectedItem == null) {
        	showToast("Selecione um registro para atualizar");
            return;
        }
    	
    	Main main = Main.getInstance();
    	main.openModalUpdateRecord(conexao.getConexao(), schema, tabela, selectedItem, () -> records(schema, tabela));
    }
    
    // Método para abir o modal deletar registro
    @FXML
    public void deleteRecord(ActionEvent event) {
    	String schema = comboBox.getValue();
    	String tabela = listView.getSelectionModel().getSelectedItem();
    	
    	if(schema == null || schema.equals("Schemas")) {
    		showToast("Selecione um schema válido");
    		return;
    	}
    	
    	if(tabela == null) {
    		showToast("Selecione uma tabela válida");
    		return;
    	}
    	
        Map<String, Object> selectedItem = tableView.getSelectionModel().getSelectedItem();
        
        if (selectedItem == null) {
        	showToast("Selecione um registro para excluir");
            return;
        }
        
        RecordDAO_Imp deleteRecord = new RecordDAO_Imp();
        deleteRecord.deleteRecord(conexao.getConexao(), schema, tabela, selectedItem, () -> records(schema, tabela));
    }
    
    public void schemas() {
        // Cria uma instância do DAO para acessar os metadados do banco de dados
        DatabaseMetadataService dao = new DatabaseMetadataService();
        
        // Obtém a lista de schemas (bancos de dados) assincronamente
        dao.getSchemas(conexao.getConexao())
           .thenAccept(schemas -> {
        	   	// Exibe os schemas obtidos no console e adiciona ao ComboBox
				Platform.runLater(() -> {
					comboBox.getItems().clear();
					System.out.println("Schemas (Databases) disponíveis:");
					System.out.println(schemas);
					comboBox.getItems().addAll(schemas);
				});
           })
           .exceptionally(ex -> {
               // Caso ocorra algum erro na execução, ele será tratado aqui
               ex.printStackTrace(); 
               return null;
           });
    }

    public void tables(String schema) {
        // Cria uma instância do DAO para acessar as tabelas no schema fornecido
        DatabaseMetadataService dao = new DatabaseMetadataService();
        
        dao.getTables(conexao.getConexao(), schema)
           .thenAccept(tables -> {
               // Atualiza a ListView na thread da aplicação JavaFX
               Platform.runLater(() -> {
                   listView.getItems().clear();
                   System.out.println("Tabelas disponíveis no schema '" + schema + "':");
                   System.out.println(tables);
                   listView.getItems().addAll(tables);
               });
           })
           .exceptionally(ex -> {
               // Caso ocorra algum erro na execução, ele será tratado aqui
               ex.printStackTrace();
               return null;
           });
    }

    public void columns(String schema, String table) {
        // Cria uma instância do DAO para acessar as colunas da tabela fornecida
        DatabaseMetadataService dao = new DatabaseMetadataService();
        
        dao.getColumns(conexao.getConexao(), schema, table)
           .thenAccept(columns -> {
               // Atualiza a TableView na thread da aplicação JavaFX
               Platform.runLater(() -> {
                   // Limpa as colunas anteriores da TableView
                   tableView.getColumns().clear();

                   // Define a política de redimensionamento automático das colunas
                   tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
                   
                   // Define a largura das colunas
                   tableView.getColumns().forEach(col -> col.setPrefWidth(200));

                   System.out.println("Colunas da tabela '" + table + "':");

                   // Configura as colunas da TableView com base nos metadados das colunas
                   configureTableColumns(tableView, columns);
                   
                   // Ajusta a largura das colunas de acordo com o conteúdo
                   for (TableColumn<Map<String, Object>, ?> col : tableView.getColumns()) {
                       col.setMinWidth(200);
                       col.setMaxWidth(300);
                   }
               });
           })
           .exceptionally(ex -> {
               // Caso ocorra algum erro na execução, ele será tratado aqui
               ex.printStackTrace();
               return null;
           });
    }

    public void records(String schema, String table) {
        // Cria uma instância do DAO para acessar os registros da tabela fornecida
        DatabaseMetadataService dao = new DatabaseMetadataService();
        
        dao.getRecords(conexao.getConexao(), schema, table)
           .thenAccept(data -> {
				Platform.runLater(() -> {
					// Define a política de redimensionamento automático das colunas
					tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
					
					// Define os dados na TableView
					tableView.setItems(data);
					tableView.refresh();
				});
           })
           .exceptionally(ex -> {
               // Caso ocorra algum erro na execução, ele será tratado aqui
               ex.printStackTrace();
               return null;
           });
    }

    private void comboBoxListener() {
        // Adiciona um ChangeListener ao ComboBox para monitorar mudanças de seleção
        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    // Exibe o schema selecionado no console e carrega suas tabelas na ListView
                    System.out.println("Schema selecionado: " + newValue);
                    listView.getSelectionModel().clearSelection();
                    tables(newValue);
                } else {
                    comboBox.setValue(null);
                }
            }
        }); 
    }

    private void listViewListener() {
        // Adiciona um listener para capturar as seleções da ListView
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Exibe o item selecionado no console e atualiza a TableView com as colunas e registros
            System.out.println("Item selecionado: " + newValue);
            if (newValue == null) {
                tableView.getColumns().clear();
            } else {
                columns(comboBox.getValue(), newValue); // Obtém as colunas da tabela selecionada
                records(comboBox.getValue(), newValue); // Obtém os registros da tabela selecionada
            }
        });
    }

    private void configureTableColumns(TableView<Map<String, Object>> tableView, List<ColumnMetadata> columns) {
        // Itera sobre cada metadado de coluna para configurar as colunas dinamicamente na TableView
        for (ColumnMetadata columnMetadata : columns) {
            String columnName = columnMetadata.getName();
            String columnType = columnMetadata.getType();
            int columnSize = columnMetadata.getSize();
            String primaryKey = columnMetadata.getPrimaryKey();

            // Cria a coluna e define o título com base no tipo e tamanho
            TableColumn<Map<String, Object>, Object> tableColumn = new TableColumn<>();
            
            // Define o título da coluna, incluindo o tipo, tamanho e se é chave primária
            StringBuilder columnTitle = new StringBuilder("[" + columnType);

            // Adiciona o tamanho da coluna se for diferente de zero
            if (columnSize != 0) {
                columnTitle.append("(").append(columnSize).append(")");
            }

            // Adiciona a informação de chave primária ao título, se for aplicável
            if ("PRIMARY KEY".equalsIgnoreCase(primaryKey)) {
                columnTitle.append("(PK)");
            }

            // Fecha os colchetes e adiciona o nome da coluna
            columnTitle.append("] ").append(columnName);

            // Define o título da coluna
            tableColumn.setText(columnTitle.toString());

            // Configura a célula da coluna para exibir o valor correto do Map
            tableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnName)));

            // Adiciona a coluna configurada à TableView
            tableView.getColumns().add(tableColumn);

            // Exibe informações da coluna para depuração
            System.out.println("Nome: " + columnName + ", Tipo: " + columnType + ", Tamanho: " + columnSize);
        }
    }
    
    private void showToast(String mensagem) {
        Popup popup = new Popup();
        Label label = new Label(mensagem);
        label.setStyle("-fx-background-color: darkgray; -fx-text-fill: red; -fx-padding: 10px;");
        
        popup.getContent().add(label);

        // Obtém o Stage atual
        Stage stage = (Stage) rootPane.getScene().getWindow();

        // Exibe o popup primeiro para garantir que o Label tenha um tamanho definido
        popup.show(stage);

        // Aguarda a próxima iteração do JavaFX para calcular corretamente a posição
        Platform.runLater(() -> {
            double centerX = stage.getX() + (stage.getWidth() / 2) - (label.getWidth() / 2);
            double centerY = stage.getY() + (stage.getHeight() / 2) - (label.getHeight() / 2);
            
            // Atualiza a posição do Popup
            popup.setX(centerX);
            popup.setY(centerY);
        });

        // Fechar o popup após 3 segundos
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> popup.hide())
        );
        timeline.play();
    }

    
    public void closePopup() {
        if (popup != null) {
            popup.hide();  // Fecha o Popup
        }
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	   	comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(comboBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });
		
		comboBoxListener();
		listViewListener();
	}

}
