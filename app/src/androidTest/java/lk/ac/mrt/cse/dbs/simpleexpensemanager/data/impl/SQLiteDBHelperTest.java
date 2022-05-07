package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.test.suitebuilder.annotation.MediumTest;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

@RunWith(AndroidJUnit4.class)
@MediumTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SQLiteDBHelperTest {
    private SQLiteDBHelper sqLiteDBHelper;

    @Before
    public void setup() {
        sqLiteDBHelper = new SQLiteDBHelper(ApplicationProvider.getApplicationContext());
    }

    @After
    public void teardown() {
        sqLiteDBHelper.close();
    }

    @Test
    public void A_insertAccount() {
        boolean result = sqLiteDBHelper.addAccount("100100", "Amana", "Nimsara", 100);
        assertThat(result).isTrue();
    }

    @Test
    public void B_getAccount() {
        Map<String, Account> accounts = sqLiteDBHelper.getAllAccounts();
        Account account = accounts.get("100100");
        boolean result = false;
        if (account != null) {
            result = account.getAccountHolderName().equals("Nimsara") && account.getBankName().equals("Amana") && account.getBalance() == 100;
        }
        assertThat(result).isTrue();
    }

    @Test
    public void C_updateBalance() throws InvalidAccountException {
        boolean result = sqLiteDBHelper.updateBalance("100100", -20);
        assertThat(result).isTrue();
    }

    @Test
    public void D_removeAccount() throws InvalidAccountException {
        boolean result = sqLiteDBHelper.removeAccount("100100");
        assertThat(result).isTrue();
    }

    @Test
    public void E_logTransaction() {
        boolean result = sqLiteDBHelper.logTransaction(new Date(2022, 5, 7), "100100", ExpenseType.INCOME, 20);
        assertThat(result).isTrue();
    }

    @Test
    public void F_getAllTransactions() {
        List<Transaction> transactions = sqLiteDBHelper.getAllTransactions();
        Transaction transaction = transactions.get(0);
        boolean result = transaction.getAccountNo().equals("100100") && transaction.getAmount() == 20 && transaction.getExpenseType() == ExpenseType.INCOME && transaction.getDate().equals(new Date(2022, 5, 7));
        assertThat(result).isTrue();
    }
}

