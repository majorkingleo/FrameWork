# Foreign Key Support — Implementation Plan

## Goal

Add first-class foreign key (FK) metadata to `at.redeye.SqlDBInterface` so that:

1. FK relationships can be declared alongside column bindings (data model level).
2. FK constraints can be emitted in DDL (`CREATE TABLE`) for any supported DBMS.
3. Automatic JOIN clauses can be generated from FK metadata (optional, Phase 4).
4. Application-level referential integrity can be validated before write operations (optional, Phase 5).

All changes must be **backward-compatible**: existing code that never registers FKs must continue to compile and run without modification.

---

## Current State (Relevant Touch-Points)

| Class / Interface | Relevant Facts |
|---|---|
| `ColumnAttribute` | Holds `datatype`, `primaryKey`, `hasIndex`, `width`. No FK metadata. |
| `TypeRegistrationInterface` | `registerTableBindings(file\|map)`, `getAllRegisteredTables()`, `getRegisteredTableByString()`. |
| `TypeRegistration` | Parses CSV bind files. Static `registeredTables_` map. |
| `StmtCreatorInterface` | Builds SELECT / INSERT / UPDATE strings. No DDL. |
| `AbstractStmtCreator` | Shared builder logic. `markTableName` / `markColumnName` are dialect hooks. |
| Bind file format | Line 1: table name. Subsequent lines: `colname,type,pk` |

---

## Phase 1 — Data Model: `ForeignKeyDefinition` (prerequisite for all other phases)

### New class: `at.redeye.SqlDBInterface.SqlDBIO.impl.ForeignKeyDefinition`

```
ForeignKeyDefinition
  - referencedTable  : String   (target table name, stored UPPERCASED)
  - referencedColumn : String   (target column name)
  - onDelete         : FKAction (enum: NO_ACTION, CASCADE, SET_NULL, RESTRICT)
  - onUpdate         : FKAction (enum: same values)
```

### New enum: `at.redeye.SqlDBInterface.SqlDBIO.impl.FKAction`

```
NO_ACTION, CASCADE, SET_NULL, RESTRICT
```

### Extend `ColumnAttribute`

Add optional field (null = not a FK):

```java
private ForeignKeyDefinition foreignKey = null;  // null means: not a FK

public boolean isForeignKey() { return foreignKey != null; }
public ForeignKeyDefinition getForeignKey() { return foreignKey; }
public void setForeignKey(ForeignKeyDefinition fk) { this.foreignKey = fk; }
```

Add constructor overloads for convenience:

```java
// PK + FK (composite column roles are valid in some schemas)
ColumnAttribute(boolean primaryKey, DBDataType dt, ForeignKeyDefinition fk)
// FK only
ColumnAttribute(DBDataType dt, ForeignKeyDefinition fk)
```

**Compatibility**: All existing constructors and callers are unchanged. The new field defaults to `null`.

---

## Phase 2 — Registration: Extend `TypeRegistrationInterface` and `TypeRegistration`

### New methods on `TypeRegistrationInterface`

```java
/**
 * Returns all FK definitions across all registered tables.
 * Key: "TABLE.column",  Value: ForeignKeyDefinition
 */
HashMap<String, ForeignKeyDefinition> getAllForeignKeys();

/**
 * Returns FK definitions for one table.
 * Key: "TABLE.column",  Value: ForeignKeyDefinition
 */
HashMap<String, ForeignKeyDefinition> getForeignKeysForTable(String tablename);

/**
 * Programmatically register a single FK.
 * @param ownerTable   the table that owns the FK column
 * @param ownerColumn  the FK column name
 * @param fk           the FK definition
 */
void registerForeignKey(String ownerTable, String ownerColumn, ForeignKeyDefinition fk);
```

### Implementation in `TypeRegistration`

Derive FK data lazily from `registeredTables_`:

```java
@Override
public HashMap<String, ForeignKeyDefinition> getAllForeignKeys() {
    HashMap<String, ForeignKeyDefinition> result = new HashMap<>();
    for (HashMap<String, ColumnAttribute> cols : registeredTables_.values()) {
        for (Map.Entry<String, ColumnAttribute> e : cols.entrySet()) {
            if (e.getValue().isForeignKey()) {
                result.put(e.getKey(), e.getValue().getForeignKey());
            }
        }
    }
    return result;
}
```

`registerForeignKey` looks up the column in `registeredTables_` and calls `setForeignKey()`. Throws `TableBindingNotRegisteredException` if the column is unknown.

### Extend bind file format

Current format:

```
TABLENAME
colname,type,pk
```

Extended format (fully backward-compatible — existing 3-token lines are unaffected):

```
TABLENAME
colname,type,pk
colname,type,false,ref_table,ref_column
colname,type,false,ref_table,ref_column,CASCADE,NO_ACTION
```

Token positions:

| Index | Content | Required |
|-------|---------|----------|
| 0 | column name | ✓ |
| 1 | type string | ✓ |
| 2 | primary key (`true`/`false`) | ✓ |
| 3 | referenced table | optional |
| 4 | referenced column | optional (required if 3 is present) |
| 5 | ON DELETE action | optional (default `NO_ACTION`) |
| 6 | ON UPDATE action | optional (default `NO_ACTION`) |

