package com.rootekstudio.repeatsandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class ZipSet
{
    static void zip(List<String> files, File zipFile)
    {
        BufferedInputStream origin = null;
        try
        {
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            byte data[] = new byte[1024];

            for (int i = 0; i < files.size(); i++)
            {
                String file = files.get(i);
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/")).replace("/",""));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
            }

            origin.close();
            out.close();

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static void UnZip(InputStream Zip, File directory)
    {
        try
        {
            ZipInputStream zis = new ZipInputStream(Zip);
            ZipEntry ze;

            while((ze = zis.getNextEntry()) != null)
            {
                FileOutputStream fos = new FileOutputStream(directory + "/"+ ze.getName());

                BufferedInputStream inputStream = new BufferedInputStream(zis);
                BufferedOutputStream outputStream = new BufferedOutputStream(fos, 1024);

                byte data[] = new byte[1024];
                int count;
                while ((count = inputStream.read(data, 0, 1024)) != -1)
                {
                    outputStream.write(data, 0, count);
                }

                zis.closeEntry();
                outputStream.flush();
                outputStream.close();
                fos.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}