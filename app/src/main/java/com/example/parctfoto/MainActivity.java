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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import lecho.lib.hellocharts.util.ChartUtils;

public class MainActivity extends AppCompatActivity {

    public static FrameLayout holder;
    FragmentManager adminFragment;
    FragmentTransaction transaccionFragment;
    Fragment frag;

    CheckBox check1,check2,check3,check4,check5,check6;
    ImageView img;
    ImageButton btnFlecha;
    Button foto, gal,borrar;
    Boolean TodosPermisos = false;
    FaceServiceRestClient servicioProcesamientoImagenes;
    SharedPreferences preferencias;
    Bitmap bmp;
    ProgressDialog dialogo;

    public static ArrayList<Integer>  arrCant = new ArrayList<>(), arrCantAcum = new ArrayList<>();
    public static ArrayList<Float> arrProm = new ArrayList<>(), arrPromAcum = new ArrayList<>();
    ArrayList<Boolean> arrCheckBox = new ArrayList<>();
    public static ArrayList<Emocion> arrEmociones = new ArrayList<>();

    public static double promEdad, promEdadAcum;
    public static int cantH, cantHAcum;
    public static int cantM, cantMAcum;
    public static int cantFotos=0;
    Double acumEdadesAcum = 0.0; int cantAcum=0;
    float cantMujerViejaAcum = 0, cantMujerJovenAcum = 0;
    int cantCalvosFelicesAcum = 0, cantNoCalvosFelicesAcum  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cantH=0; cantM=0;
        btnFlecha = findViewById(R.id.btnFlecha);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);
        check5 = findViewById(R.id.check5);
        check6 = findViewById(R.id.check6);
        img = findViewById(R.id.imagen);
        foto = findViewById(R.id.Foto);
        gal = findViewById(R.id.Galeria);
        dialogo = new ProgressDialog(this);
        borrar= findViewById(R.id.BorraDatos);
        adminFragment = getFragmentManager();
        holder = findViewById(R.id.holder);
        holder.setVisibility(View.GONE);

        String apiEndPoint = "https://mirecursodeface.cognitiveservices.azure.com/face/v1.0/";
        String subscriptionKey = "d66b28ec4faa40dcbd3d658b44983e0d";

        preferencias = getSharedPreferences("Gary", Context.MODE_PRIVATE);

        try {
            servicioProcesamientoImagenes = new FaceServiceRestClient(apiEndPoint, subscriptionKey);
            Log.d("Servicio", "Todo bien ");
        } catch (Exception error) {

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

        btnFlecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getVisibility() == View.GONE)
                {

                    procesarImagenObtenida(bmp);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {

            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                TodosPermisos = false;
            } else {
                TodosPermisos = true;
            }

        }
        if (!TodosPermisos) {
            foto.setEnabled(true);
            gal.setEnabled(true);
        }
    }

    public void OnClick(View vista) {
        Button botonApretado;
        botonApretado = (Button) vista;
        if (gal.getId() == botonApretado.getId()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), 1);

        }
        if(borrar.getId() == botonApretado.getId())
        {
            cantCalvosFelicesAcum=0;
            cantNoCalvosFelicesAcum=0;
            cantMujerViejaAcum=0;
            cantMujerJovenAcum=0;
            cantHAcum=0;
            cantMAcum=0;
            cantAcum=0;
            acumEdadesAcum=0.0;
            cantFotos=0;
        }
        if(foto.getId() == botonApretado.getId()){
            Intent llamarASacarFoto;
            llamarASacarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(llamarASacarFoto, 2);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);Uri selectedImage;

        bmp = null;
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath = selectedImage.getPath();

                    if (requestCode == 1) {
                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getApplicationContext().getContentResolver().openInputStream(selectedImage);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            bmp = BitmapFactory.decodeStream(imageStream);
                            Log.d("Servicio", "Imagen: " + bmp);
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
                    Log.d("Servicio", "Imagen: " + bmp);
                }
                break;
        }
        procesarImagenObtenida(bmp);
    }


    public void procesarImagenObtenida(final Bitmap imagenAProcesar) {
        cantFotos++;
        ByteArrayOutputStream streamSalida = new ByteArrayOutputStream();
        imagenAProcesar.compress(Bitmap.CompressFormat.JPEG, 100, streamSalida);
        ByteArrayInputStream streamEntrada = new ByteArrayInputStream(streamSalida.toByteArray());

        class procesarImagen extends AsyncTask<InputStream, String, Face[]> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialogo.show();
            }

            @Override
            protected Face[] doInBackground(InputStream... inputStreams) {
                publishProgress("Cargando...");

                Face[] resultado = null;

                arrCheckBox.clear();
                arrCheckBox.add(check1.isChecked());
                arrCheckBox.add(check2.isChecked());
                arrCheckBox.add(check3.isChecked());
                arrCheckBox.add(check4.isChecked());
                arrCheckBox.add(check5.isChecked());
                arrCheckBox.add(check6.isChecked());
                arrEmociones.clear();
                cantH = 0;
                cantM = 0;


                if (inputStreams[0] != null) {
                    try {
                        Log.d("Servicio", "Entro al try");
                        FaceServiceClient.FaceAttributeType[] atributos;
                        atributos = new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Emotion,
                                FaceServiceClient.FaceAttributeType.Hair,
                                FaceServiceClient.FaceAttributeType.Accessories,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.FacialHair,
                                FaceServiceClient.FaceAttributeType.Exposure,
                                FaceServiceClient.FaceAttributeType.Blur,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.Makeup,
                                FaceServiceClient.FaceAttributeType.Occlusion,
                                FaceServiceClient.FaceAttributeType.Noise
                        };
                        Log.d("Servicio", "Sale de atributos, atributos" + atributos);
                        resultado = servicioProcesamientoImagenes.detect(inputStreams[0], true, false, atributos);
                        Log.d("Servicio", "Sale del try");
                    } catch (Exception error) {
                        Log.d("Servicio", "Error: " + error.getMessage());
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                        toast1.show();
                    }
                } else {
                    Log.d("Servicio", "Input Stream es null");
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                    toast1.show();
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
                if (faces == null) {
                    img.setImageResource(android.R.drawable.ic_dialog_alert);
                } else {
                    if (faces.length > 0) {
                        arrCant.clear();
                        arrProm.clear();
                        btnFlecha.setVisibility(View.VISIBLE);

                        procesarCalvosFelices(faces);
                        procesarMakeUp(faces);
                        procesarEmociones(faces);
                        procesarCantSexo(faces);
                        procesarEdades(faces);
                        procesarGeneral(faces);

                        frag = new FragResultados();
                        transaccionFragment = adminFragment.beginTransaction();
                        transaccionFragment.replace(R.id.holder, frag);
                        transaccionFragment.addToBackStack(null);
                        transaccionFragment.commit();
                        holder.setVisibility(View.VISIBLE);
                        encuadrarCaras(faces,bmp);

                    } else {
                        //No se detecta ninguna cara
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                        toast1.show();
                    }
                }

            }
        }

        procesarImagen miTarea = new procesarImagen();
        miTarea.execute(streamEntrada);
    }
    public void encuadrarCaras (Face[] faces, Bitmap imag)
    {
        Bitmap imgAdibujar;
        imgAdibujar=imag.copy(Bitmap.Config.ARGB_8888,true);
        Canvas lienzo = new Canvas(imgAdibujar);
        Paint pincel = new Paint();
        pincel.setAntiAlias(true);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeWidth(5);
        for (Face unaCara:faces)
        {
            pincel.setColor( ChartUtils.pickColor());
            FaceRectangle rectanguloCara=unaCara.faceRectangle;
            lienzo.drawRect(rectanguloCara.left,rectanguloCara.top,rectanguloCara.left+rectanguloCara.width,rectanguloCara.top+rectanguloCara.height,pincel);
        }
        img.setImageBitmap(imgAdibujar);
    }
    public  void procesarCalvosFelices(Face[] faces) {
        int cantCalvosFelices = 0, cantNoCalvosFelices = 0;


        for (int i = 0; i < faces.length; i++) {

            if (faces[i].faceAttributes.emotion.happiness > 0.5) {
                if (faces[i].faceAttributes.hair.bald > 0.5) {
                    cantCalvosFelices++;
                } else {
                    cantNoCalvosFelices++;
                }
            }

        }

        arrCant.add(cantCalvosFelices);
        arrCant.add(cantNoCalvosFelices);

    }
    public void procesarMakeUp(Face[] faces) {
        float cantMujerVieja = 0, cantMujerJoven = 0;

        for (int i = 0; i < faces.length; i++) {
            if (faces[i].faceAttributes.gender.equals("female")) {
                if (faces[i].faceAttributes.age > 60) {
                    if(faces[i].faceAttributes.makeup.lipMakeup || faces[i].faceAttributes.makeup.eyeMakeup) cantMujerVieja++;
                } else {
                    if(faces[i].faceAttributes.makeup.lipMakeup || faces[i].faceAttributes.makeup.eyeMakeup) cantMujerJoven++;
                }
            }
        }

        arrProm.add(cantMujerJoven);
        arrProm.add(cantMujerVieja);

    }
    public void procesarEmociones(Face[] faces){
        boolean felicidad = false, tristeza= false, enojo= false, disgusto= false,neutral= false,miedo= false,sorpresa= false;

        for (int i = 0; i < faces.length; i++) {

            if(faces[i].faceAttributes.emotion.happiness > 0.5){
                felicidad = true;
            }
            else if(faces[i].faceAttributes.emotion.anger > 0.5)
            {
                enojo = true;
            }
            else if(faces[i].faceAttributes.emotion.disgust > 0.5)
            {
                disgusto = true;
            }
            else if(faces[i].faceAttributes.emotion.sadness > 0.5)
            {
                tristeza = true;
            }
            else if(faces[i].faceAttributes.emotion.neutral > 0.5)
            {
                neutral = true;
            }
            else if(faces[i].faceAttributes.emotion.fear > 0.5)
            {
                miedo = true;
            }
            else if(faces[i].faceAttributes.emotion.surprise > 0.5)
            {
                sorpresa = true;
            }

        }
            Emocion emocion;

            emocion = new Emocion();
            emocion.setNombre("Felicidad");
            emocion.setHayoNo(felicidad);
            arrEmociones.add(emocion);

            emocion = new Emocion();
            emocion.setNombre("Tristeza");
            emocion.setHayoNo(tristeza);
            arrEmociones.add(emocion);

            emocion = new Emocion();

            emocion.setNombre("Enojo");
            emocion.setHayoNo(enojo);
            arrEmociones.add(emocion);

            emocion = new Emocion();

            emocion.setNombre("Disgusto");
            emocion.setHayoNo(disgusto);
            arrEmociones.add(emocion);

            emocion = new Emocion();

            emocion.setNombre("Neutralidad");
            emocion.setHayoNo(neutral);
            arrEmociones.add(emocion);

            emocion = new Emocion();

            emocion.setNombre("Miedo");
            emocion.setHayoNo(miedo);
            arrEmociones.add(emocion);

            emocion = new Emocion();

            emocion.setNombre("Sorpresa");
            emocion.setHayoNo(sorpresa);
            arrEmociones.add(emocion);


    }
    public void procesarCantSexo(Face[] faces) {
        for (int i = 0; i < faces.length; i++) {
            if (faces[i].faceAttributes.gender.equals("female")) {
                cantM++;
            } else {
                cantH++;
            }
        }
    }
    public void procesarEdades(Face[] faces) {
        Double acumEdades = 0.0; int cant=0;
        for (int i = 0; i < faces.length; i++) {
            acumEdades+=faces[i].faceAttributes.age;
            cant++;
        }
        promEdad = acumEdades/cant;
    }
    public void procesarGeneral(Face[] faces){
        arrCantAcum.clear();
        for (int i = 0; i < faces.length; i++) {

            if (faces[i].faceAttributes.emotion.happiness > 0.5) {
                if (faces[i].faceAttributes.hair.bald > 0.5) {
                    cantCalvosFelicesAcum++;
                } else {
                    cantNoCalvosFelicesAcum++;
                }
            }
        }
        Log.d("PRUEBAGENERAL", "CalvosF: " + cantCalvosFelicesAcum + ", NoCalvosF: " + cantNoCalvosFelicesAcum);

        arrCantAcum.add(cantCalvosFelicesAcum );
        arrCantAcum.add(cantNoCalvosFelicesAcum );

        ///////////////////////////////////////////////////////////////////////////

        arrPromAcum.clear();
        for (int i = 0; i < faces.length; i++) {
            if (faces[i].faceAttributes.gender.equals("female")) {
                if (faces[i].faceAttributes.age > 60) {
                    if(faces[i].faceAttributes.makeup.lipMakeup || faces[i].faceAttributes.makeup.eyeMakeup) cantMujerViejaAcum++;
                } else {
                    if(faces[i].faceAttributes.makeup.lipMakeup || faces[i].faceAttributes.makeup.eyeMakeup) cantMujerJovenAcum++;
                }
            }
        }

        arrPromAcum.add(cantMujerJovenAcum);
        arrPromAcum.add(cantMujerViejaAcum);

        ///////////////////////////////////////////////////////////////////////////

        for (int i = 0; i < faces.length; i++) {
            if (faces[i].faceAttributes.gender.equals("female")) {
                cantMAcum++;
            } else {
                cantHAcum++;
            }
        }

        ///////////////////////////////////////////////////////////////////////////

        for (int i = 0; i < faces.length; i++) {
            acumEdadesAcum += faces[i].faceAttributes.age;
            cantAcum++;
        }
        promEdadAcum = acumEdadesAcum/cantAcum;
    }
    //------
    public ArrayList<Boolean> devolverCheck()
    {
            return arrCheckBox;
    }

}