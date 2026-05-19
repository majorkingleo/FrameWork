---
name: sqldbinterface
description: >
  Domain knowledge for at.redeye.SqlDBInterface — the SQL abstraction layer in the FrameWork project.
  Use when adding new DBMS support, writing new queries, registering table bindings, debugging
  database connectivity, or understanding how SELECT/INSERT/UPDATE flow through the layer.
---

# SqlDBInterface — How It Works

## Purpose

`at.redeye.SqlDBInterface` is a thin, framework-internal SQL abstraction layer. It:

- Hides DBMS-specific JDBC URL construction and driver registration behind a single connect/disconnect interface.
- Provides dialect-aware SQL statement building (identifier quoting differs per DBMS).
- Executes SELECT/INSERT/UPDATE using `PreparedStatement` for type safety.
- Maps database columns to strongly-typed Java values via a **table binding** registry.

It is **not** an ORM. Callers still write or supply WHERE clauses; the layer only handles
column-type marshalling and identifier quoting.

---

## Package Map

```
SqlDBInterface/
├── SqlDBConnection/          # JDBC connection lifecycle
│   ├── DbConnectionInterface.java        # connect / disconnect
│   └── impl/
│       ├── ConnectionDefinition.java     # host, port, user, pwd, instance, DBMS type
│       ├── SupportedDBMSTypes.java       # enum: DB_MYSQL, DB_MARIADB, DB_MSSQL, DB_ORACLE,
│       │                                 #        DB_SQLITE, DB_JAVADB
│       ├── AbstractDBConnector.java      # builds JDBC URL, registers driver, calls DriverManager
│       ├── DBConnector.java              # concrete (empty) subclass — instantiate this
│       ├── MissingConnectionParamException.java
│       └── UnSupportedDatabaseException.java
│
├── SqlDBIO/                  # Statement building & execution
│   ├── StmtCreatorInterface.java         # builds SQL strings (SELECT / INSERT / UPDATE)
│   ├── StmtExecInterface.java            # executes SQL, returns typed Java objects
│   ├── TypeRegistrationInterface.java    # table-binding registry
│   └── impl/
│       ├── DBDataType.java               # enum of supported column types
│       ├── ColumnAttribute.java          # datatype + isPrimaryKey + width
│       ├── TypeRegistration.java         # implements TypeRegistrationInterface
│       ├── creator/
│       │   ├── AbstractStmtCreator.java  # shared SELECT / INSERT / UPDATE builder logic
│       │   ├── StmtCreatorFactory.java   # picks dialect subclass by SupportedDBMSTypes
│       │   ├── DefaultStmtCreator.java   # no-op quoting (fallback)
│       │   ├── StmtCreatorMYSQL.java     # backtick quoting; tables UPPER, columns lower
│       │   ├── StmtCreatorMSSQL.java     # [bracket] quoting
│       │   ├── StmtCreatorSQLITE.java    # backtick quoting; UPDATE uses column-only form
│       │   ├── StmtCreatorDerby.java     # Derby/JavaDB quoting
│       │   └── StmtCreatorOracle.java    # Oracle quoting
│       └── executor/
│           ├── AbstractStmtExecuter.java # PreparedStatement execution + type unmarshalling
│           └── DefaultStmtExecuter.java  # concrete (empty) subclass — instantiate this
│
└── jdbc_driver/              # Vendored README/notes per driver (not framework source)
    ├── javadb/ mariadb/ mssql/ mysql/ oracle/ sqllite/
```

---

## Layer 1 — Connection (`SqlDBConnection`)

### `ConnectionDefinition`

Plain value object. Required fields vary by DBMS:

| DBMS | hostname | port (default) | username | password | instance |
|------|----------|----------------|----------|----------|----------|
| MySQL / MariaDB | ✓ | 3306 | ✓ required | optional | ✓ (DB name) |
| MSSQL | ✓ | 1433 | ✓ required | ✓ required | ✓ (DB name) |
| Oracle | ✓ | 1521 | ✓ required | ✓ required | ✓ (SID/service) |
| SQLite | — | — | — | — | ✓ (file path) |
| JavaDB/Derby | ✓ | 1527 | ✓ | ✓ | ✓ |

