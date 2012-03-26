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

import com.farata.example.dto.AssociateDTO;

public interface AssociateMapper {
	@Select("SELECT * FROM company_associate WHERE company_id = #{companyId}")
	@Results(value = {
		@Result(property="id", column="id"),
		@Result(property="associateName", column="associate")
	})
	List<AssociateDTO> getAssociates(Integer company_id);

	@Insert("INSERT INTO company_associate (associate, company_id) VALUES (#{associateName}, #{companyId})")
	//@SelectKey(statement = "call identity()", keyProperty = "id", before = false, resultType = Long.class)
	@Options(useGeneratedKeys = true, keyProperty="id")
	Integer create(AssociateDTO dto);

	@Update("UPDATE company_associate SET associate = #{associateName}, company_id = #{companyId} WHERE id = #{id}")
	Integer update(AssociateDTO dto);

	@Delete("DELETE FROM company_associate WHERE id = #{id}")
	Integer delete(AssociateDTO dto);
}
