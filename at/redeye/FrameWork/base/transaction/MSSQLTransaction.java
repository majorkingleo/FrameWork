/**
 * 
 */
package at.redeye.FrameWork.base.transaction;

import java.sql.SQLException;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;

import java.util.Date;

import org.joda.time.DateMidnight;

/**
 * @author Mario Mattl
 * 
 */
public class MSSQLTransaction extends Transaction {

	/**
	 * 
	 */
	protected MSSQLTransaction() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param def
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MissingConnectionParamException
	 * @throws UnSupportedDatabaseException
	 */
	public MSSQLTransaction(ConnectionDefinition def)
			throws ClassNotFoundException, SQLException,
			MissingConnectionParamException, UnSupportedDatabaseException {
		super(def);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.redeye.FrameWork.base.Transaction#markColumn(java.lang.String)
	 */
	@Override
	public String markColumn(String in) {
		return "[" + in.toLowerCase() + "]";
	}

	@Override
	public String markTable(String in) {
		return "[" + in.toUpperCase() + "]";
	}

	@Override
	public String getDayStmt(String column, DBDateTime day) {

		StringBuilder str = new StringBuilder();

		str.append(markColumn(column));
		str.append(" = '");
		str.append(day.getDateStr());
		str.append("'");

		return str.toString();
	}

	@Override
	public String getPeriodStmt(String column, DBDateTime begin, DBDateTime end) {

		StringBuilder str = new StringBuilder();

		str.append(markColumn(column));
		str.append(" BETWEEN ");
		str.append("'");
		str.append(begin.toString());
		str.append("'");
		str.append(" AND ");
		str.append("'");
		str.append(end.toString());
		str.append("'");

		return str.toString();
	}

	@Override
	public String getDayStmt(String column, String dayStr) {

		StringBuilder str = new StringBuilder();

		str.append(markColumn(column));
		str.append(" = '");
		str.append(dayStr);
		str.append("'");

		return str.toString();
	}

	@Override
	public String getPeriodStmt(String column, String beginStr, String endStr) {

		StringBuilder str = new StringBuilder();

		str.append(markColumn(column));
		str.append(" BETWEEN ");
		str.append("'");
		str.append(beginStr);
		str.append("'");
		str.append(" AND ");
		str.append("'");
		str.append(endStr);
		str.append("'");

		return str.toString();

	}

    @Override
    public String getPeriodStmt(String column1, String column2, DateMidnight date) 
    {
        StringBuilder str = new StringBuilder();

        
        String str_date = DBDateTime.getStdString(date);       
        
		str.append(markColumn(column1));
		str.append(" >= '");
		str.append( str_date );
		str.append("' AND '");
		str.append( str_date );
		str.append("' < ");				
        str.append(markColumn(column2));

		return str.toString();
    }
    
	@Override
	public String getDayStmt(String column, Date day) {

		DateMidnight dm = new DateMidnight(day);

		return getDayStmt(column, dm);
	}

	@Override
	public String getDayStmt(String column, DateMidnight day) {
		return getPeriodStmt(column, DBDateTime.getStdString(day), DBDateTime
				.getStdString(day.plusDays(1)));
	}

	@Override
	public String getPeriodStmt(String column, DateMidnight dm_from,
			DateMidnight dm_to) {
		return getPeriodStmt(column, DBDateTime.getStdString(dm_from),
				DBDateTime.getStdString(dm_to));
	}

}
