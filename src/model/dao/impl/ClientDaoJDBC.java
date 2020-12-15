package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.ClientDao;
import model.entities.Client;

public class ClientDaoJDBC implements ClientDao {

	private Connection conn;

	public ClientDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Client obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO client " 
			      + "(Name, Telephone, BirthDate, Cpf) " 
			      + "VALUES " 
				  + "(?, ?, ?, ?)",
				  Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			st.setString(2, obj.getTelephone());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setString(4, obj.getCpf());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Client obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE client " 
			      + "SET Name = ?, Telephone = ?, BirthDate = ?, Cpf = ? " 
			      + "WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setString(2, obj.getTelephone());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setString(4, obj.getCpf());
			st.setInt(5, obj.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM client WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Client findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT * FROM department WHERE Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Client obj = new Client();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setTelephone(rs.getString("Telephone"));
				obj.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
				obj.setCpf(rs.getString("Cpf"));
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Client> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM client ORDER BY Name");
			rs = st.executeQuery();

			List<Client> list = new ArrayList<>();

			while (rs.next()) {
				Client obj = new Client();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setTelephone(rs.getString("Telephone"));
				obj.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime())); 
				obj.setCpf(rs.getString("Cpf"));
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
