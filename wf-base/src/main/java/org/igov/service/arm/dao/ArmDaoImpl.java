package org.igov.service.arm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.igov.service.arm.model.DboTkModel;
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
