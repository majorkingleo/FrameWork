  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.tablemanipulator;

import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import java.awt.Component;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBEnum;
import at.redeye.FrameWork.base.bindtypes.DBEnumAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;

import java.awt.Color;
import java.util.HashSet;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

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
    
    public TableManipulator( Root root, JTable table, TableDesign tabledesign )
    {
        this.tabledesign = tabledesign;
        this.table = table;
        this.model = new NormalTableModel(tabledesign);        
        this.root = root;
        table.setModel(model);        
        table.setDefaultRenderer(Object.class, new NormalCellRenderer(root, this.tabledesign));
        
        TableEditorStopper.ensureEditingStopWhenTableLosesFocus(table);
    }
    
    public TableManipulator( Root root, JTable table, DBStrukt binddesc )
    {
        this.root = root;
        configure( table, binddesc, false );
    }
    
    public TableManipulator( Root root, JTable table, DBStrukt binddesc, boolean allEditable )
    {
        this.root = root;
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
                width = Math.max(width, comp.getPreferredSize().width);
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
    
    public void addAll( Collection<DBStrukt> col) 
    {
        for( DBStrukt s : col )
        {
            Vector<DBValue> values = s.getAllValues();        
            
            addRow( values );
        }
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
        /* Wir müssen hier einen 2. Vector anlegen,
         * da der eine an die Tebelle angebunden wird
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
                tabledesign.colls.get(col).additional_autocoplete_values = data;
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
        //table.updateUI();
    }

    public int getSelectedRow()
    {
        return table.getSelectedRow();
    }
}
