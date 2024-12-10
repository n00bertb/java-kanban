import model.*;
import service.*;
import utils.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("Создаем новую Таску!");

        TaskManager manager = Managers.getDefault();
        Task buyFood = new Task("Купить продукты", "Не забыть яблоки и кортошку");

        manager.createTask(buyFood);
        System.out.println(manager.getTasks() + "\n");

        System.out.println("Обновляем ранее созданную Таску!");
        Task buyFoodToUpdate = manager.getTaskByID(1);
        manager.getTaskByID(11);
        buyFoodToUpdate.setStatus(Status.IN_PROGRESS);
        buyFoodToUpdate.setDescription("Можно и без яблок");
        manager.updateTask(buyFoodToUpdate);
        System.out.println(manager.getTasks() + "\n");

        System.out.println("Создаем новый Эпик");
        Epic compliteSprintFiveFinalTask = new Epic("Сдать задание пятого спринта", "Нужно успеть до конца каникул");
        manager.createEpic(compliteSprintFiveFinalTask);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");
        System.out.println("Тут взяли Эпик по ИД для истории");
        System.out.println(manager.getEpicByID(2));
        System.out.println("Тут начали менять статус и дескрипшн созданного Эпика");
        compliteSprintFiveFinalTask.setDescription("В идеале на этой неделе");//меняем описание Эпика
        compliteSprintFiveFinalTask.setStatus(Status.IN_PROGRESS);//изменяем статус Эпика
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");

        System.out.println("вызвали обновление Эпика");
        manager.updateEpic(compliteSprintFiveFinalTask);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");

        System.out.println("Создаем пару сабтасок к новому Эпику");
        SubTask compliteSprintFiveSubTask1 = new SubTask("Прочитать всю теорию", "Обязательно делая все задания а не бездумно прокликивая", 2);
        SubTask compliteSprintFiveSubTask2 = new SubTask("Посмотреть вебинар от наставника", "Должно значительно ускорить сдачу задания", 2);
        manager.createSubtask(compliteSprintFiveSubTask1, compliteSprintFiveSubTask1.getEpicID());
        manager.createSubtask(compliteSprintFiveSubTask2, compliteSprintFiveSubTask2.getEpicID());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");
        System.out.println("Тут взяли пару Сабтасок по ИД для истории");
        System.out.println(manager.getSubtaskByID(3));
        System.out.println(manager.getSubtaskByID(4));
        manager.getSubtaskByID(44);

        System.out.println("Обновляем статус первой сабтаски Эпика чтобы проверить обновление статуса Эпика(статус Эпика не должен изменится)");
        compliteSprintFiveSubTask1.setStatus(Status.DONE);
        manager.updateSubtask(compliteSprintFiveSubTask1);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");

        System.out.println("Обновляем статус второй сабтаски Эпика чтобы проверить обновление статуса Эпика(статус Эпика должен изменится!!!)");
        compliteSprintFiveSubTask2.setStatus(Status.DONE);
        manager.updateSubtask(compliteSprintFiveSubTask2);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");
        ;

        System.out.println("Добавили новую сабтаску в Эпик (статус должен изменится на NEW)");
        SubTask compliteSprintFiveSubTask3 = new SubTask("Проверяем удаление сабтаски в эпике", "После удаления этой сабтаски статус эпика должен быть таким каким он был до", 2);
        manager.createSubtask(compliteSprintFiveSubTask3, compliteSprintFiveSubTask3.getEpicID());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");
        System.out.println("Взяли новую Сабтаску по ИД для истории");
        System.out.println(manager.getSubtaskByID(5));

        System.out.println("Удалили ранее созданную сабтаску в Эпике(статус Эпика изменился назад на DONE)");
        manager.deleteSubtask(5);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");

        System.out.println("Удаляем ранее созданную Таску и Эпик(для таски впервый раз указываем несуществующий ID)");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        manager.deleteTask(2);
        manager.deleteTask(1);
        manager.deleteEpic(2);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks() + "\n");

        System.out.println("Пытаемся добавить сабтаску в несуществующий эпик");
        SubTask compliteSprintFiveSubTask4 = new SubTask("Проверяем удаление сабтаски в эпике", "После удаления этой сабтаски статус эпика должен быть таким каким он был до", 777);
        manager.createSubtask(compliteSprintFiveSubTask4, compliteSprintFiveSubTask4.getEpicID());

        System.out.println("\nТут вывели историю того что навызывали ранее");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------");
        System.out.println("\nПроверяем что история содержит максимум 10 последних вызванных тасок/сабтасок/эпиков");
        Epic historyCheckEpic = new Epic("Эпик для проверки Иcтории", "будет содержать 1 сабтаску");
        manager.createEpic(historyCheckEpic);
        manager.getEpicByID(6);
        manager.getEpicByID(6);
        manager.getEpicByID(66);
        manager.getEpicByID(6);
        manager.getEpicByID(6);
        manager.getEpicByID(6);
        manager.getEpicByID(6);
        SubTask subTask7 = new SubTask("Сабтаска1", "Это Сабтаска1 эпика проверки истории", 6);
        manager.createSubtask(subTask7, subTask7.getEpicID());
        manager.getSubtaskByID(7);

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("Как видим пропали 2 таски сверху от предидущего вывода");


        //Проверяем очищение истории задач при удалении всех задач из списка задач
        System.out.println("\nПроверяем очищеине истории от тасок после удаоения всех тасок");
        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task2", "Description2");
        Task task3 = new Task("Task3", "Description3");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        System.out.println(manager.getTasks());
        manager.getTaskByID(8);
        manager.getTaskByID(9);
        manager.getTaskByID(10);
        System.out.println("\nСписок истории до удаления всех тасок");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("\nСписок истории после удаления всех тасок");
        manager.clearTaskList();
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }
}

