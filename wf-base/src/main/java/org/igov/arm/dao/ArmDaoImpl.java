package org.igov.arm.dao;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.time.DateFormatUtils;
import org.igov.arm.model.DboTkModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ArmDaoImpl implements ArmDao {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.0";
	
	@Value("#{sqlProperties['dbo_tk.getDboTkByOutNumber']}")
    private String getDboTkByOutNumber;
	
	@Value("#{sqlProperties['dbo_tk.createDboTk']}")
    private String createDboTk;
	
	@Value("#{sqlProperties['dbo_tk.updateDboTk']}")
    private String updateDboTk;
	
	private JdbcTemplate jdbcTemplate;
	 
    public ArmDaoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public List<DboTkModel> getDboTkByOutNumber(String outNumber) {
		List<DboTkModel> listResult = jdbcTemplate.query(
				getDboTkByOutNumber,
				BeanPropertyRowMapper.newInstance(DboTkModel.class), outNumber);
		if(listResult.isEmpty()) {
			return null;
		}
		return listResult;
	}

	@Override
	public void createDboTk(DboTkModel dboTkModel) {
		jdbcTemplate.update(createDboTk,
				new BeanPropertySqlParameterSource(dboTkModel));

	}

	@Override
	public void updateDboTk(DboTkModel dboTkModel) {
		jdbcTemplate.update(updateDboTk,
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
				dboTkModel.getCorrectionDoc(), dboTkModel.getPrioritet(), dboTkModel.getLongterm(),dboTkModel.getOut_number());
				
	}

}
