package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Setter
@Getter
public class CreateUserRequest {

	@JsonProperty
	private String username;

	@JsonProperty
	private String password;

	@JsonProperty
	private String confirmPassword;
}
