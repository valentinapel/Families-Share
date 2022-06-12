package com.example.familiesshare.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.familiesshare.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Map;

public class DrawerMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawermenu);

        Toolbar toolbar=  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //mi fa crashare l'app,dovrebbe rimpiazzare actionbar
        //implemento la possibilità di selezionare un item del menu e aprire l'activity corrispondente

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //TEST lista gruppi
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            mDatabase.child("Groups").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map in datasnapshot
                            ShowUserGroups((Map<String,Object>) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });

        }
    }

    private void ShowUserGroups(Map<String,Object> mappaGruppi) {
        ArrayList<String> gruppiUtente = new ArrayList<>();
        ConstraintLayout constr;
        constr = (ConstraintLayout) findViewById(R.id.groupZone);
        ArrayList<Button> bottoni = new ArrayList<>();
        Integer counter = new Integer(0);

        //itera tutti i gruppi
        for (Map.Entry<String, Object> entry : mappaGruppi.entrySet()){
            //Get user map
            Map gruppoTrovato = (Map) entry.getValue();
            String idGruppo =  entry.getKey();
            //Aggiungi alla lista dei gruppi se il gruppo è dell'utente
            if (gruppoTrovato.get("owner_id").equals(mAuth.getCurrentUser().getUid())){
                gruppiUtente.add((String) gruppoTrovato.get("name"));
                Button btn = new Button(this);
                btn.setX(100);
                btn.setY(200*(counter+1));
                btn.setHeight(80);
                btn.setWidth(875);
                //btn.setWidth((int) btn.getTextSize());
                btn.setText((String) gruppoTrovato.get("name"));
                btn.setTag(counter);
                btn.setOnClickListener(v -> {
                    Intent i = new Intent(DrawerMenu.this, Group.class);
                    i.putExtra("group_name", (String) gruppoTrovato.get("name"));
                    i.putExtra("group_id", idGruppo);
                    startActivity(i);
                });
                constr.addView(btn);

                bottoni.add(btn);
                //per adesso i gruppi mostrati, se superiori a 3, sovrascrivono il resto dei pulsanti dell'itnerfaccia
                counter += 1;
            }
        }
    }


    public void apriNotifiche(View vi){
        Intent io = new Intent(this, PopupNotifiche.class);
        startActivity(io);
    }


    public void create(View v){
        Intent i = new Intent(this, NewGroupCreation.class);
        startActivity(i);
    }

    //per aprire le activity dal menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.nav_homepage://prendo gli id da drawer_menu
                Intent intent = new Intent(this, DrawerMenu.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                Intent profilo = new Intent(this, AccountActivity.class);
                startActivity(profilo);
                break;
            case R.id.group_create:
                Intent creagruppo = new Intent(this, NewGroupCreation.class);
                startActivity(creagruppo);
                break;
            //CREA ATTIVITà IND
            //INVITA AMICI
            case R.id.nav_invite_friends:
                Intent invite = new Intent(this,InviteFriends.class);
                startActivity(invite);
                break;
            case R.id.nav_faqs:
                Intent faq = new Intent(this, Faq.class);
                startActivity(faq);
                break;
            case R.id.nav_guida:
                Intent guida = new Intent(this, GuidaAvviamento.class);
                startActivity(guida);
                break;
            case R.id.nav_info:
                Intent info = new Intent(this, Project.class);
                startActivity(info);
                break;
            case R.id.nav_exit:
                FirebaseAuth.getInstance().signOut();
                Intent esci = new Intent(this, MainActivity.class);
                startActivity(esci);
                break;
        }
        //prova
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}

