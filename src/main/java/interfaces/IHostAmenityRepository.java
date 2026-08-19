package interfaces;

import entity.Amenity;
import java.sql.SQLException;
import java.util.List;

public interface IHostAmenityRepository {

    List<Amenity> findAll() throws SQLException;

    Amenity findById(int amenityId) throws SQLException;

    boolean existsName(String amenityName, Integer excludeId)
            throws SQLException;

    int create(Amenity amenity) throws SQLException;

    boolean update(Amenity amenity) throws SQLException;

    boolean isUsed(int amenityId) throws SQLException;

    boolean delete(int amenityId) throws SQLException;
}
