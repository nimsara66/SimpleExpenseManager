package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class SQLiteAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private final Map<String, Account> accounts;

    public SQLiteAccountDAO(Context context) {
        super(context, "AccountData.db", null, 1);
        this.accounts = new TreeMap<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts", null);
        int rows = cursor.getCount();
        cursor.moveToFirst();
        for (int i=0; i<rows; i++) {
            String accountNo = cursor.getString(0);
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);
            accounts.put(accountNo, new Account(accountNo, bankName, accountHolderName, balance));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Accounts(accountNo TEXT PRIMARY KEY, bankName TEXT, accountHolderName TEXT, balance REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Accounts");
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        accounts.put(account.getAccountNo(), account);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        db.insert("Accounts", null, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts WHERE accountNo=?", new String[] {accountNo});
        if (cursor.getCount()>0) {
            db.delete("Accounts", "accountNo=?", new String[] {accountNo});
        }
        cursor.close();
        db.close();
        throw new InvalidAccountException("Account not found!");
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts WHERE accountNo=?", new String[] {accountNo});

        if (expenseType==ExpenseType.EXPENSE) {
            amount=-amount;
        }

        if (accounts.containsKey(accountNo) && cursor.getCount()>0) {
            Account account = accounts.get(accountNo);
            account.setBalance(account.getBalance() + amount);
            accounts.put(accountNo, account);

            cursor.moveToFirst();
            double newAmount = cursor.getDouble(3)+amount;
            ContentValues contentValues = new ContentValues();
            contentValues.put("accountNo", cursor.getString(0));
            contentValues.put("bankName", cursor.getString(1));
            contentValues.put("accountHolderName", cursor.getString(2));
            contentValues.put("balance", newAmount);

            db.update("Accounts", contentValues, "accountNo=?", new String[] {accountNo});
        } else if (accounts.containsKey(accountNo)) {
            accounts.remove(accountNo);
        }

        cursor.close();
        db.close();

        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}
