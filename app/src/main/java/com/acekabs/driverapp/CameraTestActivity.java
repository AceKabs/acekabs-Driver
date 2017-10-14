package com.acekabs.driverapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.services.AceKabsDocumentUploadService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Srinivas Devarapalli on 12/12/16.
 */
public class CameraTestActivity extends Activity {
    public static int count = 0;
    ImageView imag1, imag2;
    int TAKE_PHOTO_CODE = 0;
    Uri outputFileUri, outputFileUri2;
    Bitmap mImageBitmap;

    Context mcontext;

    int width = 1920; // 1920
    int height = 1080; // 1080

    Matrix matrix;
    Bitmap resizedBitmap;
    float scaleWidth;
    float scaleHeight;
    ByteArrayOutputStream outputStream;

    Button upload;
    String image_path1, image_path2;
    ArrayList<String> str_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_screen);
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        imag1 = (ImageView) findViewById(R.id.img1);
        imag2 = (ImageView) findViewById(R.id.img2);
        upload = (Button) findViewById(R.id.upload);

        imag1.setImageBitmap(roundCornerImage(BitmapFactory.decodeResource(getResources(), R.drawable.india), 35));

        str_list = new ArrayList<String>();


        imag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   count++;
                String file = dir + "one" + ".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {
                }

                outputFileUri = Uri.fromFile(newfile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

            }
        });


        imag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String file2 = dir + "two" + ".jpg";
                File newfile2 = new File(file2);
                try {
                    newfile2.createNewFile();
                } catch (IOException e) {
                }

                outputFileUri2 = Uri.fromFile(newfile2);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);
                startActivityForResult(cameraIntent, 1);

            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    for (int i = 0; i < str_list.size(); i++) {
                        final FirebaseAuth auth = FirebaseAuth.getInstance();
                        if (auth.getCurrentUser() != null) {
                            uploadDocToFirebase(Uri.parse(str_list.get(i)));
                            Log.e("new_uri ok", outputFileUri.toString());
                        } else {
                            auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    uploadDocToFirebase(outputFileUri);
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                imag1.setImageBitmap(mImageBitmap);
                //  imag1.setImageBitmap(roundCornerImage(mImageBitmap,35));
                // Bitmap bf =  BitmapFactory.decodeFile("");
                resizedBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, width, height, matrix, true);
                outputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                imag1.setImageBitmap(resizedBitmap);
                width = resizedBitmap.getWidth();
                height = resizedBitmap.getHeight();

                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), resizedBitmap, "Title", null);

                str_list.add(image_path1);
                // Login Anonymously
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                imag2.setImageBitmap(mImageBitmap);
                // Bitmap bf =  BitmapFactory.decodeFile("");
                resizedBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, width, height, matrix, true);
                outputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                imag1.setImageBitmap(resizedBitmap);
                width = resizedBitmap.getWidth();
                height = resizedBitmap.getHeight();

                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), resizedBitmap, "Title", null);

                str_list.add(image_path2);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadDocToFirebase(Uri imageUri) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading ....");
        dialog.show();
        new AceKabsDocumentUploadService().uploadFileFromDisk(imageUri, new IFirebaseCallback<Uri>() {
            @Override
            public void onDataReceived(Uri data) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                String storageUrl = data.toString();
                Toast.makeText(CameraTestActivity.this, "Image Uploaded at : " + storageUrl, Toast.LENGTH_SHORT).show();
                Log.d("URL", storageUrl);
            }

            @Override
            public void onFailure(Exception ex) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(CameraTestActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/

    public Bitmap roundCornerImage(Bitmap src, float round) {
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // configure paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // configure rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw Round rectangle to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }
}
