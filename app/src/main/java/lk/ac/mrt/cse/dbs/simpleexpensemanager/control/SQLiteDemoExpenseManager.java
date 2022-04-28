package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class SQLiteDemoExpenseManager extends ExpenseManager{
    private TransactionDAO sqliteTransactionDAO;
    private AccountDAO sqliteAccountDAO;

    public SQLiteDemoExpenseManager(TransactionDAO sqliteTransactionDAO, AccountDAO sqliteAccountDAO) {
        this.sqliteTransactionDAO = sqliteTransactionDAO;
        this.sqliteAccountDAO = sqliteAccountDAO;
        setup();
    }

    @Override
    public void setup() {
        /*** Begin generating dummy data for In-Memory implementation ***/
        setTransactionsDAO(sqliteTransactionDAO);
        setAccountsDAO(sqliteAccountDAO);
        /*** End ***/
    }
}

