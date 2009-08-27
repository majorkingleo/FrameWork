package at.redeye.FrameWork.base.bindtypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

/**
 * 
 * @author Mario Mattl
 * 
 */
public class DBDateTime extends DBValue {

	protected Date value = new Date(0);

	public DBDateTime(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public DBDateTime(String name, String title) {
		super(name, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean acceptString(String s) {
		return false;
	}

	@Override
	public DBDateTime getCopy() {
		DBDateTime datetime = new DBDateTime(name);
		datetime.value = value;

		return datetime;
	}

	@Override
	public MOMMDBDataType getDBType() {
		// TODO Auto-generated method stub
		return MOMMDBDataType.DB_TYPE_DATETIME;
	}

	@Override
	public Date getValue() {
		return value;
	}

	@Override
	public void loadFromCopy(Object obj) {
		value = (Date) ((Date) obj).clone();
	}

	@Override
	public void loadFromDB(Object obj) {

		value = (Date) obj;

	}

	@Override
	public void loadFromString(String s) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
						+ MOMMStmtExecInterface.SQLIF_STD_TIME_FORMAT);

		try {
			value = sdf.parse(s);
		} catch (ParseException e) {

			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return getStdString(value);
	}

	public static String getStdString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
						+ MOMMStmtExecInterface.SQLIF_STD_TIME_FORMAT);

		return sdf.format(date);
	}

	public static String getStdString(DateTime date) {
		return date.toString(MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
				+ MOMMStmtExecInterface.SQLIF_STD_TIME_FORMAT);
	}

	public static String getStdString(DateMidnight date) {
		return date.toString(MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
				+ MOMMStmtExecInterface.SQLIF_STD_TIME_FORMAT);
	}


    public String getTimeStr() {
        return getTimeStr(value);
    }

	public static String getTimeStr(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				MOMMStmtExecInterface.SQLIF_STD_TIME_FORMAT);

		return sdf.format(date);

	}

	public String getDateStr() {
		return getDateStr(value);
	}

	public static String getDateStr(Date value) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT);

		return sdf.format(value);
	}

	public static String getDateStr(DateMidnight date) {

		return date.toString(MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT);

	}
	
	public boolean loadTimePart(String time) {
		if (time.matches("(([0-1][0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9]") == false)
			return false;

		String time_str = toString();
		String completet = time_str.substring(0, 10) + " " + time;

		loadFromString(completet);

        /*
		System.out.println("completed: '" + completet + "' toString '"
				+ toString() + "'");
        */
        
		if (completet.equalsIgnoreCase(toString()) == true) {
			//System.out.println("true");
			return true;
		}

		//System.out.println("false");

		return false;
	}


}
