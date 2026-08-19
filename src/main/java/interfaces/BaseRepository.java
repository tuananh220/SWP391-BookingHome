/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import java.util.List;

public interface BaseRepository<T, ID> {

    boolean insert(T entity);

    boolean update(T entity);

    boolean delete(ID id);

    T findById(ID id);

    List<T> findAll();
}
