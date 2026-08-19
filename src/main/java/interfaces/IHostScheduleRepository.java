/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import entity.HomestaySchedule;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface IHostScheduleRepository {

    List<HomestaySchedule> findEntries(int homestayId, int hostId,
            LocalDate fromDate, LocalDate toDate)
            throws SQLException;

    boolean save(HomestaySchedule schedule, int hostId)
            throws SQLException;

    boolean clear(int homestayId, int hostId, LocalDate scheduleDate)
            throws SQLException;
}
