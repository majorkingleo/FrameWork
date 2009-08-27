/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.tablemanipulator;

import at.redeye.FrameWork.base.bindtypes.DBValue;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class TableDesign {

    public static class ColoredCell {
        int row;
        int col;
        Color color;
    }

    public Set<Integer> edited_cols;
    public Set<Integer> edited_rows;
    public Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
    protected Vector<ColoredCell>  coloredCells = new Vector<ColoredCell>();

    public void addColoredCell (int row, int col, Color color) {
        ColoredCell cell = new ColoredCell();
        cell.row = row;
        cell.col = col;
        cell.color = color;
        coloredCells.add (cell);
    }

    public static class Coll {

        public String Title;
        public boolean isEditable = true;
        public TableValidator validator = null;
        DBValue dbval = null;
        public Vector<Object> additional_autocoplete_values = null;

        public Coll(String title) {
            this.Title = title;
        }

        public Coll(String title, Boolean isEditable) {
            this.Title = title;
            this.isEditable = isEditable;
        }

        public Coll(String title, Boolean isEditable, DBValue val ) {
            this.Title = title;
            this.isEditable = isEditable;
            this.dbval = val;
        }
        
        void setEditable(boolean isEditable) {
            this.isEditable = isEditable;
        }
    }
    public Vector<Coll> colls;

    public TableDesign(Vector<Coll> colls) {
        this.colls = colls;
        this.edited_cols = new HashSet<Integer>();
        this.edited_rows = new HashSet<Integer>();
    }

    public Vector<String> getAllOfCollSorted(int col)
    {
        Vector<String> all = new Vector<String>();

        TableValidator validator = null;
        Coll coll = null;

        if( col >= 0 && col < colls.size() )
        {
            coll = colls.get(col);
            validator = coll.validator;
        }

        for( int i = 0; i < rows.size(); i++ )
        {
            if( rows.get(i).size() > col && col > 0 )
            {
                Object obj = rows.get(i).get(col);


                if( validator != null )
                {
                    all.add(validator.formatData(obj));
                }
                else
                {
                    all.add(obj.toString());
                }
            }
        }

        if( coll.additional_autocoplete_values != null )
        {
            for( Object obj : coll.additional_autocoplete_values )
            {
                if( validator != null )
                    all.add(validator.formatData(obj));
                else
                    all.add(obj.toString());
            }
        }

        Collections.sort(all);

        // System.out.println( "HEEEEEEEEERE ");
        /*
        for( int i = 0; i < all.size(); i++ )
        {
            System.out.println("xx: " + all.get(i));
        }
        */
        return all;
    }
}
