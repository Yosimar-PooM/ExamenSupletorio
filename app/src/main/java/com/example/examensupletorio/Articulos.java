package com.example.examensupletorio;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adpatador.ArticulosRevistas;
import Adpatador.Contacto;
import Adpatador.ReviewListAdapter;
import Adpatador.ReviewListAdapterRevista;
import WebServices.Asynchtask;
import WebServices.WebService;

import static android.view.View.*;

public class Articulos extends AppCompatActivity implements Asynchtask, AdapterView.OnItemClickListener {

    private DownloadManager.Request request;
    private DownloadManager downloadManager;
    private long downloadID;
    private ArticulosRevistas[] vectContactos;
    private RecyclerView recyclerView;
    String volumen = "",numero ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);
        Bundle bundle = this.getIntent().getExtras();
        volumen = bundle.getString("volumen");
        numero = bundle.getString("numero");
        consumirWebService();
        getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        getPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    private void consumirWebService(){
        Map<String, String> datos = new HashMap<>();
        String url = "http://revistas.uteq.edu.ec/ws/getarticles.php?"+"volumen="+volumen+"&"+"num="+numero;
        WebService ws = new WebService(url, datos, this, this);
        ws.execute("");
    }

    @Override
    public void processFinish(String result) throws JSONException {

        JSONObject jsonObject= new JSONObject(result);
        JSONArray results= jsonObject.getJSONArray("articles");
        ArrayList<ArticulosRevistas> listaArticulos= ArticulosRevistas.JsonObjectsBuild(results);
        vectContactos=new ArticulosRevistas[listaArticulos.size()];
        for(int i=0;i<vectContactos.length;i++){
            vectContactos[i]=listaArticulos.get(i);
        }

        presentarResultados(listaArticulos);
    }
    private void presentarResultados(ArrayList<ArticulosRevistas> listaContactos) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReviewListAdapterRevista reviewListAdapter = new ReviewListAdapterRevista(listaContactos, this);
        ReviewListAdapterRevista.setOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Posicion: "+ recyclerView.getChildAdapterPosition(v), Toast.LENGTH_LONG).show();
                iniciarDialogo(v);
            }
        });

        recyclerView.setAdapter(reviewListAdapter);
    }
    private void iniciarDialogo(final View param){
        final CharSequence[] opcionesUsuario = {"DESCARGAR", "CANCELAR"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OPCIONES");
        builder.setItems(opcionesUsuario, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opcionesUsuario[which] == "DESCARGAR"){

                }
                else if(opcionesUsuario[which] == "CANCELAR"){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                openDownloadedAttachment(context, downloadId);

            }
        }
    };
    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, intentFilter);
    }
    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this.getApplicationContext(),((Articulos)parent.getItemAtPosition(position)).getUrlPdf(),Toast.LENGTH_LONG).show();
        request = new DownloadManager.Request(Uri.parse(((ArticulosRevistas) parent.getItemAtPosition(position)).getUrl()));
        request.setDescription("PDF	Paper");
        request.setTitle("Pdf Artcilee");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filedownload.pdf");
        downloadManager = (DownloadManager) this.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            downloadID = downloadManager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        //downloadID= downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, intentFilter);
    }

    public void getPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED))
                ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Toast.makeText(this.getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        //gestionamos la finalización de la descarga
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadID, 0);
            Cursor cursor = downloadManager.query(query);

            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //podemos recuperar el fichero descargado
                    //ParcelFileDescriptor file = null;
                    try {
                        //file = manager.openDownloadedFile(downloadID);
                        Toast.makeText(Articulos.this, "Fichero obtenido con éxito!! ", Toast.LENGTH_LONG).show();
                        Intent install = new Intent(intent.ACTION_VIEW);
                        //install.setDataAndType(Uri.fromFile(new File(downloadLocalUri)), "MIME-TYPE");
                        install.setDataAndType(Uri.fromFile(new File(downloadLocalUri)), downloadMimeType);
                        install.setFlags(intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(install);
                    } catch (Exception ex) {
                        Toast.makeText(Articulos.this, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (status == DownloadManager.STATUS_FAILED) {
                    Toast.makeText(Articulos.this, "FAILED: " + reason, Toast.LENGTH_LONG).show();
                } else if (status == DownloadManager.STATUS_PAUSED) {
                    Toast.makeText(Articulos.this, "PAUSED: " + reason, Toast.LENGTH_LONG).show();
                } else if (status == DownloadManager.STATUS_PENDING) {
                    Toast.makeText(Articulos.this, "PENDING: " + reason, Toast.LENGTH_LONG).show();

                } else if (status == DownloadManager.STATUS_RUNNING) {
                    Toast.makeText(Articulos.this, "RUNNING: " + reason, Toast.LENGTH_LONG).show();
                }
            }
        }

    };
    private void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if(attachmentUri!=null) {
            //Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());
                attachmentUri = FileProvider.getUriForFile(this, "com.freshdesk.helpdesk.provider", file);
            }
        }
        Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
        openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType);
        openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try { context.startActivity(openAttachmentIntent); }
        catch (ActivityNotFoundException e) {
            //Toast.makeText(context, context.getString(R.string.app_name), Toast.LENGTH_LONG).show();
        }
    }
    private void openDownloadedAttachment(final Context context, final long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
            }
        }
        cursor.close();
    }
    BroadcastReceiver attachmentDownloadCompleteReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                openDownloadedAttachment(context, downloadId);
            }
        }
    };
    private class DownloadRequestListener extends BroadcastReceiver{


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
            String reqType = intent.getStringExtra("reqType");
            String packageName =intent.getStringExtra("package");

            if(reqType.equals("download")){
                String url = intent.getStringExtra("url");
                String dirPath = intent.getStringExtra("dirPath");
                //downloadFile(url,packageName,dirPath);
            }
            else if(reqType.equals("stop")){
                if(downloadManager!= null){
                    downloadManager.remove(downloadID);
                    unregisterReceiver(onDownloadComplete);
                    stopService(intent);
                }
            }
        }
    }
}
