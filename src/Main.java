import menegers.Managers;
import menegers.TaskManager;
import tasks.*;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefoult();

        //написать тесты
        Task task1 = taskManager.createTask(new Task("Задача ", "Описание задачи ",
                TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Задача ", "Описание задачи ",
                TaskStatus.NEW));
        Task task3 = taskManager.createTask(new Task("Задача ", "Описание задачи ",
                TaskStatus.NEW));


        // Создание Эпика с двумя подзачами:
        Epic epic1 = taskManager.createEpic(new Epic("Эпик ", "Описание эпика "));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Задача 1", "Сделать фз4",
                TaskStatus.NEW, epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Задача 2", "Повторить материал",
                TaskStatus.DONE, epic1.getId()));




        System.out.println(taskManager.getTasks());

        System.out.println(taskManager.getEpics());

        System.out.println(taskManager.getSubtasks());
       //Делаю коммит и пуш через Idea



        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(3));
        System.out.println(taskManager.getEpic(4));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getSubtask(5));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getSubtask(6));
        System.out.println((taskManager.getSubtask(5)));

        System.out.println(taskManager.getSubtasksByEpicId(4));

        System.out.println("Проверяем историю");
        System.out.println(taskManager.getHistory());


    }


}






