import java.util.*;

public class Scheduler {
    List<Processo> processos = new ArrayList<Processo>();
    int tempo = 0;
    int surtadoLimite;

    public void addProcesso(Processo p) {
        processos.add(p);
    }

       public Processo escolherProcesso() {
        Processo processoEscolhido = null;
        Processo processoRunning = null;
    
        // Verificar se existe um processo RUNNING
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING) {
                processoRunning = p;
                break;
            }
        }
    
        // Se existir um processo RUNNING e ele ainda tiver créditos e o surtadoLimite não tiver chegado a zero, mantê-lo
        if (processoRunning != null && processoRunning.getCreditos() > 0 && processoRunning.getSurtoCPU() > surtadoLimite) {
            processoEscolhido = processoRunning;
        } else {
            // Escolher o processo com maior número de créditos que está na fila de prontos
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
        if (processoEscolhido !=null) {
            if(processoEscolhido.getEstado() == Estado.READY && processoEscolhido.getSurtoCPU()>0) {
                surtadoLimite = processoEscolhido.getSurtoCPU();
                processoEscolhido.setEstado(Estado.RUNNING);
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
                surtadoLimite--;
            }
            else if (processoEscolhido.getEstado()==Estado.RUNNING && processoEscolhido.getSurtoCPU()>0){
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU()-1);
                surtadoLimite--;
            }
            else{

            }

    }
}
}

/**
 * acho que entendi #pensamentos
 * 
 * quando um processo passa 1 unidade de tempo na CPU, ele perde 1 crédito. se ele roda, ele perde prioridade.
 * se chega a 0 créditos, ele sai da cpu.
 * 
 * o processo, quando acaba de perder sua prioridade, acaba perdendo sua ordem também,
 * logo ficando em último, o que se pode ver no exemplo no momento 12, quando o processo 1
 * acabou de ser executado antes de restaurarem a prioridade/créditos, e quem foi escalonado foi o processo 3,
 * e o processo 1, que acabou de ser executado, foi para o final da fila, e o processo 2, 
 *  já que foi o penúltimo a ser executado, foi para o terceiro lugar, atrás do 4 e 3 (escolhido)
 **/