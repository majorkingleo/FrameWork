/*

 * To change this template, choose Tools | Templates

 * and open the template in the editor.

 */

package at.redeye.FrameWork.base.bindtypes;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;

/**
 * @author martin
 */

public abstract class DBStrukt {

	protected String strukt_name;
	protected String title;
	public Vector<DBValue> elements = new Vector<DBValue>();
	public HashMap<String, DBValue> element_by_name = new HashMap<String, DBValue>();
	public Vector<DBStrukt> sub_strukts = new Vector<DBStrukt>();
	protected Integer version = null;
	protected Vector<Entry<Integer, DBValue>> elements_with_version = new Vector<Entry<Integer, DBValue>>();

	public DBStrukt(String name) {
		this.strukt_name = name;
		title = new String();
	}

	public DBStrukt(String name, String title) {
		this.strukt_name = name;
		this.title = title;
	}

	public void add(DBValue value) {
		elements.add(value);
		element_by_name.put(value.getName().toLowerCase(), value);
	}

        /**
         * removes an element. The DBValue has to be from the same instance
         * as this object
         * @param value 
         */
	public void remove(DBValue value) {
		elements.remove(value);
		element_by_name.remove(value.getName().toLowerCase());
	}

	public void add(DBValue value, Integer version) {
		elements.add(value);
		element_by_name.put(value.getName().toLowerCase(), value);
		elements_with_version.add(new SimpleEntry<Integer, DBValue>(version,
				value));

		if (this.version == null)
			this.version = version;
		else if (version > this.version)
			this.version = version;
	}

	public void add(DBStrukt substrukt) {
		sub_strukts.add(substrukt);
	}

	public void consume(HashMap<String, Object> map) {
		consume(map, null);
	}

	public void consume(HashMap<String, Object> map, String prefix) {
		// Set<String> keys = map.keySet();
        
        Set<Entry<String, Object>> entries = map.entrySet();
        String k;
        
		for (Entry<String, Object> entry : entries) {
			if (prefix != null && entry.getKey().length() <= prefix.length())
				continue;			

			if (prefix != null)
				k = entry.getKey().substring(prefix.length());
			else
				k = entry.getKey();

			DBValue val = getValueByName(k);

			if (val != null) {
				val.loadFromDB(entry.getValue());
				continue;
			}

			for (int i = 0; i < sub_strukts.size(); i++) {
				DBStrukt strukt = sub_strukts.get(i);

				if (k.startsWith(strukt.getName()) && (k.charAt(strukt.getName().length()) == '_')) {                    
					if (prefix != null)
						strukt.consume(map, prefix + strukt.getName() + "_");
					else
						strukt.consume(map, strukt.getName() + "_");

					break;
				}
			}
		}
	}

	public String getName() {
		return strukt_name;
	}

        /**
         * Get DBValue by its index. Each member that is added to DBStrukt by using the add()
         * method is stored in a vector. So the elements can also be accessed by the idx of this
         * vector.
         * @param idx
         * @return DBValue 
         */
	public DBValue getValue(int idx) {
		return elements.get(idx);
	}

        /**
         * retuns the DBValue by searching the element by its name by using DBValue.getName() function
         * @param val
         * @return
         */
	public DBValue getValue(DBValue val) {
		return getValue(val.getName());
	}

	public DBValue getValue(String name) {
		for (DBValue val : elements) {
			if (val.getName().equals(name))
				return val;
		}
		return null;
	}

	public int countValues() {
		return elements.size();
	}

	public int countSubStrukts() {
		return sub_strukts.size();
	}

	public DBStrukt getSubStrukt(int idx) {
		return sub_strukts.get(idx);
	}

	public HashMap<String, ColumnAttribute> getHashMap() {
		return getHashMap("");
	}

	protected HashMap<String, ColumnAttribute> getHashMap(String prefix) {
		return getHashMap(prefix, null);
	}

	protected boolean VersionExists(DBValue val, Integer Version) {
		for (Entry<Integer, DBValue> pair : elements_with_version) {
			if ((int) pair.getKey() == (int) Version) {
				if (pair.getValue() == val)
					return true;
			}
		}

		return false;
	}

	public HashMap<String, ColumnAttribute> getHashMapForVersion(Integer Version) {
		return getHashMap("", Version);
	}

