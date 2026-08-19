/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Homestay;
import interfaces.IFavoriteRepository;
import repository.FavoriteRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteService {

    private final IFavoriteRepository repository;

    public FavoriteService() {
        repository = new FavoriteRepository();
    }

    public List<Homestay> getFavorites(int customerId) {
        try {
            return repository.findByCustomerId(customerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Homestay>();
        }
    }

    public boolean isFavorite(int customerId, int homestayId) {
        try {
            return repository.exists(customerId, homestayId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean add(int customerId, int homestayId) {
        try {
            if (repository.exists(customerId, homestayId)) {
                return true;
            }
            return repository.add(customerId, homestayId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean remove(int customerId, int homestayId) {
        try {
            return repository.remove(customerId, homestayId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
