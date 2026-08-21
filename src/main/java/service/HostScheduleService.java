/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.HomestayForm;
import entity.HomestaySchedule;
import interfaces.IHostScheduleRepository;
import repository.HostScheduleRepository;
import ultis.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostScheduleService {

    private final IHostScheduleRepository scheduleRepository;

    public HostScheduleService() {
        scheduleRepository = new HostScheduleRepository();
    }

    public HomestayForm getOwnedHomestay(int homestayId, int hostId) {
        return new HostHomestayService().getForm(homestayId, hostId);
    }

    public List<HomestaySchedule> getNextSixtyDays(
            HomestayForm homestay, int hostId
    ) {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.plusDays(59);
        return getScheduleRange(homestay, hostId, fromDate, toDate);
    }

    public List<HomestaySchedule> getScheduleRange(
            HomestayForm homestay, int hostId,
            LocalDate fromDate, LocalDate toDate
    ) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        }
        if (fromDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Không thể xem lịch bắt đầu trước hôm nay."
            );
        }
        if (fromDate.plusDays(365).isBefore(toDate)) {
            throw new IllegalArgumentException(
                    "Khoảng xem lịch không được vượt quá 365 ngày."
            );
        }
        try {
            List<HomestaySchedule> stored = scheduleRepository.findEntries(
                    homestay.getHomestayId(), hostId, fromDate, toDate
            );
            Map<LocalDate, HomestaySchedule> byDate
                    = new HashMap<LocalDate, HomestaySchedule>();
            for (HomestaySchedule schedule : stored) {
                byDate.put(schedule.getScheduleDate(), schedule);
            }

            List<HomestaySchedule> calendar
                    = new ArrayList<HomestaySchedule>();
            LocalDate date = fromDate;
            while (!date.isAfter(toDate)) {
                HomestaySchedule schedule = byDate.get(date);
                if (schedule == null) {
                    schedule = new HomestaySchedule();
                    schedule.setHomestayId(homestay.getHomestayId());
                    schedule.setScheduleDate(date);
                    schedule.setAvailable(true);
                }
                if (schedule.getEffectivePrice() == null) {
                    schedule.setEffectivePrice(
                            schedule.getCustomPrice() == null
                            ? homestay.getPricePerNight()
                            : schedule.getCustomPrice()
                    );
                }
                if (schedule.isBooked()) {
                    schedule.setAvailable(false);
                    schedule.setLockReason("Booked");
                }
                calendar.add(schedule);
                date = date.plusDays(1);
            }
            return calendar;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<HomestaySchedule>();
        }
    }

    public boolean updateRange(int homestayId, int hostId,
            LocalDate fromDate, LocalDate toDate, boolean available,
            String lockReason) {
        HomestayForm homestay = getOwnedHomestay(homestayId, hostId);
        if (homestay == null) {
            throw new IllegalArgumentException("Không tìm thấy homestay.");
        }
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)
                || fromDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        }
        if (fromDate.plusDays(365).isBefore(toDate)) {
            throw new IllegalArgumentException(
                    "Khoảng cập nhật không được vượt quá 365 ngày."
            );
        }
        if (!available && ValidationUtil.isBlank(lockReason)) {
            lockReason = "Host locked";
        }
        if (lockReason != null && lockReason.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Lý do khóa không được vượt quá 100 ký tự."
            );
        }

        try {
            List<HomestaySchedule> entries = scheduleRepository.findEntries(
                    homestayId, hostId, fromDate, toDate
            );
            for (HomestaySchedule entry : entries) {
                if (entry.isBooked()) {
                    throw new IllegalArgumentException(
                            "Khoảng ngày chứa ngày đã có booking."
                    );
                }
            }
            LocalDate date = fromDate;
            while (!date.isAfter(toDate)) {
                if (!save(homestayId, hostId, date, null, available,
                        available ? null : lockReason)) {
                    return false;
                }
                date = date.plusDays(1);
            }
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean save(int homestayId, int hostId, LocalDate date,
            BigDecimal customPrice, boolean available,
            String lockReason) {
        HomestayForm homestay = getOwnedHomestay(homestayId, hostId);
        if (homestay == null) {
            throw new IllegalArgumentException("Không tìm thấy homestay.");
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày cấu hình không hợp lệ.");
        }
        if (customPrice != null
                && customPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá tùy chỉnh không hợp lệ.");
        }
        if (!available && ValidationUtil.isBlank(lockReason)) {
            lockReason = "Host locked";
        }
        if (lockReason != null && lockReason.length() > 100) {
            throw new IllegalArgumentException(
                    "Lý do khóa không được vượt quá 100 ký tự."
            );
        }

        try {
            List<HomestaySchedule> entries = scheduleRepository.findEntries(
                    homestayId, hostId, date, date
            );
            for (HomestaySchedule entry : entries) {
                if (entry.isBooked()) {
                    throw new IllegalArgumentException(
                            "Không thể thay đổi ngày đã có booking."
                    );
                }
            }

            HomestaySchedule schedule = new HomestaySchedule();
            schedule.setHomestayId(homestayId);
            schedule.setScheduleDate(date);
            schedule.setCustomPrice(customPrice);
            schedule.setAvailable(available);
            schedule.setLockReason(
                    available ? null : lockReason.trim()
            );
            return scheduleRepository.save(schedule, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean clear(int homestayId, int hostId, LocalDate date) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày không hợp lệ.");
        }
        try {
            return scheduleRepository.clear(homestayId, hostId, date);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
