/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Homestay;
import entity.HomestayImage;
import java.math.BigDecimal;
import java.util.List;

public interface IHomestayService {

    boolean createHomestay(Homestay homestay, List<String> imageRelativePaths, List<Integer> amenityIds);

    boolean updateHomestay(Homestay homestay, List<Integer> amenityIds);

    Homestay getHomestayById(Integer homestayId);

    List<Homestay> getHomestaysByHost(Integer hostId);

    List<Homestay> searchHomestays(String city, Integer guests, BigDecimal minPrice, BigDecimal maxPrice);

    List<Homestay> getPendingHomestays();

    boolean approveHomestay(Integer homestayId);

    boolean rejectHomestay(Integer homestayId);
}
