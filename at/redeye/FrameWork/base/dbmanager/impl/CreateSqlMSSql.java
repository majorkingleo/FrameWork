/**
 * 
 */
package at.redeye.FrameWork.base.dbmanager.impl;


import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import java.util.Collection;

/**
 * @author mmattl
 * 
 */
public class CreateSqlMSSql extends BaseCreateSql {

	@Override
	public String markColumn(String col) {

		return "[" + col + "]";
	}

	@Override
	protected String addStorageInfo() {
		return "";
	}

	@Override
	protected String createIndexKeys(String table, Collection<String> indexKeys) {
		StringBuilder res = new StringBuilder();

		for (String key : indexKeys) {
			res.append(" CREATE INDEX ");
			res.append(markColumn("IDX_" + table.toUpperCase() + "_" + key.toUpperCase()));
			res.append("on");
			res.append(markColumn(table));
			res.append("(");
			res.append(markColumn(key));
			res.append(");\n");
		}

		return res.toString();
	}

	@Override
	protected String createSqlForRow(ColumnAttribute attr) {
		switch (attr.getDatatype()) {
		case DB_TYPE_STRING:
			return "VARCHAR(" + attr.getWidth() + ")";
		case DB_TYPE_DATETIME:
			return "DATETIME";
		case DB_TYPE_DATE:
			return "DATE";
		case DB_TYPE_FLOAT:
			return "float default 0";
		case DB_TYPE_DOUBLE:
			return "real default 0";
		case DB_TYPE_LONG:
		case DB_TYPE_INTEGER:
		case DB_TYPE_BOOLEAN:
		case DB_TYPE_BIT:
		case DB_TYPE_SHORT:
			return "int default '0'";
        case DB_TYPE_BLOB:
            return "varbinary (max)";

		}

		return null;
	}

}
