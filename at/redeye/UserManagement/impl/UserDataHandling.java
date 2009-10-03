package at.redeye.UserManagement.impl;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.ConnectionDialog;
import at.redeye.FrameWork.base.DBConnection;
import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.MD5Calc;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.UserManagementDialogs;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.UserManagementListener;
import at.redeye.UserManagement.bindtypes.DBPb;

public class UserDataHandling implements UserManagementInterface {

    private static final long serialVersionUID = 1L;
    private static final String setUpPwd = "4ad5361a939701c0fb174f7b9e1aca43"; // initial09
    private static final String setUpUser = "admin";
    private static Logger logger = Logger.getLogger(UserDataHandling.class.getSimpleName());
    private Root root;
    private String logoPath = "";
    private Vector<UserManagementListener> registeredListener;
    private boolean auto_login_feature_activated = true;

    public UserDataHandling(Root root) {
        super();
        this.root = root;
        registeredListener = new Vector<UserManagementListener>();
    }

    public DBPb checkUserData(String login, String pwd, boolean autoLoginRequested)
            throws InvalidLoginException, SQLException,
            UnsupportedDBDataTypeException, TableBindingNotRegisteredException,
            WrongBindFileFormatException, CloneNotSupportedException,
            UserLockedException {

        if (login.equals(setUpUser) && getEncryptedPwd(pwd).equalsIgnoreCase(setUpPwd)) {

            Object[] options = {"Datenbank einrichten", "Benutzer warten",
                "Abbrechen"};

            int n = JOptionPane.showOptionDialog(null,
                    "M\u00f6chten Sie die Datenbank einrichten,\n" + "oder die Benutzerdaten pflegen?\n",
                    "Initiales Setup", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            switch (n) {
                case 0:
                    new ConnectionDialog(root).setVisible(true);
                    break;
                case 1:
                    requestDialog(UserManagementDialogs.UM_ADMINISTRATION_DIALOG);
                    break;
                default:
                    return null;
            }

            return null;
        }

        Vector<DBStrukt> pbrecords = getAllUserData();
        String encPwd = null;
        DBPb currpb = null;
        if (pbrecords.size() > 0) {

            boolean found = false;

            for (int index = 0; index < pbrecords.size(); index++) {
                currpb = (DBPb) pbrecords.get(index);

                if (login.equals(currpb.login.toString())) {
                    encPwd = getEncryptedPwd(pwd);
                    System.out.println(encPwd);
                    found = true;
                    break;
                }

            }

            if (currpb != null && found == true &&
                    autoLoginRequested == true) {
                // Auto Login -> let password check pass
                encPwd = currpb.pwd.toString();
            }

            if (found == true && currpb != null && encPwd != null &&
                    encPwd.equalsIgnoreCase(currpb.pwd.toString())) {

                if ((Integer) currpb.locked.getValue() ==
                        UserManagementInterface.UM_ACCOUNT_LOCKED) {
                    throw new UserLockedException("Benutzer ist gesperrt!");
                }

                return currpb;

            } else {
                throw new InvalidLoginException("Wrong User data!");
            }

        }
        throw new InvalidLoginException("No User found (Empty database)!");
    }

    protected String getEncryptedPwd(String input) {
        MD5Calc md5 = new MD5Calc("MD5");
        return md5.calcChecksum(input);
    }

    public void requestDialog(UserManagementDialogs dialog) {
        // TODO Auto-generated method stub
        switch (dialog) {

            case UM_LOGIN_DIALOG:

                new LoginDlg(root, this).setVisible(true);
                break;

            case UM_ADMINISTRATION_DIALOG:

                new AdminDlg(root).setVisible(true);
                break;
            default:
                break;

        }
    }

    public Vector<DBStrukt> getAllUserData() throws SQLException,
            TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
            WrongBindFileFormatException, CloneNotSupportedException {

        DBConnection conn = root.getDBConnection();
        Transaction tid = conn.getNewTransaction();
        Vector<DBStrukt> pbrecords = tid.fetchTable(new DBPb());

        conn.closeTransaction(tid);

        return pbrecords;
    }

    public DBPb getUserData(DBPb pb) throws SQLException,
            TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
            WrongBindFileFormatException, CloneNotSupportedException {

        DBConnection conn = root.getDBConnection();
        Transaction tid = conn.getNewTransaction();

        if (tid.fetchTableWithPrimkey(pb) != true) {
            throw new SQLException("Reading PB data for id " + pb.id +
                    " returned false!");
        }

        return pb;

    }

    @Override
    public void addUMListener(UserManagementListener listener) {

        if (listener != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Adding listener...");
            }
            registeredListener.add(listener);
        }

    }

    @Override
    public void removeUMListener(UserManagementListener listener) {
        if (listener != null) {
            registeredListener.remove(listener);
        }

    }

    @Override
    public void updateListeners() {
        if (logger.isDebugEnabled()) {
            logger.debug("Calling update, registered listener: " +
                    registeredListener.size());
        }
        for (UserManagementListener currListener : registeredListener) {

            currListener.accessGranted();
        }

    }

    public void setLogo(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getLogo() {
        return logoPath;
    }

    public boolean tryAutoLogin() {

        if( !auto_login_feature_activated  )
        {
            return false;
        }

        String strAutoLogin =
                root.getSetup().
                getConfig(FrameWorkConfigDefinitions.AllowAutoLogin);
        boolean autoLoginAllowed = Boolean.parseBoolean(strAutoLogin);
        if (autoLoginAllowed != true) {
            return false;
        }

        String autoLoginUser =
                root.getSetup().
                getLocalConfig(FrameWorkConfigDefinitions.AutoLoginUser);

        if (autoLoginUser.isEmpty()) {
            return false;
        }

        DBPb pb;
        try {
            pb = checkUserData(autoLoginUser, "", true);
            if (pb == null) {
                return false;
            }
            updateListeners();
            logger.info ("\n\n" + pb.getUserName() + " : AUTO Login!\n\n");
            return true;

        } catch (InvalidLoginException ex) {
            logger.error("<tryAutoLogin>: Falsches Login: " + ex.getMessage());
            return false;
        } catch (SQLException ex) {
            logger.error("<tryAutoLogin>: Datenbank Fehler: " + ex.getMessage());
            return false;
        } catch (UnsupportedDBDataTypeException ex) {
            logger.error("<tryAutoLogin>: Allg. Fehler: " + ex.getMessage());
            return false;
        } catch (TableBindingNotRegisteredException ex) {
            logger.error("<tryAutoLogin>: Falscher Bindedeskriptor: " +
                    ex.getMessage());
            return false;
        } catch (WrongBindFileFormatException ex) {
            logger.error("<tryAutoLogin>: Falscher Bindedeskriptor: " +
                    ex.getMessage());
            return false;
        } catch (CloneNotSupportedException ex) {
            logger.error("<tryAutoLogin>: Allg. Fehler: " +
                    ex.getMessage());
            return false;
        } catch (UserLockedException ex) {
            logger.error("<tryAutoLogin>: Benutzer ist gesperrt! " +
                    ex.getMessage());
            return false;
        }




    }

    public void setAutoLogin(boolean state)
    {
        auto_login_feature_activated = state;
    }
}