	protected HashMap<String, ColumnAttribute> getHashMap(String prefix,
			Integer Version) {
		HashMap<String, ColumnAttribute> colls = new HashMap<String, ColumnAttribute>();

		for (int i = 0; i < elements.size(); i++) {
			DBValue val = elements.get(i);

			if (Version != null) {
				if (!VersionExists(val, Version))
					continue;
			}

			ColumnAttribute attr = new ColumnAttribute(val.getDBType());

			attr.setPrimaryKey(val.isPrimaryKey());
			attr.setHasIndex(val.shouldHaveIndex());

			if (DBString.class.isInstance(val)) {
				attr.setWidth(((DBString) val).getMaxLen());
			} else if (val instanceof DBEnum) {
				attr.setWidth(((DBEnum) val).getMaxLen());
			}

			colls.put(prefix + val.getName(), attr);
		}

		for (int i = 0; i < sub_strukts.size(); i++) {
			DBStrukt strukt = sub_strukts.get(i);

			HashMap<String, ColumnAttribute> sub_colls = strukt.getHashMap(
					prefix + strukt.getName() + "_", Version);

			Set<String> keys = sub_colls.keySet();

			for (String s : keys) {
				colls.put(s, sub_colls.get(s));
			}
		}

		return colls;
	}

	public HashMap<String, Object> getHashMapAndData() {
		return getHashMapAndData("");
	}

	protected HashMap<String, Object> getHashMapAndData(String prefix) {
		HashMap<String, Object> colls = new HashMap<String, Object>();

		for (int i = 0; i < elements.size(); i++) {
			DBValue val = elements.get(i);
			colls.put(prefix + val.getName(), val.getValue());
		}

		for (int i = 0; i < sub_strukts.size(); i++) {
			DBStrukt strukt = sub_strukts.get(i);

			HashMap<String, Object> sub_colls = strukt.getHashMapAndData(prefix
					+ strukt.getName() + "_");

			Set<String> keys = sub_colls.keySet();

			for (String s : keys) {
				colls.put(s, sub_colls.get(s));
			}
		}

		return colls;
	}

	public Vector<DBValue> getAllValues() {
		Vector<DBValue> values = new Vector<DBValue>();

		for (int i = 0; i < elements.size(); i++) {
			DBValue val = elements.get(i);

			values.add(val);
		}

		for (int i = 0; i < sub_strukts.size(); i++) {
			DBStrukt strukt = sub_strukts.get(i);

			values.addAll(strukt.getAllValues());
		}

		return values;
	}

	public Vector<String> getAllNames() {
		return getAllNames("");
	}

	protected Vector<String> getAllNames(String prefix) {
		Vector<String> values = new Vector<String>();

		for (int i = 0; i < elements.size(); i++) {
			DBValue val = elements.get(i);

			if (val.getTitle().isEmpty())
				values.add(prefix + val.getName());
			else
				values.add(prefix + val.getTitle());
		}

		for (int i = 0; i < sub_strukts.size(); i++) {
			DBStrukt strukt = sub_strukts.get(i);

			if (strukt.getTitle().isEmpty())
				values.addAll(strukt.getAllNames(strukt.getName() + " "));
			else
				values.addAll(strukt.getAllNames(strukt.getTitle() + " "));
		}

		return values;
	}

	public abstract DBStrukt getNewOne();

	public String getTitle() {
		return title;
	}

	private DBValue getValueByName(String key) {
		return element_by_name.get(key.toLowerCase());

		/*
		 * 
		 * for( int i = 0; i < elements.size(); i++ ) { if(
		 * key.equalsIgnoreCase(elements.get(i).getName()) ) return
		 * elements.get(i); }
		 * 
		 * return null;
		 */
	}

	public void loadFromCopy(DBStrukt s) {
		for (int i = 0; i < s.elements.size(); i++) {
			DBValue val = s.elements.get(i);

			elements.get(i).loadFromCopy(val.getValue());
		}

		for (int i = 0; i < s.sub_strukts.size(); i++) {
			sub_strukts.get(i).loadFromCopy(s.sub_strukts.get(i));
		}
	}

	public DBStrukt getCopy() {
		DBStrukt s = getNewOne();

		s.loadFromCopy(this);

		return s;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getVersion() {
		if (version == null)
			return 1;

		return version;
	}

}
