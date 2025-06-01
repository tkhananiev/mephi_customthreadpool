# Custom Thread Pool Executor

## 📌 Описание

Проект реализует пользовательский пул потоков (`CustomExecutor`) с возможностями:

- задание **corePoolSize** и **maxPoolSize**;
- задание время жизни лишних потоков (**keepAliveTime**);
- ограничение на размер очеред задач (**queueSize**);
- настройка минимального числа "живых" потоков (**minSpareThreads**);
- graceful shutdown — корректное завершение задач перед остановкой пула;
- отказ задач при перегрузке (обработчик `RejectedExecutionHandler`).

## 🧩 Структура проекта

- `Main.java` — запуск, создание пула, отправка задач.
- `MyExecutor.java` — реализация собственного thread pool’а.
- `Worker.java` — обработка задач в отдельный потоках.
- `MyThreadFactory.java` — кастомный `ThreadFactory` с читаемыми именаим потоков.
- `CustomExecutor.java` — интерфейс, который определяет поведение кастомного пула.

## 🚀 Как запустить

1. Собрать проект в IDE (например, IntelliJ IDEA)
2. Запустить `Main.java`

Программа создаст пул, отправит 10 задач и завершит работу когда всё закончится.

## 🔧 Пример настройки

```java
MyExecutor executor = new MyExecutor(
    2,                 // corePoolSize
    4,                 // maxPoolSize
    5,                 // keepAliveTime
    TimeUnit.SECONDS,  // timeUnit
    5,                 // queueSize
    1                  // minSpareThreads
);
```

## 📤 Обработка перегрузки

Если очередь задач полная, а все потоки заняты и новых создать нельзя (лимит `maxPoolSize`), задача будет отклонена и в консоли выведится:

```
[Rejected] Task was rejected due to overload: ...
```

## 📎 Пример вывода

```
>> Задача 1 начала выполнение в потоке MyPool-worker-1
<< Задача 1 завершена в потоке MyPool-worker-1
...
Все задачи завершены. Программа завершена.
```
