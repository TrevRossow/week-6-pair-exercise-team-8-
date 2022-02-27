package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {

        final String sql =
                "INSERT INTO reservation (site_id, name, from_date, to_date, create_date)" +
                        " VALUES (?, ?, ?, ?,?)" +
                        " RETURNING reservation_id;";
        LocalDate create_date = LocalDate.now();
        int reservation_id = jdbcTemplate.queryForObject(sql, int.class, siteId, name, fromDate, toDate, create_date);
        return reservation_id;

    }

    @Override
    public List<Reservation> getUpcomingReservations(int parkId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT reservation_id, reservation.site_id, reservation.name, from_date, to_date, create_date, park.park_id" +
                " FROM reservation" +
                " JOIN site ON site.site_id = reservation.site_id" +
                " JOIN campground ON campground.campground_id = site.campground_id" +
                " JOIN park ON park.park_id = campground.park_id" +
                " WHERE from_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 30" +
                " AND park.park_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()) {
            reservations.add(mapRowToReservation(results));
        }
        return reservations;
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
