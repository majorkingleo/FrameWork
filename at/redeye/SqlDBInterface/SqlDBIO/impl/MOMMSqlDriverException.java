/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.SqlDBInterface.SqlDBIO.impl;

/**
 *
 * @author martin
 */
public class MOMMSqlDriverException extends Exception
{
    
	private static final long serialVersionUID = 1L;

	public MOMMSqlDriverException()
    {
        super();
    }

    public MOMMSqlDriverException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public MOMMSqlDriverException(String message)
    {
        super(message);
    }

    public MOMMSqlDriverException(Throwable cause)
    {
        super(cause);
    }
}
