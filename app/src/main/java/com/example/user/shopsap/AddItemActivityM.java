package com.example.user.shopsap;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 24.9.2017.
 */

public class AddItemActivityM extends AppCompatActivity {

    final Context context = this;

    public static String itembarcode="000";
    public static String itemnamex="Ürün Adı";
    public static String descriptionx="Ürün Tanımı";
    public static String avgpricex="0";
    public static String reasonx="000";
    public static String isvalidx;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 0;

    private Button btn_approve;
            private ImageButton btn_image;
    protected ImageView image;
    protected EditText f_name,f_expln,f_count,f_price,f_barcode;
    protected String path;
    protected Bitmap mImageBitmap=null;
    protected static final String PHOTO_TAKEN = "photo_taken";


    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "Images";
    //uri to store file
    private Uri filePath=null;
    private File finalFile;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase,nmDatabase;

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    private StorageReference mStorageRef;


    private FirebaseUser shop;
    private String shopID;

    private String[] subNames={"DİĞER","GIDA","TEMİZLİK","BEBEK-ÇOCUK","KIYAFET","K.BAKIM","Y.GEREÇLERİ","ELEKTRONİK"};
    private static String type="vvv";
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem_2);
        Intent intent = this.getIntent();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_UPLOADS);

        nmDatabase = FirebaseDatabase.getInstance().getReference();

        shop= FirebaseAuth.getInstance().getCurrentUser();
        shopID = shop.getUid();

        btn_image=(ImageButton) findViewById(R.id.item_image);
        f_name =(EditText) findViewById(R.id.item_name);
        f_expln=(EditText) findViewById(R.id.item_expln);
        f_count=(EditText) findViewById(R.id.item_count);
        f_price=(EditText) findViewById(R.id.item_price);
        f_barcode=(EditText) findViewById(R.id.item_barcode);
        btn_approve = (Button) findViewById(R.id.add_approve);






        Spinner spinner = (Spinner) findViewById(R.id.spinnerm);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.s_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                type=item;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getBaseContext(), "chosen nothin", Toast.LENGTH_SHORT).show();
                type="zzz";
            }
        });
        if(isvalidx.equals("true"))
        {
            Toast.makeText(getApplicationContext(), "Ürün bulundu", Toast.LENGTH_SHORT).show();
            f_barcode.setText(getItembarcode().trim());
            f_name.setText(getItemnamex().trim());
            f_expln.setText(getDescriptionx().trim());
            f_price.setText(getAvgpricex().trim());
            btn_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog alertDialog=new AlertDialog.Builder(context).create();

                        alertDialog.setTitle("Resim Yükle");

                        alertDialog.setMessage("Lütfen Ürün Resiminin Kaynağını Seçin!");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Telefondan Seç", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                alertDialog.dismiss();
                                FromCard();

                            } });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yeni Resim Çek", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                alertDialog.dismiss();
                                FromCamera();

                            }});

                        alertDialog.show();
                    }
                });
        }
        else {
            Toast.makeText(getApplicationContext(), "Ürün bulunamadı!!", Toast.LENGTH_SHORT).show();
            f_barcode.setText(getItembarcode().trim());
            btn_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog alertDialog=new AlertDialog.Builder(context).create();

                        alertDialog.setTitle("Resim Yükle");

                        alertDialog.setMessage("Lütfen Ürün Resiminin Kaynağını Seçin!");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Telefondan Seç", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                alertDialog.dismiss();
                                FromCard();

                            } });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yeni Resim Çek", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                alertDialog.dismiss();
                                FromCamera();

                            }});

                        alertDialog.show();
                    }
                });


        }


        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String itemnamey,itemexplny,itemcounty ,itempricey,itembarcodey,itemtype ;
                    itemnamey = f_name.getText().toString();
                    itemexplny= f_expln.getText().toString();
                    itemcounty = f_count.getText().toString().trim();
                    itempricey = f_price.getText().toString().trim();
                    itembarcodey=f_barcode.getText().toString().trim();
                    itemtype=type;

                    setItembarcode(itembarcodey);


                    if (TextUtils.isEmpty(itemnamey)) {
                        Toast.makeText(getApplicationContext(), "Ürün ismi girin!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(itemexplny)) {
                        Toast.makeText(getApplicationContext(), "Ürün açıklaması girin! ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(itemcounty)) {
                        Toast.makeText(getApplicationContext(), "Ürün sayısı girin !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(itempricey)) {
                        Toast.makeText(getApplicationContext(), "Ürün fiyatı girin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(itembarcodey)) {
                        Toast.makeText(getApplicationContext(), "Ürün barkodu girin !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                /// UPLOAD IMAGE
                if(filePath!=null)
                {
                    uploadFile(filePath);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Ürün resmi seçin !", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Toast.makeText(getApplicationContext(), "Pushing", Toast.LENGTH_SHORT).show();
                    Item anitem = new Item(itemnamey,itemexplny,itempricey,itemcounty,itembarcodey,itemtype);
                    nmDatabase.child("Shops").child(shopID).child("Items").child(itembarcodey).push();
                    DatabaseReference newRef = nmDatabase.child("Shops").child(shopID).child("Items").child(itembarcodey);
                    newRef.child("ItemName").setValue(itemnamey);
                    newRef.child("ItemDExpln").setValue(itemexplny);
                    newRef.child("ItemCount").setValue(itemcounty);
                    newRef.child("ItemPrice").setValue(itempricey);
                    newRef.child("ItemType").setValue(itemtype);

                Intent intent = new Intent(AddItemActivityM.this,MenuMarket.class);
                startActivity(intent);

            }
        });


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void FromCamera() {
        startCameraActivity();
    }

    public void FromCard() {
        //Intent i = new Intent(Intent.ACTION_PICK,
          //      android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(i, 0);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_SELECT);

    }

    protected void startCameraActivity() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "MARKET_" + itembarcode + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        //path = "file:" + image.getAbsolutePath();
        path = image.getAbsolutePath();
        return image;
    }


    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int targetW = btn_image.getWidth();
        int targetH = btn_image.getHeight();
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


                    /* Get the size of the image */
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
		/* Figure out which way needs to be reduced less */
                int scaleFactor = 1;
                if ((targetW > 0) || (targetH > 0)) {
                    scaleFactor = Math.min(photoW/targetW, photoH/targetH);
                }
		/* Set bitmap options to scale the image decode target */
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;
		/* Decode the JPEG file into a Bitmap */
                Bitmap bitmap1 = BitmapFactory.decodeFile(path, bmOptions);
                // mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(path));
                // Bitmap resized = Bitmap.createScaledBitmap(mImageBitmap,(int)(mImageBitmap.getWidth()*0.5), (int)(mImageBitmap.getHeight()*0.5), true);
                btn_image.setImageBitmap(bitmap1);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                filePath=getImageUri(getApplicationContext(), bitmap1);
            }
            else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK
                && null != data) {
               /* Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                mImageBitmap = BitmapFactory.decodeFile(picturePath, options);*/
                //mImageBitmap = BitmapFactory.decodeFile(picturePath);
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    Bitmap resized = Bitmap.createScaledBitmap(selectedImage,targetW, targetH, false);
                    btn_image.setImageBitmap(resized);
                    filePath=getImageUri(getApplicationContext(), resized);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(AddItemActivityM.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }




    //////////////////////////////

    private void uploadFile(Uri filePathx) {
        filePath=filePathx;

        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + itembarcode + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            Upload upload = new Upload(f_barcode.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static void setItembarcode(String itembarcode) {
        AddItemActivityM.itembarcode = itembarcode;
    }

    public static void setItemnamex(String itemnamex) {
        AddItemActivityM.itemnamex = itemnamex;
    }

    public static void setDescriptionx(String descriptionx) {
        AddItemActivityM.descriptionx = descriptionx;
    }

    public static void setAvgpricex(String avgpricex) {
        AddItemActivityM.avgpricex = avgpricex;
    }
    public static void setisvalidx(String isvalidx) {
        AddItemActivityM.isvalidx = isvalidx;
    }

    public static String getItemnamex() {
        return itemnamex;
    }
    public static String getisvalidx() {
        return isvalidx;
    }

    public static String getItembarcode() {
        return itembarcode;
    }

    public static String getDescriptionx() {
        return descriptionx;
    }

    public static String getAvgpricex() {
        return avgpricex;
    }

}
