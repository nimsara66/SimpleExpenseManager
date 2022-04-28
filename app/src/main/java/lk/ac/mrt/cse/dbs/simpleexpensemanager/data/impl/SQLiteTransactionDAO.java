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

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private final List<Transaction> transactions;

    public SQLiteTransactionDAO(Context context) {
        super(context, "TransactionData.db", null, 1);
        transactions = new LinkedList<>();

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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Transactions(date TEXT, accountNo TEXT, expenseType TEXT, amount REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Transactions");
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", dateFormat.format(date));
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", expenseType.name());
        contentValues.put("amount", amount);
        db.insert("Transactions", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
