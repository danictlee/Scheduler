import java.util.*;

public class Scheduler {
    private List<Processo> processos;
   
    private Processo processoEscolhido;

    public Scheduler(){
        processos = new ArrayList<Processo>();
    }

    public Processo escolherProcesso() {
        Processo processoEscolhido = null;
        Processo processoRunning = null;
    
        // Verificar se existe um processo RUNNING
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING) {
                processoRunning = p;
            }
        }
    
        // Se existir um processo RUNNING e ele ainda tiver créditos e o surtoCPU não tiver chegado a zero, mantê-lo
        if (processoRunning != null && processoRunning.getCreditos() > 0 && (processoRunning.getSurtoCPU() > 0 || processoRunning.getSurtoCPU() == -1)) {
            processoEscolhido = processoRunning;
        } else {
            // Achar o processo READY com o maior crédito e, em caso de empate, com a menor ordem
            for (Processo p : processos) {
                if (p.getEstado() == Estado.READY) {
                    if (processoEscolhido == null || 
                        p.getCreditos() > processoEscolhido.getCreditos() || 
                        (p.getCreditos() == processoEscolhido.getCreditos() && p.getOrdem() < processoEscolhido.getOrdem())) {
                        processoEscolhido = p;
                    }
                }
            }
        }
    
        return processoEscolhido;
    }

    public void atribuicaoDeCreditos() { //checa pra ver se todos os processos estão sem crédito; se sim, aplica a fórmula créditos = créditos/2*prioridade para atribuir créditos
        boolean todosCreditosZero = true;
        for (Processo p : processos) {
            if ((p.getEstado() == Estado.RUNNING || p.getEstado() == Estado.READY) && p.getCreditos() > 0) {
                todosCreditosZero = false; //existem processos com créditos, logo não é necessário fazer a atribuição
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
            if (p.getEstado() == Estado.RUNNING && p.getSurtoCPU() == 0) { //se um processo está running e acabou de chegar ao fim de seu surtoCPU, conduzir ele para as operações de E/S, além de bloqueá-lo
                p.setEstado(Estado.BLOCKED);
                System.out.println("Processo " + p.getNome() + " foi bloqueado, iniciando operações E/S.");
            }
            else if (p.getEstado() == Estado.BLOCKED){ //se um processo estiver bloqueado, reduz em 1 unidade seu tempo de E/S
                p.setTempoES(p.getTempoES() - 1);

                if (p.getTempoES() == 0){ //ao chegar no fim de suas operações de E/S, os valores surtoCPU e tempoES são restautados á sua forma original, pois precisarão ser usados novamente em outros ciclos, além de mudar seu estado para READY e atualizar sua ordem.
                    int surtoDefault = p.getSurtoCPUDefault();
                    p.setSurtoCPU(surtoDefault);
                    int tempoESDefault = p.getTempoESDefault();
                    p.setEstado(Estado.READY);
                    System.out.println("Processo " + p.getNome() + " finalizou as operações de E/S, seu estado foi atualizado para READY.");
                    atualizarOrdem();
                    p.setTempoES(tempoESDefault);
                }

                else{
                    System.out.println("Processo " + p.getNome() + " está bloqueado, faltam " + p.getTempoES() //acompanhar quanto tempo falta nas operações de E/S
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
                todosFinalizados();
                processoEscolhido = escolherProcesso();
                processoEscolhido.setEstado(Estado.RUNNING);
            }
        }
    }

    public void atualizarOrdem() { // esse método é chamado sempre que for necessário atualizar a ordem dos processos, movendo todos os outros processos -1 posicção para cima e o processo para o final da fila
        for (Processo p : processos) {
            if (p.getEstado() == Estado.RUNNING && p.getCreditos() == 0) {
                for (Processo p2 : processos) {
                    if (p2.getOrdem() > p.getOrdem()) {
                        p2.setOrdem(p2.getOrdem() - 1);
                    }
                }
                p.setOrdem(processos.size());
            }

            else if (p.getEstado() == Estado.READY && p.getTempoES() == 0){
                for (Processo p2 : processos) {
                    if (p2.getOrdem() > p.getOrdem()) {
                        p2.setOrdem(p2.getOrdem() - 1);
                    }
                }
                p.setOrdem(processos.size());
            }
        }
    }

    public void todosFinalizados() { //checa para ver se todos os processos estão finalizados; se sim, retorna um sysout de encerramento e sai do programa
        boolean todosFinalizados = true;
        for (Processo p : processos) {
            if (p.getEstado() != Estado.EXIT) {
                todosFinalizados = false;
            }
        }

        if (todosFinalizados){
            System.out.println("Todos os processos foram finalizados.");
            System.exit(0);
        }
    }


    public void creditosChecker(Processo p){
        if (p.getEstado() == Estado.RUNNING && p.getCreditos() == 0 && p.getSurtoCPU() == -1) { //checa os créditos dos processos que não tem surtoCPU (-1) e se restaura os créditos para esses processos
            System.out.println("Processo " + p.getNome() + " perdeu seus créditos, atualizando o seu estado para READY.");
            p.setEstado(Estado.READY);
            atribuicaoDeCreditos();
        }
        else if (p.getEstado() == Estado.RUNNING && p.getCreditos() == 0 && p.getSurtoCPU() > 0) {
            System.out.println("Processo " + p.getNome() + " perdeu seus créditos, atualizando o seu estado para READY."); //checa agora os processos que tem surtoCPU mas que estão sem créditos, e restaura os créditos 
            p.setEstado(Estado.READY);
            atribuicaoDeCreditos();
        }
    }


    public void escalonar() {

        System.out.println("Processos iniciais: ");

        for (int j = 0; j < processos.size(); j++) { //printa inicialmente todas as informações dos processos
            System.out.println("Processo " + j + ": " + processos.get(j).getNome() + "; SurtoCPU: "  
                            + processos.get(j).getSurtoCPU() + "; TempoES: " + processos.get(j).getTempoES()
                            + "; TempoTotalCPU: " + processos.get(j).getTempoTotalCPU() + "; Ordem: "
                            + processos.get(j).getOrdem() + "; Prioridade: " + processos.get(j).getPrioridade()
                            + "; Creditos: " + processos.get(j).getCreditos() + "; Estado: "
                            + processos.get(j).getEstado());
        }

        int numMax = Integer.MAX_VALUE;

        processoEscolhido = escolherProcesso(); //escolhe o processo para ser escalonado
        processoEscolhido.setEstado(Estado.RUNNING);

        for (int i = 1; i < 40; i++) {

            System.out.println("Tempo: " + i); //medida de tempo 

            atribuicaoDeCreditos(); //checa créditos dos processos

            terminator(); //checa tempoCPU dos processos

            blockChecker(); //checa surtoCPU e tempoES dos processos

            if (processoEscolhido.getEstado() == Estado.RUNNING) {
                System.out.println("Processo " + processoEscolhido.getNome() + " está em execução.");
            }

            // processos com surtoCPU deverão cair aqui para serem "executados"/ diminuir atributos qwue indicam a passagem de um ciclo
            if (processoEscolhido.getSurtoCPU() > 0) { 
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
                processoEscolhido.setSurtoCPU(processoEscolhido.getSurtoCPU() - 1);
               
                atualizarOrdem();
                creditosChecker(processoEscolhido);
                
            // processos se, surtoCPU deverão cair aqui para serem "executados"/ diminuir atributos que indicam a passagem de um ciclo
            } else if (processoEscolhido.getSurtoCPU() == -1) {  
                processoEscolhido.setCreditos(processoEscolhido.getCreditos() - 1);
                processoEscolhido.setTempoTotalCPU(processoEscolhido.getTempoTotalCPU() - 1);
                
                atualizarOrdem(); //após "executar", atualizar a ordem
                creditosChecker(processoEscolhido); //checar para ver se o processo escolhido pode realmente ser escolhido
            
            }

            
            todosFinalizados(); // ver se todos os procesos estão finalizados
              

            atribuicaoDeCreditos(); //ver se precisam repor créditos
            processoEscolhido = escolherProcesso(); //escolhido o próximo processo, só rodar
            processoEscolhido.setEstado(Estado.RUNNING);

        }

    }

}
