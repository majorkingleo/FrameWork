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
import org.joda.time.LocalDate;

/**
 * @author Mario Mattl
 * 
 */
public class MySQLTransaction extends Transaction {

    /**
     *
     */
    protected MySQLTransaction() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param def
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws MissingConnectionParamException
     * @throws UnSupportedDatabaseException
     */
    public MySQLTransaction(ConnectionDefinition def)
            throws ClassNotFoundException, SQLException,
            MissingConnectionParamException, UnSupportedDatabaseException {
        super(def);
    // TODO Auto-generated constructor stub
    }

    @Override
    public String getDayStmt(String column, DBDateTime day) {

        LocalDate dm = new LocalDate((Date) day.getValue());

        return getPeriodExclStmt(column, dm.toDateTimeAtStartOfDay().toDate(), dm.plusDays(1).toDateTimeAtStartOfDay().toDate());
    }

    @Override
    public String getPeriodStmt(String column, DBDateTime begin, DBDateTime end) {

        StringBuilder str = new StringBuilder();

        str.append("( ");
        str.append(markColumn(column));
        str.append(" >= '");
        str.append(begin.toString());
        str.append("' AND ");
        str.append(markColumn(column));
        str.append(" <= '");
        str.append(end.toString());
        str.append("'");
        str.append(") ");

        return str.toString();

    }

    @Override
    public String getDayStmt(String column, String dayStr) {

        StringBuilder str = new StringBuilder();
        str.append(markColumn(column));
        str.append(" LIKE '");
        str.append(dayStr);
        str.append("%'");

        return str.toString();
    }

    @Override
    public String getPeriodStmt(String column, String beginStr, String endStr) {

        StringBuilder str = new StringBuilder();

        str.append("( ");
        str.append(markColumn(column));
        str.append(" >= '");
        str.append(beginStr);
        str.append("' AND ");
        str.append(markColumn(column));
        str.append(" <= '");
        str.append(endStr);
        str.append("'");
        str.append(") ");

        return str.toString();

    }

    private String getPeriodExclStmt(String column, String str_begin, String str_end) {

        StringBuilder str = new StringBuilder();

        str.append("( ");
        str.append(markColumn(column));
        str.append(" >= '");
        str.append(str_begin);
        str.append("' AND ");
        str.append(markColumn(column));
        str.append(" < '");
        str.append(str_end);
        str.append("'");
        str.append(") ");

        return str.toString();
    }


    private String getPeriodExclStmt(String column, Date begin, Date end) {
       

        String str_begin = DBDateTime.getStdString(begin);
        String str_end = DBDateTime.getStdString(end);

        return getPeriodExclStmt(column, str_begin, str_end);
    }

    @Override
    public String getDayStmt(String column, Date day) {

        DateMidnight dm = new DateMidnight(day);

        return getDayStmt(column, dm);
    }

    @Override
    public String getDayStmt(String column, DateMidnight day) {
        return getPeriodExclStmt(column, day.toDate(), day.plusDays(1).toDate());
    }

    @Override
    public String getDayStmt(String column, LocalDate day) {
        return getPeriodExclStmt(column, day.toDateTimeAtStartOfDay().toDate(), day.plusDays(1).toDateTimeAtStartOfDay().toDate());
    }

    @Override
    public String getPeriodStmt(String column, DateMidnight dm_from, DateMidnight dm_to) {

        // da wir bei to den Tag auch inklusive haben wollen
        // und mysql das so selektiert zählen wir 1 dazu damit wir
        // einfach den nächsten Tag um 00:00 und machen
        // den getPeriodStmtExl aufruf, damit wir keine Einträge für
        // 00:00 bekommen, aber alle davor :-)

        return getPeriodExclStmt(column,
                DBDateTime.getStdString(dm_from),
                DBDateTime.getStdString(dm_to.plusDays(1)));
    }

    @Override
    public String getPeriodStmt(String column, LocalDate dm_from, LocalDate dm_to) {

        // da wir bei to den Tag auch inklusive haben wollen
        // und mysql das so selektiert zählen wir 1 dazu damit wir
        // einfach den nächsten Tag um 00:00 und machen
        // den getPeriodStmtExl aufruf, damit wir keine Einträge für
        // 00:00 bekommen, aber alle davor :-)

        return getPeriodExclStmt(column,
                DBDateTime.getStdString(dm_from),
                DBDateTime.getStdString(dm_to.plusDays(1)));
    }

    @Override
    public String getPeriodStmt(String column1, String column2, DateMidnight date) {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append("( ");
        str.append(markColumn(column1));        
        str.append(" >= '");
        str.append(str_date);
        str.append("' AND '");
        str.append(str_date);
        str.append("' < ");
        str.append(markColumn(column2));
        str.append(") ");

        return str.toString();
    }


    @Override
    public String getPeriodStmt(String column1, String column2, LocalDate date) {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append("( ");
        str.append(markColumn(column1));
        str.append(" >= '");
        str.append(str_date);
        str.append("' AND '");
        str.append(str_date);
        str.append("' < ");
        str.append(markColumn(column2));
        str.append(") ");

        return str.toString();
    }


    @Override
    public String getHigherDate(String column, DateMidnight date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" >= '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getHigherDate(String column, LocalDate date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" >= '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getLowerDate(String column, DateMidnight date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" <= '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getLowerDate(String column, LocalDate date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" <= '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getHigherDateExl(String column, DateMidnight date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" > '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getHigherDateExl(String column, LocalDate date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" > '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }


    @Override
    public String getLowerDateExl(String column, DateMidnight date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" < '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getLowerDateExl(String column, LocalDate date)
    {
        StringBuilder str = new StringBuilder();


        String str_date = DBDateTime.getStdString(date);

        str.append(markColumn(column));
        str.append(" < '");
        str.append(str_date);
        str.append("'");

        return str.toString();
    }

    @Override
    public String getGUIFilterWhereStmt(Vector<? extends JComponent> fromFilter, Vector<? extends JComponent> toFilter) {

        StringBuilder str = new StringBuilder();
        boolean foundPartner;


        if (fromFilter != null && fromFilter.size() > 0) {
            str.append(" WHERE ");
            String nameFrom;
            String valueFrom;
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
                    str.append (markColumn(nameFrom) + " = ");
                    str.append("'"+valueFrom+"' ");
                } else {
                    str.append (markColumn(nameFrom) + " >= ");
                    str.append ("'"+valueFrom+"' and ");
                    str.append (markColumn(nameTo) + " <= ");
                    str.append ("'"+valueTo+"' ");
                }

                if (i < fromFilter.size()-1) {
                    str.append (" and ");
                }

            }
        }
        System.out.println("build: "+str.toString());
        return str.toString();
    }
}
