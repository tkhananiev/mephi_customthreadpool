package customthreadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger counter = new AtomicInteger(1);

    public MyThreadFactory(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, baseName + "-" + counter.getAndIncrement());
        System.out.println("[ThreadFactory] Создан поток: " + thread.getName());
        return thread;
    }
}

