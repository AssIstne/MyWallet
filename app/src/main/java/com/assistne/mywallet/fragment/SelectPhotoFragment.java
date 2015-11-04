package com.assistne.mywallet.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/10/9.
 */
public class SelectPhotoFragment extends Fragment implements View.OnClickListener{

    private BillActivity activity;
    private Uri imgUri;
    private static final int TAKE_PHOTO = 1;
    private static final int SELECT_FROM_ALBUM = 1;

    private boolean isCropping = false;

    private View root;
    private ViewGroup spanCrop;
    private ViewGroup spanButtons;
    private CropImageView cropImageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (BillActivity)getActivity();
        root = inflater.inflate(R.layout.fragment_select_photo, container, false);
        findViews();
        spanCrop.setVisibility(View.GONE);
        spanButtons.setVisibility(View.VISIBLE);
        return root;
    }

    private void findViews() {
        root.findViewById(R.id.select_photo_root).setOnClickListener(this);
        root.findViewById(R.id.select_photo_btn_camera).setOnClickListener(this);
        root.findViewById(R.id.select_photo_btn_album).setOnClickListener(this);
        root.findViewById(R.id.select_photo_btn_cancel).setOnClickListener(this);

        spanCrop = (ViewGroup)root.findViewById(R.id.select_span_crop_photo);
        spanButtons = (ViewGroup)root.findViewById(R.id.select_span_buttons);

        cropImageView = (CropImageView)spanCrop.findViewById(R.id.select_span_crop_img_view);
        spanCrop.findViewById(R.id.select_crop_btn_cancel).setOnClickListener(this);
        spanCrop.findViewById(R.id.select_crop_btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_photo_root:
                if (!isCropping) {
                    activity.removeFragment();
                }
                break;
            case R.id.select_photo_btn_cancel:
                activity.removeFragment();
                break;
            case R.id.select_photo_btn_camera:
                File outputImage = new File(Environment.getExternalStorageDirectory().getPath() + "/myMallet",
                        "bill_" + Calendar.getInstance(Locale.CHINA).getTimeInMillis() + ".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgUri = Uri.fromFile(outputImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.select_photo_btn_album:
                break;
            case R.id.select_crop_btn_cancel:
                activity.removeFragment();
                break;
            case R.id.select_crop_btn_ok:
                FileOutputStream out = null;
                try {
                    File imgPath = new File(imgUri.getPath());
                    out = new FileOutputStream(imgPath);
                    Bitmap img = cropImageView.getCroppedBitmap();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    activity.showPhotoImage(imgUri.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                activity.removeFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("", "Image saved to:\n" + imgUri.getPath());
                isCropping = true;
                spanCrop.setVisibility(View.VISIBLE);
                spanButtons.setVisibility(View.GONE);
                root.setBackgroundColor(Color.WHITE);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imgUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Set image for cropping
                cropImageView.setImageBitmap(bitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
}
