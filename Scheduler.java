import java.util.*;

public class Scheduler {
    List<Processo> processos = new ArrayList<Processo>();
    int tempo = 0;

    public void addProcesso(Processo p) {
        processos.add(p);
    }

    public Processo escolherProcesso() {
        Processo processoEscolhido = null;
        for (Processo p : processos) {
            if (p.getEstado() == Estado.READY) {
                if (processoEscolhido == null ||
                        p.getCreditos() > processoEscolhido.getCreditos() ||
                        (p.getCreditos() == processoEscolhido.getCreditos()
                                && p.getOrdem() > processoEscolhido.getOrdem())) {
                    processoEscolhido = p;
                }
            }
        }
        return processoEscolhido;
    }

    public void atribuicaoDeCreditos() {
        boolean todosCreditosZero = true;
        for (Processo p : processos) {
            if (p.getCreditos() > 0) {
                todosCreditosZero = false;
                break;
            }
        }
        if (todosCreditosZero) {
            for (Processo p : processos) {
                int novosCreditos = p.getCreditos() / 2 + p.getPrioridade();
                p.setCreditos(novosCreditos);
            }
        }
    }

    public void escalonar() {
    Processo processoEscolhido = escolherProcesso();
    
    }
}