package org.igov.model.arm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.igov.model.arm.DboTkModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


@Repository
public class ArmDaoImpl implements ArmDao {
	
	private static final Logger LOG = LoggerFactory.getLogger(ArmDaoImpl.class);

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.0";
	
	@Value("#{sqlProperties['dbo_tk.getDboTkByOutNumber']}")
	private String getDboTkByOutNumber;
	
	@Value("#{sqlProperties['dbo_tk.createDboTk']}")
	private String createDboTk;
	
	@Value("#{sqlProperties['dbo_tk.updateDboTk']}")
	private String updateDboTk;
	
	@Value("#{datasourceProps['datasource.driverClassName']}")
    private String driverClassName;
	
	@Value("#{datasourceProps['datasource.url']}")
    private String url;
	
	@Value("#{datasourceProps['datasource.username']}")
    private String username;
	
	@Value("#{datasourceProps['datasource.password']}")
    private String password;
	
	@Override
	public List<DboTkModel> getDboTkByOutNumber(String outNumber) {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		List<DboTkModel> listResult = new ArrayList<>();
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(getDboTkByOutNumber);
			preparedStatement.setString(1, outNumber);

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				DboTkModel dboTkModel = new DboTkModel();
				dboTkModel.setId(rs.getLong("Id"));
				dboTkModel.setIndustry(rs.getString("Industry"));
				dboTkModel.setPriznak(rs.getString("Priznak"));
				dboTkModel.setOut_number(rs.getString("Out_number"));
				dboTkModel.setData_out(rs.getDate("Data_out"));
			    dboTkModel.setDep_number(rs.getString("Dep_number"));
			    dboTkModel.setData_in(rs.getDate("Data_in"));
			    dboTkModel.setState(rs.getString("State"));
			    dboTkModel.setName_object(rs.getString("Name_object"));
			    dboTkModel.setKod(rs.getString("Kod"));
			    dboTkModel.setGruppa(rs.getString("Gruppa"));
			    dboTkModel.setUndergroup(rs.getString("Undergroup"));
			    dboTkModel.setFinans(rs.getString("Finans"));
			    dboTkModel.setData_out_raz(rs.getDate("Data_out_raz"));
			    dboTkModel.setNumber_442(rs.getInt("Number_442"));
			    dboTkModel.setWinner(rs.getString("Winner"));
			    dboTkModel.setKod_okpo(rs.getString("Kod_okpo"));
			    dboTkModel.setPhone(rs.getString("Phone"));
			    dboTkModel.setSrok(rs.getString("Srok"));
			    dboTkModel.setExpert(rs.getString("Expert"));
			    dboTkModel.setSumma(rs.getBigDecimal("Summa"));
			    dboTkModel.setuAN(rs.getString("UAN"));
			    dboTkModel.setIf_oplata(rs.getString("If_oplata"));
			    dboTkModel.setUslovie(rs.getString("Uslovie"));
			    dboTkModel.setBank(rs.getString("Bank"));
			    dboTkModel.setSmeta(rs.getString("Smeta"));
			    dboTkModel.setDataEZ(rs.getDate("DataEZ"));
			    dboTkModel.setPrilog(rs.getString("Prilog"));
			    dboTkModel.setUpdateData(rs.getDate("UpdateData"));
			    dboTkModel.setUpdOKBID(rs.getInt("UpdOKBID"));
			    dboTkModel.setNotes(rs.getString("Notes"));
			    dboTkModel.setArhiv(rs.getString("Arhiv"));
			    dboTkModel.setCreateDate(rs.getDate("CreateDate"));
			    dboTkModel.setZametki(rs.getString("Zametki"));
			    dboTkModel.setId_corp(rs.getInt("Id_corp"));
			    dboTkModel.setDataBB(rs.getDate("DataBB"));
			    dboTkModel.setPriemka(rs.getString("Priemka"));
			    dboTkModel.setProckred(rs.getString("Prockred"));
			    dboTkModel.setSumkred(rs.getBigDecimal("Sumkred"));
			    dboTkModel.setSumzak(rs.getBigDecimal("Sumzak"));
			    dboTkModel.setAuctionForm(rs.getString("AuctionForm"));
			    dboTkModel.setProtocol_Number(rs.getString("Protocol_Number"));
			    dboTkModel.setCorrectionDoc(rs.getString("CorrectionDoc"));
			    dboTkModel.setPrioritet(rs.getString("Prioritet"));
			    dboTkModel.setLongterm(rs.getString("Longterm"));
				listResult.add(dboTkModel);
			}

		} catch (Exception e) {
			LOG.error("FAIL: {}", e.getMessage());
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (Exception e) {
				LOG.error("FAIL: {}", e.getMessage());
			}

		}
		if(listResult.isEmpty()) {
			return null;
		}
		return listResult;
	}

	@Override
	public void createDboTk(DboTkModel dboTkModel) {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(createDboTk);

			preparedStatement.setLong(1, dboTkModel.getId());
			preparedStatement.setString(2, dboTkModel.getIndustry());
			preparedStatement.setString(3, dboTkModel.getPriznak());
			preparedStatement.setString(4, dboTkModel.getOut_number());
			preparedStatement.setString(5, DateFormatUtils.format(dboTkModel.getData_out(), DATE_FORMAT));
			preparedStatement.setString(6, dboTkModel.getDep_number());
			preparedStatement.setLong(7, dboTkModel.getNumber_441());
			preparedStatement.setString(8, DateFormatUtils.format(dboTkModel.getData_in(), DATE_FORMAT));
			preparedStatement.setString(9,  dboTkModel.getState());
			preparedStatement.setString(10, dboTkModel.getName_object());
			preparedStatement.setString(11, dboTkModel.getKod());
			preparedStatement.setString(12, dboTkModel.getGruppa());
			preparedStatement.setString(13, dboTkModel.getUndergroup());
			preparedStatement.setString(14, dboTkModel.getFinans());
			preparedStatement.setString(15,  DateFormatUtils.format(dboTkModel.getData_out_raz(), DATE_FORMAT));
			preparedStatement.setLong(16, dboTkModel.getNumber_442());
			preparedStatement.setString(17, dboTkModel.getWinner());
			preparedStatement.setString(18,  dboTkModel.getKod_okpo());
			preparedStatement.setString(19, dboTkModel.getPhone());
			preparedStatement.setString(20, dboTkModel.getSrok());
			preparedStatement.setString(21,  dboTkModel.getExpert()); 
			preparedStatement.setBigDecimal(22, dboTkModel.getSumma());
			preparedStatement.setString(23, dboTkModel.getuAN());
			preparedStatement.setString(24, dboTkModel.getIf_oplata());
			preparedStatement.setString(25, dboTkModel.getUslovie()); 
			preparedStatement.setString(26, dboTkModel.getBank());
			preparedStatement.setString(27, dboTkModel.getSmeta());
			preparedStatement.setString(28, DateFormatUtils.format(dboTkModel.getDataEZ(), DATE_FORMAT));
			preparedStatement.setString(29, dboTkModel.getPrilog());
			preparedStatement.setString(30, DateFormatUtils.format(dboTkModel.getUpdateData(), DATE_FORMAT));
			preparedStatement.setLong(31, dboTkModel.getUpdOKBID());
			preparedStatement.setString(32, dboTkModel.getNotes());
			preparedStatement.setString(33, dboTkModel.getArhiv());
			preparedStatement.setString(34, DateFormatUtils.format(new Date(), DATE_FORMAT));
			preparedStatement.setString(35, dboTkModel.getZametki());
			preparedStatement.setLong(36, dboTkModel.getId_corp());
			preparedStatement.setString(37, DateFormatUtils.format(dboTkModel.getDataBB(), DATE_FORMAT));
			preparedStatement.setString(38,  dboTkModel.getPriemka());
			preparedStatement.setString(39, dboTkModel.getProckred()); 
			preparedStatement.setBigDecimal(40, dboTkModel.getSumkred());
			preparedStatement.setBigDecimal(41, dboTkModel.getSumzak());
			preparedStatement.setString(42, dboTkModel.getAuctionForm());
			preparedStatement.setString(43, dboTkModel.getProtocol_Number());
			preparedStatement.setString(44,	dboTkModel.getCorrectionDoc());
			preparedStatement.setString(45, dboTkModel.getPrioritet());
			preparedStatement.setString(46, dboTkModel.getLongterm());
			

			preparedStatement.executeUpdate();
			dbConnection.commit();
			dbConnection.setAutoCommit(true);

		} catch (Exception e) {
			LOG.error("FAIL: {}", e.getMessage());
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (Exception e) {
				LOG.error("FAIL: {}", e.getMessage());
			}

		}
	}
	

	@Override
	public void updateDboTk(DboTkModel dboTkModel) {
		
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(updateDboTk);

			preparedStatement.setString(1, dboTkModel.getIndustry()==null?null:dboTkModel.getIndustry());
			preparedStatement.setString(2, dboTkModel.getPriznak()==null?null:dboTkModel.getPriznak());
			preparedStatement.setString(3, DateFormatUtils.format(dboTkModel.getData_out(), DATE_FORMAT));
			preparedStatement.setString(4, dboTkModel.getDep_number()==null?null:dboTkModel.getDep_number());
			preparedStatement.setLong(5, dboTkModel.getNumber_441()==null?1L:dboTkModel.getNumber_441());
			preparedStatement.setString(6, DateFormatUtils.format(dboTkModel.getData_in(), DATE_FORMAT));
			preparedStatement.setString(7,  dboTkModel.getState()==null?null:dboTkModel.getState());
			preparedStatement.setString(8, dboTkModel.getName_object()==null?null:dboTkModel.getName_object());
			preparedStatement.setString(9, dboTkModel.getKod()==null?null:dboTkModel.getKod());
			preparedStatement.setString(10, dboTkModel.getGruppa()==null?null:dboTkModel.getGruppa());
			preparedStatement.setString(11, dboTkModel.getUndergroup()==null?null:dboTkModel.getUndergroup());
			preparedStatement.setString(12, dboTkModel.getFinans()==null?null:dboTkModel.getFinans());
			preparedStatement.setString(13,  DateFormatUtils.format(dboTkModel.getData_out_raz(), DATE_FORMAT));
			preparedStatement.setLong(14, dboTkModel.getNumber_442()==null?1L:dboTkModel.getNumber_442());
			preparedStatement.setString(15, dboTkModel.getWinner()==null?null:dboTkModel.getWinner());
			preparedStatement.setString(16,  dboTkModel.getKod_okpo()==null?null:dboTkModel.getKod_okpo());
			preparedStatement.setString(17, dboTkModel.getPhone()==null?null:dboTkModel.getPhone());
			preparedStatement.setString(18, dboTkModel.getSrok()==null?null:dboTkModel.getSrok());
			preparedStatement.setString(19,  dboTkModel.getExpert()==null?null:dboTkModel.getExpert()); 
			preparedStatement.setBigDecimal(20, dboTkModel.getSumma()==null?null:dboTkModel.getSumma());
			preparedStatement.setString(21, dboTkModel.getuAN()==null?null:dboTkModel.getuAN());
			preparedStatement.setString(22, dboTkModel.getIf_oplata()==null?null:dboTkModel.getIf_oplata());
			preparedStatement.setString(23, dboTkModel.getUslovie()==null?null:dboTkModel.getUslovie()); 
			preparedStatement.setString(24, dboTkModel.getBank()==null?null:dboTkModel.getBank());
			preparedStatement.setString(25, dboTkModel.getSmeta()==null?null:dboTkModel.getSmeta());
			preparedStatement.setString(26, DateFormatUtils.format(dboTkModel.getDataEZ(), DATE_FORMAT));
			preparedStatement.setString(27, dboTkModel.getPrilog()==null?null:dboTkModel.getPrilog());
			preparedStatement.setString(28, DateFormatUtils.format(dboTkModel.getUpdateData(), DATE_FORMAT));
			preparedStatement.setLong(29, dboTkModel.getUpdOKBID()==null?null:dboTkModel.getUpdOKBID());
			preparedStatement.setString(30, dboTkModel.getNotes()==null?null:dboTkModel.getNotes());
			preparedStatement.setString(31, dboTkModel.getArhiv()==null?null:dboTkModel.getArhiv());
			preparedStatement.setString(32, DateFormatUtils.format(new Date(), DATE_FORMAT));
			preparedStatement.setString(33, dboTkModel.getZametki()==null?null:dboTkModel.getZametki());
			preparedStatement.setLong(34, dboTkModel.getId_corp()==null?null:dboTkModel.getId_corp());
			preparedStatement.setString(35, DateFormatUtils.format(dboTkModel.getDataBB(), DATE_FORMAT));
			preparedStatement.setString(36,  dboTkModel.getPriemka()==null?null:dboTkModel.getPriemka());
			preparedStatement.setString(37, dboTkModel.getProckred()==null?null:dboTkModel.getProckred()); 
			preparedStatement.setBigDecimal(38, dboTkModel.getSumkred()==null?null:dboTkModel.getSumkred());
			preparedStatement.setBigDecimal(39, dboTkModel.getSumzak()==null?null:dboTkModel.getSumzak());
			preparedStatement.setString(40, dboTkModel.getAuctionForm()==null?null:dboTkModel.getAuctionForm());
			preparedStatement.setString(41, dboTkModel.getProtocol_Number()==null?null:dboTkModel.getProtocol_Number());
			preparedStatement.setString(42,	dboTkModel.getCorrectionDoc()==null?null:dboTkModel.getCorrectionDoc());
			preparedStatement.setString(43, dboTkModel.getPrioritet()==null?null:dboTkModel.getPrioritet());
			preparedStatement.setString(44, dboTkModel.getLongterm()==null?null:dboTkModel.getLongterm());
			preparedStatement.setString(45, dboTkModel.getOut_number()==null?"":dboTkModel.getOut_number());
			// execute update SQL stetement
			preparedStatement.executeUpdate();
			dbConnection.commit();
			dbConnection.setAutoCommit(true);

		} catch (Exception e) {
			LOG.error("FAIL: {}", e.getMessage());
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (Exception e) {
				LOG.error("FAIL: {}", e.getMessage());
			}

		}
	}
	
	private Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(driverClassName);
			dbConnection = DriverManager.getConnection(
					url, username,password);
			return dbConnection;
		} catch (SQLException|ClassNotFoundException e) {
			LOG.error("FAIL: {}", e.getMessage());
		}

		return dbConnection;

	}

}
