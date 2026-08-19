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
import entity.Voucher;

import java.sql.SQLException;
import java.util.List;

public interface IHostVoucherRepository {

    List<Voucher> findByHostId(int hostId) throws SQLException;

    Voucher findByIdAndHostId(int voucherId, int hostId)
            throws SQLException;

    List<Homestay> findHostHomestays(int hostId) throws SQLException;

    boolean existsCode(String voucherCode, Integer excludedVoucherId)
            throws SQLException;

    int create(Voucher voucher) throws SQLException;

    boolean update(Voucher voucher) throws SQLException;

    boolean deactivate(int voucherId, int hostId) throws SQLException;

    boolean activate(int voucherId, int hostId) throws SQLException;
}
