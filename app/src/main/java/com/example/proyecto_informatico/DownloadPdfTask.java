package com.example.proyecto_informatico;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Tarea para descargar un PDF de una URL y guardarlo en la URI proporcionada
 */
public class DownloadPdfTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "DownloadPdfTask";

    private final Context context;
    private final ContentResolver resolver;
    private final Uri destinoUri;

    public DownloadPdfTask(Context context, ContentResolver resolver, Uri destinoUri) {
        this.context = context;
        this.resolver = resolver;
        this.destinoUri = destinoUri;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        HttpURLConnection connection = null;
        InputStream input = null;
        OutputStream output = null;

        try {
            // Validar URL
            if (urlString == null || urlString.isEmpty()) {
                return "URL del PDF no válida";
            }

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error del servidor: Código HTTP " + responseCode;
            }

            // Preparar streams
            input = connection.getInputStream();
            output = resolver.openOutputStream(destinoUri);

            if (output == null) {
                return "No se puede escribir en la ubicación seleccionada";
            }

            // Transferir datos
            byte[] buffer = new byte[8192];  // Buffer más grande para mejor rendimiento
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = input.read(buffer)) != -1) {
                if (isCancelled()) {
                    return "Descarga cancelada";
                }
                output.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            Log.d(TAG, "PDF descargado exitosamente. Tamaño: " + totalBytes + " bytes");
            return null; // Éxito

        } catch (MalformedURLException e) {
            return "URL mal formada: " + urlString;
        } catch (UnknownHostException e) {
            return "No se pudo conectar al servidor. Verifica tu conexión a internet";
        } catch (SocketTimeoutException e) {
            return "Tiempo de espera agotado. Inténtalo de nuevo";
        } catch (FileNotFoundException e) {
            return "Archivo no encontrado en el servidor";
        } catch (IOException e) {
            return "Error de red: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        } finally {
            // Cerrar recursos en bloque finally
            try {
                if (input != null) input.close();
            } catch (IOException e) {
                Log.e(TAG, "Error al cerrar InputStream", e);
            }
            try {
                if (output != null) output.close();
            } catch (IOException e) {
                Log.e(TAG, "Error al cerrar OutputStream", e);
            }
            if (connection != null) connection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        if (errorMessage == null) {
            Toast.makeText(context, "PDF guardado exitosamente", Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "Error al descargar PDF: " + errorMessage);
            Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, "Descarga cancelada", Toast.LENGTH_SHORT).show();
    }
}