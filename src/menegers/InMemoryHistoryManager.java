package menegers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history = new ArrayList<>();
    private final int MAX_LIMIT = 10;
    Map<Integer,Node> nodes = new HashMap<>();
    Node first;
    Node last;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // альтернативный способ:return List.copyOf(history);
    }

    @Override
    public void remove(int id) {
        // Удаляем задачу из истории по ее ID
        Node node = nodes.get(id); // Получаем узел из map по ID
        if (node != null) {
            removeNode(node); // Вызываем метод для удаления узла из двусвязного списка
            nodes.remove(id); // Удаляем запись из map
        }
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return; // Проверка на null
        }
        int taskId = task.getId();
        if (nodes.containsKey(taskId)) {
            remove(taskId); // Если задача уже есть в истории, удаляем ее
        }

        linkLast(task); // Добавляем задачу в двусвязный список
        nodes.put(taskId, last); // Сохраняем соответствие между ID задачи и узлом

        if (nodes.size() > MAX_LIMIT) {
            removeNode(first); // Если превышен лимит, удаляем самую старую задачу
        }
    }

    private void removeNode(Node node){
        // Удаляет узел из двусвязного списка
        if (node == null) {
            return; // Проверка на null
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            first = node.next; // Если удаляется первый элемент, обновляем ссылку на первый элемент
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            last = node.prev; // Если удаляется последний элемент, обновляем ссылку на последний элемент
        }
    }

    private void linkLast(Task task){
        Node newNode = new Node(task,last,null); // Создаем новый узел
        if (first == null) {
            first = newNode; // Если список пуст, новый узел становится первым
        } else {
            last.next = newNode; // Иначе, добавляем новый узел в конец списка
            newNode.prev = last; // Устанавливаем ссылку на предыдущий узел
        }
        last = newNode; // Обновляем ссылку на последний узел
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
