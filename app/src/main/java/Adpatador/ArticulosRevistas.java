package Adpatador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArticulosRevistas {
    private String titulo;
    private String fecha;
    private String url;
    private String foto;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
    public ArticulosRevistas(JSONObject a) throws JSONException {
        titulo= a.getString("title");
        fecha= a.getString("date_published");
        url= a.getString("pdf");
    }


    public static ArrayList<ArticulosRevistas> JsonObjectsBuild(JSONArray datos) throws JSONException {
        ArrayList<ArticulosRevistas> country = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            country.add(new ArticulosRevistas(datos.getJSONObject(i)));
        }
        return country;
    }

}
