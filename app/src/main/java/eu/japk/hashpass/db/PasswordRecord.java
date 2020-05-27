package eu.japk.hashpass.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PasswordRecord {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "salt", typeAffinity = ColumnInfo.BLOB)
    public byte[] salt;

    @ColumnInfo(name = "username", typeAffinity = ColumnInfo.BLOB)
    public byte[] user;

    @ColumnInfo(name = "notes", typeAffinity = ColumnInfo.BLOB)
    public byte[] notes;

    @ColumnInfo(name = "saltIV", typeAffinity = ColumnInfo.BLOB)
    public byte[] saltIV;

    @ColumnInfo(name = "usernameIV", typeAffinity = ColumnInfo.BLOB)
    public byte[] userIV;

    @ColumnInfo(name = "notesIV", typeAffinity = ColumnInfo.BLOB)
    public byte[] notesIV;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "length")
    public int length;

    @ColumnInfo(name = "charsAllowed")
    public String charsAllowed;

    public PasswordRecord(byte[] salt, byte[] user, byte[] notes, String name, int length, String charsAllowed, byte[] saltIV, byte[] userIV, byte[] notesIV){
        this.salt = salt;
        this.user = user;
        this.notes = notes;
        this.name = name;
        this.length = length;
        this.charsAllowed = charsAllowed;
        this.saltIV = saltIV;
        this.userIV = userIV;
        this.notesIV = notesIV;
    }

    public PasswordRecord(int id, byte[] salt, byte[] user, byte[] notes, String name, int length, String charsAllowed, byte[] saltIV, byte[] userIV, byte[] notesIV){
        this.uid = id;
        this.salt = salt;
        this.user = user;
        this.notes = notes;
        this.name = name;
        this.length = length;
        this.charsAllowed = charsAllowed;
        this.saltIV = saltIV;
        this.userIV = userIV;
        this.notesIV = notesIV;
    }



}
