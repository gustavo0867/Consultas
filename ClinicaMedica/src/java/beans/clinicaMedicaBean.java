package beans;

import entidades.Consulta;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.model.SelectItem;
import javax.sql.DataSource;

@Named(value = "clinicaMedicaBean")
@SessionScoped

public class clinicaMedicaBean implements Serializable{
    @Resource(lookup = "java:/ClinicaMedicaDBDS")
    private DataSource consultaDataSource;
    
    private ArrayList<Consulta> consultas;
    private Consulta consulta;
    private String anoSelecionado;
    private String tipoConsultaSelecionado;
    private boolean cadastrar;
    private boolean pesquisar;
    private ArrayList<String> mensagensErro;
    
    @PostConstruct
    public void init(){
        consultas = new ArrayList();
        consulta = new Consulta();
        mensagensErro = new ArrayList();
        cadastrar = false;
        pesquisar = false;
        
    }
        public ArrayList<Consulta> getConsultas() throws SQLException{
            getConsultasDB();
            return consultas;
        }
        
        public String cadastrarConsulta() throws SQLException{
        
           salvaConsultaDB();
        reset();
            System.out.println("Executou cadastar");
        return "index";
        }
        
        public boolean cadastrar() {
        return true;
    }
    
        public void pesquisarConsulta(){
        pesquisar = true;
        cadastrar = false;
    }
    
    public void reset() {
        consulta = new Consulta();
        cadastrar = true;
        pesquisar = false;
    }
    
    public Connection getConnectionDB(){
        Connection conexão = null;
        if(consultaDataSource == null){
            mensagensErro.add("DataSource não acessível");
            return null;
        }
        try{conexão = consultaDataSource.getConnection();}
        catch(SQLException exception) {mensagensErro.add(exception.getMessage());}
        return conexão;
    }
    
    
    
    public void salvaConsultaDB() throws SQLException {
        Connection conexao = getConnectionDB();
        if (conexao == null) {
            return;
        }
        PreparedStatement comando = null;
        try {
            comando = conexao.prepareStatement("INSERT INTO consultas (NomePaciente, NomeMedico, Ano, TipoConsulta, Urgencia) VALUES (?, ?, ?, ?, ?)");
            comando.setString(1, consulta.getNomePaciente());
            comando.setString(2, consulta.getNomeMedico());
            comando.setString(3, consulta.getAno());
            comando.setString(4, consulta.getTipoConsulta());
            comando.setString(5, consulta.getUrgencia());
            comando.executeUpdate();
            comando.close();
        } catch (SQLException exception) {
            if (comando != null) {
                comando.close();
            }
            mensagensErro.add(exception.getMessage());
        }
        conexao.close();
    }
   
    
    public String getConsultasDB() throws SQLException {
        String proxima_pagina = "";
        ArrayList<Consulta> consultas = new ArrayList<>();
        Connection conexao = getConnectionDB();
        if (conexao == null) {
            return "";
        }
        PreparedStatement comando = null;
        ResultSet resultados = null;
        try {
            comando = conexao.prepareStatement("SELECT NomePaciente, NomeMedico, Ano, TipoConsulta,Urgencia   FROM consultas ORDER BY Ano DESC");
            resultados = comando.executeQuery();
            while (resultados.next()) {
                Consulta consulta = new Consulta(
                    resultados.getString("NomePaciente"),
                    resultados.getString("NomeMedico"),
                    resultados.getString("Ano"),
                    resultados.getString("TipoConsulta"),
                    resultados.getString("Urgencia")
                );
                consultas.add(consulta);
            }
            resultados.close();
            comando.close();
            proxima_pagina = "index";
        } catch (SQLException exception) {
            if (resultados != null) resultados.close();
            if (comando != null) comando.close();
            mensagensErro.add(exception.getMessage());
        }
        conexao.close();
        this.consultas = consultas;
        return proxima_pagina;

    }
    
    public ArrayList<String> getTipoConsultaDB() throws SQLException {
        ArrayList<String> tipos = new ArrayList();
        Connection conexão = getConnectionDB();
        if (conexão == null) {
            return tipos;
        }
        PreparedStatement comando = null;
        ResultSet consultas = null;
        try {
            comando = conexão.prepareStatement("SELECT TipoConsulta FROM consultas ORDER BY TipoConsulta");
            consultas = comando.executeQuery();
            while (consultas.next()) {
                String tipo = consultas.getString("TipoConsulta");
                int total_tipos = tipos.size();
                if ((total_tipos == 0) || (!tipo.equals(tipos.get(total_tipos - 1)))) {
                    tipos.add(tipo);
                }
            }
            consultas.close();
            comando.close();
        } catch (SQLException exception) {
            if (consultas != null) {
                consultas.close();
            }
            if (comando != null) {
                comando.close();
            }
            mensagensErro.add(exception.getMessage());
        }
        conexão.close();
        return tipos;
    }
    
