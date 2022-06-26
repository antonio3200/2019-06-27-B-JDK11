package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getCategorie(){
		String sql="SELECT DISTINCT e.offense_category_id AS cat "
				+ "FROM events e "
				+ "ORDER BY cat";
		List<String> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				String s= rs.getString("cat");
				result.add(s);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
	}
	
	public List<Integer> getMese(){
		String sql="SELECT DISTINCT MONTH(e.reported_date) AS mese "
				+ "FROM events e "
				+ "ORDER BY mese ";
		List<Integer> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st= conn.prepareStatement(sql);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				int anno=rs.getInt("mese");
				result.add(anno);
				
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
	}
	
	public List<String> getVertici(String categoria,int mese){
		String sql="SELECT DISTINCT e.offense_type_id AS tipo "
				+ "FROM events e "
				+ "WHERE e.offense_category_id=? "
				+ "AND MONTH(e.reported_date)=? "
				+ "ORDER BY tipo";
		List<String> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, categoria);
			st.setInt(2, mese);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				String s= rs.getString("tipo");
				result.add(s);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
	}
	
	public List<Arco> getArchi(List<String> vertici,String categoria, int mese){
		String sql="SELECT e1.offense_type_id AS id1, e2.offense_type_id AS id2 , COUNT(DISTINCT e1.neighborhood_id) AS peso "
				+ "FROM events e1, events e2 "
				+ "WHERE e1.offense_type_id<e2.offense_type_id "
				+ "AND e1.neighborhood_id=e2.neighborhood_id "
				+ "AND e1.offense_category_id=? "
				+ "AND e1.offense_category_id=e2.offense_category_id "
				+ "AND MONTH(e1.reported_date)=? "
				+ "AND MONTH(e1.reported_date)=MONTH(e2.reported_date) "
				+ "GROUP BY id1,id2";
		List<Arco> result= new ArrayList<>();
 		Connection conn=DBConnect.getConnection();
		try {
			PreparedStatement st= conn.prepareStatement(sql);
			st.setString(1, categoria);
			st.setInt(2, mese);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				String id1=rs.getString("id1");
				String id2=rs.getString("id2");
				if(vertici.contains(id1) && vertici.contains(id2)) {
					int peso=rs.getInt("peso");
					Arco a = new Arco(id1,id2,peso);
					result.add(a);
				}
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
	}
}
