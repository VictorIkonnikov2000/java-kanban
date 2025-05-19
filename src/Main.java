import tasks.*;

import java.sql.SQLOutput;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        //написать тесты
        Task task1 = taskManager.createTask(new Task("Задача ", "Описание задачи ",
                TaskStatus.NEW));


        // Создание Эпика с двумя подзачами:
        Epic epic1 = taskManager.createEpic(new Epic("Эпик ", "Описание эпика "));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Задача 1", "Сделать фз4",
                TaskStatus.NEW, epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Задача 2", "Повторить материал",
                TaskStatus.NEW, epic1.getId()));




        System.out.println(taskManager.getTasks());

        System.out.println(taskManager.getEpics());

        System.out.println(taskManager.getSubtasks());



        System.out.println(taskManager.getTask(1));


        System.out.println(taskManager.getSubtasksByEpicId(2));

    }


}






