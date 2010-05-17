  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.tablemanipulator;

import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.base.bindtypes.DBEnum;
import at.redeye.FrameWork.base.bindtypes.DBEnumAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.utilities.StringUtils;

import java.awt.Color;
import java.util.HashSet;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class TableManipulator {
        
    private DBStrukt binddesc = null;    
    private Vector<Integer> hidden_values = new Vector<Integer>();    
            
    TableDesign tabledesign;
    JTable table;
    NormalTableModel model;    
    boolean allEditable = false;
    Root root;
    RowHeader row_header;
    int auto_show_row_header = 20;
    private static Logger logger = Logger.getLogger(TableManipulator.class.getName());
    
    public TableManipulator( Root root, JTable table, TableDesign tabledesign )
    {
        this.tabledesign = tabledesign;
        this.table = table;
        this.model = new NormalTableModel(tabledesign);        
        this.root = root;
        table.setModel(model);        
        table.setDefaultRenderer(Object.class, new NormalCellRenderer(root, this.tabledesign));
        row_header = new RowHeader( table,  new Runnable() {

            public void run() {
                checkRowHeaderLimit();
            }
        } );
        
        TableEditorStopper.ensureEditingStopWhenTableLosesFocus(table);
        readShowHeaderLimit();
    }
    
    public TableManipulator( Root root, JTable table, DBStrukt binddesc )
    {
        this.root = root;
        readShowHeaderLimit();
        configure( table, binddesc, false );
    }
    
    public TableManipulator( Root root, JTable table, DBStrukt binddesc, boolean allEditable )
    {
        this.root = root;
        readShowHeaderLimit();
        configure( table, binddesc, allEditable );
    }   

    protected boolean isHidden( int i )
    {
        for( Integer ii = 0; ii < hidden_values.size(); ii++ )
        {
            if( hidden_values.get(ii).equals(i) )
                return true;
        }
        
        return false;
    }
    
    private void configure( JTable table, DBStrukt binddesc, boolean allEditable )
    {
        this.binddesc = binddesc;
        this.allEditable = allEditable;
        
        TableEditorStopper.ensureEditingStopWhenTableLosesFocus(table);
        
        Vector<TableDesign.Coll> vec = new Vector<TableDesign.Coll>();
        
        Vector<String> names = binddesc.getAllNames();        
        Vector<DBValue> values = binddesc.getAllValues();
        
        for( int i = 0; i < names.size(); i++ )
        {
            if( !isHidden(i) )
                vec.add( new TableDesign.Coll( names.get(i), false, values.get(i) ) );            
        }
        
        this.tabledesign = new TableDesign( vec );
        this.table = table;
        this.model = new NormalTableModel(tabledesign);        
        table.setModel(model);
        table.setDefaultRenderer(Object.class, new NormalCellRenderer(root, this.tabledesign));
        row_header = new RowHeader( table, new Runnable() {

            public void run() {
                checkRowHeaderLimit();
            }
        } );
    }

    public void autoResize()
    {
        autoResizeColWidth( table, model );
    }
    
     public void autoResizeColWidth(JTable table, DefaultTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
 
        String smargin_default = root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.SpreadSheetMarginReadOnly);
        String smargin_editable = root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.SpreadSheetMarginEditable);
 
        int margin_default = Integer.valueOf(smargin_default);
        int margin_editable = Integer.valueOf(smargin_editable);

        int max_height = 0;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;
 
            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();
 
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
 
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
 
            int width_header = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);

                Dimension dim = comp.getPreferredSize();

                width = Math.max(width, dim.width);

                // System.out.println("hieght: " + dim.height + " row: " + (r +1) + " col: " + (i+1) );

                max_height = Math.max(max_height, dim.height);
            }


            if( tabledesign.colls.get(vColIndex).isEditable )
            {
                if( width_header <= width )
                    width += 2 * margin_editable;
                else
                    width = width_header += margin_default;
            } else {
                // Add margin
                if( width_header <= width )
                    width += 2 * margin_default;
                else
                    width = width_header += margin_default;
            }
 
            // Set the width
            col.setPreferredWidth(width);
        }
 
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);
 
        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        if( max_height > 0 )
        {

            int correction = 0;

            LookAndFeel look_and_feel = UIManager.getLookAndFeel();

            if( look_and_feel != null )
            {
                logger.info("look and feel: " + look_and_feel.getID() );

                if( Setup.is_linux_system() )
                {
                    correction = 1;

                    if( look_and_feel.getID().equals("Nimbus") )
                        correction = 3;
                }
                else // Windows
                {
                    correction=2;

                    if( look_and_feel.getID().equals("Nimbus") )
                        correction = 4;
                    else if( look_and_feel.getID().equals("Windows") )
                        correction = 0;
                }
            }

            //System.out.println(String.format("height: %d",max_height) );
            row_header.setCellHeight(max_height-correction);
        }
 /*
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
 
            column.setCellRenderer(new DefaultTableColour());
        }
 
        return table;
  */
    }
    
    public void add(DBStrukt binddesc) 
    {
        Vector<DBValue> values = binddesc.getAllValues();
        
        addRow( values );        
    }
    
    public void add(DBStrukt strukt, boolean set_edited ) 
    {
        add( strukt );
        
        if( set_edited )
            tabledesign.edited_rows.add(tabledesign.rows.size()-1);
    }
    
    public <T extends DBStrukt> void addAll( Collection<T> col)
    {
        for( DBStrukt s : col )
        {
            Vector<DBValue> values = s.getAllValues();        
            
            addRow( values, false );
        }

        checkRowHeaderLimit();
        row_header.updateUI();
    }
    
    public void prepareTable()
    {
        for( TableDesign.Coll coll : tabledesign.colls )
        {
            model.addColumn(coll.Title);                      
        }   
        
        /* Das muß so sein,
         * da bei der ersten for Schleife der CellEditor resetted wird
         * anscheinend
         */
        for( int i = 0; i < table.getColumnCount(); i++ )
        {
            TableColumn col = table.getColumnModel().getColumn(i);    
            
            TableDesign.Coll tcoll = tabledesign.colls.get(i);
            
            if( tcoll.dbval instanceof DBEnum ) {
                col.setCellEditor(new AdvancedEnumTableCellEditor(tabledesign, (DBEnum)tcoll.dbval));
            } else if( tcoll.dbval instanceof DBEnumAsInteger ) {
                col.setCellEditor(new AdvancedEnumTableCellEditor(tabledesign, (DBEnumAsInteger)tcoll.dbval));
            } else if( tcoll.dbval instanceof DBSqlAsInteger ) {
                col.setCellEditor(new AdvancedEnumTableCellEditor(tabledesign, (DBSqlAsInteger)tcoll.dbval));
            } else {
                col.setCellEditor(new AdvancedTableCellEditor(tabledesign));
            }
        }                       
    }

    public void addRow( Vector<?> data )
    {
        addRow(data,true);
    }

    
    private void addRow( Vector<?> data, boolean update_ui )
    {
        /* Wir müssen hier einen 2. Vector anlegen,
         * da der eine an die Tabelle angebunden wird
         * und wenn über den TableVelidator ein
         * anderer Anzeige Format String verwendet wird
         * wird unser ursprüngliches Objekt in
         * table_copy durch einen String ersetzt,
         * und weil das alles Referenzen sind, 
         * würde dies auch mit unserem db_copy
         * Vector passieren.
         * Deswegen der 2. Vector.
         */
                
        
        Vector<Object> table_copy = new Vector<Object>();
        Vector<Object> db_copy = new Vector<Object>();
        
        for( int i = 0; i < data.size(); i++ )
        {
            if( !hidden_values.contains(i) ) {
                table_copy.add( data.get(i) );
                db_copy.add( data.get(i) );
            }
            // else
            //    System.out.println( "ignored" );
        }
        
        model.addRow(table_copy);
        tabledesign.rows.add(db_copy);

        if( update_ui ) {
            checkRowHeaderLimit();
            row_header.updateUI();
        }
    }
    
    public void clear()
    {
        int i;
        while( ( i = model.getRowCount() ) > 0 )
            model.removeRow( i-1 );
        
        tabledesign.edited_cols.clear();
        tabledesign.edited_rows.clear();
        tabledesign.rows.clear();
        tabledesign.coloredCells.clear();

        checkRowHeaderLimit();
        row_header.updateUI();
    }
    
    public void remove( int row )
    {
        model.removeRow(row);
        tabledesign.rows.remove(row);
        
        Object rows[] = getEditedRows().toArray();
        
        HashSet<Integer> er = new HashSet<Integer>();                
        
        for( int i = 0; i < rows.length; i++ )
        {        
            if( (Integer)rows[i] == row )
            {                
                continue;
            }            
            
            if( (Integer)rows[i] < row )
            {
                er.add( new Integer(i));
            }
            else
            {               
                er.add(new Integer(i-1));
            }
        }
        
        tabledesign.edited_rows = er;

        checkRowHeaderLimit();
        row_header.updateUI();
    }
    
    public Set<Integer> getEditedRows()
    {
        return tabledesign.edited_rows;                
    }        
    
    public void setEditedAll() 
    {
        for( int i = 0; i < tabledesign.rows.size(); i++ )
            tabledesign.edited_rows.add(i);
    }
    
    public void setEditable( DBValue column )
    {
        setEditable( column, true );
    }
    
    public void setEditable( DBValue column, boolean isEditable )
    {
        Vector<DBValue> values = binddesc.getAllValues();
        
        for( int i = 0, col=0; i < values.size(); i++ )
        {
            if( isHidden( i ) )
                continue;
            
            if( values.get(i).hashCode() == column.hashCode() )
            {                
                
                tabledesign.colls.get(col).setEditable( isEditable );
                return;
            }
            
            col++;
        }                
    }
    
    public void setValidator(DBValue column,TableValidator validator) 
    {
        Vector<DBValue> values = binddesc.getAllValues();
        
        for( int i = 0, col=0; i < values.size(); i++ )
        {
            if( isHidden( i ) )
                continue;
            
            if( values.get(i).hashCode() == column.hashCode() )
            {                                
                tabledesign.colls.get(col).validator = validator;
                return;
            }
            
            col++;
        } 
    }
     
    public void setAdditionalAutocompleteData( DBValue column, Vector<Object> data )
    {
        Vector<DBValue> values = binddesc.getAllValues();

        for( int i = 0, col=0; i < values.size(); i++ )
        {
            if( isHidden( i ) )
                continue;

            if( values.get(i).hashCode() == column.hashCode() )
            {
                tabledesign.colls.get(col).additional_autocomplete_values = data;
                return;
            }

            col++;
        }
    }
    
    public void hide( DBValue column )
    {        
        Vector<DBValue> values = binddesc.getAllValues();
        
        boolean found = false;
        
        for( int i = 0; i < values.size(); i++ )
        {
            if( values.get(i).hashCode() == column.hashCode() )
            {
                hidden_values.add(i);
                found = true;
                break;
            }
        }
        
        if( !found ) {            
            System.out.println( "Didn't found: " + column.getName() );
            return;
        }
        
        configure( table, binddesc, allEditable );
    }
        
    
    public void stopEditing()
    {
       TableCellEditor ce =  table.getCellEditor();
       
       if( ce != null )
           ce.stopCellEditing();
    }


    public void setCellColor (DBValue column, int row, Color color) {

        Vector<DBValue> values = binddesc.getAllValues();


        for( int i = 0, col=0; i < values.size(); i++ )
        {
            if( isHidden( i ) )
                continue;


            if( values.get(i).getName().equals(column.getName()) )
            {
                tabledesign.addColoredCell(row, col, color);
                return;
            }

            col++;
        }
    }

    public void updateValue( DBValue value, int row )
    {
        Vector<DBValue> values = binddesc.getAllValues();

        for( int i = 0, col=0; i < values.size(); i++ )
        {
            if( isHidden( i ) )
                continue;

            if( values.get(i).getName().equals(value.getName()) )
            {
                model.setValueAt(value, row, col);
                //model.fireTableCellUpdated(row, col);
                return;
            }

            col++;
        }
    }

    public void updateUI()
    {        
        model.fireTableDataChanged();
        row_header.updateUI();
        //table.updateUI();
    }

    /**
     * @return -1 if nothing was selected
     */
    public int getSelectedRow()
    {
        return table.getSelectedRow();
    }

    public TableDesign getTabledesign() {
        return tabledesign;
    }

    public void showRowHeader(boolean selected)
    {
        if( selected )
            showRowHeader();
        else
            hideRowHeader();
    }

    public void showRowHeader()
    {
        row_header.setVisible(true);
    }

    public void hideRowHeader()
    {
        row_header.setVisible(false);
    }

    private void checkRowHeaderLimit()
    {
        if( auto_show_row_header < 0 )
        {
            row_header.setVisible(false);
        }
        else if( auto_show_row_header == 0 )
        {
             row_header.setVisible(true);
        }
        else
        {
            if (table.getRowCount() < auto_show_row_header &&
                !row_header.isScrollBarVisible() ) {
                row_header.setVisible(false);
            } else {
                row_header.setVisible(true);
            }
        }
    }

    private void readShowHeaderLimit()
    {
        try {
            auto_show_row_header = Integer.parseInt(root.getSetup().getLocalConfig(
                    FrameWorkConfigDefinitions.SpreadSheetRowHeaderLimit));

        } catch ( NumberFormatException ex ) {
            logger.error(StringUtils.ExceptionToString(ex));
        }
    }

    public void disableAutoRowHeader()
    {
        auto_show_row_header = -1;
    }

    public void enableAutoRowHeader()
    {
        readShowHeaderLimit();
    }
}