    public ArrayList<String> getAnosDB() throws SQLException {
        ArrayList<String> anos = new ArrayList();
        Connection conexao = getConnectionDB();
        if (conexao == null) {
            return anos;
        }
        PreparedStatement comando = null;
        ResultSet consultas = null;
        try {
            comando = conexao.prepareStatement("SELECT Ano FROM consultas ORDER BY Ano");
            consultas = comando.executeQuery();
            while (consultas.next()) {
                String ano = consultas.getString("Ano");
                int total_anos = anos.size();
                if ((total_anos == 0) || (!ano.equals(anos.get(total_anos - 1)))) {
                    anos.add(ano);
                }
            }
            consultas.close();
            comando.close();
        } catch (SQLException exception) {
            if (consultas != null) {
                consultas.close();
            }
            if (comando != null) {
                comando.close();
            }
            mensagensErro.add(exception.getMessage());
        }
        conexao.close();
        return anos;
    }
    
    
    public ArrayList<SelectItem> getAnoItens() throws SQLException {
    ArrayList<SelectItem> itens = new ArrayList();
    for (String ano : getAnosDB()) {
        boolean inserido = false;
        for (int n = 0; n < itens.size(); n++) {
            if (ano.compareTo(itens.get(n).getLabel()) > 0) {
                continue;
            }
            itens.add(n, new SelectItem(ano, ano));
            inserido = true;
            break;
        }
        if (!inserido) {
            itens.add(new SelectItem(ano, ano));
        }
    }
    return itens;
}
    
    public ArrayList<String> getInfoConsultasTipos() throws SQLException {
    getConsultas_TipoAnoDB(); 
    ArrayList<String> infoConsultasTipos = new ArrayList<>();
    for (Consulta consulta : consultas) {
        String tipoConsulta = consulta.getTipoConsulta();
        if (tipoConsulta.equals(tipoConsultaSelecionado)) {
            boolean inserido = false;
            String infoConsulta = consulta.toString();
            for (int i = 0; i < infoConsultasTipos.size(); i++) {
                String consultaExistente = infoConsultasTipos.get(i);
                if (infoConsulta.compareTo(consultaExistente) < 0) {
                    continue;
                }
                infoConsultasTipos.add(i, infoConsulta);
                inserido = true;
                break;
            }
            if (!inserido) {
                infoConsultasTipos.add(infoConsulta);
            }
        }
    }
    return infoConsultasTipos;
}

    
     public void getConsultas_TipoAnoDB() throws SQLException {
    ArrayList<Consulta> consultas = new ArrayList<>();
    Connection conexao = getConnectionDB();
    if (conexao == null) {
        return;
    }
    PreparedStatement comando = null;
    ResultSet consultasResult = null;
    try {
        comando = conexao.prepareStatement("SELECT * FROM consultas WHERE TipoConsulta = ? and Ano = ? ORDER BY TipoConsulta DESC");
        comando.setString(1, tipoConsultaSelecionado);
        comando.setString(2, anoSelecionado);
        consultasResult = comando.executeQuery();
        while (consultasResult.next()) {
            Consulta consulta = new Consulta(
                    consultasResult.getString("NomePaciente"),
                    consultasResult.getString("NomeMedico"),
                    consultasResult.getString("Ano"),
                    consultasResult.getString("TipoConsulta"),
                    consultasResult.getString("Urgencia")
            );
            consultas.add(consulta);
        }
        consultasResult.close();
        comando.close();
        this.consultas = consultas;
    } catch (SQLException exception) {
        if (consultasResult != null) {
            consultasResult.close();
        }
        if (comando != null) {
            comando.close();
        }
        mensagensErro.add(exception.getMessage());
    }
    conexao.close();
    return;
    }
    
    

    public DataSource getconsultaDataSource() {
        return consultaDataSource;
    }

    public void setconsultaDataSource(DataSource consultaDataSource) {
        this.consultaDataSource = consultaDataSource;
    }
    
    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
    
    public String getAnoSelecionado() {
        return anoSelecionado;
    }

    public void setAnoSelecionado(String anoSelecionado) {
        this.anoSelecionado = anoSelecionado;
    }
    
    
    public String getTipoConsultaSelecionado() {
    return tipoConsultaSelecionado;
    }

    public void setTipoConsultaSelecionado(String tipoConsultaSelecionado) {
        this.tipoConsultaSelecionado = tipoConsultaSelecionado;
    }
    
    public boolean isCadastrar() {
        return cadastrar;
    }

    public void setCadastrar(boolean cadastrar) {
        this.cadastrar = cadastrar;
    }

    public boolean isPesquisar() {
        return pesquisar;
    }

    public void setPesquisar(boolean pesquisar) {
        this.pesquisar = pesquisar;
    }

    public ArrayList<String> getMensagensErro() {
        return mensagensErro;
    }

    public void setMensagensErro(ArrayList<String> mensagensErro) {
        this.mensagensErro = mensagensErro;
    }
 
    public void setConsultas(ArrayList<Consulta> consulta){
        this.consultas = consultas;
    }



}
