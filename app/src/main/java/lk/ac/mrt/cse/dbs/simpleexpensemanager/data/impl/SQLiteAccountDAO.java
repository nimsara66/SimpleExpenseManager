package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class SQLiteAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    public SQLiteAccountDAO(Context context) {

        super(context, "AccountData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Account(accountNo TEXT PRIMARY KEY, bankName TEXT, accountHolderName TEXT, balance REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Account");
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accounts = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account ORDER BY accountNo", null);
        int rows = cursor.getCount();
        cursor.moveToFirst();
        for (int i=0; i<rows; i++) {
            String accountNo = cursor.getString(0);
            accounts.add(accountNo);
            cursor.moveToNext();
        }
        cursor.close();
        return accounts;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountNos = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account ORDER BY accountNo", null);
        int rows = cursor.getCount();
        cursor.moveToFirst();
        for (int i=0; i<rows; i++) {
            String accountNo = cursor.getString(0);
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);
            accountNos.add(new Account(accountNo, bankName, accountHolderName, balance));
            cursor.moveToNext();
        }
        cursor.close();
        return accountNos;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE accountNo=?", new String[] {accountNo});
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);
            cursor.close();
            return new Account(accountNo, bankName, accountHolderName, balance);
        }
        cursor.close();
        throw new InvalidAccountException("Account not found!");
    }

    @Override
    public void addAccount(Account account) {
//        TODO: need to check if account no exist
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        db.insert("Account", null, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE accountNo=?", new String[] {accountNo});
        if (cursor.getCount()>0) {
            db.delete("Account", "accountNo=?", new String[] {accountNo});
        }
        cursor.close();
        throw new InvalidAccountException("Account not found!");
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (expenseType==ExpenseType.EXPENSE) {
            amount=-amount;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE accountNo=?", new String[] {accountNo});
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            double newAmount = cursor.getDouble(3)+amount;
            ContentValues contentValues = new ContentValues();
            contentValues.put("accountNo", cursor.getString(0));
            contentValues.put("bankName", cursor.getString(1));
            contentValues.put("accountHolderName", cursor.getString(2));
            contentValues.put("balance", newAmount);

            db.update("Account", contentValues, "accountNo=?", new String[] {accountNo});
        }
        cursor.close();
        throw new InvalidAccountException("Account not found!");
    }
}
