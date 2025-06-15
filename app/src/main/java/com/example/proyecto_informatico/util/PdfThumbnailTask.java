// PdfThumbnailTask.java
package com.example.proyecto_informatico.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
import okio.BufferedSink;
import okio.Okio;

public class PdfThumbnailTask extends AsyncTask<String, Void, Bitmap> {
    private final Context context;
    private final ImageView thumbnailView;

    public PdfThumbnailTask(Context context, ImageView thumbnailView) {
        this.context = context.getApplicationContext();
        this.thumbnailView = thumbnailView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        try {
            // 1. Descargar PDF al cache
            File file = new File(context.getCacheDir(), "tmp.pdf");
            OkHttpClient client = new OkHttpClient();
            Request req = new Request.Builder().url(url).build();
            try (Response resp = client.newCall(req).execute();
                 BufferedSink sink = Okio.buffer(Okio.sink(file));
                 BufferedSource src = resp.body().source()) {
                sink.writeAll(src);
            }

            // 2. Abrir con PdfRenderer
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(pfd);

            // 3. Renderizar página 0
            PdfRenderer.Page page = renderer.openPage(0);
            Bitmap bmp = Bitmap.createBitmap(
                    page.getWidth(),
                    page.getHeight(),
                    Bitmap.Config.ARGB_8888);
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // 4. Cerrar recursos
            page.close();
            renderer.close();
            pfd.close();

            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap thumbnail) {
        if (thumbnail != null) {
            thumbnailView.setImageBitmap(thumbnail);
        } else {
            Toast.makeText(context,
                    "Error al generar vista previa", Toast.LENGTH_SHORT).show();
        }
    }
}
