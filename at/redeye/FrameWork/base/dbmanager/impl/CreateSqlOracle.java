/**
 * 
 */
package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMColumnAttribute;

import java.util.Vector;

/**
 * @author mmattl
 * 
 */
public class CreateSqlOracle extends BaseCreateSql {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.redeye.FrameWork.base.dbmanager.impl.BaseCreateSql#addStorageInfo()
	 */
	@Override
	protected String addStorageInfo() {

		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.redeye.FrameWork.base.dbmanager.impl.BaseCreateSql#markColumn(java
	 * .lang.String)
	 */
	@Override
	public String markColumn(String col) {
		return "\"" + col + "\"";
	}

	protected String createSqlForRow(MOMMColumnAttribute attr) {

		switch (attr.getDatatype()) {
		case DB_TYPE_STRING:
			return "VARCHAR(" + attr.getWidth() + ") default "+ getDefaultValueVarChar(attr.getWidth());
		case DB_TYPE_DATETIME:
			return "DATE";
		case DB_TYPE_DATE:
			return "DATE";
		case DB_TYPE_LONG:
		case DB_TYPE_FLOAT:
		case DB_TYPE_DOUBLE:
		case DB_TYPE_INTEGER:
		case DB_TYPE_BOOLEAN:
		case DB_TYPE_BIT:
		case DB_TYPE_SHORT:
			return "number default 0";
		}

		return null;
	}
	
	private String getDefaultValueVarChar(int length) {
		
		StringBuilder str = new StringBuilder ();
		str.append("'");
		for (int i = 0; i < length; i++) {
			str.append(" ");
		}
		str.append("'");
		return str.toString();
	}

    @Override
    protected String createIndexKeys( String table, Vector<String> indexKeys )
    {
        StringBuilder res = new StringBuilder();
        
        for( String key : indexKeys )
        {        
            res.append( " CREATE INDEX ");
            res.append( markColumn( "IDX_" + table.toUpperCase() + "_" + key.toUpperCase() ) );
            res.append( "on" );
            res.append( markColumn(table) );
            res.append( "(" );
            res.append( markColumn( key ));
            res.append(");\n");
        }
        
        return res.toString();
    }
    
}
