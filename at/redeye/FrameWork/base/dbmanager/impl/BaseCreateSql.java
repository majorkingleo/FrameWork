/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMColumnAttribute;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Administrator
 */
public abstract class BaseCreateSql {
    
        
    public String createSqlforTable( DBStrukt strukt )
    {
        String res = new String();
        
        Vector<String> primKeys = new Vector<String>();
        Vector<String> indexKeys = new Vector<String>();
        
        res += "create table " + markColumn(strukt.getName()) + " (";
        
        HashMap<String, MOMMColumnAttribute> colls = strukt.getHashMap();
        
        Set<String> keys = colls.keySet();
        
        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
        {
            String name = it.next();
            
            MOMMColumnAttribute attr = colls.get( name );
            
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
            res += createPrimKeys( strukt.getName(), primKeys );
        }
        
        if( !indexKeys.isEmpty() )                    
        {
            res += createIndexKeys( strukt.getName(), indexKeys );
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
            res.append( markColumn( "IDX_" + key.toUpperCase() ) );
            res.append( "(" );
            res.append( markColumn( key ));
            res.append(");\n");
        }
        
        return res.toString();
    }
    
    protected abstract String createSqlForRow( MOMMColumnAttribute attr ); 

    protected abstract String addStorageInfo() ;
  
    public abstract String markColumn (String col);        
}
