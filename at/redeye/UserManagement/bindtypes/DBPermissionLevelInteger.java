/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.UserManagement.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBEnumAsInteger;
import at.redeye.UserManagement.UserManagementInterface;

import java.util.Vector;

/**
 *
 * @author martin
 */
public class DBPermissionLevelInteger extends DBEnumAsInteger 
{
    public static class PermissionLevelHandler extends EnumAsIntegerHandler
    {
        public static final String AdminStr = "Administrator";
        public static final String OperatorStr = "Operator";
        public static final String NormalStr = "Benutzer";
        
        Integer value = UserManagementInterface.UM_PERMISSIONLEVEL_NORMAL;
        
        @Override
        public int getMaxSize() {
            return 1;
        }

        @Override
        public boolean setValue(String val) {
            if( val.equalsIgnoreCase(AdminStr) )
                value = UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN;
            else if( val.equalsIgnoreCase(OperatorStr ) )
                value = UserManagementInterface.UM_PERMISSIONLEVEL_PRIVILEGED;
            else if( val.equalsIgnoreCase(NormalStr) )
                value = UserManagementInterface.UM_PERMISSIONLEVEL_NORMAL;
            else
                return false;
            
            return true;
        }

        @Override
        public boolean setValue(Integer val) {
            if( val == UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN ||
                val ==  UserManagementInterface.UM_PERMISSIONLEVEL_NORMAL ||
                val == UserManagementInterface.UM_PERMISSIONLEVEL_PRIVILEGED )
            {
                value = val;
                return true;
            }
            
            return false;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getValueAsString() {
            switch( value )
            {
                case UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN: 
                    return AdminStr;
                case UserManagementInterface.UM_PERMISSIONLEVEL_PRIVILEGED: 
                    return OperatorStr;
                case UserManagementInterface.UM_PERMISSIONLEVEL_NORMAL:
                    return NormalStr;
            }
            return "";
        }

        @Override
        public EnumAsIntegerHandler getNewOne() {
            return new PermissionLevelHandler();
        }

        @Override
        public Vector<String> getPossibleValues() {
           Vector<String> res = new Vector<String>();
           
           res.add( NormalStr );
           res.add( OperatorStr );
           res.add( AdminStr );
           
           return res;
        }
        
    }
    
    
    public DBPermissionLevelInteger( String name, String title )
    {        
        super( name, title, new PermissionLevelHandler() );
    }
    
    public DBPermissionLevelInteger getNewOne()
    {
        return new DBPermissionLevelInteger(name, title);
    }
}
