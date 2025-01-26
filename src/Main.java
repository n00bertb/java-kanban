import model.*;
import service.*;
import utils.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("Создаем новую Таску!\n");


        TaskManager manager = Managers.getDefault();

        // Создаём задачи с уникальным временем начала
        Task task1 = new Task(
                "Встретиться с друзьями",
                "Выбрать бар где будем общаться/отдыхать",
                Duration.ofMinutes(120),
                LocalDateTime.now()
        );
        Task task2 = new Task(
                "Сходить на корпоратив",
                "Подобрать одежду под дрескод",
                Duration.ofMinutes(90),
                LocalDateTime.now().plusHours(3) // Не пересекается с task1
        );
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаём эпик с двумя подзадачами
        Epic epic1 = new Epic(
                "Организация переезда",
                "Планирование переезда в новое жилье"
        );
        manager.createEpic(epic1);

        SubTask subtask1 = new SubTask(
                "Поиск квартиры",
                "Ищем в объявлениях хороший вариант квартиры",
                Duration.ofMinutes(60),
                LocalDateTime.now().plusHours(6),
                epic1.getId() // Не пересекается с task2
        );
        SubTask subtask2 = new SubTask(
                "Подготовка к переезду",
                "Упаковка вещей и заказ грузового транспорта",
                Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(7),
                epic1.getId() // Не пересекается с subtask1
        );
        manager.createSubtask(subtask1, 3);
        manager.createSubtask(subtask2, 3);

        // Создаём эпик с одной подзадачей
        Epic epic2 = new Epic(
                "Подготовка к отпуску",
                "Подготовка к отпуску в Японии"
        );
        manager.createEpic(epic2);

        SubTask subtask3 = new SubTask(
                "Поиск авиабилетов",
                "Ищем устраивающий билет на дату отпуска",
                Duration.ofMinutes(180),
                LocalDateTime.now().plusDays(1),
                epic2.getId() // Не пересекается с другими задачами
        );
        manager.createSubtask(subtask3, 6);

        // Выводим списки задач, эпиков и подзадач
        System.out.println("\nВсе задачи:");
        manager.getTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        manager.getEpics().forEach(System.out::println);

        System.out.println("\nВсе подзадачи:");
        manager.getSubtasks().forEach(System.out::println);

        // Изменяем статусы подзадач
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        // Проверяем статус эпика
        System.out.println("\nСтатус эпика после завершения подзадач:");
        System.out.println(manager.getEpicByID(epic1.getId()));

        // Удаляем одну из задач и один из эпиков
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic2.getId());

        // Проверяем после удаления
        System.out.println("\nВсе задачи после удаления:");
        manager.getTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики после удаления:");
        manager.getEpics().forEach(System.out::println);
        // Проверяем список задач в порядке приоритета
        System.out.println("\nЗадачи в порядке приоритета:");
        manager.getPrioritizedTasks().forEach(System.out::println);

        // Вывод истории просмотров
        System.out.println("\nИстория просмотров:");
        manager.getHistory().forEach(System.out::println);

        // Проверяем пересечения задач
        Task overlappingTask = new Task(
                "Перекрывающаяся задача",
                "Описание задачи",
                Duration.ofMinutes(60),
                task2.getStartTime().plusMinutes(100) // Теперь не пересекается
        );
        try {
            manager.createTask(overlappingTask);
            System.out.println("Перекрывающаяся задача успешно добавлена: " + overlappingTask);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}

