package br.com.praticasolucoes.gestaofotos;

import java.io.*;
import java.nio.file.FileSystem;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ScritFileProcessar extends FileProcessar{
    private static String pathOrigem  = "/home/eschantal/comparacao-dbic-v2";
    private static String pathDestino  = "/home/eschantal/comparacao-dbic-v2/processado";
    String templateChangeSetFile ="<changeSet id=\"%s\" author=\"eschantal\" context=\"dev\">  \n" +
            "        <comment>\n" +"resolvendo quebra de testes de integração da api sfz-denuncia-espontanea-api\n" +
            "        </comment>\n" +
            "        <sqlFile endDelimiter=\"/\" splitStatements=\"false\" relativeToChangelogFile=\"true\" \n" +
            "                 path=\"%s\"/>\n" +
            "    </changeSet>";
    String templateChangeSetTag ="<changeSet id=\"%s\" author=\"eschantal\" context=\"dev\">  \n" +
            "        <comment>\n" +"resolvendo quebra de testes de integração da api sfz-denuncia-espontanea-api\n" +
            "        </comment>\n" +
            "        <sql>\n" + " %s \n" +
            "        </sql>\n" +
            "    </changeSet>";
    private SimpleDateFormat formatDate;
    private String template;
    public ScritFileProcessar() {
        super(null, null);
        super.forigem = new File(pathOrigem);
         super.fdestino=  new File(pathDestino);

    }

    public ScritFileProcessar(FileSystem fileSystem, File file) {
        super(fileSystem,file);
        super.forigem = new File(pathOrigem);
        super.fdestino= new File(pathDestino);
        formatDate = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    }



    @Override
    public void acaoProcessou() {
        System.out.println(template +"\r");
    }

    @Override
    public void acaoNaoProcessou() {

    }

    @Override
    public void acaoNoFinal() {

    }

    public String decideArquivoOuTag(File file) throws IOException {
        FileReader ler =  new FileReader(file);
        BufferedReader leitor = new BufferedReader(ler);
        String linha;
        String linhaOk;
        while ((linha = leitor.readLine()) != null) {
            linhaOk = this.tiraComentarios(linha);
            if (linhaOk.length() > 0) {
                if (linhaOk.indexOf("CREATE OR REPLACE FORCE VIEW") >=0) {
                    ler.close();
                    leitor.close();
                    return "VIEW";
                } else if (linhaOk.indexOf("CREATE OR REPLACE TYPE") >=0) {
                    ler.close();
                    leitor.close();
                    return "TYPE";
                } else if (linhaOk.indexOf("CREATE OR REPLACE") >=0) {
                    ler.close();
                    leitor.close();
                    return "ARQUIVO";
                } else  {
                     ler.close();
                      leitor.close();
                      return "TAG";
                }
            }
        }
        return null;
    }
    public String tiraComentarios(String linha) {
        if (linha.indexOf("--") >=0)  return  linha.substring(0,linha.indexOf("--"));
        else return linha;
    }
    public void copiaArquivo(File file, File fileDestino) throws IOException {
        FileReader ler = new FileReader(file);
        BufferedReader leitor = new BufferedReader(ler);

        FileWriter fileWriter = new FileWriter(fileDestino, false);
        PrintWriter printWriter = new PrintWriter(fileWriter);


        fileDestino.createNewFile();
        String linha;
        String linhaOk;
        while ((linha = leitor.readLine()) != null) {
            //linhaOk = this.tiraComentarios(linha);
            linhaOk = linha;
            if (linhaOk.length() > 0) printWriter.println(linhaOk);
        }
        printWriter.flush();
        printWriter.close();
        fileWriter.close();
        leitor.close();
        ler.close();
    }
    public void copiaArquivoType(File file, File fileDestino) throws IOException {
        FileReader ler = new FileReader(file);
        BufferedReader leitor = new BufferedReader(ler);

        FileWriter fileWriter = new FileWriter(fileDestino, false);
        PrintWriter printWriter = new PrintWriter(fileWriter);


        fileDestino.createNewFile();
        String linha;
        String linhaOk;
        while ((linha = leitor.readLine()) != null) {
            //linhaOk = this.tiraComentarios(linha);
            linhaOk = linha;
            if (linhaOk.length() > 0) {
                if (linhaOk.indexOf("CREATE OR REPLACE TYPE BODY")>=0) {
                    printWriter.println("/");
                }
                printWriter.println(linhaOk);
            }
        }
        printWriter.flush();
        printWriter.close();
        fileWriter.close();
        leitor.close();
        ler.close();
    }
    public String geraTag(File file) throws IOException {
        FileReader ler = new FileReader(file);
        BufferedReader leitor = new BufferedReader(ler);
        StringBuffer stringBuffer = new StringBuffer();

        String linha;
        String linhaOk;
        while ((linha = leitor.readLine()) != null) {
            linhaOk = this.tiraComentarios(linha);
            if (linhaOk.length() > 0) stringBuffer.append(linhaOk).append("\n");
        }
        leitor.close();
        ler.close();
        return stringBuffer.toString();
    }
    public String geraView(File file) throws IOException {
        FileReader ler = new FileReader(file);
        BufferedReader leitor = new BufferedReader(ler);
        StringBuffer stringBuffer = new StringBuffer().append("<![CDATA[\n");

        String linha;
        String linhaOk;
        while ((linha = leitor.readLine()) != null) {
            linhaOk = this.tiraComentarios(linha);
            //linhaOk = this.tiraComentarios(linha);
            if (linhaOk.length() > 0) stringBuffer.append(linhaOk).append("\n");
        }
        leitor.close();
        ler.close();
        return stringBuffer.append("]]>").toString();
    }
    @Override
    public void processa() throws IOException {
        //1 renomear o arquivo
        Date agora = new Date();
        SimpleDateFormat formatDate2 = new SimpleDateFormat("yyyy-MM-dd-HH.mm-ss-SSS");

        String newName = new StringBuffer()
                .append(formatDate2.format(agora))
                .append("-ADMDEB001.")
                .append(getFile().getName())
                .toString();


        File arquivoDestino = new File(this.getFdestino().getAbsolutePath() + "/" + newName);
        if (!arquivoDestino.exists() &&  !arquivoDestino.isDirectory()) {
          /*  Files.copy(this.getFileSystem().getPath(this.getFile().getAbsolutePath()),
                    getFileSystem().getPath(arquivoDestino.getAbsolutePath()));*/
            if (decideArquivoOuTag(this.getFile()).equals("ARQUIVO")) {
                copiaArquivo(this.getFile(),arquivoDestino);
                template = String.format(templateChangeSetFile, formatDate2.format(agora),
                        newName);
            } else if (decideArquivoOuTag(this.getFile()).equals("TAG")) {
                template = String.format(templateChangeSetTag, formatDate2.format(agora)
                        , geraTag(this.getFile()));
            } else if (decideArquivoOuTag(this.getFile()).equals("TYPE")) {
                copiaArquivoType (this.getFile(),arquivoDestino);
                template = String.format(templateChangeSetFile, formatDate2.format(agora),
                        newName);
            } else {
                template = String.format(templateChangeSetTag, formatDate2.format(agora)
                          ,geraView(this.getFile()));
            }
        } else throw new IOException("arquivo existe");
    }
}
