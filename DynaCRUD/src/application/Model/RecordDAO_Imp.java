package application.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class RecordDAO_Imp implements RecordDAO{

	@Override
	public CompletableFuture<Void> createRecord(Connection conn, String schema, String tableName, Map<String, TextField> fields) {
	    return CompletableFuture.runAsync(() -> {
	    	String tableNameWithSchema = schema + "." + tableName;
	        StringBuilder sql = new StringBuilder("INSERT INTO " + tableNameWithSchema + " (");
	        StringBuilder values = new StringBuilder("VALUES (");

	        for (String column : fields.keySet()) {
	            sql.append(column).append(", ");
	            values.append("?").append(", ");
	        }

	        sql.setLength(sql.length() - 2);  // Remove a última vírgula
	        values.setLength(values.length() - 2);  // Remove a última vírgula

	        sql.append(") ").append(values).append(");");

	        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
	            int index = 1;
	            for (TextField field : fields.values()) {
	                stmt.setString(index++, field.getText());
	            }

	            stmt.executeUpdate();
	            // Exibir a mensagem de sucesso de forma segura (em thread principal)
	            System.out.println("Registro salvo com sucesso!");
	        } catch (SQLException ex) {
	            // Exibir a mensagem de erro de forma segura (em thread principal)
	            Platform.runLater(() -> {
	                System.err.println("Erro ao criar o registro: " + ex.getMessage());
	                exibirMensagem(Alert.AlertType.ERROR, "Erro ao salvar!");
	            });
	        }
	    });
	}

	
	@Override
	public CompletableFuture<Void> updateRecord(Connection conn, String schema, String tableName, Map<String, TextField> fields, Map<String, Object> selectedItem) {
	    return CompletableFuture.runAsync(() -> {
	        try {
	            // Obtemos a chave primária da tabela dinamicamente
	            String primaryKeyColumn = getPrimaryKeyColumn(conn, schema, tableName);

	            if (primaryKeyColumn == null) {
	                throw new SQLException("Não foi possível identificar a chave primária da tabela.");
	            }

	            // Obtém o valor da chave primária do item selecionado
	            Object id = selectedItem.get(primaryKeyColumn);

	            if (id == null) {
	                throw new IllegalArgumentException("Registro inválido!");
	            }

	            // Monta a consulta de atualização
	            StringBuilder sql = new StringBuilder("UPDATE ").append(schema).append(".").append(tableName).append(" SET ");

	            for (String column : fields.keySet()) {
	                sql.append(column).append(" = ?, ");
	            }

	            sql.setLength(sql.length() - 2);  // Remove a última vírgula
	            sql.append(" WHERE ").append(primaryKeyColumn).append(" = ?");

	            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
	                int index = 1;
	                for (TextField field : fields.values()) {
	                    // Verifica se o campo está vazio e define como null
	                    if (field.getText().isEmpty()) {
	                        stmt.setNull(index++, java.sql.Types.NULL);
	                    } else {
	                        stmt.setString(index++, field.getText());
	                    }
	                }
	                stmt.setObject(index, id);  // Define o ID como parâmetro na consulta

	                int rowsAffected = stmt.executeUpdate();
	                if (rowsAffected > 0) {
	                    Platform.runLater(() -> {
	                        System.out.println("Registro atualizado com sucesso!");
	                    });
	                } else {
	                    throw new SQLException("Nenhum registro foi atualizado.");
	                }
	            }
	        } catch (SQLException | IllegalArgumentException ex) {
	            Platform.runLater(() -> {
	                System.err.println("Erro ao atualizar o registro: " + ex.getMessage());
	                exibirMensagem(Alert.AlertType.ERROR, "Erro ao atualizar o registro.");
	            });
	        }
	    });
	}

	@Override
	public CompletableFuture<Void> deleteRecord(Connection conn, String schema, String tableName, Map<String, Object> selectedItem, Runnable callback) {
	    return CompletableFuture.runAsync(() -> {
	        try {
	            // Obtemos a chave primária da tabela dinamicamente
	            String primaryKeyColumn = getPrimaryKeyColumn(conn, schema, tableName);

	            if (primaryKeyColumn == null) {
	                throw new SQLException("Não foi possível identificar a chave primária da tabela.");
	            }

	            // Obtém o valor da chave primária do item selecionado
	            Object id = selectedItem.get(primaryKeyColumn);

	            if (id == null) {
	                throw new IllegalArgumentException("Registro inválido!");
	            }

	            // Criação da consulta DELETE
	            String sql = "DELETE FROM " + schema + "." + tableName + " WHERE " + primaryKeyColumn + " = ?";

	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                stmt.setObject(1, id);  // Define o ID como parâmetro na consulta
	                int rowsAffected = stmt.executeUpdate();

	                Platform.runLater(() -> {
	                    if (rowsAffected > 0) {
	                        System.out.println("Registro removido com sucesso!");
	                        if (callback != null) {
	                            callback.run();
	                        }
	                    } else {
	                        exibirMensagem(Alert.AlertType.ERROR, "Falha ao excluir o registro.");
	                    }
	                });
	            }
	        } catch (SQLException | IllegalArgumentException ex) {
	            Platform.runLater(() -> {
	                System.err.println("Erro ao deletar o registro: " + ex.getMessage());
	                exibirMensagem(Alert.AlertType.ERROR, "Erro ao excluir o registro.");
	            });
	        }
	    });
	}

	private String getPrimaryKeyColumn(Connection conn, String schema, String tableName) throws SQLException {
	    // Consulta SQL para obter o nome da coluna da chave primária
	    String sql = "SELECT COLUMN_NAME " +
	                 "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
	                 "WHERE TABLE_SCHEMA = ? " +
	                 "AND TABLE_NAME = ? " +
	                 "AND CONSTRAINT_NAME = 'PRIMARY'";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        // Configura os parâmetros da consulta
	        stmt.setString(1, schema);
	        stmt.setString(2, tableName);

	        // Executa a consulta
	        ResultSet resultSet = stmt.executeQuery();

	        // Verifica se há resultados
	        if (resultSet.next()) {
	        	return resultSet.getString("COLUMN_NAME");
	        }
	    }

	    // Caso não encontre chave primária
	    return null;
	}

	private void exibirMensagem(Alert.AlertType tipo, String mensagem) {
	    // Exibe o alerta na interface do usuário
	    Alert alert = new Alert(tipo, mensagem);
	    alert.setTitle(tipo == Alert.AlertType.ERROR ? "Erro" : "Sucesso");
	    alert.showAndWait();
	}
}
