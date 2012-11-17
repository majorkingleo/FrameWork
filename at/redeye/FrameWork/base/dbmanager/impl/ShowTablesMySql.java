/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.redeye.FrameWork.base.dbmanager.ShowTables;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;

/**
 * 
 * @author Administrator
 */
public class ShowTablesMySql implements ShowTables {

        String dbversion;
    
	public Collection<String> showTables(Transaction trans) throws SQLException {

		String sql = "show tables";

		List<DBDataType> args = new ArrayList<DBDataType>();
		args.add(DBDataType.DB_TYPE_STRING);
		List<List<?>> res;

		/*
		 * Eine UnsupportedDBDataTypeException Exception sollte hier ja eher
		 * nicht geworfen werden, weil wir sollten schon wissen, was wir tun.
		 */
		try {
			res = trans.getStmtExecInterface().fetchColumnValue(sql, args);
		} catch (UnsupportedDBDataTypeException ex) {
			System.out.println("XXX: " + ex);
			Logger.getLogger(ShowTablesSqlite.class.getName()).log(
					Level.SEVERE, null, ex);
			return null;
		}

		Vector<String> ret = new Vector<String>();

		for (int i = 0; i < res.size(); i++)
			ret.add((String) res.get(i).get(0));

		return ret;

	}
        
        public static boolean isVersionNewerThan( String dbversion, int requested_major, int requested_minor, int requestet_mminor )
        {
            String parts[] = dbversion.split("\\.");
            
            int major = 0;
            int minor = 0;
            int mminor = 0;
            
            for( int i = 0; i < parts.length; i++ )
            {
                if( parts[i].matches("[0-9]+") )
                {
                    if( i == 0 ) {
                        major = Integer.valueOf(parts[i]);
                    }
                    else if( i == 1 ) {
                        minor = Integer.valueOf(parts[i]);
                    }
                    else if( i == 2 ) {
                        mminor = Integer.valueOf(parts[i]);
                    }                  
                } else {
                    
                    String s = parts[i].replaceAll("[^0-9]", "");
                    
                    if( s.isEmpty() )
                        s = "0";
                    
                    if( i == 0 ) {
                        major = Integer.valueOf(s);
                    }
                    else if( i == 1 ) {
                        minor = Integer.valueOf(s);
                    } else if( i == 2 ) {
                        mminor = Integer.valueOf(s);
                    }                    
                }
            }
            
            if( major < requested_major )
                return false;
            
            if( minor < requested_minor )
                return false;
            
            if( mminor < requestet_mminor )
                return false;
            
            return true;
        }
        
        public String getDBVersion( Transaction trans) throws SQLException
        {
                String sql = "SHOW VARIABLES LIKE 'version'";

		List<DBDataType> args = new Vector<DBDataType>();
		args.add(DBDataType.DB_TYPE_STRING);
		args.add(DBDataType.DB_TYPE_STRING);
		List<List<?>> res;

		/*
		 * Eine UnsupportedDBDataTypeException Exception sollte hier ja eher
		 * nicht geworfen werden, weil wir sollten schon wissen, was wir tun.
		 */
		try {
			res = trans.getStmtExecInterface().fetchColumnValue(sql, args);
		} catch (UnsupportedDBDataTypeException ex) {
			System.out.println("XXX: " + ex);
			Logger.getLogger(ShowTablesMySql.class.getName()).log(Level.SEVERE,
					null, ex);
			return null;
		}

		for (int i = 0; i < res.size(); i++) {
			String val = (String) res.get(i).get(1);
			return val;
		}            
                
                return null;
        }

	public boolean db_supports_all_requested_features(Transaction trans)
			throws SQLException {

		String sql = "SHOW VARIABLES LIKE 'have_innodb'";

		List<DBDataType> args = new Vector<DBDataType>();
		args.add(DBDataType.DB_TYPE_STRING);
		args.add(DBDataType.DB_TYPE_STRING);
		List<List<?>> res;

		/*
		 * Eine UnsupportedDBDataTypeException Exception sollte hier ja eher
		 * nicht geworfen werden, weil wir sollten schon wissen, was wir tun.
		 */
		try {
			res = trans.getStmtExecInterface().fetchColumnValue(sql, args);
		} catch (UnsupportedDBDataTypeException ex) {
			System.out.println("XXX: " + ex);
			Logger.getLogger(ShowTablesMySql.class.getName()).log(Level.SEVERE,
					null, ex);
			return false;
		}

		for (int i = 0; i < res.size(); i++) {
			String val = (String) res.get(i).get(1);
			if (val.equalsIgnoreCase("YES"))
				return true;
		}

		return false;
	}

}
