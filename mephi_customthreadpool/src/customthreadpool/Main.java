package customthreadpool;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyExecutor executor = new MyExecutor(
                2,                // corePoolSize
                4,                // maxPoolSize
                5,                // keepAliveTime (сек)
                TimeUnit.SECONDS,
                5                 // queueSize
        );

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(">> Задача " + taskId + " начала выполнение в потоке " + threadName);
                try {
                    Thread.sleep(2000); // имитация работы
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("!! Задача " + taskId + " была прервана");
                }
                System.out.println("<< Задача " + taskId + " завершена в потоке " + threadName);
            });
        }

        executor.shutdown();
        executor.awaitTermination();
        executor.shutdownNow();

        System.out.println("Все задачи завершены. Программа завершена.");
    }
}

