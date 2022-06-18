package com.example.independence;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphFragment extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("registos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("size é "+MainActivity.dados.size());
        ArrayList<Registo> nchamadas = MainActivity.dados;
        setContentView(R.layout.fragment_graph);
        int l = getIntent().getExtras().getInt("key");
        LineChartView lineChartView = findViewById(R.id.chart);
        int[] axisData = {};
        int cont=0;
        int[] yAxisData = {};
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#828282"));
        for (int i = 0; i <15; i++) {
            axisValues.add(i, new AxisValue(i+1).setLabel(""+(MainActivity.ultimos15.get(i).get(Calendar.DAY_OF_MONTH))));
        }
        int flag=1;
        System.out.println("TAMANHO"+nchamadas.size());
        System.out.println("TAMANHO PONTUA  "+MainActivity.pontuacao.size());
        if (l<12) {
            for (Registo k : nchamadas) {
                if (flag < 16) {
                    if (l == 0) yAxisValues.add(new PointValue(flag, k.getnChamadasEfet()));
                    if (l == 1) yAxisValues.add(new PointValue(flag, k.getnChamadasRecebidas()));
                    if (l == 2) yAxisValues.add(new PointValue(flag, k.getnSMSEnviadas()));
                    if (l == 3) yAxisValues.add(new PointValue(flag, k.getnSMSRecebidas()));
                    if (l == 4) yAxisValues.add(new PointValue(flag, k.getnClicksRecentes()));
                    if (l == 5) yAxisValues.add(new PointValue(flag, k.getnClicksHome()));
                    if (l == 6) yAxisValues.add(new PointValue(flag, k.getnNotificaces()));
                    if (l == 7) yAxisValues.add(new PointValue(flag, k.getnNotifSociais()));
                    if (l == 8) yAxisValues.add(new PointValue(flag, k.getnNotifChatting()));
                    if (l == 9) yAxisValues.add(new PointValue(flag, k.getnNotifOutrasApps()));
                    if (l == 10) yAxisValues.add(new PointValue(flag, k.getnAtivacoesEcra()));
                    if (l == 11) yAxisValues.add(new PointValue(flag, (float)k.getTempo_ecra()));
                    flag++;
                }
            }
        }
        else{
            flag=1;
            for (int i:MainActivity.pontuacao){
                yAxisValues.add(new PointValue(flag,i));
                flag++;
            }
        }
        List lines = new ArrayList();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        lineChartView.setLineChartData(data);
        Axis axis = new Axis();
        axis.setValues(axisValues);
        data.setAxisXBottom(axis);
        Axis yAxis = new Axis();
        SimpleAxisValueFormatter formatter = new SimpleAxisValueFormatter();
        //formatter.setDecimalDigitsNumber(1);
        yAxis.setFormatter(formatter);
        data.setAxisYLeft(yAxis);
        axis.setTextSize(16);

    }
}