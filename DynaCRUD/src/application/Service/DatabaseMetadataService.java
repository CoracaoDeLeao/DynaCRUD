package application.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import application.Model.ColumnMetadata;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseMetadataService {

    /**
     * Método para obter uma lista de schemas (bancos de dados) disponíveis na conexão fornecida.
     * O método ignora schemas padrão do sistema, como 'information_schema' e 'mysql'.
     *
     * @param conexao conexão com o banco de dados
     * @return CompletableFuture que retorna uma ObservableList com os nomes dos schemas
     */
    public CompletableFuture<ObservableList<String>> getSchemas(Connection conexao) {
        return CompletableFuture.supplyAsync(() -> {
            ObservableList<String> schemas = FXCollections.observableArrayList();
            try {
                // Obtém os schemas disponíveis no banco de dados
                ResultSet schemasBrute = conexao.getMetaData().getCatalogs();

                // Lista de schemas padrão que queremos ignorar
                List<String> ignoredSchemas = Arrays.asList("information_schema", "mysql", "performance_schema", "sys");

                while (schemasBrute.next()) {
                    String schemaName = schemasBrute.getString(1); // O nome do schema está na primeira coluna

                    // Se o schema não estiver na lista de schemas ignorados, adiciona à lista
                    if (!ignoredSchemas.contains(schemaName)) {
                        schemas.add(schemaName);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return schemas;
        });
    }

    /**
     * Método para obter uma lista de tabelas em um schema específico.
     *
     * @param conexao conexão com o banco de dados
     * @param schema nome do schema para obter as tabelas
     * @return CompletableFuture que retorna uma ObservableList com os nomes das tabelas
     */
    public CompletableFuture<ObservableList<String>> getTables(Connection conexao, String schema) {
        return CompletableFuture.supplyAsync(() -> {
            ObservableList<String> tables = FXCollections.observableArrayList();
            try {
                // Obtém os metadados do banco de dados através da conexão
                DatabaseMetaData metaData = conexao.getMetaData();

                // Obtendo as tabelas do schema especificado
                ResultSet tablesBrute = metaData.getTables(schema, null, null, new String[]{"TABLE"});

                if (schema == null) {
                    tables.clear(); // Limpa todos os itens da ObservableList se o schema for null
                } else {
                    while (tablesBrute.next()) {
                        String nomeTabela = tablesBrute.getString("TABLE_NAME"); // Obtendo o nome da tabela
                        tables.add(nomeTabela);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return tables;
        });
    }

    /**
     * Método para obter informações das colunas de uma tabela em um schema específico.
     *
     * @param conexao conexão com o banco de dados
     * @param schema nome do schema
     * @param table nome da tabela
     * @return CompletableFuture que retorna uma ObservableList de objetos ColumnMetadata
     */
    public CompletableFuture<ObservableList<ColumnMetadata>> getColumns(Connection conexao, String schema, String table) {
        return CompletableFuture.supplyAsync(() -> {
            ObservableList<ColumnMetadata> column = FXCollections.observableArrayList();

            // Consulta SQL para obter os nomes das colunas, tipos, tamanhos e se são chaves primárias
            String sqlColumns = "SELECT C.COLUMN_NAME, C.DATA_TYPE, C.CHARACTER_MAXIMUM_LENGTH, " +
                                "       CASE WHEN K.COLUMN_NAME IS NOT NULL THEN 'PRIMARY KEY' ELSE '' END AS PRIMARY_KEY " +
                                "FROM INFORMATION_SCHEMA.COLUMNS C " +
                                "LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE K " +
                                "  ON C.TABLE_SCHEMA = K.TABLE_SCHEMA " +
                                " AND C.TABLE_NAME = K.TABLE_NAME " +
                                " AND C.COLUMN_NAME = K.COLUMN_NAME " +
                                "WHERE C.TABLE_SCHEMA = ? AND C.TABLE_NAME = ?";

            try (PreparedStatement preparedStatement = conexao.prepareStatement(sqlColumns)) {
                preparedStatement.setString(1, schema);
                preparedStatement.setString(2, table);

                try (ResultSet columns = preparedStatement.executeQuery()) {

                    // Processar o ResultSet e adicionar colunas dinamicamente à lista
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String columnType = columns.getString("DATA_TYPE");
                        int columnSize = columns.getInt("CHARACTER_MAXIMUM_LENGTH");
                        String primaryKey = columns.getString("PRIMARY_KEY");

                        // Adicionar uma nova instância de ColumnMetadata à lista
                        column.add(new ColumnMetadata(columnName, columnType, columnSize, primaryKey));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return column;
        });
    }

    /**
     * Método para obter registros de uma tabela em um schema específico.
     *
     * @param conexao conexão com o banco de dados
     * @param schema nome do schema
     * @param table nome da tabela
     * @return CompletableFuture que retorna uma ObservableList com os registros como Map
     */
    public CompletableFuture<ObservableList<Map<String, Object>>> getRecords(Connection conexao, String schema, String table) {
        return CompletableFuture.supplyAsync(() -> {
            // Consulta SQL para selecionar todos os registros da tabela especificada
            String sql = "SELECT * FROM " + schema + "." + table;

            try (Statement statement = conexao.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                // Obter os metadados da consulta para recuperar os nomes das colunas
                ResultSetMetaData rsMetaData = resultSet.getMetaData();
                int columnCount = rsMetaData.getColumnCount();

                // Lista observável para armazenar os dados da tabela
                ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();

                // Processar cada linha de dados do ResultSet
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();

                    // Preencher o Map com os valores das colunas
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rsMetaData.getColumnName(i);
                        Object value = resultSet.getObject(i);

                        row.put(columnName, value); // Adiciona o valor ao Map
                    }

                    // Adicionar a linha de dados à lista observável
                    data.add(row);
                }

                return data;

            } catch (SQLException e) {
                e.printStackTrace();
                return FXCollections.observableArrayList(); // Retorna lista vazia em caso de erro
            }
        });
    }

}