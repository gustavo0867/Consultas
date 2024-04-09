package entidades;

public class Consulta {
    private String nomePaciente;
    private String nomeMedico;
    private String ano;        
    private String tipoConsulta;
    private String urgencia;
    
    public Consulta() {}

    public String toString() {
    return nomePaciente + " - " + nomeMedico + " - " + ano + " - " + tipoConsulta + " - " + urgencia;
}


    public String getNomeMedico() {
        return nomeMedico;
    }

    public void setNomeMedico(String nomeMedico) {
        this.nomeMedico = nomeMedico;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
 
    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }

    public Consulta(String nomePaciente, String nomeMedico, String ano, String tipoConsulta, String urgencia) {
        this.nomePaciente = nomePaciente;
        this.nomeMedico = nomeMedico;
        this.ano = ano;
        this.tipoConsulta = tipoConsulta;
        this.urgencia = urgencia;
    }
}
