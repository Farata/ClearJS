package com.farata.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import com.farata.example.dto.CompanyDTO;

public interface CompanyMapper {
	@Select("SELECT * FROM company")
	@Results(value = {
		@Result(property="id", column="id"),
		@Result(property="companyName", column="company")
	})
	List<CompanyDTO> getCompanies();
	
	@Insert("INSERT INTO company (company) VALUES (#{companyName})")
	@SelectKey(statement = "call identity()", keyProperty = "id", before = false, resultType = int.class)
	Integer create(CompanyDTO dto);

	@Update("UPDATE company SET company = #{companyName} WHERE id = #{id}")
	Integer update(CompanyDTO dto);

	@Delete("DELETE FROM company WHERE id = #{id}")
	Integer delete(CompanyDTO dto);
	
	@Delete("DELETE FROM company")
	Integer deleteAll();
	
}
