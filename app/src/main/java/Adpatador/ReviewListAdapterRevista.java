package Adpatador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.examensupletorio.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public  class ReviewListAdapterRevista extends RecyclerView.Adapter<ReviewListAdapterRevista.ReviewListViewHolder> implements View.OnClickListener {
    private static View.OnClickListener listen;
    ArrayList<ArticulosRevistas> lista;
    Context context;
    int width = 0;
    LinearLayout linearLayout;
    //private View.OnClickListener listen;

    public ReviewListAdapterRevista(ArrayList<ArticulosRevistas> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }
    @NonNull
    @Override
    public ReviewListAdapterRevista.ReviewListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.articulo, null, false);
        view.setOnClickListener(this);
        return new ReviewListAdapterRevista.ReviewListViewHolder(view);
    }
    public static void setOnclickListener(View.OnClickListener listener) {
        listen = listener;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapterRevista.ReviewListViewHolder reviewListViewHolder, int i) {
        reviewListViewHolder.titulo.setText("Titulo:"+lista.get(i).getTitulo());
        reviewListViewHolder.fecha.setText("Fecha: "+lista.get(i).getFecha());
        if (lista.get(i).getUrl() != "") {
            Glide.with(context)
                    .load(lista.get(i).getUrl())
                    .into(reviewListViewHolder.foto_Contacto);
        } else {
            Glide.with(context)
                    .load(R.drawable.contact_balck)
                    .into(reviewListViewHolder.foto_Contacto);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public void onClick(View v) {
        if (listen != null) {
            listen.onClick(v);
        }
    }

    public class ReviewListViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, fecha;
        CircleImageView foto_Contacto;
        LinearLayout ly;

        public ReviewListViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            foto_Contacto = (CircleImageView) itemView.findViewById(R.id.img_contacto);
        }


    }
}
