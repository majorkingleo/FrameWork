package at.redeye.SqlDBInterface.SqlDBIO.impl;



public class MOMMColumnAttribute {

	



	private boolean primaryKey = false;
    
    private boolean hasIndex = false;

	private MOMMDBDataType datatype;

        private int width = 0;

	

	public MOMMColumnAttribute(MOMMDBDataType datatype) {

		super();

		this.datatype = datatype;

	}

	

	

	public MOMMColumnAttribute(boolean primaryKey,

			MOMMDBDataType datatype) {

		super();

		this.primaryKey = primaryKey;

		this.datatype = datatype;

	}







	/**

	 * @return the primaryKey

	 */

	public boolean isPrimaryKey() {

		return primaryKey;

	}





	/**

	 * @param primaryKey the primaryKey to set

	 */

	public void setPrimaryKey(boolean primaryKey) {

		this.primaryKey = primaryKey;

	}





	/**

	 * @return the datatype

	 */

	public MOMMDBDataType getDatatype() {

		return datatype;

	}





	/**

	 * @param datatype the datatype to set

	 */

	public void setDatatype(MOMMDBDataType datatype) {

		this.datatype = datatype;

	}

	
        
	
        public void setWidth( int width )
        {
            this.width = width;
        }
	
        public int getWidth()
        {
            return width;
        }



        public boolean hasIndex()
        {
            return hasIndex;
        }
        
        public void setHasIndex(boolean value)
        {
            hasIndex = value;            
        }


	



}

