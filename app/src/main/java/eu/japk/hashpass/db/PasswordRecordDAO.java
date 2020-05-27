package eu.japk.hashpass.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PasswordRecordDAO {


    @Query("SELECT * FROM passwordrecord ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<PasswordRecord>> getAll();

    @Query("SELECT * FROM passwordrecord")
    List<PasswordRecord> getAllUnordered();

    @Query("SELECT * FROM passwordrecord WHERE uid = :id")
    LiveData<PasswordRecord> getRecord(int id);

    @Insert
    void insertAll(PasswordRecord... records);

    @Insert
    void insertAllList(List<PasswordRecord> records);

    @Delete
    void delete(PasswordRecord record);

    @Update
    void updatePassword(PasswordRecord record);


}
