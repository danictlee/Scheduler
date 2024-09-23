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
        Processo processoEscolhido = processos.get(0);
        Processo processoRunning = null;

        // Verificar se existe um processo RUNNING
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING) {
                processoRunning = p;
            }

            // Se existir um processo RUNNING e ele ainda tiver créditos e o surtoCPU não tiver chegado a zero, mantê-lo
            if (processoRunning != null && processoRunning.getCreditos() > 0 && (processoRunning.getSurtoCPU() > 0 || processoRunning.getSurtoCPU() == -1)) {
                processoEscolhido = processoRunning;
            } 

            // Se o processo que está executando não tiver mais créditos ou surtoCPU, achar o outro processo que deveria rodar
            else {
                if (p.getEstado() == Estado.READY) {
                    if (p.getCreditos() > processoEscolhido.getCreditos() ||
                            (p.getCreditos() == processoEscolhido.getCreditos()
                                    && p.getOrdem() < processoEscolhido.getOrdem())) {
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

    public void blockChecker() { // checa e atualiza o estado dos processos blocked
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING && p.getSurtoCPU() == 0) {
                p.setEstado(Estado.BLOCKED);
                System.out.println("Processo " + p.getNome() + " foi bloqueado, iniciando operações E/S.");
            }
            else if (p.getEstado() == Estado.BLOCKED){
                p.setTempoES(p.getTempoES() - 1);

                if (p.getTempoES() == 0){
                    int surtoDefault = p.getSurtoCPUDefault();
                    p.setSurtoCPU(surtoDefault);
                    System.out.println("Processo " + p.getNome() + " atualizou o tempo de Surto de CPU");
                       
                    p.setEstado(Estado.READY);
                    System.out.println("Processo " + p.getNome() + " finalizou as operações de E/S, seu estado foi atualizado para READY.");
                }

                else{
                    System.out.println("Processo " + p.getNome() + " está bloqueado, faltam " + p.getTempoES()
                            + " unidades de tempo para finalizar as operações de E/S.");
                }
            }
        }
    }

    public void terminator() { // se algum processo tiver completado seu tempo de cpu, é terminado
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING && p.getTempoTotalCPU() == 0) {
                p.setEstado(Estado.EXIT);
                System.out.println("Processo " + p.getNome() + " terminou.");
            }
        }
    }

    public void atualizarOrdem() {
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING && p.getCreditos() == 0) {
                for (Processo p2 : processos) {
                    if (p2.getOrdem() < p.getOrdem()) {
                        p2.setOrdem(p2.getOrdem() - 1);
                    }
                }
                p.setOrdem(processos.size());
            }
        }
    }

    public boolean todosFinalizados() {
        for (Processo p : processos) {
            if (p.getEstado() != Estado.EXIT) {
                return false;
            }
        }
        return true;
    }


    public void creditosChecker(Processo p){
        if (p.getEstado() == Estado.RUNNING && p.getCreditos() == 0) {
            System.out.println("Processo " + p.getNome() + " perdeu seus créditos, atualizando o seu estado para READY.");
            p.setEstado(Estado.READY);
            atribuicaoDeCreditos();
        }
    }


    public void escalonar() {

        System.out.println("Processos iniciais: ");

        for (int j = 0; j < processos.size(); j++) {
            System.out.println("Processo " + j + ": " + processos.get(j).getNome() + "; SurtoCPU: " 
                            + processos.get(j).getSurtoCPU() + "; TempoES: " + processos.get(j).getTempoES()
                            + "; TempoTotalCPU: " + processos.get(j).getTempoTotalCPU() + "; Ordem: "
                            + processos.get(j).getOrdem() + "; Prioridade: " + processos.get(j).getPrioridade()
                            + "; Creditos: " + processos.get(j).getCreditos() + "; Estado: "
                            + processos.get(j).getEstado());
        }

        int numMax = Integer.MAX_VALUE;
        for (int i = 1; i < 40; i++) {

            System.out.println("Tempo: " + i);

            terminator();

            blockChecker();

            Processo processoEscolhido = escolherProcesso();
            processoEscolhido.setEstado(Estado.RUNNING);
            System.out.println("Processo " + processoEscolhido.getNome() + " está em execução.");

            // o que eu quero pegar aqui são os processos com surtoCPU
            if (processoEscolhido.getSurtoCPU() > 0) { 
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
                processoEscolhido.setSurtoCPU(processoEscolhido.getSurtoCPU() - 1);
                
                //creditosChecker(processoEscolhido);

            // os sem surtoCPU caem aqui (sem alterar surtoCPU)
            } else if (processoEscolhido.getSurtoCPU() == -1) {  
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
                atualizarOrdem();
                creditosChecker(processoEscolhido);
            }

            
            if (todosFinalizados()){
                System.out.println("Todos os processos finalizados.");
                System.exit(0);
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