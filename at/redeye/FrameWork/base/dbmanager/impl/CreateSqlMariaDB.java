package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.FrameWork.base.transaction.Transaction;

/**
 *
 * @author martin
 */
public class CreateSqlMariaDB extends CreateSqlMySql {
    
    public CreateSqlMariaDB( Transaction trans )
    {
        super(trans);
    }
    
    @Override
    protected String addStorageInfo()
    {
        return " ENGINE='InnoDB' DEFAULT CHARSET='utf32' COLLATE='utf32_bin'";
    }

}
