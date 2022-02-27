package br.com.praticasolucoes.gestaofotos;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;

public abstract class FileProcessar {
    private final FileSystem fileSystem;
    protected String pathDestino;
    private final File file;
    protected File fdestino;
    protected File forigem ;


    public FileProcessar(FileSystem fileSystem,  File file) {
        this.fileSystem = fileSystem;
        this.file = file;
    }

    public File[]  getDestinoListFiles(){
        return this.getForigem().listFiles();
    };


    public File getFdestino() {
        return fdestino;
    }


    public File getForigem() {
        return forigem;
    }
    public abstract void acaoProcessou();
    public abstract void acaoNaoProcessou(Exception e);
    public abstract void acaoNoFinal();

    public FileSystem getFileSystem() {
        return fileSystem;
    }



    public File getFile() {
        return file;
    }

    public abstract void processa() throws IOException;



}
