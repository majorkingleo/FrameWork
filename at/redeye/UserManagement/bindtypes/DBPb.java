package at.redeye.UserManagement.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

public class DBPb extends DBStrukt {

	public DBInteger id = new DBInteger("id", "Id");
	public DBString title = new DBString("title", "Titel", 20);
	public DBString name = new DBString("name", "Name", 35);
	public DBString surname = new DBString("surname", "Nachname", 35);
	public DBString login = new DBString("login", "Login", 20);
	public DBString pwd = new DBString("pwd", "Passwort", 32);
	public DBPermissionLevelInteger plevel = new DBPermissionLevelInteger("plevel", "Berechtigungsstufe");
	public DBFlagInteger locked = new DBFlagInteger("locked", "Gesperrt");
	public DBHistory hist = new DBHistory("hist");

	public DBPb() {
		super("PB");
		id.setAsPrimaryKey(true);
		add(id);
		add(title);
		add(name);
		add(surname);
		add(login);
		add(pwd);
		add(plevel);
		add(locked);
		add(hist);

	}

	@Override
	public DBStrukt getNewOne() {
		return new DBPb();
	}

    public String getUserName()
    {
        String text = new String();

        if (!title.toString().isEmpty()) {
            text += title.toString();
        }
        if (!surname.toString().isEmpty()) {
            if (!text.isEmpty()) {
                text += " ";
            }
            text += surname;
        }

        if (!name.toString().isEmpty()) {
            if (!text.isEmpty()) {
                text += " ";
            }
            text += name;
        }

        if (text.isEmpty()) {
            text = login.toString();
        }
        
        return text;
    }
    
    public String getLogin()
    {
        return login.toString();
    }
            
    public int getUserId()
    {
        return (Integer)id.getValue();
    }
}
