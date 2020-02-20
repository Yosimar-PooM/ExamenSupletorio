package Adpatador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contacto {
    private String nombre;
    private String apellido;
    private String email;
    private String Id_conacto;
    private String volumen;
    private String numero;
    private String año;
    private String url;

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getId_conacto() {
        return Id_conacto;
    }

    public String getUrl_img() {
        return url_img;
    }

    public String[] getNumeros() {
        return numeros;
    }

    public String[] getCoord() {
        return coordenadas;
    }

    private String url_img;

    public String[] getCoordenadas() {
        return coordenadas;
    }


    private String[] numeros = new String[3], coordenadas = new String[2];


    public Contacto(JSONObject a) throws JSONException {
        volumen= a.getString("volume");
        numero= a.getString("number");
        año= a.getString("year");
        url= a.getString("portada");
    }


    public static ArrayList<Contacto> JsonObjectsBuild(JSONArray datos) throws JSONException {
        ArrayList<Contacto> country = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            country.add(new Contacto(datos.getJSONObject(i)));
        }
        return country;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
