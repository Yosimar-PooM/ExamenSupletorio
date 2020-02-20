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

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder> implements View.OnClickListener {

    ArrayList<Contacto> lista;
    Context context;
    int width = 0;
    LinearLayout linearLayout;
    private View.OnClickListener listener;

    public ReviewListAdapter(ArrayList<Contacto> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_list_row, null, false);
        view.setOnClickListener(this);
        return new ReviewListViewHolder(view);
    }

    public void setOnclickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ReviewListViewHolder reviewListViewHolder, int i) {
        reviewListViewHolder.volumen.setText("Volumen: "+lista.get(i).getVolumen());
        reviewListViewHolder.numero.setText("Numero: "+lista.get(i).getNumero());
        reviewListViewHolder.año.setText("año: "+lista.get(i).getAño());
        if (lista.get(i).getUrl() != "") {
            Glide.with(context)
                    .load(lista.get(i).getUrl())
                    .into(reviewListViewHolder.foto_Contacto);
        } else {
            Glide.with(context)
                    .load(R.drawable.contact)
                    .into(reviewListViewHolder.foto_Contacto);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public static class ReviewListViewHolder extends RecyclerView.ViewHolder {
        TextView volumen, numero, año;
        CircleImageView foto_Contacto;
        LinearLayout ly;

        public ReviewListViewHolder(@NonNull View itemView) {
            super(itemView);
            volumen = (TextView) itemView.findViewById(R.id.nameContact);
            numero = (TextView) itemView.findViewById(R.id.personalNumber);
            año = (TextView) itemView.findViewById(R.id.homeNumber);
            foto_Contacto = (CircleImageView) itemView.findViewById(R.id.img_contacto);
        }


    }
}
