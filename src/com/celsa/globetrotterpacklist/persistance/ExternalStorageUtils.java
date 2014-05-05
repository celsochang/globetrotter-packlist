package com.celsa.globetrotterpacklist.persistance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.celsa.globetrotterpacklist.common.Constants;

import java.io.*;
import java.util.UUID;

public class ExternalStorageUtils {

    public static void saveItemThumbnail(Bitmap bitmap, String filename) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(path + File.separator + Constants.PHOTOS_DIR);

            dir.mkdirs();

            File file = new File(dir, filename);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

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
    }

    public static Bitmap loadItemThumbnail(String filename) {
        Bitmap photo = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(new File(path + "/" + Constants.PHOTOS_DIR), filename);

            photo = BitmapFactory.decodeFile(file.getPath());
        }

        return photo;
    }

    public static boolean deleteItemThumbnail() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(new File(path + "/" + Constants.PHOTOS_DIR), "temp");

        return file.delete();
    }
}