Parsing change in `TypeRegistration.registerTableBindings(String filename)`:

```java
if (tokens.length >= 5) {
    FKAction onDelete = tokens.length >= 6 ? FKAction.valueOf(tokens[5].trim().toUpperCase()) : FKAction.NO_ACTION;
    FKAction onUpdate = tokens.length >= 7 ? FKAction.valueOf(tokens[6].trim().toUpperCase()) : FKAction.NO_ACTION;
    ForeignKeyDefinition fk = new ForeignKeyDefinition(
        tokens[3].trim().toUpperCase(),
        tokens[4].trim(),
        onDelete,
        onUpdate
    );
    colattr.setForeignKey(fk);
}
```

---

## Phase 3 — DDL: `buildCreateTableStmt`

### Add to `StmtCreatorInterface`

```java
/**
 * Generates a CREATE TABLE statement for the given registered table.
 * Includes PK constraint and FK REFERENCES clauses.
 *
 * @param tablename  registered table name (will be uppercased)
 * @param columns    the column map from TypeRegistration
 * @return DDL string; caller is responsible for executing it
 */
String buildCreateTableStmt(String tablename,
                             HashMap<String, ColumnAttribute> columns);
```

### Implement in `AbstractStmtCreator`

Skeleton:

```java
public String buildCreateTableStmt(String tablename,
                                    HashMap<String, ColumnAttribute> columns) {
    StringBuilder sb = new StringBuilder();
    List<String> pkCols = new ArrayList<>();
    List<String> fkClauses = new ArrayList<>();

    sb.append("CREATE TABLE ").append(markTableName(tablename)).append(" (\n");

    for (Map.Entry<String, ColumnAttribute> e : columns.entrySet()) {
        // strip "TABLE." prefix stored in key
        String colKey = e.getKey();
        String colName = colKey.contains(".") ? colKey.split("\\.")[1] : colKey;
        ColumnAttribute attr = e.getValue();

        sb.append("  ").append(markColumnName(colName))
          .append(" ").append(toSqlType(attr))
          .append(",\n");

        if (attr.isPrimaryKey()) pkCols.add(colName);
        if (attr.isForeignKey()) {
            ForeignKeyDefinition fk = attr.getForeignKey();
            fkClauses.add(buildFKClause(colName, fk));
        }
    }

    if (!pkCols.isEmpty()) {
        sb.append("  PRIMARY KEY (");
        sb.append(pkCols.stream().map(this::markColumnName)
                        .collect(Collectors.joining(", ")));
        sb.append("),\n");
    }
    for (String fkClause : fkClauses) {
        sb.append("  ").append(fkClause).append(",\n");
    }

    // trim trailing comma
    int last = sb.lastIndexOf(",");
    if (last != -1) sb.deleteCharAt(last);

    sb.append("\n)");
    return sb.toString();
}
```

### New abstract method: `toSqlType(ColumnAttribute)`

Convert `DBDataType` → DBMS-specific DDL type token. Each dialect subclass must implement this because type names differ (e.g., `VARCHAR` vs `NVARCHAR`, `DATETIME` vs `TIMESTAMP`).

Default mapping suggestion (add to `AbstractStmtCreator` as non-abstract baseline):

| `DBDataType` | Default SQL |
|---|---|
| `DB_TYPE_STRING` | `VARCHAR(width or 255)` |
| `DB_TYPE_INTEGER` | `INTEGER` |
| `DB_TYPE_LONG` | `BIGINT` |
| `DB_TYPE_SHORT` | `SMALLINT` |
| `DB_TYPE_FLOAT` | `FLOAT` |
| `DB_TYPE_DOUBLE` | `DOUBLE` |
| `DB_TYPE_BOOLEAN` | `BOOLEAN` |
| `DB_TYPE_BIT` | `BIT` |
| `DB_TYPE_DATE` | `DATE` |
| `DB_TYPE_TIME` | `TIME` |
| `DB_TYPE_DATETIME` | `DATETIME` |
| `DB_TYPE_BLOB` | `BLOB` |

