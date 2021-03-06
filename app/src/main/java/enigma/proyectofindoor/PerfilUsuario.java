package enigma.proyectofindoor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import Datos.ImageTask;
import Datos.JsonTask;

public class PerfilUsuario extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    int value=-1;
    int seguido=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        Intent intent= getIntent();
        value= intent.getIntExtra("valor",-1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences= this.getSharedPreferences("enigma.proyectofindoor", getApplicationContext().MODE_PRIVATE);
        TextView textView1= findViewById(R.id.textView);
        TextView textView2= findViewById(R.id.textView2);
        ImageView imageView= findViewById(R.id.imageView3);
        if(value!=-1){
        textView1.setText(MainActivity.personas.get(value).getNombre());
        textView2.setText(MainActivity.personas.get(value).getApellido());
        ImageTask imageTask = new ImageTask();
        Bitmap bitmap= null;
        try {
            bitmap=imageTask.execute(MainActivity.personas.get(value).getUrlImagen()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);}
        isSeguido();
    }

    public void isSeguido(){
        //https://findoor.herokuapp.com/persona/seguido/2/KEY=dHIDVcnvHroP6o3hbeIK/
        String url="http://findoor.herokuapp.com/persona/seguido/"+MainActivity.personas.get(value).getId()+"/KEY="+sharedPreferences.getString("token", "")+"/";
        JsonTask jsonTask= new JsonTask();
        try {
            String resultado = jsonTask.execute(url).get();
            Log.d("JSON", resultado);
            JSONObject obj = new JSONObject(resultado);
            String respuesta = obj.getString("respuesta");
            Log.d("respuesta", respuesta);
            String token = obj.getString("token");
            sharedPreferences.edit().putString("token",token).apply();
            if(respuesta.equals("true")){
                Log.d("SI", "ENTRO");
                ImageView imageView= findViewById(R.id.imageView13);
                imageView.setImageResource(R.drawable.followers);
                TextView t= findViewById(R.id.textView16);
                t.setText("Siguiendo...");
                seguido= 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void pushFollow(View v)  {
        ImageView imageView= findViewById(R.id.imageView13);
        if(seguido==0){
            //sitio/add/TYPE=type/id_site/KEY=token
            String token= sharedPreferences.getString("token","");
            String url="https://findoor.herokuapp.com/persona/seguir/"+MainActivity.personas.get(value).getId()+"/KEY="+token+"/";
            JsonTask jsonTask= new JsonTask();
            try {
                String resultado=jsonTask.execute(url).get();
                sharedPreferences.edit().putString("token",resultado).apply();
                imageView.setImageResource(R.drawable.followers);
                TextView t= findViewById(R.id.textView16);
                t.setText("Siguiendo...");
                seguido=1;
                Toast.makeText(getApplicationContext(),"Sigues a "+MainActivity.personas.get(value).getNombre(),Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else{
            //sitio/add/TYPE=type/id_site/KEY=token
            String token= sharedPreferences.getString("token","");
            String url="https://findoor.herokuapp.com/persona/delete/"+MainActivity.personas.get(value).getId()+"/KEY="+token+"/";
            JsonTask jsonTask= new JsonTask();
            try {
                String resultado=jsonTask.execute(url).get();
                sharedPreferences.edit().putString("token",resultado).apply();
                imageView.setImageResource(R.drawable.follows);
                TextView t= findViewById(R.id.textView16);
                t.setText("Seguir");
                seguido=0;
                Toast.makeText(getApplicationContext(),"Ya no sigues a "+MainActivity.personas.get(value).getNombre(),Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    
}
