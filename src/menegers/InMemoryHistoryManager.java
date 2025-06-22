package menegers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
    private Map<Integer,Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();

        Node node = first;
        while (node != null) {
            tasks.add(node.value);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        Node node = nodes.get(id);
        if (node != null) {
            removeNode(node);
            nodes.remove(id);
        }
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return; // Проверка на null
        }
        int taskId = task.getId();
        if (nodes.containsKey(taskId)) {
            remove(taskId);
        }

        linkLast(task);
        nodes.put(taskId, last);

    }

    private void removeNode(Node node){

        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            first = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            last = node.prev;
        }
    }

    private void linkLast(Task task){
        Node newNode = new Node(task,last,null); // Создаем новую таблицу
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
        }
        last = newNode;
    }

    private static class Node{
        Task value;
        Node prev;
        Node next;

        public Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
