package eu.japk.hashpass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import javax.crypto.SecretKey;
import eu.japk.hashpass.db.PasswordRecord;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {
    private final LayoutInflater mInflater;
    private List<PasswordRecord> mRecords;
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


                    Intent i = new Intent(context, ViewAndEditPassword.class);
                    i.putExtra("id", current.uid);
                    i.putExtra("name", current.name);
                    i.putExtra("length", current.length);
                    i.putExtra("charsAllowed", current.charsAllowed);
                    i.putExtra("salt", new String(decryptSalt));
                    i.putExtra("notes", new String(decryptNotes));
                    i.putExtra("user", new String(decryptUser));
                    context.startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_LONG).show();
        }
    }

    void setRecords(List<PasswordRecord> records){
        mRecords = records;
        notifyDataSetChanged();
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