Dialect overrides needed:
- **MySQL/MariaDB**: `DB_TYPE_BOOLEAN → TINYINT(1)`, `DB_TYPE_DATETIME → DATETIME`
- **MSSQL**: `DB_TYPE_BOOLEAN → BIT`, `DB_TYPE_DATETIME → DATETIME2`, `DB_TYPE_STRING → NVARCHAR`
- **Oracle**: `DB_TYPE_STRING → VARCHAR2`, `DB_TYPE_BOOLEAN → NUMBER(1)`, `DB_TYPE_DATETIME → TIMESTAMP`
- **SQLite**: all types → `TEXT / INTEGER / REAL / BLOB` (SQLite's type affinity system)
- **Derby**: `DB_TYPE_STRING → VARCHAR`, `DB_TYPE_BOOLEAN → BOOLEAN`

### `buildFKClause(colName, fk)` helper

```java
protected String buildFKClause(String colName, ForeignKeyDefinition fk) {
    return "FOREIGN KEY (" + markColumnName(colName) + ")"
         + " REFERENCES " + markTableName(fk.getReferencedTable())
         + " (" + markColumnName(fk.getReferencedColumn()) + ")"
         + " ON DELETE " + fk.getOnDelete().name().replace('_', ' ')
         + " ON UPDATE " + fk.getOnUpdate().name().replace('_', ' ');
}
```

SQLite overrides this to emit `REFERENCES` inline (SQLite uses inline FK syntax by default
and requires `PRAGMA foreign_keys = ON` to enforce them).

---

## Phase 4 — Automatic JOIN Generation (optional, low priority)

### New method on `StmtCreatorInterface`

```java
/**
 * Builds a SELECT with LEFT JOINs automatically derived from FK metadata.
 * Each FK column in rootTable triggers one JOIN to its referenced table.
 *
 * @param rootTable   starting table
 * @param extraWhere  additional WHERE clause (null = none)
 * @param columns     explicit column selection (null = SELECT *)
 * @param fkMap       FK map from TypeRegistration.getForeignKeysForTable()
 */
String buildJoinStmtForTable(String rootTable, String extraWhere,
                              HashMap<String, ColumnAttribute> columns,
                              HashMap<String, ForeignKeyDefinition> fkMap);
```

### Implementation in `AbstractStmtCreator`

```
SELECT <columns>
FROM <rootTable>
LEFT JOIN <refTable> ON <rootTable>.<fkCol> = <refTable>.<refCol>
[LEFT JOIN ...]
[WHERE ...]
```

---

## Phase 5 — Application-Level FK Validation (optional)

### New method on `StmtExecInterface`

```java
/**
 * Checks that the referenced row exists before an INSERT/UPDATE.
 * @throws SQLException if the FK row does not exist
 */
void validateForeignKeys(String tablename,
                          HashMap<String, Object> values,
                          Connection conn)
    throws SQLException, TableBindingNotRegisteredException,
           UnsupportedDBDataTypeException;
```

### Implementation in `AbstractStmtExecuter`

For each FK column in the values map:
1. Look up the `ForeignKeyDefinition`.
2. Build `SELECT 1 FROM <refTable> WHERE <refCol> = ?`.
3. Execute; if no rows returned → throw `SQLException("FK violation: ...")`.

Call this inside `insertTableValues` / `updateTableValues` before the write statement.

---

## Implementation Order

```
Phase 1  (ForeignKeyDefinition + ColumnAttribute changes)   ← must be first
Phase 2  (TypeRegistration + bind file format)              ← depends on Phase 1
Phase 3  (DDL buildCreateTableStmt)                         ← depends on Phase 1 + 2
Phase 4  (auto JOIN)                                        ← depends on Phase 1 + 2
Phase 5  (FK validation)                                    ← depends on Phase 1 + 2
```

Phases 4 and 5 are independent of each other and can be done in any order after Phase 2.

---

## Files to Create / Modify

| File | Action |
|------|--------|
| `SqlDBIO/impl/ForeignKeyDefinition.java` | **CREATE** |
| `SqlDBIO/impl/FKAction.java` | **CREATE** |
| `SqlDBIO/impl/ColumnAttribute.java` | MODIFY — add `foreignKey` field + accessors + new constructors |
| `SqlDBIO/TypeRegistrationInterface.java` | MODIFY — add 3 new methods |
| `SqlDBIO/impl/TypeRegistration.java` | MODIFY — implement new methods, extend bind-file parser |
| `SqlDBIO/StmtCreatorInterface.java` | MODIFY — add `buildCreateTableStmt` (Phase 3), `buildJoinStmtForTable` (Phase 4) |
| `SqlDBIO/impl/creator/AbstractStmtCreator.java` | MODIFY — implement new builder methods, add `toSqlType`, `buildFKClause` |
| `SqlDBIO/impl/creator/StmtCreatorMYSQL.java` | MODIFY — override `toSqlType` |
| `SqlDBIO/impl/creator/StmtCreatorMSSQL.java` | MODIFY — override `toSqlType`, `buildFKClause` (MSSQL syntax) |
| `SqlDBIO/impl/creator/StmtCreatorOracle.java` | MODIFY — override `toSqlType` |
| `SqlDBIO/impl/creator/StmtCreatorSQLITE.java` | MODIFY — override `toSqlType`, `buildFKClause` (inline syntax) |
| `SqlDBIO/impl/creator/StmtCreatorDerby.java` | MODIFY — override `toSqlType` |
| `SqlDBIO/StmtExecInterface.java` | MODIFY — add `validateForeignKeys` (Phase 5) |
| `SqlDBIO/impl/executor/AbstractStmtExecuter.java` | MODIFY — implement `validateForeignKeys`, call from write methods (Phase 5) |

---

## Compatibility Notes

- No existing public method signatures are removed or changed.
- Bind files with the old 3-token format continue to parse correctly.
- `ColumnAttribute` constructors without `ForeignKeyDefinition` continue to work; `isForeignKey()` returns `false`.
- `TypeRegistration` static map: FK metadata lives inside `ColumnAttribute` entries already in the map — no second static map is needed.
