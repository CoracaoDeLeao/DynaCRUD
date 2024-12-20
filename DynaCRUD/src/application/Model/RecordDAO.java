package application.Model;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.TextField;

public interface RecordDAO {
	
	CompletableFuture<Void> createRecord(Connection conn, String schema, String tableName, Map<String, TextField> fields);
	CompletableFuture<Void> updateRecord(Connection conn, String schema, String tableName, Map<String, TextField> fields, Map<String, Object> selectedItem);
	CompletableFuture<Void> deleteRecord(Connection conn, String schema, String tableName, Map<String, Object> selectedItem, Runnable callback);

}
