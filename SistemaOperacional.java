public class SistemaOperacional {
    
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();


        Processo p1 = new Processo("A", 2, 5, 6, 1, 3, 3);
        Processo p2 = new Processo("B", 3, 10, 6, 2, 3, 3);
        Processo p3 = new Processo("C", -1, -1, 14, 3, 3, 3);
        Processo p4 = new Processo("D", -1, -1, 10, 4, 3, 3);

        scheduler.addProcesso(p1);
        scheduler.addProcesso(p2);
        scheduler.addProcesso(p3);
        scheduler.addProcesso(p4);

        scheduler.escalonar();

    }
}
