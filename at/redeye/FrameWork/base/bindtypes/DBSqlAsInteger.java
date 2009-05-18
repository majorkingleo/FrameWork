/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBEnumAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;

import java.util.Vector;

/**
 *
 * @author martin
 */
public class DBSqlAsInteger extends DBEnumAsInteger 
{
    
    public static abstract class SqlQuery
    {
        public static class Pair
        {
            public Integer val;
            public String  text;
            
            public Pair( Integer val, String text )
            {
                this.val = val;
                this.text = text;
            }
        }
        
        public abstract Vector<Pair> getPossibleValues();
        
        public abstract int getDefaultValue();
    }
    
    public static class SqlAsIntegerHandler extends EnumAsIntegerHandler
    {                
        Integer value = 0;
        public SqlQuery query = null;
        public Vector<SqlQuery.Pair> pairs;
        
        SqlAsIntegerHandler( SqlQuery query )
        {
            this.query = query;
            pairs = query.getPossibleValues();
            this.value = query.getDefaultValue();
        }
        
        @Override
        public int getMaxSize() {
            return 1;
        }

        @Override
        public boolean setValue(String val) {
            
        
            for( int i = 0; i < pairs.size(); i++ )
            {
                if( val.equals(pairs.get(i).text) )
                {
                    value = new Integer(pairs.get(i).val);
                    return true;
                }
            }
            
            return false;
        }

        @Override
        public boolean setValue(Integer val) {
            for( int i = 0; i < pairs.size(); i++ )
            {
                if( pairs.get(i).val.equals(val) )
                {
                    value = new Integer(val);
                    return true;
                }
            }                        
            
            return false;
        }

        @Override
        public Integer getValue() {
            return new Integer(value);
        }

        @Override
        public String getValueAsString() {
            
            for( int i = 0; i < pairs.size(); i++ )
            {
                if( pairs.get(i).val.equals(value) )
                {
                    return pairs.get(i).text;                    
                }
            }
            
            return "";
        }

        @Override
        public SqlAsIntegerHandler getNewOne() {
            return new SqlAsIntegerHandler( query );
        }

        @Override
        public Vector<String> getPossibleValues() {
           Vector<String> res = new Vector<String>();
                                            
           for( SqlQuery.Pair p : pairs )
               res.add(p.text);
           
           return res;
        }
        
    }       
    
    public SqlQuery query;
    
    public DBSqlAsInteger( String name, String title, SqlQuery query )
    {                
        super( name, title, new SqlAsIntegerHandler( query ) );
        this.query = query;
    }
    
    public DBSqlAsInteger getNewOne()
    {        
        return new DBSqlAsInteger(name, title, query );
    }
    
    @Override
    public DBValue getCopy() {
        DBSqlAsInteger copy = new DBSqlAsInteger(name, title, query);
        copy.handler.setValue(handler.getValue());
        return copy;
    }
    
    @Override
    public String toString()
    {
        return handler.getValueAsString();
    }
}
