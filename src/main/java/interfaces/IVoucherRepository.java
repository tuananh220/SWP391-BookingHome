/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Voucher;

public interface IVoucherRepository extends BaseRepository<Voucher, Integer> {

    Voucher findByCode(String voucherCode);

    boolean increaseUsedCount(Integer voucherId);
}
