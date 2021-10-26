package br.com.praticasolucoes.gestaofotos;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FotoFileProcessar extends FileProcessar {

    String directory;
    private SimpleDateFormat formatDate;
    private static String pathDestino  = "/run/media/eschantal/samsung/banco_imagens";
    private static String pathOrigem  = "/run/media/eschantal/samsung/temp_fotos";

    private String diretorioDestino;

    public FotoFileProcessar() {
        super(null, null);
        super.forigem = new File(pathOrigem);
        super.fdestino =new File(pathDestino);
    }

    public FotoFileProcessar(FileSystem fileSystem, File file) {
        super(fileSystem,file);
        super.forigem = new File(pathOrigem);
        super.fdestino =new File(pathDestino);
        formatDate = new SimpleDateFormat("yyyy-MM-dd");
        directory = formatDate.format(new Date(file.lastModified()));
        diretorioDestino = this.getFdestino().getAbsolutePath() + "/" + directory;
    }


    @Override
    public void acaoProcessou() {
       System.out.println("processou " + this.getFile().getName());
    }

    @Override
    public void acaoNaoProcessou() {
        System.out.println("não processou " + this.getFile().getName());
    }

    @Override
    public void acaoNoFinal() {

    }

    @Override
    public void processa() throws IOException {

        File directory = new File(diretorioDestino);
        if (!directory.exists()) {
            Files.createDirectories(this.getFileSystem().getPath(diretorioDestino));
        }
        File arquivoDestino = new File(diretorioDestino + "/" + this.getFile().getName());
        if (!arquivoDestino.exists()) {
           Files.copy(this.getFileSystem().getPath(this.getFile().getAbsolutePath()),
                   getFileSystem().getPath(diretorioDestino+"/"+this.getFile().getName()));
           Files.delete(getFileSystem().getPath(this.getFile().getAbsolutePath()));
        } else throw new IOException("arquivo existe");
    }
}
