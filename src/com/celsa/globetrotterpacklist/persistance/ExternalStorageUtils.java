package com.celsa.globetrotterpacklist.persistance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.celsa.globetrotterpacklist.common.Constants;

import java.io.*;
import java.util.UUID;

public class ExternalStorageUtils {

    public static String saveItemThumbnail(Bitmap bitmap) throws IOException {
        String itemThumbnailId = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(path + File.separator + Constants.PHOTOS_DIR);

            dir.mkdirs();

            itemThumbnailId = UUID.randomUUID().toString();
            File file = new File(dir, itemThumbnailId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
            } finally {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        }

        return itemThumbnailId;
    }

    public static Bitmap loadItemThumbnail(String photoId) {
        Bitmap photo = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(new File(path + "/" + Constants.PHOTOS_DIR), photoId);

            photo = BitmapFactory.decodeFile(file.getPath());
        }

        return photo;
    }
}