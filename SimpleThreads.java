public class SimpleThreads {

    // Exibe mensagem com o nome da thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    // Thread original
    private static class MessageLoop implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Inicializando sistema...",
                "Carregando módulos...",
                "Sincronizando dados...",
                "Sistema operacional"
            };

            try {
                for (String info : importantInfo) {
                    Thread.sleep(3000);
                    threadMessage(info);
                }
            } catch (InterruptedException e) {
                threadMessage("MessageLoop interrompida.");
            }
        }
    }

    // Nova thread CPU-intensive
    private static class PrimeCalculator implements Runnable {

        private boolean isPrime(long number) {
            if (number < 2) return false;

            for (long i = 2; i <= Math.sqrt(number); i++) {

                // Verifica interrupção constantemente
                if (Thread.currentThread().isInterrupted()) {
                    threadMessage("Interrupção detectada durante cálculo.");
                    return false;
                }

                if (number % i == 0) {
                    return false;
                }
            }

            return true;
        }

        public void run() {

            long number = 2;

            try {

                while (!Thread.currentThread().isInterrupted()) {

                    if (isPrime(number)) {
                        threadMessage("Primo encontrado: " + number);
                    }

                    number++;
                }

            } catch (Exception e) {
                threadMessage("Erro no cálculo.");
            }

            threadMessage("PrimeCalculator finalizada.");
        }
    }

    public static void main(String args[]) throws InterruptedException {

        // Tempo máximo de espera
        long patience = 10000;

        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Iniciando threads...");

        long startTime = System.currentTimeMillis();

        Thread messageThread = new Thread(new MessageLoop(), "MessageLoop");
        Thread primeThread = new Thread(new PrimeCalculator(), "PrimeCalculator");

        messageThread.start();
        primeThread.start();

        while (messageThread.isAlive() || primeThread.isAlive()) {

            threadMessage("Monitorando execução...");

            messageThread.join(1000);
            primeThread.join(1000);

            if ((System.currentTimeMillis() - startTime > patience)) {

                threadMessage("Tempo limite excedido.");

                if (messageThread.isAlive()) {
                    threadMessage("Interrompendo MessageLoop...");
                    messageThread.interrupt();
                }

                if (primeThread.isAlive()) {
                    threadMessage("Interrompendo PrimeCalculator...");
                    primeThread.interrupt();
                }

                messageThread.join();
                primeThread.join();
            }
        }

        threadMessage("Execução finalizada.");
    }
}