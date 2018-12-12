package it.pgp.xzexample;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.tukaani.xz.XZInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by pgp on 06/11/16
 */

class Compressor {
    public static final int XZ_MAX_COMPRESSION = 7;
    URI createTarGzip(File inputDirectory, File outputFile) throws IOException {

        DirTreeWalker dtw = new DirTreeWalker(inputDirectory);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
             TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipOutputStream)) {

            tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            while (dtw.hasNext()) {
                File currentFile = dtw.next();

                String relativeFilePath = inputDirectory.toURI().relativize(
                        new File(currentFile.getAbsolutePath()).toURI()).getPath();

                TarArchiveEntry tarEntry = new TarArchiveEntry(currentFile, relativeFilePath);
                tarEntry.setSize(currentFile.length());

                tarArchiveOutputStream.putArchiveEntry(tarEntry);
                if (!currentFile.isDirectory())
                    tarArchiveOutputStream.write(IOUtils.toByteArray(new FileInputStream(currentFile)));
                tarArchiveOutputStream.closeArchiveEntry();
            }
            tarArchiveOutputStream.close();
            return outputFile.toURI();
        }
    }

    URI createTarXz(File inputDirectory, File outputFile) throws IOException {
        if (outputFile.exists()) outputFile.delete();

        DirTreeWalker dtw = new DirTreeWalker(inputDirectory);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             XZCompressorOutputStream xzOutputStream = new XZCompressorOutputStream(bufferedOutputStream,XZ_MAX_COMPRESSION);
             TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(xzOutputStream)) {

            tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            while (dtw.hasNext()) {
                File currentFile = dtw.next();

                String relativeFilePath = inputDirectory.toURI().relativize(
                        new File(currentFile.getAbsolutePath()).toURI()).getPath();

                TarArchiveEntry tarEntry = new TarArchiveEntry(currentFile, relativeFilePath);
                tarEntry.setSize(currentFile.length());

                tarArchiveOutputStream.putArchiveEntry(tarEntry);
                if (!currentFile.isDirectory())
                    tarArchiveOutputStream.write(IOUtils.toByteArray(new FileInputStream(currentFile)));
                tarArchiveOutputStream.closeArchiveEntry();
            }
            tarArchiveOutputStream.close();
            return outputFile.toURI();
        }
    }

    // TODO
    URI createTarXzWithProgress(File inputDirectory, File outputFile) throws IOException {
        if (outputFile.exists()) outputFile.delete();

        DirTreeWalker dtw = new DirTreeWalker(inputDirectory);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             XZCompressorOutputStream xzOutputStream = new XZCompressorOutputStream(bufferedOutputStream);
             TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(xzOutputStream)) {

            tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            while (dtw.hasNext()) {
                File currentFile = dtw.next();

                String relativeFilePath = inputDirectory.toURI().relativize(
                        new File(currentFile.getAbsolutePath()).toURI()).getPath();

                TarArchiveEntry tarEntry = new TarArchiveEntry(currentFile, relativeFilePath);
                tarEntry.setSize(currentFile.length());

                tarArchiveOutputStream.putArchiveEntry(tarEntry);
                if (!currentFile.isDirectory())
                    tarArchiveOutputStream.write(IOUtils.toByteArray(new FileInputStream(currentFile)));
                tarArchiveOutputStream.closeArchiveEntry();
            }
            tarArchiveOutputStream.close();
            return outputFile.toURI();
        }
    }

    void extractTarXz(File i, File outputDirectory) throws IOException {
        if (i==null || !i.exists() || i.isDirectory()) throw new IOException("Missing file or file is a dir");

        String fileName_ = i.toString();
        String tarFileName = fileName_ +".tar";
        FileInputStream instream= new FileInputStream(fileName_);
        XZInputStream ginstream =new XZInputStream(instream);
        FileOutputStream outstream = new FileOutputStream(tarFileName);
        byte[] buf = new byte[8192];
        int len;
        while ((len = ginstream.read(buf)) > 0)
        {
            outstream.write(buf, 0, len);
        }
        ginstream.close();
        outstream.close();
        //There should now be tar files in the directory
        //extract specific files from tar
        TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(tarFileName));
        TarArchiveEntry entry = null;
        int offset;
        FileOutputStream outputFile=null;
        //read every single entry in TAR file
        while ((entry = myTarFile.getNextTarEntry()) != null) {
            //the following two lines remove the .tar.xz extension for the folder name
//            String fileName = i.getName().substring(0, i.getName().lastIndexOf('.'));
//            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            File outputDir =  new File(outputDirectory.getAbsolutePath() + "/" + entry.getName());
            if(! outputDir.getParentFile().exists()){
                outputDir.getParentFile().mkdirs();
            }
            //if the entry in the tar is a directory, it needs to be created, only files can be extracted
            if(entry.isDirectory()){
                outputDir.mkdirs();
            }else{
                byte[] content = new byte[(int) entry.getSize()];
                offset=0;
                myTarFile.read(content, offset, content.length - offset);
                outputFile=new FileOutputStream(outputDir);
                IOUtils.write(content,outputFile);
                outputFile.close();
            }
        }
        //close and delete the tar files, leaving the original .tar.xz and the extracted folders
        myTarFile.close();
        File tarFile =  new File(tarFileName);
        tarFile.delete();
    }
}
