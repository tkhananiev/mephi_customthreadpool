package customthreadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private final BlockingQueue<Runnable> taskQueue;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final int corePoolSize;
    private final AtomicInteger currentPoolSize;
    private final Runnable onTerminate;
    private final boolean shutdownSignal;
    private Thread thread;

    public Worker(BlockingQueue<Runnable> taskQueue,
                  long keepAliveTime,
                  TimeUnit timeUnit,
                  int corePoolSize,
                  AtomicInteger currentPoolSize,
                  Runnable onTerminate,
                  boolean shutdownSignal) {
        this.taskQueue = taskQueue;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.corePoolSize = corePoolSize;
        this.currentPoolSize = currentPoolSize;
        this.onTerminate = onTerminate;
        this.shutdownSignal = shutdownSignal;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        try {
            while (!shutdownSignal || !taskQueue.isEmpty()) {
                Runnable task = taskQueue.poll(keepAliveTime, timeUnit);
                if (task != null) {
                    System.out.println("[Worker] " + Thread.currentThread().getName() + " executes task");
                    task.run();
                } else {
                    if (currentPoolSize.get() > corePoolSize) {
                        System.out.println("[Worker] " + Thread.currentThread().getName() + " idle timeout, stopping.");
                        break;
                    }
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            currentPoolSize.decrementAndGet();
            System.out.println("[Worker] " + Thread.currentThread().getName() + " terminated.");
            onTerminate.run();
        }
    }

    public Thread getThread() {
        return thread;
    }
}
