package com.example.parctfoto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    FrameLayout holder;
    FragmentManager adminFragment;
    FragmentTransaction transaccionFragment;
    Fragment frag;

    ImageView img;
    Button foto,gal;
    Boolean TodosPermisos = false;
    FaceServiceRestClient servicioProcesamientoImagenes;
    SharedPreferences preferencias;
    Bitmap bmp;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img=findViewById(R.id.imagen);
        foto=findViewById(R.id.Foto);
        gal=findViewById(R.id.Galeria);
        dialogo = new ProgressDialog(this);

        adminFragment = getFragmentManager();
        holder = findViewById(R.id.holder);
        frag = new FragResultados();
        transaccionFragment=adminFragment.beginTransaction();
        transaccionFragment.replace(R.id.holder, frag);
        transaccionFragment.addToBackStack(null);
        transaccionFragment.commit();
        holder.setVisibility(View.GONE);

        String apiEndPoint = "https://mirecursodeface.cognitiveservices.azure.com/face/v1.0/";
        String subscriptionKey = "d66b28ec4faa40dcbd3d658b44983e0d";

        preferencias = getSharedPreferences("Gary", Context.MODE_PRIVATE);

        try {
             servicioProcesamientoImagenes = new FaceServiceRestClient(apiEndPoint,subscriptionKey);
            Log.d("Servicio", "Todo bien ");
        }catch (Exception error){

            Log.d("Servicio", error.getMessage());
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
            }, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {

            if (grantResults[i] == PackageManager.PERMISSION_DENIED) { TodosPermisos = false; }
            else { TodosPermisos = true; }

        }
        if(!TodosPermisos)
        {
            foto.setEnabled(true);
            gal.setEnabled(true);
        }
    }

    public void  OnClick (View vista)
    {
        Button botonApretado;
        botonApretado= (Button) vista;
        if(gal.getId()== botonApretado.getId()){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), 1);

        }
        else{
            Intent llamarASacarFoto;
            llamarASacarFoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(llamarASacarFoto,2);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImageUri = null;
        Uri selectedImage;

        bmp = null;
        String filePath = null;
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath=selectedImage.getPath();

                    if (requestCode == 1) {
                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getApplicationContext().getContentResolver().openInputStream(selectedImage);

                            } catch (FileNotFoundException e) { e.printStackTrace(); }
                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            bmp = BitmapFactory.decodeStream(imageStream);
                            Log.d("Servicio","Imagen: " + bmp );
                            // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                            img.setImageBitmap(bmp);
                        }
                    }
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    bmp = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    img.setImageBitmap(bmp);
                    Log.d("Servicio","Imagen: " + bmp );
                }
                break;
        }
        procesarImagenObtenida(bmp);
    }




    public void procesarImagenObtenida(final Bitmap imagenAProcesar){

        ByteArrayOutputStream streamSalida = new ByteArrayOutputStream();
        imagenAProcesar.compress(Bitmap.CompressFormat.JPEG,100,streamSalida);
        ByteArrayInputStream streamEntrada = new ByteArrayInputStream(streamSalida.toByteArray());

        class procesarImagen extends AsyncTask<InputStream,String, Face[]>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialogo.show();
            }

            @Override
            protected Face[] doInBackground(InputStream... inputStreams) {
                publishProgress("Detectando caras...");

                Face[] resultado = null;

                if (inputStreams[0]!=null) {

                    try {
                        Log.d("Servicio", "Entro al try");
                        FaceServiceClient.FaceAttributeType[] atributos;
                        atributos = new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.FacialHair,
                                FaceServiceClient.FaceAttributeType.Gender
                        };
                        Log.d("Servicio", "Sale de atributos, atributos" + atributos);

                        resultado = servicioProcesamientoImagenes.detect(inputStreams[0], true, false, atributos);
                        Log.d("Servicio", "Sale del try");

                    } catch (Exception error) {
                        Log.d("Servicio", "Error: " + error.getMessage());
                    }
                }
                else {
                    Log.d("Servicio", "Input Stream es null");
                }
                return resultado;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                dialogo.setMessage(values[0]);
            }

            @Override
            protected void onPostExecute(Face[] faces) {
                super.onPostExecute(faces);
                dialogo.dismiss();
                if(faces == null) { img.setImageResource(android.R.drawable.ic_dialog_alert); }
                else {
                    if(faces.length > 0)
                    {
                        //procesarResultadosdeCara(faces);
                        holder.setVisibility(View.VISIBLE);
                    }
                    else {
                        //No se detecta ninguna cara
                    }
                }

            }
        }

        procesarImagen miTarea = new procesarImagen();
        miTarea.execute(streamEntrada);
    }

    public void procesarResultadosdeCara(Face[] faces){
        int cantVarones, cantMujeres;
        cantVarones = preferencias.getInt("cantVarones", 0);
        cantMujeres = preferencias.getInt("cantMujeres", 0);

        String mensaje = "";
        for (int i = 0; i < faces.length; i++)
        {
            mensaje += "Edad: " + faces[i].faceAttributes.age;
            mensaje += " - Sonrisa: " + faces[i].faceAttributes.smile;
            mensaje += " - Barba: " + faces[i].faceAttributes.facialHair.beard;
            mensaje += " - Genero: " + faces[i].faceAttributes.gender;
            mensaje += " - Anteojos: " + faces[i].faceAttributes.glasses;

            if(faces[i].faceAttributes.gender.equals("male")){
                cantVarones++;
            }
            else {
                cantMujeres++;
            }

            SharedPreferences.Editor editor;
            editor = preferencias.edit();
            editor.putInt("cantVarones", cantVarones);
            editor.putInt("cantMujeres", cantMujeres);
            editor.commit();

            if(i < faces.length -1)
            {
                mensaje += "\n";
            }

        }

        mensaje += "- H: " + cantVarones + "- M: " + cantMujeres;
    }

}
