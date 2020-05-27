package eu.japk.hashpass.db;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RecordViewModel extends AndroidViewModel {
    private RecordRepository mRepository;

    private LiveData<List<PasswordRecord>> mAllRecords;


    public RecordViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RecordRepository(application);
    }

    public LiveData<List<PasswordRecord>> getAllRecords() {
        mAllRecords = mRepository.getAllRecords();
        return mAllRecords;
    }

    public void insert(PasswordRecord record) { mRepository.insert(record);
    }
    public void insertList(List<PasswordRecord> record) { mRepository.insertList(record);
    }

    public void deleteItem(PasswordRecord record) { mRepository.deleteItem(record);}

    public void updateItem(PasswordRecord record) { mRepository.updatePassword(record);}


}
