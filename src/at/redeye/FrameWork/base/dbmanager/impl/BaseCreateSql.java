/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.FKAction;
import at.redeye.FrameWork.base.bindtypes.ForeignKeyDefinition;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public abstract class BaseCreateSql implements BackupTableInterface {
    
    static final Logger logger = Logger.getLogger(BaseCreateSql.class.getName());        
    
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
            
            res +=   markColumn(name) + " " + createSqlForRow( attr );
				// Add NOT NULL if: column cannot be null OR it's a primary key (PKs are always NOT NULL)
				if (!attr.canBeNull() || attr.isPrimaryKey()) {
					res += " NOT NULL";
				}
            
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

    protected String createPrimKeys( String table, Collection<String> primKeys )
    {
        String res = "ALTER TABLE " + markColumn(table) + " ADD PRIMARY KEY (";
                        
        boolean first = true;
        
        for( String key : primKeys )
        {
            if( !first )
                res += ",";
            
            first = false;
            
            res += markColumn(key);
        }
        
        return res + ");\n";
    }
    
    protected String createIndexKeys( String table, Collection<String> indexKeys )
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
        
        ArrayList<String> primKeys = new ArrayList<String>();
        ArrayList<String> indexKeys = new ArrayList<String>();                
        
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

    protected String getDefaultValueVarChar(int length) {

        StringBuilder str = new StringBuilder();
        str.append("'");
        for (int i = 0; i < length; i++) {
            str.append(" ");
        }
        str.append("'");
        return str.toString();
    }

    // -----------------------------------------------------------------------
    // Foreign key DDL  Generated by AI (GitHub Copilot / Claude Sonnet 4.6)
    // -----------------------------------------------------------------------

    /**
     * Generates one {@code ALTER TABLE ADD CONSTRAINT} statement per FK
     * declared on {@code strukt}. Returns an empty string when the strukt has
     * no foreign keys (safe to pass to {@code execSql} which ignores blanks).
     *
     * @param strukt the table whose FK constraints should be created
     * @return {@code ;}-terminated DDL string, or {@code ""} if no FKs exist
     */
    public String createFKSql( DBStrukt strukt )
    {
        StringBuilder sb = new StringBuilder();
        for ( ForeignKeyDefinition fk : strukt.getForeignKeys() )
        {
            sb.append( "ALTER TABLE " ).append( markColumn( strukt.getName() ) )
              .append( " ADD CONSTRAINT " ).append( markColumn( fk.getName() ) )
              .append( " FOREIGN KEY (" ).append( markColumn( fk.getOwnerColumn() ) ).append( ")" )
              .append( " REFERENCES " ).append( markColumn( fk.getReferencedTable() ) )
              .append( " (" ).append( markColumn( fk.getReferencedColumn() ) ).append( ")" )
              .append( " ON DELETE " ).append( fkActionSql( fk.getOnDelete() ) )
              .append( " ON UPDATE " ).append( fkActionSql( fk.getOnUpdate() ) )
              .append( ";\n" );
        }
        return sb.toString();
    }

    /**
     * Generates one drop statement per FK declared on {@code strukt}.
     * The default implementation uses ANSI {@code DROP CONSTRAINT}.
     * MySQL / MariaDB override {@link #dropFKStatement} to use
     * {@code DROP FOREIGN KEY}; SQLite overrides it to return an empty string.
     *
     * @param strukt the table whose FK constraints should be dropped
     * @return {@code ;}-terminated DDL string, or {@code ""} if no FKs exist
     */
    public String dropFKSql( DBStrukt strukt )
    {
        StringBuilder sb = new StringBuilder();
        for ( ForeignKeyDefinition fk : strukt.getForeignKeys() )
        {
            String stmt = dropFKStatement( strukt.getName(), fk.getName() );
            if ( stmt != null && !stmt.isEmpty() )
                sb.append( stmt ).append( ";\n" );
        }
        return sb.toString();
    }

    // AI modification start (GitHub Copilot / Claude Sonnet 4.6)
    /**
     * Generates drop statements only for FK constraints introduced at schema
     * version &lt;= {@code maxVersion}. Use this during migration to avoid
     * attempting to drop FKs that do not yet exist in the old schema.
     *
     * Generated by AI (GitHub Copilot / Claude Sonnet 4.6)
     *
     * @param strukt     the table whose FK constraints should be dropped
     * @param maxVersion upper bound (inclusive) for the schema version
     * @return {@code ;}-terminated DDL string, or {@code ""} if no FKs qualify
     */
    public String dropFKSqlUpToVersion( DBStrukt strukt, int maxVersion )
    {
        StringBuilder sb = new StringBuilder();
        for ( ForeignKeyDefinition fk : strukt.getForeignKeysUpToVersion( maxVersion ) )
        {
            String stmt = dropFKStatement( strukt.getName(), fk.getName() );
            if ( stmt != null && !stmt.isEmpty() )
                sb.append( stmt ).append( ";\n" );
        }
        return sb.toString();
    }
    // AI modification end

    /**
     * Builds a single DROP statement for one FK constraint.
     * Override in dialect subclasses where the syntax differs.
     *
     * <p>Default: ANSI {@code ALTER TABLE <t> DROP CONSTRAINT <name>}</p>
     *
     * @param table          owning table name
     * @param constraintName constraint name to drop
     * @return the DDL fragment (without trailing semicolon), or {@code ""} to skip
     */
    protected String dropFKStatement( String table, String constraintName )
    {
        return "ALTER TABLE " + markColumn( table )
             + " DROP CONSTRAINT " + markColumn( constraintName );
    }

    /**
     * Converts a {@link FKAction} value to its SQL keyword(s).
     * {@code NO_ACTION} becomes {@code "NO ACTION"} (two words).
     *
     * @param action the referential action
     * @return SQL keyword string
     */
    protected String fkActionSql( FKAction action )
    {
        return action.name().replace( '_', ' ' );
    }
}
