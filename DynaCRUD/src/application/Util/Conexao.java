package application.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class Conexao {

	private static Conexao instance; // Instância Singleton
	private Connection conexao;
	private String URL;
	private String USUARIO;
	private String SENHA;
	
	/**
	 * @param IP
	 * @param PORTA
	 * @param USER
	 * @param PASS
	 */
	public Conexao(String IP, String PORTA, String USER, String PASS) {
		URL = "jdbc:mysql://" + IP + ":" + PORTA + "/";
		USUARIO = USER;
		SENHA = PASS;
	}
	
    /**
	 * @return the conexao
	 */
	public Connection getConexao() {
		return conexao;
	}

	/**
     * @param IP
	 * @param PORTA
	 * @param USER
	 * @param PASS
	 * Método estático para obter a instância Singleton
     */
    public static Conexao getInstance(String IP, String PORTA, String USER, String PASS) {
        if (instance == null) {
            instance = new Conexao(IP, PORTA, USER, PASS);
        }
        return instance;
    }

	/**
	 * @param Base
	 */
	public void setBase(String Base) {
		this.URL += Base;
	}

	// Método para conectar ao banco
	public Optional<Connection> conectar() {
		try {
			if (conexao == null || conexao.isClosed()) {

				// Carrega o driver JDBC do MySQL (você deve ter o JAR do driver no seu
				// classpath)
				Class.forName("com.mysql.cj.jdbc.Driver");
	
				// Estabelece a conexão com o banco de dados
				conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
				System.out.println("Conectou ao banco de dados.");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Erro: Não encontrou o driver do BD!");
			e.printStackTrace();
			return Optional.empty();
		} catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
            return Optional.empty();
        }
		
		return Optional.of(conexao);
	}

	// Método para desconectar do banco
	public void desconectar() {
       try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Desconectou do banco de dados!");
            }
        } catch (SQLException e) {
            System.out.println("Não conseguiu desconectar do BD!");
            e.printStackTrace(); 
        }
	}
	

	// Método para verificar se a conexão está ativa
	public boolean isConectado() {
		try {
	        if (conexao == null) {
	            System.out.println("A conexão não foi estabelecida."); // Tratamento para conexão nula
	            return false;
	        } else if (conexao.isClosed()) {
	            System.out.println("A conexão foi fechada."); // Tratamento para conexão fechada
	            return false;
	        } else {
	            System.out.println("A conexão está ativa.");
	            return true;
	        }
		} catch (SQLException e) {
			 System.out.println("Erro ao verificar o estado da conexão.");
			e.printStackTrace();
			return false;
		}
	}
}
