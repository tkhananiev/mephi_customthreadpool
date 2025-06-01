package customthreadpool;

import java.util.concurrent.*;

public interface CustomExecutor extends Executor {
    void execute(Runnable command);
    <T> Future<T> submit(Callable<T> callable);
    void shutdown();
    void shutdownNow();
    void awaitTermination();
}
