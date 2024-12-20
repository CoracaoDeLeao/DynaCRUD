package application.Model;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

public interface Schema_Table_DAO {
	
	CompletableFuture<Void> createSchema(Connection conn, String nomeSchema);
	CompletableFuture<Void> deleteSchema(Connection conn, String nomeSchema, Runnable callback);
	CompletableFuture<Void> createTable(Connection conn, String nomeSchema, String nomeTabela, String colunasSQL);
	CompletableFuture<Void> deleteTable(Connection conn, String nomeSchema, String nomeTabela, Runnable callback);

}
