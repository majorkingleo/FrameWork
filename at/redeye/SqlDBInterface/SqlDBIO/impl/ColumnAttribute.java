package at.redeye.SqlDBInterface.SqlDBIO.impl;

public class ColumnAttribute {

	private boolean primaryKey = false;

	private boolean hasIndex = false;

	private DBDataType datatype;

	private int width = 0;

	public ColumnAttribute(DBDataType datatype) {

		super();

		this.datatype = datatype;

	}

	public ColumnAttribute(boolean primaryKey,

	DBDataType datatype) {

		super();

		this.primaryKey = primaryKey;

		this.datatype = datatype;

	}

	/**
	 * 
	 * @return the primaryKey
	 */

	public boolean isPrimaryKey() {

		return primaryKey;

	}

	/**
	 * 
	 * @param primaryKey
	 *            the primaryKey to set
	 */

	public void setPrimaryKey(boolean primaryKey) {

		this.primaryKey = primaryKey;

	}

	/**
	 * 
	 * @return the datatype
	 */

	public DBDataType getDatatype() {

		return datatype;

	}

	/**
	 * 
	 * @param datatype
	 *            the datatype to set
	 */

	public void setDatatype(DBDataType datatype) {

		this.datatype = datatype;

	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public boolean hasIndex() {
		return hasIndex;
	}

	public void setHasIndex(boolean value) {
		hasIndex = value;
	}

}
