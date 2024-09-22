import java.util.*;

public class Scheduler {
    List<Processo> processos = new ArrayList<Processo>();
    List<Processo> backup = new ArrayList<Processo>();
    int tempo = 0;
    int surtadoLimite;

    public void addProcesso(Processo p) {
        processos.add(p);
    }

    public void backupProcessos() {
        for (Processo p : processos) {
            backup.add(p);
        }
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

        // Se existir um processo RUNNING e ele ainda tiver créditos e o surtoCPU não
        // tiver chegado a zero, mantê-lo
        if (processoRunning != null && processoRunning.getCreditos() > 0 && processoRunning.getSurtoCPU() > 0) {
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

    public void blockChecker() { //checa e atualiza o estado dos processos blocked
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING && p.getSurtoCPU() == 0) {
                p.setEstado(Estado.BLOCKED);
            } else if (p.getEstado() == Estado.BLOCKED && p.getTempoES() == 0) {
                for (Processo restaurador : backupProcessos) {
                    if (restaurador.getNome().equals(p.getNome())) {
                        p.setSurtoCPU(restaurador.getSurtoCPU());
                        p.setEstado(Estado.READY);
                    }
                }
            } else if (p.getEstado() == Estado.BLOCKED) {
                p.setTempoES(p.getTempoES() - 1);
            }
        }
    }

    public void terminator() { //se algum processo tiver completado seu tempo de cpu, é terminado
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING && p.getTempoTotalCPU() == 0) {
                p.setEstado(Estado.EXIT);
            }
        }
    }

    public void ordenador(){
        
    }

    public void escalonar() {
        terminator();
        blockChecker();
        Processo processoEscolhido = escolherProcesso();
        if (processoEscolhido != null) {
            if (processoEscolhido.getEstado() == Estado.READY && processoEscolhido.getSurtoCPU() > 0) { //o que eu quero pegar aqui são os processos com surtoCPU
                processoEscolhido.setEstado(Estado.RUNNING);
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
                processoEscolhido.setSurtoCPU(processoEscolhido.getSurtoCPU() - 1);
            } else if (processoEscolhido.getEstado() == Estado.READY && processoEscolhido.getSurtoCPU() == 0) { // os sem surtoCPU caem aqui (sem alterar surtoCPU)
                processoEscolhido.setEstado(Estado.RUNNING);
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
            }

        }
    }
}

/**
 * acho que entendi #pensamentos
 * 
 * quando um processo passa 1 unidade de tempo na CPU, ele perde 1 crédito. se
 * ele roda, ele perde prioridade.
 * se chega a 0 créditos, ele sai da cpu.
 * 
 * o processo, quando acaba de perder sua prioridade, acaba perdendo sua ordem
 * também,
 * logo ficando em último, o que se pode ver no exemplo no momento 12, quando o
 * processo 1
 * acabou de ser executado antes de restaurarem a prioridade/créditos, e quem
 * foi escalonado foi o processo 3,
 * e o processo 1, que acabou de ser executado, foi para o final da fila, e o
 * processo 2,
 * já que foi o penúltimo a ser executado, foi para o terceiro lugar, atrás do 4
 * e 3 (escolhido)
 **/

/*
 * pensamentos lucao ~tentando entender
 * 
 * Os processos podem estar em 4 estados: Ready, Running, Blocked e Exit
 * Os processos já foram admitidos, o simulador deverá apenas executar
 * 
 * Cada processo tem um número de creditos. INICIALMENTE, o numero de creditos é
 * igual a prioridade
 * O processo que tiver o maior numero de creditos, e esta em estado READY, é
 * selecionado.
 * Ao ser escalonado, o processo utiliza tempo de CPU, sendo esse tempo um
 * surto de CPU anterior à um bloqueio (se o processo tem E/S) ou tempo total de
 * CPU.
 * Em processos com operações de E/S, o surto é descontado do tempo total de
 * CPU.
 * 
 * A cada segundo que passa, o processo em execucao perde um credito.
 * quando seus creditos chegarem a 0, ou houver algum bloqueio (processo de
 * entrada e saida) o escalanador deve selecionar outro processo para exec
 * 
 * Se nenhum processo na fila de prontos possuir creditos, o algoritmo faz uma
 * atribuicao de
 * creditos a todos os processos (incluindo processos blocked), de acordo com
 * cred = cred / 2 + prioridade.
 * 
 * 
 * O escalanador deverá apresentar o escalonamento dos processos, sua utilização
 * de CPU,
 * o tempo de execução, bem como os seus estados em uma linha do tempo.
 * 
 * O parametro ordem é utilizado como criterio de desempate (se mais de um
 * processo possuir
 * a mesma prioridade). Um processo que acabou de perder sua prioridade (se for
 * zero), sera
 * o que possui a menor ordem atual, tendo os outros processos uma atualizao de
 * sua ordem.
 * 
 */