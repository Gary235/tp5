package com.example.parctfoto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
ImageView img;
Button foto,gal;
Boolean TodosPermisos=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img=findViewById(R.id.imagen);
        foto=findViewById(R.id.Foto);
        gal=findViewById(R.id.Galeria);
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
        if(TodosPermisos==false)
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
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccione una imagen"), 1);

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
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                            // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                            img.setImageBitmap(bmp);
                        }
                    }
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap fotoRecibida = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    img.setImageBitmap(fotoRecibida);
                }



                break;
        }
    }

}
