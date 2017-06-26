package org.igov.model.arm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


@Repository
public class ArmDaoImpl implements ArmDao {
	
	private static final Logger LOG = LoggerFactory.getLogger(ArmDaoImpl.class);
	
	 @Value("${dbo_tk.getDboTkByOutNumber}")
	 private String getDboTkByOutNumber;
	 
	 @Value("${dbo_tk.createDboTk}")
	 private String createDboTk;
	 
	 @Value("${dbo_tk.updateDboTk}")
	 private String updateDboTk;
	 
	 @Value("${dbo_tk.updateDboTkByExpert}")
	 private String updateDboTkByExpert;
	 
	 @Value("${dbo_tk.selectMaxNumber441}")
	 private String selectMaxNumber441;
	 
	 @Value("${dbo_tk.selectMaxNumber442}")
	 private String selectMaxNumber442;
	 
	 @Value("${arm.driverClassName}")
	 private String driverClassName;
	 
	 @Value("${arm.url}")
	 private String url;
	 
	 @Value("${arm.username}")
	 private String username;
	 
	 @Value("${arm.password}")
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
			    dboTkModel.setNumber_441(rs.getInt("Number_441"));
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
	public DboTkResult createDboTk(DboTkModel dboTkModel) {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		DboTkResult dboTkResult = null;
		
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(createDboTk);

			preparedStatement.setString(1, dboTkModel.getIndustry()==null?null:dboTkModel.getIndustry());
			preparedStatement.setString(2, dboTkModel.getPriznak()==null?null:dboTkModel.getPriznak());
			preparedStatement.setString(3, dboTkModel.getOut_number()==null?"":dboTkModel.getOut_number());
			preparedStatement.setDate(4, dboTkModel.getData_out()==null?null:new java.sql.Date(dboTkModel.getData_out().getTime()));
			preparedStatement.setString(5, dboTkModel.getDep_number()==null?null:dboTkModel.getDep_number());
			preparedStatement.setLong(6, dboTkModel.getNumber_441()==null?1L:dboTkModel.getNumber_441());
			preparedStatement.setDate(7, dboTkModel.getData_in()==null?null:new java.sql.Date(dboTkModel.getData_in().getTime()));
			preparedStatement.setString(8,  dboTkModel.getState()==null?null:dboTkModel.getState());
			preparedStatement.setString(9, dboTkModel.getName_object()==null?null:dboTkModel.getName_object());
			preparedStatement.setString(10, dboTkModel.getKod()==null?null:dboTkModel.getKod());
			preparedStatement.setString(11, dboTkModel.getGruppa()==null?null:dboTkModel.getGruppa());
			preparedStatement.setString(12, dboTkModel.getUndergroup()==null?null:dboTkModel.getUndergroup());
			preparedStatement.setString(13, dboTkModel.getFinans()==null?null:dboTkModel.getFinans());
			preparedStatement.setDate(14,  dboTkModel.getData_out_raz()==null?null:new java.sql.Date(dboTkModel.getData_out_raz().getTime()));
			preparedStatement.setLong(15, dboTkModel.getNumber_442()==null?1L:dboTkModel.getNumber_442());
			preparedStatement.setString(16, dboTkModel.getWinner()==null?null:dboTkModel.getWinner());
			preparedStatement.setString(17,  dboTkModel.getKod_okpo()==null?null:dboTkModel.getKod_okpo());
			preparedStatement.setString(18, dboTkModel.getPhone()==null?null:dboTkModel.getPhone());
			preparedStatement.setString(19, dboTkModel.getSrok()==null?null:dboTkModel.getSrok());
			preparedStatement.setString(20,  dboTkModel.getExpert()==null?null:dboTkModel.getExpert()); 
			preparedStatement.setBigDecimal(21, dboTkModel.getSumma()==null?null:dboTkModel.getSumma());
			preparedStatement.setString(22, dboTkModel.getuAN()==null?null:dboTkModel.getuAN());
			preparedStatement.setString(23, dboTkModel.getIf_oplata()==null?null:dboTkModel.getIf_oplata());
			preparedStatement.setString(24, dboTkModel.getUslovie()==null?null:dboTkModel.getUslovie()); 
			preparedStatement.setString(25, dboTkModel.getBank()==null?null:dboTkModel.getBank());
			preparedStatement.setString(26, dboTkModel.getSmeta()==null?null:dboTkModel.getSmeta());
			preparedStatement.setDate(27, dboTkModel.getDataEZ()==null?null:new java.sql.Date(dboTkModel.getDataEZ().getTime()));
			preparedStatement.setString(28, dboTkModel.getPrilog()==null?null:dboTkModel.getPrilog());
			preparedStatement.setDate(29, dboTkModel.getUpdateData()==null?null:new java.sql.Date(dboTkModel.getUpdateData().getTime()));
			preparedStatement.setLong(30, dboTkModel.getUpdOKBID()==null?null:dboTkModel.getUpdOKBID());
			preparedStatement.setString(31, dboTkModel.getNotes()==null?null:dboTkModel.getNotes());
			preparedStatement.setString(32, dboTkModel.getArhiv()==null?null:dboTkModel.getArhiv());
			preparedStatement.setDate(33, new java.sql.Date(new Date().getTime()));
			preparedStatement.setString(34, dboTkModel.getZametki()==null?null:dboTkModel.getZametki());
			preparedStatement.setLong(35, dboTkModel.getId_corp()==null?1L:dboTkModel.getId_corp());
			preparedStatement.setDate(36, dboTkModel.getDataBB()==null?null:new java.sql.Date(dboTkModel.getDataBB().getTime()));
			preparedStatement.setString(37,  dboTkModel.getPriemka()==null?null:dboTkModel.getPriemka());
			preparedStatement.setString(38, dboTkModel.getProckred()==null?null:dboTkModel.getProckred()); 
			preparedStatement.setBigDecimal(39, dboTkModel.getSumkred()==null?null:dboTkModel.getSumkred());
			preparedStatement.setBigDecimal(40, dboTkModel.getSumzak()==null?null:dboTkModel.getSumzak());
			preparedStatement.setString(41, dboTkModel.getAuctionForm()==null?null:dboTkModel.getAuctionForm());
			preparedStatement.setString(42, dboTkModel.getProtocol_Number()==null?null:dboTkModel.getProtocol_Number());
			preparedStatement.setString(43,	dboTkModel.getCorrectionDoc()==null?null:dboTkModel.getCorrectionDoc());
			preparedStatement.setString(44, dboTkModel.getPrioritet()==null?null:dboTkModel.getPrioritet());
			preparedStatement.setString(45, dboTkModel.getLongterm()==null?null:dboTkModel.getLongterm());

			preparedStatement.executeUpdate();
			dbConnection.commit();
			dbConnection.setAutoCommit(true);
			
			dboTkResult = new DboTkResult();
            dboTkResult.setMess("ok");
            dboTkResult.setCode("0000");
            dboTkResult.setState("r");

		} catch (Exception e) {
			dboTkResult = new DboTkResult();
            dboTkResult.setMess(e.getMessage());
            dboTkResult.setCode("e");
            dboTkResult.setState("e");
            LOG.error("FAIL createDboTk: {}", dboTkResult);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (Exception e) {
				dboTkResult = new DboTkResult();
	            dboTkResult.setMess(e.getMessage());
	            dboTkResult.setCode("e");
	            dboTkResult.setState("e");
	            LOG.error("FAIL createDboTk: {}", dboTkResult);
	            return dboTkResult;
			}

		}
		return dboTkResult;
	}
	

	@Override
	public DboTkResult updateDboTk(DboTkModel dboTkModel) {
		DboTkResult dboTkResult = null;
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(updateDboTk);

			preparedStatement.setString(1, dboTkModel.getIndustry()==null?null:dboTkModel.getIndustry());
			preparedStatement.setString(2, dboTkModel.getPriznak()==null?null:dboTkModel.getPriznak());
			preparedStatement.setDate(3, dboTkModel.getData_out()==null?null:new java.sql.Date(dboTkModel.getData_out().getTime()));
			preparedStatement.setString(4, dboTkModel.getDep_number()==null?null:dboTkModel.getDep_number());
			preparedStatement.setLong(5, dboTkModel.getNumber_441()==null?1L:dboTkModel.getNumber_441());
			preparedStatement.setDate(6, dboTkModel.getData_in()==null?null:new java.sql.Date(dboTkModel.getData_in().getTime()));
			preparedStatement.setString(7,  dboTkModel.getState()==null?null:dboTkModel.getState());
			preparedStatement.setString(8, dboTkModel.getName_object()==null?null:dboTkModel.getName_object());
			preparedStatement.setString(9, dboTkModel.getKod()==null?null:dboTkModel.getKod());
			preparedStatement.setString(10, dboTkModel.getGruppa()==null?null:dboTkModel.getGruppa());
			preparedStatement.setString(11, dboTkModel.getUndergroup()==null?null:dboTkModel.getUndergroup());
			preparedStatement.setString(12, dboTkModel.getFinans()==null?null:dboTkModel.getFinans());
			preparedStatement.setDate(13,  dboTkModel.getData_out_raz()==null?null:new java.sql.Date(dboTkModel.getData_out_raz().getTime()));
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
			preparedStatement.setDate(26, dboTkModel.getDataEZ()==null?null:new java.sql.Date(dboTkModel.getDataEZ().getTime()));
			preparedStatement.setString(27, dboTkModel.getPrilog()==null?null:dboTkModel.getPrilog());
			preparedStatement.setDate(28, dboTkModel.getUpdateData()==null?null:new java.sql.Date(dboTkModel.getUpdateData().getTime()));
			preparedStatement.setLong(29, dboTkModel.getUpdOKBID()==null?null:dboTkModel.getUpdOKBID());
			preparedStatement.setString(30, dboTkModel.getNotes()==null?null:dboTkModel.getNotes());
			preparedStatement.setString(31, dboTkModel.getArhiv()==null?null:dboTkModel.getArhiv());
			preparedStatement.setDate(32, new java.sql.Date(new Date().getTime()));
			preparedStatement.setString(33, dboTkModel.getZametki()==null?null:dboTkModel.getZametki());
			preparedStatement.setLong(34, dboTkModel.getId_corp()==null?null:dboTkModel.getId_corp());
			preparedStatement.setDate(35, dboTkModel.getDataBB()==null?null:new java.sql.Date(dboTkModel.getDataBB().getTime()));
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
			
			dboTkResult = new DboTkResult();
            dboTkResult.setMess("ok");
            dboTkResult.setCode("0000");
            dboTkResult.setState("r");

		} catch (Exception e) {
			dboTkResult = new DboTkResult();
            dboTkResult.setMess(e.getMessage());
            dboTkResult.setCode("e");
            dboTkResult.setState("e");
            LOG.error("FAIL updateDboTk: {}", dboTkResult);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (Exception e) {
				dboTkResult = new DboTkResult();
	            dboTkResult.setMess(e.getMessage());
	            dboTkResult.setCode("e");
	            dboTkResult.setState("e");
	            LOG.error("FAIL updateDboTk: {}", dboTkResult);
	            return dboTkResult;
			}

		}
		
		return dboTkResult;
	}
	
	@Override
	public DboTkResult updateDboTkByExpert(DboTkModel dboTkModel) {
		DboTkResult dboTkResult = null;
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(updateDboTkByExpert);

			preparedStatement.setString(1, dboTkModel.getIndustry()==null?null:dboTkModel.getIndustry());
			preparedStatement.setString(2, dboTkModel.getPriznak()==null?null:dboTkModel.getPriznak());
			preparedStatement.setDate(3, dboTkModel.getData_out()==null?null:new java.sql.Date(dboTkModel.getData_out().getTime()));
			preparedStatement.setString(4, dboTkModel.getDep_number()==null?null:dboTkModel.getDep_number());
			preparedStatement.setLong(5, dboTkModel.getNumber_441()==null?1L:dboTkModel.getNumber_441());
			preparedStatement.setDate(6, dboTkModel.getData_in()==null?null:new java.sql.Date(dboTkModel.getData_in().getTime()));
			preparedStatement.setString(7,  dboTkModel.getState()==null?null:dboTkModel.getState());
			preparedStatement.setString(8, dboTkModel.getName_object()==null?null:dboTkModel.getName_object());
			preparedStatement.setString(9, dboTkModel.getKod()==null?null:dboTkModel.getKod());
			preparedStatement.setString(10, dboTkModel.getGruppa()==null?null:dboTkModel.getGruppa());
			preparedStatement.setString(11, dboTkModel.getUndergroup()==null?null:dboTkModel.getUndergroup());
			preparedStatement.setString(12, dboTkModel.getFinans()==null?null:dboTkModel.getFinans());
			preparedStatement.setDate(13,  dboTkModel.getData_out_raz()==null?null:new java.sql.Date(dboTkModel.getData_out_raz().getTime()));
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
			preparedStatement.setDate(26, dboTkModel.getDataEZ()==null?null:new java.sql.Date(dboTkModel.getDataEZ().getTime()));
			preparedStatement.setString(27, dboTkModel.getPrilog()==null?null:dboTkModel.getPrilog());
			preparedStatement.setDate(28, dboTkModel.getUpdateData()==null?null:new java.sql.Date(dboTkModel.getUpdateData().getTime()));
			preparedStatement.setLong(29, dboTkModel.getUpdOKBID()==null?null:dboTkModel.getUpdOKBID());
			preparedStatement.setString(30, dboTkModel.getNotes()==null?null:dboTkModel.getNotes());
			preparedStatement.setString(31, dboTkModel.getArhiv()==null?null:dboTkModel.getArhiv());
			preparedStatement.setDate(32, new java.sql.Date(new Date().getTime()));
			preparedStatement.setString(33, dboTkModel.getZametki()==null?null:dboTkModel.getZametki());
			preparedStatement.setLong(34, dboTkModel.getId_corp()==null?null:dboTkModel.getId_corp());
			preparedStatement.setDate(35, dboTkModel.getDataBB()==null?null:new java.sql.Date(dboTkModel.getDataBB().getTime()));
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
			preparedStatement.setString(46,  dboTkModel.getExpert()==null?null:dboTkModel.getExpert()); 
			// execute update SQL stetement
			preparedStatement.executeUpdate();
			dbConnection.commit();
			dbConnection.setAutoCommit(true);
			
			dboTkResult = new DboTkResult();
            dboTkResult.setMess("ok");
            dboTkResult.setCode("0000");
            dboTkResult.setState("r");

		} catch (Exception e) {
			dboTkResult = new DboTkResult();
            dboTkResult.setMess(e.getMessage());
            dboTkResult.setCode("e");
            dboTkResult.setState("e");
            LOG.error("FAIL updateDboTk: {}", dboTkResult);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (Exception e) {
				dboTkResult = new DboTkResult();
	            dboTkResult.setMess(e.getMessage());
	            dboTkResult.setCode("e");
	            dboTkResult.setState("e");
	            LOG.error("FAIL updateDboTk: {}", dboTkResult);
	            return dboTkResult;
			}

		}
		
		return dboTkResult;
	}
	
	@Override
	public Integer getMaxValue() {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		Integer dboTkModelMaxNum =null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(selectMaxNumber441);
			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				
				dboTkModelMaxNum = rs.getInt("Number_441");

			}

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				LOG.error("FAIL: {}", e.getMessage());
			}
			
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
		
		return dboTkModelMaxNum;
	}
	
	@Override
	public Integer getMaxValue442() {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		Integer dboTkModelMaxNum442 =null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(selectMaxNumber442);
			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				
				dboTkModelMaxNum442 = rs.getInt("Number_442");

			}

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				LOG.error("FAIL: {}", e.getMessage());
			}
			
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
		
		return dboTkModelMaxNum442;
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
