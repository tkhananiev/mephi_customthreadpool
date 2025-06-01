package customthreadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Worker implements Runnable {
    private final BlockingQueue<Runnable> taskQueue;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final int corePoolSize;
    private final AtomicInteger currentPoolSize;
    private final Runnable onTerminate;
    private final Supplier<Boolean> isShutdown;

    public Worker(BlockingQueue<Runnable> taskQueue,
                  long keepAliveTime,
                  TimeUnit timeUnit,
                  int corePoolSize,
                  AtomicInteger currentPoolSize,
                  Runnable onTerminate,
                  Supplier<Boolean> isShutdown) {
        this.taskQueue = taskQueue;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.corePoolSize = corePoolSize;
        this.currentPoolSize = currentPoolSize;
        this.onTerminate = onTerminate;
        this.isShutdown = isShutdown;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (isShutdown.get() && taskQueue.isEmpty()) break;

                Runnable task = taskQueue.poll(keepAliveTime, timeUnit);
                if (task != null) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        System.out.println("[Worker] Ошибка выполнения задачи: " + e.getMessage());
                    }
                } else {
                    if (currentPoolSize.get() > corePoolSize) {
                        System.out.println("[Worker] " + Thread.currentThread().getName() + " завершает работу из-за простоя.");
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            currentPoolSize.decrementAndGet();
            onTerminate.run();
            System.out.println("[Worker] " + Thread.currentThread().getName() + " завершён.");
        }
    }
}
