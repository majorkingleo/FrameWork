/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.tablemanipulator;

import at.redeye.FrameWork.base.bindtypes.DBEnum;
import at.redeye.FrameWork.base.bindtypes.DBEnumAsInteger;

import at.redeye.FrameWork.widgets.AutoCompleteTextField;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author martin
 */
public class AdvancedEnumTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 1L;
    JComboBox component = new JComboBox();
    TableDesign tabledesign;
    int last_row = 0;
    int last_col = 0;
    Object current_value;    
    
    public AdvancedEnumTableCellEditor(TableDesign tabledesign, DBEnum value ) {
        this.tabledesign = tabledesign;

        for( String s : value.getPossibleValues() )
        {
            component.addItem(s);
        }

        AutoCompleteTextField editor = new AutoCompleteTextField();
        component.setEditor(editor);
        editor.set_items(value.getPossibleValues());
        component.setEditable(true);
        editor.setEditable(true);
    }

    public AdvancedEnumTableCellEditor(TableDesign tabledesign, DBEnumAsInteger value) {
        this.tabledesign = tabledesign;
        
        for( String s : value.getPossibleValues() )
        {
            component.addItem(s);
        }

        AutoCompleteTextField editor = new AutoCompleteTextField();
        component.setEditor(editor);
        editor.set_items(value.getPossibleValues());
        component.setEditable(true);
        editor.setEditable(true);
    }

    public Object getCellEditorValue() {
        /*
        System.out.println( "value+:" + ((JTextField)component).getText() );
        System.out.println( "add row:" + last_row );
         */
        // System.out.println("getCellEditorValue");

        tabledesign.edited_cols.add(last_col);
        tabledesign.edited_rows.add(last_row);
        return component.getSelectedItem().toString();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        // System.out.println("getTableCellEditorComponent");

        last_row = row;
        last_col = column;
        current_value = value;
        component.setSelectedItem(value);
        return component;
    }

    @Override
    public boolean stopCellEditing() {

        // System.out.println("stopCellEditing");

        return super.stopCellEditing();
    }
}