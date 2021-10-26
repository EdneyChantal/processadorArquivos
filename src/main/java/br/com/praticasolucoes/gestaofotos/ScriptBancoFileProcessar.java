package br.com.praticasolucoes.gestaofotos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.HashSet;
import java.util.Set;

public class ScriptBancoFileProcessar extends  FileProcessar{
    private static Set<String> tablespaces;
    private static String pathOrigem  = "/home/eschantal/desenvolvimento/Scripts banco";
    private static String pathDestino  = "/home/eschantal/desenvolvimento/Scripts banco";

    public ScriptBancoFileProcessar() {
        super(null, null);
        super.forigem = new File(pathOrigem);
        super.fdestino=  new File(pathDestino);
        tablespaces = new HashSet<String>();
    }

    public ScriptBancoFileProcessar(FileSystem fileSystem, File file) {
        super(fileSystem,file);
        super.forigem = new File(pathOrigem);
        super.fdestino= new File(pathDestino);
    }

    @Override
    public void acaoProcessou() {

    }

    @Override
    public void acaoNaoProcessou() {

    }

    @Override
    public void acaoNoFinal() {
       tablespaces.stream().forEach(tablespace->{
           System.out.println("CREATE TABLESPACE  "+tablespace + " DATAFILE '/u01/app/oracle/oradata/dbic/" + tablespace
           + ".dbf' SIZE 20M AUTOEXTEND ON;");
          // System.out.println(tablespace);
       });
    }

    @Override
    public void processa() throws IOException {
        FileReader ler = new FileReader(this.getFile());
        BufferedReader leitor = new BufferedReader(ler);
        String linha;
        while ((linha = leitor.readLine()) != null) {
            //linhaOk = this.tiraComentarios(linha);
            int pos = linha.indexOf("CREATE TABLE");
            if (pos != -1) pos = linha.indexOf(" ",pos+1);
            if (pos >=0) {
               int inic = linha.indexOf(" ",pos+1);
               int fim = -1;
               if (fim== -1) fim = linha.indexOf(".",inic+1);
               if (fim== -1) fim = linha.indexOf(")",inic+1);
               if (fim==-1)   fim  = linha.indexOf(" ",inic+1);
               if (fim== -1) fim = linha.length();
               String tablespace = linha.substring(inic+1,fim);
               tablespaces.add(tablespace);
            }
        }
        leitor.close();
        ler.close();
    }
}
