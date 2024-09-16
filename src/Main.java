import model.*;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        Task buyFood = new Task("Купить продукты", "Не забыть яблоки и кортошку");
        Task buyFoodCreated = taskManager.addTask(buyFood);
        System.out.println(buyFoodCreated);

        Task buyFoodToUpdate = new Task(buyFood.getId(), "Купить продукты", "Можно и без яблок", Status.IN_PROGRESS);
        Task buyFoodUpdated = taskManager.updateTask(buyFoodToUpdate);
        System.out.println(buyFoodUpdated);
        Epic compliteSprintFiveFinalTask = new Epic("Сдать задание пятого спринта", "Нужно успеть до конца каникул");
        taskManager.addEpic(compliteSprintFiveFinalTask);
        System.out.println(compliteSprintFiveFinalTask);
        SubTask compliteSprintFiveSubTask1 = new SubTask("Прочитать всю теорию", "Обязательно делая все задания а не бездумно прокликивая", compliteSprintFiveFinalTask.getId());
        SubTask compliteSprintFiveSubTask2 = new SubTask("Посмотреть вебинар от наставника", "Должно значительно ускорить сдачу задания", compliteSprintFiveFinalTask.getId());
        taskManager.addSubtask(compliteSprintFiveSubTask1);
        taskManager.addSubtask(compliteSprintFiveSubTask2);
        System.out.println(compliteSprintFiveFinalTask);
        compliteSprintFiveSubTask1.setStatus(Status.DONE);
        taskManager.updateSubtask(compliteSprintFiveSubTask1);
        System.out.println(compliteSprintFiveFinalTask);
        taskManager.updateEpic(compliteSprintFiveFinalTask);
        compliteSprintFiveSubTask2.setStatus(Status.DONE);
        taskManager.updateSubtask(compliteSprintFiveSubTask2);
        taskManager.updateEpic(compliteSprintFiveFinalTask);
        System.out.println(compliteSprintFiveFinalTask);
    }
}
