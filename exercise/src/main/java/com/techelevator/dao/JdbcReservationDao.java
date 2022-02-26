package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override // worked on this for a bit tonight... not complete/ not passing test
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {


        final String sql =
                "INSERT INTO reservation (site_id, name, from_date, to_date, create_date)" +
                        " VALUES (?, ?, ?, ?,?)" +
                        " RETURNING reservation_id;";
        LocalDate create_date = LocalDate.now();
        int reservation_id = jdbcTemplate.queryForObject(sql, int.class, siteId, name, fromDate, toDate, create_date);
        return reservation_id;

//        Reservation newReservation = new Reservation();
//        LocalDate create_date=LocalDate.now();
//        final String sql =
//                "INSERT INTO reservation (site_id, name, from_date, to_date, create_date)" +
//                        " VALUES (?, ?, ?, ?, ?)" +
//                        " RETURNING reservation_id;";
//        int newId = jdbcTemplate.queryForObject(sql, int.class,
//                newReservation.getSiteId(), newReservation.getName(), newReservation.getFromDate(), newReservation.getToDate(), newReservation.getCreateDate());
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, siteId, name, fromDate, toDate);
//        if (results.next()) {
//            newReservation = mapRowToReservation(results);
//        }
//        return newReservation.getReservationId();
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
