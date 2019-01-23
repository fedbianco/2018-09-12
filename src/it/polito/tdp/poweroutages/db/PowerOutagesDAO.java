package it.polito.tdp.poweroutages.db;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.Year;

import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.powerOutages;

public class PowerOutagesDAO {
	
	public List<Nerc> loadAllNercs() {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	public List<Nerc> loadAllNercsFirst() {

		String sql = "Select distinct n.id, n.value " + 
				"From Nerc n , NercRelations nr " + 
				"Where n.id = nr.nerc_one";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	public List<Nerc> loadAllNercsSecond(Nerc nerc) {

		String sql = "Select n.id, n.value " + 
				"From Nerc n , NercRelations nr " + 
				"Where n.id = nr.nerc_two " + 
				"and nr.nerc_one = ?";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nerc.getId());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	public List<powerOutages> getWeight(Nerc nerc1 , Nerc nerc2) {

		String sql = "(Select `nerc_id`,YEAR(date_event_began) as year, MONTH(date_event_began) as month " + 
				"From PowerOutages " + 
				"Where nerc_id = ?) " + 
				"union\n" + 
				"(Select `nerc_id`,YEAR(date_event_began) as year, MONTH(date_event_began) as month " + 
				"From PowerOutages " + 
				"Where nerc_id = ?)";
		List<powerOutages> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nerc1.getId());
			st.setInt(2, nerc2.getId());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				powerOutages n = new powerOutages(res.getInt("nerc_id"), Year.of(res.getInt("year")),Month.of(res.getInt("month")));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	public List<powerOutages> getWeightDifferent(Nerc nerc1 , Nerc nerc2) {

		String sql = "Select distinct po.nerc_id,YEAR(po.date_event_began) as year, MONTH(po.date_event_began) as month " + 
				"From PowerOutages po " + 
				"where po.nerc_id = ? " + 
				"and YEAR(po.date_event_began) in (Select YEAR(po1.date_event_began) " + 
				"								From PowerOutages po1 " + 
				"								Where po1.nerc_id = ? " + 
				"								and MONTH(po.date_event_began) = MONTH(po1.date_event_began))";
		List<powerOutages> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nerc1.getId());
			st.setInt(2, nerc2.getId());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				powerOutages n = new powerOutages(res.getInt("nerc_id"), Year.of(res.getInt("year")),Month.of(res.getInt("month")));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
}
