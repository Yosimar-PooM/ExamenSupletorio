package com.example.examensupletorio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adpatador.Contacto;
import Adpatador.ReviewListAdapter;
import WebServices.Asynchtask;
import WebServices.WebService;

public  class MainActivity extends AppCompatActivity implements Asynchtask {

    private Contacto[] vectContactos;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        consumirWebService();

    }
    private void consumirWebService(){
        Map<String, String> datos = new HashMap<>();
        String url = "http://revistas.uteq.edu.ec/ws/getrevistas.php";
        WebService ws = new WebService(url, datos, this, this);
        ws.execute("");
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONObject jsonObject= new JSONObject(result);
        JSONArray results= jsonObject.getJSONArray("issues");
        ArrayList<Contacto> listaContactos=Contacto.JsonObjectsBuild(results);
        vectContactos=new Contacto[listaContactos.size()];
        for(int i=0;i<vectContactos.length;i++){
            vectContactos[i]=listaContactos.get(i);
        }

        presentarResultados(listaContactos);
    }
    private void presentarResultados(ArrayList<Contacto> listaContactos) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(listaContactos, this);
        reviewListAdapter.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Posicion: "+ recyclerView.getChildAdapterPosition(v), Toast.LENGTH_LONG).show();
                iniciarDialogo(v);
            }
        });

        recyclerView.setAdapter(reviewListAdapter);
    }
    private void iniciarDialogo(final View param){
        final CharSequence[] opcionesUsuario = {"MOSTRAR REVISTAS", "CANCELAR"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OPCIONES");
        builder.setItems(opcionesUsuario, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opcionesUsuario[which] == "MOSTRAR REVISTAS"){
                    Intent intent=new Intent(getApplicationContext(),Articulos.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("volumen",vectContactos[recyclerView.getChildAdapterPosition(param)].getVolumen());
                    bundle.putString("numero",vectContactos[recyclerView.getChildAdapterPosition(param)].getNumero());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(opcionesUsuario[which] == "CANCELAR"){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


}