### `AbstractDBConnector.connectToDatabase()`

Switch on `SupportedDBMSTypes`:
1. Validates required fields, throws `MissingConnectionParamException` if absent.
2. Registers the appropriate JDBC driver via `DriverManager.registerDriver(new <Driver>())`.
3. Assembles the JDBC URL string.
4. Returns `DriverManager.getConnection(url, user, pwd)`.

`disconnectDatabase(conn)` simply calls `conn.close()`.

### Typical usage

```java
ConnectionDefinition def = new ConnectionDefinition(
    "localhost", 3306, "user", "secret", "mydb", SupportedDBMSTypes.DB_MARIADB);
DbConnectionInterface connector = new DBConnector(def);
Connection conn = connector.connectToDatabase();
// ... use conn ...
connector.disconnectDatabase(conn);
```

---

## Layer 2 — Type Registry (`TypeRegistration`)

Before any table-aware query can run, columns must be registered so the executor knows
their Java type and whether they are primary keys.

### Option A — Bind file (CSV)

```
TABLENAME          ← line 1: table name (uppercased automatically)
colname,type,pk    ← subsequent lines
id,int,true
name,string,false
valid,bool,false
```

```java
TypeRegistrationInterface reg = new TypeRegistration(SupportedDBMSTypes.DB_MYSQL);
reg.registerTableBindings("myapp/tables/mytable.bind");
```

### Option B — Programmatic

```java
HashMap<String, ColumnAttribute> cols = new HashMap<>();
cols.put("MYTABLE.id",   new ColumnAttribute(true,  DBDataType.DB_TYPE_INTEGER));
cols.put("MYTABLE.name", new ColumnAttribute(false, DBDataType.DB_TYPE_STRING));
HashMap<String, HashMap<String, ColumnAttribute>> tables = new HashMap<>();
tables.put("MYTABLE", cols);
reg.registerTableBindings(tables);
```

### `DBDataType` enum

`DB_TYPE_STRING`, `DB_TYPE_INTEGER`, `DB_TYPE_LONG`, `DB_TYPE_SHORT`,
`DB_TYPE_FLOAT`, `DB_TYPE_DOUBLE`, `DB_TYPE_BOOLEAN`, `DB_TYPE_BIT`,
`DB_TYPE_DATE`, `DB_TYPE_TIME`, `DB_TYPE_DATETIME`, `DB_TYPE_BLOB`

`TypeRegistration.setTypeMatchTable()` maps string tokens from bind files
(e.g., `"varchar"` → `DB_TYPE_STRING`, `"datetime"` → `DB_TYPE_DATETIME`) for each DBMS.

---

## Layer 3 — Statement Creator (`StmtCreator*`)

`StmtCreatorFactory.getStmtCreator(dbmstype)` returns the right dialect subclass.
Each subclass only overrides two methods:

- `markTableName(String)` — wraps a table name in dialect-appropriate quotes.
- `markColumnName(String)` — wraps a column name in dialect-appropriate quotes.

SQLite also overrides `markTableAndColumnNameForUpdate` to omit the table prefix in SET clauses.

`AbstractStmtCreator` contains the shared SQL-building logic:

| Method | Purpose |
|--------|---------|
| `buildStmtForTable(String[], whereStmt, columnMap)` | Multi-table SELECT with explicit column map |
| `buildStmtForTable(String, pkValues)` | Single-table SELECT by primary key (uses `?` placeholders) |
| `buildInsertStmtForTable(String, values)` | INSERT with `?` placeholders for all supplied values |
| `buildUpdateStmtForTable(String, values, whereStmt)` | UPDATE; uses PK columns if whereStmt is null |
| `getCols2Handle()` | Returns ordered list of columns bound to `?` (for the executor) |

---

## Layer 4 — Statement Executor (`AbstractStmtExecuter`)

Constructed with an open `Connection` and the `SupportedDBMSTypes`. It internally creates
a `TypeRegistration` and a `StmtCreatorFactory` — callers do **not** pass those separately
when going through the executor's high-level API.

