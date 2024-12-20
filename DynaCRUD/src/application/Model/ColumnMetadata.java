package application.Model;

public class ColumnMetadata {
    private String name;
    private String type;
    private int size;
    String primaryKey;
    
	/**
	 * @param name
	 * @param type
	 * @param size
	 */
	public ColumnMetadata(String name, String type, int size, String primaryKey) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.primaryKey = primaryKey;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the primaryKey
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
}
