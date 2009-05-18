/*

 * To change this template, choose Tools | Templates

 * and open the template in the editor.

 */



package at.redeye.FrameWork.base.bindtypes;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMColumnAttribute;



/**

 *

 * @author martin

 */

public abstract class DBStrukt {
   
    protected String name;
    protected String title = new String();
    public Vector<DBValue> elements = new Vector<DBValue>();
    public Vector<DBStrukt> sub_strukts = new Vector<DBStrukt>();
    protected String version = null;

    public DBStrukt( String name )
    {
        this.name = name;
    }

    

    public DBStrukt( String name, String title )
    {
        this.name = name;
        this.title = title;
    }

    

    public void add( DBValue value )
    {
        elements.add( value );
    }

    

    public void add( DBStrukt substrukt )
    {
        sub_strukts.add(substrukt);
    }

    

    public void consume(HashMap<String, Object> map ) 
    {
        consume( map, "" );
    }

    

    public void consume(HashMap<String, Object> map, String prefix ) 
    {        
        Set<String> keys = map.keySet();
        
        for( String key : keys )
        {
            if( key.length() <= prefix.length() )
                continue;            
            
            String k = key.substring(prefix.length());           
            
            DBValue val = getValueByName( k );
            
            if( val != null )
            {
                val.loadFromDB(map.get(key));
                continue;
            }
            
            for( int i = 0; i < sub_strukts.size(); i++ )
            {
                DBStrukt strukt =  sub_strukts.get(i);                

                if( k.startsWith( strukt.getName() + "_" ) )
                {
                    strukt.consume( map, prefix + strukt.getName() + "_" );
                    break;
                }
            }

        }
    }

    

    public String getName()
    {
        return name;
    }

    

    public DBValue getValue( int idx )
    {
        return elements.get(idx);
    }

    

    public int countValues()
    {
        return elements.size();
    }

    

    public int countSubStrukts()
    {
        return sub_strukts.size();
    }

    

    public DBStrukt getSubStrukt( int idx )
    {
        return sub_strukts.get(idx);
    }

    

    public HashMap<String, MOMMColumnAttribute> getHashMap()
    {
        return getHashMap( "" );
    }    

    protected HashMap<String, MOMMColumnAttribute> getHashMap( String prefix )
    {
        HashMap<String, MOMMColumnAttribute> colls = new HashMap<String, MOMMColumnAttribute>();      

        for( int i = 0; i < elements.size(); i++ )   
        {
            DBValue val = elements.get(i);
            
            MOMMColumnAttribute attr = new MOMMColumnAttribute( val.getDBType() );
            
            attr.setPrimaryKey(val.isPrimaryKey());
            attr.setHasIndex(val.shouldHaveIndex());
            
            if( DBString.class.isInstance(val) ) {
                attr.setWidth( ((DBString)val).getMaxLen() );
            } else if( val instanceof DBEnum ) {
                attr.setWidth( ((DBEnum)val).getMaxLen() );
            }
            
            colls.put( prefix + val.getName(), attr );
        }               

        for( int i = 0; i < sub_strukts.size(); i++ )
        {
            DBStrukt strukt = sub_strukts.get(i);
            
            HashMap<String, MOMMColumnAttribute> sub_colls = strukt.getHashMap( prefix + strukt.getName() + "_" );
            
            Set<String> keys = sub_colls.keySet();
            
            for( String s : keys )
            {
                colls.put( s, sub_colls.get(s) );
            }
        }
        
        return colls;
    }

    public HashMap<String, Object> getHashMapAndData()
    {
        return getHashMapAndData( "" );
    }
    
    protected HashMap<String, Object> getHashMapAndData( String prefix )
    {
        HashMap<String, Object> colls = new HashMap<String, Object>();      

        for( int i = 0; i < elements.size(); i++ )   
        {
            DBValue val = elements.get(i);                        
            colls.put( prefix + val.getName(), val.getValue() );
        }
        
        for( int i = 0; i < sub_strukts.size(); i++ )
        {
            DBStrukt strukt = sub_strukts.get(i);
            
            HashMap<String, Object> sub_colls = strukt.getHashMapAndData( prefix + strukt.getName() + "_" );
            
            Set<String> keys = sub_colls.keySet();
            
            for( String s : keys )
            {
                colls.put( s, sub_colls.get(s) );
            }
        }
        
        return colls;
    }

    public Vector<DBValue> getAllValues() 
    {
        Vector<DBValue> values = new Vector<DBValue>();
        
        for( int i = 0; i < elements.size(); i++ )   
        {
            DBValue val = elements.get(i);
            
            values.add(val);
        }
        
        for( int i = 0; i < sub_strukts.size(); i++ )
        {
            DBStrukt strukt = sub_strukts.get(i);
           
            values.addAll( strukt.getAllValues() );
        }
        
        return values;
    }
    
    public Vector<String> getAllNames() 
    {
        return getAllNames( "" );
    }
    
    protected Vector<String> getAllNames( String prefix ) 
    {
        Vector<String> values = new Vector<String>();
        
        for( int i = 0; i < elements.size(); i++ )   
        {
            DBValue val = elements.get(i);            

            if( val.getTitle().isEmpty() )
                values.add( prefix + val.getName() );
            else
                values.add( prefix + val.getTitle() );
        }
        
        for( int i = 0; i < sub_strukts.size(); i++ )
        {
            DBStrukt strukt = sub_strukts.get(i);
            
            if( strukt.getTitle().isEmpty() )                
                values.addAll( strukt.getAllNames( strukt.getName() + " " ) );
            else
                values.addAll( strukt.getAllNames( strukt.getTitle() + " " ) );    
        }
       
        return values;
    }

    

    public abstract DBStrukt getNewOne();



    private String getTitle() {
        return title;
    }

    



    private DBValue getValueByName(String key) 
    {        
        for( int i  = 0; i < elements.size(); i++ )
        {
            if( key.equalsIgnoreCase(elements.get(i).getName()) )
                return elements.get(i);
        }

        return null;
    }


    public void loadFromCopy( DBStrukt s )
    {               
        for( int i = 0; i < s.elements.size(); i++ )
        {
            DBValue val = s.elements.get(i);           

            elements.get(i).loadFromCopy(val.getValue());
        }
        
        for( int i = 0; i < s.sub_strukts.size(); i++ )
        {
            sub_strukts.get(i).loadFromCopy(s.sub_strukts.get(i));
        }
    }

    

    public DBStrukt getCopy()
    {
        DBStrukt s = getNewOne();

        s.loadFromCopy(this);

        return s;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }
    
    public String getVersion()
    {
        if( version == null )
            return "0.1";
        
        return version;
    }    

}

