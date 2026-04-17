package com.statewide.login.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuMasterVO {

	private String varMenuName;
	private String varURL;
	private String varMenuId;

	private String varDisplayOrder;
	private String varSeatId;
	private String varHospitalCode;
	private String varUserId;

	private String varModuleName;
	private String varMenuLevel;
	private String varMenuContext;

}
