package eu.japk.hashpass;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import eu.japk.hashpass.db.PasswordRecord;
import eu.japk.hashpass.db.RecordViewModel;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {
    private final LayoutInflater mInflater;
    private List<PasswordRecord> mRecords; // Cached copy of words
    private Context context;

    RecordListAdapter(Context context) { mInflater = LayoutInflater.from(context); this.context = context; }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.record_item, parent, false);
        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, final int position) {
        if (mRecords != null) {
            final PasswordRecord current = mRecords.get(position);
            holder.tv.setText(current.name);

            holder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    decryptAndStart(current);

                }
            });

            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decryptAndStart(current);
                }
            });

        }
    }

    private void decryptAndStart(PasswordRecord current){
        SecretKeyFunctions skf = new SecretKeyFunctions();
        try {
            if(skf.secretKeyExists()){
                CryptoFunctions cf = new CryptoFunctions();
                try {
                    SecretKey sk = skf.getKey();
                    byte[] decryptUser = cf.decrypt(sk, current.user, current.userIV);
                    byte[] decryptNotes = cf.decrypt(sk, current.notes, current.notesIV);
                    byte[] decryptSalt = cf.decrypt(sk, current.salt, current.saltIV);

                    Log.i("SAlt", new String(decryptSalt));


                    Intent i = new Intent(context, ViewAndEditPassword.class);
                    i.putExtra("id", current.uid);
                    i.putExtra("name", current.name);
                    i.putExtra("length", current.length);
                    i.putExtra("charsAllowed", current.charsAllowed);
                    i.putExtra("salt", new String(decryptSalt));
                    i.putExtra("notes", new String(decryptNotes));
                    i.putExtra("user", new String(decryptUser));
                    context.startActivity(i);
                    Log.i("ID",""+current.uid);
                } catch (UnrecoverableEntryException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setRecords(List<PasswordRecord> records){
        mRecords = records;
        notifyDataSetChanged();
    }

    void refreshRecords(List<PasswordRecord> records){
        mRecords = records;
        notifyDataSetChanged();
    }

    int getPosition(List<PasswordRecord> records, int id){
        return getIndexOf(records, id);
    }

    public static int getIndexOf(List<PasswordRecord> list, int id) {
        int pos = 0;

        for(PasswordRecord myObj : list) {
            if(id == myObj.uid)
                return pos;
            pos++;
        }

        return -1;
    }

    void removed(int position){
        mRecords.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }
    public List<PasswordRecord> getList(){
        return this.mRecords;
    }

    @Override
    public int getItemCount() {
        if (mRecords != null)
            return mRecords.size();
        else return 0;
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv;
        private final FloatingActionButton fab;
        private final LinearLayout ll;


        private RecordViewHolder(View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.listTV);
            fab = itemView.findViewById(R.id.listFAB);
            ll = itemView.findViewById(R.id.pwLay);
        }
    }
}
