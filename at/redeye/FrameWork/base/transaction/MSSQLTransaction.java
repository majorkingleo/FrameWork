/**
 * 
 */
package at.redeye.FrameWork.base.transaction;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.swing.JComponent;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.widgets.DBFilterComboBox;
import at.redeye.FrameWork.widgets.DBFilterEditField;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;

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
    public String getPeriodStmt(String column1, String column2, DateMidnight date) {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column1));
        str.append(" >= '");
        str.append(str_date);
        str.append("' AND '");
        str.append(str_date);
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
        return getPeriodStmt(column, DBDateTime.getStdString(day), DBDateTime.getStdString(day.plusDays(1)));
    }

    @Override
    public String getPeriodStmt(String column, DateMidnight dm_from,
            DateMidnight dm_to) {
        return getPeriodStmt(column, DBDateTime.getStdString(dm_from),
                DBDateTime.getStdString(dm_to));
    }

    @Override
    public String getGUIFilterWhereStmt(Vector<? extends JComponent> fromFilter, Vector<? extends JComponent> toFilter) {

        StringBuilder str = new StringBuilder();
        boolean foundPartner;


        if (fromFilter != null && fromFilter.size() > 0) {
            str.append(" WHERE ");
            String nameFrom = "";
            String valueFrom = "";
            for (int i = 0; i < fromFilter.size(); i++) {
                foundPartner = false;
                JComponent c = fromFilter.get(i);
                if (c instanceof DBFilterComboBox) {
                    nameFrom = ((DBFilterComboBox) c).getColName();
                    valueFrom = ((DBFilterComboBox) c).getSelectedItem().toString();
                } else if (c instanceof DBFilterEditField) {
                    nameFrom = ((DBFilterComboBox) c).getColName();
                    valueFrom = ((DBFilterComboBox) c).getSelectedItem().toString();
                } else {
                    // not supported item
                    System.out.println("Unsupported filter element in container!");
                    return "";

                }
                String nameTo = "";
                String valueTo = "";
                for (int j = 0; j < toFilter.size(); j++) {
                    JComponent cTo = toFilter.get(j);
                    if (c instanceof DBFilterComboBox) {
                        nameTo = ((DBFilterComboBox) cTo).getColName();
                        valueTo = ((DBFilterComboBox) cTo).getSelectedItem().toString();
                    } else if (c instanceof DBFilterEditField) {
                        nameTo = ((DBFilterComboBox) cTo).getColName();
                        valueTo = ((DBFilterComboBox) cTo).getSelectedItem().toString();
                    } else {
                        // not supported item
                        System.out.println("Unsupported filter element in container!");
                        return "";

                    }
                    if (nameTo.equals(nameFrom)) {
                        foundPartner = true;
                        break;
                    }

                }

                if (!foundPartner) {
                    str.append(markColumn(nameFrom) + " = ");
                    str.append("'" + valueFrom + "' ");
                } else {
                    str.append(markColumn(nameFrom) + " between ");
                    str.append("'" + valueFrom + "' and ");
                    str.append("'" + valueTo + "' ");
                }

                if (i < fromFilter.size() - 1) {
                    str.append(" and ");
                }

            }
        }
        System.out.println("build: " + str.toString());
        return str.toString();
    }

    @Override
    public String getHigherDate(String column, DateMidnight dm_from) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLowerDate(String column, DateMidnight dm_from) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getHigherDateExl(String column, DateMidnight dm_from) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLowerDateExl(String column, DateMidnight dm_from) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
