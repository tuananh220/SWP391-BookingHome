/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import entity.Banner;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface IBannerService {

    List<Banner> getActiveBanners();

    boolean createBanner(Banner banner);

    boolean updateBanner(Banner banner);
}
