/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingNight {

    private int bookingNightId;
    private int bookingId;
    private int homestayId;
    private LocalDate stayDate;
    private BigDecimal nightPrice;
    private boolean active;

    public BookingNight() {
    }

    public int getBookingNightId() {
        return bookingNightId;
    }

    public void setBookingNightId(int bookingNightId) {
        this.bookingNightId = bookingNightId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public LocalDate getStayDate() {
        return stayDate;
    }

    public void setStayDate(LocalDate stayDate) {
        this.stayDate = stayDate;
    }

    public BigDecimal getNightPrice() {
        return nightPrice;
    }

    public void setNightPrice(BigDecimal nightPrice) {
        this.nightPrice = nightPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
