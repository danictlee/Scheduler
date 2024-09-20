public class Processo {

    private String nome;
    private int surtoCPU;
    private int tempoES;
    private int tempoTotalCPU;
    private int ordem;
    private int prioridade;
    private int creditos;
    private Estado estado;

    public Processo(String nome, int surtoCPU, int tempoES, int tempoTotalCPU, int ordem, int prioridade, int creditos,
            Estado estado) {
        this.nome = nome;
        this.surtoCPU = surtoCPU;
        this.tempoES = tempoES;
        this.tempoTotalCPU = tempoTotalCPU;
        this.ordem = ordem;
        this.prioridade = prioridade;
        this.creditos = creditos;
        this.estado = Estado.READY;
    }
    

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setSurtoCPU(int surtoCPU) {
        this.surtoCPU = surtoCPU;
    }

    public int getSurtoCPU() {
        return this.surtoCPU;
    }

    public void setTempoES(int tempoES) {
        this.tempoES = tempoES;
    }

    public int getTempoES() {
        return this.tempoES;
    }

    public void setTempoTotalCPU(int tempoTotalCPU) {
        this.tempoTotalCPU = tempoTotalCPU;
    }

    public int getTempoTotalCPU() {
        return this.tempoTotalCPU;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getOrdem() {
        return this.ordem;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getPrioridade() {
        return this.prioridade;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public int getCreditos() {
        return this.creditos;
    }
}
