/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Administrator
 */
public abstract class BaseCreateSql implements BackupTableInterface {
    
        
    public String createSqlforTable( DBStrukt strukt )
    {
        String res = new String();
        
        Vector<String> primKeys = new Vector<String>();
        Vector<String> indexKeys = new Vector<String>();
        
        res += "create table " + markColumn(strukt.getName()) + " (";
        
        HashMap<String, ColumnAttribute> colls = strukt.getHashMap();
        
        Set<String> keys = colls.keySet();
        
        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
        {
            String name = it.next();
            
            ColumnAttribute attr = colls.get( name );
            
            res +=   markColumn(name) + " " + createSqlForRow( attr ) + " NOT NULL";
            
            if( it.hasNext() )
                res = res + ",\n";
            
            if( attr.isPrimaryKey() )
                primKeys.add(name);
            
            if( attr.hasIndex() )
                indexKeys.add(name);
        }
        
        res += ")" + addStorageInfo() + ";\n";
        
        if( !primKeys.isEmpty() )                    
        {
            res += createPrimKeys( strukt.getName(), primKeys ) + ";";
        }
        
        if( !indexKeys.isEmpty() )                    
        {
            res += createIndexKeys( strukt.getName(), indexKeys ) + ";";
        }
        
        return res;
    }

    protected String createPrimKeys( String table, Vector<String> primKeys )
    {
        String res = "ALTER TABLE " + markColumn(table) + " ADD PRIMARY KEY (";
                        
        for( int i = 0; i < primKeys.size(); i++ )
        {
            if( i > 0 )
                res += ",";
            
            res += markColumn(primKeys.get(i));                        
        }                
        
        return res + ");\n";
    }
    
    protected String createIndexKeys( String table, Vector<String> indexKeys )
    {
        StringBuilder res = new StringBuilder();
        
        for( String key : indexKeys )
        {        
            res.append( "ALTER TABLE ");
            res.append( markColumn(table) );
            res.append( " ADD INDEX ");
            res.append( markColumn( "IDX_" + table.toUpperCase() + "_" + key.toUpperCase() ) );
            res.append( "(" );
            res.append( markColumn( key ));
            res.append(");\n");
        }
        
        return res.toString();
    }
    
    protected abstract String createSqlForRow( ColumnAttribute attr ); 

    protected abstract String addStorageInfo() ;
  
    public abstract String markColumn (String col);        
    
    @Override
    public String createSqlForBackup( String table, String target_name )
    {
        String res = "create table " + markColumn( target_name ) + " as select * from " + markColumn( table );
        
        return res;
    }
    
    @Override
    public String createSqlDropTable( String table )
    {
        String res = "drop table " + markColumn( table );
        
        return res;
    }
    
    public String createSqlForNewRows( DBStrukt strukt, Integer Version )
    {
        String res = new String();
        
        Vector<String> primKeys = new Vector<String>();
        Vector<String> indexKeys = new Vector<String>();                
        
        HashMap<String, ColumnAttribute> colls = strukt.getHashMapForVersion(Version);
        
        Set<String> keys = colls.keySet();
        
        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
        {
            String name = it.next();
            
            res += "alter table " + markColumn(strukt.getName()) + " add ";
            
            ColumnAttribute attr = colls.get( name );
            
            res +=   markColumn(name) + " " + createSqlForRow( attr ) + " " + appendNotNullIfSupportedbyNewRows( attr ) + ";\n";
            
            if( attr.isPrimaryKey() )
                primKeys.add(name);
            
            if( attr.hasIndex() )
                indexKeys.add(name);
        }                                
        
        if( !primKeys.isEmpty() )                    
        {
            res += createPrimKeys( strukt.getName(), primKeys );
        }
        
        if( !indexKeys.isEmpty() )                    
        {
            res += createIndexKeys( strukt.getName(), indexKeys );
        }
        
        return res;        
    }

    protected String appendNotNullIfSupportedbyNewRows(ColumnAttribute attr) {
        return " NOT NULL";
    }
}
