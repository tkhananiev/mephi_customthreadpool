package customthreadpool;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyExecutor executor = new MyExecutor(
                2,
                4,
                5,
                TimeUnit.SECONDS,
                5,
                1
        );

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println(">> Задача " + taskId + " начала выполнение в потоке " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("<< Задача " + taskId + " завершена в потоке " + Thread.currentThread().getName());
            });
        }

        executor.shutdown();
        executor.awaitTermination();

        System.out.println("Все задачи завершены. Программа завершена.");
    }
}
