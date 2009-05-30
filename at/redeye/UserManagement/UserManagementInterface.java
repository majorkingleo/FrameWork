package at.redeye.UserManagement;

import java.sql.SQLException;
import java.util.Vector;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.InvalidLoginException;
import at.redeye.UserManagement.impl.UserLockedException;

public interface UserManagementInterface {

	public final int UM_PERMISSIONLEVEL_NORMAL = 1;
	public final int UM_PERMISSIONLEVEL_PRIVILEGED = 2;
	public final int UM_PERMISSIONLEVEL_ADMIN = 3;

	public final int UM_ACCOUNT_LOCKED = 1;
	public final int UM_ACCOUNT_UNLOCKED = 0;
	
	public void requestDialog(UserManagementDialogs dialog);

	public Vector<DBStrukt> getAllUserData() throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException;

    public DBPb getUserData(DBPb pb) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException;


	public DBPb checkUserData(String login, String pwd)
			throws InvalidLoginException, SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException,
			WrongBindFileFormatException, CloneNotSupportedException, UserLockedException;

    public void setLogo (String logoPath);

    public String getLogo ();

	public void addUMListener(UserManagementListener listener);

	public void removeUMListener(UserManagementListener listener);

	public void updateListeners();

}
