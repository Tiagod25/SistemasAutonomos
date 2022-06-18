package com.example.independence;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewDataActivity extends AppCompatActivity {
    private ListView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        view=findViewById(R.id.lista1);
        view.setDividerHeight(8);
        ArrayList<String> a=new ArrayList<String>();
        a.add("Número de Chamadas Efetuadas");
        a.add("Número de Chamadas Recebidas");
        a.add("Número de Mensagens Efetuadas");
        a.add("Número de Mensagens Recebidas");
        a.add("Número de Clicks");
        a.add("Número de Clicks Home");
        a.add("Número de Notificações");
        a.add("Número de Notificações de Redes Sociais");
        a.add("Número de Notificações de Chatting");
        a.add("Número de Notificações de Outras Apps");
        a.add("Número de Ativações de Ecrã");
        a.add("Tempo de Ecrã");
        a.add("Pontuações");
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,a);
        view.setAdapter(arrayAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });
    }
    private void openActivity(int tipo) {
        Intent intent= new Intent(this,GraphFragment.class);
        Bundle b= new Bundle();
        b.putInt("key",tipo);
       // b.putString("id",getAndroidId());
       // b.putIntegerArrayList("array",dados);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}
