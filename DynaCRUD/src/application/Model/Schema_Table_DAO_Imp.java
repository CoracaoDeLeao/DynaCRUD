package application.Model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Schema_Table_DAO_Imp implements Schema_Table_DAO {
	
	@Override
	public CompletableFuture<Void> createSchema(Connection conn, String nomeSchema) {
	    return CompletableFuture.runAsync(() -> {
	        if (!nomeSchema.matches("[a-zA-Z0-9_]+")) {
	            Platform.runLater(() -> exibirErro(new IllegalArgumentException("Nome do schema inválido: " + nomeSchema)));
	            throw new IllegalArgumentException("Nome do schema inválido.");
	        }

	        String sql = "CREATE SCHEMA " + nomeSchema;

	        try (Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate(sql);
	            Platform.runLater(() -> System.out.println("Schema '" + nomeSchema + "' criado com sucesso!"));
	        } catch (SQLException e) {
	            Platform.runLater(() -> exibirErro(e));
	            throw new RuntimeException("Erro ao criar o schema: " + e.getMessage(), e);
	        }
	    });
	}

	@Override
	public CompletableFuture<Void> deleteSchema(Connection conn, String nomeSchema, Runnable callback) {
	    return CompletableFuture.runAsync(() -> {
	        if (!nomeSchema.matches("[a-zA-Z0-9_]+")) {
	            Platform.runLater(() -> exibirErro(new IllegalArgumentException("Nome do schema inválido: " + nomeSchema)));
	            throw new IllegalArgumentException("Nome do schema inválido.");
	        }

	        String sql = "DROP SCHEMA " + nomeSchema;

	        try (Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate(sql);
	            Platform.runLater(() -> {
	                System.out.println("Schema '" + nomeSchema + "' deletado com sucesso!");
	                if (callback != null) callback.run();
	            });
	        } catch (SQLException e) {
	            Platform.runLater(() -> exibirErro(e));
	            throw new RuntimeException("Erro ao deletar o schema: " + e.getMessage(), e);
	        }
	    });
	}

	@Override
	public CompletableFuture<Void> createTable(Connection conn, String nomeSchema, String nomeTabela, String colunasSQL) {
	    return CompletableFuture.runAsync(() -> {
	        if (!nomeTabela.matches("[a-zA-Z0-9_]+") || !colunasSQL.matches("[a-zA-Z0-9_,()\\s]+")) {
	            Platform.runLater(() -> exibirErro(new IllegalArgumentException("Nome da tabela ou colunas inválidos.")));
	            throw new IllegalArgumentException("Nome da tabela ou colunas inválidos.");
	        }

	        String sql = "CREATE TABLE " + nomeSchema + "." + nomeTabela + " (" + colunasSQL + ")";

	        try (Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate(sql);
	            Platform.runLater(() -> System.out.println("Tabela '" + nomeTabela + "' criada com sucesso!"));
	        } catch (SQLException e) {
	            Platform.runLater(() -> exibirErro(e));
	            throw new RuntimeException("Erro ao criar a tabela: " + e.getMessage(), e);
	        }
	    });
	}

	@Override
	public CompletableFuture<Void> deleteTable(Connection conn, String nomeSchema, String nomeTabela, Runnable callback) {
	    return CompletableFuture.runAsync(() -> {
	        if (!nomeTabela.matches("[a-zA-Z0-9_]+")) {
	            Platform.runLater(() -> exibirErro(new IllegalArgumentException("Nome da tabela inválido: " + nomeTabela)));
	            throw new IllegalArgumentException("Nome da tabela inválido.");
	        }

	        String sql = "DROP TABLE " + nomeSchema + "." + nomeTabela + " CASCADE";

	        try (Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate(sql);
	            Platform.runLater(() -> {
	                System.out.println("Tabela '" + nomeTabela + "' deletada com sucesso!");
	                if (callback != null) callback.run();
	            });
	        } catch (SQLException e) {
	            Platform.runLater(() -> exibirErro(e));
	            throw new RuntimeException("Erro ao deletar a tabela: " + e.getMessage(), e);
	        }
	    });
	}
    
	private void exibirErro(Exception e) {
	    // Criação do alerta de erro para exibição no frontend
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Erro");
	    alert.setHeaderText("Ocorreu um erro:");
	    alert.setContentText(e.getMessage());  // Exibe a mensagem do erro
	    alert.showAndWait();

	    // Log do erro no console
	    System.err.println("Ocorreu um erro:\n");
	    e.printStackTrace();
	}
}