### High-level API (table-binding–aware)

| Method | Returns | Notes |
|--------|---------|-------|
| `fetchTableValue(String[] tables, String where)` | `List<HashMap<String,Object>>` | All registered columns; requires prior `registerTableBindings` on the `TypeRegistration` inside the executer — **not** directly accessible; use the overload below instead |
| `fetchTableValue(String table, HashMap pkValues)` | `HashMap<String,Object>` | Single row by PK |
| `insertTableValues(String table, HashMap values)` | `int` rows affected | Uses PreparedStatement |
| `updateTableValues(String table, HashMap values, String where)` | `int` rows affected | where=null → PK-based |

### Low-level API (caller supplies full SQL)

| Method | Returns |
|--------|---------|
| `fetchColumnValue(String stmt, List<DBDataType> types)` | `List<List<?>>` ordered by column position |
| `insertValues(String stmt)` | `int` |
| `updateValues(String stmt)` | `int` |

### Type unmarshalling (`processTypeValue`)

`ResultSet` values are cast to the registered `DBDataType`:
- DATE / TIME / DATETIME → `java.util.Date` (via `Timestamp`)
- BLOB → `byte[]`
- STRING → trimmed, never null (empty string if DB null)

### PreparedStatement binding (`setPreparedStatementTypes`)

Binds `?` parameters by Java type inspection (`instanceof` chain):
`String`, `Date`, `Float`, `Double`, `Integer`, `Long`, `Short`, `Boolean`, `Byte`, `byte[]`.
Unknown types throw `SQLException`.

---

## End-to-End Flow

```
1. Build ConnectionDefinition (host/port/user/pwd/db, DBMS type)
2. new DBConnector(def).connectToDatabase()  →  java.sql.Connection
3. new DefaultStmtExecuter(conn, dbmstype)   →  StmtExecInterface

   Inside DefaultStmtExecuter constructor:
     new TypeRegistration(dbmstype)           →  TypeRegistrationInterface
     new StmtCreatorFactory(reg).getStmtCreator(dbmstype) → dialect StmtCreator

4. Register table bindings (if using table-aware fetch/update):
     reg.registerTableBindings("path/to/table.bind")
     -- OR --
     executer.getStmtCreator().registration.registerTableBindings(map)

5. Query:
     executer.fetchTableValue(new String[]{"ORDERS"}, "where status='NEW'")
       → stmtCreator.buildStmtForTable(...)   builds SELECT
       → conn.prepareStatement(sql)
       → rs.next() → processTypeValue()       unmarshals each column
       → List<HashMap<String,Object>>

6. connector.disconnectDatabase(conn)
```

---

## Adding a New DBMS

1. Add a constant to `SupportedDBMSTypes`.
2. Add a `case` in `AbstractDBConnector.connectToDatabase()` for URL construction and driver registration.
3. Create `StmtCreatorXxx extends AbstractStmtCreator` implementing `markTableName` / `markColumnName`.
4. Register it in `StmtCreatorFactory.getStmtCreator()`.
5. If the new DBMS uses non-standard type names, extend `TypeRegistration.setTypeMatchTable()`.

---

## Known Constraints / Gotchas

- `TypeRegistration.registeredTables_` is **static** — shared across all `TypeRegistration` instances in the same JVM. Re-instantiating `TypeRegistration` resets the registry.
- `AbstractStmtExecuter.lastStmt` is also **static** — the last-executed SQL string is shared state.
- `fetchTableValue(String[], where)` looks up table bindings internally via the executer's own `TypeRegistration`, which starts empty. The caller must populate it via `new TypeRegistration(dbmstype)` separately **and** pass the same data, or use the overload that accepts a pre-built `TypeRegistration`. (The test application demonstrates registering through the separate `reg` object and relying on the shared static map.)
- Date values serialised to `PreparedStatement` are converted to a formatted string (`toDateString`), not via `ps.setDate()`. DBMS-specific creators may override `toDateString` for correct dialect formatting.
