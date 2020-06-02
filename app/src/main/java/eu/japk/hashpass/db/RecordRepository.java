package eu.japk.hashpass.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecordRepository {

    private PasswordRecordDAO recordDao;
    private LiveData<List<PasswordRecord>> allRecords;
    AppDatabase db;

    public RecordRepository(Application application) {
        db = AppDatabase.getDatabase(application);
        recordDao = db.recordDao();
        allRecords = recordDao.getAll();
    }

    LiveData<List<PasswordRecord>> getAllRecords() {
        return allRecords;
    }


    void insert(PasswordRecord record){
        new insertAsyncTask(recordDao).execute(record);
    }

    void insertList(List<PasswordRecord> record){
        new insertListAsyncTask(recordDao).execute(record);
    }



    public void deleteItem(PasswordRecord record) {
        new deleteAsyncTask(recordDao).execute(record);
    }

    void updatePassword(PasswordRecord record) {new updateAsyncTask(recordDao).execute(record);}

    private static class insertAsyncTask extends AsyncTask<PasswordRecord, Void, Void> {

        private PasswordRecordDAO mAsyncTaskDao;

        insertAsyncTask(PasswordRecordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PasswordRecord... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    private static class insertListAsyncTask extends AsyncTask<List<PasswordRecord>, Void, Void> {

        private PasswordRecordDAO mAsyncTaskDao;

        insertListAsyncTask(PasswordRecordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<PasswordRecord>... params) {
            mAsyncTaskDao.insertAllList(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<PasswordRecord, Void, Void> {

        private PasswordRecordDAO mAsyncTaskDao;

        deleteAsyncTask(PasswordRecordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PasswordRecord... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<PasswordRecord, Void, Void> {

        private PasswordRecordDAO mAsyncTaskDao;

        updateAsyncTask(PasswordRecordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PasswordRecord... params) {
            mAsyncTaskDao.updatePassword(params[0]);
            return null;
        }
    }
}
