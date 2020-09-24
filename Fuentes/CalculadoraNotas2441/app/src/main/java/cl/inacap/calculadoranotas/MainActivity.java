package cl.inacap.calculadoranotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;


public class MainActivity extends AppCompatActivity {
    private int porcentajeActual = 0;
    private EditText notaTxt;
    private EditText porcentajeTxt;
    private Button agregarBtn;
    private Button limpiarBtn;
    private ListView notasLv;
    private List<Nota> notas = new ArrayList<>();
    private ArrayAdapter<Nota> adapter;
    private TextView promedioTxt;
    private LinearLayout promedioLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.promedioLl = findViewById(R.id.promedioLl);
        this.promedioTxt = findViewById(R.id.promedioTxt);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.porcentajeTxt = findViewById(R.id.porcentajeTxt);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notasLv = findViewById(R.id.notasLv);
        this.adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, notas);
        this.notasLv.setAdapter(adapter);
        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notaTxt.setText("");
                porcentajeTxt.setText("");
                promedioLl.setVisibility(View.INVISIBLE);
                porcentajeActual = 0;
                notas.clear();
                adapter.notifyDataSetChanged();
            }
        });
        this.agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                List<String> errores = new ArrayList<>();
                String notasStr = notaTxt.getText().toString().trim();
                String porcStr = porcentajeTxt.getText().toString().trim();
                int porcentaje = 0;
                double nota = 0;
                try {
                    nota = Double.parseDouble(notasStr);
                    if (nota < 1.0 && nota > 7.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("-La nota debe ser un numero entre 1.0 y 7.0");
                }
                try {

                    porcentaje = Integer.parseInt(porcStr);
                    if (porcentaje < 1.0 && porcentaje > 7.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("-El porcentaje debe ser un numero entre 1 y 100");
                }

                if (errores.isEmpty()) {
                    if (porcentaje + porcentajeActual > 100) {
                        //se excede el valor permitido
                        Toast.makeText(MainActivity.this, "el procentaje no puede ser mayor a 100", Toast.LENGTH_SHORT).show();

                    } else {
                        //ingresar la nota
                        Nota n = new Nota();
                        n.setValor(nota);
                        n.setPorcentaje(porcentaje);
                        porcentajeActual += porcentaje; //va a sumar un porcentaje al otro
                        notas.add(n);
                        adapter.notifyDataSetChanged();
                        mostrarPromedio();
                    }
                } else {
                    mostrarErrores(errores);
                }


            }
        });

    }
    private void mostrarPromedio(){
        double promedio = 0;
        for (Nota n : notas ){
            promedio+= (n.getValor() * n.getPorcentaje()/100);
        }
        this.promedioTxt.setText(String.format("%.1f",promedio));
        if (promedio<4.0){
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorError));
        }else{
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorExitoso));
        }
        this.promedioLl.setVisibility(View.VISIBLE);
    }


    private void mostrarErrores(List<String> errores) {
        String mensaje = "";
        for (String e : errores) {
            mensaje += "-" + e + "/n";
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder
                .setTitle("Error de validacion")//define el titulo
                .setMessage(mensaje)//define el mensaje del dialogo
                .setPositiveButton("Aceptar", null)//agrega el btn aceptar
                .create()//alert de que lo crea
                .show();//lo muestra
    }
}