package br.dreambricks.heinekenquiz;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "players")
public class Player {

    @Id
    String id;
    String nomeBar;
    Date dataCadastro;
    byte[] fileEncrypted; //filename: nomeBar_yyyyMMdd_HHmmss_hash.enc

    String fileName;

    public Player() {
    }

    public Player(String id, String nomeBar, Date dataCadastro, byte[] fileEncrypted, String fileName) {
        this.id = id;
        this.nomeBar = nomeBar;
        this.dataCadastro = dataCadastro;
        this.fileEncrypted = fileEncrypted;
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeBar() {
        return nomeBar;
    }

    public void setNomeBar(String nomeBar) {
        this.nomeBar = nomeBar;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public byte[] getFileEncrypted() {
        return fileEncrypted;
    }

    public void setFileEncrypted(byte[] fileEncrypted) {
        this.fileEncrypted = fileEncrypted;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
