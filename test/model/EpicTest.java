package model;

import service.*;
import utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    TaskManager manager;
    Epic epic;
    SubTask subtask1;
    SubTask subtask2;

    @BeforeEach
    void setUp(){
        manager = Managers.getDefault();
        epic = new Epic("Epic 1", "Description of Epic 1");
        manager.createEpic(epic);
        subtask1 = new SubTask("Subtask 1", "Description subtask 1", epic.getId());
        subtask2 = new SubTask("Subtask 2", "Description subtask 2", epic.getId());
        manager.createSubtask(subtask1,epic.getId());
        manager.createSubtask(subtask2,epic.getId());
    }

    @Test
    void shouldReturnTrueAndExceptionOfClassTypesWhenSubtaskIsEpic(){

        Task fakeTask = new Epic("Epic 1", "Description of Epic 1");
        try {
            manager.createSubtask((SubTask) fakeTask, ((SubTask) fakeTask).getEpicID());
            fail();
        }catch (ClassCastException exception){
            assertTrue(true);
        }
    }

    @Test
    void shouldReturnTrueByComparingTwoIdenticalEpics(){
        Epic epic1 = epic;
        assertEquals(epic, epic1);
    }

    @Test
    void shouldReturnNewStatusForNewEpic(){
        assertEquals(Status.NEW, manager.getEpicByID(epic.getId()).getStatus());
    }

}