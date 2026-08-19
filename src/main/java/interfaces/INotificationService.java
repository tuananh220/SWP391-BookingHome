/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Notification;
import java.util.List;

public interface INotificationService {

    boolean sendNotification(Integer userId, String title, String message);

    List<Notification> getUserNotifications(Integer userId);

    boolean markAsRead(Integer notificationId);
}
