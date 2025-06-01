package customthreadpool;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyExecutor implements CustomExecutor {
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final int queueSize;

    private final BlockingQueue<Runnable> taskQueue;
    private final Set<Thread> workerThreads = ConcurrentHashMap.newKeySet();
    private final MyThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectionHandler;
    private final AtomicInteger currentPoolSize = new AtomicInteger(0);
    private volatile boolean isShutdown = false;

    public MyExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit, int queueSize) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.queueSize = queueSize;

        this.taskQueue = new LinkedBlockingQueue<>(queueSize);
        this.threadFactory = new MyThreadFactory("MyPool-worker");
        this.rejectionHandler = (r, e) -> System.out.println("[Rejected] Задача отклонена: " + r);

        for (int i = 0; i < corePoolSize; i++) {
            addWorker();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (isShutdown) return;

        if (!taskQueue.offer(command)) {
            if (currentPoolSize.get() < maxPoolSize) {
                addWorker();
                taskQueue.offer(command);
            } else {
                rejectionHandler.rejectedExecution(command, null);
            }
        } else {
            System.out.println("[Executor] Принята задача: " + command);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        FutureTask<T> future = new FutureTask<>(callable);
        execute(future);
        return future;
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }

    @Override
    public void shutdownNow() {
        isShutdown = true;
        for (Thread t : workerThreads) {
            t.interrupt();
        }
    }

    @Override
    public void awaitTermination() {
        while (!taskQueue.isEmpty() || currentPoolSize.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void addWorker() {
        if (currentPoolSize.get() >= maxPoolSize) return;
        currentPoolSize.incrementAndGet();

        Worker worker = new Worker(
                taskQueue,
                keepAliveTime,
                timeUnit,
                corePoolSize,
                currentPoolSize,
                () -> workerThreads.remove(Thread.currentThread()),
                () -> isShutdown
        );

        Thread thread = threadFactory.newThread(worker);
        workerThreads.add(thread);
        thread.start();
    }
}

