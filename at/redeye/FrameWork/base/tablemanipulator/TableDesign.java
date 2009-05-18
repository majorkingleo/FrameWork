/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.tablemanipulator;

import at.redeye.FrameWork.base.bindtypes.DBValue;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class TableDesign {

    public Set<Integer> edited_cols;
    public Set<Integer> edited_rows;
    public Vector<Vector<Object>> rows = new Vector<Vector<Object>>();

    public static class Coll {

        public String Title;
        public boolean isEditable = true;
        public TableValidator validator = null;
        DBValue dbval = null;

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
}
