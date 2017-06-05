package org.igov.model.arm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
				dboTkModel.setData_out(rs.getTimestamp("Data_out"));
			    dboTkModel.setDep_number(rs.getString("Dep_number"));
			    dboTkModel.setData_in(rs.getTimestamp("Data_in"));
			    dboTkModel.setState(rs.getString("State"));
			    dboTkModel.setName_object(rs.getString("Name_object"));
			    dboTkModel.setKod(rs.getString("Kod"));
			    dboTkModel.setGruppa(rs.getString("Gruppa"));
			    dboTkModel.setUndergroup(rs.getString("Undergroup"));
			    dboTkModel.setFinans(rs.getString("Finans"));
			    dboTkModel.setData_out_raz(rs.getTimestamp("Data_out_raz"));
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
			    dboTkModel.setDataEZ(rs.getTimestamp("DataEZ"));
			    dboTkModel.setPrilog(rs.getString("Prilog"));
			    dboTkModel.setUpdateData(rs.getTimestamp("UpdateData"));;
			    dboTkModel.setUpdOKBID(rs.getInt("UpdOKBID"));
			    dboTkModel.setNotes(rs.getString("Notes"));
			    dboTkModel.setArhiv(rs.getString("Arhiv"));
			    dboTkModel.setCreateDate(rs.getTimestamp("CreateDate"));
			    dboTkModel.setZametki(rs.getString("Zametki"));
			    dboTkModel.setId_corp(rs.getInt("Id_corp"));
			    dboTkModel.setDataBB(rs.getTimestamp("DataBB"));
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

		} catch (SQLException e) {
			LOG.error("FAIL: {}", e.getMessage());
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (dbConnection != null) {
					dbConnection.close();
				}
			} catch (SQLException e) {
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
		/*jdbcTemplate.update(createDboTk,
				new BeanPropertySqlParameterSource(dboTkModel));*/

	}

	@Override
	public void updateDboTk(DboTkModel dboTkModel) {
		/*jdbcTemplate.update(updateDboTk,
				dboTkModel.getId(),dboTkModel.getIndustry(),dboTkModel.getPriznak(),dboTkModel.getOut_number(),
				DateFormatUtils.format(dboTkModel.getData_out(), DATE_FORMAT),dboTkModel.getDep_number(),
				dboTkModel.getNumber_441(), DateFormatUtils.format(dboTkModel.getData_in(), DATE_FORMAT), dboTkModel.getState(), dboTkModel.getName_object(),
				dboTkModel.getKod(), dboTkModel.getGruppa(), dboTkModel.getUndergroup(), dboTkModel.getFinans(), DateFormatUtils.format(dboTkModel.getData_out_raz(), DATE_FORMAT), dboTkModel.getNumber_442(),
				dboTkModel.getWinner(), dboTkModel.getKod_okpo(), dboTkModel.getPhone(), dboTkModel.getSrok(), dboTkModel.getExpert(), dboTkModel.getSumma(),
				dboTkModel.getuAN(), dboTkModel.getIf_oplata(), dboTkModel.getUslovie(), dboTkModel.getBank(), dboTkModel.getSmeta(),
				DateFormatUtils.format(dboTkModel.getDataEZ(), DATE_FORMAT), dboTkModel.getPrilog(), DateFormatUtils.format(dboTkModel.getUpdateData(), DATE_FORMAT),
				dboTkModel.getUpdOKBID(), dboTkModel.getNotes(), dboTkModel.getArhiv(), DateFormatUtils.format(new Date(), DATE_FORMAT), 
				dboTkModel.getZametki(), dboTkModel.getId_corp(),DateFormatUtils.format(dboTkModel.getDataBB(), DATE_FORMAT),  dboTkModel.getPriemka(),
				dboTkModel.getProckred(), dboTkModel.getSumkred(), dboTkModel.getSumzak(), dboTkModel.getAuctionForm(), dboTkModel.getProtocol_Number(),
				dboTkModel.getCorrectionDoc(), dboTkModel.getPrioritet(), dboTkModel.getLongterm(),dboTkModel.getOut_number());*/
				
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
