package com.e.myshoppy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.ContentHandler;
import java.util.HashMap;
import java.util.Map;

public class Account extends Fragment {

    Context context;

    public Account(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_account, container, false);


        final EditText nameEdit = v.findViewById(R.id.name);
        final EditText emailEdit = v.findViewById(R.id.email);
        final EditText noEdit = v.findViewById(R.id.number);
        final EditText flatEdit = v.findViewById(R.id.flat_no);
        final EditText cityEdit = v.findViewById(R.id.city);
        final EditText landEdit = v.findViewById(R.id.landmark);
        final EditText pinEdit = v.findViewById(R.id.pincode);
        final Spinner spin = v.findViewById(R.id.state);
        final Button editSave = v.findViewById(R.id.edit_save);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+
                FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameEdit.setText(snapshot.child("info").child("name").getValue().toString());
                emailEdit.setText(snapshot.child("info").child("email").getValue().toString());
                noEdit.setText(snapshot.child("info").child("contact").getValue().toString());

                if(snapshot.child("info").getChildrenCount()>3){
                    flatEdit.setText(snapshot.child("info").child("house_no").getValue().toString());
                    cityEdit.setText(snapshot.child("info").child("city").getValue().toString());
                    landEdit.setText(snapshot.child("info").child("landmark").getValue().toString());
                    pinEdit.setText(snapshot.child("info").child("pincode").getValue().toString());
                    spin.setSelection(Integer.valueOf(snapshot.child("info").child("state_pos").getValue().toString()));
                    spin.setEnabled(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editSave.getText().toString();

                if(text.equals("Edit")){
                    editSave.setText("save");
                    nameEdit.setEnabled(true);
                    flatEdit.setEnabled(true);
                    cityEdit.setEnabled(true);
                    landEdit.setEnabled(true);
                    pinEdit.setEnabled(true);
                    spin.setEnabled(true);

                }else{

                    String name = nameEdit.getText().toString();
                    String flat = flatEdit.getText().toString();
                    String city = cityEdit.getText().toString();
                    String land = landEdit.getText().toString();
                    String pin = pinEdit.getText().toString();

                    if(name.isEmpty())
                        nameEdit.setError("Field is Empty");
                    else if(flat.isEmpty())
                        flatEdit.setError("Field is Empty");
                    else if(city.isEmpty())
                        cityEdit.setError("Field is Empty");
                    else if(land.isEmpty())
                        landEdit.setError("Field is Empty");
                    else if(pin.length()!=6)
                        pinEdit.setError("Incorrect pincode");
                    else{

                        Map<String, Object> info = new HashMap<>();
                        info.put("name",name);
                        info.put("email",emailEdit.getText().toString());
                        info.put("house_no",flat);
                        info.put("city",city);
                        info.put("landmark",land);
                        info.put("pincode",pin);
                        info.put("state",spin.getSelectedItem().toString());
                        info.put("state_pos",spin.getSelectedItemPosition());
                        Map<String,Object> infod = new HashMap<>();
                        infod.put("info",info);

                        ref.updateChildren(infod);

                        nameEdit.setEnabled(false);
                        noEdit.setEnabled(false);
                        flatEdit.setEnabled(false);
                        cityEdit.setEnabled(false);
                        landEdit.setEnabled(false);
                        pinEdit.setEnabled(false);
                        spin.setEnabled(false);
                        editSave.setText("Edit");
                    }

                }
            }
        });

        return v;
    }
}