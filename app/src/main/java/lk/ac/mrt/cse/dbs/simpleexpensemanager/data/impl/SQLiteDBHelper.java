package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    // Accounts table creation
    private static final String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE Accounts(" +
            "accountNo TEXT PRIMARY KEY, " +
            "bankName TEXT NOT NULL, " +
            "accountHolderName TEXT NOT NULL, " +
            "balance REAL NOT NULL " +
            ")";

    // Transactions table creation
    private static final String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE Transactions(" +
            "date TEXT NOT NULL, " +
            "accountNo TEXT NOT NULL, " +
            "expenseType TEXT NOT NULL, " +
            "amount REAL NOT NULL, " +
            "FOREIGN KEY(accountNo) " +
            "REFERENCES Accounts(accountNo)" +
            ")";

    public SQLiteDBHelper(Context context) {
        super(context, "190175X.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNTS_TABLE);
        db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Accounts");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
    }

    public Map<String, Account> getAllAccounts() {
        Map<String, Account> accounts = new TreeMap<>();

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
        return accounts;
    }

    public boolean addAccount(String accountNo, String bankName, String accountHolderName, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("bankName", bankName);
        contentValues.put("accountHolderName", accountHolderName);
        contentValues.put("balance", balance);
        long result = db.insert("Accounts", null, contentValues);

        db.close();

        return result != -1;
    }

    public boolean removeAccount(String accountNo) throws InvalidAccountException {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts WHERE accountNo=?", new String[]{accountNo});
        if (cursor.getCount() > 0) {
            result = db.delete("Accounts", "accountNo=?", new String[]{accountNo});
        }
        cursor.close();
        db.close();

        if (result == -1) {
            throw new InvalidAccountException("Account not found!");
        }
        return true;
    }

    public boolean updateBalance(String accountNo, double amount) throws InvalidAccountException {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts WHERE accountNo=?", new String[] {accountNo});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            double newAmount = cursor.getDouble(3)+amount;
            ContentValues contentValues = new ContentValues();
            contentValues.put("accountNo", cursor.getString(0));
            contentValues.put("bankName", cursor.getString(1));
            contentValues.put("accountHolderName", cursor.getString(2));
            contentValues.put("balance", newAmount);
            result = db.update("Accounts", contentValues, "accountNo=?", new String[] {accountNo});
        }

        cursor.close();
        db.close();

        if (result == -1) {
            throw new InvalidAccountException("Account not found!");
        }
        return true;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new LinkedList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Transactions", null);
        int rows = cursor.getCount();
        cursor.moveToFirst();
        for (int i=0; i<rows; i++) {
            String dateString = cursor.getString(0);
            String accountNo = cursor.getString(1);
            String expenseTypeString = cursor.getString(2);
            double amount = cursor.getDouble(3);
            cursor.moveToNext();
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                ExpenseType expenseType = ExpenseType.valueOf(expenseTypeString);
                transactions.add(new Transaction(date, accountNo, expenseType, amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();

        return transactions;
    }

    public boolean logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", dateFormat.format(date));
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", expenseType.name());
        contentValues.put("amount", amount);
        long result = db.insert("Transactions", null, contentValues);

        db.close();

        return result != -1;
    }
}
