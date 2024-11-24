package org.chiches.asycsyyc;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProducerConsumer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Store store = new Store(15);
            ProducerConsumerUI ui = new ProducerConsumerUI(store);
            ui.createAndShowGUI(25, 25); // Specify number of producers and consumers
        });
    }
}

class ProducerConsumerUI {
    private final Store store;
    private JFrame frame;
    private JTextArea logArea;
    private JLabel queueStatusLabel;
    private List<JLabel> producerStatusLabels;
    private List<JLabel> consumerStatusLabels;
    private int numProducers;
    private int numConsumers;

    public ProducerConsumerUI(Store store) {
        this.store = store;
    }

    public void createAndShowGUI(int numProducers, int numConsumers) {
        this.numProducers = numProducers;
        this.numConsumers = numConsumers;
        producerStatusLabels = new ArrayList<>();
        consumerStatusLabels = new ArrayList<>();

        frame = new JFrame("Producer-Consumer Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(numProducers + numConsumers + 1, 1));
        queueStatusLabel = new JLabel("Queue Status: 0 items");
        queueStatusLabel.setOpaque(true);
        queueStatusLabel.setBackground(Color.LIGHT_GRAY);
        statusPanel.add(queueStatusLabel);

        for (int i = 0; i < numProducers; i++) {
            JLabel producerStatusLabel = new JLabel("Producer-" + (i + 1) + " Status: Idle");
            producerStatusLabel.setOpaque(true);
            producerStatusLabel.setBackground(Color.LIGHT_GRAY);
            producerStatusLabels.add(producerStatusLabel);
            statusPanel.add(producerStatusLabel);
        }

        for (int i = 0; i < numConsumers; i++) {
            JLabel consumerStatusLabel = new JLabel("Consumer-" + (i + 1) + " Status: Idle");
            consumerStatusLabel.setOpaque(true);
            consumerStatusLabel.setBackground(Color.LIGHT_GRAY);
            consumerStatusLabels.add(consumerStatusLabel);
            statusPanel.add(consumerStatusLabel);
        }

        frame.add(statusPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(e -> startSimulation());
        buttonPanel.add(startButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void startSimulation() {
        for (int i = 0; i < numProducers; i++) {
            Thread producer = new Thread(new Producer(store, this, "Producer-" + (i + 1)), "Producer-" + (i + 1));
            producer.start();
        }

        for (int i = 0; i < numConsumers; i++) {
            Thread consumer = new Thread(new Consumer(store, this, "Consumer-" + (i + 1)), "Consumer-" + (i + 1));
            consumer.start();
        }
    }

    public void logStatus(String status) {
        SwingUtilities.invokeLater(() -> logArea.append(status + "\n"));
    }

    public void updateQueueStatus(int size) {
        SwingUtilities.invokeLater(() -> {
            queueStatusLabel.setText("Queue Status: " + size + " items");
            if (size == 0) {
                queueStatusLabel.setBackground(Color.RED);
            } else if (size == store.getCapacity()) {
                queueStatusLabel.setBackground(Color.YELLOW);
            } else {
                queueStatusLabel.setBackground(Color.GREEN);
            }
        });
    }

    public void updateProducerStatus(String producerName, String status) {
        SwingUtilities.invokeLater(() -> {
            int index = Integer.parseInt(producerName.split("-")[1]) - 1;
            JLabel label = producerStatusLabels.get(index);
            label.setText(producerName + " Status: " + status);
            switch (status) {
                case "Idle":
                    label.setBackground(Color.LIGHT_GRAY);
                    break;
                case "Waiting (Queue Full)":
                    label.setBackground(Color.YELLOW);
                    break;
                case "Produced Product":
                    label.setBackground(Color.GREEN);
                    break;
                case "Working":
                    label.setBackground(Color.CYAN);
                    break;
            }
        });
    }

    public void updateConsumerStatus(String consumerName, String status) {
        SwingUtilities.invokeLater(() -> {
            int index = Integer.parseInt(consumerName.split("-")[1]) - 1;
            JLabel label = consumerStatusLabels.get(index);
            label.setText(consumerName + " Status: " + status);
            switch (status) {
                case "Idle":
                    label.setBackground(Color.LIGHT_GRAY);
                    break;
                case "Waiting (Queue Empty)":
                    label.setBackground(Color.RED);
                    break;
                case "Consumed Product":
                    label.setBackground(Color.GREEN);
                    break;
                case "Working":
                    label.setBackground(Color.CYAN);
                    break;
            }
        });
    }
}

class Store {
    private final Queue<Integer> queue;
    private final int capacity;
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    private int productCount = 0;

    public Store(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public int getCapacity() {
        return capacity;
    }

    public void produce(ProducerConsumerUI ui) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                ui.logStatus(Thread.currentThread().getName() + " waiting: Queue is full");
                ui.updateProducerStatus(Thread.currentThread().getName(), "Waiting (Queue Full)");
                notFull.await();
            }
            productCount++;
            queue.add(productCount);
            ui.logStatus(Thread.currentThread().getName() + " produced product: " + productCount);
            ui.updateProducerStatus(Thread.currentThread().getName(), "Produced Product");
            ui.updateQueueStatus(queue.size());
            notEmpty.signalAll();
        } finally {
            lock.unlock();
            Thread.sleep(250);
        }
    }

    public void consume(ProducerConsumerUI ui) throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                ui.logStatus(Thread.currentThread().getName() + " waiting: Queue is empty");
                ui.updateConsumerStatus(Thread.currentThread().getName(), "Waiting (Queue Empty)");
                notEmpty.await();
            }
            int product = queue.poll();
            ui.logStatus(Thread.currentThread().getName() + " consumed product: " + product);
            ui.updateConsumerStatus(Thread.currentThread().getName(), "Consumed Product");
            ui.updateQueueStatus(queue.size());
            notFull.signalAll();
        } finally {
            lock.unlock();
            Thread.sleep(250);
        }
    }
}

class Producer implements Runnable {
    private final Store store;
    private final ProducerConsumerUI ui;
    private final String name;

    public Producer(Store store, ProducerConsumerUI ui, String name) {
        this.store = store;
        this.ui = ui;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ui.updateProducerStatus(Thread.currentThread().getName(), "Working");
                Thread.sleep((long) (Math.random() * 1000L));
                store.produce(ui);
                //Thread.sleep((int) (Math.random() * 1000));
                ui.updateProducerStatus(name, "Idle");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final Store store;
    private final ProducerConsumerUI ui;
    private final String name;

    public Consumer(Store store, ProducerConsumerUI ui, String name) {
        this.store = store;
        this.ui = ui;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                store.consume(ui);
                ui.updateConsumerStatus(Thread.currentThread().getName(), "Working");
                Thread.sleep((int) (Math.random() * 1000));
                ui.updateConsumerStatus(name, "Idle");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

