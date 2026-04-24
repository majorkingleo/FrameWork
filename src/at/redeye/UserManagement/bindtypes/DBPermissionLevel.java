/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.UserManagement.bindtypes;

import at.redeye.FrameWork.base.bindtypes.*;

import java.util.Vector;

/**
 *
 * @author martin
 */
public class DBPermissionLevel extends DBEnum 
{
    public static enum PERMISSIONLEVEL
    {
        Administrator,
        Operator,
        Benutzer
    };
           
    
    public static class PermEnumHandler extends DBEnum.EnumHandler
    {
        PERMISSIONLEVEL types;

        public PermEnumHandler()
        {
            types = PERMISSIONLEVEL.Benutzer;
        }
        
        @Override
        public int getMaxSize() {
            int max=0;
            
            for( PERMISSIONLEVEL val :PERMISSIONLEVEL.values() )
            {
                if( max < val.toString().length() )
                    max = val.toString().length();
            }
            
            return max;
        }

        @Override
        public boolean setValue(String val) {
            try {
                types  = PERMISSIONLEVEL.valueOf(val);
            } catch( IllegalArgumentException ex ) {
                return false;
            }
            return true;
        }

        @Override
        public String getValue() {
            return types.toString();
        }

        @Override
        public EnumHandler getNewOne() {
            return new PermEnumHandler();
        }

        @Override
        public Vector<String> getPossibleValues() {
            Vector<String> res = new Vector<String>();
            
            for( PERMISSIONLEVEL t : PERMISSIONLEVEL.values() )
                res.add( t.toString() );
            
            return res;
        }
    }
    
    public DBPermissionLevel( String name, String title )
    {
        super( name, title, new PermEnumHandler() );
    }

    public DBPermissionLevel getNewOne()
    {
        return new DBPermissionLevel( name, title );
    }
}
