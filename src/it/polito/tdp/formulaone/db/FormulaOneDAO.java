package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driverr;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	private Map<Integer, Driverr> driversMap; 
	
	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}


	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
		System.out.println(dao.getAllDrivers().values());
		
		System.out.println(dao.getDriversCon(constructors.get(0)));
		
		System.out.println(dao.getGareIns(dao.getAllDrivers().get(5), dao.getAllDrivers().get(1)));
	
	}

	public List<Driverr> getDriversCon(Constructor c) {
		
		if(driversMap == null){
			driversMap = this.getAllDrivers();
		}
		
		
		String sql = "SELECT driverId FROM results  WHERE constructorId=? " ;
	
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, c.getConstructorId());
		
			ResultSet rs = st.executeQuery() ;
		
			List<Driverr> list = new ArrayList<Driverr>() ;
			while(rs.next()) {
				Driverr d = driversMap.get(rs.getInt("driverId"));
				if(!list.contains(d))
					list.add(d);
			}
		
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}

	}

	private Map<Integer, Driverr> getAllDrivers() {
		driversMap = new TreeMap <Integer, Driverr> () ;
		
		String sql = "SELECT * FROM drivers";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				LocalDate dob = null;
				if(rs.getDate("dob")!=null)
					dob = rs.getDate("dob").toLocalDate();
					
				Driverr d = new Driverr(rs.getInt("driverID"), rs.getString("driverRef"), rs.getInt("number"),
						rs.getString("code"), rs.getString("forename"), rs.getString("surname"), 
						dob, rs.getString("nationality"), rs.getString("url"));
				driversMap.put(d.getDriverId(), d);
			}

			conn.close();
			return driversMap;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}

	public int getGareInsieme(Driverr d1, Driverr d2) {
		
		String sql = "SELECT COUNT(r1.raceId) as c FROM results AS r1, results AS r2 WHERE r1.driverId=? AND r2.driverId=? AND r1.raceId=r2.raceId " ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, d1.getDriverId());
			st.setInt(2, d2.getDriverId());
		
			ResultSet rs = st.executeQuery() ;
		
			int gare  = 0 ;			
			if(rs.next()) {
			gare = rs.getInt("c");
			}
		
			conn.close();
			return gare ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}

	}

	public List<Integer> getGareIns(Driverr d1, Driverr d2) {
		
		String sql = "SELECT r1.raceId as races FROM results AS r1, results AS r2 WHERE r1.driverId=? AND r2.driverId=? AND r1.raceId=r2.raceId " ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, d1.getDriverId());
			st.setInt(2, d2.getDriverId());
		
			ResultSet rs = st.executeQuery() ;
		
			List<Integer> gare = new ArrayList<Integer> () ;			
			while(rs.next()) {
				gare.add(rs.getInt("races"));
			}
		
			conn.close();
			return gare ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}

	}
	
}
