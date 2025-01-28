package model;

import utils.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    TaskManager manager;
    Epic epic;
    SubTask subtask1;
    SubTask subtask2;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = new Epic("Epic 1", "Description of Epic 1");
        manager.createEpic(epic);
        subtask1 = new SubTask("Subtask 1", "Description subtask 1", Duration.ofMinutes(60),
                LocalDateTime.now(), epic.getId());
        subtask2 = new SubTask("Subtask 2", "Description subtask 2", Duration.ofMinutes(60),
                LocalDateTime.now().plusHours(6), epic.getId());
        manager.createSubtask(subtask1,epic.getId());
        manager.createSubtask(subtask2,epic.getId());
    }

    @Test
    void shouldAdd2SubtaskToEpic(){
        assertEquals(2, manager.getEpicByID(epic.getId()).getSubtaskList().size());
    }

    @Test
    void shouldReturnTrueAndExceptionOfClassTypesWhenEpicIsSubtask(){
        Task subtaskThatFakedEpic = new SubTask("Faked subtask", "Description of faked subtask", Duration.ofMinutes(60),
                LocalDateTime.now().plusHours(6), epic.getId());
        try {
            manager.createEpic((Epic) subtaskThatFakedEpic);
            fail();
        }catch (ClassCastException exception){
            assertTrue(true);
        }
    }

    @Test
    void shouldReturnTrueByComparingTwoIdenticalSubtasks() {
        SubTask subtaskComp = subtask1;
        assertEquals(subtaskComp, subtask1);
    }
}