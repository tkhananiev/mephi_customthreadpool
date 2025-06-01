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
        String name = baseName + "-" + counter.getAndIncrement();
        System.out.println("[ThreadFactory] Creating new thread: " + name);
        return new Thread(r, name);
    }
}
