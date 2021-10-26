package br.com.praticasolucoes.gestaofotos;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.*;

public class ProcessaFiles {
    public static void main(String[] args) throws Exception {
      //executar(ScritFileProcessar.class);
      executar(ScriptBancoFileProcessar.class);
    }

    public static void executar(Class<?extends FileProcessar> classFileProcessar) throws Exception  {
        File[] pathnames;
        FileSystem fileSystem= FileSystems.getDefault();
        FileProcessar fileProcessar = classFileProcessar.newInstance();
        pathnames = fileProcessar.getForigem().listFiles();

        for (File pathname : pathnames) {
            try { Thread.sleep (100); } catch (InterruptedException ex) {}

            Constructor<? extends FileProcessar>  constructor = classFileProcessar.getConstructor(FileSystem.class,File.class);
            FileProcessar fotoFile = constructor.newInstance(fileSystem,pathname);
            try {
                fotoFile.processa();
                fotoFile.acaoProcessou();
            } catch (IOException e) {
                fotoFile.acaoNaoProcessou();
            }
        }
        fileProcessar.acaoNoFinal();
    }
}
